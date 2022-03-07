package com.example.todolisttraining.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


//データベースに保存する変数を用意
@Entity(tableName = "tasks")
public class TaskEntity {

    //自動生成true
  @PrimaryKey(autoGenerate = true)
    public int id;


    @ColumnInfo(name = "first_name")
    public String text;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
