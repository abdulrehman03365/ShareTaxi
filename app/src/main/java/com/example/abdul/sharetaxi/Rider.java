package com.example.abdul.sharetaxi;

public class Rider  {



    String Catagory,Name,Email,Password,Phonenumber,downloadimageurl;

    public Rider() {

    }

    public Rider(String catagory, String name, String email, String password, String phonenumber, String downloadimageurl) {
        Catagory = catagory;
        Name = name;
        Email = email;
        Password = password;
        Phonenumber = phonenumber;
        this.downloadimageurl = downloadimageurl;
    }
}
