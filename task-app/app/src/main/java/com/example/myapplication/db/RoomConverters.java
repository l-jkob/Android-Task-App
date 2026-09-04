package com.example.myapplication.db;

import androidx.room.TypeConverter;
import java.util.ArrayList;
import java.util.Collections;

public class RoomConverters {



    @TypeConverter
    public static String fromArrayListToString(ArrayList<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return String.join(",", list);
    }

    @TypeConverter
    public static ArrayList<String> fromStringToArrayList(String value) {
        if (value == null || value.isEmpty()) {
            return new ArrayList<>();
        }
        String[] parts = value.split(",");
        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, parts);
        return list;
    }
}
