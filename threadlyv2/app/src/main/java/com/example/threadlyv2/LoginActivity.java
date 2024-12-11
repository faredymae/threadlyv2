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
                String username = binding.loginUsername.getText().toString();
                String password = binding.loginPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    // Check credentials
                    boolean checkCredentials = databaseHelper.checkUsernamePassword(username, password);
                    if (checkCredentials) {
                        // Save the logged-in username in SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", username); // Save the username
                        editor.apply();

                        // Log the saved username to check
                        Log.d("LoginActivity", "Username saved: " + username);

                        // Show success message
                        Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();

                        // Navigate to the home activity
                        Intent homeActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(homeActivityIntent);
                        finish(); // Close login activity
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
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
