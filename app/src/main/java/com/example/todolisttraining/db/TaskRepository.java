package com.example.todolisttraining.db;


import android.app.Application;
import android.util.Log;

import com.example.todolisttraining.ui.TaskAdapter;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class TaskRepository {
    private final String TAG = "TaskRepository";
    private TaskDAO mTaskDAO;
    private FirebaseManager mFirebaseManager;


    public TaskRepository(Application applicationContext) {
        AppDatabase db = AppDatabase.getInstance(applicationContext);
        this.mTaskDAO = db.taskDAO();

        this.mFirebaseManager = new FirebaseManager();
    }

    public Flowable<List<TaskEntity>> getAllRoomData(){
        return  mTaskDAO.getAll();
    }

    public Single<List<TaskEntity>> getAllRoomDataSingle(){
        return  mTaskDAO.getAllSingle();
    }

    public Single<List<TaskEntity>> getFirebaseList(){
        return  mFirebaseManager.fetchTasks();
    }

    public Completable insertTask(TaskEntity taskEntity){

        Log.d(TAG,"insertTask");

        //UUID生成
        String uuid = UUID.randomUUID().toString();

        //Entityに登録
        TaskEntity task = new TaskEntity();
        task.setText(taskEntity.getText());
        task.setImportant(taskEntity.isImportant);
        task.setUUId(uuid);
        task.setDelete(false);

        return  mTaskDAO.getAllSingle()
                .flatMapCompletable(tasksFromDB -> Completable.create((emitter) ->{
                            tasksFromDB.add(task);

                    //firebaseを更新する
                    mFirebaseManager.uploadTasks(tasksFromDB)
                            .observeOn(Schedulers.io())
                            .andThen(mTaskDAO.insert(task))
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io())
                            .subscribe(() -> {
                                //アップロード完了時の処理
                                Log.d(TAG,"firebaseUploadTasks");
                            });
                        }));

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
                            Log.d(TAG, activeTasks.get(entityPos).getUUId() + "entityPos.getUUId()");
                            Log.d(TAG, task.getUUId() + "task.getUUId() ");
                            task.setDelete(true);
                            deleteTask = task;
                            break;
                        }
                    }


                    mFirebaseManager.uploadTasks(tasksFromDB)
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
