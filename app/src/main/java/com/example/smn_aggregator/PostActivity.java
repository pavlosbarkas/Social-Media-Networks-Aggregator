package com.example.smn_aggregator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PostActivity extends AppCompatActivity {

    private Button btnPostFacebook;
    private Button btnPostInstagram;
    private Button btnPostTwitter;
    private PackageManager pm;

    public static final String TAG = "SMN_Aggregator_App_Debug";
    public static final String packageName = "com.instagram.android";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        
        Intent intent = getIntent();
        if (intent!=null){
            pm = getPackageManager();
            btnPostFacebook = findViewById(R.id.btnPostFacebook);
            btnPostInstagram = findViewById(R.id.btnPostInstagram);
            btnPostTwitter = findViewById(R.id.btnPostTwitter);

            /*
            When the user clicks on each of the buttons above, he is redirected to the
            activity that handles the post functionality in each one of the social media.
             */
            btnPostFacebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "PostActivity --> onClick: Facebook post");
                    Intent intent1 = new Intent(PostActivity.this, FacebookPostType.class);
                    startActivity(intent1);
                }
            });

            btnPostInstagram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isPackageInstalled(packageName,pm)){
                        Log.d(TAG, "PostActivity --> onClick: Instagram post ");
                        Intent intent2 = new Intent(PostActivity.this, InstagramPostStory.class);
                        startActivity(intent2);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Instagram must be installed on the device!", Toast.LENGTH_LONG).show();
                    }
                }
            });

            btnPostTwitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "PostActivity --> onClick: Twitter post");
                    Intent intent1 = new Intent(PostActivity.this, TwitterPostType.class);
                    startActivity(intent1);
                }
            });
        }
    }

    /*
    This method checks if Instagram's application is installed in the phone.
    Instagram's API doesn't allow posting a photo without going through
    Facebook App Review. So, when the user wants to post something on Instagram,
    he is redirected to the Instagram application or if it isn't installed, a toast
    appears saying that Instagram must be installed.
     */
    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed: pressed");
        Intent intent = new Intent(PostActivity.this, FunctionsActivity.class);
        startActivity(intent);
    }
}