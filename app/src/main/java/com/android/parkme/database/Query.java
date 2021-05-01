package com.android.parkme.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "query_table")
public class Query {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private int id;

    @NonNull
    @ColumnInfo(name = "qid")
    private int qid;

    @ColumnInfo(name = "from_id")
    private String from;

    @ColumnInfo(name = "to_id")
    private String to;

    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "time")
    private long time;

    public Query() {}

    public Query(String status, String from, String to, long time) {
        this.status = status;
        this.from = from;
        this.to = to;
        this.time = time;
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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
