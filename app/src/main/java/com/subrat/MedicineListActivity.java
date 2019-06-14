package com.subrat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.subrat.alarm.AlarmSoundService;
import com.subrat.database.MediCareDatabase;
import com.subrat.database.MedicineEntity;
import com.subrat.medicine.AddMedicineActivity;
import com.subrat.medicine.MedicineAdapter;

import java.util.List;

public class MedicineListActivity extends AppCompatActivity {

    MediCareDatabase database;
    RecyclerView recyclerView;
    TextView noRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_list);
        stopService(new Intent(MedicineListActivity.this, AlarmSoundService.class));

        //getting databse instance
        database = MediCareDatabase.getAppDatabase(this);

        //initialize view
        recyclerView = findViewById(R.id.rv_medicine_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noRecords = findViewById(R.id.tv_no_medicine);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MedicineListActivity.this, AddMedicineActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        List<MedicineEntity> list = database.medicineDao().getAllMedicines();
        if (list.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            noRecords.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noRecords.setVisibility(View.GONE);
            MedicineAdapter adapter = new MedicineAdapter(list);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
