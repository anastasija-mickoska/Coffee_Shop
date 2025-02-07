package com.example.coffeeshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    protected void onStart() {
        super.onStart();
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logoutItem = menu.findItem(R.id.action_logout);

        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("username", null);
        if (loggedInUser != null) {
            logoutItem.setVisible(true);
        } else {
            logoutItem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;

        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("username", null);

        if (id == R.id.action_home) {
            intent = new Intent(this, MainActivity.class);
        } else if (id == R.id.action_menu) {
            intent = new Intent(this, MenuActivity.class);
        } else if (id == R.id.action_login) {
            if ("admin".equals(loggedInUser)) {
                intent = new Intent(this, AdminActivity.class);
            } else if (loggedInUser != null) {
                intent = new Intent(this, MyOrdersActivity.class);
            } else {
                intent = new Intent(this, LoginActivity.class);
            }
        } else if (id == R.id.action_cart) {
            if (loggedInUser != null) {
                intent = new Intent(this, CartActivity.class);
            } else {
                intent = new Intent(this, LoginActivity.class);
            }
        }
        else if (id == R.id.action_logout) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            intent = new Intent(this, MainActivity.class);
        }

        if (intent != null) {
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
