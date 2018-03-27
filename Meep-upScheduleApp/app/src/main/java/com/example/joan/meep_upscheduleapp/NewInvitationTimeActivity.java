package com.example.joan.meep_upscheduleapp;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

public class NewInvitationTimeActivity extends AppCompatActivity {

    String mFrdSelected;
    TimePicker mTimePicker;
    int mYear;
    int mMonth;
    int mDay;
    boolean mIsGroup;
    String mGroupId;


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClick(View view) {

        int hour = mTimePicker.getHour();
        int minutes = mTimePicker.getMinute();

        Intent intent = new Intent(getApplicationContext(), NewInvitationActivity.class);
        intent.putExtra("username", mFrdSelected);
        intent.putExtra("year", mYear);
        intent.putExtra("month", mMonth);
        intent.putExtra("day", mDay);
        intent.putExtra("hour", hour);
        intent.putExtra("minutes", minutes);
        intent.putExtra("isGroup", mIsGroup);
        if (mIsGroup) {
            intent.putExtra("groupId", mGroupId);
        }
        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_invitation_time);

        mTimePicker = (TimePicker) findViewById(R.id.timePicker);

        Intent intent = getIntent();
        mFrdSelected = intent.getStringExtra("username");
        mYear = intent.getIntExtra("year", 0);
        mMonth = intent.getIntExtra("month", 0);
        mDay = intent.getIntExtra("day", 0);
        mIsGroup = intent.getBooleanExtra("isGroup", false);
        if (mIsGroup) {
            mGroupId = intent.getStringExtra("groupId");
        }

    }
}
