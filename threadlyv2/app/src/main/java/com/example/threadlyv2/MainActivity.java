package com.example.threadlyv2;

import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.threadlyv2.databinding.ActivityLoginBinding;
import com.example.threadlyv2.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // Navigation Bar - Switching of activities
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replacedFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                replacedFragment(new HomeFragment());
            }
            else if (itemId == R.id.nav_search) {
                replacedFragment(new SearchFragment());

            } else if (itemId == R.id.nav_addPost) {
                replacedFragment(new AddPostFragment());

            } else if (itemId == R.id.nav_notification) {
                replacedFragment(new NotificationFragment());

            } else if (itemId == R.id.nav_profile) {
                replacedFragment(new ProfileFragment());

            }
            return true;
        });


    }
    private void replacedFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}