package com.subrat.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * This is the android Room Database Class. this class creates database in android once app is run.
 * verson =1 . this should is upgraded is db schema is changed
 */
@Database(entities = {
        MedicineEntity.class
}, version = 1, exportSchema = false)
public abstract class MediCareDatabase extends RoomDatabase {
    private static MediCareDatabase INSTANCE;

    public abstract MedicineDao medicineDao();

    //Single instance of the Database class is created which will be used in application.
    public static MediCareDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MediCareDatabase.class, "medicare")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
