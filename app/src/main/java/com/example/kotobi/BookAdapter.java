package com.example.kotobi;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Books> booksList;
    private Context context;

    public BookAdapter(Context context,List<Books> booksList) {
        this.booksList = booksList;
        this.context = context;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item, parent, false);
        return new BookViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Books book = booksList.get(position);
        holder.name.setText(book.name);
        holder.description.setText(book.description);
        holder.price.setText(String.valueOf(book.price)+" dh");
        holder.Author.setText(book.author);
        holder.type.setText("type:"+book.type);

        // Load image using Glide
        Glide.with(holder.bookImage.getContext()).load(book.imageUrl).into(holder.bookImage);
        holder.buy.setOnClickListener(v -> sendWhatsAppMessage(book));

     /*   holder.download.setOnClickListener(v -> {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();

            if (user != null) {
                user.reload().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "task.isSuccessful() yyyyyyyyyyyyyes", Toast.LENGTH_SHORT).show();
                        downloadFile();
                    } else {
                        // Re-authenticate the user
                        mAuth.signInWithEmailAndPassword(Objects.requireNonNull(user.getEmail()), Objects.requireNonNull(user.getDisplayName()))
                                .addOnCompleteListener(authTask -> {
                                    if (authTask.isSuccessful()) {
                                        downloadFile();
                                    } else {
                                        Toast.makeText(context, "Re-authentication failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            } else {
                // User is not signed in, prompt to sign in
                Toast.makeText(context, "Authenticate first", Toast.LENGTH_SHORT).show();
            }

        }); */
        holder.download.setOnClickListener(v -> {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();

            if (user != null) {
                // Directly proceed to download the file
                downloadFile();
            } else {
                // User is not signed in, prompt to sign in
                Toast.makeText(context, "Authenticate first", Toast.LENGTH_SHORT).show();
                promptForPassword();
            }
        });
    }


  /*  private void downloadFile(String fileUrl) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(fileUrl);
        File localFile = new File(context.getExternalFilesDir(null), fileUrl);

            //Retrieve the ID Token

        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            // Send this token to your backend server for verification
                            Log.d("Token", "ID Token: " + idToken);
                        } else {
                            // Handle error
                            Log.e("Token", "Error fetching ID token", task.getException());
                        }
                    }
                });

        storageRef.getFile(localFile).addOnSuccessListener(taskSnapshot ->
                        Toast.makeText(context, "File downloaded successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show());
    }*/










    private void promptForPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Re-authenticate");

        final EditText input = new EditText(context);
        builder.setView(input);
        builder.setPositiveButton("OK", (dialog, which) -> {
            String password = input.getText().toString();
            reAuthenticateUser(password);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void reAuthenticateUser(String password) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Re-authentication successful
                    downloadFile();
                } else {
                    Toast.makeText(context, "Re-authentication failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



















  private void downloadFile() {
      StorageReference storageRef = FirebaseStorage.getInstance().getReference();
      StorageReference fileRef = storageRef.child("images/file.jpg"); // Adjust the path

      File localFile;
      try {
          localFile = File.createTempFile("tempFile", "jpg"); // Adjust the extension if needed
          fileRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
              // Successfully downloaded file
              Log.d(TAG, "File downloaded to: " + localFile.getAbsolutePath());
          }).addOnFailureListener(exception -> {
              // Handle any errors
              Log.e(TAG, "Download failed: " + exception.getMessage());
              Toast.makeText(context, "Download failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
          });
      } catch (IOException e) {
          Log.e(TAG, "Error creating temp file: " + e.getMessage());
          Toast.makeText(context, "Error creating temp file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
      }
  }

    public int getItemCount() {
        return booksList.size();
    }

    private void sendWhatsAppMessage(Books book) {
        String Numero = "+212612593674";
        String Msg = "Hello, I am interested in buying the book: " + book.name+" ,Is this Book still available?"; //this is the message that show in whatsapp conversation

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String url = "https://api.whatsapp.com/send?phone=" + Numero + "&text=" + Uri.encode(Msg);
            intent.setPackage("com.whatsapp");
            intent.setData(Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "we can't find whatsapp in your device", Toast.LENGTH_SHORT).show();
            Log.d("Whatsapp Exception", Objects.requireNonNull(e.getMessage()));
        }
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView description;
        public TextView price;
        public TextView Author;
        public TextView type;
        public ImageView bookImage;
        public TextView buy;
        public TextView download;

        public BookViewHolder(View itemView) {
            super(itemView);
            buy = itemView.findViewById(R.id.buy);
            download = itemView.findViewById(R.id.download);
            name = itemView.findViewById(R.id.book_Name);
            description = itemView.findViewById(R.id.book_Description);
            price = itemView.findViewById(R.id.book_Price);
            bookImage = itemView.findViewById(R.id.book_Image);
            Author = itemView.findViewById(R.id.Author);
            type = itemView.findViewById(R.id.typeBook);

        }
    }
}