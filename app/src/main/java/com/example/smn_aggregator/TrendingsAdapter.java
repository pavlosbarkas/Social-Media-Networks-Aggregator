package com.example.smn_aggregator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Trend;

/*
This is the adapter handling the data of the Trendings Activity. All hashtags returned from twitter
are sent here in order to be shown to the user using a recycleView. Every item of the recycleView
has an onClickListener so when the user clicks on it, he can see posts containing it. Also this
class implements Filterable in order the user to be able to search between the returned hashtags
and find what he wants.
 */
public class TrendingsAdapter extends RecyclerView.Adapter<TrendingsAdapter.TrendingsViewHolder> implements Filterable {

    private ArrayList<Trend> trendingHashtags;
    private ArrayList<Trend> allTrendingHashtags;
    private onHashtagClickListener hashtagClickListener;

    public interface onHashtagClickListener{
        void onHashtagClick(int position);
    }

    public void setOnHashtagClickListener(onHashtagClickListener listener){
        this.hashtagClickListener = listener;
    }

    public static  class TrendingsViewHolder extends RecyclerView.ViewHolder{

        public TextView hashtagText;

        public TrendingsViewHolder(@NonNull View itemView, onHashtagClickListener listener){
            super(itemView);

            hashtagText = itemView.findViewById(R.id.hashtagTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onHashtagClick(position);
                        }
                    }
                }
            });

        }

    }

    public TrendingsAdapter(ArrayList<Trend> trendingHashtags){
        this.trendingHashtags = trendingHashtags;
        allTrendingHashtags = new ArrayList<>(trendingHashtags);
    }

    @NonNull
    @Override
    public TrendingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trending_item, parent, false);
        TrendingsViewHolder trendingsViewHolder = new TrendingsViewHolder(v, hashtagClickListener);
        return trendingsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrendingsViewHolder holder, int position) {

        Trend currentTrend = trendingHashtags.get(position);

        holder.hashtagText.setText(currentTrend.getName());

    }

    @Override
    public int getItemCount() {
        return trendingHashtags.size();
    }

    @Override
    public Filter getFilter() {
        return hashtagFilter;
    }

    //The hashtags are filtered based on what the user is typing in the SearchView as he is typing it
    private Filter hashtagFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Trend> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length()==0){
                filteredList.addAll(allTrendingHashtags);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Trend trend: allTrendingHashtags){
                    if (trend.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(trend);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            trendingHashtags.clear();
            trendingHashtags.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
