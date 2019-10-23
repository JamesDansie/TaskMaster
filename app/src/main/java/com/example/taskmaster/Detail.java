package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class Detail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String taskName = getIntent().getStringExtra("taskName");
        TextView tasktitle = findViewById(R.id.DetailTaskName);
        tasktitle.setText(taskName);
    }
}
