package com.example.todolisttraining.db;


import android.app.Application;
import android.util.Log;

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
    private List<TaskEntity> mTasks = new ArrayList<>();
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
                    mTasks = tasksFromDB;
                    mTasks.add(task);

                    //firebaseを更新する
                    firebaseManager.uploadTasks(mTasks)
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
    public Completable deleteTask(int position) {

        Log.d(TAG,"deleteTask");

        mTaskDAO.getAllSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(tasksFromDB -> {
                            mTasks = tasksFromDB;


                            String uuid = "";
                            //削除されてないタスクを取り出す
                            for(TaskEntity taskEntity:mTasks){
                                if(!taskEntity.isDelete()){
                                    taskEntities.add(taskEntity);
                                }
                            }

                            Log.d(TAG,taskEntities.size() + "taskEntities.size() ");
                            //削除するタスクを指定して
                            for(TaskEntity mTask:mTasks){
                                //DeleteフラグがFalaseのListの中で該当したUUIDと
                                //全てのListにあるUUIDが一致したとき
                                //削除フラグをtrueにする
                                if(taskEntities.get(position).getUUId() == mTask.getUUId()){
                                    mTask.setDelete(true);
                                    mTasks.set(mTask.getId(),mTask);
                                    Log.d(TAG,mTask.getId() +"mTaskId");
                                }
                            }

                            Log.d(TAG,taskEntities.get(position).isDelete() + "taskEntities.get(position).isDelete() ");
                            Log.d(TAG,taskEntities.get(position).getId() + "id");
                            Log.d(TAG,taskEntities.get(position).getUUId() + " uuid");
                            Log.d(TAG,uuid);

                            Log.d(TAG,mTasks.get(3).isDelete() + ": mTasks : taskEntities.get(position).isDelete() ");
                            Log.d(TAG,mTasks.get(3).getId() + ": mTasks : id");
                            Log.d(TAG,mTasks.get(3).getUUId() + ": mTasks : uuid");
                            Log.d(TAG,uuid);

                            Log.d(TAG, mTasks.size() + "first");

                            //firebaseを更新する
                    firebaseManager.uploadTasks(mTasks)
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io())
                            .subscribe(() -> {
                                //アップロード完了時の処理
                                Log.d(TAG,"firebaseUploadTasks");
                            });
                        });

        try{
            Thread.sleep(1000);
            Log.d(TAG,position + "position");
            return mTaskDAO.update(mTasks.get(taskEntities.get(position).getId()));
        }catch(InterruptedException e){
            return null;
        }

    }


//    //Data比較
//    public void compareData(){
//        firebaseManager.fetchTasks()
//            .subscribeOn(Schedulers.io())
//            .observeOn(Schedulers.io())
//            .subscribe(tasksFromFirestore -> {
//                mTaskDAO.getAllSingle()
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(Schedulers.io())
//                    .subscribe(tasksFromDB -> {},
//                            throwable -> Log.e(TAG, "Unable to get username", throwable));
//                                //tasksFromFirestoreとtasksFromDBの比較処理
//                                //DBまたはfirestoreの更新
//                        },
//                        throwable -> Log.e(TAG, "Unable to get username", throwable));
//
//    }

}
