package com.example.nabeo.movementhistory;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by nabeo on 2017/10/12.
 */

public class PutStringToFile {
    public static int NOW_LOCATION = 0, MOVE_STATE = 1;
    public static File file;
    public final static String ROOT_DIR = Environment.getExternalStorageDirectory().toString();
    private static DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static DateFormat formatter2 = new SimpleDateFormat("HH:mm:ss");
    private static String LOCATION_RESULT = ROOT_DIR + "/LocationHistory/location_history.txt";
    private static String TEMP_RECORD = ROOT_DIR + "/LocationHistory/tempRecord.txt";
    private static String LOCATION_PREDICT_RESULTS = ROOT_DIR + "/LocationHistory/location_predict_results.txt";
    private static String room = " ";
    private static String state = " ";

    PutStringToFile(){

    }

    public static void writeLocationHistry(String str, int mode){
        long currentTime = System.currentTimeMillis();
        String currentTimeFormatted = formatter.format(currentTime);
        try {
            FileWriter fileWriter = new FileWriter(new File(LOCATION_RESULT), true);
            PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(TEMP_RECORD)));
            switch (mode) {
                case 0:
                    room = str;
                    fileWriter.write("* " + currentTimeFormatted + " " + room + "\n");
                    fileWriter.close();
                    printWriter.print("* " + currentTimeFormatted + " " + room);
                    printWriter.close();
                break;
                case 1:
                    state = str;
                    fileWriter.write(formatter2.format(currentTime) + " " + state + "\n");
                    fileWriter.close();
                    printWriter.print(formatter2.format(currentTime) + " " + state);
                    printWriter.close();
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void endOfFile(){
        try {
            FileWriter fileWriter = new FileWriter(new File(LOCATION_RESULT), true);
            fileWriter.write("end\n");
            fileWriter.close();
            PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(TEMP_RECORD)));
            printWriter.print("end");
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startScan(){
        try {
            FileWriter fileWriter = new FileWriter(new File(LOCATION_RESULT), true);
            fileWriter.write("startScan\n");
            fileWriter.close();
            PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(TEMP_RECORD)));
            printWriter.print("end");
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writePredictResult(String str){
        try{
            FileWriter fileWriter = new FileWriter(new File(LOCATION_PREDICT_RESULTS), true);
            fileWriter.write(formatter2.format(System.currentTimeMillis()) + "\n" + str);
            fileWriter.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
