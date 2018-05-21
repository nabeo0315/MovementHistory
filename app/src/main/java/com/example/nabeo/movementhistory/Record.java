package com.example.nabeo.movementhistory;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by nabeo on 2017/11/08.
 */

public class Record {
    private int id;
    private String departurePoin, arrivalPoint, movementState, firstLeaveTime, firstEntryTime, secondLeaveTime, secondEntryTime;
    private static DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private long movingTime, firstStayTime, secondStayTime;

    public Record(int id, String departurePoin, String arrivalPoint){
        this.id = id;
        this.departurePoin = departurePoin;
        this.arrivalPoint = arrivalPoint;
    }

    public void setFirstTime(String time){
        this.firstEntryTime = time.split(" ")[0] + " " + time.split(" ")[1];
        this.firstLeaveTime = time.split(" ")[2] + " " + time.split(" ")[3];
    }

    public void setSecondTime(String time){
        this.secondEntryTime = time.split(" ")[0] + " " + time.split(" ")[1];
        this.secondLeaveTime = time.split(" ")[2] + " " + time.split(" ")[3];
    }

    public void setMovementState(String movementState){
        this.movementState = movementState;
    }

    public int getId(){
        return this.id;
    }

    public String getDeparturePoin(){
        return this.departurePoin;
    }

    public String getArrivalPoint(){
        return this.arrivalPoint;
    }

    public String getMovementState(){
        Log.v("sb", this.movementState);
        return this.movementState;
    }

    public String getFirstEntryTime(){ return firstEntryTime; }

    public String getFirstLeaveTime(){
        return firstLeaveTime;
    }

    public String getSecondEntryTime(){
        return secondEntryTime;
    }

    public String getSecondLeaveTime(){
        return secondLeaveTime;
    }

    public long getMovingTime(){
        try {
            movingTime = formatter.parse(secondEntryTime).getTime() - formatter.parse(firstLeaveTime).getTime();
            //movingTime -= 9*60*60*1000;
        }catch(ParseException e){
            e.printStackTrace();
        }
        return movingTime / 1000;
    }

    public long getFirstStayTime(){
        try{
            firstStayTime = formatter.parse(firstLeaveTime).getTime() - formatter.parse(firstEntryTime).getTime();
        }catch(ParseException e){
            e.printStackTrace();
        }
        return firstStayTime / 1000;
    }

    public long getSecondStayTime(){
        try{
            secondStayTime = formatter.parse(secondLeaveTime).getTime() - formatter.parse(secondEntryTime).getTime();
        }catch(ParseException e){
            e.printStackTrace();
        }
        return secondStayTime / 1000;
    }
}
