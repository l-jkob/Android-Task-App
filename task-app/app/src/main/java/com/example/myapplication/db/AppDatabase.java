package com.example.myapplication.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;
import com.example.myapplication.model.Todo;

@Database(entities = {Todo.class}, version = 2, exportSchema = false)
@TypeConverters(RoomConverters.class)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase sInstance;

    public abstract RoomTodoDAO todoDao();

    public static AppDatabase getInstance(final Context aContext) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(
                            aContext.getApplicationContext(),
                            AppDatabase.class,
                            "todo-database"
                    )
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return sInstance;
    }
}
