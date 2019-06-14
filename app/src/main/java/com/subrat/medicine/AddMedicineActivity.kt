package com.subrat.medicine

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentActivity
import com.subrat.R
import com.subrat.alarm.AlarmReceiver
import com.subrat.alarm.AlarmSoundService
import com.subrat.database.MediCareDatabase
import com.subrat.database.MedicineEntity
import kotlinx.android.synthetic.main.activity_add_medicine.*
import java.util.*
import java.util.concurrent.TimeUnit

class AddMedicineActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var database: MediCareDatabase
    var medicineId: Long? = -1
    //Pending intent instance
    private var pendingIntent: PendingIntent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medicine)

        //get databse instance
        database = MediCareDatabase.getAppDatabase(this)

        //receiving intent form activity or notification
        medicineId = intent.getLongExtra("id", -1)

        //stopping the alam sound
        stopService(Intent(this@AddMedicineActivity, AlarmSoundService::class.java))
        if (medicineId != -1L) {
            setValueToViews(database.medicineDao().getMedicineDetail(medicineId!!))
            btn_delete.visibility = View.VISIBLE
        } else {
            btn_delete.visibility = View.GONE
        }

        tv_date.setOnClickListener(this)
        tv_time.setOnClickListener(this)
        tv_set_date.setOnClickListener(this)
        tv_set_time.setOnClickListener(this)
        btn_save.setOnClickListener(this)
        btn_delete.setOnClickListener(this)
    }

    override fun onClick(v: View) {

        when (v.id) {

            R.id.tv_date, R.id.tv_set_date -> DatePickerFragment { data->
                tv_date.text = data
            }.show(supportFragmentManager, "Select Date")

            R.id.tv_time, R.id.tv_set_time -> TimePickerFragment { data ->
                tv_time.text = data
            }.show(supportFragmentManager, "Select Time")

            R.id.btn_save -> {
                if (medicineId == -1L) {
                    medicineId = null
                }
                if (et_medicine_name.text.toString().isEmpty()) {
                    et_medicine_name.error = "Enter Medicine name"
                    return
                }

                //Creating medicine entity with data and inserting or updating into database
                val medicine = MedicineEntity(
                    medicineId,
                    et_medicine_name.text.toString(),
                    tv_date.text.toString(),
                    tv_time.text.toString(),
                    alarm_on_off.isChecked,
                    java.lang.Float.valueOf(et_repetation.text.toString())
                )
                val dbId = database.medicineDao().insert(medicine)
                if (dbId != null) {
                    medicineId = dbId
                    showToast("Successfylly Saved")
                    /* Retrieve a PendingIntent that will perform a broadcast */
                    val alarmIntent = Intent(this@AddMedicineActivity, AlarmReceiver::class.java)
                    alarmIntent.putExtra("medicine", et_medicine_name.text.toString())
                    alarmIntent.putExtra("id", dbId)
                    pendingIntent = PendingIntent.getBroadcast(this@AddMedicineActivity, dbId.toInt(), alarmIntent, 0)
                    btn_delete.visibility = View.VISIBLE

                } else {
                    showToast("Something Went Wrong!!")
                }

                if (alarm_on_off.isChecked) {
                    if (tv_date.text.toString().isEmpty() && tv_time.text.toString().isEmpty()) {
                        showToast("Enter Data and Time to Set Alarm")
                    } else {
                        triggerAlarmManager(java.lang.Float.valueOf(et_repetation.text.toString()))
                    }
                } else {
                    stopAlarmManager()
                }
            }

            R.id.btn_delete -> {
                database.medicineDao().deleteMedicine(medicineId!!)
                val alarmIntent = Intent(this@AddMedicineActivity, AlarmReceiver::class.java)
                alarmIntent.putExtra("medicine", et_medicine_name.text.toString())
                alarmIntent.putExtra("id", medicineId!!)
                pendingIntent =
                    PendingIntent.getBroadcast(this@AddMedicineActivity, medicineId!!.toInt(), alarmIntent, 0)
                stopAlarmManager()
                showToast("Medicine Deleted Successfully")
                super.onBackPressed()
            }
        }

    }

    private fun setValueToViews(medicineDetail: MedicineEntity) {
        et_medicine_name.setText(medicineDetail.medicineName)
        tv_date.text = medicineDetail.date
        tv_time.text = medicineDetail.time
        alarm_on_off.isChecked = medicineDetail.alarm!!
        et_repetation.setText(medicineDetail.repetation.toString())
    }


    //Trigger alarm manager with entered time interval
    fun triggerAlarmManager(repetation: Float?) {
        var repetation = repetation
        val mDate = tv_date.text.toString()
        val datesList = mDate.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val mtime = tv_time.text.toString()
        val timesList = mtime.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        // get a Calendar object with current time
        val cal = Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis()
        cal.clear()
        cal.set(
            Integer.valueOf(datesList[0]),
            Integer.valueOf(datesList[1]) - 1,
            Integer.valueOf(datesList[2]),
            Integer.valueOf(timesList[0]),
            Integer.valueOf(timesList[1]),
            5
        )

        Log.d("dateTime", cal.time.toString())
        //get instance of alarm manager
        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //set alarm manager with entered timer by converting into milliseconds
        if (repetation != null) {
            if (repetation > 0 && repetation < 1) {
                repetation *= 60
                manager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    cal.timeInMillis,
                    TimeUnit.MINUTES.toMillis(repetation.toInt().toLong()),
                    pendingIntent
                )
            } else if (repetation >= 1) {
                manager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    cal.timeInMillis,
                    TimeUnit.HOURS.toMillis(repetation.toInt().toLong()),
                    pendingIntent
                )
            } else {
                manager.set(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
            }
        }

    }

    //Stop/Cancel alarm manager
    fun stopAlarmManager() {

        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.cancel(pendingIntent)//cancel the alarm manager of the pending intent

        //Stop the Media Player Service to stop sound and notification
        stopService(Intent(this@AddMedicineActivity, AlarmSoundService::class.java))
    }

    fun showToast(message: String) {
        Toast.makeText(this@AddMedicineActivity, message, Toast.LENGTH_SHORT).show()
    }


    /**
     * Data Picker Dialog to pick a date as required
     */
    class DatePickerFragment(val listener: (String) -> Unit) : AppCompatDialogFragment(), DatePickerDialog.OnDateSetListener {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            return DatePickerDialog(Objects.requireNonNull<FragmentActivity>(activity), this, year, month, day)
        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
            var month = month
            month++
//            tv_date.text =
                listener("$year/$month/$dayOfMonth")
        }
    }


    /**
     * Time Picker Dialog to pick a time as required
     */
    class TimePickerFragment(val listener: (String) -> Unit) : AppCompatDialogFragment(), TimePickerDialog.OnTimeSetListener {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)
            return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
        }

        override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
//            tv_time.text =
            listener("$hourOfDay:$minute")
        }
    }

    companion object {
        //Alarm Request Code
        private val ALARM_REQUEST_CODE = 133
    }
}

