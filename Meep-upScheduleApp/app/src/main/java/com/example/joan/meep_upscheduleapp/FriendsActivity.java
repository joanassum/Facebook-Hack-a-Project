package com.example.joan.meep_upscheduleapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FriendsActivity extends AppCompatActivity {

    ArrayList<String> mFriends = new ArrayList<>();
    ArrayList<String> mGroupIds = new ArrayList<>();
    ArrayList<String> mGroupNames = new ArrayList<>();
    int friendNumber;
    ListView mFriendsListView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.clear();
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.friends_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case (R.id.addNewFriends):
                intent = new Intent(getApplicationContext(), AddFriendsActivity.class);
                startActivity(intent);
                break;
            case (R.id.newGroup):
                intent = new Intent(getApplicationContext(), NewGroupActivity.class);
                startActivity(intent);
                break;
            case (R.id.logout):
                ParseUser.logOut();
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        final ReentrantLock lock = new ReentrantLock();

        setTitle("Friends");

        mFriendsListView = (ListView) findViewById(R.id.friendsListView);

        mFriendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), InvitationHistoryActivity.class);
                if (!mFriends.get(position).startsWith("Group:")) {
                    intent.putExtra("isGroup", false);
                    intent.putExtra("username", mFriends.get(position));
                } else {
                    int index = 0;
                    for (int i = 0; i < position; i++) {
                        if (mFriends.get(position).startsWith("Group:")) {
                            i ++;
                        }
                    }
                    intent.putExtra("isGroup", true);
                    intent.putExtra("username", mGroupNames.get(index));
                    intent.putExtra("groupId", mGroupIds.get(index));
                    Log.i("Group id", mGroupIds.get(index));
                }
                startActivity(intent);
            }
        });

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
                        friendNumber = mFriends.size();
                        mFriendsListView.setAdapter(arrayAdapter);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });

        ParseQuery<ParseObject> query2 = new ParseQuery<>("Groups");
        query2.whereContains("users", ParseUser.getCurrentUser().getUsername());
        query2.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseObject group : objects) {
                            mFriends.add("Group: " + group.getString("groupName") +
                                    "\nUsers: " + group.getList("users").toString());
                            mGroupNames.add(group.getString("groupName"));
                            mGroupIds.add(group.getObjectId());
                        }
                        mFriendsListView.setAdapter(arrayAdapter);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
