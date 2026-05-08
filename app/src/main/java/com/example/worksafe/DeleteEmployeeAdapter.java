package com.example.worksafe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DeleteEmployeeAdapter extends RecyclerView.Adapter<DeleteEmployeeAdapter.ViewHolder> {

    private List<Employee> fullList;
    private List<Employee> filteredList;
    private OnDeleteClickListener deleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(Employee employee);
    }

    public DeleteEmployeeAdapter(List<Employee> list, OnDeleteClickListener listener) {
        this.fullList = list;
        this.filteredList = new ArrayList<>(list);
        this.deleteClickListener = listener;
    }

    public void updateList(List<Employee> newList) {
        this.fullList = newList;
        this.filteredList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_delete_employee, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Employee emp = filteredList.get(position);
        holder.name.setText(emp.name);
        holder.role.setText(emp.role);
        
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(emp);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, role;
        ImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtName);
            role = itemView.findViewById(R.id.txtRole);
            btnDelete = itemView.findViewById(R.id.btn_delete_item);
        }
    }
}
