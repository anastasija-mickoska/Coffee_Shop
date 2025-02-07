package com.example.coffeeshop;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuProductsAdapter extends RecyclerView.Adapter<MenuProductsAdapter.MenuProductViewHolder> {

    private Context context;
    private String category;
    private List<Map<String, String>> productList;

    public MenuProductsAdapter(Context context, String category, List<Map<String, String>> productList) {
        this.context = context;
        this.category = category;
        this.productList = productList;
    }

    @NonNull
    @Override
    public MenuProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_item_product, parent, false);
        return new MenuProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuProductViewHolder holder, int position) {
        Map<String, String> product = productList.get(position);
        holder.productNameTextView.setText(product.get("name"));
        String imagePath = product.get("image");
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        holder.imageView.setImageBitmap(bitmap);
        holder.itemView.setOnClickListener(v -> {
            openProductDetails(product.get("name"), imagePath);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    private void openProductDetails(String productName, String imagePath) {
        Bundle bundle = new Bundle();
        bundle.putString("product_name", productName);
        bundle.putString("image_path", imagePath);
        ProductFragment fragment = new ProductFragment();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();

        boolean isLandscape = context.getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE;
        if (isLandscape) {
            transaction.replace(R.id.menuFragment, fragment);
        } else {
            transaction.replace(R.id.menuFragment, fragment);
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    public static class MenuProductViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        ImageView imageView;

        public MenuProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productName);
            imageView = itemView.findViewById(R.id.productImage);
        }
    }
}
