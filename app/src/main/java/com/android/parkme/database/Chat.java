package com.android.parkme.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "chat_table")
public class Chat implements Serializable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private int id;

    @NonNull
    @ColumnInfo(name = "qid")
    private int qid;

    @ColumnInfo(name = "from_id")
    private int from;

    @ColumnInfo(name = "to_id")
    private int to;

    @ColumnInfo(name = "time")
    private long time;

    @ColumnInfo(name = "msg")
    private String msg;

    public Chat() {
    }

    public Chat(int qid, int from, int to, long time, String msg) {
        this.qid = qid;
        this.from = from;
        this.to = to;
        this.time = time;
        this.msg = msg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQid() {
        return qid;
    }

    public void setQid(int qid) {
        this.qid = qid;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}