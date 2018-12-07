package com.example.android.hiphopdiscography;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.ArrayList;

public class RecordLabelActivity extends AppCompatActivity {
    String ipAddress = "18.220.86.83";
    String artistName;
    int myArtistID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_label);

        Intent intent = getIntent();
        artistName = intent.getStringExtra("artist_name");
        myArtistID = intent.getIntExtra("artist_id",-1);

        final ArrayList<String> recordLabelList = new ArrayList<String>();
        final ListView listView = (ListView) findViewById(R.id.record_label_list);
        Thread RLThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url;
                    if(artistName.equals("")){
                        url = new URL("http://" + ipAddress + ":3000/api/all_record_labels");
                    }
                    else{
                        //TODO::change url
                        url = new URL("http://" + ipAddress + ":3000/api/record_label_by_artist?" +
                                "artist_name="+artistName);
                    }

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-Type","application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoInput(true);

                    //StringBuffer content = new StringBuffer();
                    Log.i("recordlabelCh","response code: " + conn.getResponseCode());
                    Log.i("recordlabelCh", "request method: " + conn.getRequestMethod());
                    Log.i("recordlabelCh", "response message: " + conn.getResponseMessage());

                    BufferedReader in = new BufferedReader(
                           new InputStreamReader(conn.getInputStream()));

                    String inputLine;
                    StringBuffer content = new StringBuffer();

                    while((inputLine = in.readLine()) != null){
                    Log.i("recordlabelCh","Message from DB: " + inputLine);
                      content.append(inputLine);
                    }
                    in.close();


            JSONObject reader = new JSONObject(content.toString());
            JSONArray dataArray = (JSONArray) reader.get("data");
            Log.i("recordlabelCh","Record Label: " + dataArray.getString(0));
            //insert both id and name for record label
            for(int i=0;i<dataArray.length();i++){
                recordLabelList.add(
                        dataArray.getJSONObject(i).getString("r_name"));
            }
                    //TODO::set on click listenter for listView to manipulate the list

                } catch (MalformedURLException e) {
                    Log.d("ErrorDB",e.getMessage());
                } catch (ProtocolException e) {
                    Log.d("ErrorDB",e.getMessage());
                } catch (IOException e) {
                    Log.d("ErrorDB",e.getMessage());
                } catch (JSONException e) {
                 Log.d("ErrorDB",e.getMessage());
                }
            }
        });
        RLThread.start();
        while(RLThread.isAlive()){
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,recordLabelList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position,convertView,parent);

                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                tv.setTextColor(Color.WHITE);
                return view;
            }
        };
        //After this, list should be displayed
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object recordLabelName = adapterView.getItemAtPosition(i);
                Intent myIntent = new Intent(RecordLabelActivity.this,
                        ArtistActivity.class);
                //TODO::Check this works: send is artist id to user activity
                myIntent.putExtra("artist_id",myArtistID);
                myIntent.putExtra("record_label",recordLabelName.toString());
                startActivity(myIntent);
            }
        });

    }
}
