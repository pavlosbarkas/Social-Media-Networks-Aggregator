package com.example.smn_aggregator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FunctionsActivity extends AppCompatActivity {

    private Button btnTrendingHashtags;
    private Button btnPost;
    public static final String TYPE3 = "trends";
    public static final String TAG = "SMN_Aggregator_App_Debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functions);

        btnTrendingHashtags = findViewById(R.id.btnTrendingHashtags);
        btnPost = findViewById(R.id.btnPost);

        /*
        When the trendings button is pressed, a new twitter task searching for the current trends
        is initialized and the user is redirected to a new activity where the trending
        hashtags are shown
         */
        btnTrendingHashtags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwitterTask twitterTask = new TwitterTask(TYPE3, FunctionsActivity.this);
                twitterTask.execute();
            }
        });

        /*
        When the create post button is pressed the user is redirected to an activity where he can choose
        in which social media he wants to create a post
         */
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "FunctionsActivity --> onClick: move to PostActivity");
                Intent intent = new Intent(FunctionsActivity.this, PostActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed: pressed");
        Intent intent = new Intent(FunctionsActivity.this, MainActivity.class);
        startActivity(intent);
    }
}