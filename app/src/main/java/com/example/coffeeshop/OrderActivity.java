package com.example.coffeeshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class OrderActivity extends BaseActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LatLng location;
    private String username;
    private EditText phoneNumber;
    private double price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        price = getIntent().getDoubleExtra("totalPrice", 0.0);
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);
        EditText userInput = findViewById(R.id.userName);
        userInput.setText(username);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        Button orderBtn = findViewById(R.id.orderButton);
        orderBtn.setOnClickListener(v -> {
            addOrderToDatabase();
            Intent intent = new Intent(OrderActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void addOrderToDatabase() {
        SQLiteDatabase db = openOrCreateDatabase("coffeeShop", MODE_PRIVATE, null);

        db.execSQL("CREATE TABLE IF NOT EXISTS orders ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userID INTEGER, " +
                "phoneNumber TEXT, " +
                "orderDate DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "latitude REAL, " +
                "longitude REAL, " +
                "totalPrice REAL, " +
                "FOREIGN KEY(userID) REFERENCES users(id))");

        int userId = 0;
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE username = ?", new String[]{username});
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();

        phoneNumber = findViewById(R.id.phoneNumber);
        String number = phoneNumber.getText().toString();

        db.execSQL("INSERT INTO orders (userID, phoneNumber, latitude, longitude, totalPrice) VALUES (?, ?, ?, ?, ?)",
                new Object[]{userId, number, location.latitude, location.longitude, price});

        int orderId = -1;
        Cursor orderCursor = db.rawQuery("SELECT last_insert_rowid()", null);
        if (orderCursor.moveToFirst()) {
            orderId = orderCursor.getInt(0);
        }
        orderCursor.close();

        if (orderId == -1) {
            Toast.makeText(this, "Error creating order", Toast.LENGTH_SHORT).show();
            return;
        }

        db.execSQL("CREATE TABLE IF NOT EXISTS orderItems ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "orderID INTEGER, " +
                "productID INTEGER, " +
                "quantity INTEGER, " +
                "totalPrice REAL, " +
                "FOREIGN KEY(orderID) REFERENCES orders(id), " +
                "FOREIGN KEY(productID) REFERENCES products(id))");

        String cartItemsJson = getIntent().getStringExtra("cartItems");
        if (cartItemsJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Map<String, String>>>() {}.getType();
            List<Map<String, String>> cartItems = gson.fromJson(cartItemsJson, type);

            for (Map<String, String> item : cartItems) {
                String productName = item.get("product_name");
                int quantity = Integer.parseInt(item.get("quantity"));
                double totalPrice = Double.parseDouble(item.get("total_price"));

                int productId = -1;
                Cursor productCursor = db.rawQuery("SELECT id FROM products WHERE name = ?", new String[]{productName});
                if (productCursor.moveToFirst()) {
                    productId = productCursor.getInt(0);
                    Log.d("Order", "Product id:"+productId);
                }
                productCursor.close();

                if (productId != -1) {
                    db.execSQL("INSERT INTO orderItems (orderID, productID, quantity, totalPrice) VALUES (?, ?, ?, ?)",
                            new Object[]{orderId, productId, quantity, totalPrice});
                }
            }
        }
        db.execSQL("DELETE FROM cart WHERE userID=?",new String[]{String.valueOf(userId)} );
        db.close();
        Toast.makeText(this, "Order saved successfully!", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng defaultLocation = new LatLng(41.9981, 21.4254);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));

        mMap.setOnMapClickListener(latLng -> {
            if (location == null) {
                location = latLng;
                mMap.addMarker(new MarkerOptions().position(latLng).title("Location"));
            }
        });
    }
}