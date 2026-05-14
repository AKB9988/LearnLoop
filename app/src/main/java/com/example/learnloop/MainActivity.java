package com.example.learnloop;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.learnloop.fragments.BountyBoardFragment;
import com.example.learnloop.fragments.CreateRequestFragment;
import com.example.learnloop.fragments.DashboardFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Main activity hosting the bottom navigation and fragment container.
 * Tabs: Requests | Dashboard | Create | Profile
 */
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Verify user is authenticated
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        bottomNav = findViewById(R.id.bottomNavigation);

        // Default fragment
        if (savedInstanceState == null) {
            loadFragment(new BountyBoardFragment());
            bottomNav.setSelectedItemId(R.id.nav_requests);
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_requests) {
                fragment = new BountyBoardFragment();
            } else if (itemId == R.id.nav_dashboard) {
                fragment = new DashboardFragment();
            } else if (itemId == R.id.nav_create) {
                fragment = new CreateRequestFragment();
            } else if (itemId == R.id.nav_profile) {
                fragment = new com.example.learnloop.fragments.ProfileFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}