package com.example.yogaadmin_uioptimized.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.yogaadmin_uioptimized.data.model.ClassTypeEntity;

import java.util.List;

@Dao
public interface ClassTypeDao {
    @Insert
    void insert(ClassTypeEntity classTypeEntity);

    @Update
    void update(ClassTypeEntity classTypeEntity);

    @Delete
    void delete(ClassTypeEntity classTypeEntity);

    @Query("SELECT * FROM class_types")
    LiveData<List<ClassTypeEntity>> getAllClassTypes();

    @Query("SELECT * FROM class_types")
    List<ClassTypeEntity> getAllClassTypesList();

    @Query("SELECT * FROM class_types WHERE classTypeId = :classTypeId")
    LiveData<ClassTypeEntity> getClassTypeById(int classTypeId);

    @Query("SELECT * FROM class_types WHERE classTypeId = :classTypeId")
    ClassTypeEntity getClassTypeByIdSync(int classTypeId);
}