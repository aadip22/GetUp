package com.example.cac_2024;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Calendar;
import java.util.Random;

public class StepActivity extends AppCompatActivity implements SensorEventListener {

    private static final int SENSOR_PERMISSION_CODE = 1;

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private TextView stepCountTextView;
    private TextView goalTextView;
    private Chronometer chronometer;
    private ProgressBar progressBar;

    private LottieAnimationView trophyAnim;
    private LottieAnimationView failAnim;
    private long elapsedTime = 0;
    private int stepCount = 0;

    private boolean isStarting = false;
    private int goalSteps = 100;

    private double goalDistance = goalSteps * 2.5;

    private int timeMins = 15;
    private TextView goalTextView1;

    private Button goNextButton;

    private boolean hasFinished = false;

    private boolean won = false;


    private ProgressBar progressBarCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, SENSOR_PERMISSION_CODE);
        } else {
            initializeSensors();
        }

        stepCountTextView = findViewById(R.id.stepCountTextView);
        goalTextView = findViewById(R.id.progress_text);
        chronometer = findViewById(R.id.chronometer);
        progressBar = findViewById(R.id.progressBar);
        progressBarCircle = findViewById(R.id.progress_bar);
        goalTextView1 = findViewById(R.id.goalTextView);
        goNextButton = findViewById(R.id.goNextButton);
        trophyAnim = findViewById(R.id.trophy_view);
        failAnim = findViewById(R.id.fail_view);

        resetStepCount();

//        chronometer.setBase(SystemClock.elapsedRealtime());

        goNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("timePref", Context.MODE_PRIVATE);
                int streak = sharedPref.getInt("dayStreak", 20);
                SharedPreferences.Editor editor = sharedPref.edit();

                if(won) {
                    editor.putInt("dayStreak", streak + 1);
                } else {
                    editor.putInt("dayStreak", 0);
                }

                editor.apply();
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(myIntent);
            }
        });
    }
    protected void onStart(){
        super.onStart();
        goalTextView1.setVisibility(TextView.INVISIBLE);
        resetStepCount();
        hasFinished = false;
        goNextButton.setVisibility(TextView.GONE);
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("timePref", Context.MODE_PRIVATE);
        goalSteps = sharedPref.getInt("stepGoal", 750);
        timeMins = sharedPref.getInt("minutes", 15);
        chronometer.setBase(SystemClock.elapsedRealtime() + 1000*timeMins*60);
        chronometer.setCountDown(true);
        chronometer.start();
        goalDistance = goalSteps * 2.5;
        goalTextView.setText(String.format("%d/%d steps", 0, goalSteps));
    }
    private void initializeSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            //stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        }
        if (stepSensor == null) {
            Toast.makeText(this, "Step counting sensor is not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null && stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR && !hasFinished) {
            stepCount += event.values.length;
            stepCountTextView.setText(String.valueOf(stepCount));
            updateProgress();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }

    private void updateProgress() {
        if(!hasFinished)
        {
            long currentTime = SystemClock.elapsedRealtime();
            long elapsedTimeSeconds = (currentTime - chronometer.getBase()) / 1000;

            float distanceCovered = stepCount * 2.5f;
            int progress = (int) ((distanceCovered / goalDistance) * 100);
            progressBar.setProgress(progress);
            goalTextView.setText(String.format("%d/%d steps", stepCount, goalSteps));
            progressBarCircle.setProgress(progress);

            String progressText = String.format("%02d:%02d", (elapsedTimeSeconds / 60)*-1, (elapsedTimeSeconds % 60)*-1);
            chronometer.setText(progressText);

            if(stepCount > goalSteps) {
                chronometer.stop();
                hasFinished = true;
                chronometer.setText("good job!");
                goalTextView.setVisibility(TextView.INVISIBLE);
                goalTextView1.setVisibility(TextView.INVISIBLE);
                chronometer.setVisibility(TextView.INVISIBLE);
                stepCountTextView.setVisibility(TextView.INVISIBLE);
                goNextButton.setVisibility(TextView.VISIBLE);
                trophyAnim.playAnimation();
                won = true;
            }

            if(SystemClock.elapsedRealtime() > chronometer.getBase()) {
                hasFinished = true;
                chronometer.stop();
                goalTextView.setVisibility(TextView.INVISIBLE);
                goalTextView1.setVisibility(TextView.INVISIBLE);
                chronometer.setVisibility(TextView.INVISIBLE);
                stepCountTextView.setVisibility(TextView.INVISIBLE);
                goNextButton.setVisibility(TextView.VISIBLE);
                if(stepCount < goalSteps) {
                    chronometer.setText("uh oh!");
                    failAnim.playAnimation();

                } else {
                    chronometer.setText("good job!");
                }
            }
        }
    }

    private void resetStepCount() {
        isStarting = true;
        stepCount = 0;
        stepCountTextView.setText(String.valueOf(stepCount));
        progressBarCircle.setProgress(0);
        goalTextView.setText(String.format("0/%d steps", goalSteps));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SENSOR_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeSensors();
            } else {
                Toast.makeText(this, "Permission denied, step counting will not work", Toast.LENGTH_SHORT).show();
            }
        }
    }
}