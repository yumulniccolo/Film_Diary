package com.example.finalexer3grp2;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Film.class}, version = 2, exportSchema = false)
public abstract class FilmDatabase extends RoomDatabase {
    public abstract FilmDao filmDao();

    private static volatile FilmDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static FilmDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (FilmDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    FilmDatabase.class, "film_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
