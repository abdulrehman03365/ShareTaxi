package com.example.abdul.sharetaxi;




public class HistoryModel {

    String  Cost,Date,Dest,From,Time,Key;

    public HistoryModel() {

    }

    public HistoryModel(String key,String cost, String date, String dest, String from, String time) {
        Key=key;
        Cost = cost;
        Date = date;
        Dest = dest;
        From = from;
        Time = time;
    }


}
