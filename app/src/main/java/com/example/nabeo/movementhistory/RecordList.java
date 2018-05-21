package com.example.nabeo.movementhistory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by nabeo on 2017/11/07.
 */

public class RecordList extends AppCompatActivity {
    private ListView listView;
    private File file;
    public final static String ROOT_DIR = Environment.getExternalStorageDirectory().toString();
    private static String LOCATION_RESULT = ROOT_DIR + "/LocationHistory/location_history.txt";
    private static String PARSED_RECORD = ROOT_DIR + "/LocationHistory/record.txt";
    private static String TEMP = ROOT_DIR + "/LocationHistory/temp.txt";
    private static DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    private static DateFormat formatter2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private Date dateTo, dateFrom = null;
    private long slashHold = 0;
    private ArrayList<Record> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_list);
        makeMovementHistory(LOCATION_RESULT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.file_menu, menu);
        menu.findItem(R.id.selectFile).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent selectFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                selectFileIntent.setType("file/*");
                startActivityForResult(selectFileIntent, 1234);
                return true;
            }
        });
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try{
            if(requestCode == 1234 && resultCode == RESULT_OK) {
                Log.v("data", data.getDataString());
                String filePath = data.getDataString().replace("file://", "");
                String decodefilePath = URLDecoder.decode(filePath, "utf-8");
                makeMovementHistory(decodefilePath);
                Log.v("filepath", decodefilePath);
            }
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }

    private void makeMovementHistory(String filePath){
        try{
            BufferedReader br = new BufferedReader(new FileReader(filePath));

            String line, departurePoint = "", arrivalPoint = "";
            String state = "", place = "", time = "";
            String entryTime = "", leaveTime = "", additionalTime = "", firstTime = "", secondTime = "";
            boolean firstFlag = true,firstFlag2 = true, firstStateFlag = true, firstStopFlag = true,
                    checkFlag = false, fatalLocationFlag = false, writeLocation = true, duprecatedFlag = false;
            StringBuilder sb = new StringBuilder(), sb2 = new StringBuilder();
            StringBuilder movingState = new StringBuilder();
            int id = 0;
            Record record;
            long dateToTime = 0, dateFromTime = 0, interval = 0, timeOfState = 0;
            slashHold = formatter.parse("00:00:03").getTime();

            while((line = br.readLine()) != null){
                if(line.startsWith("*") && !line.split(" ")[3].equals(place)){
//                    if(!state.equals("") || firstStopFlag){
//                        movingState.append(state + " " + formatter.format(interval) + "\n");
//                        interval = - (9*60*60*1000);
//                    }
                    if(!firstFlag){
                        Log.v("mills", String.valueOf(interval) + " " + slashHold);
                        if(interval >= slashHold) movingState.append(state + " " + formatter.format(interval) + "\n");
                        interval = -(9*60*60*1000);
                        if(!additionalTime.equals(""))leaveTime = leaveTime.split(" ")[0] + " " + additionalTime;
                        if(writeLocation)sb.append("* " + entryTime + " " + leaveTime + " " + place + "\n");
                        sb.append(movingState);
                        movingState.setLength(0);
                        firstStopFlag = true;
                    }
                    //sb.append(line + "\n");
                    if(!fatalLocationFlag) {
                        entryTime = line.split(" ")[1] + " " + line.split(" ")[2];
                        leaveTime = line.split(" ")[1] + " " + line.split(" ")[2];
                        additionalTime = "";
                        firstStateFlag = true;
                        firstFlag = false;
                        writeLocation = true;
                        state = "";
                        place = line.split(" ")[3];
                    }else{
                        writeLocation = false;
                    }
                    checkFlag = false;
                    fatalLocationFlag = false;
                }else if(line.startsWith("*") && line.split(" ")[3].equals(place) && !fatalLocationFlag){
                    leaveTime = line.split(" ")[1] + " " + line.split(" ")[2];
                    additionalTime = "";
                    movingState.setLength(0);//StringBuilderの初期化
                    interval = -(9*60*60*1000);
                    firstStateFlag = true;
                    firstStopFlag = true;
                    checkFlag = false;
                    state = "";
                }else if(line.equals("end")){
                    if(additionalTime != "")leaveTime = leaveTime.split(" ")[0] + " " + additionalTime;
                    sb.append("* " + entryTime + " " + leaveTime + " " + place + "\n");
                    sb.append(movingState);
                }else if(line.equals("startScan")){
                    checkFlag = true;
                }else{
                    //Log.v("line", line);
                    if(!state.equals(line.split(" ")[1])){//別の状態が現れた場合,または最初
                        Log.v("mills", String.valueOf(interval) + " " + slashHold);
                        if(!firstStateFlag && !firstStopFlag && interval >= slashHold) {//最初は出力しない
                            movingState.append(state + " " + formatter.format(interval) + "\n");
                        }
                        interval = -(9*60*60*1000);
                        state = line.split(" ")[1];
                        if(state.equals("Stop") && firstStateFlag) firstStopFlag = true; else firstStopFlag = false;
                        if(checkFlag && !state.equals("Stop")){
                            fatalLocationFlag = true;
                            Log.v("checkFlag", "true");
                        }
                        firstStateFlag = false;
                        dateFrom = formatter.parse(line.split(" ")[0]);
                        dateFromTime = dateFrom.getTime();
                        //Log.v("dateFromTime", String.valueOf(interval));
                    }else{//同じ状態の場合
                        if(state.equals("Stop") && firstStopFlag){
                            additionalTime = line.split(" ")[0];
                        }
                        dateTo = formatter.parse(line.split(" ")[0]);
                        dateToTime = dateTo.getTime();
                        interval = (dateToTime - dateFromTime) - (9*60*60*1000);//差分を取り続ける
                    }
                }
            }

            File parsedRecord = new File(PARSED_RECORD);
            if(parsedRecord.exists()) parsedRecord.delete();
            FileWriter fileWriter = new FileWriter(parsedRecord);
            fileWriter.write(sb.toString());
            fileWriter.write("end");
            fileWriter.close();
            br.close();

            BufferedReader br2 = new BufferedReader(new FileReader(PARSED_RECORD));
            list = new ArrayList<Record>();
            Calendar calendar = Calendar.getInstance();
            Calendar timeCalendar = Calendar.getInstance();
            StringBuilder stringBuilder = new StringBuilder();

            while((line = br2.readLine()) != null){
                if(line.startsWith("*")){
                    if(!firstFlag2){
                        arrivalPoint = line.split(" ")[5];
                        movingState.append(state + " " + time + "\n");
                        stringBuilder.append(movingState);
                        secondTime = line.split(" ")[1] + " " + line.split(" ")[2] + " " + line.split(" ")[3] + " " + line.split(" ")[4];
                        record = new Record(id++, departurePoint, arrivalPoint);
                        record.setMovementState(movingState.toString());
                        record.setFirstTime(firstTime);
                        record.setSecondTime(secondTime);
                        list.add(record);
                    }
                    movingState.setLength(0);
                    stringBuilder.append(line + "\n");
                    firstTime = line.split(" ")[1] + " " + line.split(" ")[2] + " " + line.split(" ")[3] + " " + line.split(" ")[4];
                    place = line.split(" ")[5];
                    departurePoint = place;
                    firstFlag2 = false;
                    state = "";
                }else if(state.equals(line.split(" ")[0])){
                    //calendar.setTimeInMillis(formatter.parse(time).getTime() + formatter.parse(line.split(" ")[1]).getTime());
                    timeOfState = formatter.parse(time).getTime() + formatter.parse(line.split(" ")[1]).getTime() + 9*60*60*1000;
                    calendar.setTime(formatter.parse(line.split(" ")[1]));
                    timeCalendar.setTime(formatter.parse(time));
                    Log.v("times", line.split(" ")[1] + " " + time);

                    calendar.add(Calendar.SECOND, timeCalendar.MINUTE*60 + timeCalendar.SECOND);
                    Log.v("timeOFState", formatter2.format(timeOfState));
                    duprecatedFlag = true;
                    state = line.split(" ")[0];
                    time = formatter2.format(timeOfState);
                }else if(line.equals("end")) {

                }else{
                    if(duprecatedFlag){
                        movingState.append(state + " " + formatter.format(timeOfState) + "\n");
                        duprecatedFlag = false;
                    }else if(!state.equals("")){
                        movingState.append(state + " " + time + "\n");
                    }
                    state = line.split(" ")[0];
                    time = line.split(" ")[1];
                }
            }

            File parsedRecord2 = new File(PARSED_RECORD);
            if(parsedRecord2.exists()) parsedRecord.delete();
            FileWriter fileWriter2 = new FileWriter(parsedRecord);
            fileWriter2.write(stringBuilder.toString());
            fileWriter2.write("end");
            fileWriter2.close();

            br2.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }catch (ParseException e){
            e.printStackTrace();
        }

        listView = (ListView)findViewById(R.id.listView);

        MyAdapter myAdapter = new MyAdapter(this);
        myAdapter.setList(list);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Record record = (Record)parent.getItemAtPosition(position);
                Intent intent = new Intent(RecordList.this, ShowDetail.class);
                intent.putExtra("departurePoint", record.getDeparturePoin());
                intent.putExtra("movementState", record.getMovementState());
                intent.putExtra("arrivalPoint", record.getArrivalPoint());
                intent.putExtra("movingTime", record.getMovingTime());
                intent.putExtra("firstStayTime", record.getFirstStayTime());
                intent.putExtra("secondStayTime", record.getSecondStayTime());
                startActivity(intent);
            }
        });
    }
}
