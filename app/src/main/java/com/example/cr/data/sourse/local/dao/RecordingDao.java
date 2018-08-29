package com.example.cr.data.sourse.local.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.cr.data.entity.Recording;

import java.util.List;

@Dao
public interface RecordingDao {

    @Query("SELECT * FROM recordings")
    List<Recording> getAllRecordings();

    @Query("SELECT * FROM recordings WHERE id = :id")
    Recording getRecordingById(int id);

    @Query("SELECT * FROM recordings WHERE name = :name")
    Recording getRecordingByName(String name);

    @Query("SELECT * FROM recordings WHERE name LIKE '%' || :searchString || '%'")
    List<Recording> getSearchResults(String searchString);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveRecording(Recording recording);

    @Delete
    void deleteRecording(Recording recording);

    @Query("DELETE FROM recordings")
    void deleteAll();

}
