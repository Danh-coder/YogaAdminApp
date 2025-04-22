package com.example.yogaadmin_uioptimized;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class ClassInstanceItem implements Parcelable { // Implement Parcelable
    public int instanceId;
    public int classId;
    public String date;
    public int teacherId;
    public String additionalComments;
    public String classDescription;
    public String teacherName;
    public String classTime;

    public ClassInstanceItem(int instanceId, int classId, String date, int teacherId, String additionalComments, String classDescription, String teacherName, String classTime) {
        this.instanceId = instanceId;
        this.classId = classId;
        this.date = date;
        this.teacherId = teacherId;
        this.additionalComments = additionalComments;
        this.classDescription = classDescription;
        this.teacherName = teacherName;
        this.classTime = classTime;
    }

    protected ClassInstanceItem(Parcel in) { // Parcelable constructor
        instanceId = in.readInt();
        classId = in.readInt();
        date = in.readString();
        teacherId = in.readInt();
        additionalComments = in.readString();
        classDescription = in.readString();
        teacherName = in.readString();
        classTime = in.readString();
    }

    public static final Creator<ClassInstanceItem> CREATOR = new Creator<ClassInstanceItem>() { // Parcelable Creator
        @Override
        public ClassInstanceItem createFromParcel(Parcel in) {
            return new ClassInstanceItem(in);
        }

        @Override
        public ClassInstanceItem[] newArray(int size) {
            return new ClassInstanceItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) { // writeToParcel method
        dest.writeInt(instanceId);
        dest.writeInt(classId);
        dest.writeString(date);
        dest.writeInt(teacherId);
        dest.writeString(additionalComments);
        dest.writeString(classDescription);
        dest.writeString(teacherName);
        dest.writeString(classTime);
    }

    public String getDate() {
        return date;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public int getClassId() {
        return classId;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public String getAdditionalComments() {
        return additionalComments;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public String getClassDescription(){
        return classDescription;
    }

    public String getClassTime() { // Getter for classTime
        return classTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassInstanceItem that = (ClassInstanceItem) o;
        return instanceId == that.instanceId && classId == that.classId && Objects.equals(date, that.date) && Objects.equals(teacherName, that.teacherName) && Objects.equals(classDescription, that.classDescription) && Objects.equals(additionalComments, that.additionalComments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceId, classId, date, teacherName, classDescription, additionalComments);
    }
}