package com.example.resistorscannerappnew;
import android.util.SparseIntArray;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.*;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.imgproc.Moments;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class ColorCodeProcessor {




    // HSV colour bounds
    private static final Scalar COLOR_BOUNDS[][] = {
            { new Scalar(0, 0, 0),   new Scalar(179, 255, 40) },    // black
            { new Scalar(7, 40, 85), new Scalar(15, 255, 145) },    // brown
            { new Scalar(0, 85, 0),   new Scalar(5, 255, 200) },    // red
            { new Scalar(5, 100, 160), new Scalar(15, 255, 255) },   // orange
            { new Scalar(19, 87, 136), new Scalar(24, 255, 255) }, // yellow
            { new Scalar(40, 40, 40), new Scalar(80, 115, 255) },   // green
            { new Scalar(80, 50, 50), new Scalar(106, 250, 150) },  // blue
            { new Scalar(115, 0, 90), new Scalar(175, 255, 255) }, // violet
            { new Scalar(15, 0, 120), new Scalar(179, 30, 135) },   // silver
            { new Scalar(0, 0, 90), new Scalar(180, 15, 140) }     // white
    };

    public final String color_string[] = {"Black", "Brown", "Red", "Orange", "Yellow", "Green", "Blue", "Violet", "Silver", "White"};

    private HashMap<Integer, String> areas = new HashMap<Integer, String>();
    private Map<Integer, String> sorted_values;
    private ArrayList<String> sorted_colors = new ArrayList<>();

    private int x;
    private int y;
    private int h;
    private int w;
    static double area_limit = 20;

    public Mat frameProcess(CvCameraViewFrame frame){

        Mat matHSV = frame.rgba();
        Mat filteredHSV =new Mat();
        int cols = matHSV.cols();
        int rows = matHSV.rows();

        System.out.println("cols: " + cols +  " rows: " + rows);

        Mat scanArea = matHSV.submat((int)(rows*0.5), (int)(rows*0.5)+10, (int)(cols*0.5) - 50, (int)(cols*0.5) + 50);
        System.out.println("scanArea: rows start: " + (int)(rows*0.5) + ", rows end: " + (int)(rows*0.5 +15) + ", col start: " + (int)(cols*0.5 - 50 ) + ", col end: " + ((int)(cols*0.5) + 50));
        Imgproc.cvtColor(scanArea, scanArea, Imgproc.COLOR_RGBA2BGR);
        Imgproc.bilateralFilter(scanArea, filteredHSV, 5, 80, 80);
        Imgproc.cvtColor(filteredHSV, filteredHSV, Imgproc.COLOR_RGB2HSV);

        findColor(filteredHSV);

        Imgproc.rectangle(matHSV, new Point(x+590, y+360), new Point(590+x+w, y+360+h), new Scalar(255, 255, 0), 1);


        Imgproc.rectangle(matHSV, new Point((int)(cols*0.5) - 50, (int)(rows*0.5)), new Point((int)(cols*0.5) + 50, (int)(rows*0.5)-5), new Scalar(255, 255, 255), 1);
        //Imgproc.rectangle(matHSV, new Point((int)(cols*0.5) - 50, (int)(rows*0.5)), new Point((int)(cols*0.5) + 50, (int)(rows*0.5)+10), new Scalar(255, 255, 255), 1);


        String valueString = sorted_colors.get(0) + ", " + sorted_colors.get(1) + ", " + sorted_colors.get(2);
        Imgproc.putText(matHSV, valueString, new Point(10, 100), Core.FONT_HERSHEY_COMPLEX,
                2, new Scalar(255, 0, 0, 255), 3);


        return matHSV;
    };


    public void findColor(Mat searchFrame) {


        System.out.println("Reached findColor");
        Rect rect = null;

        for (int i=0; i < 10; i++ ){

            Mat mask = new Mat();
            Mat hierarchy = new Mat();
            List<MatOfPoint> contour = new ArrayList<MatOfPoint>();
            Core.inRange(searchFrame, COLOR_BOUNDS[i][0], COLOR_BOUNDS[i][1], mask);

            Imgproc.findContours(mask, contour, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

            for (int j = 0; j < contour.size(); j++){
                System.out.println("Reached for loop j");
                double area = Imgproc.contourArea(contour.get(j));
                rect = Imgproc.boundingRect(contour.get(j));
                x = rect.x;
                y = rect.y;
                h = rect.height;
                w = rect.width;
                if (area > area_limit){

                    try{
                        if (color_string[j].contentEquals("Orange")){
                            System.out.println("############### Found color: " + color_string[j]);
                            System.out.println("############### Found area: " + area);
                            System.out.println("X: " + x);
                            System.out.println("Y: " + y);
                            System.out.println("w: " + w);
                            System.out.println("h: " + h);


                        }

                        if (sorted_colors.size() > 4){
                            System.out.println("Reached color clear #############");
                            sorted_colors.clear();
                        }
                        else{
                            System.out.println("Reached else color string #############");
                            sorted_colors.add(color_string[j]);
                        }

                        areas.put(x, color_string[j]);







                        sorted_values = new TreeMap<Integer, String>(areas);
                        System.out.println("colors found: " + sorted_colors);

                    }
                    catch(ArrayIndexOutOfBoundsException | NullPointerException e){
                        System.out.println("Exception");
                    }

                    //Moments M = Imgproc.moments(contour.get(j));
                    //int cx = (int) (M.get_m10() / M.get_m00());


                }

            }





        }
    }

}
