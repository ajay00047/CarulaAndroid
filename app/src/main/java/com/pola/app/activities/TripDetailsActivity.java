package com.pola.app.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.pola.app.R;
import com.pola.app.Utils.Constants;
import com.pola.app.Utils.Singleton;
import com.pola.app.Utils.StringUtil;
import com.pola.app.Utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TripDetailsActivity extends AppCompatActivity {


    private static final String[] passengers = {"1", "2", "3", "4", "5", "6", "7"};
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    TimePickerDialog timePickerDialog;
    private LinearLayout dateLayout, timeLayout, fareLayout, passengersLayout;
    private TextView selectDate, selectTime;
    private Button next;
    private EditText fare;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        dateLayout = (LinearLayout) findViewById(R.id.date_label);
        timeLayout = (LinearLayout) findViewById(R.id.time_label);
        fareLayout = (LinearLayout) findViewById(R.id.fare_label);
        passengersLayout = (LinearLayout) findViewById(R.id.passengers_label);
        selectDate = (TextView) findViewById(R.id.select_date);
        selectTime = (TextView) findViewById(R.id.select_time);
        fare = (EditText) findViewById(R.id.fare);
        next = (Button) findViewById(R.id.button_next);

        if (Singleton.rideNow) {
            dateLayout.setVisibility(View.GONE);
            timeLayout.setVisibility(View.GONE);
            Singleton.tripDetailsBean.setDate(StringUtil.getCurrentDate());
            Singleton.tripDetailsBean.setTime(StringUtil.getCurrentPlusTime(5));
            if(Singleton.userBean.getIam().equals("P")){
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            dateLayout.setVisibility(View.VISIBLE);
            timeLayout.setVisibility(View.VISIBLE);
        }

        if (Singleton.userBean.getIam().equals("O")) {
            passengersLayout.setVisibility(View.VISIBLE);
            fareLayout.setVisibility(View.VISIBLE);
        } else {
            passengersLayout.setVisibility(View.GONE);
            fareLayout.setVisibility(View.GONE);
        }


        myCalendar = Calendar.getInstance();

        spinner = (Spinner) findViewById(R.id.passengers_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(TripDetailsActivity.this,
                android.R.layout.simple_spinner_item, passengers);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //spinner.setOnItemSelectedListener(TripDetailsActivity.this);

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        timeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });

        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                selectTime.setText(convertDateTo12HourFormat(hourOfDay, minute));

            }
        }, 07, 30, false);

        dateLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(TripDetailsActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (Singleton.userBean.getIam().equals("O")) {
                                            if (Utils.isEmpty(fare)) {
                                                Toast.makeText(TripDetailsActivity.this, "Fare cannot be blank or 0", Toast.LENGTH_SHORT).show();
                                            }else {
                                                Singleton.tripDetailsBean.setFare(Integer.parseInt(fare.getText().toString().trim()));
                                                Singleton.tripDetailsBean.setPassengers(Integer.parseInt(spinner.getSelectedItem().toString().trim()));
                                            }
                                        }

                                        if ("Select Date".equals(selectDate.getText()) && !Singleton.rideNow) {
                                            Toast.makeText(TripDetailsActivity.this, "Date not selected", Toast.LENGTH_SHORT).show();
                                        } else if ("Select Time".equals(selectTime.getText()) && !Singleton.rideNow) {
                                            Toast.makeText(TripDetailsActivity.this, "Time not selected", Toast.LENGTH_SHORT).show();
                                        } else {
                                            if(!Singleton.rideNow) {

                                                if ((StringUtil.convertDate2Epoch(selectDate.getText().toString() + " " + selectTime.getText().toString()) - StringUtil.currentEpoch()) > 0) {
                                                    Singleton.tripDetailsBean.setDate(selectDate.getText().toString());
                                                    Singleton.tripDetailsBean.setTime(selectTime.getText().toString());

                                                    Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(TripDetailsActivity.this, "Trip Timing cannot be less than current time!", Toast.LENGTH_SHORT).show();
                                                }
                                            }else{
                                                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                                                startActivity(intent);
                                            }


                                        }
                                    }

                                }

        );

    }


    private void updateLabel() {
        String myFormat = "dd MMMM, yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        selectDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateTime() {
        String myFormat = "hh:mm AM/PM"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        selectTime.setText(sdf.format(myCalendar.getTime()));
    }

    private String convertDateTo12HourFormat(int hours, int min) {
        String ampm = "AM";

        if (hours >= 12) {
            ampm = "PM";
            hours -= 12;
        }
        if (hours == 0)
            hours = 12;

        return (String.format("%02d", hours) + ":" + String.format("%02d", min) + " " + ampm);
    }
}
