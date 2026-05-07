package com.example.worksafe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.Manifest;
import com.google.firebase.messaging.FirebaseMessaging;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class dashboard extends AppCompatActivity {

    private TextView txtHeartRate, txtHeartStatus;
    private TextView txtMotion, txtMotionStatus;
    private TextView txtTemp, txtTempStatus;
    private TextView txtGas, txtGasStatus;
    private TextView txtGlobalStatus;

    private boolean alertOpened = false;
    private String currentEmployeeName = "Worker 1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dashboard);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult();
                        Log.d("FCM_TOKEN", token);
                    }
                });

        // 🔗 LIAISON AVEC LE XML
        txtHeartRate = findViewById(R.id.txtHeartRate);
        txtHeartStatus = findViewById(R.id.txtHeartStatus);
        txtMotion = findViewById(R.id.txtMotion);
        txtTemp = findViewById(R.id.txtTemp);
        txtTempStatus = findViewById(R.id.txtTempStatus);
        txtGas = findViewById(R.id.txtGas);
        txtGasStatus = findViewById(R.id.txtGasStatus);
        txtGlobalStatus = findViewById(R.id.txtGlobalStatus);
        TextView txtEmployeeName = findViewById(R.id.txtEmployeeName);
        TextView txtEmployeeRole = findViewById(R.id.txtEmployeeRole);
        ImageView btnBack = findViewById(R.id.btnBack);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

        // 📥 RECUPERATION DES DONNEES DE L'INTENT
        Intent intent = getIntent();
        if (intent != null) {
            String name = intent.getStringExtra("EMP_NAME");
            String role = intent.getStringExtra("EMP_ROLE");
            if (name != null) {
                currentEmployeeName = name;
                if (txtEmployeeName != null) txtEmployeeName.setText(name);
            }
            if (role != null && txtEmployeeRole != null) {
                txtEmployeeRole.setText(role);
            }
        }

        // 🔙 BOUTON RETOUR
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
        //  FIREBASE CONNECTION
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("WorkSafe")
                .child("latest");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Récupération avec les bonnes clés
                Double tempVal = snapshot.child("temperature").getValue(Double.class);
                Double gasVal = snapshot.child("gas_value").getValue(Double.class);
                String gasStatus = snapshot.child("gas_status").getValue(String.class);
                String motion = snapshot.child("fall_detection").getValue(String.class);
                String tempStatus = snapshot.child("temp_status").getValue(String.class);
                String heartbeat = snapshot.child("heartbeat").getValue(String.class);
                String workerStatus  = snapshot.child("worker_status").getValue(String.class);


                // valeurs par défaut
                float temp = (tempVal != null) ? tempVal.floatValue() : 0;
                int gas = (gasVal != null) ? gasVal.intValue() : 0;
                int heartRate = 75; // temporaire
                if (txtTempStatus != null && tempStatus != null) {
                    txtTempStatus.setText(tempStatus);
                }
                if (txtGasStatus != null && gasStatus != null) {
                    txtGasStatus.setText(gasStatus);
                }

                //  UPDATE UI
                updateSensorData(heartRate, temp, gas, motion, workerStatus);

                //  GLOBAL STATUS
                String status = calculateGlobalStatus(heartRate, temp, gas, motion);
                applyGlobalStatus(workerStatus);

                //  ALERT SYSTEM
                if (!alertOpened && "DANGER".equalsIgnoreCase(workerStatus)) {
                    alertOpened = true;
                    saveAlertToFirebase("DANGER", "Worker in  DANGER!");
                    showNotification(" DANGER", "Worker is in DANGER!");
                    openAlertScreen("DANGER", "Worker in  DANGER!");

                }


            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (txtGlobalStatus != null) txtGlobalStatus.setText("ERROR");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        alertOpened = false;
    }

    private void openAlertScreen(String type, String message) {
        Intent intent = new Intent(dashboard.this, AlerteActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("message", message);
        intent.putExtra("EMP_NAME", currentEmployeeName);
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    private void updateSensorData(int heartRate, float temp, int gas, String motion ,String workerStatus) {
        if (txtHeartRate != null) txtHeartRate.setText(heartRate + " BPM");
        if (txtTemp != null) txtTemp.setText(temp + " °C");
        if (txtGas != null) txtGas.setText(gas + " PPM");
        if (txtMotion != null) txtMotion.setText(motion);

        if (txtHeartStatus != null) {
            if (heartRate > 0 && (heartRate < 60 || heartRate > 100)) {
                txtHeartStatus.setText("WARNING");
                txtHeartStatus.setTextColor(Color.RED);
            } else if (heartRate == 0) {
                txtHeartStatus.setText("OFFLINE");
                txtHeartStatus.setTextColor(Color.GRAY);
            } else {
                txtHeartStatus.setText("STABLE");
                txtHeartStatus.setTextColor(Color.parseColor("#4CAF50"));
            }
        }


        if (txtMotionStatus != null) {
            if ("FALL".equalsIgnoreCase(motion)) {
                txtMotionStatus.setText("DANGER");
                txtMotionStatus.setTextColor(Color.RED);
            } else {
                txtMotionStatus.setText("NORMAL");
                txtMotionStatus.setTextColor(Color.parseColor("#4FC3F7"));
            }
        }
        if (txtGlobalStatus != null) {
            if ("DANGER".equalsIgnoreCase(workerStatus)) {
                txtGlobalStatus.setText("DANGER");
                txtGlobalStatus.setBackgroundColor(Color.RED);
            } else if ("WARNING".equalsIgnoreCase(workerStatus)) {
                txtGlobalStatus.setText("WARNING");
                txtGlobalStatus.setBackgroundColor(Color.parseColor("#FFB74D"));
            } else {
                txtGlobalStatus.setBackgroundColor(Color.parseColor("#4CAF50"));
            }
        }
    }

    private void applyGlobalStatus(String workerStatus) {
        if (txtGlobalStatus == null) return;
        txtGlobalStatus.setText(workerStatus);
        switch (workerStatus) {
            case "DANGER":
                txtGlobalStatus.setBackgroundColor(Color.RED);
                break;
            case "WARNING":
                txtGlobalStatus.setBackgroundColor(Color.parseColor("#FFB74D"));
                break;
            case "SAFE":
            default:
                txtGlobalStatus.setBackgroundColor(Color.parseColor("#4CAF50"));
                break;
        }
    }

    private String calculateGlobalStatus(int heartRate, float temp, int gas, String motion) {
        if ("FALL".equalsIgnoreCase(motion) || gas > 600 || temp > 39.0f) return "DANGER";
        if (gas > 400 || temp > 37.5f || heartRate > 100 || heartRate < 60) return "WARNING";
        return "SAFE";
    }


    private void saveAlertToFirebase(String type, String message) {
        DatabaseReference alertRef = FirebaseDatabase.getInstance()
                .getReference("worksafe")
                .child("alerts");
        String id = alertRef.push().getKey();
        if (id != null) {
            AlertModel alert = new AlertModel(type, message, currentEmployeeName, System.currentTimeMillis());
            alertRef.child(id).setValue(alert);
        }
    }


    public void setTxtMotionStatus(TextView txtMotionStatus) {
        this.txtMotionStatus = txtMotionStatus;
    }
    public void showNotification(String title, String message) {
        String channelId = "worksafe_alerts";

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "WorkSafe Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for danger alerts");
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_warning)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }


}
