package com.example.threadlyv2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList;

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
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Post post = postList.get(position);

        // Load profile picture using Glide
        String profileImageUri = post.getProfileImageUri();
        if (profileImageUri != null && !profileImageUri.isEmpty()) {
            Glide.with(context)
                    .load(profileImageUri)
                    .circleCrop() // Display profile image as a circle
                    .placeholder(R.drawable.ic_default_pfp) // Placeholder in case of error or missing image
                    .into(holder.profilePicture);
        } else {
            // If no image URI, show the default placeholder
            holder.profilePicture.setImageResource(R.drawable.ic_default_pfp);
        }

        // Other post bindings (e.g., title, body)
        holder.username.setText(post.getUsername());
        holder.postTitle.setText(post.getPostTitle());
        holder.postBody.setText(post.getPostBody());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    // ViewHolder class to hold references to the views in each item layout
    public static class PostViewHolder extends RecyclerView.ViewHolder {

        TextView username, handle, postTitle, postBody;
        ImageView profilePicture;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find views from the item_post.xml layout
            username = itemView.findViewById(R.id.textUsername);
            handle = itemView.findViewById(R.id.textHandle);
            postTitle = itemView.findViewById(R.id.textPostTitle);
            postBody = itemView.findViewById(R.id.textPostBody);
            profilePicture = itemView.findViewById(R.id.imageProfilePicture);
        }
    }

    public void updatePostList(List<Post> newPostList) {
        this.postList = newPostList;
        notifyDataSetChanged();
    }
}
