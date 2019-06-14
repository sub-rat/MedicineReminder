package com.subrat.medicine;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import com.subrat.R;
import com.subrat.alarm.AlarmReceiver;
import com.subrat.alarm.AlarmSoundService;
import com.subrat.database.MediCareDatabase;
import com.subrat.database.MedicineEntity;

import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AddMedicineActivity extends AppCompatActivity implements View.OnClickListener {

    EditText medicineName, periodOfRepetation;
    TextView setDate, setTime;
    protected static TextView date, time;
    Button save, delete;
    Switch alarm;
    MediCareDatabase database;
    Long medicineId;
    //Pending intent instance
    private PendingIntent pendingIntent;

    //Alarm Request Code
    private static final int ALARM_REQUEST_CODE = 133;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        //initializing the views
        medicineName = findViewById(R.id.et_medicine_name);
        setDate = findViewById(R.id.tv_set_date);
        setTime = findViewById(R.id.tv_set_time);
        date = findViewById(R.id.tv_date);
        time = findViewById(R.id.tv_time);
        save = findViewById(R.id.btn_save);
        delete = findViewById(R.id.btn_delete);
        periodOfRepetation = findViewById(R.id.et_repetation);
        alarm = findViewById(R.id.alarm_on_off);

        //get databse instance
        database = MediCareDatabase.getAppDatabase(this);

        //receiving intent form activity or notification
        medicineId = getIntent().getLongExtra("id", -1);

        //stopping the alam sound
        stopService(new Intent(AddMedicineActivity.this, AlarmSoundService.class));
        if (medicineId != -1) {
            setValueToViews(database.medicineDao().getMedicineDetail(medicineId));
            delete.setVisibility(View.VISIBLE);
        } else {
            delete.setVisibility(View.GONE);
        }

        date.setOnClickListener(this);
        time.setOnClickListener(this);
        setDate.setOnClickListener(this);
        setTime.setOnClickListener(this);
        save.setOnClickListener(this);
        delete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tv_date:
            case R.id.tv_set_date:
                new DatePickerFragment().show(getSupportFragmentManager(), "Select Date");
                break;

            case R.id.tv_time:
            case R.id.tv_set_time:
                new TimePickerFragment().show(getSupportFragmentManager(), "Select Time");
                break;

            case R.id.btn_save:
                if (medicineId == -1) {
                    medicineId = null;
                }
                if (medicineName.getText().toString().isEmpty()) {
                    medicineName.setError("Enter Medicine name");
                    return;
                }

                //Creating medicine entity with data and inserting or updating into database
                MedicineEntity medicine = new MedicineEntity(
                        medicineId,
                        medicineName.getText().toString(),
                        date.getText().toString(),
                        time.getText().toString(),
                        alarm.isChecked(),
                        Float.valueOf(periodOfRepetation.getText().toString()));
                Long dbId = database.medicineDao().insert(medicine);
                if (dbId != null) {
                    medicineId = dbId;
                    showToast("Successfylly Saved");
                    /* Retrieve a PendingIntent that will perform a broadcast */
                    Intent alarmIntent = new Intent(AddMedicineActivity.this, AlarmReceiver.class);
                    alarmIntent.putExtra("medicine", medicineName.getText().toString());
                    alarmIntent.putExtra("id", dbId);
                    pendingIntent = PendingIntent.getBroadcast(AddMedicineActivity.this, dbId.intValue(), alarmIntent, 0);
                    delete.setVisibility(View.VISIBLE);

                } else {
                    showToast("Something Went Wrong!!");
                }

                if (alarm.isChecked()) {
                    if (date.getText().toString().isEmpty() && time.getText().toString().isEmpty()) {
                        showToast("Enter Data and Time to Set Alarm");
                    } else {
                        triggerAlarmManager(Float.valueOf(periodOfRepetation.getText().toString()));
                    }
                } else {
                    stopAlarmManager();
                }
                break;

            case R.id.btn_delete:
                database.medicineDao().deleteMedicine(medicineId);
                Intent alarmIntent = new Intent(AddMedicineActivity.this, AlarmReceiver.class);
                alarmIntent.putExtra("medicine", medicineName.getText().toString());
                alarmIntent.putExtra("id", medicineId);
                pendingIntent = PendingIntent.getBroadcast(AddMedicineActivity.this, medicineId.intValue(), alarmIntent, 0);
                stopAlarmManager();
                showToast("Medicine Deleted Successfully");
                super.onBackPressed();
                break;

        }

    }

    private void setValueToViews(MedicineEntity medicineDetail) {
        medicineName.setText(medicineDetail.getMedicineName());
        date.setText(medicineDetail.getDate());
        time.setText(medicineDetail.getTime());
        alarm.setChecked(medicineDetail.getAlarm());
        periodOfRepetation.setText(String.valueOf(medicineDetail.getRepetation()));
    }


    //Trigger alarm manager with entered time interval
    public void triggerAlarmManager(Float repetation) {
        String mDate = date.getText().toString();
        String[] datesList = mDate.split("/");
        String mtime = time.getText().toString();
        String[] timesList = mtime.split(":");

        // get a Calendar object with current time
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.clear();
        cal.set(Integer.valueOf(datesList[0]), Integer.valueOf(datesList[1]) - 1, Integer.valueOf(datesList[2]), Integer.valueOf(timesList[0]), Integer.valueOf(timesList[1]), 5);

        Log.d("dateTime", cal.getTime().toString());
        //get instance of alarm manager
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //set alarm manager with entered timer by converting into milliseconds
        if (repetation > 0 && repetation < 1) {
            repetation = repetation * 60;
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), TimeUnit.MINUTES.toMillis(repetation.intValue()), pendingIntent);
        } else if (repetation >= 1) {
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), TimeUnit.HOURS.toMillis(repetation.intValue()), pendingIntent);
        } else {
            manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        }

    }

    //Stop/Cancel alarm manager
    public void stopAlarmManager() {

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);//cancel the alarm manager of the pending intent

        //Stop the Media Player Service to stop sound and notification
        stopService(new Intent(AddMedicineActivity.this, AlarmSoundService.class));
    }

    public void showToast(String message) {
        Toast.makeText(AddMedicineActivity.this, message, Toast.LENGTH_SHORT).show();
    }


    /**
     * Data Picker Dialog to pick a date as required
     */
    public static class DatePickerFragment extends AppCompatDialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(Objects.requireNonNull(getActivity()), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            month++;
            date.setText(year + "/" + month + "/" + dayOfMonth);
        }
    }


    /**
     * Time Picker Dialog to pick a time as required
     */
    public static class TimePickerFragment extends AppCompatDialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            time.setText(hourOfDay + ":" + minute);
        }
    }
}

