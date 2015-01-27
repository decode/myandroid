package com.example.myapplication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by home on 1/23/15.
 */
public class AlarmActivity extends Activity {

    private TextView txtDate;
    private TextView txtTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        processView();
        processController();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    private void processController() {
    }

    private void processView() {
        txtDate = (TextView) findViewById(R.id.txt_alarm_date);
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
        txtDate.setText(date_format.format(Calendar.getInstance().getTime()));
        txtTime = (TextView) findViewById(R.id.txt_alarm_time);
        date_format = new SimpleDateFormat("HH:mm");
        txtTime.setText(date_format.format(Calendar.getInstance().getTime()));
    }

    public void setAlarm(View view) {
        int id = view.getId();
        Intent result;
        switch (id) {
            case R.id.btn_alarm_reset:
                result = getIntent();
                result.putExtra("pickedDatetime", "");
                setResult(Activity.RESULT_OK, result);
                break;
            case R.id.btn_alram_ok:
                result = getIntent();
                result.putExtra("pickedDatetime", txtDate.getText() + " " + txtTime.getText());
                setResult(Activity.RESULT_OK, result);
                break;
            case R.id.btn_alarm_cancel:
                break;
        }
        finish();
    }

    public void pickDate(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void pickTime(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            TextView text_date = (TextView) getActivity().findViewById(R.id.txt_alarm_time);
            String picked_time = hourOfDay + ":" + minute;
            text_date.setText(picked_time);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            TextView text_date = (TextView) getActivity().findViewById(R.id.txt_alarm_date);
            String picked_date = year + "-" + (month+1) + "-" + day;
            text_date.setText(picked_date);
        }
    }
}
