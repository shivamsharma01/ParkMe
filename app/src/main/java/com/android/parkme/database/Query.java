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
    @ColumnInfo(name = "from_name")
    private String fromName;

    @NonNull
    @ColumnInfo(name = "from_id")
    private int fromId;

    @NonNull
    @ColumnInfo(name = "to_name")
    private String toName;

    @NonNull
    @ColumnInfo(name = "to_id")
    private int toId;

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

    public Query(int qid, String status, String fromName, int fromId, String toName, int toId, long time, float rating) {
        this.qid = qid;
        this.status = status;
        this.fromName = fromName;
        this.fromId = fromId;
        this.toName = toName;
        this.toId = toId;
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
    public String getFromName() {
        return fromName;
    }

    public void setFromName(@NonNull String fromName) {
        this.fromName = fromName;
    }

    @NonNull
    public String getToName() {
        return toName;
    }

    public void setToName(@NonNull String toName) {
        this.toName = toName;
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

    public int getFromId() {
        return fromId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    @NonNull
    public int getToId() {
        return toId;
    }

    public void setToId(@NonNull int toId) {
        this.toId = toId;
    }
}
