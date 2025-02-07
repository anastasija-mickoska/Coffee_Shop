package com.example.coffeeshop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<Map<String, String>> cartItemList;
    private OnItemClickListener listener;


    public CartAdapter(Context context, List<Map<String, String>> cartItemList, OnItemClickListener listener) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Map<String, String> cartItem = cartItemList.get(position);

        String productName = cartItem.get("product_name");
        String quantity = cartItem.get("quantity");
        String totalPrice = cartItem.get("total_price");
        String imagePath = cartItem.get("image_path");
        if(imagePath!=null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            holder.cartImage.setImageBitmap(bitmap);
        }
        holder.cartName.setText(productName);
        holder.cartQuantity.setText("Quantity: " + quantity);
        holder.cartPrice.setText("Total Price: " + totalPrice+ "$");
        holder.deleteFromCart.setOnClickListener(v -> {
            listener.onItemDeleted(position);
        });

    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView cartName, cartQuantity, cartPrice;
        ImageView cartImage,deleteFromCart;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cartName = itemView.findViewById(R.id.cartName);
            cartQuantity = itemView.findViewById(R.id.cartQuantity);
            cartPrice = itemView.findViewById(R.id.cartPrice);
            cartImage = itemView.findViewById(R.id.cartImage);
            deleteFromCart = itemView.findViewById(R.id.deleteFromCart);

        }
    }
    public interface OnItemClickListener {
        void onItemDeleted(int position);
    }
}
