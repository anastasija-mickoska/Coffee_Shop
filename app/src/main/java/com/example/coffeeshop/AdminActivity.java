package com.example.coffeeshop;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;

public class AdminActivity extends BaseActivity {

    private boolean isLandscape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        isLandscape = getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE;

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, new DashboardFragment());
            transaction.commit();

            if (isLandscape) {
                adjustLayoutForSingleFragment();
            }
        }
    }

    private void adjustLayoutForSingleFragment() {
        FrameLayout fragmentContainer = findViewById(R.id.fragmentContainer);
        FrameLayout fragmentContainer2 = findViewById(R.id.fragmentContainer2);

        fragmentContainer.getLayoutParams().width = FrameLayout.LayoutParams.MATCH_PARENT;
        fragmentContainer.getLayoutParams().height = FrameLayout.LayoutParams.WRAP_CONTENT;
        fragmentContainer2.setVisibility(View.GONE);

        fragmentContainer.requestLayout();
    }

    public void adjustLayoutForTwoFragments() {
        FrameLayout fragmentContainer = findViewById(R.id.fragmentContainer);
        FrameLayout fragmentContainer2 = findViewById(R.id.fragmentContainer2);

        fragmentContainer.setVisibility(View.VISIBLE);
        fragmentContainer2.setVisibility(View.VISIBLE);

        ConstraintLayout.LayoutParams params1 = (ConstraintLayout.LayoutParams) fragmentContainer.getLayoutParams();
        ConstraintLayout.LayoutParams params2 = (ConstraintLayout.LayoutParams) fragmentContainer2.getLayoutParams();

        params1.width = 0;
        params1.matchConstraintPercentWidth = 0.5f;
        params2.width = 0;
        params2.matchConstraintPercentWidth = 0.5f;

        fragmentContainer.setLayoutParams(params1);
        fragmentContainer2.setLayoutParams(params2);

        fragmentContainer.requestLayout();
        fragmentContainer2.requestLayout();
    }


}
