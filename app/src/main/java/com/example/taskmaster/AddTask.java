package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

            db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "tasks").allowMainThreadQueries().build();

            TextView titleView = findViewById(R.id.AddTaskTitle);
            String taskTitle = titleView.getText().toString();

            TextView descView = findViewById(R.id.AddTaskDescription);
            String taskDesc = descView.getText().toString();

            Task newTask = new Task(taskTitle, taskDesc);

            List<Task> tasks = db.taskDao().getAll();
            HashSet<String> titles = new HashSet<>();

            for(Task task : tasks){
                titles.add(task.getTitle());
            }

            if(!titles.contains(newTask.getTitle())){
                db.taskDao().addTask(newTask);
                InternetTask internetTask = new InternetTask(newTask);

                Gson gson = new Gson();
                String json = gson.toJson(internetTask);

                final MediaType JSON
                        = MediaType.get("application/json; charset=utf-8");

                OkHttpClient client = new OkHttpClient();
                    Log.i("PostUrl",getString(R.string.backend_url) + "/tasks");

                    RequestBody body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("title", newTask.getTitle())
                            .addFormDataPart("body", newTask.getDescription())
                            .build();
                    Request request = new Request.Builder()
                            .url(getString(R.string.backend_url) + "/tasks")
                            .post(body)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.e("PostPain",e.getMessage());
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                        }
                    });

            }


        });
    }
}
