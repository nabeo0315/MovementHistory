package com.example.nabeo.movementhistory;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by nabeo on 2017/11/09.
 */

public class ShowDetail extends AppCompatActivity {
    private String departurePoint, movementState, arrivalPoint;
    private TextView textView;
    private long movingTime, firstStayTime, secondStayTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_detail);

        textView = (TextView)findViewById(R.id.recordView);
        Intent intent = getIntent();
        StringBuilder sb = new StringBuilder();
        departurePoint = intent.getStringExtra("departurePoint");
        movementState = intent.getStringExtra("movementState");
        arrivalPoint = intent.getStringExtra("arrivalPoint");
        movingTime = intent.getLongExtra("movingTime", 0);
        firstStayTime = intent.getLongExtra("firstStayTime", 0);
        secondStayTime = intent.getLongExtra("secondStayTime", 0);
        sb.append(departurePoint + " 在室時間:" + firstStayTime + "s\n"
                    + movementState +
                    arrivalPoint + " 在室時間:" + secondStayTime + "s\n"
                    + "移動時間:" + movingTime + "s");
        textView.setText(sb.toString());

    }
}
