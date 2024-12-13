package com.example.threadlyv2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.threadlyv2.databinding.FragmentAddPostBinding;

public class AddPostFragment extends Fragment {

    private FragmentAddPostBinding binding;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddPostBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        // Initialize database helper
        databaseHelper = new DatabaseHelper(getActivity());

        // Handle post button click
        binding.btnPost.setOnClickListener(view -> {
            // Get input data from the EditText fields
            String postTitle = binding.postEditTitle.getText().toString().trim();
            String postBody = binding.postEditBody.getText().toString().trim();

            // Retrieve the logged-in username
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            String username = sharedPreferences.getString("currentUsername", null);

            int userId = sharedPreferences.getInt("currentUserId", -1);
            if (username == null || userId == -1) {
                Toast.makeText(getActivity(), "User is not logged in. Please log in to post.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if any field is empty
            if (postTitle.isEmpty() || postBody.isEmpty()) {
                Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
            } else {
                // Insert the post into the database
                boolean insertSuccess = databaseHelper.insertPost(username, postTitle, postBody);

                if (insertSuccess) {
                    Toast.makeText(getActivity(), "Post created successfully!", Toast.LENGTH_SHORT).show();
                    // Optionally, clear the fields after successful post
                    binding.postEditTitle.setText("");
                    binding.postEditBody.setText("");
                } else {
                    Toast.makeText(getActivity(), "Failed to create post", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Release the binding object to avoid memory leaks
    }
}
