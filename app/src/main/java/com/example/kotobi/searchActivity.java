package com.example.kotobi;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class searchActivity extends AppCompatActivity {

    private EditText searchBar;
    private RecyclerView recycler;
    private SearchAdapter dataAdapter;
    private List<Books> dataList;

    private DatabaseReference databaseReference;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        searchBar = findViewById(R.id.search_bar);
        recycler = findViewById(R.id.recycler_view);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        dataList = new ArrayList<>();
        dataAdapter = new SearchAdapter(dataList,searchActivity.this);
        recycler.setAdapter(dataAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("books");

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchInFirebase(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });
    }

    private void searchInFirebase(String searchText) {
        databaseReference.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Books dataModel = snapshot.getValue(Books.class);
                            if (dataModel != null) {
                                dataList.add(dataModel);
                            }
                        }
                        dataAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("MainActivity", "onCancelled", databaseError.toException());
                    }
                });
    }
}