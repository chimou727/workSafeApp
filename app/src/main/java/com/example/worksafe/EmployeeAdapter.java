package com.example.worksafe;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> {

    private List<Employee> fullList; // Store the original full list
    private List<Employee> filteredList; // List being displayed

    public EmployeeAdapter(List<Employee> list) {
        this.fullList = list;
        this.filteredList = new ArrayList<>(list);
    }

    // Method to update the full list (e.g., when Firestore updates)
    public void updateList(List<Employee> newList) {
        this.fullList = newList;
        this.filteredList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    // Method to filter the list based on search query
    public void filter(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(fullList);
        } else {
            String filterPattern = query.toLowerCase().trim();
            for (Employee item : fullList) {
                if ((item.name != null && item.name.toLowerCase().contains(filterPattern)) ||
                    (item.id != null && item.id.toLowerCase().contains(filterPattern))) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged();
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
        Employee emp = filteredList.get(position);

        holder.name.setText(emp.name != null ? emp.name : "Unknown");
        holder.role.setText(emp.role != null ? emp.role : "No Role");
        holder.status.setText(emp.status != null ? emp.status.toUpperCase() : "N/A");

        int color = Color.GRAY;
        if (emp.status != null) {
            switch (emp.status.toLowerCase()) {
                case "safe":
                    color = Color.parseColor("#28A745");
                    holder.status.setBackgroundResource(R.drawable.status_safe);
                    break;
                case "warning":
                    color = Color.parseColor("#FFC107");
                    holder.status.setBackgroundResource(R.drawable.status_warning);
                    break;
                case "danger":
                    color = Color.parseColor("#DC3545");
                    holder.status.setBackgroundResource(R.drawable.status_danger);
                    break;
                default:
                    holder.status.setBackgroundResource(R.drawable.status_safe);
                    break;
            }
        }

        if (holder.avatar instanceof ShapeableImageView) {
            ((ShapeableImageView) holder.avatar).setStrokeColor(ColorStateList.valueOf(color));
        }

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, dashboard.class);
            intent.putExtra("EMP_NAME", emp.name);
            intent.putExtra("EMP_ROLE", emp.role);
            intent.putExtra("EMP_STATUS", emp.status);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }
}
