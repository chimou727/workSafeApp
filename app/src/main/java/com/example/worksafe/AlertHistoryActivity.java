package com.example.worksafe;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.List;

public class AlertHistoryActivity extends AppCompatActivity {

    private AlertAdapter adapter;
    private List<AlertModel> alertList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_history);

        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        RecyclerView recyclerView = findViewById(R.id.rv_alerts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        alertList = new ArrayList<>();
        adapter = new AlertAdapter(alertList);
        recyclerView.setAdapter(adapter);

        // Firebase Realtime Database path: worksafe/alerts
        databaseReference = FirebaseDatabase.getInstance().getReference("worksafe").child("alerts");

        fetchAlerts();
    }

    private void fetchAlerts() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                alertList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    AlertModel alert = dataSnapshot.getValue(AlertModel.class);
                    if (alert != null) {
                        alertList.add(alert);
                    }
                }
                // Reverse list to show latest alerts first
                Collections.reverse(alertList);
                adapter.updateList(alertList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AlertHistoryActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
