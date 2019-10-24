package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class Detail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String taskName = getIntent().getStringExtra("taskTitle");
        TextView tasktitle = findViewById(R.id.DetailTaskName);
        tasktitle.setText(taskName);

        String taskDescription = getIntent().getStringExtra("taskDescription");
        TextView taskDesc =  findViewById(R.id.taskDescriptionDetailText);
        taskDesc.setText(taskDescription);
    }
}
