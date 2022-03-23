package com.example.todolisttraining.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;



//データベースを取り扱うクラス

@Database(entities = {TaskEntity.class}, version = 8, exportSchema = false)
public abstract class AppDatabase  extends RoomDatabase {

    private static AppDatabase sInstance;
    public static final String DATABASE_NAME = "mydb";

    public abstract TaskDAO taskDAO();

    public static AppDatabase getInstance(final Context context) {
        //二つ以上あると問題だから制御する
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(
                            context,
                            AppDatabase.class,
                            DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return sInstance;
    }
}
