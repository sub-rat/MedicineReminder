package com.subrat.medicine

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.subrat.R
import com.subrat.database.MedicineEntity
import kotlinx.android.synthetic.main.recycler_view_item_medicine.view.*
import java.util.*

/**
 * Adapter class for listdown the medicine that we are adding into databse.
 */
class MedicineAdapter(medicineList: List<MedicineEntity>) : RecyclerView.Adapter<MedicineAdapter.MyViewHolder>() {
    internal var medicineList: List<MedicineEntity> = ArrayList()

    init {
        this.medicineList = medicineList
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.recycler_view_item_medicine, viewGroup, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, position: Int) {
        val medicine = medicineList[position]
        myViewHolder.medicineName.text = medicine.medicineName
        myViewHolder.dateTime.text = String.format("%s %s", medicine.date, medicine.time)
        myViewHolder.itemView.setOnClickListener {
            val intent = Intent(myViewHolder.itemView.context, AddMedicineActivity::class.java)
            intent.putExtra("id", medicine.id)
            myViewHolder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return medicineList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var medicineName: TextView
        var dateTime: TextView

        init {
            medicineName = itemView.tv_medicine_name
            dateTime = itemView.tv_intake_time
        }
    }
}
