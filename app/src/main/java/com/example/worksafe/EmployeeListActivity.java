package com.example.worksafe;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EmployeeListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EmployeeAdapter adapter;
    private List<Employee> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);

        recyclerView = findViewById(R.id.recyclerViewEmployees);

        list = new ArrayList<>();

        // 🔥 SAMPLE DATA
        list.add(new Employee("John Doe", "Engineer", "SAFE"));
        list.add(new Employee("Sara Ben", "Technician", "WARNING"));
        list.add(new Employee("Ahmed Ali", "Worker", "DANGER"));
        list.add(new Employee("Li Wei", "Supervisor", "DANGER"));
        list.add(new Employee("Ben Carter", "Engineer", "SAFE"));

        adapter = new EmployeeAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
