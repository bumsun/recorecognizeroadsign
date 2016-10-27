package com.partymaker.znaki;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgproc.COLOR_RGB2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvRectangle;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_DO_ROUGH_SEARCH;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_FIND_BIGGEST_OBJECT;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_SCALE_IMAGE;

/**
 * Created by X550V on 21.10.2016.
 */

public class TrackSigns {
    private opencv_core.IplImage origImg;
    private opencv_core.IplImage threshold;
    private String nameSign;

    private String paravozik = "paravozik.xml";
    private String proezd_zapreshen = "proezd_zapreshen.xml";
    private String peshehod = "peshehod.xml";
    private String stope = "stope.xml";
    private String odin_road = "odin_road.xml";


    private opencv_objdetect.CascadeClassifier cascade;
    private OpenCVFrameConverter.ToMat.ToMat toMat = new OpenCVFrameConverter.ToMat();
    private ArrayList<Map<String, opencv_core.Rect>> allRects = new ArrayList<>();
    private OnFindObjectsListener onFindObjectsListener;

    public interface OnFindObjectsListener {
        public void onFinish(Bitmap photo, String signName);
    }

    private opencv_core.Size minSize = new opencv_core.Size(20, 20);

    public void setOnFindObjectsListener(OnFindObjectsListener onFindObjectsListener) {
        this.onFindObjectsListener = onFindObjectsListener;
    }


