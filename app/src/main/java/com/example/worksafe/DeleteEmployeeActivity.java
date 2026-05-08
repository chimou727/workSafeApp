package com.example.worksafe;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DeleteEmployeeActivity extends AppCompatActivity {

    private static final String TAG = "DeleteEmployeeActivity";
    private DeleteEmployeeAdapter adapter;
    private List<Employee> employeeList;
    private FirebaseFirestore db;
    private EditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_employee);

        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerViewDeleteEmployees);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        employeeList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        adapter = new DeleteEmployeeAdapter(employeeList, this::showDeleteConfirmation);
        recyclerView.setAdapter(adapter);

        searchBar = findViewById(R.id.search_bar_delete);
        setupSearchBar();

        fetchEmployees();
    }

    private void setupSearchBar() {
        if (searchBar != null) {
            searchBar.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.filter(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void fetchEmployees() {
        db.collection("employees")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Listen failed.", error);
                        return;
                    }

                    if (value != null) {
                        List<Employee> newList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            Employee employee = doc.toObject(Employee.class);
                            if (employee != null) {
                                employee.id = doc.getId(); // Ensure ID is set for deletion
                                if (employee.name != null) {
                                    newList.add(employee);
                                }
                            }
                        }
                        employeeList = newList;
                        adapter.updateList(employeeList);
                    }
                });
    }

    private void showDeleteConfirmation(Employee employee) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Employee")
                .setMessage("Are you sure you want to delete " + employee.name + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteEmployee(employee))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteEmployee(Employee employee) {
        if (employee.id == null || employee.id.isEmpty()) {
            Toast.makeText(this, "Error: Employee ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("employees").document(employee.id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, employee.name + " deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting document", e);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
