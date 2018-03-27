package com.example.joan.meep_upscheduleapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class NewGroupActivity extends AppCompatActivity {

    ListView mFriendsListView;
    ArrayList<String> mFriends = new ArrayList<>();
    boolean[] mSelected;

    public void addGroup(View view) {
        ArrayList<String> selectedUsers = new ArrayList<>();
        for (int i = 0; i < mFriends.size(); i++) {
            if (mSelected[i]) {
                selectedUsers.add(mFriends.get(i));
            }
        }
        if (selectedUsers.size() == 0) {
            Toast.makeText(this, "No user selected!", Toast.LENGTH_SHORT).show();
            return;
        }
        final ParseObject group = new ParseObject("Groups");
        EditText groupNameEditText = (EditText) findViewById(R.id.groupNameEditText);
        group.put("groupName", groupNameEditText.getText().toString());
        selectedUsers.add(ParseUser.getCurrentUser().getUsername());
        group.addAllUnique("users", selectedUsers);
        group.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(NewGroupActivity.this, "Group Created successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), FriendsActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        setTitle("New Group");

        mFriendsListView = (ListView) findViewById(R.id.newGroupListView);

        mFriends.clear();

        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mFriends);


        ParseQuery<ParseObject> query = new ParseQuery<>("Friends");
        query.whereEqualTo("user", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseObject friends : objects) {
                            mFriends.addAll(friends.<String>getList("friends"));
                        }
                        mSelected = new boolean[mFriends.size()];
                        mFriendsListView.setAdapter(arrayAdapter);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });


        mFriendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mSelected[position]) {
                    parent.getChildAt(position).setBackgroundColor(getColor(R.color.white));
                } else {
                    parent.getChildAt(position).setBackgroundColor(getColor(R.color.grey));
                }
                mSelected[position] = !mSelected[position];
            }
        });
    }
}
