package com.example.todolisttraining.db;


import android.app.Application;
import android.util.Log;

import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class TaskRepository {
    private final String TAG = "TaskRepository";
    private TaskDAO mTaskDAO;
    private FirebaseManager firebaseManager;
    //private List<TaskEntity> mTasks = new ArrayList<>();
    private List<TaskEntity> taskEntities = new ArrayList<>();


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
                    tasksFromDB.add(task);

                    //firebaseを更新する
                    firebaseManager.uploadTasks(tasksFromDB)
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io())
                            .subscribe(() -> {
                                //アップロード完了時の処理
                                Log.d(TAG,"firebaseUploadTasks");
                            });
                });

        //RoomDatabaseに登録
        try{
            Thread.sleep(1000);
            return mTaskDAO.insert(task);
        }catch(InterruptedException e){
            return null;
        }

    }


    //Task削除処理
    //表記上削除するがRoomからは削除せずにisDeleteフラグをtrueにして表示しないよう
    public Completable deleteTask(int entityPos) {

        Log.d(TAG, "deleteTask");

        return mTaskDAO.getAllSingle()
                .flatMapCompletable(tasksFromDB -> Completable.create((emitter) -> {
                    List<TaskEntity> activeTasks = new ArrayList<>();

                    String uuid = "";
                    TaskEntity deleteTask = null;
                    //削除されてないタスクを取り出す
                    for (TaskEntity taskEntity : tasksFromDB) {
                        if (!taskEntity.isDelete()) {
                            activeTasks.add(taskEntity);
                        }
                    }

                    Log.d(TAG, activeTasks.size() + "taskEntities.size() ");
                    //削除するタスクを指定して
                    for (TaskEntity task : tasksFromDB) {
                        //DeleteフラグがFalaseのListの中で該当したUUIDと
                        //全てのListにあるUUIDが一致したとき
                        //削除フラグをtrueにする
                        if (activeTasks.get(entityPos).getUUId().equals(task.getUUId())) {
                            task.setDelete(true);
                            deleteTask = task;
                            break;
                        }
                    }

//                    Log.d(TAG, activeTasks.get(position).isDelete() + "activeTasks.get(position).isDelete() ");
//                    Log.d(TAG, activeTasks.get(position).getId() + "id");
//                    Log.d(TAG, activeTasks.get(position).getUUId() + " uuid");

                    Log.d(TAG, tasksFromDB.size() + "first");

                    firebaseManager.uploadTasks(tasksFromDB)
                            .observeOn(Schedulers.io())
                            .andThen(mTaskDAO.update(deleteTask))
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io())
                            .subscribe(() -> {
                                emitter.onComplete();
                            });
                }));
    }


}
