package com.example.joan.meep_upscheduleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class AddFriendsActivity extends AppCompatActivity {

    public void onClick(View view) {
        final EditText editText = (EditText) findViewById(R.id.friendsUsernameEditText);
        final String frdUsername = editText.getText().toString();

        ParseQuery<ParseObject> queryfrd = new ParseQuery<>("Friends");
        queryfrd.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
        queryfrd.whereEqualTo("friends", frdUsername);
        queryfrd.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        Toast.makeText(AddFriendsActivity.this, "You are already friends with " + frdUsername, Toast.LENGTH_SHORT).show();
                        editText.setText("");
                    } else {
                        ParseQuery<ParseUser> query = ParseUser.getQuery();
                        query.whereEqualTo("username", frdUsername);
                        query.findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> objects, ParseException e) {
                                if (e == null) {
                                    if (objects.size() > 0) {
                                        sendFriendRequest(frdUsername);
                                    } else {
                                        Toast.makeText(AddFriendsActivity.this, "user: " + frdUsername + " not found", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });


    }

    public void sendFriendRequest(final String frdUsername) {

        ParseQuery<ParseObject> query = new ParseQuery<>("FriendRequests");
        query.whereEqualTo("sender", ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("recipient", frdUsername);
        query.whereNotEqualTo("state", "Declined");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        Toast.makeText(AddFriendsActivity.this, "Error: Request had already been sent before", Toast.LENGTH_SHORT).show();
                    } else {
                        final EditText editText = (EditText) findViewById(R.id.friendsUsernameEditText);
                        final ParseObject request = new ParseObject("FriendRequests");
                        request.put("sender", ParseUser.getCurrentUser().getUsername());
                        request.put("recipient", frdUsername);
                        request.put("state", "notReplyed");

                        editText.setText("");
                        request.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(AddFriendsActivity.this, "FriendRequestSent", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        setTitle("Add Friends");
    }
}
