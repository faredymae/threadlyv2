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
        return inflater.inflate(R.layout.fragment_profile, container, false); // Inflate the layout
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileImageView = view.findViewById(R.id.profileImageView);

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
                            Log.d("ProfileFragment", "No image selected or data is null");
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

    // Check if the app has the MANAGE_EXTERNAL_STORAGE permission (for Android 11 and above)
    private boolean isExternalStorageManager() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && android.os.Environment.isExternalStorageManager();
    }

    // Request the MANAGE_EXTERNAL_STORAGE permission
    private void requestManageExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            // Check if the intent is available before trying to start it
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                try {
                    startActivityForResult(intent, REQUEST_CODE_PERMISSION);
                } catch (Exception e) {
                    Log.e("ProfileFragment", "Unable to open settings for external storage permission", e);
                    Toast.makeText(requireContext(), "Unable to open settings. Please enable the permission manually in Settings.", Toast.LENGTH_LONG).show();
                }
            } else {
                // If the intent isn't supported, show a message
                Log.e("ProfileFragment", "Settings activity not found for managing external storage permission.");
                Toast.makeText(requireContext(), "This device does not support broad file access. Please enable the permission manually.", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Updates the profile image using Glide
    private void updateProfileImage(Uri imageUri) {
        Log.d("ProfileFragment", "Updating profile image: " + imageUri.toString());
        Glide.with(this)
                .load(imageUri)
                .circleCrop() // Ensures the image is displayed as a circle
                .placeholder(R.drawable.ic_default_pfp) // Default profile picture placeholder
                .error(R.drawable.ic_default_pfp) // Error placeholder
                .into(profileImageView);
    }

    // Saves the image URI to SharedPreferences
    private void saveImageUri(Uri imageUri) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("ProfileData", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("profile_image", imageUri.toString()).apply();
    }

    // Loads the saved image URI from SharedPreferences
    private Uri loadImageUri() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("ProfileData", Context.MODE_PRIVATE);
        String uriString = sharedPreferences.getString("profile_image", null);
        return uriString != null ? Uri.parse(uriString) : null;
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
