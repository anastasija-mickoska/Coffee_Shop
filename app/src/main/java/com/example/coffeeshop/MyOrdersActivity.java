package com.example.coffeeshop;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MyOrdersActivity extends BaseActivity {
    ListView listView;
    int userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_orders);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        userId=-1;
        SQLiteDatabase db = openOrCreateDatabase("coffeeShop", MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE username = ?", new String[]{username});
        if(cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();
        listView = findViewById(R.id.list_view_myOrders);
        displayOrders();
    }

    private void displayOrders() {
        List<Map<String, String>> orderList = getUserOrders(userId);

        String[] from = {"orderId", "orderDate", "productName", "quantity", "totalPrice", "imagePath"};
        int[] to = {R.id.OrderId, R.id.OrderDate, R.id.ProductName, R.id.Quantity, R.id.TotalPrice, R.id.ProductImage};

        SimpleAdapter adapter = new SimpleAdapter(this, orderList, R.layout.item_order, from, to) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                String imagePath = orderList.get(position).get("imagePath");

                ImageView productImage = view.findViewById(R.id.ProductImage);

                if (imagePath != null && !imagePath.isEmpty()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    productImage.setImageBitmap(bitmap);
                }
                RatingBar ratingBar = view.findViewById(R.id.ratingBar);
                ratingBar.setOnRatingBarChangeListener(null);
                ratingBar.setOnRatingBarChangeListener((ratingBar1, userRating, fromUser) -> {
                    if (fromUser) {
                        Map<String, String> order = orderList.get(position);
                        int productId = -1;
                        SQLiteDatabase db = openOrCreateDatabase("coffeeShop", MODE_PRIVATE, null);
                        Cursor c = db.rawQuery("SELECT id, rating, numRatings FROM products WHERE name=?",
                                new String[]{order.get("productName")});
                        if (c.moveToFirst()) {
                            productId = c.getInt(0);
                            float currentRating = c.getFloat(1);
                            float numberRatings = c.getFloat(2);
                            float newRating = ((currentRating * numberRatings) + userRating) / (numberRatings + 1);
                            numberRatings++;
                            db.execSQL("UPDATE products SET rating = ?, numRatings = ? WHERE id = ?",
                                    new Object[]{newRating, numberRatings, productId});
                            Toast.makeText(view.getContext(), "Rating saved successfully!", Toast.LENGTH_SHORT).show();
                        }
                        c.close();
                        db.close();
                    }
                });
                return view;
            }
        };

        listView.setAdapter(adapter);
    }
    private List<Map<String, String>> getUserOrders(int userId) {
        List<Map<String, String>> orderList = new ArrayList<>();
        SQLiteDatabase db = openOrCreateDatabase("coffeeShop", MODE_PRIVATE, null);

        String query = "SELECT o.id, o.orderDate, p.name, oi.quantity, oi.totalPrice, p.rating, p.numRatings, p.image_path " +
                "FROM orders o " +
                "JOIN orderItems oi ON o.id = oi.orderId " +
                "JOIN products p ON oi.productId = p.id " +
                "WHERE o.userId = ? ORDER BY o.orderDate DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        while (cursor.moveToNext()) {
            HashMap<String, String> orderData = new HashMap<>();
            orderData.put("orderId", cursor.getString(0));
            orderData.put("orderDate", cursor.getString(1));
            orderData.put("productName", cursor.getString(2));
            orderData.put("quantity", cursor.getString(3));
            orderData.put("totalPrice", cursor.getString(4));
            orderData.put("rating", cursor.getString(5));
            orderData.put("numRatings", cursor.getString(6));
            orderData.put("imagePath", cursor.getString(7));
            orderList.add(orderData);
        }
        cursor.close();
        db.close();
        return orderList;
    }

}