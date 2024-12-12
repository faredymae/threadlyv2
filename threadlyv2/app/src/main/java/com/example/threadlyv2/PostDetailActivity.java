package com.example.threadlyv2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class PostDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_post_detail); // Use a dedicated layout for this activity

        // Retrieve data from Intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("postTitle");
        String body = intent.getStringExtra("postBody");
        String username = intent.getStringExtra("postUsername");
        String fullname = intent.getStringExtra("postFullname");
        String createdAt = intent.getStringExtra("postCreatedAt");
        int likeCount = intent.getIntExtra("likeCount", 0);
        int commentCount = intent.getIntExtra("commentCount", 0);
        String profilePictureUri = intent.getStringExtra("profilePictureUri");

        // Initialize views
        TextView titleTextView = findViewById(R.id.post_titleDP);
        TextView bodyTextView = findViewById(R.id.post_bodyDP);
        TextView usernameTextView = findViewById(R.id.usernamePD);
        TextView fullnameTextView = findViewById(R.id.fullnamePD);
        TextView createdAtTextView = findViewById(R.id.created_atPD);
        TextView likeCountTextView = findViewById(R.id.like_countPD);
        TextView commentCountTextView = findViewById(R.id.comment_countPD);
        ImageView profilePictureImageView = findViewById(R.id.imageViewPFPPD);

        // Populate views
        titleTextView.setText(title != null ? title : "No Title");
        bodyTextView.setText(body != null ? body : "No Content");
        usernameTextView.setText(username != null ? username : "@unknown");
        fullnameTextView.setText(fullname != null ? fullname : "Unknown User");
        createdAtTextView.setText(createdAt != null ? createdAt : "Unknown Date");
        likeCountTextView.setText(String.valueOf(likeCount));
        commentCountTextView.setText(String.valueOf(commentCount));

        // Load profile picture
        if (profilePictureUri != null && !profilePictureUri.isEmpty()) {
            Glide.with(this)
                    .load(profilePictureUri)
                    .circleCrop()
                    .placeholder(R.drawable.ic_default_pfp)
                    .into(profilePictureImageView);
        } else {
            profilePictureImageView.setImageResource(R.drawable.ic_default_pfp);
        }
    }
}
