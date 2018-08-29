package com.example.cr.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

@Entity(tableName = "recordings")

public class Recording{

    @PrimaryKey(autoGenerate = true)
    //@ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "number")
    private String number;

    @ColumnInfo(name = "date")
    private long date;

    // constructor
    public Recording(String name, String number, long date) {
        //this.id = id;
        this.name = name;
        this.number = number;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
       this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }


}
