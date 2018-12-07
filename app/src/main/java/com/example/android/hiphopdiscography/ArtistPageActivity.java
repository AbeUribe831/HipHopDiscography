package com.example.android.hiphopdiscography;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//TODO::test the record label button and albums button
public class ArtistPageActivity extends AppCompatActivity {
    String artistName = "";
    int myArtistID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_page);

        Intent intent = getIntent();
        myArtistID = intent.getIntExtra("artist_id",-1);
        artistName = intent.getStringExtra("artist_name");
        Log.d("can_edit", "Artist Page " + myArtistID);
        Log.d("artist_name","Artist Page " + artistName);
        TextView title = (TextView) findViewById(R.id.artist_name_title);
        title.setText(artistName
        );
        Button recordLabelButton = (Button) findViewById(R.id.record_label_button);
        Button albumsButton = (Button) findViewById(R.id.album_button);
        Button songsButton = (Button) findViewById(R.id.song_button);
        Button tourButton = (Button) findViewById(R.id.tour_button);

        recordLabelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent recordLabelIntent = new Intent(ArtistPageActivity.this,
                        RecordLabelActivity.class);
                //TODO::Check this works: send is artist id to user activity
                recordLabelIntent.putExtra("artist_name",artistName);
                recordLabelIntent.putExtra("artist_id",myArtistID);
                startActivity(recordLabelIntent);
            }
        });

        albumsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent albumIntent = new Intent(ArtistPageActivity.this,
                        AlbumActivity.class);
                Log.d("artist_name","Artist Page " + artistName);
                albumIntent.putExtra("artist_name",artistName);
                albumIntent.putExtra("artist_id",myArtistID);
                startActivity(albumIntent);
            }
        });
        songsButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent songIntent = new Intent(ArtistPageActivity.this, SongActivity.class);
                songIntent.putExtra("artist_name",artistName);
                songIntent.putExtra("artist_id",myArtistID);
                startActivity(songIntent);
            }
        });

        tourButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent tourIntent = new Intent(ArtistPageActivity.this,
                        TourActivity.class);
                tourIntent.putExtra("artist_name",artistName);
                tourIntent.putExtra("artist_id",myArtistID);
                startActivity(tourIntent);
            }
        });
    }
}
