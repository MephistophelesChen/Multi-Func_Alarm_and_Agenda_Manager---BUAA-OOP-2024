package com.example.myapplication;

import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.openvino.java.OpenVINO;
import org.openvino.java.core.Core;
import org.openvino.java.core.*;
import org.openvino.java.core.Model;

public class EmoRecog {
    private static final String[] LABELS = {"neutral", "happy", "sad", "surprise", "anger"};
    private static final String FACE_DETECTION_MODEL = "model/emorecog/opencv_face_detector_uint8.pb";
    private static final String FACE_DETECTION_CONFIG = "model/emorecog/opencv_face_detector.pbtxt";
    private static final String EMOTION_MODEL = "model/emorecog/emotions-recognition-retail-0003.xml";
    private static final String EMOTION_WEIGHTS = "model/emorecog/emotions-recognition-retail-0003.bin";
    public static void main(String[] args) {
        // 初始化 OpenVINO Core
        OpenVINO vino = OpenVINO.load();
        vino.loadCvDll();
        Core core = new Core();
        Model model = core.readModel(EMOTION_MODEL,EMOTION_WEIGHTS);
        CompiledModel compiledModel = core.compileModel(model, "AUTO");
        InferRequest inferRequest = compiledModel.createInferRequest();

        // 初始化 OpenCV 人脸检测模型
        Net faceNet = Dnn.readNetFromTensorflow(FACE_DETECTION_MODEL, FACE_DETECTION_CONFIG);
        if (faceNet.empty()) {
            System.out.println("Failed to load face detection model!");
            return;
        }

        // 打开摄像头
        VideoCapture capture = new VideoCapture(0);
        if (!capture.isOpened()) {
            System.out.println("Cannot open the camera!");
            return;
        }

        Mat frame = new Mat();
        while (capture.read(frame)) {
            // 人脸检测
            Mat blob = Dnn.blobFromImage(frame, 1.0, new Size(300, 300), new Scalar(104.0, 177.0, 123.0), false, false);
            faceNet.setInput(blob);
            Mat detections = faceNet.forward("detection_out");
            Mat detectionMat = new Mat(detections.size(2), detections.size(3), CvType.CV_32F);
            detections.reshape(1, (int) detections.total() / detections.size(3)).copyTo(detectionMat);

            for (int i = 0; i < detectionMat.rows(); i++) {
                // Confidence is between 0 and 1
                float confidence = (float) detectionMat.get(i, 2)[0];
                if (confidence > 0.5) { // Replace 0.5 with your confidence threshold
                    int xLeftBottom = (int) (detectionMat.get(i, 3)[0] * frame.cols());
                    int yLeftBottom = (int) (detectionMat.get(i, 4)[0] * frame.rows());
                    int xRightTop = (int) (detectionMat.get(i, 5)[0] * frame.cols());
                    int yRightTop = (int) (detectionMat.get(i, 6)[0] * frame.rows());

                    //Rect object = new Rect(xLeftBottom, yLeftBottom, xRightTop - xLeftBottom, yRightTop - yLeftBottom);
                    //Imgproc.rectangle(frame, object, new Scalar(0, 255, 0));
                    // 确保坐标在图像范围内
                    xLeftBottom = Math.max(0, xLeftBottom);
                    yLeftBottom = Math.max(0, yLeftBottom);
                    xRightTop = Math.min(frame.cols(), xRightTop);
                    yRightTop = Math.min(frame.rows(), yRightTop);

                    // 提取人脸 ROI
                    Rect roi = new Rect(xLeftBottom, yLeftBottom, xRightTop - xLeftBottom, yRightTop - yLeftBottom);
                    Mat face = new Mat(frame, roi);

                    // 预处理人脸
                    Imgproc.resize(face, face, new Size(64, 64));
                    face.convertTo(face, CvType.CV_32F);

                    // 将 Mat 转换为 float 数组并进行预处理
                    float[] faceData = new float[(int) (face.total() * face.channels())];
                    face.get(0, 0, faceData);

                    // Transpose the data from HWC to CHW
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
                    // 设置输入张量并推理
                    Tensor inputTensor = inferRequest.getInputTensor();
                    inputTensor.setData(transposedData);
                    inferRequest.infer();

                    // 获取推理结果
                    Tensor outputTensor = inferRequest.getOutputTensor();
                    float[] probs = outputTensor.getData(float[].class, (int) outputTensor.getSize());
                    int maxIndex = argMax(probs);
                    // 显示标签
                    String label = LABELS[maxIndex];
                    Imgproc.putText(frame, label, new Point(xLeftBottom, yLeftBottom - 10), Imgproc.FONT_HERSHEY_SIMPLEX, 1.0, new Scalar(255, 0, 0), 2);
                    Imgproc.rectangle(frame, new Point(xLeftBottom, yLeftBottom), new Point(xRightTop, yRightTop), new Scalar(0, 0, 255), 2);
                }
            }

            // 显示结果(桌面应用的窗口，在安卓中要另想办法)
            HighGui.imshow("Emotion Detection", frame);
            if (HighGui.waitKey(1) == 'q') {
                break;
            }
        }

        // 释放资源
        capture.release();
        HighGui.destroyAllWindows();
    }

    private static Mat blobFromImage(Mat image) {
        return Dnn.blobFromImage(image, 1.0, new Size(64, 64), new Scalar(0), true, false);
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
