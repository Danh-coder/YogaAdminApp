package com.example.yogaadmin_uioptimized;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ClassItem implements Parcelable { // Implement Parcelable
    public int classId;
    public List<String> daysOfWeek;
    public String time;
    public int capacity;
    public int duration;
    public double price;
    public int classTypeId;
    public String description;
    public String level;
    public String room;
    public String classTypeName;

    public ClassItem(int classId, List<String> daysOfWeek, String time, int capacity, int duration, double price, int classTypeId, String description, String level, String room, String classTypeName) {
        this.classId = classId;
        this.daysOfWeek = daysOfWeek;
        this.time = time;
        this.capacity = capacity;
        this.duration = duration;
        this.price = price;
        this.classTypeId = classTypeId;
        this.description = description;
        this.level = level;
        this.room = room;
        this.classTypeName = classTypeName;
    }

    protected ClassItem(Parcel in) { // Parcelable constructor
        classId = in.readInt();
        daysOfWeek = in.createStringArrayList();
        time = in.readString();
        capacity = in.readInt();
        duration = in.readInt();
        price = in.readDouble();
        classTypeId = in.readInt();
        description = in.readString();
        level = in.readString();
        room = in.readString();
        classTypeName = in.readString();
    }

    public static final Creator<ClassItem> CREATOR = new Creator<ClassItem>() { // Parcelable Creator
        @Override
        public ClassItem createFromParcel(Parcel in) {
            return new ClassItem(in);
        }

        @Override
        public ClassItem[] newArray(int size) {
            return new ClassItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) { // writeToParcel method
        dest.writeInt(classId);
        dest.writeStringList(daysOfWeek);
        dest.writeString(time);
        dest.writeInt(capacity);
        dest.writeInt(duration);
        dest.writeDouble(price);
        dest.writeInt(classTypeId);
        dest.writeString(description);
        dest.writeString(level);
        dest.writeString(room);
        dest.writeString(classTypeName);
    }
}