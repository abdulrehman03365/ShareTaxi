package com.example.abdul.sharetaxi;

public class Driver {

    String Carcolour, Carmodel, Carnumber, Catagory, DriverCnicno, Email, Name, Password,
            Phonenumber, PickupRadius, SeatsAvailable, downloadimageurl;

    public Driver() {

    }

    public Driver(String carcolour, String carmodel, String carnumber, String catagory, String driverCnicno, String email, String name, String password, String phonenumber, String pickupRadius, String seatsAvailable, String downloadimageurl) {
        Carcolour = carcolour;
        Carmodel = carmodel;
        Carnumber = carnumber;
        Catagory = catagory;
        DriverCnicno = driverCnicno;
        Email = email;
        Name = name;
        Password = password;
        Phonenumber = phonenumber;
        PickupRadius = pickupRadius;
        SeatsAvailable = seatsAvailable;
        this.downloadimageurl = downloadimageurl;
    }
}

