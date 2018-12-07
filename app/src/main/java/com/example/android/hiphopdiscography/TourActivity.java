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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
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

//TODO::test how I call threads for all tour functions
public class TourActivity extends AppCompatActivity {
    String ipAddress = "18.220.86.83";
    String spinnerOption = "city";
    String artistName= "";
    ArrayList<Tour> tourList;
    ListView listView;
    ArrayAdapter<Tour> adapter;
    int myArtistID;
    boolean canEdit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);

        Intent intent = getIntent();
        artistName = intent.getStringExtra("artist_name");
        myArtistID = intent.getIntExtra("artist_id",-1);
        String[] args = new String[2];


        tourList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.tour_list);


        Thread tourThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Check if artist can edit
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
                        LinearLayout songOptionsLayout = findViewById(R.id.tour_options_layout);
                        songOptionsLayout.setVisibility(View.VISIBLE);
                        Button addSongButton = findViewById(R.id.new_tour_button);
                        addSongButton.setVisibility(View.VISIBLE);
                    }
                    //end of check
                    URL url;
                   if (!artistName.equals("")){
                        url = new URL("http://" + ipAddress + ":3000/api/specific_artist_tours?artist_name="+artistName);
                    }
                    else{
                        url = new URL("http://" + ipAddress + ":3000/api/all_artist_tours");
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
                    Log.d("tourDates","tourDate 0:" +dataArray.getString(0));
                    Log.d("tourDates","tourDate 1:" +dataArray.getString(1));
                    //insert both id and name for recordbel
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
        tourThread.start();
        while(tourThread.isAlive()){
        }

        adapter = new ArrayAdapter<Tour>(this,
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


        final Spinner spinner = (Spinner) findViewById(R.id.tour_spinner);
        final ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(this,
                R.array.tour_spinner_strings,android.R.layout.simple_spinner_dropdown_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapterSpinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("SpinnerTest","test: " + adapterView.getItemAtPosition(i).toString());
                spinnerOption = adapterView.getItemAtPosition(i).toString();
                if (spinnerOption.equals("date")) {
                    EditText endDate = findViewById(R.id.end_date_edit_text);
                    endDate.setVisibility(View.VISIBLE);
                    EditText startDate = findViewById(R.id.tour_specifics_edit_text);
                    startDate.setHint(getResources().getString(R.string.start_date));
                } else {
                    EditText endDate = findViewById(R.id.end_date_edit_text);
                    endDate.setVisibility(View.GONE);
                    EditText startDate = findViewById(R.id.tour_specifics_edit_text);
                    startDate.setHint(getResources().getString(R.string.tour_specifics));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Button clicked
        final Button searchButton = findViewById(R.id.tour_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] args = new String[2];
                args[0] = ((EditText) findViewById(R.id.tour_specifics_edit_text)).getText().toString();

                if(spinnerOption.equals("date")){
                    args[1] = ((EditText) findViewById(R.id.end_date_edit_text)).getText().toString();
                    searchNewTours("tour_between_dates",args);
                }
                else if(spinnerOption.equals("city")){
                    searchNewTours("tour_in_city",args);
                }
                else if(spinnerOption.equals("state")){
                    searchNewTours("tour_in_state",args);
                }
                else if(spinnerOption.equals("country")){
                    searchNewTours("tour_in_country",args);
                }

            }
        });
    }


    //Search

    public void searchNewTours(final String apiRequest, final String[] args){
        /*final ArrayList<Tour> tourList = new ArrayList<Tour>();
        final ListView listView = (ListView) findViewById(R.id.tour_list);*/
        Thread tourThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("arrayArgument", args[0]);
                    URL url;
                    //dates and all artist
                    if (apiRequest.equals("tour_between_dates") && artistName.equals("")){
                        url = new URL("http://" + ipAddress + ":3000/api/"+apiRequest+"?" +
                                "firstdate="+args[0]+"&seconddate="+args[1]);
                    }
                    //dates and specific artist
                    else if (apiRequest.equals("tour_between_dates") && !artistName.equals("")){
                        url = new URL("http://" + ipAddress + ":3000/api/"+apiRequest+"_by_artist?" +
                                "firstdate="+args[0]+"&seconddate="+args[1]+"&artist_name="+artistName);
                    }
                    //city and all artists
                    else if(apiRequest.equals("tour_in_city") && artistName.equals("")){
                        url = new URL("http://" + ipAddress + ":3000/api/"+apiRequest+"?" +
                                "city="+args[0]);
                    }
                    //city and specific artists
                    else if(apiRequest.equals("tour_in_city") && !artistName.equals("")){
                        url = new URL("http://" + ipAddress + ":3000/api/"+apiRequest+"_by_artist?" +
                                "city="+args[0]+"&artist_name="+artistName);
                    }
                    //state and all artists
                    else if(apiRequest.equals("tour_in_state") && artistName.equals("")){
                        url = new URL("http://" + ipAddress + ":3000/api/"+apiRequest+"?" +
                                "state="+args[0]);
                    }
                    //state and specific artists
                    else if(apiRequest.equals("tour_in_state") && !artistName.equals("")){
                        url = new URL("http://" + ipAddress + ":3000/api/"+apiRequest+"_by_artist?" +
                                "state="+args[0]+"&artist_name="+artistName);
                    }
                    //country and all artists
                    else if(apiRequest.equals("tour_in_country") && artistName.equals("")){
                        url = new URL("http://" + ipAddress + ":3000/api/"+apiRequest+"?" +
                                "country="+args[0]);
                    }
                    //country and specific artists
                    else if(apiRequest.equals("tour_in_country") && !artistName.equals("")){
                        url = new URL("http://" + ipAddress + ":3000/api/"+apiRequest+"_by_artist?" +
                                "country="+args[0]+"&artist_name="+artistName);
                    }
                    else{
                        return;
                    }
                    Log.d("tourDates","\n\n"+url.toString());

                    tourList.clear();

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
                    Log.d("tourDates","Clicked 0:" +dataArray.getString(0));
                    Log.d("tourDates","Clicked 1:" +dataArray.getString(1));
                    //insert both id and name for recordbel
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
        tourThread.start();
        while(tourThread.isAlive()){
        }

                adapter.clear();
                adapter.addAll(tourList);
                //adapter.notifyDataSetChanged();
                //listView.invalidateViews();
                //listView.refreshDrawableState();

    }

}
