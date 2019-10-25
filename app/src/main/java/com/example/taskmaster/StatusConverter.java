package com.example.taskmaster;

import androidx.room.TypeConverter;

public class StatusConverter {

    @TypeConverter
    public static Status toStatus(int status){
        if(status == Status.NEW.getCode()) {
            return Status.NEW;
        } else if (status == Status.ASSIGN.getCode()){
            return Status.ASSIGN;
        } else if (status == Status.IN_PROGRESS.getCode()) {
            return Status.IN_PROGRESS;
        } else if (status == Status.COMPLETE.getCode()) {
            return Status.COMPLETE;
        } else {
            throw new IllegalArgumentException("Acceptable statuses are; NEW, ASSIGN, IN_PROGRESS AND COMPLETE");
        }
    }

    @TypeConverter
    public static int toInteger(Status status){
        return status.getCode();
    }
}
