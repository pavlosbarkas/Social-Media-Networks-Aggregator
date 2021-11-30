package com.example.smn_aggregator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import twitter4j.Status;

public class TwitterRepliesActivity extends AppCompatActivity {

    public static final String TAG = "SMN_Aggregator_App_Debug";

    /*
    This list contains all the replies that twitter search returned for
    the specific post the user had chosen.
     */
    private ArrayList<Status> replies;



    //A recyclerView is used in order to create a list of all the replies returned from the search
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_replies);

        Intent intent = getIntent();

        StatusesWrapper statusesWrapper = (StatusesWrapper) intent.getSerializableExtra("replies");
        this.replies = statusesWrapper.getStatuses();

        Log.d(TAG, "RECEIVED " + replies.size() + " REPLIES");

        recyclerView = findViewById(R.id.repliesRecView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new TwitterRepliesAdapter(replies);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}