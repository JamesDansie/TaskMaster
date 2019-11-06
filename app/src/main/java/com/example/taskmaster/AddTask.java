package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
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
import java.util.LinkedList;
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

public class AddTask extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private AppDatabase db;
    AWSAppSyncClient awsAppSyncClient;
    private LinkedList<ListTeamsQuery.Item> teams;
    private String team;
    private String teamID;
    private static final int READ_REQUEST_CODE = 42;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        teams = new LinkedList<>();

        // Build a connection to AWS
        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        ListTeamsQuery query = ListTeamsQuery.builder().build();
        awsAppSyncClient.query(query)
                .responseFetcher(AppSyncResponseFetchers.NETWORK_FIRST)
                .enqueue(allTeamCallback);

//        Button addFileButton = findViewById(R.id.buttonAddFile);
//        addFileButton.setOnClickListener((event) -> {
//        });

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
                        .taskTeamId(teamID)
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

    private GraphQLCall.Callback<ListTeamsQuery.Data> allTeamCallback = new GraphQLCall.Callback<ListTeamsQuery.Data>() {
        @Override
        public void onResponse(@Nonnull Response<ListTeamsQuery.Data> response) {
            Log.d("AddTask.Callback", "made it to the callback success");
            Handler h = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message message){
                    teams.addAll(response.data().listTeams().items());

                    LinkedList<String> teamNames = new LinkedList<>();

                    for(ListTeamsQuery.Item team : teams){
                        teamNames.add(team.name());
                    }

                    Spinner spinner = findViewById(R.id.spinnerAddTask);

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTask.this, android.R.layout.simple_spinner_item, teamNames);

                    spinner.setAdapter(adapter);

                    spinner.setOnItemSelectedListener(AddTask.this);
                }
            };
            h.obtainMessage().sendToTarget();
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("AddTask.Callback", e.getMessage());
        }
    };

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.i("TeamSelected",Integer.toString(i));

        Spinner spinner  = (Spinner)findViewById(R.id.spinnerAddTask);
        String text = spinner.getSelectedItem().toString();
        Log.i("TeamSelected", text);

        for(ListTeamsQuery.Item team : teams){
            if(team.name().equals(text)){
                Log.i("TeamId?",team.id());
                teamID = team.id();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //From the docs; https://developer.android.com/guide/topics/providers/document-provider#client
    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch(View v) {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("image/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i("AddTask.onActResult", "Uri: " + uri.toString());
//                showImage(uri);
            }
        }
    }
}

