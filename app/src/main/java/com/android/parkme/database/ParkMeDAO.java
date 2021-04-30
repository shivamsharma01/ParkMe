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

    @Query("DELETE FROM chat_table")
    void deleteAll();

    @Query("SELECT * FROM chat_table where qid=:qid ORDER BY time")
    List<Chat> getChatForQueryID(int qid);

    @Insert()
    void insertAll(List<Chat> chats);

    @Update()
    void update(Chat chat);
}