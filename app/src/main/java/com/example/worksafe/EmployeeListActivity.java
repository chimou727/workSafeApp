package com.example.worksafe;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
        
        // Setup Add Employee Button
        View btnAddContainer = findViewById(R.id.add_employee_container);
        if (btnAddContainer != null) {
            btnAddContainer.setOnClickListener(v -> {
                Intent intent = new Intent(EmployeeListActivity.this, AddEmployeeActivity.class);
                startActivity(intent);
            });
        }
        
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> {
                Intent intent = new Intent(EmployeeListActivity.this, AddEmployeeActivity.class);
                startActivity(intent);
            });
        }

        // Setup Delete Employee Button
        View btnDeleteContainer = findViewById(R.id.delete_employee_container);
        if (btnDeleteContainer != null) {
            btnDeleteContainer.setOnClickListener(v -> {
                Intent intent = new Intent(EmployeeListActivity.this, DeleteEmployeeActivity.class);
                startActivity(intent);
            });
        }

        FloatingActionButton fabDelete = findViewById(R.id.fab_delete);
        if (fabDelete != null) {
            fabDelete.setOnClickListener(v -> {
                Intent intent = new Intent(EmployeeListActivity.this, DeleteEmployeeActivity.class);
                startActivity(intent);
            });
        }

        // Setup Menu Button (More Vert)
        ImageView btnMore = findViewById(R.id.btn_more);
        if (btnMore != null) {
            btnMore.setOnClickListener(v -> showPopupMenu(btnMore));
        }

        // Use a real-time listener to fetch employees
        listenForEmployees();
    }

    private void showPopupMenu(ImageView view) {
        PopupMenu popup = new PopupMenu(this, view);
        // Add "Historique" menu item
        popup.getMenu().add("Historique");
        
        popup.setOnMenuItemClickListener(item -> {
            if (item.getTitle().equals("Historique")) {
                Intent intent = new Intent(EmployeeListActivity.this, AlertHistoryActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
        popup.show();
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
                            // Ensure the ID from the document is set if not already in the object
                            if (employee != null) {
                                if (employee.id == null || employee.id.isEmpty()) {
                                    employee.id = doc.getId();
                                }
                                if (employee.name != null) {
                                    newList.add(employee);
                                }
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
