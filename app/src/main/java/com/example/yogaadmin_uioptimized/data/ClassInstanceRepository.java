package com.example.yogaadmin_uioptimized.data;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.yogaadmin_uioptimized.ClassInstanceItem;
import com.example.yogaadmin_uioptimized.data.model.ClassInstanceEntity;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClassInstanceRepository {
    private ClassInstanceDao classInstanceDao;
    private LiveData<List<ClassInstanceEntity>> allClassInstances;
    private LiveData<List<ClassInstanceItem>> allClassInstanceItems;

    public ClassInstanceRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        classInstanceDao = db.classInstanceDao();
        allClassInstances = classInstanceDao.getAllClassInstances();
        allClassInstanceItems = classInstanceDao.getAllClassInstanceItems();
    }

    public LiveData<List<ClassInstanceEntity>> getAllClassInstances() {
        return allClassInstances;
    }

    public List<ClassInstanceEntity> getAllClassInstancesList() {
        return classInstanceDao.getAllClassInstancesList();
    }

    public LiveData<ClassInstanceEntity> getClassInstanceById(int instanceId) {
        return classInstanceDao.getClassInstanceById(instanceId);
    }

    public LiveData<List<ClassInstanceItem>> getAllClassInstanceItems() {
        return allClassInstanceItems;
    }

    public LiveData<List<ClassInstanceEntity>> getClassInstancesByClassId(int classId) {
        return classInstanceDao.getClassInstancesByClassId(classId);
    }

    public void insert(ClassInstanceEntity classInstanceEntity) {
        classInstanceDao.insert(classInstanceEntity);
    }

    public void update(ClassInstanceEntity classInstanceEntity) {
        classInstanceDao.update(classInstanceEntity);
    }

    public void delete(ClassInstanceEntity classInstanceEntity) {
        Log.d("ClassInstanceRepository", "Deleting class instance: " + classInstanceEntity);
        classInstanceDao.delete(classInstanceEntity);
    }

    public void insertClassInstances(List<ClassInstanceEntity> classInstances) {
        new InsertClassInstancesAsyncTask(classInstanceDao).execute(classInstances);
    }

    public List<ClassInstanceEntity> searchByTeacherName(String teacherName) {
        return classInstanceDao.searchByTeacherName(teacherName);
    }

    public List<ClassInstanceEntity> searchByDayOfWeek(String dayOfWeek) {
//        List<ClassInstanceEntity> allInstances = classInstanceDao.searchByDayOfWeek(dayOfWeek);
        List<ClassInstanceEntity> allInstances = classInstanceDao.getAllClassInstancesList();
        List<ClassInstanceEntity> filteredInstances = new ArrayList<>();

        for (ClassInstanceEntity instance : allInstances) {
            String instanceDateString = instance.date;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate instanceDate = LocalDate.parse(instanceDateString, formatter);
                DayOfWeek instanceDayOfWeek = instanceDate.getDayOfWeek();
                String instanceDayOfWeekString = instanceDayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault());

                if (instanceDayOfWeekString.equals(dayOfWeek)) {
                    filteredInstances.add(instance);
                }
            }
        }
        return filteredInstances;
    }

    public List<ClassInstanceEntity> searchByDate(String date) {
        return classInstanceDao.searchByDate(date);
    }

    private static class InsertClassInstancesAsyncTask extends AsyncTask<List<ClassInstanceEntity>, Void, Void> {
        private ClassInstanceDao classInstanceDao;

        InsertClassInstancesAsyncTask(ClassInstanceDao classInstanceDao) {
            this.classInstanceDao = classInstanceDao;
        }

        @Override
        protected Void doInBackground(List<ClassInstanceEntity>... lists) {
            classInstanceDao.insertAll(lists[0]);
            return null;
        }
    }
}