package com.example.coffeeshop;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {

    private Context context;
    private List<Map<String, String>> productList;
    private boolean showDeleteButton;
    private OnProductDeleteListener deleteListener;

    public ProductsAdapter(Context context, List<Map<String, String>> productList, boolean showDeleteButton, OnProductDeleteListener deleteListener) {
        this.context = context;
        this.productList = productList;
        this.showDeleteButton = showDeleteButton;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Map<String, String> product = productList.get(position);

        holder.productName.setText(product.get("name"));
        holder.productRating.setText("Rating: "+ product.get("rating"));
        holder.productPrice.setText("Price: "+ product.get("price") + "$");

        String imagePath = product.get("image");
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        holder.productImage.setImageBitmap(bitmap);

        if (showDeleteButton) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onProductDelete(product.get("id"), position);
                } else {
                    Toast.makeText(context, "Delete functionality is not available", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productRating, productPrice;
        Button deleteButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productRating = itemView.findViewById(R.id.product_rating);
            productPrice = itemView.findViewById(R.id.product_price);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
    public interface OnProductDeleteListener {
        void onProductDelete(String productId, int position);
    }
}
