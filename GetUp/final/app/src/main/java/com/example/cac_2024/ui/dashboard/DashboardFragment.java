package com.example.cac_2024.ui.dashboard;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cac_2024.MainActivity;
import com.example.cac_2024.R;
import com.example.cac_2024.databinding.FragmentDashboardBinding;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerInterface;
import com.skydoves.powerspinner.PowerSpinnerView;
import com.skydoves.powerspinner.internals.PowerSpinnerDsl;

import java.util.Calendar;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private Button startTimeBtn;
    private TextView startTimeText;
    private Button endTimeBtn;
    private TextView endTimeText;

    private PowerSpinnerView powerSpinnerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        startTimeBtn = root.findViewById(R.id.startTimeBtn);
        startTimeText = root.findViewById(R.id.startTimeText);
        startTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(container.getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                SharedPreferences sharedPref = getActivity().getSharedPreferences("timePref", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putInt("startHour", hourOfDay);
                                editor.putInt("startMin", minute);
                                editor.apply();
                                String format = "%1$02d";
                                String hourText = String.format(format, hourOfDay);
                                String minText = String.format(format, minute);
                                startTimeText.setText(hourText + ":" + minText);

                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
    });
        endTimeBtn = root.findViewById(R.id.endTimeBtn);
        endTimeText = root.findViewById(R.id.endTimeText);
        powerSpinnerView = root.findViewById(R.id.powerSpinnerView);
        endTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(container.getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                SharedPreferences sharedPref = getActivity().getSharedPreferences("timePref", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putInt("endHour", hourOfDay);
                                editor.putInt("endMin", minute);

                                editor.apply();
                                String format = "%1$02d";
                                String hourText = String.format(format, hourOfDay);
                                String minText = String.format(format, minute);
                                endTimeText.setText(hourText + ":" + minText);
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        powerSpinnerView.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
            @Override public void onItemSelected(int oldIndex, @Nullable String oldItem, int newIndex, String newItem) {
                SharedPreferences sharedPref = getActivity().getSharedPreferences("timePref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                if(newIndex == 0) {
                    editor.putInt("stepGoal", 750);
                    editor.putInt("minutes", 15);

                } else if (newIndex == 1) {
                    editor.putInt("stepGoal", 1500);
                    editor.putInt("minutes", 15);
                } else if (newIndex == 2) {
                    editor.putInt("stepGoal", 2000);
                    editor.putInt("minutes", 40);
                } else if (newIndex == 3) {
                    editor.putInt("stepGoal", 4000);
                    editor.putInt("minutes", 40);
                }
                editor.apply();
            }
        });
    dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    public void onStart(){
        super.onStart();
        SharedPreferences sharedPref = getActivity().getSharedPreferences("timePref", Context.MODE_PRIVATE);
        String format = "%1$02d";
        String hourText = String.format(format, sharedPref.getInt("startHour", 0));
        String minText = String.format(format, sharedPref.getInt("startMin", 0));
        startTimeText.setText(hourText + ":" + minText);

        hourText = String.format(format, sharedPref.getInt("endHour", 0));
        minText = String.format(format, sharedPref.getInt("endMin", 0));
        endTimeText.setText(hourText + ":" + minText);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}