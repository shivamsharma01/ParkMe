package com.android.parkme.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ParkMeDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Chat chat);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(com.android.parkme.database.Query query);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Announcement announcement);

    @Query("SELECT * FROM chat_table where qid=:qid ORDER BY time")
    List<Chat> getChatForQueryID(int qid);

    @Query("SELECT * FROM query_table where from_id=:id")
    List<com.android.parkme.database.Query> raisedByMe(int id);

    @Query("SELECT * FROM query_table where to_id=:id")
    List<com.android.parkme.database.Query> raisedAgainstMe(int id);

    @Query("UPDATE query_table SET status =:status where qid=:qid")
    void updateCancelRequest(String status, int qid);

    @Query("UPDATE query_table SET status =:status AND rating =:rating where qid=:qid")
    void updateCloseRequest(String status, int qid, float rating);

}