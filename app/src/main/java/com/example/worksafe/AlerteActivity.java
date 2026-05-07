package com.example.worksafe;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class AlerteActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerte);

        // 🔗 XML CONNECTION
        TextView txtEmployeeName = findViewById(R.id.txtEmployeeName);
        TextView txtAppTitle = findViewById(R.id.txtAppTitle);

        MaterialButton btnViewLocation = findViewById(R.id.btnViewLocation);
        MaterialButton btnCallEmergency = findViewById(R.id.btnCallEmergency);

// 🔒 PROTECTION
        if (txtEmployeeName == null || txtAppTitle == null ||
                btnViewLocation == null || btnCallEmergency == null) {

            Toast.makeText(this, "UI Error", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // 📦 DATA FROM DASHBOARD
        String empName = getIntent().getStringExtra("EMP_NAME");
        String type = getIntent().getStringExtra("type");
        getIntent().getStringExtra("message");

        // 👤 SET EMPLOYEE NAME
        if (empName != null) {
            txtEmployeeName.setText(empName);
        }

        // 🧠 DYNAMIC ALERT TITLE
        if (type != null) {
            switch (type) {
                case "FALL":
                    txtAppTitle.setText("FALL DETECTED");
                    break;

                case "GAS":
                    txtAppTitle.setText("GAS LEAK ALERT");
                    break;

                case "TEMP":
                    txtAppTitle.setText("HIGH TEMPERATURE");
                    break;

                default:
                    txtAppTitle.setText("EMERGENCY ALERT");
                    break;
            }
        }

        // 🔊 ALARM SOUND
        try {
            mediaPlayer = MediaPlayer.create(this, android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Sound error", Toast.LENGTH_SHORT).show();
        }
        // 📳 VIBRATION
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(2000);
            }
        }

        // 📍 BUTTON: VIEW LOCATION
        btnViewLocation.setOnClickListener(v -> {
            Toast.makeText(this, "Opening map...", Toast.LENGTH_SHORT).show();

            finish();
        });

        // 🚨 BUTTON: EMERGENCY ACTION
        btnCallEmergency.setOnClickListener(v -> {
            Toast.makeText(this, "Emergency response initiated!", Toast.LENGTH_LONG).show();
            finish();
        });
    }

    // 🛑 STOP SOUND WHEN LEAVING SCREEN
    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
