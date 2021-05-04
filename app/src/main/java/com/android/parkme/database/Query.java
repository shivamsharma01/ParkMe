package com.android.parkme.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "query_table")
public class Query {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "qid")
    private int qid;

    @NonNull
    @ColumnInfo(name = "from_id")
    private String from;

    @NonNull
    @ColumnInfo(name = "to_id")
    private String to;

    @NonNull
    @ColumnInfo(name = "status")
    private String status;

    @NonNull
    @ColumnInfo(name = "time")
    private long time;

    @NonNull
    @ColumnInfo(name = "rating")
    private float rating;

    public Query() {
    }

    public Query(String status, String from, String to, long time, float rating) {
        this.status = status;
        this.from = from;
        this.to = to;
        this.time = time;
        this.rating = rating;
    }

    public int getQid() {
        return qid;
    }

    public void setQid(int qid) {
        this.qid = qid;
    }

    @NonNull
    public String getFrom() {
        return from;
    }

    public void setFrom(@NonNull String from) {
        this.from = from;
    }

    @NonNull
    public String getTo() {
        return to;
    }

    public void setTo(@NonNull String to) {
        this.to = to;
    }

    @NonNull
    public String getStatus() {
        return status;
    }

    public void setStatus(@NonNull String status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
