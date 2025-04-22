package com.example.yogaadmin_uioptimized.sync;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.yogaadmin_uioptimized.utils.NetworkUtils; // Import NetworkUtils
import com.example.yogaadmin_uioptimized.viewmodel.FirebaseViewModel;
import androidx.lifecycle.ViewModelProvider; // Import ViewModelProvider - Not needed here

public class SyncWorker extends Worker {

    private final Context context; // Hold Context
    private FirebaseViewModel firebaseViewModel; // Hold FirebaseViewModel

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        firebaseViewModel = new ViewModelProvider.AndroidViewModelFactory((Application) context.getApplicationContext()).create(FirebaseViewModel.class); // Initialize FirebaseViewModel
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("SyncWorker", "doWork: SyncWorker started");
        if (NetworkUtils.isNetworkConnected(context)) {
            Log.d("SyncWorker", "doWork: Network connected, starting Firebase sync");
            firebaseViewModel.uploadAllData(); // Trigger Firebase data upload
            Log.d("SyncWorker", "doWork: Firebase sync triggered");
            return Result.success(); // Indicate success
        } else {
            Log.w("SyncWorker", "doWork: No network connection, sync skipped");
            return Result.failure(); // Indicate failure (not an error, just skipped sync due to no network)
        }
    }
}