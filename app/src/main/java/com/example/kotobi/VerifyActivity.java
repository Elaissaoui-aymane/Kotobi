package com.example.kotobi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.atomic.AtomicReference;

public class VerifyActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        Button btn = findViewById(R.id.button);
        Button resend = findViewById(R.id.resend_btn);

        btn.setOnClickListener(v -> {
            AtomicReference<FirebaseUser> user = new AtomicReference<>(FirebaseAuth.getInstance().getCurrentUser()); // Directly get the current user

            if (user.get() != null) {
                user.get().reload()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                user.set(auth.getCurrentUser()); // Update the user object

                                if (user.get() != null && user.get().isEmailVerified()) {
                                    Toast.makeText(getApplicationContext(), "Email verified", Toast.LENGTH_SHORT).show();
                                    saveVerificationState(true);
                                    navigateToMainActivity();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Email not verified", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to reload user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("VerifyActivity", "Failed to reload user", task.getException());
                            }
                        });
            } else {
                Toast.makeText(this, "User is null", Toast.LENGTH_SHORT).show();
            }
        });

        resend.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null && !user.isEmailVerified()) {
                user.sendEmailVerification()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "E-mail de vérification renvoyé avec succès", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Échec de l'envoi de l'e-mail de vérification: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("VerifyActivity", "Échec de l'envoi de l'e-mail de vérification", task.getException());
                            }
                        });
            } else {
                Toast.makeText(getApplicationContext(), "Utilisateur non connecté ou e-mail déjà vérifié", Toast.LENGTH_SHORT).show();
            }
        });
    }
        private void saveVerificationState(boolean isVerified) {
            SharedPreferences sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("is_verified", isVerified);
            editor.apply();
        }



        private void navigateToMainActivity() {
            Intent intent = new Intent(VerifyActivity.this, DisplayBooksActivity.class);
            startActivity(intent);
            finish();
        }

}
