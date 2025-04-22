package com.example.yogaadmin_uioptimized;

import java.util.List;

public class DateGroupedClassInstance {
    private String date;
    private String dayOfWeek;
    private List<ClassInstanceItem> items;

    public DateGroupedClassInstance(String date, String dayOfWeek, List<ClassInstanceItem> items) {
        this.date = date;
        this.dayOfWeek = dayOfWeek;
        this.items = items;
    }

    public String getDate() {
        return date;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public List<ClassInstanceItem> getItems() {
        return items;
    }
}