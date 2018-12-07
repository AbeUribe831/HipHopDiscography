package com.example.android.hiphopdiscography;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RegisterActivity extends AppCompatActivity {
    String ipAddress = "18.220.86.83";
    boolean isArtist = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final CheckBox isArtistButton = (CheckBox) findViewById(R.id.artist_checkbox);
        isArtistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText artistNameEditText = (EditText) findViewById(R.id.artist_name_text);
                if(isArtistButton.isChecked()){
                    isArtist = true;
                    artistNameEditText.setVisibility(View.VISIBLE);
                }
                else{
                    isArtist = false;
                    artistNameEditText.setVisibility(View.GONE);
                }
            }
        });

        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            EditText usernameEditText = (EditText) findViewById(R.id.username_text);
            EditText emailEditText = (EditText) findViewById(R.id.email_text);
            EditText artistNameEditText = (EditText) findViewById(R.id.artist_name_text);
            EditText passwordEditText = (EditText) findViewById(R.id.password_text);
            @Override
            public void onClick(View view) {
                 new Thread(new Runnable() {
                     @TargetApi(Build.VERSION_CODES.KITKAT)
                     @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                     @Override
                     public void run() {
                         URL url = null;
                         int isArtistNum=0;
                         if(isArtist){
                             isArtistNum=1;
                         }
                         try {
                             String urlParams = "username="+usernameEditText.getText().toString()+
                                     "&email="+emailEditText.getText().toString()+
                                     "&artist="+isArtistNum+
                                     "&artist_name="+artistNameEditText.getText().toString()+
                                     "&password="+passwordEditText.getText().toString();

                             byte[] postData = urlParams.getBytes(StandardCharsets.UTF_8);
                             int postDataLength = postData.length;
                             url = new URL("http://"+ipAddress+":3000/api/signup");
                             HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                             conn.setDoOutput(true);
                             conn.setInstanceFollowRedirects(false);
                             conn.setRequestMethod("POST");
                             conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                             conn.setRequestProperty("charset","utf-8");
                             conn.setRequestProperty("Content-Length",Integer.toString(postDataLength));
                             conn.setUseCaches(false);

                             try(DataOutputStream wr = new DataOutputStream(conn.getOutputStream())){
                                 wr.write(postData);
                             }
                             BufferedReader in = new BufferedReader(
                                     new InputStreamReader(conn.getInputStream()));

                             String inputLine;
                             StringBuffer content = new StringBuffer();

                             while((inputLine = in.readLine()) != null){
                                 content.append(inputLine);
                             }
                             in.close();

                             try {
                                 JSONObject reader = new JSONObject(content.toString());
                             } catch (JSONException e) {
                                 e.printStackTrace();
                             }
                         } catch (MalformedURLException e) {
                             e.printStackTrace();
                         } catch (ProtocolException e) {
                             e.printStackTrace();
                         } catch (IOException e) {
                             e.printStackTrace();
                         }

                     }
                 }).start();
            }
        });
    }
}
