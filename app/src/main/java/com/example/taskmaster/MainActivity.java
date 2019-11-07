package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.amazonaws.amplify.generated.graphql.GetTeamQuery;
import com.amazonaws.amplify.generated.graphql.ListTasksQuery;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.SignInUIOptions;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
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
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskInteractionListener{
    private List<Task> tasks;

    private RecyclerView recyclerView;
    private TaskAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private AWSAppSyncClient awsAppSyncClient;
    private String username;
    private String team;
    private String teamID;
    private final String TAG = "Dansie";

    public AppDatabase db;

    @Override
    protected void onResume(){
        super.onResume();

        this.tasks = new LinkedList<>();

        //TODO: Kill the local storage
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        this.username = prefs.getString("username", "Interchangable Cog");
        this.team = prefs.getString("team","any");
        this.teamID = prefs.getString("teamID","blue");

        if(username.length() == 0){
            username = "Interchangable Cog";
        }
        if(team == null){
            team = "any";
        }
        String name = AWSMobileClient.getInstance().getUsername();

        TextView userTasks = findViewById(R.id.textViewUserTasksMain);
        userTasks.setText(name +"'s tasks are;");

//        queryAllTasks();
        queryTeamTasks();
        //******************** Local DB *******************
//        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "tasks")
//                .fallbackToDestructiveMigration()
//                .allowMainThreadQueries().build();
//
//        this.tasks.addAll(db.taskDao().getAll());

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

    protected void setUserName(String name){
        Handler handlerForMainThread = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message inputMessage){

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = prefs.edit();

                editor.putString("username", name);
                editor.apply();

                TextView userTasks = findViewById(R.id.textViewUserTasksMain);
                userTasks.setText(name +"'s tasks are;");
            }
        };
        handlerForMainThread.obtainMessage().sendToTarget();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //***************** OkHttp To Heroku ***************
//        OkHttpClient client = new OkHttpClient();
//
//        Request request = new Request.Builder()
//                .url(getString(R.string.backend_url) + "/tasks") // backend_url + "/tasks";
//                .build();
//
//        client.newCall(request).enqueue(new LogTasksCallback(this));

        //****************** AWS Amplify *************

        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();


        //****************** AWS Cognito ************
        //TODO: have the logins go to the same method or class
        AWSMobileClient.getInstance().initialize(getApplicationContext(), new com.amazonaws.mobile.client.Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails result) {
                Log.i(TAG, "Main.LogIn onResult: " + result.getUserState().toString());

                if(result.getUserState().toString().equals("SIGNED_OUT")){
                    AWSMobileClient.getInstance().showSignIn(MainActivity.this,
                            SignInUIOptions.builder()
                                    .backgroundColor(R.color.colorPrimary)
                                    .logo(R.drawable.picolas)
                            .build(),
                            new com.amazonaws.mobile.client.Callback<UserStateDetails>() {
                                @Override
                                public void onResult(UserStateDetails result) {
                                    Log.i(TAG, "Main.LogIn callback success " + result.getUserState().toString());
                                    setUserName(AWSMobileClient.getInstance().getUsername());
                                }

                                @Override
                                public void onError(Exception e) {
                                    Log.e(TAG, "Main.LogIn callback failure " + e.getMessage());
                                }
                            });

                    }

                }


            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Main.LogIn Initialization error.", e);
            }
        });

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

        Button signInButton = findViewById(R.id.buttonSignIn);
        signInButton.setOnClickListener((event) -> {
            Log.i(TAG, "Main.LogInButton I've been clicked");

            AWSMobileClient.getInstance().showSignIn(MainActivity.this,
                    SignInUIOptions.builder().backgroundColor(R.color.colorPrimary).logo(R.drawable.picolas).build(),
            new com.amazonaws.mobile.client.Callback<UserStateDetails>(){

                @Override
                public void onResult(UserStateDetails result) {
                    Log.i(TAG, "Main.LogInButton "+ result.getUserState().toString());
                    setUserName(AWSMobileClient.getInstance().getUsername());
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "Main.LogInButton "+e.getMessage());

                }
            });
        });

        Button signOutButton = findViewById(R.id.buttonSignOut);
        signOutButton.setOnClickListener((event) -> {
            //TODO: after logout send to log in with a callback function
            AWSMobileClient.getInstance().signOut();
            Log.i(TAG,"Main.LogOutButton I've been clicked");
            setUserName("Interchangable Cog");
        });
    }

    public void queryTeamTasks() {
        awsAppSyncClient.query(GetTeamQuery.builder().id(teamID).build())
                .responseFetcher(AppSyncResponseFetchers.NETWORK_FIRST)
                .enqueue(getTeamTasksCallback);
    }
    public GraphQLCall.Callback<GetTeamQuery.Data> getTeamTasksCallback = new GraphQLCall.Callback<GetTeamQuery.Data>() {
        @Override
        public void onResponse(@Nonnull com.apollographql.apollo.api.Response<GetTeamQuery.Data> response) {

            Handler handlerForMainThread = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message inputMessage){
                    GetTeamQuery.GetTeam teamItem = response.data().getTeam();
                    if(teamItem != null){

                        List<GetTeamQuery.Item> tasksItems = teamItem.tasks().items();

                        tasks.clear();

                        for(GetTeamQuery.Item taskItem : tasksItems){
                            tasks.add(new Task(taskItem));
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }
            };
            handlerForMainThread.obtainMessage().sendToTarget();
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {

        }
    };

    @Override
    public void potato(Task task) {
        Intent goToDetailIntent = new Intent(this, Detail.class);

        goToDetailIntent.putExtra("taskTitle", task.getTitle());
        goToDetailIntent.putExtra("taskDescription", task.getDescription());

        System.out.println("************************");
        Log.i(TAG, "Main.TaskPassedInPotato "+task.toString());
        goToDetailIntent.putExtra("taskId", task.getIdDyno());
        goToDetailIntent.putExtra("imageURL", task.getImageURL());

        MainActivity.this.startActivity(goToDetailIntent);
    }

    public void putDataOnPage(String data){

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
        mAdapter.notifyDataSetChanged();

    }
}

class LogTasksCallback implements Callback {
    MainActivity currentMainActivityInstance;

    public LogTasksCallback(MainActivity currentMainActivityInstance){
        this.currentMainActivityInstance = currentMainActivityInstance;
    }

    private static final String TAG = "Dansie Main.Callback";

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