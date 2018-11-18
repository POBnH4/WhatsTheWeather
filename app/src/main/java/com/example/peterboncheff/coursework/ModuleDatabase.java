package com.example.peterboncheff.coursework;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

@Database(entities = {Module.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class ModuleDatabase extends RoomDatabase {

    public abstract ModuleDao moduleDao();
    private static ModuleDatabase INSTANCE;
    public static ModuleDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (ModuleDatabase.class){
                final String DATABASE_NAME = "module_database";
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), ModuleDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                    //Wipes and rebuilds instead of migrating
                    //if no Migration object;
                }
            }
        }
        return INSTANCE;
    }

}
