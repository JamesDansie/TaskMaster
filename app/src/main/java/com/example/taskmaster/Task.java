package com.example.taskmaster;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.amazonaws.amplify.generated.graphql.ListTasksQuery;

@Entity
public class Task {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String title;
    private String description;
    private String assignedUser;

    @TypeConverters(StatusConverter.class)
    private String status;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = "new";
        this.assignedUser = null;
    }

    public Task(InternetTask internetTask){
        this.title = internetTask.getTitle();
        this.description = internetTask.getBody();
        this.status = "new";
        this.assignedUser = internetTask.getAssignedUser();
    }

    public Task(ListTasksQuery.Item item){
        this.title = item.name();
        this.description = item.description();
        this.status = item.status();
        this.assignedUser = item.assignedUser();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(String assignedUser) {
        this.assignedUser = assignedUser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
