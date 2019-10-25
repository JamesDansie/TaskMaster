package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AddTask extends AppCompatActivity {
    public AppDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Button submitTask = findViewById(R.id.AddTask);
        submitTask.setOnClickListener((event) -> {
            findViewById(R.id.submitText).setVisibility(View.VISIBLE);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run(){
                    findViewById(R.id.submitText).setVisibility(View.INVISIBLE);
                }
            }, 1000);


            TextView titleView = findViewById(R.id.AddTaskTitle);
            String taskTitle = titleView.getText().toString();

            TextView descView = findViewById(R.id.AddTaskDescription);
            String taskDesc = descView.getText().toString();

            Task newTask = new Task(taskTitle, taskDesc);

            db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "tasks").allowMainThreadQueries().build();
            db.taskDao().addTask(newTask);

        });
    }
}
