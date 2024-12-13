package com.example.threadlyv2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.threadlyv2.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Fetch username and password inputs
                String username = binding.loginUsername.getText().toString().trim();
                String password = binding.loginPassword.getText().toString().trim();

                // Validate input fields
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return; // Exit the listener to prevent further execution
                }

                // Check credentials against the database
                boolean checkCredentials = databaseHelper.checkUsernamePassword(username, password);
                if (checkCredentials) {
                    // Fetch user ID from the database
                    int userId = databaseHelper.getUserId(username);
                    if (userId == -1) {
                        // Handle case where user ID is not found
                        Toast.makeText(LoginActivity.this, "Error: Unable to fetch user ID", Toast.LENGTH_SHORT).show();
                        return; // Exit early to prevent undefined behavior
                    }

                    // Save user session details in SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("currentUserId", userId);  // Save user ID
                    editor.putString("currentUsername", username); // Save username
                    editor.apply();

                    // Show success message
                    Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();

                    // Navigate to the home activity
                    Intent homeActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(homeActivityIntent);
                    finish(); // Close the login activity
                } else {
                    // Show invalid credentials message
                    Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });


        binding.signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerActivityIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerActivityIntent);
            }
        });
    }
}
