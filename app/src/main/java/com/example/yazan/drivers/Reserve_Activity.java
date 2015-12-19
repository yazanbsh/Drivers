package com.example.yazan.drivers;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.example.yazan.icab.SignUpActivity.PREFS_NAME;

public class Reserve_Activity extends AppCompatActivity {

    String assignCarUrl="http://gradwebsite-domain.usa.cc/assign.php";
    String reserveUrl="http://www.gradwebsite-domain.usa.cc/reservation.php";

    RequestQueue rq;

    public static boolean isLoged=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);
        dateadjust();
        timeadjust();

        Button nowbtn= (Button) findViewById(R.id.resnowbtn);
        Button laterbtn= (Button) findViewById(R.id.reslaterbtn);

        nowbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done2();
            }
        });
        laterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done();
            }
        });



    }

    public void dateadjust(){
        final EditText resdate = (EditText) findViewById(R.id.reservedateet);
        resdate.setInputType(InputType.TYPE_NULL);
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                EditText editText = (EditText) findViewById(R.id.reservedateet);
                editText.setInputType(InputType.TYPE_NULL);
                editText.setText(sdf.format(myCalendar.getTime()));
            }

        };

        resdate.setOnClickListener(new View.OnClickListener()

                                   {
                                       @Override
                                       public void onClick(View v) {
                                           // TODO Auto-generated method stub
                                           new DatePickerDialog(Reserve_Activity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth, date, myCalendar
                                                   .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                                   myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                                       }
                                   }

        );
        resdate.setOnFocusChangeListener(new View.OnFocusChangeListener()

        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(resdate.getWindowToken(), 0);

                if (hasFocus) {

                    new DatePickerDialog(Reserve_Activity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });
    }

    public void timeadjust() {
        final EditText restime = (EditText) findViewById(R.id.reservetimeet);
        restime.setInputType(InputType.TYPE_NULL);
        restime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(Reserve_Activity.this,android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        restime.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        restime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(restime.getWindowToken(), 0);
                if (hasFocus) {
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(Reserve_Activity.this,android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            restime.setText(selectedHour + ":" + selectedMinute);
                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                }
            }
        });
    }

    public void done(){
        Intent returnIntent = new Intent();
        EditText restime = (EditText) findViewById(R.id.reservetimeet);
        EditText resdate = (EditText) findViewById(R.id.reservedateet);
        if (resdate.getText().toString().length()==0){
            Toast.makeText(getBaseContext(),"please select date",Toast.LENGTH_LONG).show();
        }
        else if (restime.getText().toString().length()==0){
            Toast.makeText(getBaseContext(),"please select time",Toast.LENGTH_LONG).show();
        }
        else{
        String datestring=resdate.getText().toString();
        String datenum = datestring.substring(6, 10) + "-" + datestring.substring(3, 5) + "-" + datestring.substring(0, 2);
        String timenum;
        String timestrig=restime.getText().toString();
        String all;
        if (timestrig.length()==4){
            timenum="0"+timestrig;
        }
        else{
            timenum=timestrig;
        }
        all=datenum+" "+timenum+":00";
//        Toast.makeText(getBaseContext(),timenum,Toast.LENGTH_LONG).show();
//        Toast.makeText(getBaseContext(),all,Toast.LENGTH_LONG).show();


            returnIntent.putExtra("flag","later");
            returnIntent.putExtra("all",all);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        }
    }

    public void done2(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("flag","now");
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

    }
}
