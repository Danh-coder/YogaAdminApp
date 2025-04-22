package com.example.yogaadmin_uioptimized.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Transaction;
import androidx.room.Delete;


import com.example.yogaadmin_uioptimized.ClassInstanceItem;
import com.example.yogaadmin_uioptimized.data.model.ClassInstanceEntity;

import java.util.List;

@Dao
public interface ClassInstanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ClassInstanceEntity classInstance);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ClassInstanceEntity> classInstances);

    @Query("SELECT * FROM class_instances")
    LiveData<List<ClassInstanceEntity>> getAllClassInstances();

    @Query("SELECT * FROM class_instances")
    List<ClassInstanceEntity> getAllClassInstancesList();

    @Query("SELECT * FROM class_instances WHERE classId = :classId")
    LiveData<List<ClassInstanceEntity>> getClassInstancesByClassId(int classId);

    @Transaction
    @Query("SELECT ci.instanceId, ci.classId, ci.date, ci.teacherId, ci.additionalComments, c.description AS classDescription, t.name AS teacherName " +
            "FROM class_instances ci " +
            "INNER JOIN classes c ON ci.classId = c.classId " +
            "INNER JOIN teachers t ON ci.teacherId = t.teacherId")
    LiveData<List<ClassInstanceItem>> getAllClassInstanceItems();

    @Query("SELECT * FROM class_instances WHERE instanceId = :instanceId")
    LiveData<ClassInstanceEntity> getClassInstanceById(int instanceId);

    @Query("SELECT ci.* FROM class_instances ci INNER JOIN teachers t ON ci.teacherId = t.teacherId WHERE LOWER(t.name) LIKE '%' || LOWER(:teacherName) || '%'")
    List<ClassInstanceEntity> searchByTeacherName(String teacherName);

    @Query("SELECT ci.* FROM class_instances ci INNER JOIN classes c ON ci.classId = c.classId WHERE LOWER(c.daysOfWeek) LIKE '%' || LOWER(:dayOfWeek) || '%'")
    List<ClassInstanceEntity> searchByDayOfWeek(String dayOfWeek);

    @Query("SELECT * FROM class_instances WHERE date = :date")
    List<ClassInstanceEntity> searchByDate(String date);

    @Update
    void update(ClassInstanceEntity classInstanceEntity);

    @Delete
    void delete(ClassInstanceEntity classInstanceEntity);
}