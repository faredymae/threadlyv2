package com.example.threadlyv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.threadlyv2.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize binding object before setting the content view
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullName = binding.regFullName.getText().toString();
                String username = binding.regUsername.getText().toString();
                String password = binding.regPassword.getText().toString();
                String confirmPassword = binding.regConfirmPassword.getText().toString(); // Corrected

                // Check if any field is empty
                if (fullName.equals("") || username.equals("") || password.equals("") || confirmPassword.equals("")) {
                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if passwords match
                    if (password.equals(confirmPassword)) {
                        // Check if username already exists
                        Boolean checkUsername = databaseHelper.checkUsername(username);

                        if (checkUsername == false) {
                            // Insert data into the database
                            Boolean insert = databaseHelper.insertData(fullName, username, password, confirmPassword);

                            if (insert == true) {
                                Toast.makeText(RegisterActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(RegisterActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "User already exists!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginActivityIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginActivityIntent);
            }
        });
    }
}
