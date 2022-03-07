package com.example.todolisttraining;

import android.content.Context;

//import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
//import androidx.room.Room;
//import androidx.test.core.app.ApplicationProvider;
//import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.todolisttraining.db.AppDatabase;
import com.example.todolisttraining.db.TaskDAO;
import com.example.todolisttraining.db.TaskEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//@RunWith(AndroidJUnit4.class)
public class SimpleEntityReadWriteTest {
    private TaskDAO taskDao;
    private AppDatabase db;

//    @Rule
//    public InstantTaskExecutorRule instantTaskExecutorRule =
//            new InstantTaskExecutorRule();
//
//    @Before
//    public void createDb() {
//        Context context = ApplicationProvider.getApplicationContext();
//        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
//        taskDao = db.taskDAO();
//    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void writeTaskAndReadInList() throws Exception {
        TaskEntity task = new TaskEntity();
        task.setId(0);
        task.setText("aaa");
        taskDao.insert(task).blockingAwait();

        taskDao.getAll()
                .test()
                .assertValue(tasks -> tasks.size() == 1);
    }

    @Test
    public void deleteTask() throws Exception {
        TaskEntity task = new TaskEntity();
        task.setId(1);
        task.setText("aaa");

        taskDao.insert(task).blockingAwait();

        taskDao.delete(task).blockingAwait();

        taskDao.getAll()
                .test()
                .assertValue(tasks -> tasks.size() == 0);

    }
}