    private File save(String cascadeName, Activity activity){
        File mCascadeFile = null;
        try {
            // load cascade file from application resources
            InputStream is = activity.getAssets().open(cascadeName);
            File cascadeDir = activity.getDir("signs", Context.MODE_PRIVATE);
            mCascadeFile = new File(cascadeDir, cascadeName);
            FileOutputStream os = new FileOutputStream(mCascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mCascadeFile;
    }



    public void signs1(String pathXml) {

        cascade = new opencv_objdetect.CascadeClassifier(pathXml);
        cascade.load(pathXml);
        opencv_core.RectVector sign = new opencv_core.RectVector();

        System.out.println("true = ");
        //cascade.detectMultiScale(img, signs, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING, Size(30, 30), Size(200, 200));
        cascade.detectMultiScale(toMat.convert(toMat.convert(threshold)), sign, 1.27, 6, CV_HAAR_DO_ROUGH_SEARCH & CV_HAAR_FIND_BIGGEST_OBJECT, minSize, null);

        //cvHaarDetectObjects()
        if (sign.size() != 0) {
            putInRectArray("Пешеходный переход", sign);
        }
        System.out.println("peshehod size = " + sign.size());

    }

    public void signs2(String pathXml) {
        opencv_core.RectVector sign = new opencv_core.RectVector();
        cascade = new opencv_objdetect.CascadeClassifier(pathXml);
        cascade.load(pathXml);
        System.out.println("true = ");
        //cascade.detectMultiScale(img, signs, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING, Size(30, 30), Size(200, 200));
        cascade.detectMultiScale(toMat.convert(toMat.convert(threshold)), sign, 1.25, 6, CV_HAAR_SCALE_IMAGE, minSize, null);
        //cvHaarDetectObjects()
        if (sign.size() != 0) {


            putInRectArray("Въезд запрещён", sign);
        }

    }

    public void assetToFile(Activity activity, String pathToPhoto) throws FileNotFoundException {
        // opencv_core.IplImage origImg = cvLoadImage(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/may.png");
        origImg = cvLoadImage(pathToPhoto);

        threshold = cvCreateImage(cvGetSize(origImg), 8, 1);

        AndroidFrameConverter frameConverter = new AndroidFrameConverter();
        cvCvtColor(origImg, threshold, COLOR_RGB2GRAY);

        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/TrackSigns");
        System.out.println("folder = " + folder);
        if (!folder.exists()) {
            folder.mkdirs();
        }


        signs1(save(peshehod, activity).getAbsolutePath());
        signs2(save(stope, activity).getAbsolutePath());
        signs3(save(odin_road, activity).getAbsolutePath());
        signs4(save(paravozik, activity).getAbsolutePath());
        signs5(save(proezd_zapreshen, activity).getAbsolutePath());


        Frame frame1 = toMat.convert(origImg);
        Bitmap bitmap1 = frameConverter.convert(frame1);
        onFindObjectsListener.onFinish(bitmap1, nameSign);




    }

    public void signs3(String pathXml) {

        cascade = new opencv_objdetect.CascadeClassifier(pathXml);
        cascade.load(pathXml);
        opencv_core.RectVector sign = new opencv_core.RectVector();
        System.out.println("true = ");
        //cascade.detectMultiScale(img, signs, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING, Size(30, 30), Size(200, 200));
        cascade.detectMultiScale(toMat.convert(toMat.convert(threshold)), sign, 1.27, 6, CV_HAAR_FIND_BIGGEST_OBJECT, minSize, null);
        //cvHaarDetectObjects()
        if (sign.size() != 0) {
            putInRectArray("Дорога с односторонним движением", sign);
        }
        System.out.println("odin_road size = " + sign.size());

    }

    public void signs5(String pathXml) {

        cascade = new opencv_objdetect.CascadeClassifier(pathXml);
        cascade.load(pathXml);
        opencv_core.RectVector sign = new opencv_core.RectVector();
        System.out.println("true = ");

        //cascade.detectMultiScale(img, signs, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING, Size(30, 30), Size(200, 200));
        cascade.detectMultiScale(toMat.convert(toMat.convert(threshold)), sign, 1.42, 6, CV_HAAR_DO_ROUGH_SEARCH & CV_HAAR_FIND_BIGGEST_OBJECT, minSize, null);
        //cvHaarDetectObjects()
        if (sign.size() != 0) {
            putInRectArray("Движение запрещено", sign);
        }
        System.out.println("movement_prohibition size = " + sign.size());
        drawRectangle();
    }

    public void signs4(String pathXml) {
        cascade = new opencv_objdetect.CascadeClassifier(pathXml);
        cascade.load(pathXml);
        opencv_core.RectVector sign = new opencv_core.RectVector();
        System.out.println("true = ");
        //cascade.detectMultiScale(img, signs, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING, Size(30, 30), Size(200, 200));
        cascade.detectMultiScale(toMat.convert(toMat.convert(threshold)), sign, 1.3, 7, CV_HAAR_DO_CANNY_PRUNING, minSize, null);
        if (sign.size() != 0) {
            putInRectArray("Железнодорожный переезд без шлагбаума", sign);
        }
        // cascade.detectMultiScale(toMat.assetToFile(toMat.assetToFile(threshold)), signs,);
        //cvHaarDetectObjects()
        System.out.println("train_new size = " + sign.size());

    }



    private void drawRectangle() { // рисует прямоугольник

        Map<Integer, Map<String, opencv_core.Rect>> vectorMap = new HashMap<>();
        int maxArea = 0;

        System.out.println("allRects size = " + allRects.size());
        for (int i = 0; i < allRects.size(); i++) {
            for (String key : allRects.get(i).keySet()) {
                int square = allRects.get(i).get(key).height() * allRects.get(i).get(key).width();
                vectorMap.put(square, allRects.get(i));

                if (maxArea == 0) {
                    maxArea = square;
                } else {
                    if (maxArea <= square) {
                        maxArea = square;
                    }
                }
            }


        }
        for (int key : vectorMap.keySet()) {

            if (key >= maxArea * 3 / 5) {
                for (String name : vectorMap.get(key).keySet()) {
                    opencv_core.Rect r = vectorMap.get(key).get(name);
                    cvRectangle(origImg, new opencv_core.CvPoint(r.x(), r.y()), new opencv_core.CvPoint(r.x() + r.width(), r.y() + r.height()), new opencv_core.CvScalar(0, 255, 0, 0), 2, 8, 0);
                    if (nameSign != null && !nameSign.contains(name)) {

                        nameSign = name + ", " + name;
                    } else {

                        nameSign = name;
                    }


                }
            }
        }


//        for (int key : vectorMap.keySet()) {
//
//            if (key >= maxArea * 6 / 10) {
//                for (String name : vectorMap.get(key).keySet()) {
//                    opencv_core.Rect r = vectorMap.get(key).get(name);
//                    cvRectangle(origImg, new opencv_core.CvPoint(r.x(), r.y()), new opencv_core.CvPoint(r.x() + r.width(), r.y() + r.height()), new opencv_core.CvScalar(0, 0, 255, 0), 2, 8, 0);
//                    if (nameSign != null && !nameSign.contains(name)) {
//
//                        nameSign = name + ", " + name;
//                    } else {
//
//                        nameSign = name;
//                    }
//
//
//                }
//            }
//        }

    }
    private void putInRectArray(String signName, opencv_core.RectVector sign) {
        for (int i = 0; i < sign.size(); i++) {
            System.out.println("put in array cicle");
            Map<String, opencv_core.Rect> currentMap = new HashMap<>();
            currentMap.put(signName, sign.get(i));
            allRects.add(currentMap);
        }
    }
}

