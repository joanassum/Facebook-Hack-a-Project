package com.example.joan.meep_upscheduleapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import java.util.List;

public class InvitationHistoryActivity extends AppCompatActivity {

    String mFriendSelected;

    ArrayList<String> mHistory = new ArrayList<>();
    List<ParseObject> mParseObjects = new ArrayList<>();
    ArrayAdapter mArrayAdapter;
    boolean mIsGroup;
    String mGroupId = "";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.clear();
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.invitation_history_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case (R.id.newInvitation):
                intent = new Intent(getApplicationContext(), NewInvitationDateActivity.class);
                intent.putExtra("username", mFriendSelected);
                intent.putExtra("isGroup", mIsGroup);
                if (mIsGroup) {
                    intent.putExtra("groupId", mGroupId);
                }
                intent.putExtra("rescheduling", false);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        mArrayAdapter.notifyDataSetChanged();
        super.onStart();
    }

    public void updateInviteHistory() {
        ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Invitations");
        query1.whereEqualTo("sender", ParseUser.getCurrentUser().getUsername());
        query1.whereEqualTo("recipient", mFriendSelected);

        ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Invitations");
        query2.whereEqualTo("recipient", ParseUser.getCurrentUser().getUsername());
        query2.whereEqualTo("sender", mFriendSelected);

        List<ParseQuery<ParseObject>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);

        ParseQuery<ParseObject> query = ParseQuery.or(queries);
        query.orderByAscending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    mHistory.clear();
                    if (objects.size() > 0) {
                        mParseObjects = objects;
                        for (ParseObject invitation : objects) {
                            String state = "";
                            if (invitation.getList("accepted").size() == 1) {
                                state = "Accepted";
                            } else if (invitation.getList("rejected").size() == 1) {
                                state = "Rejected";
                            } else if (invitation.getList("suggestedReschedule").size() == 1) {
                                state = "Suggested Reschedule";
                            } else {
                                state = "Not Replyed";
                            }
                            String historyContent = "Invited By: " + invitation.getString("sender") +
                                    "\nDate: " + invitation.getDate("meetUpDate").toString() +
                                    "\nState: " + state;

                            if (!invitation.getString("sender").equals(ParseUser.getCurrentUser().getUsername())) {
                                historyContent = "> " + historyContent;
                            }
                            mHistory.add(historyContent);
                        }
                        mArrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), FriendsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation_history);

        Intent intent = getIntent();
        mIsGroup = intent.getBooleanExtra("isGroup", false);
        if (mIsGroup) {
            mGroupId = intent.getStringExtra("groupId");
        }
        mFriendSelected = intent.getStringExtra("username");
        setTitle("Invitation with " + mFriendSelected);

        ListView listView = (ListView) findViewById(R.id.invitationListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mParseObjects.get(position).getString("sender")
                                .equals(ParseUser.getCurrentUser().getUsername())) {
                    Intent intent = new Intent(getApplicationContext(), NotReplyedInvitationActivity.class);
                    intent.putExtra("objectId", mParseObjects.get(position).getObjectId());
                    intent.putExtra("username", mFriendSelected);
                    intent.putExtra("isGroup", mIsGroup);
                    if (mIsGroup) {
                        intent.putExtra("groupId", mGroupId);
                    }
                    startActivity(intent);
                }
            }
        });
        mArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mHistory);
        listView.setAdapter(mArrayAdapter);

        if (!mIsGroup) {
            updateInviteHistory();
        } else {
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Invitations");
            query.whereEqualTo("groupId", mGroupId);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        mHistory.clear();
                        if (objects.size() > 0) {
                            mParseObjects = objects;
                            for (ParseObject invitation : objects) {
                                List<String> accepted = invitation.getList("accepted");
                                List<String> rejected = invitation.getList("rejected");
                                List<String> reschedule = invitation.getList("suggestedReschedule");
                                String historyContent = "Invited By: " + invitation.getString("sender") +
                                        "\nDate: " + invitation.getDate("meetUpDate").toString() +
                                        "\nAccepted: " + accepted.size() + "\n" + accepted.toString() +
                                        "\n Rejected: " + rejected.size() + "\n" + rejected.toString() +
                                        "\n Suggested Reschedule: " +  reschedule.size() + "\n" + reschedule.toString();

                                if (!invitation.getString("sender").equals(ParseUser.getCurrentUser().getUsername())) {
                                    historyContent = "> " + historyContent;
                                }
                                mHistory.add(historyContent);
                            }
                            mArrayAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }

    }
}
