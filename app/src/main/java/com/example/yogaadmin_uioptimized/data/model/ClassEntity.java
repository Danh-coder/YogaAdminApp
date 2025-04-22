package com.example.yogaadmin_uioptimized.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.yogaadmin_uioptimized.Converters;

@Entity(tableName = "classes",
        foreignKeys = {
                @ForeignKey(entity = ClassTypeEntity.class,
                        parentColumns = "classTypeId",
                        childColumns = "classTypeId",
                        onDelete = ForeignKey.SET_NULL)
        })
public class ClassEntity {
    @PrimaryKey(autoGenerate = true)
    public int classId;

    @TypeConverters(Converters.class)
    public String daysOfWeek;

    public String time;
    public int capacity;
    public int duration;
    public double price;
    public int classTypeId;
    public String description;
    public String level;
    public String room;

    public ClassEntity(String daysOfWeek, String time, int capacity, int duration, double price, int classTypeId, String description, String level, String room) {
        this.daysOfWeek = daysOfWeek;
        this.time = time;
        this.capacity = capacity;
        this.duration = duration;
        this.price = price;
        this.classTypeId = classTypeId;
        this.description = description;
        this.level = level;
        this.room = room;
    }
}