package com.example.yogaadmin_uioptimized.ui;

import android.app.AlertDialog; // Import AlertDialog
import android.content.Context;
import android.content.DialogInterface; // Import DialogInterface
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import androidx.recyclerview.widget.RecyclerView;

import com.example.yogaadmin_uioptimized.R;
import com.example.yogaadmin_uioptimized.data.model.TeacherEntity;
import com.example.yogaadmin_uioptimized.data.model.TeacherWithClassTypes;
import com.example.yogaadmin_uioptimized.viewmodel.TeacherViewModel;

import java.util.ArrayList;
import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder> {

    private List<TeacherWithClassTypes> teachers = new ArrayList<>();
    private Context context;
    private TeacherViewModel teacherViewModel; // Add TeacherViewModel

    public TeacherAdapter(Context context, TeacherViewModel teacherViewModel) { // Update constructor
        this.context = context;
        this.teacherViewModel = teacherViewModel; // Initialize TeacherViewModel
    }

    public void setTeachers(List<TeacherWithClassTypes> teachers) {
        this.teachers = teachers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teacher_item, parent, false);
        return new TeacherViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        TeacherWithClassTypes currentTeacher = teachers.get(position);
        holder.textViewName.setText(currentTeacher.teacher.name);
        holder.textViewBasicInfo.setText(currentTeacher.teacher.basicInfo);
        // Display class types
        StringBuilder classTypes = new StringBuilder();
        if (currentTeacher.classTypes != null && !currentTeacher.classTypes.isEmpty()) {
            for (int i = 0; i < currentTeacher.classTypes.size(); i++) {
                classTypes.append(currentTeacher.classTypes.get(i).name);
                if (i < currentTeacher.classTypes.size() - 1) {
                    classTypes.append(", ");
                }
            }
        }
        holder.textViewClassTypes.setText(classTypes.toString());

        // Edit button click listener
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditTeacherActivity.class);
            intent.putExtra("teacherId", currentTeacher.teacher.teacherId);
            context.startActivity(intent);
        });

        // Delete button click listener
        holder.deleteButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Confirm Delete");
            builder.setMessage("Are you sure you want to delete this teacher?");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Proceed with deletion
                    teacherViewModel.deleteTeacher(currentTeacher.teacher);
                    Toast.makeText(context, "Teacher deleted", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Cancel deletion
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return teachers.size();
    }

    class TeacherViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewName;
        private TextView textViewBasicInfo;
        private TextView textViewClassTypes;
        ImageButton editButton; // Add Edit Button
        ImageButton deleteButton; // Add Delete Button


        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewBasicInfo = itemView.findViewById(R.id.textViewBasicInfo);
            textViewClassTypes = itemView.findViewById(R.id.textViewClassTypes);
            editButton = itemView.findViewById(R.id.editButton); // Initialize Edit Button
            deleteButton = itemView.findViewById(R.id.deleteButton); // Initialize Delete Button
        }
    }
}