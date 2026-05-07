package com.example.worksafe;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddEmployeeActivity extends AppCompatActivity {

    private EditText etEmployeeId, etName, etRole;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        db = FirebaseFirestore.getInstance();

        etEmployeeId = findViewById(R.id.etEmployeeId);
        etName = findViewById(R.id.etName);
        etRole = findViewById(R.id.etRole);
        Button btnSave = findViewById(R.id.btnSaveEmployee);
        ImageView btnBack = findViewById(R.id.btnBack);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> saveEmployee());
        }
    }

    private void saveEmployee() {
        String id = etEmployeeId.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String role = etRole.getText().toString().trim();

        if (id.isEmpty() || name.isEmpty() || role.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // Structure de l'employé avec les 7 champs requis
        Map<String, Object> employee = new HashMap<>();
        employee.put("id", id);
        employee.put("name", name);
        employee.put("role", role);
        employee.put("status", "safe");
        
        // Champs capteurs par défaut
        employee.put("gas", 400);
        employee.put("heartRate", 72);
        employee.put("temp", 36.5);

        db.collection("employees").document(id)
                .set(employee)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Employé ajouté avec succès", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
