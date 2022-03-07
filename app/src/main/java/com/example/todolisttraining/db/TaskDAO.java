package com.example.todolisttraining.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;


//タスクのエンティティにアクセスする
//List取得、追加、削除
@Dao
public interface TaskDAO {

    //EntityにあるListを取得
    @Query("SELECT * FROM tasks")
    Flowable<List<TaskEntity>> getAll();

    @Insert
    Completable insert(TaskEntity tasks);

    @Delete
    Completable delete(TaskEntity task);

    @Update
    Completable update(TaskEntity task);
}
