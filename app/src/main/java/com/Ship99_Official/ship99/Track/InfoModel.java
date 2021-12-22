package com.Ship99_Official.ship99.Track;

public class InfoModel {
    String photo,name,location,phonNumber;

    public InfoModel(String photo, String name, String location,String phonNumber) {
        this.photo = photo;
        this.name = name;
       this.phonNumber=phonNumber;
        this.location = location;
    }

    public InfoModel() {

    }

    public String getPhonNumber() {
        return phonNumber;
    }

    public String getPhoto() {
        return photo;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public void setPhonNumber(String phonNumber) {
        this.phonNumber = phonNumber;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
