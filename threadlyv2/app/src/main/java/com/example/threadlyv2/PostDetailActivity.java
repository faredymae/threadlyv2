package com.example.threadlyv2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {
    private int currentUserId; // To store the current user's ID
    private DatabaseHelper databaseHelper;
    private RecyclerView commentsRecyclerView;
    private CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_post_detail); // Use a dedicated layout for this activity

        // Retrieve currentUserId from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("currentUserId", -1);
        if (currentUserId == -1) {
            // Handle the case where user ID is not found
            Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity to prevent further actions
            return;
        }

        databaseHelper = new DatabaseHelper(this);

        // Retrieve data from Intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("postTitle");
        String body = intent.getStringExtra("postBody");
        String username = intent.getStringExtra("username");
        String fullname = intent.getStringExtra("fullname");
        String createdAt = intent.getStringExtra("createdAt");
        int likeCount = intent.getIntExtra("likeCount", 0);
        int commentCount = intent.getIntExtra("commentCount", 0);
        String profilePictureUri = intent.getStringExtra("profileImageUri");
        int postId = intent.getIntExtra("postId", -1);

        // Initialize views
        TextView titleTextView = findViewById(R.id.post_titleDP);
        TextView bodyTextView = findViewById(R.id.post_bodyDP);
        TextView usernameTextView = findViewById(R.id.usernamePD);
        TextView fullnameTextView = findViewById(R.id.fullnamePD);
        TextView createdAtTextView = findViewById(R.id.created_atPD);
        TextView likeCountTextView = findViewById(R.id.like_countPD);
        TextView commentCountTextView = findViewById(R.id.comment_countPD);
        ImageView profilePictureImageView = findViewById(R.id.imageViewPFPPD);
        TextView postCommentButton = findViewById(R.id.post_commentBtn);
        EditText commentInput = findViewById(R.id.comment_input);
        commentsRecyclerView = findViewById(R.id.recycleViewComment);

        // Set up the RecyclerView
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(this, new ArrayList<>()); // Initialize adapter with an empty list
        commentsRecyclerView.setAdapter(commentAdapter);


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

        // Load comments when the activity starts
        if (postId != -1) {
            loadComments(postId);
        } else {
            Toast.makeText(this, "Error: Invalid Post ID", Toast.LENGTH_SHORT).show();
        }

        // Handle comment posting
        postCommentButton.setOnClickListener(v -> {
            String commentText = commentInput.getText().toString().trim();

            if (commentText.isEmpty()) {
                Toast.makeText(this, "Comment cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            String createdAtComment = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            Comment comment = new Comment(postId, currentUserId, commentText, createdAtComment, false);

            long result = databaseHelper.insertComment(comment);
            if (result != -1) {
                commentInput.setText("");
                loadComments(postId);
                Toast.makeText(this, "Comment posted successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to post comment. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadComments(int postId) {
        // Fetch comments for the given post ID
        List<Comment> comments = databaseHelper.getCommentsForPost(postId);

        // Update the RecyclerView adapter with the new data
        commentAdapter.setComments(comments);
    }
}
