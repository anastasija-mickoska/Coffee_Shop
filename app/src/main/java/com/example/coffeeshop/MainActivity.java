package com.example.coffeeshop;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity {

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
        Button shop = findViewById(R.id.shop_now_button);
        shop.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            startActivity(intent);
        });
        List<Map<String,String>> products = fetchTopProductsFromDatabase();

        RecyclerView topProductsRecyclerView = findViewById(R.id.recycler_view_topProducts);
        topProductsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ProductsAdapter adapter = new ProductsAdapter(this, products, false, null);
        topProductsRecyclerView.setAdapter(adapter);
    }
    private List<Map<String,String>> fetchTopProductsFromDatabase() {
        List<Map<String,String>> products = new ArrayList<>();

        SQLiteDatabase db = openOrCreateDatabase("coffeeShop", MODE_PRIVATE, null);

        Cursor cursor = db.rawQuery("SELECT name, price, image_path, rating FROM products ORDER BY rating DESC LIMIT 3", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String productName = cursor.getString(cursor.getColumnIndex("name"));
                String productPrice = cursor.getString(cursor.getColumnIndex("price"));
                String productImage = cursor.getString(cursor.getColumnIndex("image_path"));
                String productRating = cursor.getString(cursor.getColumnIndex("rating"));

                Map<String, String> product = new HashMap<>();
                product.put("name", productName);
                product.put("price", productPrice);
                product.put("image", productImage);
                product.put("rating", productRating);
                products.add(product);

            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return products;
    }
}