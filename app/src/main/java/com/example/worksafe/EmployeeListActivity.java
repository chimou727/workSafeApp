package com.example.worksafe;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EmployeeListActivity extends AppCompatActivity {

    private static final String TAG = "EmployeeListActivity";
    private EmployeeAdapter adapter;
    private List<Employee> employeeList;
    private FirebaseFirestore db;
    private EditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);

        // Set title
        TextView title = findViewById(R.id.tv_employees_title);
        if (title != null) title.setText("Employees");

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewEmployees);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        employeeList = new ArrayList<>();
        adapter = new EmployeeAdapter(employeeList);
        recyclerView.setAdapter(adapter);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Setup Search Bar
        searchBar = findViewById(R.id.search_bar);
        setupSearchBar();
        
        // Setup Floating Action Button to open AddEmployeeActivity
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> {
                Intent intent = new Intent(EmployeeListActivity.this, AddEmployeeActivity.class);
                startActivity(intent);
            });
        }

        // Use a real-time listener to fetch employees
        listenForEmployees();
    }

    private void setupSearchBar() {
        if (searchBar != null) {
            searchBar.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (adapter != null) {
                        adapter.filter(s.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void listenForEmployees() {
        db.collection("employees")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Listen failed.", error);
                        Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        List<Employee> newList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            Employee employee = doc.toObject(Employee.class);
                            if (employee != null && employee.name != null) {
                                newList.add(employee);
                            }
                        }
                        
                        employeeList = newList;
                        adapter.updateList(employeeList);
                        
                        // If there was text in search bar, re-filter
                        if (searchBar != null && !searchBar.getText().toString().isEmpty()) {
                            adapter.filter(searchBar.getText().toString());
                        }
                        
                        Log.d(TAG, "Fetched " + employeeList.size() + " employees");
                    }
                });
    }
}
