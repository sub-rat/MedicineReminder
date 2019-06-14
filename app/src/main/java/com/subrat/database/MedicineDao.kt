package com.subrat.database


import androidx.room.*

/**
 * This is where we run queries for crud operation .
 */
@Dao
interface MedicineDao {

    @get:Query("select * from medicines")
    val allMedicines: List<MedicineEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(medicine: MedicineEntity): Long?

    @Query("select * from medicines where id =:medId")
    fun getMedicineDetail(medId: Long): MedicineEntity

    @Update
    fun updateMedicine(medicine: MedicineEntity)


    @Query("delete from medicines where id=:medId")
    fun deleteMedicine(medId: Long)
}
