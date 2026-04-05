package com.example.worksafe;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;

public class EmployeeDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dashboard);

        // Get views
        ShapeableImageView avatar = findViewById(R.id.dash_img_avatar);
        TextView name = findViewById(R.id.dash_txtName);
        TextView role = findViewById(R.id.dash_txtRole);
        TextView status = findViewById(R.id.dash_txtStatus);
        ImageView btnBack = findViewById(R.id.btn_back);

        // Get data from Intent
        String empName = getIntent().getStringExtra("EMP_NAME");
        String empRole = getIntent().getStringExtra("EMP_ROLE");
        String empStatus = getIntent().getStringExtra("EMP_STATUS");

        // Set data
        name.setText(empName);
        role.setText(empRole);
        status.setText(empStatus);

        // Set status color and avatar border
        if (empStatus != null) {
            int color;
            switch (empStatus.toUpperCase()) {
                case "SAFE":
                    color = Color.parseColor("#28A745");
                    status.setBackgroundResource(R.drawable.status_safe);
                    break;
                case "WARNING":
                    color = Color.parseColor("#FFC107");
                    status.setBackgroundResource(R.drawable.status_warning);
                    break;
                case "DANGER":
                    color = Color.parseColor("#DC3545");
                    status.setBackgroundResource(R.drawable.status_danger);
                    break;
                default:
                    color = Color.GRAY;
            }
            avatar.setStrokeColor(ColorStateList.valueOf(color));
        }

        // Back button logic
        btnBack.setOnClickListener(v -> finish());
    }
}
