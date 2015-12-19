package com.example.yazan.drivers;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.example.yazan.drivers.SignUpActivity.PREFS_NAME;


/**
 * Created by Yazan on 12/16/2015.
 */
public class LoginActivity extends AppCompatActivity {

    boolean isNet=false;
    String url="http://www.gradwebsite-domain.usa.cc/login_car.php";

    RequestQueue rq;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        // TODO Auto-generated method stub
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logindialog);

        Button doneLogin= (Button) findViewById(R.id.doneloginbtn);
        doneLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginMethode();
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
            int     exitValue = ipProcess.waitFor();
            if (exitValue == 0)isNet=true;
            else isNet=false;
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    public void loginMethode(){

        final EditText user = (EditText) findViewById(R.id.usersigninet);
        final EditText pass = (EditText) findViewById(R.id.passsigninet);
        boolean ready=false;
        if (!isOnline())
            Toast.makeText(getBaseContext(), "please check your internet connection", Toast.LENGTH_LONG).show();

        else if (user.getText().toString().length() == 0) {
            Toast.makeText(getBaseContext(), "please enter username", Toast.LENGTH_LONG).show();
        } else if (pass.getText().toString().length() == 0) {
            Toast.makeText(getBaseContext(), "please enter password", Toast.LENGTH_LONG).show();
        }
        else ready=true;

        if(ready) {
            String em=(user.getText().toString());
            String pas=pass.getText().toString();
            String url2=url+"?driver="+em+"&password="+pas;
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url2,null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject arg0) {
                    // TODO Auto-generated method stub
                    try {
                        JSONArray array=arg0.getJSONArray("users");
                        JSONObject object=array.getJSONObject(0);
                        String string=object.getString("message");
//                        Toast.makeText(getBaseContext(),arg0.toString(),Toast.LENGTH_LONG).show();
                        if (string.equals("log_in_failed.")){
                            Toast.makeText(getBaseContext(),"erooooooooooooooooor",Toast.LENGTH_LONG).show();
                        }
                        else {
                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("Id", string);
                            editor.putBoolean("Flag", true);
                            editor.commit();
                            loginSuccess(user.getText().toString());

                        }

                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    // TODO Auto-generated method stub
                    Toast.makeText(getBaseContext(),"something went wrong, please try again",Toast.LENGTH_LONG).show();

                }
            }) /*{

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // TODO Auto-generated method stub
                    Map<String, String> parameters = new HashMap<String, String>();

                    parameters.put("email", user.getText().toString());
                    parameters.put("password", pass.getText().toString());
                    return parameters;
                }

            }*/;

            rq.add(request);
        }

    }

    public void loginSuccess(String username){
        Toast.makeText(getBaseContext(), "login success", Toast.LENGTH_LONG).show();
        final EditText user = (EditText) findViewById(R.id.usersigninet);
        final EditText pass = (EditText) findViewById(R.id.passsigninet);
        user.getText().clear();
        pass.getText().clear();

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",username);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

    }


}
