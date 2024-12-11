package com.example.threadlyv2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList; // Post is your data model class

    // Constructor for the adapter
    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item_post.xml layout for each item
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        // Get the post for the current position
        Post post = postList.get(position);

        // Bind the post data to the views
        holder.username.setText(post.getUsername());
        holder.postTitle.setText(post.getPostTitle());
        holder.postBody.setText(post.getPostBody());
        holder.handle.setText("@" + post.getUsername().toLowerCase());

        // Set likes and comments (if available in your Post model)
        holder.likes.setText(String.valueOf(post.getLikes()));
        holder.comments.setText(String.valueOf(post.getComments()));

        // Placeholder image for profile picture (update this with real data later)
        holder.profilePicture.setImageResource(R.drawable.ic_default_pfp);
    }

    @Override
    public int getItemCount() {
        return postList.size(); // Number of posts
    }

    // ViewHolder class to hold references to the views in each item layout
    public static class PostViewHolder extends RecyclerView.ViewHolder {

        TextView username, handle, postTitle, postBody, likes, comments;
        ImageView profilePicture;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find views from the item_post.xml layout
            username = itemView.findViewById(R.id.textUsername);
            handle = itemView.findViewById(R.id.textHandle);
            postTitle = itemView.findViewById(R.id.textPostTitle);
            postBody = itemView.findViewById(R.id.textPostBody);
            likes = itemView.findViewById(R.id.textLikes);
            comments = itemView.findViewById(R.id.textComments);
            profilePicture = itemView.findViewById(R.id.imageProfilePicture);
        }
    }
}
