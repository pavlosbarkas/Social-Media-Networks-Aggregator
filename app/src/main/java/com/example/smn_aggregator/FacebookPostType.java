package com.example.smn_aggregator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class FacebookPostType extends AppCompatActivity {

    private Button text;
    private Button photo;
    public static final String TAG = "SMN_Aggregator_App_Debug";
    public static final String TYPE1 = "text";
    public static final String TYPE2 = "photo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_post_type);

        Intent intent = getIntent();
        if (intent!=null){
            text = findViewById(R.id.btnFacebookText);
            photo = findViewById(R.id.btnFacebookPhoto);

            /*
            Depending on what the user wants to post, the corresponding button is clicked.
            Once the user's option is clicked, the FacebookPostStory Activity starts in the type
            needed for what the user clicked. The TYPE1 will start the activity in such a way
            that will only allow text in the post, while TYPE2 will also allow the addition of an image.
             */
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(FacebookPostType.this,FacebookPostStory.class);
                    intent1.putExtra("type", TYPE1);
                    Log.d(TAG, "FacebookPostType --> onClick: " + TYPE1 );
                    startActivity(intent1);
                }
            });

            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(FacebookPostType.this,FacebookPostStory.class);
                    intent1.putExtra("type", TYPE2);
                    Log.d(TAG, "FacebookPostType --> onClick: " + TYPE2 );
                    startActivity(intent1);
                }
            });
        }
    }
}