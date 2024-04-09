package com.kartavya.captaincalling;

public class Chats {
    private String Message,Name,Picture,isCaptain,Date,Phone;


    public Chats(String message, String name, String picture, String isCaptain, String date, String phone) {
        Message = message;
        Name = name;
        Picture = picture;
        this.isCaptain = isCaptain;
        Date = date;
        Phone = phone;
    }

    public Chats() {
    }


    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
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

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}
