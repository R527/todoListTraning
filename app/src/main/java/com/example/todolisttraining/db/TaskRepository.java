package com.example.todolisttraining.db;


import android.app.Application;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;

import com.example.todolisttraining.AppComponent;
import com.example.todolisttraining.ui.TaskListFragment;
import com.example.todolisttraining.viewmodel.TaskListViewModel;

import java.util.List;
import java.util.UUID;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;


public class TaskRepository {
    private final String TAG = "TaskRepository";
    private TaskDAO mTaskDAO;
    private FirebaseManager firebaseManager;

    public TaskEntity getAllData(){
        return  null;
    }

    public TaskRepository(TaskDAO mTaskDAO, FirebaseManager firebaseManager) {
        this.mTaskDAO = mTaskDAO;
        this.firebaseManager = firebaseManager;
    }

    public Completable insertTask(String text){

        Log.d(TAG,"insertTask");

        //UUID生成
        String uuid = UUID.randomUUID().toString();

        //firebaseに登録する処理
        firebaseManager.addFirebaseData(text,uuid);

        //Entityに登録
        TaskEntity task = new TaskEntity();
        task.setText(text);
        task.setUUId(uuid);
        task.setDelete(false);

        //Data比較
        //compareData();
        Log.d(TAG,task.toString());

        //RoomDatabaseに登録
        return mTaskDAO.insert(task);
    }


    //Task削除処理
    //表記上削除するがRoomからは削除せずにisDeleteフラグをtrueにして表示しないよう処理する
    public Completable deleteTask(int position) {

        List<TaskEntity> list = (List<TaskEntity>) mTaskDAO.getAll();
        list.get(position).setDelete(true);

        return mTaskDAO.update(list.get(position));
    }


    //Data比較
    public void compareData(){
        firebaseManager.getFirebaseData();
        mTaskDAO.getAll();
    }

}
