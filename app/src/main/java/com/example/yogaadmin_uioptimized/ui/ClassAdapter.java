package com.example.yogaadmin_uioptimized.ui;

import android.app.AlertDialog; // Import AlertDialog
import android.content.Context;
import android.content.DialogInterface; // Import DialogInterface
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yogaadmin_uioptimized.ClassItem;
import com.example.yogaadmin_uioptimized.R;
import com.example.yogaadmin_uioptimized.data.model.ClassEntity;
import com.example.yogaadmin_uioptimized.viewmodel.ClassViewModel;
import com.google.android.material.chip.Chip;

import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

    private List<ClassItem> classList;
    private ClassViewModel classViewModel;
    private Context context;

    public ClassAdapter(List<ClassItem> classList, ClassViewModel classViewModel, Context context) {
        this.classList = classList;
        this.classViewModel = classViewModel;
        this.context = context;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_item, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        ClassItem currentClass = classList.get(position);
        holder.textViewClassName.setText(currentClass.description);
        holder.textViewClassDays.setText("Days: " + String.join(", ", currentClass.daysOfWeek));
        holder.textViewClassTime.setText("Time: " + currentClass.time);
        holder.textViewClassCapacity.setText("Capacity: " + currentClass.capacity);
        holder.textViewClassDuration.setText("Duration: " + currentClass.duration + " mins");
        holder.textViewClassPrice.setText("Price: $" + currentClass.price);
        holder.textViewClassRoom.setText("Room: " + currentClass.room);
        holder.chipClassType.setText(currentClass.classTypeName);
        holder.chipLevel.setText(currentClass.level);

        String level = currentClass.level;
        if (level != null) {
            if (level.equalsIgnoreCase("Beginner")) {
                holder.chipLevel.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.levelBeginnerColor)));
                holder.chipLevel.setTextColor(Color.WHITE);
            } else if (level.equalsIgnoreCase("Intermediate")) {
                holder.chipLevel.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.levelIntermediateColor)));
                holder.chipLevel.setTextColor(Color.BLACK);
            } else if (level.equalsIgnoreCase("Advanced")) {
                holder.chipLevel.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.levelAdvancedColor)));
                holder.chipLevel.setTextColor(Color.WHITE);
            } else {
                holder.chipLevel.setChipBackgroundColor(ColorStateList.valueOf(Color.LTGRAY));
                holder.chipLevel.setTextColor(Color.BLACK);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch ClassSpecificInstanceListActivity on item click
                Intent intent = new Intent(context, ClassSpecificInstanceListActivity.class);
                intent.putExtra("CLASS_ID", currentClass.classId); // Pass classId as intent extra
                context.startActivity(intent);
            }
        });

        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditClassActivity.class);
            intent.putExtra("classId", currentClass.classId);
            context.startActivity(intent);
        });

        holder.deleteButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context); // Use AlertDialog.Builder
            builder.setTitle("Confirm Delete");
            builder.setMessage("Are you sure you want to delete this class?");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() { // Yes button
                public void onClick(DialogInterface dialog, int id) {
                    // Proceed with deletion if user clicks Yes
                    ClassEntity classEntity = new ClassEntity(
                            String.join(",", currentClass.daysOfWeek),
                            currentClass.time,
                            currentClass.capacity,
                            currentClass.duration,
                            currentClass.price,
                            currentClass.classTypeId,
                            currentClass.description,
                            currentClass.level,
                            currentClass.room
                    );
                    classEntity.classId = currentClass.classId;
                    classViewModel.delete(classEntity);
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() { // No button
                public void onClick(DialogInterface dialog, int id) {
                    // Do nothing if user clicks No/Cancel
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show(); // Show the AlertDialog
        });
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public void setClassList(List<ClassItem> classList) {
        this.classList = classList;
        notifyDataSetChanged();
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewClassName;
        public TextView textViewClassDays;
        public TextView textViewClassTime;
        public TextView textViewClassCapacity;
        public TextView textViewClassDuration;
        public TextView textViewClassPrice;
        public TextView textViewClassRoom;
        public Chip chipClassType;
        public Chip chipLevel;
        public ImageButton editButton;
        public ImageButton deleteButton;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewClassName = itemView.findViewById(R.id.textViewClassName);
            textViewClassDays = itemView.findViewById(R.id.textViewClassDays);
            textViewClassTime = itemView.findViewById(R.id.textViewClassTime);
            textViewClassCapacity = itemView.findViewById(R.id.textViewClassCapacity);
            textViewClassDuration = itemView.findViewById(R.id.textViewClassDuration);
            textViewClassPrice = itemView.findViewById(R.id.textViewClassPrice);
            textViewClassRoom = itemView.findViewById(R.id.textViewClassRoom);
            chipClassType = itemView.findViewById(R.id.chipClassType);
            chipLevel = itemView.findViewById(R.id.chipLevel);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}