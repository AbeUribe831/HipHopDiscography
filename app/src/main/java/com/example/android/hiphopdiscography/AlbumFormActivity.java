package com.example.android.hiphopdiscography;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import java.time.chrono.ThaiBuddhistEra;

//TODO::write code for adding a new album
public class AlbumFormActivity extends AppCompatActivity {
    String ipAddress = "18.220.86.83";
    int artistID;
    String oldAlbumName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_form);

        Intent intent = getIntent();
        final int update = intent.getIntExtra("update",-1);
        final int sales;
        String riaaRanking;
        artistID = intent.getIntExtra("artist_id",-1);
        if(update == 1) {
            Log.d("AlbumForm", "can update " + update);
            oldAlbumName = intent.getStringExtra("album_name");
            sales = intent.getIntExtra("sales",-1);
            riaaRanking = intent.getStringExtra("riaa_ranking");

            EditText albumEditText = findViewById(R.id.album_name_edit_text);
            EditText salesEditText = findViewById(R.id.album_sales_edit_text);
            EditText riaaRankingEditText = findViewById(R.id.album_riaa_ranking_edit_text);

            albumEditText.setText(oldAlbumName);
            if(sales != -1) {
                salesEditText.setText(String.valueOf(sales));
            }
            riaaRankingEditText.setText(riaaRanking);
        }

        Button updateOrAddButton = findViewById(R.id.update_or_add_song_button);
        if(update == 1){
            updateOrAddButton.setText(getResources().getString(R.string.update));
        }
        else{
            updateOrAddButton.setText(getResources().getString(R.string.add_album));
        }

        updateOrAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //update == 1 means that we call /updateAlbum with a post request
                new Thread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        URL url;
                        EditText albumEditText = findViewById(R.id.album_name_edit_text);
                        EditText salesEditText = findViewById(R.id.album_sales_edit_text);
                        EditText riaaRankingEditText = findViewById(R.id.album_riaa_ranking_edit_text);
                        try {
                        if(update == 1){
                            //url = new URL("http://"+ipAddress+":3000/api/updateAlbum");
                            String urlParams = "album_name="+oldAlbumName+"&album_name_change="+albumEditText.getText().toString()+
                                    "&sales="+salesEditText.getText().toString()+
                                    "&riaa_ranking="+riaaRankingEditText.getText().toString()
                                    +"&artist_id="+artistID;

                            byte[] postData = urlParams.getBytes(StandardCharsets.UTF_8);
                            int postDataLength = postData.length;
                            url = new URL("http://"+ipAddress+":3000/api/updateAlbum");
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
                                url = new URL("http://"+ipAddress+":3000/api/newAlbum");

                            String urlParams = "album_name="+albumEditText.getText().toString()+
                                    "&sales="+salesEditText.getText().toString()+
                                    "&riaa_ranking="+riaaRankingEditText.getText().toString()
                                    +"&artist_id="+artistID;

                            byte[] postData = urlParams.getBytes(StandardCharsets.UTF_8);
                            int postDataLength = postData.length;
                            url = new URL("http://"+ipAddress+":3000/api/newAlbum");
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



                        } catch (MalformedURLException e) {
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
