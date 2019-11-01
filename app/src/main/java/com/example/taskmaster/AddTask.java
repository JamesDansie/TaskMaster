package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.CreateTaskMutation;
import com.amazonaws.amplify.generated.graphql.ListTeamsQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Nonnull;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import type.CreateTaskInput;

public class AddTask extends AppCompatActivity {
    public AppDatabase db;
    AWSAppSyncClient awsAppSyncClient;
    public String team;

    public void onRadioButtonClicked(View view){
        boolean checked  = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radioButtonBlueTeam:
                if (checked){
                    team = "blue";
                }
                break;
            case R.id.radioButtonRedTeam:
                if(checked){
                    team = "red";
                }
                break;
            case R.id.radioButtonGreenTeam:
                if(checked)
                    team = "green";
                break;
        }
        Log.i("TeamIs",team);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Build a connection to AWS
        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();


        Button submitTask = findViewById(R.id.AddTask);
        submitTask.setOnClickListener((event) -> {

            Context context = getApplicationContext();
            CharSequence text = "Submitted!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

//            db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "tasks").allowMainThreadQueries().build();

            TextView titleView = findViewById(R.id.AddTaskTitle);
            String taskTitle = titleView.getText().toString();

            TextView descView = findViewById(R.id.AddTaskDescription);
            String taskDesc = descView.getText().toString();

            //Setting up new task
            Task newTask = new Task(taskTitle, taskDesc);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String username = prefs.getString("username", "Interchangable Cog");
            newTask.setAssignedUser(username);

            // Saving the new task
            runAddTaskMutation(newTask);

//            //******************* OkHttp Stuff ****************
//            //Making a set of titles
//            List<Task> tasks = db.taskDao().getAll();
//            HashSet<String> titles = new HashSet<>();
//            for(Task task : tasks){
//                titles.add(task.getTitle());
//            }
//
//            //If the task title is unique, then add it to all the things
//            if(!titles.contains(newTask.getTitle())){
//                db.taskDao().addTask(newTask);
//
//                OkHttpClient client = new OkHttpClient();
//                String PostUrl = getString(R.string.backend_url) + "/tasks";
//                Log.i("PostUrl",PostUrl);
//
//                RequestBody body = new MultipartBody.Builder()
//                        .setType(MultipartBody.FORM)
//                        .addFormDataPart("title", newTask.getTitle())
//                        .addFormDataPart("body", newTask.getDescription())
//                        .build();
//                Request request = new Request.Builder()
//                        .url(PostUrl)
//                        .post(body)
//                        .build();
//                client.newCall(request).enqueue(new Callback() {
//                    @Override
//                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                        Log.e("PostPain",e.getMessage());
//                    }
//
//                    @Override
//                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//
//                    }
//                });
//            }
        });
    }

    //******************** GraphQL stuff ******************
    public void runAddTaskMutation(Task newTask){
        GraphQLCall.Callback<ListTeamsQuery.Data> addTeamCallback = new GraphQLCall.Callback<ListTeamsQuery.Data>() {
            @Override
            public void onResponse(@Nonnull Response<ListTeamsQuery.Data> response) {
                CreateTaskInput input = CreateTaskInput.builder()
                        .name(newTask.getTitle())
                        .description(newTask.getDescription())
                        .status(newTask.getStatus())
                        .assignedUser(newTask.getAssignedUser())
                        .taskTeamId(team)
                        .build();
                CreateTaskMutation mutation = CreateTaskMutation.builder().input(input).build();
                awsAppSyncClient.mutate(mutation).enqueue(new GraphQLCall.Callback<CreateTaskMutation.Data>() {
                    @Override
                    public void onResponse(@Nonnull Response<CreateTaskMutation.Data> response) {
                        Log.i("AddTask.Mutation","Success");
                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {
                        Log.e("AddTask.MutationFail",e.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                Log.e("AddTask.AddTaskFail",e.getMessage());
            }
        };

        //Must be network_only otherwise makes duplicates
        awsAppSyncClient.query(ListTeamsQuery.builder().build())
                .responseFetcher(AppSyncResponseFetchers.NETWORK_ONLY)
                .enqueue(addTeamCallback);
    }
}

