package com.example.threadlyv2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProfileFragment extends Fragment {

    private static final int REQUEST_CODE_PERMISSION = 1;
    private ImageView profileImageView;
    private RecyclerView recyclerViewUserPosts;
    private PostAdapter postAdapter;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Button logoutBtn;

    private DatabaseHelper databaseHelper;
    private int currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("ProfileFragment", "onCreateView called");
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        profileImageView = rootView.findViewById(R.id.profileImageView);
        recyclerViewUserPosts = rootView.findViewById(R.id.recyclerViewUserPosts);
        logoutBtn = rootView.findViewById(R.id.logoutBtn);
        TextView profTVName = rootView.findViewById(R.id.profTVName);  // Full Name TextView
        TextView profTVUserName = rootView.findViewById(R.id.profTVUserName);  // Username TextView

        // Set up RecyclerView
        recyclerViewUserPosts.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(requireContext());

        // Get current user ID from SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("currentUserId", -1);

        if (currentUserId != -1) {
            // Load and display user posts
            loadUserPosts();

            // Load user's full name from the database (assuming you have a method for this)
            String fullName = databaseHelper.getUserFullName(currentUserId);
            profTVName.setText(fullName);

            // Use static value for testing username
            profTVUserName.setText("TestUsername");  // Static value for testing
        } else {
            Log.e("ProfileFragment", "Invalid user ID");
            Toast.makeText(requireContext(), "Unable to load posts", Toast.LENGTH_SHORT).show();
        }

        // Set up logout button functionality
        logoutBtn.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            updateProfileImage(null);

            // Redirect to Login screen
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        return rootView;
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("ProfileFragment", "onViewCreated called");

        // Initialize the image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            Uri selectedImageUri = data.getData();
                            updateProfileImage(selectedImageUri);
                            saveImageUri(selectedImageUri);
                        }
                    }
                });

        // Load saved profile image
        Uri savedImageUri = loadImageUri();
        updateProfileImage(savedImageUri);

        // Set profile image click listener
        profileImageView.setOnClickListener(v -> openImagePicker());
    }

    private void loadUserPosts() {
        List<Post> userPosts = databaseHelper.getPostsByUser(currentUserId);
        if (userPosts != null && !userPosts.isEmpty()) {
            postAdapter = new PostAdapter(requireContext(), userPosts);
            recyclerViewUserPosts.setAdapter(postAdapter);
        } else {
            Log.d("ProfileFragment", "No posts found for the user.");
            Toast.makeText(requireContext(), "No posts to display", Toast.LENGTH_SHORT).show();
        }
    }

    private void openImagePicker() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
            } else {
                launchImagePicker();
            }
        } else {
            launchImagePicker();
        }
    }

    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void updateProfileImage(@Nullable Uri imageUri) {
        if (imageUri != null) {
            Glide.with(this)
                    .load(imageUri)
                    .circleCrop()
                    .placeholder(R.drawable.ic_default_pfp)
                    .error(R.drawable.ic_default_pfp)
                    .into(profileImageView);
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_default_pfp)
                    .into(profileImageView);
        }
    }

    private void saveImageUri(Uri imageUri) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("ProfileData", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("profile_image", imageUri.toString()).apply();
        String username = sharedPreferences.getString("username", null);

        if (username != null) {
            databaseHelper.updateUserProfileImage(username, imageUri.toString());
        }
    }

    private Uri loadImageUri() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("ProfileData", Context.MODE_PRIVATE);
        String uriString = sharedPreferences.getString("profile_image", null);
        return uriString != null ? Uri.parse(uriString) : null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchImagePicker();
            }
        }
    }
}
