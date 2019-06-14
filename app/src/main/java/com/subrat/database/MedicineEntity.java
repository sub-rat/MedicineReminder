package com.subrat.database;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * This is entitiy class whcich represents the database table name = medicines .
 */
@Entity(tableName = "medicines")
public class MedicineEntity {
    @PrimaryKey(autoGenerate = true)
    Long id;
    @ColumnInfo(name = "name")
    private String medicineName;
    @ColumnInfo(name = "date")
    private String date;
    @ColumnInfo(name = "time")
    private String time;
    @ColumnInfo(name = "alarm")
    private Boolean alarm;
    @ColumnInfo(name = "repetation")
    private Float repetation;

    public MedicineEntity(Long id, String medicineName, String date, String time, Boolean alarm, Float repetation) {
        this.id = id;
        this.medicineName = medicineName;
        this.date = date;
        this.time = time;
        this.alarm = alarm;
        this.repetation = repetation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getAlarm() {
        return alarm;
    }

    public void setAlarm(Boolean alarm) {
        this.alarm = alarm;
    }

    public Float getRepetation() {
        return repetation;
    }

    public void setRepetation(Float repetation) {
        this.repetation = repetation;
    }
}
