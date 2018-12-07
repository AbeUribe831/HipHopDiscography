package com.example.android.hiphopdiscography;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    String ipAddress = "18.220.86.83";
    boolean failedLogin = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final TextView registerButton = (TextView) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(myIntent);
            }
        });

        final Button loginButton = (Button) findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    public void login(){
       Thread loginThread = new Thread(new Runnable() {
            @Override
            public void run() {
                EditText userNameEditText = (EditText)findViewById(R.id.usernameText);
                String userName = userNameEditText.getText().toString();
                EditText passwordEditText = (EditText)findViewById(R.id.passwordText);
                String password = passwordEditText.getText().toString();

                URL url = null;
                HttpURLConnection conn = null;
                try {
                    //TODO::get ip-address and api from mohit

                    url = new URL("http://"+ipAddress+":3000/api/signin?username="+userName+"&password="+password);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-Type","application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoInput(true);

                    Log.i("SendInfo","Info is sent");
                    //return 0 or 1 for if user is artist

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuffer content = new StringBuffer();
                    while((inputLine = in.readLine()) != null){
                        content.append(inputLine);
                    }
                    in.close();

                    JSONObject serverResponce = new JSONObject(content.toString());
                    Log.i("serverTest","type: " +serverResponce.getString("type"));
                    Log.i("serverTest","artist_id " + serverResponce.getString("artist_id"));
                    conn.disconnect();
                    //login credentials work
                    if(serverResponce.getString("type").equals("true")){
                        Intent myIntent = new Intent(LoginActivity.this,
                                UserMainActivity.class);
                        //TODO::Check this works: send is artist id to user activity
                        myIntent.putExtra("artist_id",serverResponce.getInt("artist_id"));
                        startActivity(myIntent);
                    }
                    //login credentials fail
                    else{
                        Log.d("loginT", "failed login");
                        failedLogin = true;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    conn.disconnect();
                }

            }
        });
       loginThread.start();
       //nasty workaround that I don't feel like changing, AsyncTask should be the solution
       while(loginThread.isAlive()){
       }
        if(failedLogin){
            failedLogin = false;
            Toast.makeText(getApplicationContext(),
                    "failed login credentials", Toast.LENGTH_SHORT).show();
        }
    }
}
