package com.subrat.database


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This is entitiy class which represents the database table name = medicines .
 */
@Entity(tableName = "medicines")
class MedicineEntity(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo(name = "name") var medicineName: String?,
    @ColumnInfo(name = "date") var date: String?,
    @ColumnInfo(name = "time") var time: String?,
    @ColumnInfo(name = "alarm") var alarm: Boolean?,
    @ColumnInfo(name = "repetation") var repetation: Float?
)
