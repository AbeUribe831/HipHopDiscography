package com.example.android.hiphopdiscography;

public class Song {
    public int song_id;
    public String song_name;
    public int duration;
    public int sales;
    public String riaa_ranking;
    public String features;

    Song(int id, String name, int d, int s, String rank, String feat){
        super();
        song_id = id;
        song_name = name;
        duration = d;
        sales=s;
        riaa_ranking=rank;
        features = feat;
    }

    @Override
    public String toString(){
        int seconds = duration % 60;
        String secondsString = String.valueOf(seconds);
        if(seconds < 10){
            secondsString="0"+String.valueOf(secondsString);
        }
        String salesString="";
        if(sales != -1){
            salesString = "sales: " + sales;
        }
        String rankString = "";
        if(!riaa_ranking.equals("")){
            rankString = "riaa ranking: " + riaa_ranking;
        }
        String featuresString="";
        if(!features.equals("")){
            featuresString="features: " + features;
        }
        return song_name + " " + (duration/60) + ":" + secondsString + " " + salesString + " "
                + rankString + " " + featuresString;
    }
}
