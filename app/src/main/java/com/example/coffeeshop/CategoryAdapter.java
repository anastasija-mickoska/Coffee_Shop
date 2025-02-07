package com.example.coffeeshop;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private Context context;
    private List<String> categoryList;

    public CategoryAdapter(Context context, List<String> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categoryList.get(position);
        holder.categoryNameTextView.setText(category);

        List<Map<String, String>> productList = loadProductsFromDatabase(category);

        MenuProductsAdapter menuProductsAdapter = new MenuProductsAdapter(context, category, productList);
        holder.productRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.productRecyclerView.setNestedScrollingEnabled(false);
        holder.productRecyclerView.setAdapter(menuProductsAdapter);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    private List<Map<String, String>> loadProductsFromDatabase(String category) {
        List<Map<String, String>> productList = new ArrayList<>();
        SQLiteDatabase db = context.openOrCreateDatabase("coffeeShop", Context.MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT * FROM products WHERE category = ?", new String[]{category});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String imagePath = cursor.getString(cursor.getColumnIndex("image_path"));

                Map<String, String> product = new HashMap<>();
                product.put("name", name);
                product.put("image", imagePath);
                productList.add(product);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return productList;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTextView;
        RecyclerView productRecyclerView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.categoryName);
            productRecyclerView = itemView.findViewById(R.id.recycler_view_menuProducts);
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false) {
                @Override
                public boolean canScrollHorizontally() {
                    return false;
                }
            };
            productRecyclerView.setLayoutManager(layoutManager);
            productRecyclerView.setNestedScrollingEnabled(false);
        }
    }

}
