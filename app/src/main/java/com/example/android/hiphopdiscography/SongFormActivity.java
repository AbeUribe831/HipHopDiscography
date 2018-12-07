package com.example.android.hiphopdiscography;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

//TODO::search album ID based on album written
public class SongFormActivity extends AppCompatActivity {
    String ipAddress = "18.220.86.83";
    String oldSongName;
    int artistID;
    String artistName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_form);

        Intent intent = getIntent();
        final int update = intent.getIntExtra("update",-1);
        final int duration;
        int sales;
        final String riaaRanking;
        final String features;
        artistName = intent.getStringExtra("artist_name");
        artistID = intent.getIntExtra("artist_id",-1);
        if(update == 1) {
            Log.d("SongForm", "can update " + update);
            oldSongName = intent.getStringExtra("song_name");
            duration = intent.getIntExtra("duration",-1);
            sales = intent.getIntExtra("sales",-1);
            riaaRanking = intent.getStringExtra("riaa_ranking");
            features = intent.getStringExtra("features");

            EditText songEditText = findViewById(R.id.song_name_edit_text);
            EditText durationEditText = findViewById(R.id.song_duration_edit_text);
            EditText salesEditText = findViewById(R.id.song_sales_edit_text);
            EditText riaaRankingEditText = findViewById(R.id.song_riaa_ranking_edit_text);
            EditText featuresEditText = findViewById(R.id.song_features_edit_text);

            songEditText.setText(oldSongName);
            durationEditText.setText(String.valueOf(duration));
            if(sales != -1) {
                salesEditText.setText(String.valueOf(sales));
            }
            riaaRankingEditText.setText(riaaRanking);
            featuresEditText.setText(features);
        }

        Button updateOrAddButton = findViewById(R.id.update_or_add_song_button);
        if(update == 1){
            updateOrAddButton.setText(getResources().getString(R.string.update));
        }
        else{
            updateOrAddButton.setText(getResources().getString(R.string.add_song));
        }

        //TODO::add
        updateOrAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //update == 1 means that we call /updateSong with a post request
                new Thread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        URL url;
                        EditText songEditText = findViewById(R.id.song_name_edit_text);
                        EditText durationEditText = findViewById(R.id.song_duration_edit_text);
                        EditText salesEditText = findViewById(R.id.song_sales_edit_text);
                        EditText riaaRankingEditText = findViewById(R.id.song_riaa_ranking_edit_text);
                        EditText featuresEditText = findViewById(R.id.song_features_edit_text);
                        int albumID=-1;
                        try {
                            Log.d("newSong","update value " + update);
                            if(update == 1){
                                //url = new URL("http://"+ipAddress+":3000/api/updateAlbum");
                                String urlParams = "song_name="+oldSongName+
                                        "&song_name_change="+songEditText.getText().toString()+
                                        "&sales="+salesEditText.getText().toString()+
                                        "&duration="+durationEditText.getText().toString()+
                                        "&riaa_ranking="+riaaRankingEditText.getText().toString()
                                        +"&features="+featuresEditText.getText().toString();

                                byte[] postData = urlParams.getBytes(StandardCharsets.UTF_8);
                                int postDataLength = postData.length;
                                url = new URL("http://"+ipAddress+":3000/api/updateSong");
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
                                    Log.d("inputFromServer", "\nServer: " + reader.getString("data") +
                                            "\nupdate "+ update);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                            //add new album
                            else{
                                EditText albumForSong = (EditText) findViewById(R.id.album_for_song_edit_text);

                                //Getting album id
                                URL artistIDURL = new URL("http://" + ipAddress + ":3000/api/album_of_artist" +
                                        "?artist_name="+artistName);
                                Log.d("newSong","artistName " + artistName);
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
                                    Log.d("newSong", "in loop" + i + " " +
                                            dataArtistArray.getJSONObject(i).getString("album_name"));

                                    if (dataArtistArray.getJSONObject(i).getString("album_name")
                                            .equals(albumForSong.getText().toString())){
                                        Log.d("newSong", "albumID in if " + dataArtistArray.getJSONObject(i).getInt("album_id"));
                                        albumID = dataArtistArray.getJSONObject(i).getInt("album_id");
                                        Log.d("newSong", "albumID in if " + albumID);
                                        break;

                                    }
                                }
                                //Did not find album
                                if(albumID==-1){
                                    return;
                                }
                                //end of getting album id
                                Log.d("newSong","albumID "+albumID);

                                url = new URL("http://"+ipAddress+":3000/api/newSong");

                                String urlParams = "song_name="+songEditText.getText().toString()+
                                        "&duration="+durationEditText.getText().toString()+
                                        "&sales="+salesEditText.getText().toString()+
                                        "&riaa_ranking="+riaaRankingEditText.getText().toString()+
                                        "&features="+featuresEditText.getText().toString()+
                                        "&artist_id="+artistID+
                                        "&album_id="+albumID;
                                Log.d("newSong",urlParams);

                                byte[] postData = urlParams.getBytes(StandardCharsets.UTF_8);
                                int postDataLength = postData.length;
                                url = new URL("http://"+ipAddress+":3000/api/newSong");
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
                                    Log.d("newSong", "\nServer: " + reader.getString("data") +
                                            "\nupdate "+ update);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }



                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }
}
