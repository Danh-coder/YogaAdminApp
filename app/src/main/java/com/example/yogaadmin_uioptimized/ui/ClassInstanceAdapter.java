package com.example.yogaadmin_uioptimized.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yogaadmin_uioptimized.ClassInstanceItem;
import com.example.yogaadmin_uioptimized.R;
import com.example.yogaadmin_uioptimized.DateGroupedClassInstance;
import com.example.yogaadmin_uioptimized.data.model.ClassInstanceEntity;
import com.example.yogaadmin_uioptimized.viewmodel.ClassInstanceViewModel;
import com.example.yogaadmin_uioptimized.viewmodel.ClassViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ClassInstanceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DateGroupedClassInstance> dateGroupedItems;
    private ClassInstanceViewModel classInstanceViewModel;
    private ClassViewModel classViewModel; // Add ClassViewModel
    private Context context;
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    public ClassInstanceAdapter(List<DateGroupedClassInstance> dateGroupedItems, ClassInstanceViewModel classInstanceViewModel, ClassViewModel classViewModel, Context context) {
        this.dateGroupedItems = dateGroupedItems;
        this.classInstanceViewModel = classInstanceViewModel;
        this.classViewModel = classViewModel;
        this.context = context;
    }

    public void setClassInstanceItems(List<DateGroupedClassInstance> newList) {
        this.dateGroupedItems = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            View headerView = inflater.inflate(R.layout.class_instance_date_header, parent, false);
            return new DateHeaderViewHolder(headerView);
        } else {
            View itemView = inflater.inflate(R.layout.class_instance_item, parent, false);
            return new ClassInstanceViewHolder(itemView);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int currentPosition = 0;
        for (DateGroupedClassInstance groupedItem : dateGroupedItems) {
            if (position == currentPosition) {
                DateHeaderViewHolder headerViewHolder = (DateHeaderViewHolder) holder;
                headerViewHolder.textViewDateHeader.setText(groupedItem.getDayOfWeek() + ", " + formatDate(groupedItem.getDate()));
                return;
            }
            currentPosition++;

            for (int i = 0; i < groupedItem.getItems().size(); i++) {
                if (position == currentPosition) {
                    // Bind Class Instance Item
                    ClassInstanceViewHolder itemViewHolder = (ClassInstanceViewHolder) holder;
                    ClassInstanceItem currentClassInstance = groupedItem.getItems().get(i);
                    LocalDate date = LocalDate.parse(currentClassInstance.getDate(), formatter);

                    itemViewHolder.textViewTeacherName.setText("Teacher: " + currentClassInstance.getTeacherName());
                    itemViewHolder.textViewClassDescription.setText(currentClassInstance.getClassDescription());
                    itemViewHolder.textViewTime.setText(currentClassInstance.getClassTime());
                    itemViewHolder.textViewAdditionalComments.setText(!Objects.equals(currentClassInstance.getAdditionalComments(), "") ? "Additional Comments: " + currentClassInstance.getAdditionalComments() : "Additional Comments: None");

                    // Set OnClickListener on CardView to launch detail activity - ADDED CLICK LISTENER HERE
                    itemViewHolder.itemView.setOnClickListener(v -> { // Or use itemViewHolder.cardView.setOnClickListener if you prefer click on the entire card
                        Intent intent = new Intent(context, ClassInstanceDetailActivity.class);
                        intent.putExtra("INSTANCE_ID", currentClassInstance.instanceId);
                        // Fetch ClassItem and pass both ClassInstanceItem and ClassItem
                        classViewModel.getClassItemByIdLiveData(currentClassInstance.classId).observe((LifecycleOwner) context, classItem -> {
                            if (classItem != null) {
                                intent.putExtra("CLASS_INSTANCE_ITEM", currentClassInstance);
                                intent.putExtra("CLASS_ITEM", classItem);
                                context.startActivity(intent);
                            } else {
                                Toast.makeText(context, "Error loading class details", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });


                    itemViewHolder.editButton.setOnClickListener(v -> {
                        // Handle edit action
                        Intent intent = new Intent(context, EditClassInstanceActivity.class);
                        intent.putExtra("instanceId", currentClassInstance.instanceId);
                        context.startActivity(intent);
                    });

                    itemViewHolder.deleteButton.setOnClickListener(v -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Confirm Delete");
                        builder.setMessage("Are you sure you want to delete this class instance?");

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Proceed with deletion
                                // Handle delete action
                                ClassInstanceEntity classInstanceEntity = new ClassInstanceEntity(
                                        currentClassInstance.getClassId(),
                                        currentClassInstance.getDate(),
                                        currentClassInstance.getTeacherId(),
                                        currentClassInstance.getAdditionalComments()
                                );
                                classInstanceEntity.instanceId = currentClassInstance.instanceId;

                                classInstanceViewModel.delete(classInstanceEntity);
                                Log.d("ClassInstanceAdapter", "Deleted class instance: " + classInstanceEntity);
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

//                        // Handle delete action
//                        ClassInstanceEntity classInstanceEntity = new ClassInstanceEntity(
//                                currentClassInstance.getClassId(),
//                                currentClassInstance.getDate(),
//                                currentClassInstance.getTeacherId(),
//                                currentClassInstance.getAdditionalComments()
//                        );
//                        classInstanceEntity.instanceId = currentClassInstance.instanceId;
//
//                        classInstanceViewModel.delete(classInstanceEntity);
//                        Log.d("ClassInstanceAdapter", "Deleted class instance: " + classInstanceEntity);
                    });
                    return;
                }
                currentPosition++;
            }
        }
    }

    private String formatDate(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr, formatter);
        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        return date.format(displayFormatter);
    }


    @Override
    public int getItemViewType(int position) {
        int currentPosition = 0;
        for (DateGroupedClassInstance groupedItem : dateGroupedItems) {
            if (position == currentPosition) {
                return VIEW_TYPE_HEADER;
            }
            currentPosition++;
            currentPosition += groupedItem.getItems().size();
        }
        return VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (dateGroupedItems != null) {
            for (DateGroupedClassInstance groupedItem : dateGroupedItems) {
                count++;
                count += groupedItem.getItems().size();
            }
        }
        return count;
    }


    // Existing ClassInstanceViewHolder (renamed for clarity) - No changes needed inside this ViewHolder
    static class ClassInstanceViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewDate;
        private TextView textViewDayOfWeek;
        private TextView textViewTeacherName;
        private TextView textViewClassDescription;
        private TextView textViewTime;
        private TextView textViewAdditionalComments;
        private ImageButton editButton;
        private ImageButton deleteButton;


        public ClassInstanceViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTeacherName = itemView.findViewById(R.id.textViewTeacherName);
            textViewClassDescription = itemView.findViewById(R.id.textViewClassDescription);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewAdditionalComments = itemView.findViewById(R.id.textViewAdditionalComments);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    // New ViewHolder for Date Header
    static class DateHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDateHeader;
        public DateHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDateHeader = itemView.findViewById(R.id.textViewDateHeader);
        }
    }
}