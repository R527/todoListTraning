package com.example.todolisttraining.db;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;

import com.example.todolisttraining.AppComponent;
import com.example.todolisttraining.ui.TaskListFragment;
import com.example.todolisttraining.viewmodel.TaskListViewModel;

import java.util.List;
import java.util.UUID;
import io.reactivex.rxjava3.core.Completable;


public class TaskRepository extends AndroidViewModel {
    private TaskDAO mTaskDAO;
    private FirebaseManager firebaseManager = new FirebaseManager();
    private TaskListViewModel taskListViewModel = new TaskListViewModel(getApplication());

    public TaskRepository(@NonNull Application application) {
        super(application);
        mTaskDAO = ((AppComponent)application).getDatabase().taskDAO();
    }


    public TaskEntity getAllData(){
        return  null;
    }

    public Completable insertTask(String text){
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
        compareData();

        //RoomDatabaseに登録
        //todo mTaskDaoが取得方法は合っているのか
        return mTaskDAO.insert(task);
    }


    //Task削除処理
    //表記上削除するがRoomからは削除せずにisDeleteフラグをtrueにして表示しないよう処理する
    public Completable deleteTask(int position) {

        List<TaskEntity> list = taskListViewModel.mTasks;
        list.get(position).setDelete(true);

        return mTaskDAO.update(list.get(position));
    }


    //Data比較
    public void compareData(){
        firebaseManager.getFirebaseData();
        mTaskDAO.getAll();

    }

}
