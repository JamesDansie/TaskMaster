package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AddTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Button submitTask = findViewById(R.id.AddTask);
        submitTask.setOnClickListener((event) -> {
            findViewById(R.id.submitText).setVisibility(View.VISIBLE);
        });
    }
}
