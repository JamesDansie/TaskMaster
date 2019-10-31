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

            //TODO: cannot pass id from front for some reason
            String idToBeDeleted = getIntent().getStringExtra("taskId");
            Log.i("IdToBeDeleted",idToBeDeleted);
            runDeleteTaskMutation(idToBeDeleted);
        });
    }

    //I am here to delete by ID, need to check id that was passed.
    public void runDeleteTaskMutation(String id){
        Log.i("IdToBeDelete", id);
        DeleteTaskInput deleteTaskInput = DeleteTaskInput.builder()
                .id(id)
                .build();

        awsAppSyncClient.mutate(DeleteTaskMutation.builder().input(deleteTaskInput).build())
                .enqueue(new GraphQLCall.Callback<DeleteTaskMutation.Data>() {
                    @Override
                    public void onResponse(@Nonnull Response<DeleteTaskMutation.Data> response) {
                        Log.i("Delete","yay!");
                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {
                        Log.i("Delete","sad :(");
                    }
                });

//        DeleteTaskInput deleteTaskInput = DeleteTaskInput.builder()

    }


//    public void runAddTaskMutation(Task newTask){
//        CreateTaskInput createTaskInput = CreateTaskInput.builder()
//                .name(newTask.getTitle())
//                .description(newTask.getDescription())
//                .status(newTask.getStatus())
//                .build();
//        awsAppSyncClient.mutate(CreateTaskMutation.builder().input(createTaskInput).build())
//                .enqueue(addTaskCallback);
//    };
}
