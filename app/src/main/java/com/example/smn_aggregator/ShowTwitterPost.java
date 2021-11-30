package com.example.smn_aggregator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

import twitter4j.Query;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class ShowTwitterPost extends AppCompatActivity {

    public static final String TAG = "SMN_Aggregator_App_Debug";
    public static final String TYPE5 = "searchReplies";
    public static final String TYPE6 = "likeTweet";
    public static final String TYPE7 = "unlikeTweet";

    private boolean liked = false;
    private Button likeButton;

    //The post that was clicked from the user is saved here
    private Status currentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_twitter_post);

        Intent intent = getIntent();
        currentStatus = (Status) intent.getSerializableExtra("status");
        Log.d(TAG, currentStatus.getUser().getScreenName() + " ARRIVED TO SHOW!");

        Long id = currentStatus.getId();
        StatusOfTweet s = new StatusOfTweet(id);
        s.execute();

        /*
        The status received from SearchTwitterPost is shown in detail here.
        An AsyncTask is used for the user's profile image in order the application not to be delayed.
         */
        new DownloadProfileImageTask((ImageView) findViewById(R.id.profileImageView)).execute(currentStatus.getUser().get400x400ProfileImageURLHttps());
        TextView screenNameText = findViewById(R.id.screenNameText);
        TextView userNameText = findViewById(R.id.usernameText);
        TextView statusText = findViewById(R.id.statusText);
        TextView createdAtText = findViewById(R.id.createdAtText);
        likeButton = findViewById(R.id.likeButton);
        TextView favoritesCountText = findViewById(R.id.favoritesCountText);
        TextView retweetsCountText = findViewById(R.id.retweetsCountText);

        screenNameText.setText(currentStatus.getUser().getScreenName());
        userNameText.setText("@" + currentStatus.getUser().getName());
        statusText.setText(currentStatus.getText());
        createdAtText.setText(currentStatus.getCreatedAt().toString());
        likeButton.setBackgroundResource(R.drawable.twitterblackheart);
        favoritesCountText.setText(String.valueOf(currentStatus.getFavoriteCount()) + " Likes");
        retweetsCountText.setText(String.valueOf(currentStatus.getRetweetCount()) + " Retweets");

        Button viewRepliesButton = findViewById(R.id.viewRepliesButton);

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!liked) {
                    Toast.makeText(ShowTwitterPost.this, "You liked the tweet!", Toast.LENGTH_SHORT).show();
                    liked = true;
                    likeButton.setBackgroundResource(R.drawable.twitterheartred);
                    TwitterTask twitterTask = new TwitterTask(TYPE6, id);
                    twitterTask.execute();
                }
                else
                {
                    Toast.makeText(ShowTwitterPost.this, "You removed the like from the tweet!", Toast.LENGTH_SHORT).show();
                    likeButton.setBackgroundResource(R.drawable.twitterblackheart);
                    liked = false;
                    TwitterTask twitterTask = new TwitterTask(TYPE7, id);
                    twitterTask.execute();
                }
            }
        });

        /*
        When the seeReplies button is clicked a new twitter task is executed that searches
        all the replies given to this specific post. Then the user is redirected to
        TwitterRepliesActivity where all the replies are shown.
         */
        viewRepliesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query query = new Query(currentStatus.getUser().getScreenName());
                query.setSinceId(currentStatus.getId());

                Toast.makeText(ShowTwitterPost.this, "Loading replies...", Toast.LENGTH_LONG).show();
                TwitterTask twitterTask = new TwitterTask(TYPE5, ShowTwitterPost.this, query);
                twitterTask.execute();
            }
        });
    }

    /*
    This AsyncTask is responsible for getting the post's author's profile picture
    and setting it as the image Bitmap of the imageView
     */
    private class DownloadProfileImageTask extends AsyncTask<String, Void, Bitmap>{

        private ImageView imageView;

        public DownloadProfileImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            Bitmap bitmap = null;
            try{
                InputStream in = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            }catch(Exception e){
                Log.d(TAG, "ERROR DOWNLOADING IMAGE!", e);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }


    /*
    This AsyncTask is responsible for checking whether the user has already liked the tweet
    and setting the icon for the likeButton accordingly.
    Note: Twitter API only returns the last 20 liked tweets
     */
    private class StatusOfTweet extends AsyncTask<Integer, Void, Boolean> {

        public static final String consumer_key = BuildConfig.twitterConsumerKey;
        public static final String consumer_secret_key= BuildConfig.twitterConsumerSecret;
        public static final String access_token = BuildConfig.twitterAccessToken;
        public static final String access_token_secret = BuildConfig.twitterAccessTokenSecret;

        private long id;

        public StatusOfTweet(Long id) { this.id = id; }

        //This method configures the authentication in order to be able to use the Twitter API
        private Twitter configureTwitter(){
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey(consumer_key)
                    .setOAuthConsumerSecret(consumer_secret_key)
                    .setOAuthAccessToken(access_token)
                    .setOAuthAccessTokenSecret(access_token_secret);
            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter twitter = tf.getInstance();
            return twitter;
        }

        /*
        This method searches the ids of the last 20 liked tweets to see if any of them match
        the id of the current tweet.
        */
        private boolean checkLike(Twitter twitter) throws TwitterException {
            ResponseList<twitter4j.Status> liked = twitter.getFavorites();
            for (twitter4j.Status st: liked) {
                if (st.getId() == id) {
                    Log.d(TAG, "checkLike: " + st.getUser().getName());
                    return true;
                }
            }
            return false;
        }


        @Override
        protected Boolean doInBackground(Integer... integers) {
            try {
                return checkLike(configureTwitter());
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                Log.d(TAG, "onPostExecute: true");
                likeButton.setBackgroundResource(R.drawable.twitterheartred);
                liked = true;
            }
            else if (!aBoolean) {
                Log.d(TAG, "onPostExecute: false");
                likeButton.setBackgroundResource(R.drawable.twitterblackheart);
                liked = false;
            }
        }
    }


}