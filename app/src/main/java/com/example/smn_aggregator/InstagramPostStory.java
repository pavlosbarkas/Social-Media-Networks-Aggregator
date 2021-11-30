package com.example.smn_aggregator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class InstagramPostStory extends AppCompatActivity {

    //UI components
    private Button btnSelectImage;
    private Button btnPost;
    private ImageView img;
    private static Uri imageUri;

    //Used to signal the return from Instagram
    private boolean flag = false;

    public static final int REQUEST_CODE = 1;
    public static final String IMAGE_URI = "image_uri";
    public static final String TAG = "SMN_Aggregator_App_Debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram_post_story);

        Intent intent = getIntent();
        if (intent!=null){
            btnSelectImage = findViewById(R.id.btnSelectImage);
            btnPost = findViewById(R.id.btnInstagramExecutePost);
            img = (ImageView)findViewById(R.id.InstagramImageView);
            checkSelectedPhoto();

            btnSelectImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "InstagramPostStory --> onClick: going to gallery");
                    openGallery();
                }
            });

            btnPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imageUri!=null) {
                        Log.d(TAG, "InstagramPostStory --> onClick: post action accepted " );
                        postToInstagram();
                    }
                    else
                        Toast.makeText(InstagramPostStory.this, "You have to select an image first!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    /*
    This method checks if a photo has already been selected from another social media.
    If yes it is set as preselected and for this social media.
     */
    private void checkSelectedPhoto(){
        Uri tempFacebook = FacebookPostStory.getImageUri();
        Uri tempTwitter = TwitterPostStory.getImageUri();
        Log.d(TAG, "checkSelectedPhoto: temp facebook uri " + tempFacebook);
        Log.d(TAG, "checkSelectedPhoto: temp twitter uri " + tempTwitter);
        if (tempFacebook!=null) {
            img.setImageURI(tempFacebook);
            imageUri = tempFacebook;
        }
        else if (tempTwitter!=null) {
            img.setImageURI(tempTwitter);
            imageUri = tempTwitter;
        }
    }

    //This method redirects the user to Instagram's application
    private void postToInstagram() {
        Log.d(TAG, "InstagramPostStory --> onCreate: posting ");
        String type = "image/*";
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType(type);
        flag = true;
        share.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(share, "Share to"));
    }

    //This method redirects the user to Gallery
    private void openGallery() {
        Log.d(TAG, "InstagramPostStory --> openGallery: in gallery");
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, REQUEST_CODE);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d(TAG, "onPostResume: here + flag = " + flag);
        if (flag){
            Intent i = new Intent(InstagramPostStory.this, PostActivity.class);
            startActivity(i);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE){
            Log.d(TAG, "InstagramPostStory --> onActivityResult: chosen photo");
            imageUri = data.getData();
            img.setImageURI(imageUri);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (imageUri != null)
            outState.putString(IMAGE_URI, String.valueOf(imageUri));
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String tempUri = savedInstanceState.getString(IMAGE_URI);
        if (tempUri!=null) {
            Uri uri = Uri.parse(tempUri);
            img.setImageURI(uri);
        }
    }

    public static Uri getImageUri(){ return imageUri; }
}