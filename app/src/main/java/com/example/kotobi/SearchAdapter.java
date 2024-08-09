package com.example.kotobi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.DataViewHolder> {

    private List<Books> dataList;
    private Context cont;

    public SearchAdapter(List<Books> dataList,Context cont) {

        this.dataList = dataList;
        this.cont = cont;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.searchxml, parent, false);
        return new DataViewHolder(itemView);


        /* Initialize App Check with Play Integrity provider
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance());*/
    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        Books data = dataList.get(position);
        holder.nameTextView1.setText(data.name);
        holder.description1.setText(data.description);
        holder.price1.setText(data.price+" dh");
        holder.author1Name.setText(data.author);
        holder.type1.setText("type:"+data.type);

        holder.buy1.setOnClickListener(v -> sendWhatsAppMessage(data));
        // Load image using Glide
        Glide.with(holder.book_Image1.getContext()).load(data.imageUrl).into(holder.book_Image1);
        holder.download.setOnClickListener(v -> downloadFile(data.getFileUrl(), data.getName()));
    }

    private void downloadFile(String fileUrl, String fileName) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(fileUrl);
        File localFile = new File(cont.getExternalFilesDir(null), fileName);

        storageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> Toast.makeText(cont, "File downloaded successfully", Toast.LENGTH_SHORT).show())
           .addOnFailureListener(exception -> Toast.makeText(cont, "File download failed: " + exception.getMessage(), Toast.LENGTH_LONG).show());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
        //method for navigate to whatsApp when click to buy field
    private void sendWhatsAppMessage(Books book) {
        String Num = "+212612593674";
        String Msg = "Hello, I am interested in buying the book: " + book.name+" ,Is this Book still available?"; //this is the message that show in whatsapp conversation

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String url = "https://api.whatsapp.com/send?phone=" + Num + "&text=" + Uri.encode(Msg);
            intent.setPackage("com.whatsapp");
            intent.setData(Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            cont.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(cont, "we can't find whatsapp in your device"+e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView1, price1, description1,type1, author1Name,buy1, download;
        public ImageView book_Image1;

        public DataViewHolder(View view) {
            super(view);
            nameTextView1 = view.findViewById(R.id.book_Name1);
            price1 = view.findViewById(R.id.book_Price1);
            buy1 = view.findViewById(R.id.buy_search);
            description1 = view.findViewById(R.id.book_Description1);
            type1 = view.findViewById(R.id.typeBook1);
            author1Name = view.findViewById(R.id.Author1);
            book_Image1 = view.findViewById(R.id.book_Image1);
            download = view.findViewById(R.id.download);
        }
    }
}
