package com.example.threadlyv2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList;
    private DatabaseHelper databaseHelper;
    private int currentUserId;  // To store the current user's ID

    // Constructor for the adapter
    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
        this.databaseHelper = new DatabaseHelper(context);
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
        boolean isLiked = isPostLiked(post.getPostID());  // Use SharedPreferences
        post.setLikedByUser(isLiked);  // Update the post object to reflect the like state

        // Load profile picture using Glide
        String profileImageUri = post.getProfileImageUri();
        if (profileImageUri != null && !profileImageUri.isEmpty()) {
            Glide.with(context)
                    .load(profileImageUri)
                    .circleCrop() // Display profile image as a circle
                    .placeholder(R.drawable.ic_default_pfp) // Placeholder in case of error or missing image
                    .into(holder.profilePicture);
        } else {
            holder.profilePicture.setImageResource(R.drawable.ic_default_pfp);
        }


        // Bind other post details
        holder.fullname.setText(post.getFullname());
        holder.username.setText(post.getUsername());
        holder.postTitle.setText(post.getPostTitle());
        holder.postBody.setText(post.getPostBody());
        holder.textLikeCount.setText(String.valueOf(post.getLikeCount()));

        // Set like button state
        if (post.isLikedByUser()) {
            holder.likeButton.setImageResource(R.drawable.ic_liked); // Icon when liked
        } else {
            holder.likeButton.setImageResource(R.drawable.ic_like);  // Icon when not liked
        }

        // Format and set the created_at timestamp
        if (post.getCreatedAt() != null && !post.getCreatedAt().isEmpty()) {
            String formattedDate = formatTimestamp(post.getCreatedAt());
            holder.postCreatedAt.setText(formattedDate);
        } else {
            holder.postCreatedAt.setText("N/A"); // Fallback if created_at is missing
        }

        // Handle like button click
        holder.likeButton.setOnClickListener(v -> toggleLike(post, position, holder));  // Add click listener to toggle like

        holder.itemView.setOnClickListener(v -> {
            Log.d("PostAdapter", "Item clicked: " + post.getPostID());

            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("postId", post.getPostID());
            intent.putExtra("fullname", post.getFullname());
            intent.putExtra("username", post.getUsername());
            intent.putExtra("postTitle", post.getPostTitle());
            intent.putExtra("postBody", post.getPostBody());
            intent.putExtra("likeCount", post.getLikeCount());
            intent.putExtra("commentCount", post.getCommentCount());
            intent.putExtra("createdAt", post.getCreatedAt());
            intent.putExtra("profileImageUri", post.getProfileImageUri()); // Pass profile image URI
            context.startActivity(intent);

            Log.d("PostAdapter", "Intent Data: " + post.getPostID() + ", " + post.getPostTitle());

        });


    }

    private void toggleLike(Post post, int position, PostViewHolder holder) {
        int postId = post.getPostID();

        // Toggle like state
        boolean newLikeState = !post.isLikedByUser();
        post.setLikedByUser(newLikeState);

        if (post.isLikedByUser()) {
            // Like the post
            databaseHelper.likePost(postId, currentUserId);
            post.setLikeCount(post.getLikeCount() + 1);
        } else {
            // Unlike the post
            databaseHelper.unlikePost(postId, currentUserId);
            post.setLikeCount(post.getLikeCount() - 1);
        }

        // Update the like count in the UI
        holder.textLikeCount.setText(String.valueOf(post.getLikeCount()));

        // Change the like button icon
        holder.likeButton.setImageResource(post.isLikedByUser() ? R.drawable.ic_liked : R.drawable.ic_like);

        // Save the like state to SharedPreferences
        saveLikeState(postId, post.isLikedByUser());

        // Notify the adapter that the like state has been updated
        notifyItemChanged(position);
    }

    // Save the like state to SharedPreferences
    private void saveLikeState(int postId, boolean isLiked) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LikePreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("post_" + postId, isLiked);  // Storing the like state for each post by its ID
        editor.apply();
    }

    // Check if the post is liked from SharedPreferences
    private boolean isPostLiked(int postId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LikePreferences", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("post_" + postId, false);  // Default is false
    }

    // Format the timestamp
    public String formatTimestamp(String createdAt) {
        try {
            SimpleDateFormat databaseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            databaseFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date postDate = databaseFormat.parse(createdAt);

            long currentTime = System.currentTimeMillis();
            long timeDifference = currentTime - postDate.getTime();

            long seconds = timeDifference / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (seconds < 60) {
                return seconds + "s";
            } else if (minutes < 60) {
                return minutes + "m";
            } else if (hours < 24) {
                return hours + "h";
            } else {
                return days + "d";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return createdAt;
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        TextView username, fullname, postTitle, postBody, postCreatedAt, textLikeCount;
        ImageView profilePicture, likeButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            fullname = itemView.findViewById(R.id.textUsername);
            username = itemView.findViewById(R.id.textHandle);
            postTitle = itemView.findViewById(R.id.textPostTitle);
            postBody = itemView.findViewById(R.id.textPostBody);
            profilePicture = itemView.findViewById(R.id.imageProfilePicture);
            postCreatedAt = itemView.findViewById(R.id.textCreatedAt);
            likeButton = itemView.findViewById(R.id.likeButton);
            textLikeCount = itemView.findViewById(R.id.textLikeCount);
        }
    }

    public void updatePostList(List<Post> newPostList) {
        this.postList.clear();
        this.postList.addAll(newPostList);
        notifyDataSetChanged();
    }

}

