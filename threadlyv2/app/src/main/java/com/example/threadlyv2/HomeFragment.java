package com.example.threadlyv2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Retrieve user session
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        int currentUserId = sharedPreferences.getInt("currentUserId", -1);

        if (currentUserId == -1) {
            Toast.makeText(getActivity(), "User not logged in. Please login.", Toast.LENGTH_SHORT).show();
            // Redirect to login screen
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
            return view;
        }

        // Initialize database helper
        databaseHelper = new DatabaseHelper(getActivity());

        // Set up RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Fetch posts from the database
        List<Post> postList = fetchPostsFromDatabase();

        // Set the adapter
        postAdapter = new PostAdapter(getActivity(), postList);
        recyclerView.setAdapter(postAdapter);

        return view;
    }

    // Method to fetch posts from the database and convert them into Post objects
    private List<Post> fetchPostsFromDatabase() {
        List<Post> postList = new ArrayList<>();
        Cursor cursor = databaseHelper.getAllPosts();  // This works fine

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int postID = cursor.getInt(cursor.getColumnIndex("post_id"));
                String username = cursor.getString(cursor.getColumnIndex("username"));
                String postTitle = cursor.getString(cursor.getColumnIndex("post_title"));
                String postBody = cursor.getString(cursor.getColumnIndex("post_body"));
                String createdAt = cursor.getString(cursor.getColumnIndex("created_at"));
                int likeCount = cursor.getInt(cursor.getColumnIndex("like_count"));
                Log.d("HomeFragment", "Fetched created_at: " + createdAt);  // Debugging log


                String profileImageUri = fetchProfileImageUrl(username);  // Assuming this method works fine

                // Corrected line: Calling fetchFullnameForUsername via databaseHelper
                String fullname = databaseHelper.fetchFullnameForUsername(username);  // Use the instance of DatabaseHelper

                postList.add(new Post(postID, fullname, username, postTitle, postBody, likeCount, 10, profileImageUri, createdAt, true)); // Pass createdAt as String
            } while (cursor.moveToNext());
            cursor.close();
        }

        return postList;
    }


    // Fetch the profile image URL from the database using the username
    private String fetchProfileImageUrl(String username) {
        String profileImageUrl = null;
        Cursor cursor = databaseHelper.getUserProfile(username);  // Fetch user profile from 'allusers' table
        if (cursor != null && cursor.moveToFirst()) {
            profileImageUrl = cursor.getString(cursor.getColumnIndex("profile_picture"));
            cursor.close();
        }
        return profileImageUrl;
    }

    // Method to refresh the posts when the profile image is updated
    public void refreshPosts() {
        // Fetch the updated posts from the database
        List<Post> updatedPostList = fetchPostsFromDatabase();
        // Update the adapter's data set
        postAdapter.updatePostList(updatedPostList);
    }

    // Method to handle profile image updates
    public void onProfileImageUpdated(Uri newImageUri) {
        // Save the new image URI
        saveImageUri(newImageUri);

        // Optionally, refresh the posts
        refreshPosts();
    }

    // Save the profile image URI to SharedPreferences
    private void saveImageUri(Uri imageUri) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ProfileData", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("profile_image", imageUri.toString()).apply();
    }

    private void saveLikeState(int postId, boolean isLiked) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LikePreferences", Context.MODE_PRIVATE);  // Use getActivity() to get the context
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("post_" + postId, isLiked);  // Storing the like state for each post by its ID
        editor.apply();
    }

    private boolean isPostLiked(int postId) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LikePreferences", Context.MODE_PRIVATE);  // Use getActivity() to get the context
        return sharedPreferences.getBoolean("post_" + postId, false);  // Default is false, meaning not liked
    }

}
