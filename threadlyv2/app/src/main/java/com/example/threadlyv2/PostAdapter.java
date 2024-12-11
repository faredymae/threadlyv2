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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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

        // Bind other post details
        holder.username.setText(post.getUsername());
        holder.postTitle.setText(post.getPostTitle());
        holder.postBody.setText(post.getPostBody());

        // Format and set the created_at timestamp
        if (post.getCreatedAt() != null && !post.getCreatedAt().isEmpty()) {
            String formattedDate = formatTimestamp(post.getCreatedAt());
            holder.postCreatedAt.setText(formattedDate);
        } else {
            holder.postCreatedAt.setText("N/A"); // Fallback if created_at is missing
        }
    }

    // timezone converted
    public String formatTimestamp(String createdAt) {
        try {
            // Parse the 'createdAt' timestamp into a Date object
            SimpleDateFormat databaseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            databaseFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Ensure parsing is in UTC timezone
            Date postDate = databaseFormat.parse(createdAt);

            // Get the current time and local timezone
            long currentTime = System.currentTimeMillis();

            // Calculate the difference in milliseconds
            long timeDifference = currentTime - postDate.getTime();

            // Convert the difference into seconds, minutes, hours, etc.
            long seconds = timeDifference / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            // Return a formatted string depending on the time difference
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
            return createdAt; // Return raw timestamp in case of error
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    // ViewHolder class to hold references to the views in each item layout
    public static class PostViewHolder extends RecyclerView.ViewHolder {

        TextView username, handle, postTitle, postBody, postCreatedAt;
        ImageView profilePicture;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find views from the item_post.xml layout
            username = itemView.findViewById(R.id.textUsername);
            handle = itemView.findViewById(R.id.textHandle);
            postTitle = itemView.findViewById(R.id.textPostTitle);
            postBody = itemView.findViewById(R.id.textPostBody);
            profilePicture = itemView.findViewById(R.id.imageProfilePicture);
            postCreatedAt = itemView.findViewById(R.id.textCreatedAt); // Make sure this ID matches your layout

        }
    }

    public void updatePostList(List<Post> newPostList) {
        this.postList = newPostList;
        notifyDataSetChanged();
    }
}
