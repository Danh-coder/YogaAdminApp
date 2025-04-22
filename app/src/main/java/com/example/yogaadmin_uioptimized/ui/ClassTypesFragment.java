package com.example.yogaadmin_uioptimized.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yogaadmin_uioptimized.R;
import com.example.yogaadmin_uioptimized.viewmodel.ClassTypeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ClassTypesFragment extends Fragment {

    private ClassTypeViewModel classTypeViewModel;
    private RecyclerView classTypesRecyclerView;
    private ClassTypeAdapter classTypeAdapter;
    private FloatingActionButton fabAddClassType;

    public ClassTypesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_class_types, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        classTypeViewModel = new ViewModelProvider(this).get(ClassTypeViewModel.class);

        classTypesRecyclerView = view.findViewById(R.id.classTypesRecyclerView);
        fabAddClassType = view.findViewById(R.id.fabAddClassType);

        classTypesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        classTypeAdapter = new ClassTypeAdapter(getContext(), classTypeViewModel); // Initialize with context
        classTypesRecyclerView.setAdapter(classTypeAdapter);

        classTypeViewModel.getAllClassTypes().observe(getViewLifecycleOwner(), classTypeEntities -> {
            classTypeAdapter.setClassTypes(classTypeEntities);
        });

        fabAddClassType.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddClassTypeActivity.class);
            startActivity(intent);
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if (classTypeViewModel != null && classTypeAdapter != null) {
            classTypeViewModel.getAllClassTypes().observe(getViewLifecycleOwner(), classTypeEntities -> {
                classTypeAdapter.setClassTypes(classTypeEntities);
            });
        }
    }
}