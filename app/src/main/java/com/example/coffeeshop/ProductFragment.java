package com.example.coffeeshop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.io.File;

public class ProductFragment extends Fragment {
    private String productName;
    private String imagePath;
    private double productPrice;
    public ProductFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productName = getArguments().getString("product_name");
            imagePath = getArguments().getString("image_path");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product, container, false);
        NumberPicker numberPicker = rootView.findViewById(R.id.quantityPicker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(10);
        numberPicker.setValue(1);
        TextView nameTextView = rootView.findViewById(R.id.productNameDetails);
        ImageView imageView = rootView.findViewById(R.id.productImageDetails);
        TextView priceTextView = rootView.findViewById(R.id.productPriceDetails);
        nameTextView.setText(productName);
        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        }
        SQLiteDatabase db = requireContext().openOrCreateDatabase("coffeeShop", Context.MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT price FROM products WHERE name = ?", new String[]{productName});
        if (cursor.moveToFirst()) {
            productPrice = cursor.getDouble(0);
            cursor.close();
        }
        priceTextView.setText("Price: "+ productPrice+ "$");
        return rootView;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        Button addCartBtn = view.findViewById(R.id.addToCartBtn);
        addCartBtn.setOnClickListener(v -> {
            if(username != null) {
                SQLiteDatabase db = requireContext().openOrCreateDatabase("coffeeShop", Context.MODE_PRIVATE, null);

                db.execSQL("CREATE TABLE IF NOT EXISTS cart ( " +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "userID INTEGER, " +
                        "productID INTEGER, " +
                        "quantity INTEGER, " +
                        "totalPrice REAL, " +
                        "FOREIGN KEY(userID) REFERENCES users(id), " +
                        "FOREIGN KEY(productID) REFERENCES products(id) )");
                NumberPicker numberPicker = view.findViewById(R.id.quantityPicker);
                int quantity = numberPicker.getValue();
                int userId = 0;
                Cursor cursor = db.rawQuery("SELECT id from users WHERE username=?", new String[]{username});
                if(cursor.moveToFirst()) {
                    userId = cursor.getInt(0);
                    cursor.close();
                }
                int productId = 0;
                Cursor cursor1 = db.rawQuery("SELECT id from products WHERE name=?", new String[]{productName});
                if(cursor1.moveToFirst()) {
                    productId = cursor1.getInt(0);
                    cursor1.close();
                }
                addToCart(userId, productId, quantity, productPrice);
                Intent intent = new Intent(getActivity(), CartActivity.class);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
    public void addToCart(int userID, int productID, int quantity, double pricePerUnit) {
        SQLiteDatabase db = requireContext().openOrCreateDatabase("coffeeShop", Context.MODE_PRIVATE, null);

        double totalPrice = quantity * pricePerUnit;

        Cursor cursor = db.rawQuery("SELECT quantity, totalPrice FROM cart WHERE userID = ? AND productID = ?",
                new String[]{String.valueOf(userID), String.valueOf(productID)});

        if (cursor.moveToFirst()) {
            int existingQuantity = cursor.getInt(0);
            double existingTotal = cursor.getDouble(1);
            int newQuantity = existingQuantity + quantity;
            double newTotal = existingTotal + totalPrice;

            db.execSQL("UPDATE cart SET quantity = ?, totalPrice = ? WHERE userID = ? AND productID = ?",
                    new Object[]{newQuantity, newTotal, userID, productID});
        } else {
            db.execSQL("INSERT INTO cart (userID, productID, quantity, totalPrice) VALUES (?, ?, ?, ?)",
                    new Object[]{userID, productID, quantity, totalPrice});
        }
        cursor.close();
        db.close();
    }


}
