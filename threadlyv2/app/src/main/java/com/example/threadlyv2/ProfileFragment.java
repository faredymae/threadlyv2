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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class ProfileFragment extends Fragment {

    private static final int REQUEST_CODE_PERMISSION = 1;
    private ImageView profileImageView;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("ProfileFragment", "onCreateView called");
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("ProfileFragment", "onViewCreated called");

        profileImageView = view.findViewById(R.id.profileImageView);

        if (profileImageView == null) {
            Log.e("ProfileFragment", "ProfileImageView is null");
        }

        // Initialize the image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            Uri selectedImageUri = data.getData();
                            Log.d("ProfileFragment", "Image selected: " + selectedImageUri.toString());
                            updateProfileImage(selectedImageUri);
                            saveImageUri(selectedImageUri); // Save the URI to SharedPreferences
                        } else {
                            Log.e("ProfileFragment", "No image selected or data is null");
                        }
                    } else {
                        Log.d("ProfileFragment", "Image selection cancelled");
                        Toast.makeText(requireContext(), "Image selection cancelled", Toast.LENGTH_SHORT).show();
                    }
                });

        // Load and display the saved profile image, if any
        Uri savedImageUri = loadImageUri();

        if (savedImageUri != null) {
            updateProfileImage(savedImageUri);
        } else {
            // Load default image if no saved URI is found
            updateProfileImage(null); // Glide will use the placeholder in this case
        }

        // Set onClickListener to the profileImageView
        profileImageView.setOnClickListener(v -> {
            Log.d("ProfileFragment", "Profile Image clicked");
            openImagePicker();
        });
    }

    // Opens the image picker when the user clicks on the profile image
    private void openImagePicker() {
        Log.d("ProfileFragment", "Opening image picker...");

        // For Android 10 and below (API level 29 or lower), request READ_EXTERNAL_STORAGE permission
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Log.d("ProfileFragment", "Permission not granted, requesting...");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
            } else {
                // Permission granted, proceed with opening the image picker
                launchImagePicker();
            }
        } else {
            // For Android 11 and above, proceed with the image picker using scoped storage
            launchImagePicker();
        }
    }

    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Log.d("ProfileFragment", "Launching image picker intent...");
        imagePickerLauncher.launch(intent);
    }

    // Updates the profile image using Glide
    private void updateProfileImage(Uri imageUri) {
        Log.d("ProfileFragment", "Updating profile image: " + (imageUri != null ? imageUri.toString() : "null"));
        if (imageUri != null) {
            Glide.with(this)
                    .load(imageUri)
                    .circleCrop() // Ensures the image is displayed as a circle
                    .placeholder(R.drawable.ic_default_pfp) // Default profile picture placeholder
                    .error(R.drawable.ic_default_pfp) // Error placeholder
                    .into(profileImageView);
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_default_pfp) // Default placeholder
                    .into(profileImageView);
        }
    }

    // Saves the image URI to SharedPreferences and updates the database
    private void saveImageUri(Uri imageUri) {
        try {
            Log.d("ProfileFragment", "Saving image URI: " + imageUri.toString());

            // Save the image URI to SharedPreferences
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("ProfileData", Context.MODE_PRIVATE);
            sharedPreferences.edit().putString("profile_image", imageUri.toString()).apply();

            // Get the username from SharedPreferences (you can store this value during login)
            SharedPreferences prefs = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            String username = prefs.getString("username", null);

            Log.d("ProfileFragment", "Retrieved username: " + username);
            // Update the profile image URI in the database
            if (username != null) {
                DatabaseHelper databaseHelper = new DatabaseHelper(requireContext());
                databaseHelper.updateUserProfileImage(username, imageUri.toString());
                Log.d("ProfileFragment", "Profile image updated in database");
            } else {
                Log.e("ProfileFragment", "Username is null, cannot update database");
            }
        } catch (Exception e) {
            Log.e("ProfileFragment", "Error saving image URI: " + e.getMessage(), e);
        }
    }

    // Loads the saved image URI from SharedPreferences
    private Uri loadImageUri() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("ProfileData", Context.MODE_PRIVATE);
        String uriString = sharedPreferences.getString("profile_image", null);

        if (uriString != null) {
            return Uri.parse(uriString);
        } else {
            // Fallback to default image if not found
            return null;
        }
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("ProfileFragment", "Permission granted, opening image picker");
                launchImagePicker();
            } else {
                Log.d("ProfileFragment", "Permission denied");
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
