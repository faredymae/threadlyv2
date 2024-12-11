package com.example.threadlyv2;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        Cursor cursor = databaseHelper.getAllPosts();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String username = cursor.getString(cursor.getColumnIndex("username"));
                String postTitle = cursor.getString(cursor.getColumnIndex("post_title"));
                String postBody = cursor.getString(cursor.getColumnIndex("post_body"));

                String profileImageUri = fetchProfileImageUrl(username); // Get profile image URI from the database

                // Add a Post object to the list
                postList.add(new Post(username, postTitle, postBody, 12, 10, profileImageUri)); // Static likes/comments for now
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
}
