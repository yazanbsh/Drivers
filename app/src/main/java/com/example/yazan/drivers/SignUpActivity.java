package com.example.yazan.drivers;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Yazan on 12/16/2015.
 */
public class SignUpActivity extends AppCompatActivity {

    boolean isNet = false;
    String insertUserUrl = "http://www.gradwebsite-domain.usa.cc/signup_user.php";

    RequestQueue rq;

    public static final String PREFS_NAME = "Settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signupdialog);
        dateadjust();
        Button doneSignup = (Button) findViewById(R.id.donesignupbtn);
        doneSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupmethod();
            }
        });


        rq = Volley.newRequestQueue(getApplicationContext());
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

    public boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            if (exitValue == 0) isNet = true;
            else isNet = false;
            return (exitValue == 0);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void signupmethod() {

        final EditText BD = (EditText) findViewById(R.id.datesignupet);
        BD.setInputType(InputType.TYPE_NULL);

        final EditText user = (EditText) findViewById(R.id.usersignupet);
        final EditText pass = (EditText) findViewById(R.id.passsignupet);
        EditText conpass = (EditText) findViewById(R.id.passconfirmsignupet);
        final EditText email = (EditText) findViewById(R.id.emailsignupet);
        final EditText phone = (EditText) findViewById(R.id.phonesignupet);
        boolean ready=false;

        if (!isOnline())
            Toast.makeText(getBaseContext(), "please check your internet connection", Toast.LENGTH_LONG).show();

        else if (user.getText().toString().length() == 0) {
            Toast.makeText(getBaseContext(), "please enter username", Toast.LENGTH_LONG).show();
            ready=false;
        } else if (pass.getText().toString().length() == 0) {
            Toast.makeText(getBaseContext(), "please enter password", Toast.LENGTH_LONG).show();
            ready=false;
        } else if (conpass.getText().toString().length() == 0) {
            Toast.makeText(getBaseContext(), "please enter password confirm", Toast.LENGTH_LONG).show();
            ready=false;
        } else if (email.getText().toString().length() == 0) {
            Toast.makeText(getBaseContext(), "please enter Email", Toast.LENGTH_LONG).show();
            ready=false;
        } else if (phone.getText().toString().length() == 0) {
            Toast.makeText(getBaseContext(), "please enter Phone number", Toast.LENGTH_LONG).show();
            ready=false;
        } else if (BD.getText().toString().length() == 0) {
            Toast.makeText(getBaseContext(), "please enter Birthday", Toast.LENGTH_LONG).show();
            ready=false;
        } else if (!pass.getText().toString().equals(conpass.getText().toString())) {
            Toast.makeText(getBaseContext(), "entered password must match password confirm", Toast.LENGTH_LONG).show();
            ready=false;
        }
        else ready=true;

        //////
        if(ready) {

            StringRequest request = new StringRequest(Request.Method.POST, insertUserUrl, new Response.Listener<String>() {

                @Override
                public void onResponse(String arg0) {
                    // TODO Auto-generated method stub
                    //signSuccess();
                    Toast.makeText(getBaseContext(),arg0.toString(),Toast.LENGTH_LONG).show();

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    // TODO Auto-generated method stub
                    Toast.makeText(getBaseContext(),"something went wrong, please try again",Toast.LENGTH_LONG).show();

                }
            }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // TODO Auto-generated method stub
                    Map<String, String> parameters = new HashMap<String, String>();

                    String phonetype = phone.getText().toString();
                    String phonenum = phonetype.substring(0, 4) + "-" + phonetype.substring(4, 7) + "-" + phonetype.substring(7, 10);
                    String datefrmat = BD.getText().toString();
                    String datenum = datefrmat.substring(6, 10) + "-" + datefrmat.substring(3, 5) + "-" + datefrmat.substring(0, 2);


                    parameters.put("name", user.getText().toString());
                    parameters.put("email", email.getText().toString());
                    parameters.put("birthday", datenum);
                    parameters.put("phone", phonenum);
                    parameters.put("password", pass.getText().toString());

                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("Name", user.getText().toString());
                    editor.commit();

                    return parameters;
                }

            };

            rq.add(request);
        }
    }

    private void updateLabel() {

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        //editText.setText(sdf.format(myCalendar.getTime()));

    }

    public void dateadjust(){
        final EditText BD = (EditText) findViewById(R.id.datesignupet);
        BD.setInputType(InputType.TYPE_NULL);
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
                EditText editText = (EditText) findViewById(R.id.datesignupet);
                editText.setInputType(InputType.TYPE_NULL);
                editText.setText(sdf.format(myCalendar.getTime()));
            }

        };

        BD.setOnClickListener(new View.OnClickListener()

                              {
                                  @Override
                                  public void onClick (View v){
                                      // TODO Auto-generated method stub
                                      new DatePickerDialog(SignUpActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth, date, myCalendar
                                              .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                              myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                                  }
                              }

        );
        BD.setOnFocusChangeListener(new View.OnFocusChangeListener()

        {
            @Override
            public void onFocusChange (View v,boolean hasFocus){
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(BD.getWindowToken(), 0);

                if (hasFocus) {

                    new DatePickerDialog(SignUpActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });
    }

    public void signSuccess(){
        Toast.makeText(this,"account created successfully, you can login now",Toast.LENGTH_LONG).show();

        final EditText BD = (EditText) findViewById(R.id.datesignupet);
        BD.setInputType(InputType.TYPE_NULL);

        final EditText user = (EditText) findViewById(R.id.usersignupet);
        final EditText pass = (EditText) findViewById(R.id.passsignupet);
        EditText conpass = (EditText) findViewById(R.id.passconfirmsignupet);
        final EditText email = (EditText) findViewById(R.id.emailsignupet);
        final EditText phone = (EditText) findViewById(R.id.phonesignupet);

        user.getText().clear();
        pass.getText().clear();
        conpass.getText().clear();
        email.getText().clear();
        phone.getText().clear();
        BD.getText().clear();


    }


}
