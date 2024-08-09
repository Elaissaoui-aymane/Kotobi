package com.example.kotobi;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddBooks extends AppCompatActivity {

    private EditText productNameEditText;
    private EditText productDescriptionEditText;
    private EditText productPriceEditText;
    private EditText productImageUrlEditText;
    private EditText Author_name;
    private EditText bookType;
    private EditText download;

    private DatabaseReference productsRef;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_books);
        download = findViewById(R.id.download_url);
        Author_name = findViewById(R.id.Author_name);
        bookType = findViewById(R.id.Book_Type);
        productNameEditText = findViewById(R.id.productNameEditText);
        productDescriptionEditText = findViewById(R.id.productDescriptionEditText);
        productPriceEditText = findViewById(R.id.productPriceEditText);
        productImageUrlEditText = findViewById(R.id.productImageUrlEditText);
        Button addProductButton = findViewById(R.id.addProductButton);

        FirebaseDatabase auth = FirebaseDatabase.getInstance();
        productsRef = auth.getReference("books");

        addProductButton.setOnClickListener(v -> addNewProduct());
    }

    private void addNewProduct() {
        String Author_Name = Author_name.getText().toString().trim();
        String type_book = bookType.getText().toString().trim();
        String productName = productNameEditText.getText().toString().trim();
        String productDescription = productDescriptionEditText.getText().toString().trim();
        String productPriceString = productPriceEditText.getText().toString().trim();
        String productImageUrl = productImageUrlEditText.getText().toString().trim();
        String download_url = download.getText().toString().trim();

        if (productName.isEmpty() || productDescription.isEmpty() || productPriceString.isEmpty() || productImageUrl.isEmpty() || Author_Name.isEmpty() ||type_book.isEmpty() || download_url.isEmpty()){
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double productPrice = Double.parseDouble(productPriceString);

        String productId = productsRef.push().getKey();
        Books newBooks = new Books(productId, productName, productDescription, productPrice, productImageUrl,Author_Name,type_book,download_url);
        productsRef.child(productId).setValue(newBooks);
        Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show();
        finish(); // Close the activity and return to the previous one
    }
}