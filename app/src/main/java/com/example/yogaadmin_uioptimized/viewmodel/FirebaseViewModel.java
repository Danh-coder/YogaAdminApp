package com.example.yogaadmin_uioptimized.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.yogaadmin_uioptimized.Converters;
import com.example.yogaadmin_uioptimized.data.ClassInstanceDao;
import com.example.yogaadmin_uioptimized.data.ClassInstanceRepository;
import com.example.yogaadmin_uioptimized.data.ClassRepository;
import com.example.yogaadmin_uioptimized.data.ClassTypeRepository; // Add ClassTypeRepository
import com.example.yogaadmin_uioptimized.data.TeacherRepository; // Add TeacherRepository
import com.example.yogaadmin_uioptimized.data.model.ClassEntity;
import com.example.yogaadmin_uioptimized.data.model.ClassInstanceEntity;
import com.example.yogaadmin_uioptimized.data.model.ClassTypeEntity; // Add ClassTypeEntity
import com.example.yogaadmin_uioptimized.data.model.TeacherEntity; // Add TeacherEntity
import com.example.yogaadmin_uioptimized.data.model.TeacherWithClassTypes;
import com.example.yogaadmin_uioptimized.utils.LiveDataUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseViewModel extends AndroidViewModel {
    private ClassRepository classRepository;
    private ClassTypeRepository classTypeRepository;
    private TeacherRepository teacherRepository;
    private ClassInstanceRepository classInstanceRepository;
    private DatabaseReference databaseReference;

    public FirebaseViewModel(Application application) {
        super(application);
        classRepository = new ClassRepository(application);
        classTypeRepository = new ClassTypeRepository(application);
        teacherRepository = new TeacherRepository(application);
        classInstanceRepository = new ClassInstanceRepository(application);
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @SuppressLint("StaticFieldLeak")
    public void uploadAllData() { // New method to upload all data
        uploadClassTypes();
        uploadTeachers();
        uploadClasses();
        uploadClassInstances();
    }

    @SuppressLint("StaticFieldLeak")
    public void uploadClasses() { // Updated to fetch data from Room
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                List<ClassEntity> classes = classRepository.getAllClassesList(); // Fetch classes from Room

                for (ClassEntity classEntity : classes) {
                    java.util.Map<String, Object> classMap = new java.util.HashMap<>();
                    classMap.put("daysOfWeek", Converters.fromString(classEntity.daysOfWeek)); // Use Converters.fromString
                    classMap.put("time", classEntity.time);
                    classMap.put("capacity", classEntity.capacity);
                    classMap.put("duration", classEntity.duration);
                    classMap.put("price", classEntity.price);
                    classMap.put("classTypeId", classEntity.classTypeId);
                    classMap.put("description", classEntity.description);
                    classMap.put("level", classEntity.level);
                    classMap.put("room", classEntity.room);

                    databaseReference.child("Classes").child(String.valueOf(classEntity.classId)).setValue(classMap);
                    Log.d("FirebaseViewModel", "Uploaded class: " + classEntity.classId); // Logging
                }
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void uploadClassTypes() { // New method to upload Class Types
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                List<ClassTypeEntity> classTypes = classTypeRepository.getAllClassTypesList(); // Get Class Types from Room

                if (classTypes != null) { // Check for null list
                    for (ClassTypeEntity classTypeEntity : classTypes) {
                        java.util.Map<String, Object> classTypeMap = new java.util.HashMap<>();
                        classTypeMap.put("name", classTypeEntity.name);
                        classTypeMap.put("description", classTypeEntity.description);

                        databaseReference.child("ClassTypes").child(String.valueOf(classTypeEntity.classTypeId)).setValue(classTypeMap);
                        Log.d("FirebaseViewModel", "Uploaded class type: " + classTypeEntity.classTypeId); // Logging
                    }
                } else {
                    Log.w("FirebaseViewModel", "uploadClassTypes: ClassTypeList is null, skipping upload"); // Warning log if null
                }
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void uploadTeachers() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                List<TeacherWithClassTypes> teachersWithClassTypesList = teacherRepository.getTeachersWithClassTypesList();
                List<TeacherEntity> teachers = teacherRepository.getAllTeachersList(); // Get Teachers from Room - already have this line

                if (teachers != null) {
                    for (TeacherEntity teacherEntity : teachers) {
                        java.util.Map<String, Object> teacherMap = new java.util.HashMap<>();
                        teacherMap.put("name", teacherEntity.name);
                        teacherMap.put("basicInfo", teacherEntity.basicInfo);

                        // Upload TeacherClassTypeCrossRef data for each teacher
                        TeacherWithClassTypes teacherWithClassTypes = findTeacherWithClassTypes(teachersWithClassTypesList, teacherEntity.teacherId); // Helper method to find TeacherWithClassTypes
                        if (teacherWithClassTypes != null && teacherWithClassTypes.classTypes != null) {
                            List<Integer> classTypeIds = new ArrayList<>();
                            for (ClassTypeEntity classTypeEntity : teacherWithClassTypes.classTypes) {
                                classTypeIds.add(classTypeEntity.classTypeId); // Collect ClassTypeIds
                            }
                            teacherMap.put("classTypeIds", classTypeIds); // Add classTypeIds to teacherMap
                        }


                        databaseReference.child("Teachers").child(String.valueOf(teacherEntity.teacherId)).setValue(teacherMap);
                        Log.d("FirebaseViewModel", "Uploaded teacher: " + teacherEntity.teacherId + " with class types"); // Logging
                    }
                } else {
                    Log.w("FirebaseViewModel", "uploadTeachers: TeacherList is null, skipping upload"); // Warning log if null
                }
                return null;
            }
        }.execute();
    }

    private TeacherWithClassTypes findTeacherWithClassTypes(List<TeacherWithClassTypes> teachersWithClassTypesList, int teacherId) { // Helper method to find TeacherWithClassTypes by teacherId
        if (teachersWithClassTypesList != null) {
            for (TeacherWithClassTypes teacherWithClassTypes : teachersWithClassTypesList) {
                if (teacherWithClassTypes.teacher.teacherId == teacherId) {
                    return teacherWithClassTypes;
                }
            }
        }
        return null;
    }

    @SuppressLint("StaticFieldLeak")
    public void uploadClassInstances() { // New method to upload Class Instances
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                List<ClassInstanceEntity> classInstances = classInstanceRepository.getAllClassInstancesList(); // Get Class Instances from Room

                if (classInstances != null) { // Check for null list
                    for (ClassInstanceEntity classInstanceEntity : classInstances) {
                        Map<String, Object> classInstanceMap = new HashMap<>();
                        classInstanceMap.put("classId", classInstanceEntity.classId);
                        classInstanceMap.put("date", classInstanceEntity.date);
                        classInstanceMap.put("teacherId", classInstanceEntity.teacherId);
                        classInstanceMap.put("additionalComments", classInstanceEntity.additionalComments);

                        databaseReference.child("ClassInstances").child(String.valueOf(classInstanceEntity.instanceId)).setValue(classInstanceMap);
                        Log.d("FirebaseViewModel", "Uploaded class instance: " + classInstanceEntity.instanceId); // Logging
                    }
                } else {
                    Log.w("FirebaseViewModel", "uploadClassInstances: ClassInstanceList is null, skipping upload"); // Warning log if null
                }
                return null;
            }
        }.execute();
    }
}