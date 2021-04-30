package com.android.parkme.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Chat.class}, version = 1, exportSchema = false)
public abstract class ParkMeRoomDatabase extends RoomDatabase {

    public abstract ParkMeDAO parkMeDAODao();

    private static volatile ParkMeRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static ParkMeRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ParkMeRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ParkMeRoomDatabase.class, "parkme_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}