package com.android.parkme.database;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ParkMeDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Chat chat);

    @Query("DELETE FROM chat_table")
    void deleteAll();

    @Query("SELECT * FROM chat_table where qid=:qid ORDER BY time")
    List<Chat> getChatForQueryID(int qid);

    @Insert()
    void insertAll(List<Chat> chats);

    @Update()
    void update(Chat chat);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(com.android.parkme.database.Query query);

    @Query("SELECT * FROM query_table where from_id=:id")
    List<com.android.parkme.database.Query> raisedByMe(int id);

    @Query("SELECT * FROM query_table where to_id=:id")
    List<com.android.parkme.database.Query> raisedAgainstMe(int id);
}