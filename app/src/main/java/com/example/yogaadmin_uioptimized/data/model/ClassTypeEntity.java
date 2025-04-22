package com.example.yogaadmin_uioptimized.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "class_types")
public class ClassTypeEntity {
    @PrimaryKey(autoGenerate = true)
    public int classTypeId;

    public String name;
    public String description;

    public ClassTypeEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }
}