package com.example.threadlyv2;

import android.database.Cursor;
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

                // Add a Post object to the list
                postList.add(new Post(username, postTitle, postBody, 12, 10)); // Static likes/comments for now
            } while (cursor.moveToNext());
            cursor.close();
        }

        return postList;
    }
}
