package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskInteractionListener{
    //TODO: I would like to replace ll with a hashtable, but it breaks... everything :(
    private List<Task> tasks;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public AppDatabase db;

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


        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "tasks")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();

        this.tasks = new LinkedList<>();
        this.tasks.addAll(db.taskDao().getAll());

        // ************ Recycle section *****************

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new TaskAdapter(this.tasks, this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(getString(R.string.backend_url) + "/tasks") // backend_url + "/tasks";
                .build();

        client.newCall(request).enqueue(new LogTasksCallback(this));

        //****************** Buttons ****************

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

    }

    @Override
    public void potato(Task task) {
        Intent goToDetailIntent = new Intent(this, Detail.class);

        goToDetailIntent.putExtra("taskTitle", task.getTitle());
        goToDetailIntent.putExtra("taskDescription", task.getDescription());

        MainActivity.this.startActivity(goToDetailIntent);
    }

    public void putDataOnPage(String data){
//        TextView textView = findViewById(R.id.textView);
//        textView.setText(data);

        //Turning JSON into InternetTasks
        Gson gson = new Gson();
        InternetTask[] incomingArr = gson.fromJson(data, InternetTask[].class);

        //Getting a set of existing titles
        HashSet<String> titles = new HashSet<>();
        for(Task task : this.tasks){
            titles.add(task.getTitle());
        }

        for(InternetTask internetTask: incomingArr){
            //if the title is a new title then add it
            if(!titles.contains(internetTask.getTitle())){
                titles.add(internetTask.getTitle());
                Task newTask = new Task(internetTask);
                tasks.add(newTask);
                db.taskDao().addTask(newTask);
            }
        }
    }
}

class LogTasksCallback implements Callback {
    MainActivity currentMainActivityInstance;

    public LogTasksCallback(MainActivity currentMainActivityInstance){
        this.currentMainActivityInstance = currentMainActivityInstance;
    }

    private static final String TAG = "Callback";

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
        Log.e(TAG, "internet error");
        Log.e(TAG, e.getMessage());
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        String responseBody = response.body().string();
        Log.i(TAG, responseBody);

        Handler handlerForMainThread = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessageToMain){
                currentMainActivityInstance.putDataOnPage((String)inputMessageToMain.obj);
            }
        };
        Message completeMessage = handlerForMainThread.obtainMessage(0, responseBody);
        completeMessage.sendToTarget();
    }
}