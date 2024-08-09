package com.example.kotobi;





import static android.content.ContentValues.TAG;
import com.facebook.AccessToken;
import com.facebook.FacebookException;

import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookSdk;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;



public class SignInActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private FirebaseAuth auth;
    private CallbackManager mCallbackManager;
    FirebaseDatabase Fda;
    private GoogleSignInClient gsic;
     GoogleSignInOptions gsio;
    private ImageView Goog;
    int b = 20;
    private TextView already;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        FacebookSdk.sdkInitialize(getApplicationContext());


        etEmail = findViewById(R.id.editEmail);
        etPassword = findViewById(R.id.editTextPassword);
        Button btn_sig = findViewById(R.id.buttonSign);
        Goog = findViewById(R.id.imageGoogle);
        auth = FirebaseAuth.getInstance();
        Fda = FirebaseDatabase.getInstance();
        already = findViewById(R.id.already);
        //navigate to logIn Activity
        already.setOnClickListener(v -> {
            Intent i = new Intent(SignInActivity.this,logInActivity.class);
            startActivity(i);
        });

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();

        gsio = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id_par_defaut))
                        .requestEmail().build();


        gsic = GoogleSignIn.getClient(SignInActivity.this,gsio);

        // Sign in with EmailPassword Method
        btn_sig.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            //validate tha EditText content
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            //for format of password (ReGex) the Password must be more than 6 caracter
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Password must be more than 6 caracter", Toast.LENGTH_SHORT).show();
                return;
            }
            //Method for register user
            enregisterUser(email, password);
        });
        //Method for register with google
        Goog.setOnClickListener(v1 -> signIn());


                           //Sign in with Facebook
    mCallbackManager = CallbackManager.Factory.create();
    LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setPermissions(Arrays.asList("email", "public_profile"));

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            public void onError(@NonNull FacebookException e) {
                Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
        public void onSuccess(LoginResult loginResult) {
            Log.d(TAG, "facebook:onSuccess:" + loginResult);
            handleFacebookAccessToken(loginResult.getAccessToken());
        }

            private void handleFacebookAccessToken(AccessToken accessToken) {
            }

            @Override
        public void onCancel() {
            Log.d(TAG, "facebook:onCancel");
        }


    });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if(b == requestCode){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                auth(account.getIdToken());

            }catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }






private void handleFacebookAccessToken(AccessToken token) {
    Log.d(TAG, "handleFacebookAccessToken:" + token);

    AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
    auth.signInWithCredential(credential)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success");
                    FirebaseUser user = auth.getCurrentUser();
                    updateUI(user);
                    saveSignInState(true);
                    navigateToDisplayBooksActivity();

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    updateUI(null);
                }
            });
}

private void updateUI(FirebaseUser user) {
    // Update your UI with the user's information

}
    // those 2 method saveSignInState and navigateToDisplayBooksActivity it's for
    // register the sign in the phone
    private void saveSignInState(boolean isSignedIn) {
        SharedPreferences sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("is_signed_in", isSignedIn);
        editor.apply();
    }

    private void navigateToDisplayBooksActivity() {
        Intent intent = new Intent(SignInActivity.this, DisplayBooksActivity.class);
        startActivity(intent);
        finish();
    }


    private void signIn() {
        Intent signIn = gsic.getSignInIntent();
        startActivityForResult(signIn,20);
    }




    private void auth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        FirebaseUser user = auth.getCurrentUser();
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("id",user.getUid());
                        map.put("nom",user.getDisplayName());
                        map.put("profile",user.getPhotoUrl());
                        Fda.getReference().child("books").child(user.getUid()).setValue(map);
                        saveSignInState(true);
                        navigateToDisplayBooksActivity();
                    }else
                        Toast.makeText(SignInActivity.this, "Error in Sign In", Toast.LENGTH_SHORT).show();
                });
    }

    private void enregisterUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            envoyerEmailVerification(user);
                            saveSignInState(true);
                        }
                    } else {
                        Toast.makeText(SignInActivity.this, "Failed to register user: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("SignInActivity", "create User failed", task.getException());
                    }
                });
    }

    private void envoyerEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignInActivity.this, "Your Verification email sent", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(SignInActivity.this, VerifyActivity.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(SignInActivity.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                        Log.e("SignInActivity", "sendEmail failed", task.getException());
                    }
                });
    }


}
