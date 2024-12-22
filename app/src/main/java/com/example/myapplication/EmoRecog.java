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

import org.tensorflow.lite.*;

/*
import org.openvino.java.OpenVINO;
import org.openvino.java.core.CompiledModel;
import org.openvino.java.core.Core;
import org.openvino.java.core.InferRequest;
import org.openvino.java.core.Model;
import org.openvino.java.core.Tensor;
*/
public class EmoRecog implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "EmoRecogHelper";
    private CameraBridgeViewBase cameraView;
    private Net faceNet;
    //private InferRequest inferRequest;
    private static final String[] LABELS = {"neutral", "happy", "sad", "surprise", "anger"};
    private static final String FACE_DETECTION_MODEL = "assets/opencv_face_detector_uint8.pb";
    private static final String FACE_DETECTION_CONFIG = "assets/opencv_face_detector.pbtxt";
    //private static final String EMOTION_MODEL = "model/emorecog/emotions-recognition-retail-0003.xml";
    //private static final String EMOTION_WEIGHTS = "model/emorecog/emotions-recognition-retail-0003.bin";

    public void initialize(Context context, ViewGroup parent, int cameraViewId) {
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "OpenCV initialization failed");
            return;
        }
        //System.loadLibrary("openvino_c");
        /*OpenVINO vino = OpenVINO.load();
        vino.loadCvDll();
        Core core = new Core();
        Model model = core.readModel(EMOTION_MODEL, EMOTION_WEIGHTS);
        CompiledModel compiledModel = core.compileModel(model, "AUTO");
        inferRequest = compiledModel.createInferRequest();
*/
        faceNet = Dnn.readNetFromTensorflow(FACE_DETECTION_MODEL, FACE_DETECTION_CONFIG);
        if (faceNet.empty()) {
            Log.e(TAG, "Failed to load face detection model!");
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
    }

    @Override
    public void onCameraViewStopped() {
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

                float[] transposedData = new float[faceData.length];
                int channels = 3;
                int height = 64;
                int width = 64;
                for (int c = 0; c < channels; c++) {
                    for (int h = 0; h < height; h++) {
                        for (int w = 0; w < width; w++) {
                            transposedData[c * height * width + h * width + w] = faceData[h * width * channels + w * channels + c];
                        }
                    }
                }
/*
                Tensor inputTensor = inferRequest.getInputTensor();
                inputTensor.setData(transposedData);
                inferRequest.infer();

                Tensor outputTensor = inferRequest.getOutputTensor();
                float[] probs = outputTensor.getData(float[].class, (int) outputTensor.getSize());
                int maxIndex = argMax(probs);
                String label = LABELS[maxIndex];
                Imgproc.putText(frame, label, new Point(xLeftBottom, yLeftBottom - 10), Imgproc.FONT_HERSHEY_SIMPLEX, 1.0, new Scalar(255, 0, 0), 2);
                Imgproc.rectangle(frame, new Point(xLeftBottom, yLeftBottom), new Point(xRightTop, yRightTop), new Scalar(0, 0, 255), 2);*/
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
}