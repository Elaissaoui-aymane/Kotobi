package com.example.kotobi;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayBooksActivity extends AppCompatActivity {

    private BookAdapter bookAdapter;
    private final List<Books> bookList = new ArrayList<>();
    private DatabaseReference booksRef;
    private GoogleSignInClient gsic;
    FirebaseAuth auth;
    EditText et_search;
    TextView etName;
    ImageView profile, searchIcon;
    // search = findViewById(R.id.imageSearch);

    Button out;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_display);
        out = findViewById(R.id.out);
        etName = findViewById(R.id.NameUSer);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookAdapter = new BookAdapter(getApplicationContext(),bookList);
        recyclerView.setAdapter(bookAdapter);
        auth = FirebaseAuth.getInstance();
        searchIcon = findViewById(R.id.searchIcon);
        profile = findViewById(R.id.profileImage);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        booksRef = database.getReference("books");
        //to get the informations from SignInActivity
        Intent intent = getIntent();
        String Name = intent.getStringExtra("Name");
        String ProfileUrl = intent.getStringExtra("ImageUrl");
        String nom = intent.getStringExtra("NameUse");
        if (nom != null) {
            Log.d("nomUser", nom);
        }
        if (InternetAvailable()) {
            loadBooksFromFirebase();
        } else {
            showError("No internet connection. Please check your connection and try again.");
        }
        Log.d("DisplayBooksActivity", "Profile URL: " + ProfileUrl);
        //check the profile Image if null or the variable is empty
        if (ProfileUrl != null && !ProfileUrl.isEmpty() ){
            Glide.with(DisplayBooksActivity.this)
                    .load(ProfileUrl) // Placeholder image
                    .error("error_profile")         // Error image
                    .into(profile);// ImageView in XML
            etName.setText(Name);
        } else if (nom != null) {
            etName.setText(nom);
        } else {
            // Load a default or placeholder image
            //Glide can load and display images from many sources,
            // while also taking care of caching and keeping a low memory impact
            // when doing image manipulations
            Glide.with(DisplayBooksActivity.this)
                    .load(R.drawable.background_splash)
                    .into(profile);
            etName.setText(nom);
        }
    /*    searchBar.setOnClickListener(v -> {
            String texte =searchBar.getQuery().toString();
            if (!texte.isEmpty()) {
                searchInFirebase(texte);
            }

        });*/
        searchIcon.setOnClickListener(v -> {
            Intent i2 = new Intent(getApplicationContext(),searchActivity.class);
            startActivity(i2);
        });
        //Sign out
        out.setOnClickListener(v -> signOutEmail());
    }
    private void signOutEmail() {
       if (auth != null) {
            // Sign out the user
            auth.signOut();
            Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show();
            saveSignOutState(true);
            saveSignInState(false);
            Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
            startActivity(intent);

        } else {
            Toast.makeText(this, "FirebaseAuth instance is null", Toast.LENGTH_SHORT).show();
        }
    }
    //this is for sign out
    private void saveSignOutState(boolean isSignedout) {
        SharedPreferences sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("is_signed_out", isSignedout);
        editor.apply();
    }
    private void saveSignInState(boolean isSignedIn) {
        SharedPreferences sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("is_signed_in", isSignedIn);
        editor.apply();
    }



    private void loadBooksFromFirebase() {
        booksRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Books book = snapshot.getValue(Books.class);
                    if (book != null) {
                        bookList.add(book);
                    }
                }
                bookAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Handle possible errors.
                showError("Check your internet connction");
            }
        });
    }
    // this is the Sign out method with Google in firebase
    private void signOut() {
        // Sign out from Firebase
        auth.signOut();

        // Sign out from Google if using Google Sign-In
        gsic.signOut().addOnCompleteListener(this, task -> {
            Intent intent = new Intent(DisplayBooksActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });
    }
    private boolean InternetAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = connectivityManager.getActiveNetwork();
                if (network == null) {
                    return false;
                }
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                return capabilities != null &&
                        (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
            }
        }
        return false;
    }

    private void showError(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
    private void searchInFirebase(String searchText) {
        booksRef.orderByChild("name").equalTo(searchText).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("Error","book exist");
                    Intent i = new Intent(getApplicationContext(),AddBooks.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "no Book with this name in our library", Toast.LENGTH_SHORT).show();
                }
            }
                //problem in database
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
            });
        }
}