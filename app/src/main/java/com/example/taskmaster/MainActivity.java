package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onResume(){
        super.onResume();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String username = prefs.getString("username", "Interchangable Cog");
        if(username.length() == 0){
            username = "Interchangable Cog";
        }
        TextView userTasks = findViewById(R.id.textView8);
        userTasks.setText(username +"'s tasks are;");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addTaskButton = findViewById(R.id.ButtonTaskAdd);
        addTaskButton.setOnClickListener((event) -> {
            Intent goToAddTask = new Intent(MainActivity.this, AddTask.class);
            MainActivity.this.startActivity(goToAddTask);
        });

        Button allTasksButton = findViewById(R.id.ButtonTasksAll);
        allTasksButton.setOnClickListener( (e) -> {
            Intent goToAllTasks = new Intent(MainActivity.this, AllTasks.class);
            MainActivity.this.startActivity(goToAllTasks);
        });

        Button settingsButton = findViewById(R.id.buttonSettings);
        settingsButton.setOnClickListener((event) -> {
            Intent goToSettings = new Intent(MainActivity.this, Settings.class);
            MainActivity.this.startActivity(goToSettings);
        });

        Button task1Button = findViewById(R.id.task1button);
        task1Button.setOnClickListener((event -> {
            Intent goToDetail = new Intent(MainActivity.this, Detail.class);
            goToDetail.putExtra("taskName", task1Button.getText());
            MainActivity.this.startActivity(goToDetail);
        }));

        Button task2Button = findViewById(R.id.task2button);
        task2Button.setOnClickListener((event -> {
            Intent goToDetail = new Intent(MainActivity.this, Detail.class);
            goToDetail.putExtra("taskName", task2Button.getText());
            MainActivity.this.startActivity(goToDetail);
        }));

        Button task3Button = findViewById(R.id.task3button);
        task3Button.setOnClickListener((event -> {
            Intent goToDetail = new Intent(MainActivity.this, Detail.class);
            goToDetail.putExtra("taskName", task3Button.getText());
            MainActivity.this.startActivity(goToDetail);
        }));
    }
}
