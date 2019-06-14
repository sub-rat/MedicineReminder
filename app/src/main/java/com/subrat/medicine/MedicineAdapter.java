package com.subrat.medicine;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.subrat.R;
import com.subrat.database.MedicineEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class for listdown the medicine that we are adding into databse.
 */
public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MyViewHolder> {
    List<MedicineEntity> medicineList = new ArrayList<>();

    public MedicineAdapter(List<MedicineEntity> medicineList) {
        this.medicineList = medicineList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_item_medicine, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int position) {
        final MedicineEntity medicine = medicineList.get(position);
        myViewHolder.medicineName.setText(medicine.getMedicineName());
        myViewHolder.dateTime.setText(String.format("%s %s", medicine.getDate(), medicine.getTime()));
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(myViewHolder.itemView.getContext(), AddMedicineActivity.class);
                intent.putExtra("id", medicine.getId());
                myViewHolder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView medicineName, dateTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            medicineName = itemView.findViewById(R.id.tv_medicine_name);
            dateTime = itemView.findViewById(R.id.tv_intake_time);
        }
    }
}
