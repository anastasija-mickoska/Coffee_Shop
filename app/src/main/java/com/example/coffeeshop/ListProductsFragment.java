package com.example.coffeeshop;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListProductsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductsAdapter adapter;
    private List<Map<String, String>> productList;
    private SQLiteDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_products, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_listProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = requireContext().openOrCreateDatabase("coffeeShop", getContext().MODE_PRIVATE, null);

        productList = loadProducts();
        adapter = new ProductsAdapter(getContext(), productList, false, null);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<Map<String, String>> loadProducts() {
        List<Map<String, String>> productList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT id, name, price, image_path, rating FROM products", null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Map<String, String> product = new HashMap<>();

                String productId = cursor.getString(cursor.getColumnIndex("id"));
                String productName = cursor.getString(cursor.getColumnIndex("name"));
                String productPrice = cursor.getString(cursor.getColumnIndex("price"));
                String productImage = cursor.getString(cursor.getColumnIndex("image_path"));
                String productRating = cursor.getString(cursor.getColumnIndex("rating"));

                product.put("id", productId);
                product.put("name", productName);
                product.put("price", productPrice);
                product.put("image", productImage);
                product.put("rating", productRating);

                productList.add(product);
            }
            cursor.close();
        }
        return productList;
    }
}
