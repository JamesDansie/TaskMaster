package com.example.taskmaster;
//From Michelle's sample code
import android.util.Log;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks;
    private OnTaskInteractionListener listener;
    private final String TAG = "Dansie";

    public TaskAdapter(List<Task> tasks, OnTaskInteractionListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        Task task;
        TextView taskTitleView;
        TextView taskDescriptionView;
        public TaskViewHolder(@NonNull View taskView) {
            super(taskView);
            this.taskTitleView = taskView.findViewById(R.id.TaskTitle);
            this.taskDescriptionView = taskView.findViewById(R.id.TaskDescription);
        }
    }



    // RecyclerView needs us to create a brand new row, from scratch, for holding data
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_task, parent, false);
        final TaskViewHolder holder = new TaskViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"TaskAdap.PotatoListner I was clicked");
                Log.i(TAG, "TaskAdap.taskToListner "+holder.task.toString());
                listener.potato(holder.task);
            }
        });
        return holder;
    }

    // RecyclerView has a row (maybe previously used?) that needs to be updated for a particular location/index
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task taskAtPosition = this.tasks.get(position);
        holder.task = taskAtPosition;
        holder.taskTitleView.setText(taskAtPosition.getTitle());
        holder.taskDescriptionView.setText(taskAtPosition.getDescription());
    }

    @Override
    public int getItemCount() {
        return this.tasks.size();
    }

    // Make sure that my adapter can communicate with any Activity it's a part of that implements this interface
    public static interface OnTaskInteractionListener {
        public void potato(Task task);
    }

}
