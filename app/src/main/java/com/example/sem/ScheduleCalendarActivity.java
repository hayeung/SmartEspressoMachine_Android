package com.example.sem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ScheduleCalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_calendar);
    }

    public void scheduler_time_menu(View view){
        Intent intent = new Intent(this, ScheduleTimeActivity.class);
        startActivity(intent);
    }
}
