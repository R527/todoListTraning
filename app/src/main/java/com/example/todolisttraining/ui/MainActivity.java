package com.example.todolisttraining.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todolisttraining.R;
import com.example.todolisttraining.db.FirebaseManager;
import com.example.todolisttraining.db.TaskRepository;
import com.example.todolisttraining.ui.AddTaskDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private FirebaseManager firebaseManager = new FirebaseManager();
    private TaskRepository taskRepository = new TaskRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setUp処理
        firebaseManager.setUpFirebaseManager();

        //タスク追加ボタン
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.add_task_button);

        //DiaLogを表示するボタン設定
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AddTaskDialogFragment dialog = new AddTaskDialogFragment();
                dialog.show(getSupportFragmentManager(), "AddTaskDialogFragment");
            }
        });


    }

}