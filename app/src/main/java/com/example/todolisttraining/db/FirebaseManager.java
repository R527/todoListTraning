package com.example.todolisttraining.db;


import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Firebase全般扱う
public class FirebaseManager {
    private final String TAG = "FirebaseManager";
    FirebaseFirestore firebaseFirestore;

    public FirebaseManager(FirebaseFirestore firebaseFirestore) {
        this.firebaseFirestore = firebaseFirestore;
    }


    //タスクを取得してデータを返す
    public List<TaskEntity> getFirebaseData() {

        Log.d(TAG,"getFirebaseData");
        firebaseFirestore = FirebaseFirestore.getInstance();
        List<TaskEntity> taskEntities = null;
        //task内容を取得する
        firebaseFirestore.collection("tasks")
                .get()
                .addOnCompleteListener(task -> {

                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Log.d(TAG,document.getId() + " => " + document.getData());
                                Log.d(TAG,document.get("UUID").toString());
                                Log.d(TAG,document.get("task").toString());
                                Log.d(TAG,document.get("isDelete").toString());

                                //task情報をtaskEntity化しListに追加
                                taskEntities.add(setEntities(
                                        document.get("UUID").toString(),
                                        document.get("task").toString(),
                                        Boolean.valueOf(document.get("isDelete").toString())));
                            }
                        }else {
                            Log.w(TAG,"Error getting documents.",task.getException());
                        }
                });
        return taskEntities;
    }

    //新しいタスク情報をFirebaseに追加する
    //Task内容　＝　text
    //uuid = UUIDクラスを取得;
    public void addFirebaseData(String text,String uuid) {

        Log.d(TAG,"addFirebaseData");
        firebaseFirestore = FirebaseFirestore.getInstance();
        Map<String,Object> tasks = new HashMap<>();

        //task情報をセットする
        tasks.put("UUID",uuid);
        tasks.put("task",text);
        tasks.put("isDelete",false);

        //Firebaseを更新する
        firebaseFirestore.collection("tasks")
                .add(tasks)
                .addOnSuccessListener(documentReference ->{
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG,"Error adding document",e);
                });
    }

    //List＜TaskEntity＞化するためのメソッド
    //
    private TaskEntity setEntities(String uuid,String task,Boolean isDelete){
        TaskEntity taskEntity = new TaskEntity();

        taskEntity.setUUId(uuid);
        taskEntity.setText(task);
        taskEntity.setDelete(isDelete);

        return  taskEntity;
    }
}
