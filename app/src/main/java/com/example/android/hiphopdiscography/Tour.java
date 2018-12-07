package com.example.android.hiphopdiscography;

public class Tour {
    public String artist_name;
    public String tour_name;
    public int price;
    public String date_of_show;
    public String city;
    public String state;
    public String country;

    public Tour(String a_name, String t_name, int p, String date, String c, String s, String country){
        super();
        artist_name=a_name;
        tour_name=t_name;
        price=p;
        date_of_show=date;
        city=c;
        state=s;
        this.country=country;
    }

    @Override
    public String toString(){
        return tour_name +" by " + artist_name + " at " + city+", " + state+" " + country +
                " on " + date_of_show + " for $" + price;
    }
}
