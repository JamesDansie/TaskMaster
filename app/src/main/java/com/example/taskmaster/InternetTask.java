package com.example.taskmaster;

public class InternetTask {
    public long id;
    public String title;
    public String body;
    public String assignedUser;

    public InternetTask(){};

    public InternetTask(long id, String title, String body, String assignedUser) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.assignedUser = assignedUser;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(String assignedUser) {
        this.assignedUser = assignedUser;
    }

    @Override
    public String toString() {
        return "InternetTask{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", assignedUser='" + assignedUser + '\'' +
                '}';
    }
}
