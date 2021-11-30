package com.example.smn_aggregator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import twitter4j.Status;

/*
This is the adapter handling the data of the SearchTwitterPosts Activity. All posts returned from twitter
are sent here in order to be shown to the user using a recycleView. The recycleView will also offer an onClick
function on every item so that the user can see the post in more detail.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private ArrayList<Status> statuses;
    private onPostClickListener postClickListener;

    public interface onPostClickListener{
        void onPostClick(int position);
    }

    public void setOnPostClickListener(onPostClickListener listener){
        this.postClickListener = listener;
    }

    public  static  class PostViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        public TextView userTextView;
        public TextView textView;

        public PostViewHolder(@NonNull View itemView, onPostClickListener listener) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            userTextView = itemView.findViewById(R.id.userNameView);
            textView = itemView.findViewById(R.id.textView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onPostClick(position);
                        }
                    }
                }
            });
        }
    }

    public PostAdapter(ArrayList<Status> statuses) {
        this.statuses = statuses;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        PostViewHolder postViewHolder = new PostViewHolder(v, postClickListener);
        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Status currentPost = statuses.get(position);

        holder.imageView.setImageResource(R.drawable.twitter_logo);
        holder.userTextView.setText(currentPost.getUser().getScreenName());
        holder.textView.setText(currentPost.getText());
    }

    @Override
    public int getItemCount() {
        return statuses.size();
    }
}
