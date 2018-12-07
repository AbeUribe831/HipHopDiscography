package com.example.android.hiphopdiscography;

public class Album {
    String album_name;
    int sales;
    String riaa_ranking;

    Album(String name,int sales, String rank){
        album_name=name;
        this.sales=sales;
        riaa_ranking=rank;
    }

    @Override
    public String toString(){

        return album_name + " sold " + sales + " with a " + riaa_ranking + " ranking";
    }
}
