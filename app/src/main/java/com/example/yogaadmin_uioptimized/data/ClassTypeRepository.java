package com.example.yogaadmin_uioptimized.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.yogaadmin_uioptimized.data.model.ClassTypeEntity;

import java.util.List;

public class ClassTypeRepository {
    private ClassTypeDao classTypeDao;

    public ClassTypeRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        classTypeDao = db.classTypeDao();
    }

    public LiveData<List<ClassTypeEntity>> getAllClassTypes() {
        return classTypeDao.getAllClassTypes();
    }

    public List<ClassTypeEntity> getAllClassTypesList() {
        return classTypeDao.getAllClassTypesList();
    }

    public void insertClassType(ClassTypeEntity classTypeEntity) {
        classTypeDao.insert(classTypeEntity);
    }

    public void updateClassType(ClassTypeEntity classTypeEntity) {
        classTypeDao.update(classTypeEntity);
    }

    public void deleteClassType(ClassTypeEntity classTypeEntity) {
        classTypeDao.delete(classTypeEntity);
    }

    public LiveData<ClassTypeEntity> getClassTypeById(int classTypeId) {
        return classTypeDao.getClassTypeById(classTypeId);
    }

    public ClassTypeEntity getClassTypeByIdSync(int classTypeId) {
        return classTypeDao.getClassTypeByIdSync(classTypeId);
    }
}