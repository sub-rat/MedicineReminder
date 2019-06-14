package com.subrat.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * This is the android Room Database Class. this class creates database in android once app is run.
 * verson =1 . this should is upgraded is db schema is changed
 */
@Database(entities = [MedicineEntity::class], version = 1, exportSchema = false)
abstract class MediCareDatabase : RoomDatabase() {

    abstract fun medicineDao(): MedicineDao

    companion object {
        var INSTANCE: MediCareDatabase? = null

        //Single instance of the Database class is created which will be used in application.
        fun getAppDatabase(context: Context): MediCareDatabase {
            if (INSTANCE == null) {
                synchronized(MediCareDatabase::class.java) {
                    INSTANCE =
                        Room.databaseBuilder(context.applicationContext, MediCareDatabase::class.java, "medicare")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build()
                }
            }
            return INSTANCE as MediCareDatabase
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
