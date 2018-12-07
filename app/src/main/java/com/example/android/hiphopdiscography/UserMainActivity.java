package com.example.android.hiphopdiscography;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class UserMainActivity extends AppCompatActivity {
    int artistID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        Intent intent = getIntent();
        artistID = intent.getIntExtra("artist_id",-1);
        final Button recordLabelButton = (Button) findViewById(R.id.record_label_button);
        Log.d("can_edit", "user main " + artistID);
        recordLabelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent recordLabelIntent = new Intent(UserMainActivity.this,
                        RecordLabelActivity.class);
                recordLabelIntent.putExtra("artist_id",artistID);
                recordLabelIntent.putExtra("artist_name","");
                startActivity(recordLabelIntent);
            }
        });

        Button artistButton = (Button) findViewById(R.id.artist_button);
        artistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent artistIntent = new Intent(UserMainActivity.this,
                        ArtistActivity.class);
                artistIntent.putExtra("artist_id",artistID);
                artistIntent.putExtra("record_label","");
                startActivity(artistIntent);

            }
        });

        Button albumButton = (Button) findViewById(R.id.album_button);
        albumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent albumIntent = new Intent(UserMainActivity.this,
                        AlbumActivity.class);
                albumIntent.putExtra("artist_id",artistID);
                albumIntent.putExtra("artist_name","");
                startActivity(albumIntent);
            }
        });

        Button songButton = (Button) findViewById(R.id.song_button);
        songButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent songIntent = new Intent(UserMainActivity.this,
                        SongActivity.class);
                songIntent.putExtra("artist_id",artistID);
                songIntent.putExtra("artist_name","");
                startActivity(songIntent);
            }
        });

        final Button tourButton = (Button) findViewById(R.id.user_tour_button);
        tourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tourIntent = new Intent(UserMainActivity.this,
                        TourActivity.class);
                tourIntent.putExtra("artist_id",artistID);
                tourIntent.putExtra("artist_name","");
                startActivity(tourIntent);
            }
        });
        Button customSearchButton = (Button) findViewById(R.id.custom_button);
        customSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent customIntent = new Intent(UserMainActivity.this,
                        CustomActivity.class);
                customIntent.putExtra("artist_id",artistID);
                customIntent.putExtra("artist_name","");
                startActivity(customIntent);
            }
        });

    }

}
