package com.example.yogaadmin_uioptimized;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.work.PeriodicWorkRequest; // Import PeriodicWorkRequest
import androidx.work.WorkManager; // Import WorkManager
import androidx.work.WorkRequest; // Import WorkRequest
import java.util.concurrent.TimeUnit; // Import TimeUnit
import androidx.work.ExistingPeriodicWorkPolicy; // Import ExistingPeriodicWorkPolicy
import com.example.yogaadmin_uioptimized.sync.SyncWorker; // Import SyncWorker

import com.example.yogaadmin_uioptimized.ui.UploadActivity;
import com.example.yogaadmin_uioptimized.ui.ClassesFragment;
import com.example.yogaadmin_uioptimized.ui.ClassInstancesFragment;
import com.example.yogaadmin_uioptimized.ui.ClassTypesFragment;
import com.example.yogaadmin_uioptimized.ui.TeachersFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.tab_classes) {
                loadFragment(new ClassesFragment()); // Placeholder Fragment - Create this next
                return true;
            } else if (id == R.id.tab_class_instances) {
                loadFragment(new ClassInstancesFragment()); // Placeholder Fragment - Create this next
                return true;
            } else if (id == R.id.tab_teachers) {
                loadFragment(new TeachersFragment()); // Placeholder Fragment - Create this next
                return true;
            } else if (id == R.id.tab_class_types) {
                loadFragment(new ClassTypesFragment()); // Placeholder Fragment - Create this next
                return true;
            }
            return false;
        });

        // Load the default fragment (e.g., ClassesFragment) when the activity starts
        loadFragment(new ClassesFragment());

        scheduleSyncWorker(); // Schedule SyncWorker - Call this method at the end of onCreate
    }

    private void scheduleSyncWorker() { // Method to schedule SyncWorker
        PeriodicWorkRequest syncWorkRequest =
                new PeriodicWorkRequest.Builder(SyncWorker.class, 1, TimeUnit.MINUTES) // Run every 15 minutes (minimum interval)
                        // Additional constraints can be added here if needed (e.g., setConstraints(...))
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "YogaAdminSyncWork", // Unique work name
                ExistingPeriodicWorkPolicy.KEEP, // KEEP: Keep existing work if another is already enqueued with the same name
                syncWorkRequest);
        Log.d("MainActivity", "SyncWorker scheduled"); // Log when worker is scheduled
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu); // Create menu_main.xml next
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_upload) {
            Intent intent = new Intent(MainActivity.this, UploadActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}