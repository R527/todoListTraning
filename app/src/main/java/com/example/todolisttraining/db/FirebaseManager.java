package com.example.todolisttraining.db;


import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

//Firebase全般扱う
public class FirebaseManager {
    private final String TAG = "FirebaseManager";
    FirebaseFirestore mFirestore;
    private static final String FIRESTORE_COLLECTION_PATH = "tasks";

    public FirebaseManager() {
        this.mFirestore = FirebaseFirestore.getInstance();
    }


    //taskを取得するメソッド
    public Single<List<TaskEntity>> fetchTasks() {
        return Single.create((sub) -> {
            mFirestore.collection(FIRESTORE_COLLECTION_PATH)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List r = new ArrayList<String>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                TaskEntity t = new TaskEntity();
                                t.setId(0);
                                t.setText((String) document.getData().get("text"));
                                //t.setImportant((Boolean) document.getData().get("isImportant"));

                                t.setDelete((Boolean) document.getData().get("isDelete"));
                                t.setUUId((String) document.getData().get("uuid"));
                                r.add(t);
                            }
                            sub.onSuccess(r);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            sub.onError(task.getException());
                        }
                    });
        });
    }


    //Taskをアップロードする
    public Completable uploadTasks(List<TaskEntity> tasks) {
        Log.d(TAG,  "uploadTasks");
        Log.d(TAG, String.valueOf(tasks.size()));
        return Completable.create((sub) -> {
            mFirestore.runTransaction((Transaction.Function<Void>) transaction -> {
                for (TaskEntity task : tasks) {
                    DocumentReference dr = mFirestore.collection(FIRESTORE_COLLECTION_PATH).document(task.getUUId());
                    Map<String, Object> docData = new HashMap<>();
                    Log.d(TAG,task.isDelete() + "deleteFlag" + task.getText());
                    Log.d(TAG, String.valueOf(tasks.size()));
                    docData.put("id", task.getId());
                    docData.put("text", task.getText());
                    //docData.put("isImportant", task.isImportant());
                    docData.put("isDelete", task.isDelete());
                    docData.put("uuid", task.getUUId());
                    transaction.set(dr, docData);
                }

                // Success
                return null;
            }).addOnSuccessListener(aVoid -> Log.d(TAG, "Transaction success!"))
                    .addOnFailureListener(e -> Log.w(TAG, "Transaction failure.", e));
        });
    }

    //タスクを取得してデータを返す
//    public List<TaskEntity> getFirebaseData() {
//
//        Log.d(TAG,"getFirebaseData");
//        List<TaskEntity> taskEntities = new ArrayList<>();
//        //task内容を取得する
//        firebaseFirestore.collection("tasks")
//                .get()
//                .addOnCompleteListener(task -> {
//
//                        if(task.isSuccessful()){
//                            for(QueryDocumentSnapshot document : task.getResult()){
//                                Log.d(TAG,document.getId() + " => " + document.getData());
//                                Log.d(TAG,document.get("UUID").toString());
//                                Log.d(TAG,document.get("task").toString());
//                                Log.d(TAG,document.get("isDelete").toString());
//
//                                //task情報をtaskEntity化しListに追加
//                                taskEntities.add(setTaskEntities(
//                                        document.get("UUID").toString(),
//                                        document.get("task").toString(),
//                                        Boolean.valueOf(document.get("isDelete").toString())));
//                            }
//                        }else {
//                            Log.w(TAG,"Error getting documents.",task.getException());
//                        }
//                });
//        return taskEntities;
//    }
//
//    //新しいタスク情報をFirebaseに追加する
//    //Task内容　＝　text
//    //uuid = UUIDクラスを取得;
//    public void addFirebaseData(String text,String uuid) {
//
//        Log.d(TAG,"addFirebaseData");
//        Map<String,Object> tasks = new HashMap<>();
//
//        //task情報をセットする
//        tasks.put("UUID",uuid);
//        tasks.put("task",text);
//        tasks.put("isDelete",false);
//
//        //Firebaseを更新する
//        firebaseFirestore.collection("tasks")
//                .add(tasks)
//                .addOnSuccessListener(documentReference ->{
//                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                })
//                .addOnFailureListener(e -> {
//                    Log.w(TAG,"Error adding document",e);
//                });
//    }
//
//    //List＜TaskEntity＞化するためのメソッド
//    //
//    private TaskEntity setTaskEntities(String uuid,String task,Boolean isDelete){
//        TaskEntity taskEntity = new TaskEntity();
//
//        taskEntity.setUUId(uuid);
//        taskEntity.setText(task);
//        taskEntity.setDelete(isDelete);
//
//        return  taskEntity;
//    }
}
