package com.example.threadlyv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        // Casting of Widgets
        Button btnSplashLogin      =  (Button)   findViewById(R.id.btnSplashLogin);
        Button btnSplashRegister   =   findViewById(R.id.btnSplashRegister);

        // Methods for the Button

        btnSplashLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent LoginActivity = new Intent(SplashActivity.this, com.example.threadlyv2.LoginActivity.class);
                startActivity(LoginActivity);

            }
        });

        btnSplashRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent RegisterActivityIntent = new Intent(SplashActivity.this, com.example.threadlyv2.RegisterActivity.class);
                startActivity(RegisterActivityIntent);
            }
        });






















        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}