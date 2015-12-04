package com.example.yazan.drivers;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MapsActivity extends ActionBarActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    double l1, l2;
    boolean isSet=false;
    boolean isLoged=false;
    boolean isNet=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        runnable.run();
        if (!isLoged)
        {
            TextView mytv= (TextView) findViewById(R.id.tvbar);
            mytv.setText("please login to use the app!");
        }

        loginmethod();
        ///////

        FloatingActionButton fab= (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView mytv = (TextView) findViewById(R.id.tvbar);
                mytv.setText("Hello there!");
                loginmethod();

            }
        });

        Button settinghomebutton= (Button)findViewById(R.id.settinhomegbutton);
        settinghomebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingmethod();
            }
        });
    }

//    private class checknet extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//            String data=null;
//            if (hasActiveInternetConnection())data="true";
//            else data="false";
//            return data;
//
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.

        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            mMap.setMyLocationEnabled(true);


            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                    @Override
                    public void onMyLocationChange(Location arg0) {
                        // TODO Auto-generated method stub
                        /*l1 = arg0.getLatitude();
                        l2 = arg0.getLongitude();*/
                        if (!isSet){
                            mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("U'r here!"));
                            isSet=true;
                        }
                    }
                });
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
//        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

    }

   /* public void fab_Onclick(View view) {
        TextView mytv= (TextView) findViewById(R.id.tvbar);
        mytv.setText("Hello there!");
    }
*/

    private void updateLabel() {

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        //editText.setText(sdf.format(myCalendar.getTime()));

    }

    Runnable runnable = new Runnable() {
        final Handler handler = new Handler();

        @Override
        public void run() {

            try{
                //do your code here
                if(isOnline())
                {
                    isNet=true;
                }
                else
                {
                    isNet=false;
                    Toast.makeText(getBaseContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }

                if (!isLoged)
                {
                    TextView mytv= (TextView) findViewById(R.id.tvbar);
                    mytv.setText("please login to use the app!");
                }
                //also call the same runnable
                handler.postDelayed(this, 20*1000);
            }
            catch (Exception e) {
                // TODO: handle exception
            }
            finally{
                //also call the same runnable
                handler.postDelayed(this, 20*1000);
            }
        }

    };
//    handler.postDelayed(runnable, 1000);


    public boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            if (exitValue == 0)isNet=true;
            else isNet=false;
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    public void loginmethod()
    {
        if (isLoged==false)
        {
            final Dialog startDialog=new Dialog(this,R.style.startdialog);
            startDialog.setContentView(R.layout.startdialog);
            final Dialog loginDialog=new Dialog(this,R.style.startdialog);
            final Dialog signupDialog=new Dialog(this,R.style.startdialog);
            loginDialog.setContentView(R.layout.logindialog);
            signupDialog.setContentView(R.layout.signupdialog);

            startDialog.show();
            Button loginButton= (Button) startDialog.findViewById(R.id.sdlogin);
            Button signupButton= (Button) startDialog.findViewById(R.id.sdsignup);
            final EditText editText= (EditText) signupDialog.findViewById(R.id.datesignupet);
            editText.setInputType(InputType.TYPE_NULL);

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginDialog.show();
                    Button doneLogin= (Button) loginDialog.findViewById(R.id.doneloginbtn);
                    doneLogin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText user= (EditText) loginDialog.findViewById(R.id.usersigninet);
                            EditText pass= (EditText) loginDialog.findViewById(R.id.passsigninet);
                            if (!isOnline())
                                Toast.makeText(getBaseContext(), "please check your internet connection", Toast.LENGTH_LONG).show();

                            else if (user.getText().toString().length()==0)
                            {
                                Toast.makeText(getBaseContext(), "please enter username", Toast.LENGTH_LONG).show();
                            }
                            else if (pass.getText().toString().length()==0)
                            {
                                Toast.makeText(getBaseContext(), "please enter password", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });

            signupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signupDialog.show();
                    Button doneSignup= (Button) signupDialog.findViewById(R.id.donesignupbtn);
                    doneSignup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText user= (EditText) signupDialog.findViewById(R.id.usersignupet);
                            EditText pass= (EditText) signupDialog.findViewById(R.id.passsignupet);
                            EditText conpass= (EditText) signupDialog.findViewById(R.id.passconfirmsignupet);
                            EditText email= (EditText) signupDialog.findViewById(R.id.emailsignupet);
                            EditText BD= (EditText) signupDialog.findViewById(R.id.datesignupet);

                            if (!isOnline())
                                Toast.makeText(getBaseContext(), "please check your internet connection", Toast.LENGTH_LONG).show();

                            else if (user.getText().toString().length()==0)
                            {
                                Toast.makeText(getBaseContext(), "please enter username", Toast.LENGTH_LONG).show();
                            }
                            else if (pass.getText().toString().length()==0)
                            {
                                Toast.makeText(getBaseContext(), "please enter password", Toast.LENGTH_LONG).show();
                            }
                            else if (conpass.getText().toString().length()==0)
                            {
                                Toast.makeText(getBaseContext(), "please enter password confirm", Toast.LENGTH_LONG).show();
                            }
                            else if (email.getText().toString().length()==0)
                            {
                                Toast.makeText(getBaseContext(), "please enter Email", Toast.LENGTH_LONG).show();
                            }
                            else if (BD.getText().toString().length()==0)
                            {
                                Toast.makeText(getBaseContext(), "please enter Birthday", Toast.LENGTH_LONG).show();
                            }
                            else if (!pass.getText().toString().equals(conpass.getText().toString()))
                            {
                                Toast.makeText(getBaseContext(), "entered password must match password confirm", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });

            /////////////
            final Calendar myCalendar = Calendar.getInstance();

            final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    // TODO Auto-generated method stub
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    String myFormat = "MM/dd/yy"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                    EditText editText= (EditText) signupDialog.findViewById(R.id.datesignupet);
                    editText.setInputType(InputType.TYPE_NULL);
                    editText.setText(sdf.format(myCalendar.getTime()));
                }

            };
            /////////////////////
            editText.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    new DatePickerDialog(MapsActivity.this,android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                    if (hasFocus){

                        new DatePickerDialog(MapsActivity.this,android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                }
            });
        }
    }

    public void settingmethod()
    {
        final Dialog settingdialog=new Dialog(this,R.style.AppTheme);
        settingdialog.setContentView(R.layout.settingpage);

        final Dialog editinfodialog=new Dialog(this,R.style.AppTheme);
        editinfodialog.setContentView(R.layout.setchangepage);

        final Dialog passchangedialog=new Dialog(this,R.style.AppTheme);
        passchangedialog.setContentView(R.layout.passchangefrag);
        settingdialog.show();

        Button changepass= (Button) settingdialog.findViewById(R.id.setpasschangebtn);
        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passchangedialog.show();
            }
        });

    }

}
