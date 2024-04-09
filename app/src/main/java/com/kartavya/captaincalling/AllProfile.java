package com.kartavya.captaincalling;

public class AllProfile {
    private String Address,District,Level,Name,Phone,Picture,PrimarySport,SecondarySport,State,Expertise;


    public AllProfile(String address, String district, String level, String name, String phone, String picture, String primarySport, String secondarySport, String state, String expertise) {
        Address = address;
        District = district;
        Level = level;
        Name = name;
        Phone = phone;
        Picture = picture;
        PrimarySport = primarySport;
        SecondarySport = secondarySport;
        State = state;
        Expertise = expertise;
    }

    public AllProfile() {
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public String getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        Level = level;
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

    public String getPrimarySport() {
        return PrimarySport;
    }

    public void setPrimarySport(String primarySport) {
        PrimarySport = primarySport;
    }

    public String getSecondarySport() {
        return SecondarySport;
    }

    public void setSecondarySport(String secondarySport) {
        SecondarySport = secondarySport;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getExpertise() {
        return Expertise;
    }

    public void setExpertise(String expertise) {
        Expertise = expertise;
    }
}
