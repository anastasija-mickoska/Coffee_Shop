package com.example.coffeeshop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.ArrayAdapter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AddProductFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText insertName, insertPrice;
    private Spinner categorySpinner;
    private Uri imageUri;
    private String imagePath = null;

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                                imageUri = result.getData().getData();
                                imagePath = saveImageToInternalStorage();
                                Toast.makeText(requireContext(), "Image selected successfully!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        insertName = view.findViewById(R.id.insertName);
        insertPrice = view.findViewById(R.id.insertPrice);
        categorySpinner = view.findViewById(R.id.chooseCategory);
        Button selectImageButton = view.findViewById(R.id.selectImageButton);
        Button addProductButton = view.findViewById(R.id.addProductButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.categories,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorySpinner.setAdapter(adapter);

        selectImageButton.setOnClickListener(v -> openGallery());

        addProductButton.setOnClickListener(v -> addProductToDatabase());

        return view;
    }

    @SuppressLint("IntentReset")
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private String saveImageToInternalStorage() {
        if (imageUri == null) return null;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
            File directory = requireActivity().getFilesDir();
            File file = new File(directory, "product_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void addProductToDatabase() {
        String name = insertName.getText().toString().trim();
        String priceStr = insertPrice.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        if (name.isEmpty() || priceStr.isEmpty() || imagePath == null) {
            Toast.makeText(requireContext(), "All fields including image are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = 0.0;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Invalid price format!", Toast.LENGTH_SHORT).show();
            return;
        }
        SQLiteDatabase db = requireContext().openOrCreateDatabase("coffeeShop", Activity.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS products (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, price REAL, category TEXT, image_path TEXT, rating REAL DEFAULT 0.0, numRatings INTEGER DEFAULT 0)");
        try {
            db.execSQL("INSERT INTO products (name, price, category, image_path, rating, numRatings) VALUES (?, ?, ?, ?, ?, ?)",
                    new Object[]{name, price, category, imagePath, 0.0, 0});
            Toast.makeText(requireContext(), "Product added successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Failed to add product!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            db.close();
        }
        Intent intent = new Intent(requireContext(), MainActivity.class);
        startActivity(intent);
    }
}
