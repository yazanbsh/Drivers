package com.example.yazan.drivers;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.example.yazan.drivers.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.example.yazan.drivers.CommonUtilities.EXTRA_MESSAGE;
import static com.example.yazan.drivers.CommonUtilities.SENDER_ID;
import static com.example.yazan.drivers.SignUpActivity.PREFS_NAME;

public class MapsActivity extends ActionBarActivity {

    AsyncTask<Void, Void, Void> mRegisterTask;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    double l1, l2;
    boolean tst=true;
    boolean isSet=false;
    //    boolean isLoged=false;
    boolean isNet=false;
    LatLng userLatLng;

    Dialog startDialog;

    String showUserUrl ="http://www.gradwebsite-domain.usa.cc/show_users.php";
    String logouturl="http://gradwebsite-domain.usa.cc/logout_car.php";
    String carLocationurl ="http://www.gradwebsite-domain.usa.cc/car_location.php";
    String assignCarUrl="http://gradwebsite-domain.usa.cc/assign.php";
    String reserveUrl="http://www.gradwebsite-domain.usa.cc/reservation.php";
    String reservetime="";

    RequestQueue rq;

    public static String name;
    public static String id = "0";
    public static boolean isLoged=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        name = settings.getString("Name", "user");

        id = settings.getString("Id", "0");
        isLoged=settings.getBoolean("Flag",false);

        startDialog=new Dialog(this,R.style.startdialog);
        startDialog.setContentView(R.layout.startdialog);


//        runnable.run();
        if (!isLoged)
        {
            TextView mytv= (TextView) findViewById(R.id.tvbar);
            mytv.setText("please login or sign up to use the app!");
        }

        loginmethod();
        ///////


        FloatingActionButton fab= (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView mytv = (TextView) findViewById(R.id.tvbar);
                if (!isLoged) {
                    mytv.setText("Hello there!");
                    loginmethod();
                }

                else {
                    setCarLocation();
                    Toast.makeText(getBaseContext(),"showing now",Toast.LENGTH_SHORT).show();
                    showUsersMethode();
//                    assignCarMethode();

                }

            }
        });

        Button settinghomebutton= (Button)findViewById(R.id.settinhomegbutton);
        settinghomebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingmethod();
            }
        });

        GCMRegistrar.checkDevice(this);

        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);

        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                DISPLAY_MESSAGE_ACTION));

        // Get GCM registration id
        final String regId = GCMRegistrar.getRegistrationId(this);
        // Check if regid already presents
        if (regId.equals("")) {
            // Registration is not present, register now with GCM
            GCMRegistrar.register(this, SENDER_ID);
        } else {
            // Device is already registered on GCM
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.
                Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        // Register on our server
                        // On server creates a new user
                        ServerUtilities.register(context, name, id, regId);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };
                mRegisterTask.execute(null, null, null);
            }
        }

    }


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

                            MarkerOptions marker=new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("U'r here!").snippet("U'r here!");
                            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.user));
                            mMap.addMarker(marker);
                            userLatLng=new LatLng(arg0.getLatitude(),arg0.getLongitude());
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



    /**
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
     mytv.setText("please login or sign up to use the app!");
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
     */
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

            startDialog.show();
            Button loginButton= (Button) startDialog.findViewById(R.id.sdlogin);

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    loginDialog.show();
                    Intent intent = new Intent(MapsActivity.this,LoginActivity.class);
                    startActivityForResult(intent, 1);

                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                isLoged=true;
                TextView textView=(TextView) findViewById(R.id.tvbar);
                textView.setText("hello "+result);
                startDialog.dismiss();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        else if (requestCode==2){
            String flag=data.getStringExtra("flag");
            if (flag.equals("now")){
                Toast.makeText(getBaseContext(),"do now",Toast.LENGTH_SHORT).show();
                assignCarMethode();
            }
            else if (flag.equals("later")){
                Toast.makeText(getBaseContext(),"do later",Toast.LENGTH_SHORT).show();
                String all=data.getStringExtra("all");
                reservetime=all;
                reserveationmethode();
            }

        }
    }//onActivityResult

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

        Button logoutButton= (Button) settingdialog.findViewById(R.id.logoutbtn);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutMethode();
                settingdialog.dismiss();
                if (tst){
                    tst=false;
                }
                else isLoged=false;

            }
        });

    }


    public void logoutMethode(){

        boolean ready=false;
        if (!isOnline())
            Toast.makeText(getBaseContext(), "please check your internet connection", Toast.LENGTH_LONG).show();
        else ready=true;

        if(ready) {

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

            final String id = settings.getString("Id", "0");

            rq = Volley.newRequestQueue(getApplicationContext());
            String url=logouturl+"?id="+id;
            Log.d("url is",url);


            JsonObjectRequest jOR = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>()
            {

                @Override
                public void onResponse(JSONObject response) {
                    // TODO Auto-generated method stub

                    try {

                        JSONArray array1=response.getJSONArray("carLogout");
                        JSONObject object1=array1.getJSONObject(0);
                        String status = object1.getString("message");
//                        Toast.makeText(getBaseContext(),response.toString(),Toast.LENGTH_LONG).show();

                        if(status.equals("logged_out_successfuly.")){
                            Toast.makeText(getBaseContext(),"logged out",Toast.LENGTH_LONG).show();
                            isLoged = false;
                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("Id", "");
                            editor.putBoolean("Flag", false);
                            editor.commit();

                        }
                        else {
                            Toast.makeText(getBaseContext(),"noooo work",Toast.LENGTH_LONG).show();
                        }

                    }catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    // TODO Auto-generated method stub
                    Toast.makeText(getBaseContext(),"something went wrong, please try again",Toast.LENGTH_LONG).show();

                }
            })/*{

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // TODO Auto-generated method stub
                    Map<String, String> parameters = new HashMap<String, String>();

                    parameters.put("id","27");

                    return parameters;
                }

            }*/;

            rq.add(jOR);
            isLoged=settings.getBoolean("Flag",false);

        }

    }


    public void setCarLocation(){
        rq = Volley.newRequestQueue(getApplicationContext());

        String s1=""+userLatLng.latitude;
        String s2=""+userLatLng.longitude;
        String s3="27";
        String url= carLocationurl +"?id="+s3+"&geolat="+s1+"&geolong="+s2;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                // TODO Auto-generated method stub
//                    loginSuccess(user.getText().toString());
                try {
                    JSONArray array=arg0.getJSONArray("users");
                    JSONObject object=array.getJSONObject(0);
                    String string=object.getString("message");

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
//                Toast.makeText(getBaseContext(),"something went wrong, please try again",Toast.LENGTH_LONG).show();
                Log.d("test", arg0.toString());

            }
        });

        rq.add(request);
    }


    /**
     * Receiving push messages
     * */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getApplicationContext());

            /**
             * Take appropriate action on this message
             * depending upon your app requirement
             * For now i am just displaying it on the screen
             * */
            Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();

            // Releasing wake lock
            WakeLocker.release();
        }
    };

    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        unregisterReceiver(mHandleMessageReceiver);
        GCMRegistrar.onDestroy(this);
        super.onDestroy();
    }

    public void showUsersMethode(){
        boolean ready=false;
        if (!isOnline())
            Toast.makeText(getBaseContext(), "please check your internet connection", Toast.LENGTH_LONG).show();
        else ready=true;


        if(ready) {

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

            final String id = settings.getString("Id", "0");
            rq = Volley.newRequestQueue(getApplicationContext());
            String url= showUserUrl +"?id="+id;
            JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        JSONArray array=jsonObject.getJSONArray("cars");
                        for (int i=0;i<array.length();i+=2){
                            JSONObject object=array.getJSONObject(i);
                            JSONObject object2=array.getJSONObject(i+1);

                            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                                // Use default InfoWindow frame
                                @Override
                                public View getInfoWindow(Marker arg0) {
                                    return null;
                                }

                                // Defines the contents of the InfoWindow
                                @Override
                                public View getInfoContents(Marker arg0) {

                                    // Getting view from the layout file info_window_layout
                                    View v = getLayoutInflater().inflate(R.layout.info_window, null);
                                    TextView tvDrv = (TextView) v.findViewById(R.id.tv_driver);
//                                    TextView tvOfc = (TextView) v.findViewById(R.id.tv_office);
//                                    TextView tvPhn= (TextView) v.findViewById(R.id.tv_phone);
                                    tvDrv.setText(arg0.getSnippet());

                                    // Returning the view containing InfoWindow contents
                                    return v;

                                }
                            });

                            String snip=object.getString("driver") + "\n" + object2.getString("officeName")+"\n"+object.getString("phone");



                            MarkerOptions markerOptions=new MarkerOptions().position(new LatLng(object.getDouble("geolat"),
                                    object.getDouble("geolong"))).snippet(snip);

                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car));

                            Marker marker = mMap.addMarker(markerOptions);

                            // Showing InfoWindow on the GoogleMap
