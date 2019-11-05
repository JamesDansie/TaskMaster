package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.amazonaws.amplify.generated.graphql.DeleteTaskMutation;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import javax.annotation.Nonnull;

import type.DeleteTaskInput;

public class Detail extends AppCompatActivity {

    public AppDatabase db;
    private AWSAppSyncClient awsAppSyncClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Build a connection to AWS
        awsAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        String taskName = getIntent().getStringExtra("taskTitle");
        TextView tasktitle = findViewById(R.id.DetailTaskName);
        tasktitle.setText(taskName);

        String taskDescription = getIntent().getStringExtra("taskDescription");
        TextView taskDesc =  findViewById(R.id.taskDescriptionDetailText);
        taskDesc.setText(taskDescription);

        Button deleteButton = findViewById(R.id.DetailDeleteTask);
        deleteButton.setOnClickListener((event) -> {

            //************ Local DB ***************
//            db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "tasks").allowMainThreadQueries().build();
//            db.taskDao().deleteByTitle(taskName);

            String idToBeDeleted = getIntent().getStringExtra("taskId");
            Log.i("Detail.IdToBeDeleted",idToBeDeleted);
            runDeleteTaskMutation(idToBeDeleted);
        });
    }

    //id is a dyno ID
    public void runDeleteTaskMutation(String id){
        Log.i("Detail.IdToBeDelete", id);
        DeleteTaskInput deleteTaskInput = DeleteTaskInput.builder()
                .id(id)
                .build();

        awsAppSyncClient.mutate(DeleteTaskMutation.builder().input(deleteTaskInput).build())
                .enqueue(new GraphQLCall.Callback<DeleteTaskMutation.Data>() {
                    @Override
                    public void onResponse(@Nonnull Response<DeleteTaskMutation.Data> response) {
                        Log.i("Detail.Delete","yay!");
                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {
                        Log.i("Dettail.Delete","sad :(");
                    }
                });
    }
}
