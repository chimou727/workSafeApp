package com.example.worksafe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        EditText nameInput = findViewById(R.id.nameInput);
        EditText idInput = findViewById(R.id.idInput);
        Button login = findViewById(R.id.loginBtn);

        login.setOnClickListener(v -> {
            String userName = nameInput.getText().toString().trim();
            String userId = idInput.getText().toString().trim();

            if (userName.isEmpty()) {
                nameInput.setError("Enter name");
                return;
            }

            if (userId.isEmpty()) {
                idInput.setError("Enter ID");
                return;
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Querying based on the fields shown in your Firestore screenshot
            db.collection("supervisors")
                    .whereEqualTo("id", userId)
                    .whereEqualTo("name", userName)
                    .whereEqualTo("role", "supervisor")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            // Get the supervisor's document to retrieve the 'chantier'
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            String chantier = document.getString("chantier");

                            Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();

                            // Move to EmployeeListActivity and pass the chantier info
                            Intent intent = new Intent(MainActivity.this, EmployeeListActivity.class);
                            intent.putExtra("chantier", chantier);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
