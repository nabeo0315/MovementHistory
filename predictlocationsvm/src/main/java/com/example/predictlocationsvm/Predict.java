package com.example.predictlocationsvm;

import android.util.Log;

/**
 * Created by nabeo on 2017/12/27.
 */

public class Predict {
    static{
        System.loadLibrary("jnilibsvm");
    }

    private native void jniSvmPredict(String cmd);

    public Predict(){
        Log.v("PredictLibrary", "init");
    }

    public void startPredict(String cmd){
        jniSvmPredict(cmd);
    }

    public static Predict predict;
    public static Predict getInstance(){
        if(predict == null){
            predict = new Predict();
        }
        return predict;
    }
}
