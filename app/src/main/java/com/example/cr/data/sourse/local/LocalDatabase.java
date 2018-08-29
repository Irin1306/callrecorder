package com.example.cr.data.sourse.local;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.cr.data.sourse.local.dao.RecordingDao;

@Database(entities = {
        com.example.cr.data.entity.Recording.class

}, version = 1)

public abstract class LocalDatabase extends RoomDatabase {


    private static final Object sLock = new Object();
    private static LocalDatabase INSTANCE;

    /**
     * Singleton pattern
     */
    public static LocalDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        LocalDatabase.class,
                        "crdatabase_db")
                       /* .addMigrations(new Migration(1, 2) {
                              @Override
                                   public void migrate(@NonNull SupportSQLiteDatabase database) {
                                       database.execSQL(

                                                );
                                   }
                              }
                        )*/
                        .build();
            }
            return INSTANCE;
        }
    }

    public abstract RecordingDao recordingDao();


}
