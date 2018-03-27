package com.example.joan.meep_upscheduleapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class NewInvitationDateActivity extends AppCompatActivity {

    String mFrdSelected;
    DatePicker mDatePicker;
    boolean mIsGroup;
    String mGroupId;

    public void onClick(View view) {
        int year = mDatePicker.getYear();
        int month = mDatePicker.getMonth();
        int day = mDatePicker.getDayOfMonth();

        Intent intent = new Intent(getApplicationContext(), NewInvitationTimeActivity.class);
        intent.putExtra("username", mFrdSelected);
        intent.putExtra("year", year);
        intent.putExtra("month", month);
        intent.putExtra("day", day);
        intent.putExtra("isGroup", mIsGroup);
        if (mIsGroup) {
            intent.putExtra("groupId", mGroupId);
        }
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        if (intent.getBooleanExtra("rescheduling", false)) {
            String objectId = intent.getStringExtra("objectId");

            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Invitations");
            query.whereEqualTo("objectId", objectId);

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() == 1) {
                            ParseObject object = objects.get(0);
                            object.removeAll("suggestedReschedule", Arrays.asList(ParseUser.getCurrentUser().getUsername()));
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Toast.makeText(NewInvitationDateActivity.this,
                                                "Reschedule Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_invitation_date);

        Intent intent = getIntent();
        mFrdSelected = intent.getStringExtra("username");
        mIsGroup = intent.getBooleanExtra("isGroup", false);
        if (mIsGroup) {
            mGroupId = intent.getStringExtra("groupId");
        }

        mDatePicker = (DatePicker) findViewById(R.id.datePicker);
        Date date = new Date();
        mDatePicker.updateDate(1900+ date.getYear(), date.getMonth(), date.getDay());

    }
}
