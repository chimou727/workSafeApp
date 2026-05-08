package com.example.worksafe;

import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.AlertViewHolder> {

    private List<AlertModel> alertList;

    public AlertAdapter(List<AlertModel> alertList) {
        this.alertList = alertList;
    }

    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alert, parent, false);
        return new AlertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertViewHolder holder, int position) {
        AlertModel alert = alertList.get(position);
        holder.tvEmployee.setText(alert.employee);
        holder.tvMessage.setText(alert.message);
        holder.tvType.setText(alert.type != null ? alert.type.toUpperCase() : "UNKNOWN");
        
        // Set background color based on type
        if ("SAFE".equalsIgnoreCase(alert.type)) {
            holder.tvType.setBackgroundResource(R.drawable.status_safe);
        } else if ("WARNING".equalsIgnoreCase(alert.type) || "TEMP".equalsIgnoreCase(alert.type)) {
            holder.tvType.setBackgroundResource(R.drawable.status_warning);
        } else if ("DANGER".equalsIgnoreCase(alert.type) || "FALL".equalsIgnoreCase(alert.type) || "GAS".equalsIgnoreCase(alert.type)) {
            holder.tvType.setBackgroundResource(R.drawable.status_danger);
        }

        // Format timestamp
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(alert.timestamp);
        String date = DateFormat.format("dd-MM-yyyy HH:mm", cal).toString();
        holder.tvTimestamp.setText(date);
    }

    @Override
    public int getItemCount() {
        return alertList.size();
    }

    public void updateList(List<AlertModel> newList) {
        this.alertList = newList;
        notifyDataSetChanged();
    }

    static class AlertViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmployee, tvMessage, tvType, tvTimestamp;

        public AlertViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmployee = itemView.findViewById(R.id.tv_alert_employee);
            tvMessage = itemView.findViewById(R.id.tv_alert_message);
            tvType = itemView.findViewById(R.id.tv_alert_type);
            tvTimestamp = itemView.findViewById(R.id.tv_alert_timestamp);
        }
    }
}
