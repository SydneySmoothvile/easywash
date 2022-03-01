package com.example.cbrapp.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(version = 1, entities = ServicesItem.class, exportSchema = false)
public abstract class ServicesDatabase extends RoomDatabase {
    private static ServicesDatabase instance;

    public abstract ServicesDAO servicesDAO();

    public static ServicesDatabase getInstance(Context context){
        if(instance==null)
            instance= Room.databaseBuilder(context,ServicesDatabase.class,"MyAttendantDB").build();
        return instance;
    }
}
