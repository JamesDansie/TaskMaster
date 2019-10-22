package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
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

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run(){
                    findViewById(R.id.submitText).setVisibility(View.INVISIBLE);
                }
            }, 1000);

        });
    }
}
