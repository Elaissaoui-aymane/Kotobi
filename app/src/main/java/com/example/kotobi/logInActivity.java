package com.example.kotobi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.FirebaseAuth;

public class logInActivity extends AppCompatActivity {
 private FirebaseAuth auth1;
 private EditText Email1, password1;
 private Button log;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Email1 = findViewById(R.id.editEmailAddress);
        password1 = findViewById(R.id.editPassword);
        log = findViewById(R.id.Btn_log);
        auth1 = FirebaseAuth.getInstance();
        log.setOnClickListener(v -> {
            String email = Email1.getText().toString();
            String password = password1.getText().toString();
            if (email.isEmpty() || password.isEmpty())
                Toast.makeText(this, "please fill all the fields", Toast.LENGTH_SHORT).show();
            else
               loginUser(email, password);
        });
    }
    private void saveSignInState(boolean isSignedIn) {
        SharedPreferences sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("is_signed_in", isSignedIn);
        editor.apply();
    }

    private void loginUser(String email, String password) {
    auth1.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
        Toast.makeText(logInActivity.this, "login succseful", Toast.LENGTH_SHORT).show();
        saveSignInState(true);
        Intent intent = new Intent(getApplicationContext(), DisplayBooksActivity.class);
        startActivity(intent);
        finish();
    }).addOnFailureListener(e -> {
        Toast.makeText(this, "Email Or Password Are Incorrect", Toast.LENGTH_SHORT).show();
    });

    }
}