package com.example.coffeeshop;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.FragmentTransaction;

public class DashboardFragment extends Fragment {

    private Button addProductBtn, listProductsBtn, deleteProductBtn;
    private boolean isLandscape;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        addProductBtn = rootView.findViewById(R.id.addBtn);
        listProductsBtn = rootView.findViewById(R.id.listProducts);
        deleteProductBtn = rootView.findViewById(R.id.deleteProduct);

        isLandscape = getResources().getConfiguration().orientation ==
                android.content.res.Configuration.ORIENTATION_LANDSCAPE;

        addProductBtn.setOnClickListener(view -> openFragment(new AddProductFragment()));
        listProductsBtn.setOnClickListener(view -> openFragment(new ListProductsFragment()));
        deleteProductBtn.setOnClickListener(view -> openFragment(new DeleteProductFragment()));

        return rootView;
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        if (isLandscape) {
            ((AdminActivity) getActivity()).adjustLayoutForTwoFragments();
            transaction.replace(R.id.fragmentContainer2, fragment);
        } else {
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

}
