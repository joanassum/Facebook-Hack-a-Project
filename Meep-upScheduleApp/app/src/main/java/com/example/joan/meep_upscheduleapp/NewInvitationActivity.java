package com.example.joan.meep_upscheduleapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Date;

public class NewInvitationActivity extends AppCompatActivity {

    String mFrdSelected;
    int mYear;
    int mMonth;
    int mDay;
    int mHour;
    int mMinutes;

    public void onClick(View view) {
        Date date = new Date(mYear - 1900, mMonth, mDay, mHour, mMinutes);
        final ParseObject invitation = new ParseObject("Invitations");
        invitation.put("sender", ParseUser.getCurrentUser().getUsername());
        invitation.put("recipient", mFrdSelected);
        invitation.put("meetUpDate", date);
        invitation.put("state", "NotReplyed");

        invitation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(NewInvitationActivity.this, "Invitation Sent", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Intent intent = new Intent(getApplicationContext(), InvitationHistoryActivity.class);
        intent.putExtra("username", mFrdSelected);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_invitation);

        TextView textView = (TextView) findViewById(R.id.ConfirmTextView);

        Intent intent = getIntent();
        mFrdSelected = intent.getStringExtra("username");
        mYear = intent.getIntExtra("year", 0);
        mMonth = intent.getIntExtra("month", 0);
        mDay = intent.getIntExtra("day", 0);
        mHour = intent.getIntExtra("hour", 0);
        mMinutes = intent.getIntExtra("minutes", 0);

        textView.setText("Date: " + mDay + "/" + mMonth + "/" + mYear + "\n"
                + "Time: " + mHour + " : " + mMinutes + "\n"
                + "To: " + mFrdSelected);
    }
}
