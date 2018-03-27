package com.example.joan.meep_upscheduleapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotReplyedInvitationActivity extends AppCompatActivity {

    ParseObject mParseObject;
    String mFrdSelected;
    boolean mIsGroup;
    String mGroupId;

    public void accept(View view) {
        if (mParseObject.getList("rejected").contains(ParseUser.getCurrentUser().getUsername())) {
            mParseObject.removeAll("rejected", Arrays.asList(ParseUser.getCurrentUser().getUsername()));
        }
        mParseObject.addUnique("accepted", ParseUser.getCurrentUser().getUsername());
        mParseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(NotReplyedInvitationActivity.this, "Invitation Accepted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), InvitationHistoryActivity.class);
                    intent.putExtra("username", mFrdSelected);
                    intent.putExtra("isGroup", mIsGroup);
                    if (mIsGroup) {
                        intent.putExtra("groupId", mGroupId);
                    }
                    startActivity(intent);
                }
            }
        });
    }

    public void reject(View view) {
        if (mParseObject.getList("accepted").contains(ParseUser.getCurrentUser().getUsername())) {
            mParseObject.removeAll("accepted", Arrays.asList(ParseUser.getCurrentUser().getUsername()));
        }
        mParseObject.addUnique("rejected", ParseUser.getCurrentUser().getUsername());
        mParseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(NotReplyedInvitationActivity.this, "Invitation Rejected", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), InvitationHistoryActivity.class);
                    intent.putExtra("username", mFrdSelected);
                    intent.putExtra("isGroup", mIsGroup);
                    if (mIsGroup) {
                        intent.putExtra("groupId", mGroupId);
                    }
                    startActivity(intent);
                }
            }
        });
    }

    public void reschedule(View view) {
        if (mParseObject.getList("suggestedReschedule").contains(ParseUser.getCurrentUser().getUsername())) {
            return;
        }
        mParseObject.put("suggestedReschedule", ParseUser.getCurrentUser().getUsername());
        mParseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(NotReplyedInvitationActivity.this, "Invitation Rescheduled", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), NewInvitationDateActivity.class);
                    intent.putExtra("username", mFrdSelected);
                    intent.putExtra("isGroup", mIsGroup);
                    if (mIsGroup) {
                        intent.putExtra("groupId", mGroupId);
                    }
                    intent.putExtra("rescheduling", true);
                    intent.putExtra("objectId", mParseObject.getObjectId());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_replyed_invitation);

        setTitle("Invitation Details");

        final Intent intent = getIntent();
        String objectId = intent.getStringExtra("objectId");
        mFrdSelected = intent.getStringExtra("username");
        mIsGroup = intent.getBooleanExtra("isGroup", false);
        if (mIsGroup) {
            mGroupId = intent.getStringExtra("groupId");
        }


        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Invitations");
        query.whereEqualTo("objectId", objectId);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() == 1) {
                        mParseObject = objects.get(0);
                        final ParseObject object = objects.get(0);
                        final String sender = object.getString("sender");
                        String recipent = object.getString("recipient");
                        final List<String> accepted = object.getList("accepted");
                        final List<String> rejected = object.getList("rejected");
                        final List<String> reschedule = object.getList("suggestedReschedule");
                        final String date = object.getDate("meetUpDate") + "";
                        final TextView textView = (TextView) findViewById(R.id.invitationDetailsTextView);
                        if (mIsGroup) {
                            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Groups");
                            query.whereEqualTo("objectId", intent.getStringExtra("groupId"));
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if (e == null) {
                                        if (objects.size() == 1) {
                                            textView.setText("Invited By: " + sender + "\n Invited: " +
                                                    objects.get(0).getList("users").toString() +
                                                    "\n Date: " + date +
                                                    "\nAccepted: " + accepted.size() + "\n" + accepted.toString() +
                                                    "\nRejected: " + rejected.size() + "\n" + rejected.toString() +
                                                    "\nSuggested Reschedule: " + reschedule.size() + "\n" + reschedule.toString());
                                        }
                                    }
                                }
                            });
                        } else {
                            textView.setText("Invited By: " + sender + "\n Invited: " + recipent +
                                    "\n Date: " + date +
                                    "\nAccepted: " + accepted.toString() +
                                    "\nRejected: " + rejected.toString() +
                                    "\nSuggested Reschedule: " + reschedule.toString());
                        }
                    }
                }
            }
        });
    }
}
