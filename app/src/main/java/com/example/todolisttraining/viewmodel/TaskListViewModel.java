package com.example.todolisttraining.viewmodel;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.todolisttraining.db.TaskDAO;
import com.example.todolisttraining.db.TaskEntity;
import com.example.todolisttraining.db.TaskRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;


/**
 * UIに表示するデータ・状態の保持
 * UIからイベントをうけとる
 * UIの変更指示
 *
 * 制約
 * mTaskの順序と表示順は一致している前提
 *
 * やらないこと
 * 個別のビューをもたないようにする
 */


public class TaskListViewModel extends AndroidViewModel {
    private final String TAG = "TaskListViewModel";
    private List<TaskEntity> mTasks;
    private TaskRepository taskRepository;

    //コンストラクター

    public TaskListViewModel(@NonNull Application application) {
        super(application);
        mTasks = new ArrayList<>();
        this.taskRepository = new TaskRepository(application);
    }

    //非同期処理対応の返り値
    //メソッド内にバージョン不足だと利用できないメソッドあるから注意書きの@RequiresApi
    @RequiresApi(api = Build.VERSION_CODES.N)
    public Single<List<String>> getTaskTextList() {
        Log.d(TAG,"getTaskTextList");
        //tasksを全取得して
        Single<List<TaskEntity>> list = taskRepository.getAllRoomData();
        List<String> strList = new ArrayList<>();

        if(list == null){
            return null;
        }else{
            return taskRepository.getAllRoomData()
                    //DatabaseにあるTasks＜List＞を取得していじる
                    .map(tasks -> {
                        mTasks = tasks;
                        Log.d(TAG, String.valueOf(mTasks.size()));

                        return tasks.stream()
                                .filter(t -> !t.isDelete())
                                .map(t -> t.getText())
                                .collect(Collectors.toList());

//                        return tasks.stream()
//                                //Stringのみを抽出
//                                //for文で回すのと同じ処理
//                                .map(task -> task.getId())
//                                .forEach(id -> tasks.get(id));


//                        for (TaskEntity item: mTasks) {
//                            if(item.isDelete() == false){
//                                strList.add(item.getText());
//                            }
//                        }
//                        return  strList;
                    });
        }

    }

    //タスクを追加する処理
    public Completable insertTask(final String text) {
        Log.d(TAG,"insertTask");
        return taskRepository.insertTask(text);
    }

    //タスク削除処理
    public Completable deleteTask(int position) {
        Log.d(TAG,"deleteTask");
        return taskRepository.deleteTask(position);
    }

    public static class TaskListViewModelFactory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;
        private TaskDAO mTaskDAO;
        public List<TaskEntity> mTasks;
        private TaskRepository taskRepository;

        public TaskListViewModelFactory(@NonNull Application application, TaskDAO mTaskDAO, List<TaskEntity> mTasks, TaskRepository taskRepository) {
            mApplication = application;
            this.mTaskDAO = mTaskDAO;
            this.mTasks = mTasks;
            this.taskRepository = taskRepository;
        }

        @SuppressWarnings("unchecked")
        @Override
        @NonNull
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new TaskListViewModel(mApplication);
        }
    }
}
