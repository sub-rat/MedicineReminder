package com.subrat

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.subrat.alarm.AlarmSoundService
import com.subrat.database.MediCareDatabase
import com.subrat.medicine.AddMedicineActivity
import com.subrat.medicine.MedicineAdapter
import kotlinx.android.synthetic.main.content_main.*

class MedicineListActivity : AppCompatActivity() {

    lateinit var database: MediCareDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medicine_list)
        stopService(Intent(this@MedicineListActivity, AlarmSoundService::class.java))

        //getting databse instance
        database = MediCareDatabase.getAppDatabase(this)

        rv_medicine_list.setHasFixedSize(true)
        rv_medicine_list.layoutManager = LinearLayoutManager(this)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MedicineListActivity, AddMedicineActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        val list = database.medicineDao().allMedicines
        if (list.isEmpty()) {
            rv_medicine_list.visibility = View.GONE
            tv_no_medicine.visibility = View.VISIBLE
        } else {
            rv_medicine_list.visibility = View.VISIBLE
            tv_no_medicine.visibility = View.GONE
            val adapter = MedicineAdapter(list)
            rv_medicine_list.adapter = adapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

}
