package com.example.worksafe;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> {

    private List<Employee> list;

    public EmployeeAdapter(List<Employee> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, role, status;
        ImageView avatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtName);
            role = itemView.findViewById(R.id.txtRole);
            status = itemView.findViewById(R.id.txtStatus);
            avatar = itemView.findViewById(R.id.img_avatar);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_employee, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Employee emp = list.get(position);

        holder.name.setText(emp.name);
        holder.role.setText(emp.role);
        holder.status.setText(emp.status);

        if (emp.status != null) {
            int color;
            switch (emp.status.toUpperCase()) {
                case "SAFE":
                    color = Color.parseColor("#28A745");
                    holder.status.setBackgroundResource(R.drawable.status_safe);
                    break;
                case "WARNING":
                    color = Color.parseColor("#FFC107");
                    holder.status.setBackgroundResource(R.drawable.status_warning);
                    break;
                case "DANGER":
                    color = Color.parseColor("#DC3545");
                    holder.status.setBackgroundResource(R.drawable.status_danger);
                    break;
                default:
                    color = Color.GRAY;
                    holder.status.setBackgroundResource(R.drawable.status_safe);
                    break;
            }

            if (holder.avatar instanceof ShapeableImageView) {
                ((ShapeableImageView) holder.avatar).setStrokeColor(ColorStateList.valueOf(color));
            }
        }

        // 1. CLICK ON THE BADGE (STATUT)
        holder.status.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Status: " + emp.status, Toast.LENGTH_SHORT).show();
        });

        // 2. CLICK ON EMPLOYEE CARD -> OPEN DASHBOARD
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EmployeeDashboardActivity.class);
            intent.putExtra("EMP_NAME", emp.name);
            intent.putExtra("EMP_ROLE", emp.role);
            intent.putExtra("EMP_STATUS", emp.status);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }
}
