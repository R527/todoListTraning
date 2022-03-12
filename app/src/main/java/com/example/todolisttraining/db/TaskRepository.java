package com.example.todolisttraining.db;


import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.todolisttraining.AppComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class TaskRepository {
    private final String TAG = "TaskRepository";
    private TaskDAO mTaskDAO;
    private FirebaseManager firebaseManager;
    private List<TaskEntity> mTasks = new ArrayList<>();

    public TaskRepository(Application applicationContext) {
        AppDatabase db = AppDatabase.getInstance(applicationContext);
        this.mTaskDAO = db.taskDAO();

        this.firebaseManager = new FirebaseManager();
    }

    public Single<List<TaskEntity>> getAllRoomData(){
        return  mTaskDAO.getAllSingle();
    }

    public Completable insertTask(String text){

        Log.d(TAG,"insertTask");

        //UUID生成
        String uuid = UUID.randomUUID().toString();

        //Entityに登録
        TaskEntity task = new TaskEntity();
        task.setText(text);
        task.setUUId(uuid);
        task.setDelete(false);

        mTaskDAO.getAllSingle()
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(tasksFromDB -> {
                    mTasks = tasksFromDB;
                    mTasks.add(task);
                });

        Log.d(TAG, String.valueOf(mTasks.size()));
        if(mTasks.size() != 0){
            Log.d(TAG,mTasks.get(0).getText());
        }

        //firebaseに登録する処理
        firebaseManager.uploadTasks(mTasks)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(() -> {
                    //アップロード完了時の処理
                    Log.d(TAG,"firebaseUploadTasks");
                });



        //Data比較
        compareData();

        //RoomDatabaseに登録
        return mTaskDAO.insert(task);
    }


    //Task削除処理
    //表記上削除するがRoomからは削除せずにisDeleteフラグをtrueにして表示しないよう処理する
    public Completable deleteTask(int position) {
//        Flowable<List<TaskEntity>> list = getAllRoomData();
        List<TaskEntity> list2 = new ArrayList<>();

        //mTaskDAO.update(list.get(position));
        return null;
    }


    //Data比較
    public void compareData(){
        firebaseManager.fetchTasks()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(tasksFromFirestore -> {
                mTaskDAO.getAllSingle()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(tasksFromDB -> {},
                            throwable -> Log.e(TAG, "Unable to get username", throwable));
                                //tasksFromFirestoreとtasksFromDBの比較処理
                                //DBまたはfirestoreの更新
                        },
                        throwable -> Log.e(TAG, "Unable to get username", throwable));

    }

}
