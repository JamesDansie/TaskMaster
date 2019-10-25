package com.example.taskmaster;

public enum Status {
    NEW(0),
    ASSIGN(1),
    IN_PROGRESS(2),
    COMPLETE(3);

    private int code;

    Status(int code){
        this.code = code;
    }

    public int getCode(){
        return this.code;
    }
}
