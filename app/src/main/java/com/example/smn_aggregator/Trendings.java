package com.example.smn_aggregator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import java.util.ArrayList;
import twitter4j.Query;
import twitter4j.Trend;
import twitter4j.Trends;

public class Trendings extends AppCompatActivity {

    public static final String TAG = "SMN_Aggregator_App_Debug";

    private Trends trends;
    private ArrayList<Trend> trendingHashtags = new ArrayList<>();
    public static final String TYPE4 = "searchPosts";

    //A recyclerView is used in order to create a list of all the hashtags returned from the search
    private RecyclerView recyclerView;
    private TrendingsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trendings);

        Intent intent = getIntent();
        trends = (Trends)intent.getSerializableExtra("trends");

        Log.d(TAG, "GOT TRENDS FROM INTENT");

        for (Trend trend: trends.getTrends()){
            trendingHashtags.add(trend);
        }

        Log.d(TAG, "WE GOT HASHTAGS");
        Log.d(TAG, "SIZE = " + trendingHashtags.size());

        recyclerView = findViewById(R.id.trendingsRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new TrendingsAdapter(trendingHashtags);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        /*
        Each item of the recyclerView has an onClickListener. When the user clicks
        a specific hashtag, then he is shown posts containing it
         */
        adapter.setOnHashtagClickListener(new TrendingsAdapter.onHashtagClickListener() {
            @Override
            public void onHashtagClick(int position) {
                Log.d(TAG, "WILL SEND THE TREND " + trendingHashtags.get(position).getName() + " TO BE SEARCHED");

                //The text of the hashtag clicked will be the query of the twitter posts search method in TwitterTask
                Query query = new Query(trendingHashtags.get(position).getName());

                TwitterTask twitterTask = new TwitterTask(TYPE4, Trendings.this, query);
                twitterTask.execute();
            }
        });
    }

    /*
    This method provides a searchView to the user so that he can search among
    the hashtags that have already been returned from twitter. We didn't find
    a way to search live on twitter's hashtags using its API, so we did this
    for a search functionality. The search is activated by pressing the icon on
    the top right corner of the menu. In order the search text that the user
    has already added to remain the same on a rotation of the phone, a configChanges
    has been added to the manifest of this activity and it isn't recreated when
    changing phone orientation. The search function works as the user types the query
    and returns all the results having the query as a substring.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.trendings_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.trendings_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            //Everytime the user makes a change in the search text, a new  search is made
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
}