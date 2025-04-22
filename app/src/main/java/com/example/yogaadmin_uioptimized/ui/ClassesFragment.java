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
import com.example.yogaadmin_uioptimized.viewmodel.ClassViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ClassesFragment extends Fragment {

    private ClassViewModel classViewModel;
    private RecyclerView classesRecyclerView;
    private ClassAdapter classAdapter;
    private FloatingActionButton fabAddClass;

    public ClassesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_classes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        classesRecyclerView = view.findViewById(R.id.classesRecyclerView);
        fabAddClass = view.findViewById(R.id.fabAddClass);

        classViewModel = new ViewModelProvider(this).get(ClassViewModel.class);

        classesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        classAdapter = new ClassAdapter(new ArrayList<>(), classViewModel, getContext()); // Initialize with empty list and context
        classesRecyclerView.setAdapter(classAdapter);

        classViewModel.getAllClasses().observe(getViewLifecycleOwner(), classItems -> {
            classAdapter.setClassList(classItems);
        });

        fabAddClass.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddClassActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (classViewModel != null && classAdapter != null) {
            classViewModel.getAllClasses().observe(getViewLifecycleOwner(), classItems -> {
                classAdapter.setClassList(classItems);
            });
        }
    }
}