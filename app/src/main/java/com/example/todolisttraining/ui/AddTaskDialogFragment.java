package com.example.todolisttraining.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.todolisttraining.R;
import com.example.todolisttraining.viewmodel.TaskListViewModel;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


//DiaLogに関するクラス
public class AddTaskDialogFragment  extends DialogFragment {
    private final String TAG = "AddTaskDialogFragment";
    private TaskListViewModel mTaskListViewModel;
    private final CompositeDisposable mDisposable = new CompositeDisposable();//破棄するためのクラス？

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mTaskListViewModel = new ViewModelProvider(this).get(TaskListViewModel.class);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
        mDisposable.clear();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        CharSequence[] items = {"重要","test","aaa"};
        boolean[] itemChecked = new boolean[items.length];

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //ダイアログのレイアウトを適用
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.add_task_dialog, null));


        //OKボタンの設定
        //キャンセルボタンの設定
        builder.setMessage("タスクの追加")
                .setMultiChoiceItems(items, itemChecked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "OK was clicked.");

                        EditText editText = (EditText) getDialog().findViewById(R.id.task_text);
                        if (editText != null) {
                            //タスク追加処理
                            mDisposable.add(mTaskListViewModel.insertTask(editText.getText().toString())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {},
                                            throwable -> Log.e(TAG, "Unable to update username", throwable)));
                        } else {
                            //例外
                            Log.e("", "EditText not found!");
                        }

                    }
                })
                //キャンセル処理
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
