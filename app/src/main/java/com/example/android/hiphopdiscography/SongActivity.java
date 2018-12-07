package com.example.android.hiphopdiscography;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SongActivity extends AppCompatActivity {
    String ipAddress = "18.220.86.83";
    boolean canEdit = false;
    String artistName;
    int myArtistID;
    String toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        Intent intent = getIntent();
        artistName = intent.getStringExtra("artist_name");
        myArtistID = intent.getIntExtra("artist_id",-1);
        Log.d("artistID",""+myArtistID);
        final ArrayList<Song> songList = new ArrayList<Song>();
        final ListView listView = (ListView) findViewById(R.id.song_list);

        Thread RLThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(myArtistID != -1) {
                        URL artistIDURL = new URL("http://" + ipAddress + ":3000/api/artists");
                        HttpURLConnection connArtist = (HttpURLConnection) artistIDURL.openConnection();
                        connArtist.setRequestMethod("GET");
                        connArtist.setDoInput(true);
                        String inputArtistLine;

                        BufferedReader inRead = new BufferedReader(
                                new InputStreamReader(connArtist.getInputStream()));
                        StringBuffer contentBuffer = new StringBuffer();

                        while ((inputArtistLine = inRead.readLine()) != null) {
                            contentBuffer.append(inputArtistLine);
                        }
                        inRead.close();

                        JSONObject artistRead = new JSONObject(contentBuffer.toString());
                        JSONArray dataArtistArray = (JSONArray) artistRead.get("data");
                        for (int i = 0; i < dataArtistArray.length(); i++) {
                            if (dataArtistArray.getJSONObject(i).getString("artist_name")
                                    .equals(artistName)&&
                                    dataArtistArray.getJSONObject(i).getInt("artist_id") == myArtistID){
                                        canEdit = true;
                            }
                        }
                    }
                    if(canEdit){
                        LinearLayout songOptionsLayout = findViewById(R.id.song_options_layout);
                        songOptionsLayout.setVisibility(View.VISIBLE);
                        Button addSongButton = findViewById(R.id.new_song_button);
                        addSongButton.setVisibility(View.VISIBLE);
                    }
                    URL url;
                    if(artistName.equals("")) {
                        url = new URL("http://" + ipAddress + ":3000/api/songs");
                    }
                    //If page is given artistName then get songs bu artist
                    else{
                        url = new URL("http://"+ipAddress+":3000/api/songs_of_artist" +
                                "?artist_name="+artistName);
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
                        int sales = -1;
                        String riaa_ranking = "";
                        String features = "";
                        Log.d("areValuesNull","size of array " + dataArray.length());
                        Log.d("areValuesNull","sales: " + dataArray.getJSONObject(i).getString("sales").length());
                        Log.d("areValuesNull","riaa_ranking: " + dataArray.getJSONObject(i).getString("riaa_ranking").length());
                        Log.d("areValuesNull","features: " + dataArray.getJSONObject(i).getString("features").length());
                        Log.d("areValuesNull","song_name: " + dataArray.getJSONObject(i).getString("song_name"));

                        if(!dataArray.getJSONObject(i).isNull("sales")){
                            sales = dataArray.getJSONObject(i).getInt("sales");
                        }
                        if(!dataArray.getJSONObject(i).isNull("riaa_ranking")){
                            riaa_ranking = dataArray.getJSONObject(i).getString("riaa_ranking");
                        }
                        if(!dataArray.getJSONObject(i).isNull("features")){
                            features = dataArray.getJSONObject(i).getString("features");
                        }


                        songList.add(new Song(
                                dataArray.getJSONObject(i).getInt("song_id"),
                                dataArray.getJSONObject(i).getString("song_name"),
                                dataArray.getJSONObject(i).getInt("duration"),
                                sales,
                                riaa_ranking,
                                features));

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
        ArrayAdapter<Song> adapter = new ArrayAdapter<Song>(this,
                android.R.layout.simple_list_item_1,songList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position,convertView,parent);

                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                tv.setTextColor(Color.WHITE);
                return view;
            }
        };
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                CheckBox delete = findViewById(R.id.delete_song_checkbox);

                if(canEdit){
                    CheckBox deleteSong = findViewById(R.id.delete_song_checkbox);
                    Log.d("deleteSong",""+deleteSong.isChecked());
                    if(deleteSong.isChecked()){
                        Log.d("deleteSong","delete song is checked");
                        Thread deleteThread = new Thread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void run() {
                                URL url = null;
                                try {
                                    url = new URL("http://"+ipAddress+":3000/api/deleteSong");
                                    Song song = (Song) adapterView.getItemAtPosition(i);
                                    String urlParams = "song_id="+song.song_id;
                                    Log.d("deleteSong",urlParams);
                                    Log.d("deleteSong",url.toString());
                                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                    byte[] postData = urlParams.getBytes(StandardCharsets.UTF_8);
                                    int postDataLength = postData.length;
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
                                        toast = reader.getString("data");
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

                        });
                        deleteThread.start();
                        while (deleteThread.isAlive()){}
                        Toast.makeText(getApplicationContext(),toast,Toast.LENGTH_SHORT);

                    }
                    else {
                        Song song = (Song) adapterView.getItemAtPosition(i);
                        Log.d("clickedSong", "\nsong name " + song.song_name + "\nduration = " + song.duration +
                                "\nsales = " + song.sales + "\ncan edit " + canEdit);
                        Intent myIntent = new Intent(SongActivity.this,
                                SongFormActivity.class);
                        Log.d("artist_name", artistName.toString());
                        //myIntent.putExtra("artist_name", artistName.toString());
                        myIntent.putExtra("update", 1);
                        myIntent.putExtra("song_name", song.song_name);
                        myIntent.putExtra("duration", song.duration);
                        myIntent.putExtra("sales", song.sales);
                        myIntent.putExtra("riaa_ranking", song.riaa_ranking);
                        myIntent.putExtra("features", song.features);
                        myIntent.putExtra("artist_id", myArtistID);
                        startActivity(myIntent);
                    }
                }

            }
        });

        Button newSong = findViewById(R.id.new_song_button);
        newSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(canEdit){
                    Log.d("newSong","Song Activity " + myArtistID);
                    Intent myIntent = new Intent(SongActivity.this, SongFormActivity.class);
                    myIntent.putExtra("update",0);
                    myIntent.putExtra("song_name","");
                    myIntent.putExtra("duration","");
                    myIntent.putExtra("sales","");
                    myIntent.putExtra("riaa_ranking","");
                    myIntent.putExtra("features","");
                    myIntent.putExtra("artist_id",myArtistID);
                    myIntent.putExtra("artist_name",artistName);
                    startActivity(myIntent);
                }
            }
        });
    }

}
