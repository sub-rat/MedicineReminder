package com.subrat.database;


import androidx.room.*;

import java.util.List;

/**
 * This is where we run queries for crud operation .
 */
@Dao
public interface MedicineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(MedicineEntity medicine);

    @Query("select * from medicines")
    List<MedicineEntity> getAllMedicines();

    @Query("select * from medicines where id =:medId")
    MedicineEntity getMedicineDetail(Long medId);

    @Update
    void updateMedicine(MedicineEntity medicine);


    @Query("delete from medicines where id=:medId")
    void deleteMedicine(Long medId);
}
