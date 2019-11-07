package com.example.taskmaster;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Task.class}, version = 5)
public abstract class AppDatabase  extends RoomDatabase {
    public abstract TaskDao taskDao();
}
