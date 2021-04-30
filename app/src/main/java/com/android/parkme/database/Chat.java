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
    private String from;

    @ColumnInfo(name = "to_id")
    private String to;

    @ColumnInfo(name = "time")
    private long time;

}