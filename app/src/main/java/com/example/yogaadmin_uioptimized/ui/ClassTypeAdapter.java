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
import androidx.recyclerview.widget.RecyclerView;

import com.example.yogaadmin_uioptimized.R;
import com.example.yogaadmin_uioptimized.data.model.ClassTypeEntity;
import com.example.yogaadmin_uioptimized.viewmodel.ClassTypeViewModel; // Add ClassTypeViewModel import

import java.util.ArrayList;
import java.util.List;

public class ClassTypeAdapter extends RecyclerView.Adapter<ClassTypeAdapter.ClassTypeViewHolder> {

    private List<ClassTypeEntity> classTypes = new ArrayList<>();
    private Context context;
    private ClassTypeViewModel classTypeViewModel; // Add ClassTypeViewModel

    public ClassTypeAdapter(Context context, ClassTypeViewModel classTypeViewModel) { // Update Constructor
        this.context = context;
        this.classTypeViewModel = classTypeViewModel; // Initialize ClassTypeViewModel
    }

    public void setClassTypes(List<ClassTypeEntity> classTypes) {
        this.classTypes = classTypes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClassTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.class_type_item, parent, false);
        return new ClassTypeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassTypeViewHolder holder, int position) {
        ClassTypeEntity currentClassType = classTypes.get(position);
        holder.textViewName.setText(currentClassType.name);
        holder.textViewDescription.setText(currentClassType.description);

        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditClassTypeActivity.class);
            intent.putExtra("classTypeId", currentClassType.classTypeId);
            context.startActivity(intent);
        });

        holder.deleteButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Confirm Delete");
            builder.setMessage("Are you sure you want to delete this class type?");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    classTypeViewModel.deleteClassType(currentClassType);
                    Toast.makeText(context, "Class type deleted", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return classTypes.size();
    }

    class ClassTypeViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewName;
        private TextView textViewDescription;
        ImageButton editButton; // Add Edit Button
        ImageButton deleteButton; // Add Delete Button


        public ClassTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            editButton = itemView.findViewById(R.id.editButton); // Initialize Edit Button
            deleteButton = itemView.findViewById(R.id.deleteButton); // Initialize Delete Button
        }
    }
}