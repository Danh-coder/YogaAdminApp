package com.example.yogaadmin_uioptimized.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "teachers")
public class TeacherEntity {
    @PrimaryKey(autoGenerate = true)
    public int teacherId;

    public String name;
    public String basicInfo;

    public TeacherEntity(String name, String basicInfo) {
        this.name = name;
        this.basicInfo = basicInfo;
    }
}