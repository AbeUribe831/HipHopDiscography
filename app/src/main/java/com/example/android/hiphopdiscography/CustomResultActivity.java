package com.example.android.hiphopdiscography;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CustomResultActivity extends AppCompatActivity {
    String ipAddress = "18.220.86.83:3000/api";
    String strArg1 = "";
    String strArg2 = "";
    String artistName = "";
    int intArg1 = -1;
    int listType;
    ArrayList<Tour> tourList;
    ArrayList<String> recordLabelList;
    ArrayList<Album> albumList;
    ArrayList<String> artistList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_result);

        final Intent intent = getIntent();
        final String apiRequest = intent.getStringExtra("apiRequest");
        final ListView listView = findViewById(R.id.custom_result_list_view);


        //fills the arguments for the apiRequest
        switch (apiRequest){
            case "date":
                strArg1 = intent.getStringExtra("str1");
                strArg2 = intent.getStringExtra("str2");
                break;
            case "date for artist":
                strArg1 = intent.getStringExtra("str1");
                strArg2 = intent.getStringExtra("str2");
                artistName = intent.getStringExtra("str3");
                break;
            case "city":
                strArg1 = intent.getStringExtra("str1");
                break;
            case "city for artist":
                strArg1 = intent.getStringExtra("str1");
                strArg2 = intent.getStringExtra("str2");
                break;
            case "state":
                strArg1 = intent.getStringExtra("str1");
                break;
            case "state for artist":
                strArg1 = intent.getStringExtra("str1");
                strArg2 = intent.getStringExtra("str2");
                break;
            case "country":
                strArg1 = intent.getStringExtra("str1");
                break;
            case "country for artist":
                strArg1 = intent.getStringExtra("str1");
                strArg2 = intent.getStringExtra("str2");
                break;
            case "record label by sales":
                break;
            case "album sold more than":
                intArg1 = intent.getIntExtra("int1",-1);
                break;
            case "artist sold more than":
                intArg1 = intent.getIntExtra("int1",-1);
                break;
            case "artist sales":
                break;
            case "artist rating":
                strArg1 = intent.getStringExtra("str1");
                break;
        }

        Log.d("customResult","str1 " + strArg1 + " str2" + strArg2 +
                " artistName " + artistName + " int1" + intArg1 );

        final Thread customThread = new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                switch (apiRequest){
                    case "date":
                        url = new URL("http://"+ipAddress+"/tour_between_dates?" +
                                "firstdate="+strArg1+"&seconddate="+strArg2);
                        break;
                    case "date for artist":
                        url = new URL("http://"+ipAddress+"/tour_between_dates_by_artist?" +
                                "firstdate="+strArg1+"&seconddate="+strArg2+"&artist_name="+artistName);
                        break;
                    case "city":
                        url = new URL("http://"+ipAddress+"/tour_in_city?city="+strArg1);
                        break;
                    case "city for artist":
                        url = new URL("http://"+ipAddress+"/tour_in_city_by_artist?" +
                                "city="+strArg1+"&artist_name"+strArg2);
                        break;
                    case "state":
                        url = new URL("http://"+ipAddress+"/tour_in_state?state="+strArg1);
                        break;
                    case "state for artist":
                        url = new URL("http://"+ipAddress+"/tour_in_state_by_artist?" +
                                "state="+strArg1+"&artist_name="+strArg2);
                        break;
                    case "country":
                        url = new URL("http://"+ipAddress+"/tour_in_country?country="+strArg1);
                        break;
                    case "country for artist":
                        url = new URL("http://"+ipAddress+"/tour_in_country_by_artist?" +
                                "country="+strArg1+"&artist_name="+strArg2);
                        break;
                    case "record label by sales":
                        url = new URL("http://"+ipAddress+"/sales_record_label");
                        break;
                    case "album sold more than":
                        url = new URL("http://"+ipAddress+"/more_than_sales_artist?sales="+intArg1);
                        break;
                    case "artist sold more than":
                        url = new URL("http://"+ipAddress+"/more_than_sales_album?sales="+intArg1);
                        break;
                    case "artist sales":
                        url = new URL("http://"+ipAddress+"/artists_total_sales");
                        break;
                    case "artist rating":
                        url = new URL("http://"+ipAddress+"/artists_with_ranking?riaa_ranking=" +
                                strArg1);
                        break;
                }
                    Log.d("customResult",url.toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    String inputLine;
                    BufferedReader inRead = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    StringBuffer contentBuffer = new StringBuffer();

                    while ((inputLine = inRead.readLine()) != null) {
                        contentBuffer.append(inputLine);
                    }
                    inRead.close();

                    JSONObject artistRead = new JSONObject(contentBuffer.toString());
                    JSONArray dataArray = (JSONArray) artistRead.get("data");

                    //print tours
                    if(apiRequest.equals("date") || apiRequest.equals("date for artist") ||
                            apiRequest.equals("city") || apiRequest.equals("city for artist")||
                            apiRequest.equals("state") | apiRequest.equals("state for artist") ||
                            apiRequest.equals("country") || apiRequest.equals("country for artist")) {
                        listType = 0;
                         tourList = new ArrayList<>();
                        for(int i=0;i<dataArray.length();i++){
                            tourList.add(new Tour(
                                    dataArray.getJSONObject(i).getString("artist_name"),
                                    dataArray.getJSONObject(i).getString("tour_name"),
                                    dataArray.getJSONObject(i).getInt("price"),
                                    dataArray.getJSONObject(i).getString("date_of_show"),
                                    dataArray.getJSONObject(i).getString("city"),
                                    dataArray.getJSONObject(i).getString("state"),
                                    dataArray.getJSONObject(i).getString("country")));

                        }
                    }
                    //print record label
                    else if(apiRequest.equals("record label by sales")){
                        listType = 1;
                        recordLabelList = new ArrayList<>();
                        for(int i=0;i<dataArray.length();i++){
                            recordLabelList.add(
                                    dataArray.getJSONObject(i).getString("r_name"));
                        }
                    }
                    //print albums
                    else if(apiRequest.equals("album sold more than")){
                        listType = 2;
                        albumList = new ArrayList<>();
                        for(int i=0;i<dataArray.length();i++){
                            Log.d("ArtistArray", dataArray.getJSONObject(i).toString());

                            albumList.add(new Album(
                                    dataArray.getJSONObject(i).getString("album_name"),
                                    dataArray.getJSONObject(i).getInt("sales"),
                                    dataArray.getJSONObject(i).getString("riaa_ranking")));
                        }
                    }
                    //print artists
                    else{
                        listType = 3;
                        artistList = new ArrayList<>();
                        for(int i=0;i<dataArray.length();i++){
                            artistList.add(
                                    dataArray.getJSONObject(i).getString("artist_name"));
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
        });
        customThread.start();
        while(customThread.isAlive()){}
        if(listType == 0){
            ArrayAdapter<Tour> adapter = new ArrayAdapter<Tour>(this,
                    android.R.layout.simple_list_item_1,tourList){
                @Override
                public View getView(int position, View convertView, ViewGroup parent){
                    View view = super.getView(position,convertView,parent);

                    TextView tv = (TextView) view.findViewById(android.R.id.text1);

                    tv.setTextColor(Color.WHITE);
                    return view;
                }
            };
            listView.setAdapter(adapter);
        }
        else if(listType == 1){
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
        }
        else if(listType == 2){
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
        }
        else if(listType == 3){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
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
        }





    }
}
