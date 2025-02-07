package com.example.coffeeshop;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends BaseActivity {

    SQLiteDatabase db;
    EditText username, password, confirmPassword;
    Button registerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = openOrCreateDatabase("coffeeShop", MODE_PRIVATE, null);
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS users(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "username VARCHAR, " +
                        "password VARCHAR UNIQUE);"
        );

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        registerButton = findViewById(R.id.registerBtn);

        registerButton.setOnClickListener(this::registerUser);
    }

    private void registerUser(View view) {
        String usernameInput = username.getText().toString().trim();
        String passwordInput = password.getText().toString();
        String confirmPasswordInput = confirmPassword.getText().toString();

        if (usernameInput.isEmpty()) {
            Toast.makeText(this, "Please enter a username.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (passwordInput.isEmpty() || confirmPasswordInput.isEmpty()) {
            Toast.makeText(this, "Please enter a password.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!passwordInput.equals(confirmPasswordInput)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{usernameInput});
        if (cursor.moveToFirst()) {
            Toast.makeText(this, "Username already exists. Please choose another.", Toast.LENGTH_LONG).show();
            cursor.close();
            return;
        }
        cursor.close();

        try {
            db.execSQL(
                    "INSERT INTO users (username, password) VALUES (?, ?)",
                    new Object[]{usernameInput, passwordInput}
            );
            Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show();

            username.setText("");
            password.setText("");
            confirmPassword.setText("");
        } catch (Exception e) {
            Log.e("RegisterActivity", "Error registering user", e);
            Toast.makeText(this, "Error registering user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
    }
}