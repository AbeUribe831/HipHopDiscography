package com.example.android.hiphopdiscography;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class CustomActivity extends AppCompatActivity {
    String spinnerOption = "city";
    String apiRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        final Spinner spinner = (Spinner) findViewById(R.id.custom_tour_spinner);
        final ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(this,
                R.array.tour_spinner_strings,android.R.layout.simple_spinner_dropdown_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapterSpinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("SpinnerTest","test: " + adapterView.getItemAtPosition(i).toString());
                spinnerOption = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //Button clicked
        final Button searchButton = findViewById(R.id.custom_tour_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] argsString = new String[2];
                String artistName;
                EditText firstParamEditText = findViewById(R.id.custom_first_param_edit_text);
                EditText secondParamEditText = findViewById(R.id.custom_second_param_edit_text);
                EditText thirdParamEditText = findViewById(R.id.custom_third_param_edit_text);
                Intent customResultIntent = new Intent(CustomActivity.this,
                        CustomResultActivity.class);
                customResultIntent.putExtra("apiRequest",spinnerOption);
                switch (spinnerOption){
                    case "date":
                        apiRequest="tour_between_dates";
                        argsString[0] = ((EditText) findViewById(R.id.custom_first_param_edit_text)).getText().toString();
                        argsString[1]=((EditText) findViewById(R.id.custom_second_param_edit_text)).getText().toString();
                        customResultIntent.putExtra("str1",argsString[0]);
                        customResultIntent.putExtra("str2",argsString[1]);
                        break;
                    //TODO:: NEED third edit text for end date and artist, pass artist name to activity
                    case "date for artist":
                        apiRequest="tour_between_dates_by_artist";
                        argsString[0] = ((EditText) findViewById(R.id.custom_first_param_edit_text)).getText().toString();
                        argsString[1]=((EditText) findViewById(R.id.custom_second_param_edit_text)).getText().toString();
                        artistName = ((EditText) findViewById(R.id.custom_third_param_edit_text)).getText().toString();
                        customResultIntent.putExtra("str1",argsString[0]);
                        customResultIntent.putExtra("str2",argsString[1]);
                        customResultIntent.putExtra("str3",artistName);
                        break;
                    case "city":
                        apiRequest="tour_in_city";
                        argsString[0] = ((EditText) findViewById(R.id.custom_first_param_edit_text)).getText().toString();
                        customResultIntent.putExtra("str1",argsString[0]);
                        break;
                    case "city for artist":
                        apiRequest="tour_in_city_by_artist";
                        argsString[0] = ((EditText) findViewById(R.id.custom_first_param_edit_text)).getText().toString();
                        argsString[1]=((EditText) findViewById(R.id.custom_second_param_edit_text)).getText().toString();
                        customResultIntent.putExtra("str1",argsString[0]);
                        customResultIntent.putExtra("str2",argsString[1]);
                        break;
                    case "state":
                        apiRequest="tour_in_state";
                        argsString[0] = ((EditText) findViewById(R.id.custom_first_param_edit_text)).getText().toString();
                        customResultIntent.putExtra("str1",argsString[0]);
                        break;
                    case "state for artist":
                        apiRequest="tour_in_state_by_artist";
                        argsString[0] = ((EditText) findViewById(R.id.custom_first_param_edit_text)).getText().toString();
                        argsString[1]=((EditText) findViewById(R.id.custom_second_param_edit_text)).getText().toString();
                        customResultIntent.putExtra("str1",argsString[0]);
                        customResultIntent.putExtra("str2",argsString[1]);
                        break;
                    case "country":
                        apiRequest="tour_in_country";
                        argsString[0] = ((EditText) findViewById(R.id.custom_first_param_edit_text)).getText().toString();
                        customResultIntent.putExtra("str1",argsString[0]);
                        break;
                    case "country for artist":
                        apiRequest="tour_in_country_by_artist";
                        argsString[0] = ((EditText) findViewById(R.id.custom_first_param_edit_text)).getText().toString();
                        argsString[1]=((EditText) findViewById(R.id.custom_second_param_edit_text)).getText().toString();
                        customResultIntent.putExtra("str1",argsString[0]);
                        customResultIntent.putExtra("str2",argsString[1]);
                        break;
                    case "record label by sales":
                        apiRequest="sales_record_label";
                        break;
                    case "album sold more than":
                        apiRequest="more_than_sales_album";
                        int intArg= Integer.parseInt(((EditText) findViewById(R.id.custom_first_param_edit_text)).getText().toString());
                        customResultIntent.putExtra("int1",intArg);
                        break;
                    case "artist sold more than":
                        apiRequest="more_than_sales_artist";
                        intArg= Integer.parseInt(((EditText) findViewById(R.id.custom_first_param_edit_text)).getText().toString());
                        customResultIntent.putExtra("int1",intArg);
                        break;
                    case "artist sales":
                        apiRequest="artists_total_sales";
                        break;
                    case "artist rating":
                        apiRequest="artists_with_ranking";
                        argsString[0] = ((EditText) findViewById(R.id.custom_first_param_edit_text)).getText().toString();
                        customResultIntent.putExtra("str1",argsString[0]);
                        break;
                }
                startActivity(customResultIntent);


            }
        });
    }
}