//                            marker.showInfoWindow();
//                            mMap.addMarker(marker);
                        }
                    }
                    catch (JSONException e){
                        Toast.makeText(getBaseContext(),"error in show cars",Toast.LENGTH_LONG).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });
            rq.add(request);

        }

    }

    public void assignCarMethode() {
        boolean ready = false;
        if (!isOnline())
            Toast.makeText(getBaseContext(), "please check your internet connection", Toast.LENGTH_LONG).show();
        else ready = true;


        if (ready) {

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

            final String id = settings.getString("Id", "0");
            rq = Volley.newRequestQueue(getApplicationContext());
            String url=assignCarUrl+"?userId="+id+"&fromGeolat="+userLatLng.latitude+
                    "&fromGeolong="+userLatLng.longitude+"&toGeolat="+"&toGeolong=";

            JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    Toast.makeText(getBaseContext(),jsonObject.toString(),Toast.LENGTH_LONG).show();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(getBaseContext(),"error in assign",Toast.LENGTH_LONG).show();

                }
            });
            rq.add(request);
        }
    }

    public void reserveationmethode(){
        boolean ready = false;
        if (!isOnline())
            Toast.makeText(getBaseContext(), "please check your internet connection", Toast.LENGTH_LONG).show();
        else ready = true;


        if (ready) {

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

            final String id = settings.getString("Id", "0");
            rq = Volley.newRequestQueue(getApplicationContext());
            String url=reserveUrl+"?userId="+id+"&fromGeolat="+userLatLng.latitude+
                    "&fromGeolong="+userLatLng.longitude+"&time="+reservetime+"&toGeolat="+"&toGeolong=";
            String url2="http://www.gradwebsite-domain.usa.cc/reservation.php?userId=27&fromGeolat=32&fromGeolong=32&time=2022-7-28 06:43:00&toGeolat&toGeolong";

            JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    Toast.makeText(getBaseContext(),jsonObject.toString(),Toast.LENGTH_LONG).show();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(getBaseContext(),"error in reserve",Toast.LENGTH_LONG).show();
                    Log.d("testreserve", volleyError.toString());
                    volleyError.printStackTrace();

                }
            });
            rq.add(request);
        }
    }


}
