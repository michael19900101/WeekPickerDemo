package com.michael.weekpickerdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.michael.weekpickerdemo.entity.DateUtil;
import com.michael.weekpickerdemo.entity.Week;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextView weektext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weektext = findViewById(R.id.weektext);
        weektext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new WeekPickerDialog(MainActivity.this, Calendar.getInstance(), new WeekPickerDialog.OnWeekSelectListener() {
                    @Override
                    public void onWeekSelect(Week week) {
                        if(week == null){
                            weektext.setText("请选择");
                        }else {
                            weektext.setText(week.getSelectWeekBeginAndEnd());
                        }
                    }
                }).show();
            }
        });
    }
}
