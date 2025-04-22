package com.example.yogaadmin_uioptimized.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.yogaadmin_uioptimized.data.model.ClassEntity;

import java.util.List;

public class ClassRepository {
    private ClassDao classDao;
    private LiveData<List<ClassEntity>> allClasses;

    public ClassRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        classDao = db.classDao();
        allClasses = classDao.getAllClasses();
    }

    public LiveData<List<ClassEntity>> getAllClasses() {
        return classDao.getAllClasses();
    }

    public List<ClassEntity> getAllClassesList() {
        Log.d("ClassRepository", "getAllClassesList: " + classDao.getAllClassesList());
        return classDao.getAllClassesList();
    }

    public ClassEntity getClassById(int classId) {
        return classDao.getClassById(classId);
    }

    public LiveData<ClassEntity> getClassByIdLiveData(int classId) {
        return classDao.getClassByIdLiveData(classId);
    }

    public void insertClass(ClassEntity classEntity) {
        classDao.insert(classEntity);
    }

    public void update(ClassEntity classEntity) {
        classDao.update(classEntity);
    }

    public void delete(ClassEntity classEntity) {
        classDao.delete(classEntity.classId);
    }
}