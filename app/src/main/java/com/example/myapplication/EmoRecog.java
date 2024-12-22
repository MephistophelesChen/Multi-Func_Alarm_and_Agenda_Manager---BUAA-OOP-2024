package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class EmoRecog implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "EmoRecogHelper";
    private CameraBridgeViewBase cameraView;
    private Net faceNet;
    private Interpreter tflite;
    private TensorImage inputImageBuffer;
    private TensorBuffer outputProbabilityBuffer;
    private ImageProcessor imageProcessor;
    private static final String[] LABELS = {"anger", "disgust", "fear", "happy", "sad", "surprised", "normal"};
    private static final String FACE_DETECTION_MODEL = "opencv_face_detector_uint8.pb";
    private static final String FACE_DETECTION_CONFIG = "opencv_face_detector.pbtxt";
    private static final String EMOTION_MODEL = "mobilenet_v2.tflite";

    public void initialize(Context context, ViewGroup parent, int cameraViewId) {
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "OpenCV初始化失败");
            return;
        }

        try {
            // 复制模型文件到缓存目录
            copyFileFromAssets(context, FACE_DETECTION_MODEL);
            copyFileFromAssets(context, FACE_DETECTION_CONFIG);
            copyFileFromAssets(context, EMOTION_MODEL);

            // 使用缓存目录中的文件路径
            File modelFile = new File(context.getCacheDir(), FACE_DETECTION_MODEL);
            File configFile = new File(context.getCacheDir(), FACE_DETECTION_CONFIG);
            faceNet = Dnn.readNetFromTensorflow(modelFile.getAbsolutePath(), configFile.getAbsolutePath());
            if (faceNet.empty()) {
                Log.e(TAG, "加载人脸检测模型失败！");
                return;
            }

            File emotionModelFile = new File(context.getCacheDir(), EMOTION_MODEL);
            FileInputStream fis = new FileInputStream(emotionModelFile);
            tflite = new Interpreter(fis.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, emotionModelFile.length()));
            inputImageBuffer = new TensorImage(DataType.FLOAT32);
            outputProbabilityBuffer = TensorBuffer.createFixedSize(new int[]{1, LABELS.length}, DataType.FLOAT32);
            imageProcessor = new ImageProcessor.Builder()
                    .add(new ResizeOp(64, 64, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                    .add(new NormalizeOp(0, 255))
                    .build();
        } catch (Exception e) {
            Log.e(TAG, "加载模型失败！", e);
            return;
        }

        View placeholder = parent.findViewById(R.id.camera_view);
        int index = parent.indexOfChild(placeholder);
        parent.removeView(placeholder);

        cameraView = new JavaCameraView(context, -1);
        cameraView.setId(R.id.camera_view);
        cameraView.setVisibility(SurfaceView.VISIBLE);
        parent.addView(cameraView, index);

        cameraView.setCvCameraViewListener(this);
        cameraView.enableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        cameraView.enableView();
    }

    @Override
    public void onCameraViewStopped() {
        cameraView.disableView();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat frame = inputFrame.rgba();
        Mat blob = Dnn.blobFromImage(frame, 1.0, new Size(300, 300), new Scalar(104.0, 177.0, 123.0), false, false);
        faceNet.setInput(blob);
        Mat detections = faceNet.forward("detection_out");
        Mat detectionMat = new Mat(detections.size(2), detections.size(3), CvType.CV_32F);
        detections.reshape(1, (int) detections.total() / detections.size(3)).copyTo(detectionMat);

        for (int i = 0; i < detectionMat.rows(); i++) {
            float confidence = (float) detectionMat.get(i, 2)[0];
            if (confidence > 0.5) {
                int xLeftBottom = (int) (detectionMat.get(i, 3)[0] * frame.cols());
                int yLeftBottom = (int) (detectionMat.get(i, 4)[0] * frame.rows());
                int xRightTop = (int) (detectionMat.get(i, 5)[0] * frame.cols());
                int yRightTop = (int) (detectionMat.get(i, 6)[0] * frame.rows());

                xLeftBottom = Math.max(0, xLeftBottom);
                yLeftBottom = Math.max(0, yLeftBottom);
                xRightTop = Math.min(frame.cols(), xRightTop);
                yRightTop = Math.min(frame.rows(), yRightTop);

                Rect roi = new Rect(xLeftBottom, yLeftBottom, xRightTop - xLeftBottom, yRightTop - yLeftBottom);
                Mat face = new Mat(frame, roi);

                Imgproc.resize(face, face, new Size(64, 64));
                face.convertTo(face, CvType.CV_32F);

                float[] faceData = new float[(int) (face.total() * face.channels())];
                face.get(0, 0, faceData);

                ByteBuffer inputBuffer = ByteBuffer.allocateDirect(4 * 64 * 64 * 3);
                inputBuffer.order(ByteOrder.nativeOrder());
                inputBuffer.rewind();
                for (float value : faceData) {
                    inputBuffer.putFloat(value);
                }

                tflite.run(inputBuffer, outputProbabilityBuffer.getBuffer().rewind());

                float[] probs = outputProbabilityBuffer.getFloatArray();
                int maxIndex = argMax(probs);
                String label = LABELS[maxIndex];
                Imgproc.putText(frame, label, new Point(xLeftBottom, yLeftBottom - 10), Imgproc.FONT_HERSHEY_SIMPLEX, 1.0, new Scalar(255, 0, 0), 2);
                Imgproc.rectangle(frame, new Point(xLeftBottom, yLeftBottom), new Point(xRightTop, yRightTop), new Scalar(0, 0, 255), 2);
            }
        }
        return frame;
    }

    private static int argMax(float[] array) {
        int maxIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }
    private void copyFileFromAssets(Context context, String fileName) {
        File destinationFile = new File(context.getCacheDir(), fileName);
        if (destinationFile.exists()) {
            return; // 文件已存在，无需复制
        }

        try (InputStream is = context.getAssets().open(fileName);
             FileOutputStream fos = new FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            Log.i(TAG, "文件已复制到缓存目录: " + destinationFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "从assets复制文件到缓存目录失败", e);
        }
    }
}