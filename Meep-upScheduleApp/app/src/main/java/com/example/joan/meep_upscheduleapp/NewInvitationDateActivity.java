package com.example.joan.meep_upscheduleapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import java.util.Date;

public class NewInvitationDateActivity extends AppCompatActivity {

    String mFrdSelected;
    DatePicker mDatePicker;

    public void onClick(View view) {
        int year = mDatePicker.getYear();
        int month = mDatePicker.getMonth();
        int day = mDatePicker.getDayOfMonth();

        Intent intent = new Intent(getApplicationContext(), NewInvitationTimeActivity.class);
        intent.putExtra("username", mFrdSelected);
        intent.putExtra("year", year);
        intent.putExtra("month", month);
        intent.putExtra("day", day);
        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_invitation_date);

        Intent intent = getIntent();
        mFrdSelected = intent.getStringExtra("username");

        mDatePicker = (DatePicker) findViewById(R.id.datePicker);
        Date date = new Date();
        mDatePicker.updateDate(1900+ date.getYear(), date.getMonth(), date.getDay());

    }
}
