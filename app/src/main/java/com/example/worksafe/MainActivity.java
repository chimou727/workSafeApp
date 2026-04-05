package com.example.worksafe;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText email = findViewById(R.id.emailInput);
        EditText password = findViewById(R.id.passwordInput);
        ImageView eyeIcon = findViewById(R.id.eyeIcon);
        Button login = findViewById(R.id.loginBtn);

        // Gestion de la visibilité du mot de passe
        eyeIcon.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // Masquer le mot de passe
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                eyeIcon.setImageResource(android.R.drawable.ic_menu_view);
            } else {
                // Afficher le mot de passe
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                eyeIcon.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            }
            isPasswordVisible = !isPasswordVisible;
            // Replacer le curseur à la fin du texte
            password.setSelection(password.getText().length());
        });

        // Gestion du clic sur le bouton Login
        login.setOnClickListener(v -> {
            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();

            if (userEmail.isEmpty()) {
                email.setError("Enter your email");
                return;
            }

            if (userPassword.isEmpty()) {
                password.setError("Enter your password");
                return;
            }

            // Validation des identifiants et navigation
            if (userEmail.equals("admin@gmail.com") && userPassword.equals("1234")) {
                Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
                
                // Connexion vers l'écran de la liste des employés
                Intent intent = new Intent(MainActivity.this, EmployeeListActivity.class);
                startActivity(intent);
                finish(); // Ferme l'écran de login pour éviter de revenir en arrière
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }
}