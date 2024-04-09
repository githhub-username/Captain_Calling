package com.kartavya.captaincalling;

public class Players {
    private String Name,Phone,Picture,isCaptain;

    public Players(String name, String phone, String picture, String isCaptain) {
        Name = name;
        Phone = phone;
        Picture = picture;
        this.isCaptain = isCaptain;
    }

    public Players() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getPicture() {
        return Picture;
    }

    public void setPicture(String picture) {
        Picture = picture;
    }

    public String getIsCaptain() {
        return isCaptain;
    }

    public void setIsCaptain(String isCaptain) {
        this.isCaptain = isCaptain;
    }
}
