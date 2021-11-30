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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.IOException;

public class FacebookPostStory extends AppCompatActivity {

    //UI components that will be used in TYPE1 (text post only is selected)
    private static String quote;
    private Button btnTextPost;
    private  EditText txtInput;

    //Extra UI components that will be used in TYPE2 (post with both text and photo)
    private EditText hashtag;
    private static String strHashtag;
    private Button btnSelectImage;
    private Button btnPhotoPost;
    private ImageView img;
    private static Uri imageUri;
    private Bitmap selectedImage;
    private boolean emptyHashtag;

    private ShareDialog shareDialog;
    private String type;
    public static final String POST_QUOTE = "post_quote";
    public static final String IMAGE_URI = "image_uri";
    public static final String CAPTION = "caption";
    public static final int REQUEST_CODE = 1;
    public static final String TYPE1 = "text";
    public static final String TYPE2 = "photo";
    public static final String TAG = "SMN_Aggregator_App_Debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            type = intent.getStringExtra("type");
            shareDialog = new ShareDialog(FacebookPostStory.this);

            if (type.equals(TYPE1)) {
                setContentView(R.layout.facebook_text);
                btnTextPost = findViewById(R.id.btnTextPost);
                txtInput = findViewById(R.id.txtTextInput);
                checkTextInput();

                btnTextPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tempQuote = txtInput.getText().toString();
                        if (quote!=null) {
                            if (quote.equals(tempQuote))
                                postQuoteToFacebook();
                            else if (!tempQuote.equals("") && !tempQuote.equals(quote)){
                                quote = tempQuote;
                                postQuoteToFacebook();
                            }
                            else
                                Toast.makeText(FacebookPostStory.this, "You have to enter a quote!", Toast.LENGTH_LONG).show();
                        }
                        else {
                            quote = txtInput.getText().toString();
                            if (!quote.equals(""))
                                postQuoteToFacebook();
                            else
                                Toast.makeText(FacebookPostStory.this, "You have to enter a quote!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            else if (type.equals(TYPE2)) {
                setContentView(R.layout.facebook_photo);
                emptyHashtag = true;
                btnSelectImage = findViewById(R.id.btnSelectImage);
                btnPhotoPost = findViewById(R.id.btnPhotoPost);
                img = (ImageView)findViewById(R.id.FacebookImageView);
                hashtag = findViewById(R.id.txtHashtagInput);
                imageUri = null;
                checkSelectedPhoto();

                btnSelectImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "FacebookPostStory --> onClick: going to gallery");
                        openGallery();
                    }
                });

                btnPhotoPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkHashtag();
                        if (imageUri!=null) {
                            Log.d(TAG, "FacebookPostStory --> onClick: post accepted ");
                            try {
                                postPhotoToFacebook();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else
                            Toast.makeText(FacebookPostStory.this, "You have to select an image first!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    //This method checks the format of the given hashtag
    //Replaces spaces with _
    //Adds '#' if it's missing
    private void checkHashtag() {
        strHashtag = hashtag.getText().toString();
        if (!strHashtag.equals("")){
            String temp = strHashtag.replaceAll("\\s+","_");
            if (temp.charAt(0) != '#')
                temp = '#' + temp;
            strHashtag = temp;
            emptyHashtag = false;
            Log.d(TAG, "FacebookPostStory --> Hashtag: " + strHashtag);
        }
    }


    //This method checks if the text field from another social media has been filled
    //In this case, it checks only Twitter because Instagram does not provide only text post
    private void checkTextInput(){
        String tempTwitter = TwitterPostStory.getTxt();
        if (tempTwitter!=null){
            if (!tempTwitter.equals("")) {
                quote = tempTwitter;
                txtInput.setText(quote);
                Log.d(TAG, "checkTextInput: " + quote);
            }
        }
    }


    //This method checks if a photo has already been selected from another social media
    private void checkSelectedPhoto(){
        Uri tempInstagram = InstagramPostStory.getImageUri();
        Uri tempTwitter = TwitterPostStory.getImageUri();
        Log.d(TAG, "checkSelectedPhoto: temp facebook uri " + tempInstagram);
        Log.d(TAG, "checkSelectedPhoto: temp twitter uri " + tempTwitter);
        if (tempInstagram!=null) {
            img.setImageURI(tempInstagram);
            imageUri = tempInstagram;
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (tempTwitter!=null) {
            img.setImageURI(tempTwitter);
            imageUri = tempTwitter;
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //This method redirects the user to Gallery
    private void openGallery() {
        Log.d(TAG, "FacebookPostStory --> openGallery: in gallery");
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, REQUEST_CODE);
    }


    //This method posts the quote to Facebook
    //The url is always set because Facebook does not allow only text sharing through their API
    private void postQuoteToFacebook() {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            Log.d(TAG, "postQuoteOnFacebook: posting quote -->" + quote);
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("https://github.com/UomMobileDevelopment"))
                    .setQuote(quote)
                    .build();
            shareDialog.show(linkContent);
        }
        else
            Log.d(TAG, "postToFacebook: error with quote");
    }


    //This method posts the photo to Facebook
    //.setCaption method is deprecated
    //To maintain the functionality of photo with caption, the hashtag option is available
    private void postPhotoToFacebook() throws IOException {
        selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        if (!emptyHashtag){
            Log.d(TAG, "FacebookPostStory --> onCreate: posting photo with hashtag");
            if (shareDialog.canShow(SharePhotoContent.class)) {
                SharePhoto photo = new SharePhoto.Builder()
                        .setBitmap(selectedImage)
                        .build();
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .setShareHashtag(new ShareHashtag.Builder()
                                .setHashtag(strHashtag).build())
                        .build();
                shareDialog.show(content, ShareDialog.Mode.WEB);
            }
            else
                Log.d(TAG, "postToFacebook: error with hashtag");
        }
        else if (emptyHashtag){
            Log.d(TAG, "FacebookPostStory --> onCreate: posting photo without hashtag");
            if (shareDialog.canShow(SharePhotoContent.class)) {
                SharePhoto photo = new SharePhoto.Builder()
                        .setBitmap(selectedImage)
                        .build();
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                shareDialog.show(content, ShareDialog.Mode.WEB);
            }
            else
                Log.d(TAG, "postToFacebook: error without hashtag");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            Log.d(TAG, "FacebookPostStory --> onActivityResult: photo has been chosen");
            imageUri = data.getData();
            img.setImageURI(imageUri);
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            Log.d(TAG, "FacebookPostStory --> onActivityResult: post upload done");
            Intent i = new Intent(FacebookPostStory.this, PostActivity.class);
            startActivity(i);
        }
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (type.equals(TYPE1))
            outState.putString(POST_QUOTE, quote);
        else if (type.equals(TYPE2)) {
            if (imageUri != null)
                outState.putString(IMAGE_URI, String.valueOf(imageUri));
            String tempHashtag = hashtag.getText().toString();
            if (tempHashtag != null)
                outState.putString(CAPTION, tempHashtag);
        }
    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (type.equals(TYPE1)){
            String tempQuote = savedInstanceState.getString(POST_QUOTE);
            if (tempQuote!=null)
                txtInput.setText(tempQuote);
        }
        else if (type.equals(TYPE2)){
            String tempUri = savedInstanceState.getString(IMAGE_URI);
            if (tempUri!=null){
                Uri uri = Uri.parse(tempUri);
                img.setImageURI(uri);
            }

            String tempHashtag = savedInstanceState.getString(CAPTION);
            if (tempHashtag!=null)
                hashtag.setText(tempHashtag);
        }
    }

    public static Uri getImageUri(){ return imageUri; }
    public static String getQuote() { return quote; }
    public static String getHashtag() { return strHashtag; }
}