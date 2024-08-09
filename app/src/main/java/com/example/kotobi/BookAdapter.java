package com.example.kotobi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

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