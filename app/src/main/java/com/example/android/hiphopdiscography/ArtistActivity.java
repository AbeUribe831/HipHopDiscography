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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

//TODO::Test that artist is passsed to Artist Pageq
public class ArtistActivity extends AppCompatActivity {
    String ipAddress = "18.220.86.83";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        final ArrayList<String> artistList = new ArrayList<String>();
        Intent intent = getIntent();
        final int myArtistID = intent.getIntExtra("artist_id",-1);
        final String recordLabel = intent.getStringExtra("record_label");
        Log.d("can_edit","Artist activity " + myArtistID);
        final ListView listView = (ListView) findViewById(R.id.artist_list);

        Thread artistThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url;
                    if(recordLabel.equals("")) {
                        url = new URL("http://" + ipAddress + ":3000/api/artists");
                    }
                    else{
                        url = new URL("http://"+ipAddress+":3000/api/artists_on_record_label" +
                                "?rl_name="+recordLabel);
                    }

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-Type","application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoInput(true);

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));

                    String inputLine;
                    StringBuffer content = new StringBuffer();

                    while((inputLine = in.readLine()) != null){
                        content.append(inputLine);
                    }
                    in.close();


                    JSONObject reader = new JSONObject(content.toString());
                    JSONArray dataArray = (JSONArray) reader.get("data");
                    //insert both id and name for record label
                    for(int i=0;i<dataArray.length();i++){
                        artistList.add(
                                dataArray.getJSONObject(i).getString("artist_name"));
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
        artistThread.start();
        while(artistThread.isAlive()){
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,artistList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position,convertView,parent);

                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                tv.setTextColor(Color.WHITE);
                return view;
            }
        };
        listView.setAdapter(adapter);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object artistName = adapterView.getItemAtPosition(i);
                Intent myIntent = new Intent(ArtistActivity.this,
                        ArtistPageActivity.class);
                Log.d("artist_name",artistName.toString());
                //TODO::Check this works: send is artist id to user activity
                myIntent.putExtra("artist_id",myArtistID);
                myIntent.putExtra("artist_name", artistName.toString());
                startActivity(myIntent);
                }
        });
    }
}
