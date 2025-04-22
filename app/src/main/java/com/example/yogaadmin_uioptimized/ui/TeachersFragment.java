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
import com.example.yogaadmin_uioptimized.viewmodel.TeacherViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TeachersFragment extends Fragment {

    private TeacherViewModel teacherViewModel;
    private RecyclerView teachersRecyclerView;
    private TeacherAdapter teacherAdapter;
    private FloatingActionButton fabAddTeacher;

    public TeachersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teachers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        teacherViewModel = new ViewModelProvider(this).get(TeacherViewModel.class);

        teachersRecyclerView = view.findViewById(R.id.teachersRecyclerView);
        fabAddTeacher = view.findViewById(R.id.fabAddTeacher);

        teachersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        teacherAdapter = new TeacherAdapter(getContext(), teacherViewModel); // Initialize with context
        teachersRecyclerView.setAdapter(teacherAdapter);

        teacherViewModel.getAllTeachersWithClassTypes().observe(getViewLifecycleOwner(), teacherWithClassTypes -> {
            teacherAdapter.setTeachers(teacherWithClassTypes);
        });

        fabAddTeacher.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddTeacherActivity.class);
            startActivity(intent);
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if (teacherViewModel != null && teacherAdapter != null) {
            teacherViewModel.getAllTeachersWithClassTypes().observe(getViewLifecycleOwner(), teacherWithClassTypes -> {
                teacherAdapter.setTeachers(teacherWithClassTypes);
            });
        }
    }
}