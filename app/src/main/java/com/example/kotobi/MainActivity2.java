package com.example.kotobi;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {
   Button btn_sign, Btn_LOGIN,admin;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        SharedPreferences sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE);
        boolean isSignedIn = sharedPref.getBoolean("is_signed_in", false);
        boolean isVerified = sharedPref.getBoolean("is_verified", false);
       // boolean isSignedOut = sharedPref.getBoolean("is_signed_out", false);

             /* if (isSignedOut) {
                 //user sign out , navigate to SignInActivity
                 Intent intent = new Intent(MainActivity2.this, SignInActivity.class);
                 startActivity(intent);
             } */
                if (isSignedIn) {
                    if (isVerified) {
                        Log.d("isSignedIn", "isSignedIn ");
                    // User is signed in and email is verified, navigate to MainActivity
                    Intent intent1 = new Intent(MainActivity2.this, DisplayBooksActivity.class);
                    startActivity(intent1);
                    } else{
                        Toast.makeText(this, "Non Verifier", Toast.LENGTH_SHORT).show();
                    Intent intent2 = new Intent(MainActivity2.this, VerifyActivity.class);
                    startActivity(intent2);
                }
            }

        Btn_LOGIN = findViewById(R.id.Btn_LOGIN);
        btn_sign = findViewById(R.id.Btn_sign);
        admin = findViewById(R.id.Admin_btn);
        admin.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(),AddBooks.class);
            startActivity(i);
        });

        btn_sign.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            startActivity(intent);
        });
        Btn_LOGIN.setOnClickListener(v -> {
            Intent intent2 = new Intent(getApplicationContext(), logInActivity.class);
            startActivity(intent2);
        });

    }
}