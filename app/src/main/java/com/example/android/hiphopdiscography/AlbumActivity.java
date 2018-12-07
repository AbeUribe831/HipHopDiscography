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

public class AlbumActivity extends AppCompatActivity {
    String ipAddress = "18.220.86.83";
    String artistName;
    boolean canEdit = false;
    int myArtistID;
    String toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        Intent intent = getIntent();
        artistName = intent.getStringExtra("artist_name");
        Log.d("ArtistArray","artistName: " + artistName);
        myArtistID = intent.getIntExtra("artist_id",-1);
        final ArrayList<Album> albumList = new ArrayList<Album>();
        final ListView listView = (ListView) findViewById(R.id.album_list);

        Thread RLThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Check if user can edit this page
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
                        if(canEdit){
                            LinearLayout albumOptionsLayout = findViewById(R.id.album_layout);
                            albumOptionsLayout.setVisibility(View.VISIBLE);
                            Button addAlbumButton = findViewById(R.id.new_album_button);
                            addAlbumButton.setVisibility(View.VISIBLE);
                        }
                    }

                    Log.d("can_edit",""+canEdit + " artistID " + myArtistID);
                    URL url = null;
                    if(artistName.equals("")) {
                        url = new URL("http://" + ipAddress + ":3000/api/albums");
                        Log.d("ArtistArray", url.toString());
                    }
                    else{
                        url = new URL("http://" + ipAddress + ":3000/api/album_of_artist?" +
                                "artist_name="+artistName);
                        Log.d("ArtistArray", url.toString());
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
                    Log.d("artist_name","size of array "+dataArray.length());
                    for(int i=0;i<dataArray.length();i++){
                        Log.d("ArtistArray", dataArray.getJSONObject(i).toString());

                        albumList.add(new Album(
                                dataArray.getJSONObject(i).getString("album_name"),
                                dataArray.getJSONObject(i).getInt("sales"),
                                dataArray.getJSONObject(i).getString("riaa_ranking")));
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
        ArrayAdapter<Album> adapter = new ArrayAdapter<Album>(this,
                android.R.layout.simple_list_item_1,albumList){
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
                if(canEdit){
                    CheckBox deleteAlbum = findViewById(R.id.delete_album_checkbox);
                    if(deleteAlbum.isChecked()){
                       Thread deleteThread = new Thread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void run() {
                                URL url = null;
                                try {
                                    url = new URL("http://"+ipAddress+":3000/api/deleteAlbum");
                                    String urlParams = "album_name="+((Album)adapterView.getItemAtPosition(i)).album_name;
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
                        Album album = (Album) adapterView.getItemAtPosition(i);
                        Intent myIntent = new Intent(AlbumActivity.this,
                                AlbumFormActivity.class);
                        Log.d("artist_name", artistName.toString());
                        //myIntent.putExtra("artist_name", artistName.toString());
                        myIntent.putExtra("update", 1);
                        myIntent.putExtra("album_name", album.album_name);
                        myIntent.putExtra("sales", album.sales);
                        myIntent.putExtra("riaa_ranking", album.riaa_ranking);
                        myIntent.putExtra("artist_id", myArtistID);
                        startActivity(myIntent);
                    }
                }

            }
        });

        Button button = findViewById(R.id.new_album_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(AlbumActivity.this,
                        AlbumFormActivity.class);
                myIntent.putExtra("update",0);
                myIntent.putExtra("artist_id",myArtistID);
                startActivity(myIntent);
            }
        });

    }
}
