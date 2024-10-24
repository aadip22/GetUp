package com.example.cac_2024.ui.home;

import static android.content.Context.ALARM_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cac_2024.NotificationReceiver;
import com.example.cac_2024.R;
import com.example.cac_2024.StepActivity;
import com.example.cac_2024.databinding.FragmentHomeBinding;

import org.w3c.dom.Text;

import java.util.Calendar;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Button pickTimeBtn;
    private Button startBtn;
    private TextView selectedTimeTV;

    private TextView streakText;

    private int enableHour = 22;
    private int enableMin = 0;
    private TextView waitText;

    private boolean setFirstAlarm;
    private int dayOfYear;

    int hourToSet;
    int minToSet;

    private boolean hasSnoozed = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        pickTimeBtn =  (Button)
                root.findViewById(R.id.snoozeButton);
        waitText = root.findViewById(R.id.waitText);
        selectedTimeTV = (TextView) root.findViewById(R.id.idTVSelectedTime);
        startBtn = (Button)
                root.findViewById(R.id.startButton);
        streakText = (TextView) root.findViewById(R.id.streakText);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), StepActivity.class);
                getActivity().startActivity(myIntent);
            }
        });

        pickTimeBtn.setOnClickListener(new View.OnClickListener() {
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
                                hourToSet = hourOfDay;
                                minToSet = minute;
                                enableHour = hourToSet;
                                enableMin = minToSet;
                                hasSnoozed = true;
                                SharedPreferences sharedPref = getActivity().getSharedPreferences("timePref", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putInt("notifHour", hourToSet);
                                editor.putInt("notifMin", minute);
                                editor.apply();
                                selectedTimeTV.setText(String.valueOf(enableHour));
                                pickTimeBtn.setVisibility(View.GONE);
                                startBtn.setEnabled(false);
                                setAlarm(hourOfDay, minute);
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        return root;
    }
    public void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("timePref", Context.MODE_PRIVATE);
        enableHour = sharedPreferences.getInt("notifHour", enableHour);
        enableMin = sharedPreferences.getInt("notifMin", enableMin);

        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
        streakText.setText(String.valueOf(sharedPreferences.getInt("dayStreak", 0)));
        selectedTimeTV.setText(String.valueOf(enableHour));
        boolean isButtonEnabled = ((currentHour*60 + currentMinute <= enableHour*60+enableMin+15) && (currentHour*60 + currentMinute >= enableHour*60+enableMin));

        if(hasSnoozed) {
            pickTimeBtn.setVisibility(View.GONE);
            pickTimeBtn.setEnabled(false);
            startBtn.setEnabled(isButtonEnabled);
        } else {
            pickTimeBtn.setEnabled(isButtonEnabled);
            startBtn.setEnabled(isButtonEnabled);
        }

        if(isButtonEnabled) {
            waitText.setVisibility(View.GONE);
            pickTimeBtn.setVisibility(View.VISIBLE);
        } else {
            waitText.setVisibility(View.VISIBLE);
            pickTimeBtn.setVisibility(View.GONE);
        }
    }

    public void setAlarm(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(getContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        //Toast.makeText(getContext(), "Alarm for " + calendar.get(Calendar.MINUTE), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Nullable
    @Override
    public Context getContext() {
        return super.getContext();
    }
}