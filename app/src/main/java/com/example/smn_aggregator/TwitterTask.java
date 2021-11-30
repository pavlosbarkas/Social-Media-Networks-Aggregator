package com.example.smn_aggregator;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.StatusUpdate;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class TwitterTask extends AsyncTask<String, Void, Void> {
    private String type;
    private String text;
    private File file;
    private Query query;
    private long id;

    public static final String TYPE1 = "text";
    public static final String TYPE2 = "photo";
    public static final String TYPE3 = "trends";
    public static final String TYPE4 = "searchPosts";
    public static final String TYPE5 = "searchReplies";
    public static final String TYPE6 = "likeTweet";
    public static final String TYPE7 = "unlikeTweet";
    public static final String TAG = "SMN_Aggregator_App_Debug";

    /*
    The getTrends method of twitter4j needs a WOEID(Where On Earth Identifier) in order
    to return the trending hashtags in a specific area. The woeid 23424977 is for the
    United States
     */
    public static final int WOEID = 23424977;

    public static final String consumer_key = BuildConfig.twitterConsumerKey;
    public static final String consumer_secret_key= BuildConfig.twitterConsumerSecret;
    public static final String access_token = BuildConfig.twitterAccessToken;
    public static final String access_token_secret = BuildConfig.twitterAccessTokenSecret;

    private Context context;

    public TwitterTask(String type, Context context){
        this.type = type;
        this.context = context;
    }

    public TwitterTask(String type, Context context, Query query){
        this.type = type;
        this.context = context;
        this.query = query;
    }

    public TwitterTask(String  PostType,String tweetText) {
        type = PostType;
        text = tweetText;
    }

    public TwitterTask(String PostType,String tweetText, File f) {
        type = PostType;
        text = tweetText;
        file = f;
    }

    public TwitterTask(String PostType,long tweetId){
        type = PostType;
        id = tweetId;
    }
    
    /*
    Various twitter tasks are used depending on what we need to do. The
    variations are separated from each other using TYPE constants and the
    number which represents the task we want to execute
     */
    @Override
    protected Void doInBackground(String... strings) {
        Twitter twitter = configureTwitter();
        if (type.equals(TYPE1)) {
            try {
                postTextOnlyTweet(twitter);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
        else if (type.equals(TYPE2)) {
            try {
                postImageTweet(twitter);
            } catch (TwitterException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
        else if (type.equals(TYPE3))
            getTwitterTrends(twitter);
        else if (type.equals(TYPE4))
            searchTwitterPosts(twitter);
        else if (type.equals(TYPE5))
            searchReplies(twitter);
        else if (type.equals(TYPE6)) {
            try {
                likeTweet(twitter);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        } else if (type.equals(TYPE7)) {
            try {
                unlikeTweet(twitter);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


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
    After the user has selected to post a tweet with text only, this method
    is executed and posts the tweet
     */
    private void postTextOnlyTweet(Twitter twitter) throws TwitterException {
        twitter.updateStatus(text);
        Log.d(TAG, "TwitterTask --> postTextOnlyTweet: " + text);
    }

    /*
    After the user has selected to post a tweet with text and photo, this method
    is executed and posts the tweet
     */
    private void postImageTweet(Twitter twitter) throws TwitterException, URISyntaxException {
        StatusUpdate status = new StatusUpdate(text);
        status.setMedia(file);
        twitter.updateStatus(status);
        Log.d(TAG, "TwitterTask --> postImageTweet: " + file);
    }


    //This method executes the like
    private void likeTweet(Twitter twitter) throws TwitterException {
        Log.d(TAG, "likeTweet: done");
        twitter.createFavorite(id);
    }

    //This method removes the like
    private void unlikeTweet(Twitter twitter) throws TwitterException {
        Log.d(TAG, "unlikeTweet: done");
        twitter.destroyFavorite(id);
    }

    /*
    This method returns trending hashtags for given location woeid.
    Current woeid 23424977 is for the United States.
     */
    private void getTwitterTrends(Twitter twitter){
        Trends trends;
        try{
            trends = twitter.getPlaceTrends(WOEID );
            Intent intent = new Intent(context, Trendings.class);
            intent.putExtra("trends", trends);
            context.startActivity(intent);
        }catch (TwitterException e){
            e.printStackTrace();
        }
    }

    /*
    After the user clicks on a specific hashtag from the trendings list, this method
    is executed and searches most popular posts made on Twitter that contain
    the word in the hashtag
     */
    private void searchTwitterPosts(Twitter twitter) {

        QueryResult queryResult = null;
        query.resultType(Query.ResultType.popular);
        try {
            queryResult = twitter.search(query);
        } catch (TwitterException twitterException) {
            twitterException.printStackTrace();
        }
        ArrayList<twitter4j.Status> statuses = new ArrayList<>();

        for (twitter4j.Status status: queryResult.getTweets()){
            statuses.add(status);
        }

        StatusesWrapper statusesWrapper = new StatusesWrapper(statuses);
        Intent intent = new Intent(context, SearchTwitterPosts.class);
        intent.putExtra("statuses", statusesWrapper);
        context.startActivity(intent);
    }

    /*
    When the user sees a specific post, he can choose to see all the replies made
    on the post. This method returns some of the replies, 90 at most, because
    if we return too many replies, twitter sets a 5 minute timeout and we
    can't use the API
     */
    private void searchReplies(Twitter twitter){

        ArrayList<twitter4j.Status> finalRepliesList = new ArrayList<>();
        QueryResult queryResult = null;

        try {
            /*
            A counter was added because if we returned all the replies, twitter would give a timeout
            of 5 minutes to our app and then we couldn't get data from the API.
            */
            int i = 0;
            do {
                queryResult = twitter.search(query);
                Log.d(TAG, "FOUND " + queryResult.getTweets().size() + " REPLIES");
                ArrayList<twitter4j.Status> replies = (ArrayList<twitter4j.Status>) queryResult.getTweets();

                for (twitter4j.Status reply : replies) {
                    finalRepliesList.add(reply);
                }
                i++;
            }while((query = queryResult.nextQuery()) != null && i<=5);
        }catch (TwitterException e){
            /*
            In case twitter sets the timeout, the replies that have already been fetched
            are shown to the user. However if the timeout has been set, the app can't interact
            with the Twitter API for 5 minutes
             */
            Log.d(TAG, "ERROR IN GETTING REPLIES");
            StatusesWrapper statusesWrapper = new StatusesWrapper(finalRepliesList);
            Intent intent = new Intent(context, TwitterRepliesActivity.class);
            intent.putExtra("replies", statusesWrapper);
            context.startActivity(intent);
        }


        //The result of replies search is sent to the Activity responsible to show them to the user
        StatusesWrapper statusesWrapper = new StatusesWrapper(finalRepliesList);
        Intent intent = new Intent(context, TwitterRepliesActivity.class);
        intent.putExtra("replies", statusesWrapper);
        context.startActivity(intent);
    }


}
