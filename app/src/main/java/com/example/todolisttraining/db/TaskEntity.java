package com.example.todolisttraining.db;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


//データベースに保存する変数を用意
@Entity(tableName = "tasks")
public class TaskEntity {

    //自動生成true
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "uuid")
    public String uuid;

    @ColumnInfo(name = "text")
    public String text;

    @ColumnInfo(name = "isDelete")
    public boolean isDelete;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUUId() {
        return uuid;
    }

    public void setUUId(String uuid) {
        this.uuid = uuid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }
}
