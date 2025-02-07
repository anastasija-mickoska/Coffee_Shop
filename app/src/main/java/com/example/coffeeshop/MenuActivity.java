package com.example.coffeeshop;

import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;

public class MenuActivity extends BaseActivity {
    private boolean isLandscape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        isLandscape = getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE;

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.menuFragment, new MenuProductsFragment());
            transaction.commit();
        }
    }
}
