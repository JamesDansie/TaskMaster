package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.amazonaws.amplify.generated.graphql.ListTasksQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

public class AllTasks extends AppCompatActivity implements TaskAdapter.OnTaskInteractionListener{
    private RecyclerView recyclerView;
    private TaskAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Task> tasks;
    private AWSAppSyncClient awsAppSyncClient;
    private static final String TAG = "Dansie";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);

        tasks = new LinkedList<>();

        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerAllTasks);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new TaskAdapter(this.tasks, this);
        recyclerView.setAdapter(mAdapter);

        queryAllTasks();
    }

    private void queryAllTasks(){
        awsAppSyncClient.query(ListTasksQuery.builder().build())
                .responseFetcher(AppSyncResponseFetchers.NETWORK_FIRST)
                .enqueue(getAllTasksCallback);
    }

    public GraphQLCall.Callback<ListTasksQuery.Data> getAllTasksCallback = new GraphQLCall.Callback<ListTasksQuery.Data>() {
        @Override
        public void onResponse(@Nonnull Response<ListTasksQuery.Data> response) {

            Handler h = new Handler(Looper.getMainLooper()){
                public void handleMessage(Message inputMessage){
                    List<ListTasksQuery.Item> items = response.data().listTasks().items();
                    tasks.clear();

                    for(ListTasksQuery.Item item : items){
                        tasks.add(new Task(item));
                    }
                    mAdapter.notifyDataSetChanged();
                }
            };
            h.obtainMessage().sendToTarget();
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
        Log.i(TAG, task.toString());
        goToDetailIntent.putExtra("taskId", task.getIdDyno());
        goToDetailIntent.putExtra("latitude", task.getLatitude());
        goToDetailIntent.putExtra("longitude", task.getLongitude());


        AllTasks.this.startActivity(goToDetailIntent);
    }
}
