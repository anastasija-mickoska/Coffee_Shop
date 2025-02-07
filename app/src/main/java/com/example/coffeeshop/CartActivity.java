package com.example.coffeeshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class CartActivity extends BaseActivity implements CartAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<Map<String, String>> cartItemList;
    private double totalPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.recycler_view_cart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartItemList = new ArrayList<>();
        SQLiteDatabase db = openOrCreateDatabase("coffeeShop", MODE_PRIVATE, null);
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        int userId = 0;
        Cursor cursor = db.rawQuery("SELECT id from users WHERE username=?", new String[]{username});
        if(cursor.moveToFirst()) {
            userId = cursor.getInt(0);
            cursor.close();
        }
        totalPrice=0.0;
        Cursor cursor1 = db.rawQuery("SELECT c.quantity, c.totalPrice, p.name, p.image_path, c.id FROM cart c " +
                "INNER JOIN products p ON c.productID = p.id " +
                "WHERE c.userID = ?", new String[]{String.valueOf(userId)});

        while (cursor1.moveToNext()) {
            Map<String, String> cartItem = new HashMap<>();
            cartItem.put("product_name", cursor1.getString(2));
            cartItem.put("quantity", cursor1.getString(0));
            cartItem.put("total_price", cursor1.getString(1));
            cartItem.put("image_path", cursor1.getString(3));
            cartItem.put("cartItemID", cursor1.getString(4));
            cartItemList.add(cartItem);
            totalPrice = totalPrice + cursor1.getDouble(1);
        }
        cursor1.close();
        db.close();
        cartAdapter = new CartAdapter(this, cartItemList, this);
        recyclerView.setAdapter(cartAdapter);
        TextView total = findViewById(R.id.totalPrice);
        total.setText("Total: "+ totalPrice + "$");
        Button order = findViewById(R.id.orderButton);
        double finalPrice = totalPrice;
        order.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, OrderActivity.class);

            Gson gson = new Gson();
            String cartItemsJson = gson.toJson(cartItemList);

            intent.putExtra("cartItems", cartItemsJson);
            intent.putExtra("totalPrice", finalPrice);

            startActivity(intent);
        });
    }
    @Override
    public void onItemDeleted(int position) {
        SQLiteDatabase db = openOrCreateDatabase("coffeeShop", MODE_PRIVATE, null);
        Map<String, String> item = cartItemList.get(position);
        Cursor cursor = db.rawQuery("DELETE FROM cart WHERE id=?", new String[]{String.valueOf(item.get("cartItemID"))});
        if(cursor.moveToFirst()) {
            totalPrice = totalPrice - cursor.getColumnIndex("totalPrice")*cursor.getColumnIndex("quantity");
            TextView total = findViewById(R.id.totalPrice);
            total.setText("Total: "+ totalPrice + "$");
            Toast.makeText(this, "Item removed from cart.", Toast.LENGTH_SHORT).show();
            cursor.close();
        }
        db.close();
        cartItemList.remove(position);
        cartAdapter.notifyItemRemoved(position);
    }
}