package com.example.threadlyv2;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter; // Assuming you have a PostAdapter for displaying posts
    private DatabaseHelper databaseHelper;
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize components
        recyclerView = view.findViewById(R.id.recyclerViewSearchResults);
        searchView = view.findViewById(R.id.searchViewPosts);
        databaseHelper = new DatabaseHelper(getActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initially, display all posts or an empty list
        List<Post> initialPosts = new ArrayList<>();
        postAdapter = new PostAdapter(getActivity(), initialPosts);
        recyclerView.setAdapter(postAdapter);

        // Handle search query
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });

        return view;
    }

    private void performSearch(String query) {
        if (query.isEmpty()) {
            postAdapter.updatePostList(new ArrayList<>()); // Clear the list when the query is empty
            return;
        }

        // Fetch filtered posts
        List<Post> filteredPosts = fetchFilteredPosts(query);
        if (filteredPosts.isEmpty()) {
            Toast.makeText(getActivity(), "No results found", Toast.LENGTH_SHORT).show();
        }

        // Update the adapter
        postAdapter.updatePostList(filteredPosts);
    }

    private List<Post> fetchFilteredPosts(String query) {
        List<Post> filteredPosts = new ArrayList<>();
        Cursor cursor = databaseHelper.searchPosts(query); // Implement this in DatabaseHelper

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int postID = cursor.getInt(cursor.getColumnIndex("post_id"));
                String username = cursor.getString(cursor.getColumnIndex("username"));
                String postTitle = cursor.getString(cursor.getColumnIndex("post_title"));
                String postBody = cursor.getString(cursor.getColumnIndex("post_body"));
                String createdAt = cursor.getString(cursor.getColumnIndex("created_at"));
                int likeCount = cursor.getInt(cursor.getColumnIndex("like_count"));

                String profileImageUri = fetchProfileImageUrl(username);
                String fullname = databaseHelper.fetchFullnameForUsername(username);

                filteredPosts.add(new Post(postID, fullname, username, postTitle, postBody, likeCount, 10, profileImageUri, createdAt, true));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return filteredPosts;
    }

    private String fetchProfileImageUrl(String username) {
        String profileImageUrl = null;
        Cursor cursor = databaseHelper.getUserProfile(username);
        if (cursor != null && cursor.moveToFirst()) {
            profileImageUrl = cursor.getString(cursor.getColumnIndex("profile_picture"));
            cursor.close();
        }
        return profileImageUrl;
    }
}
