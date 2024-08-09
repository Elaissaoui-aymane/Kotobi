package com.example.kotobi;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });






//elaissaouiaymane15@gmail.com
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(this::run, 1500);
        handler.postDelayed(this::sharedPre, 1300);


    }
    private void sharedPre() {
        SharedPreferences sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE);
        boolean isSignedOut = sharedPref.getBoolean("is_signed_out", false);
        if (isSignedOut) {
            Log.d("Is sign out", "sharedPre: true ");
            //user sign out , navigate to SignInActivity
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(intent);
        }
    }



   private void run() {
        // on below line we are
        // creating a new intent
        Intent i = new Intent(getApplicationContext(), MainActivity2.class);

        // on below line we are
        // starting a new activity.
        startActivity(i);

        // on the below line we are finishing
        // our current activity.
        finish();
    }
}

