package com.kartavya.captaincalling;

public class AllBlogs {
    private String Date,Description,Name,Phone,Picture,ProfilePic,Status,Title,Category;


    public AllBlogs(String date, String description, String name, String phone, String picture, String profilePic, String status, String title,String category) {
        Date = date;
        Description = description;
        Name = name;
        Phone = phone;
        Picture = picture;
        ProfilePic = profilePic;
        Status = status;
        Title = title;
        Category=category;
    }

    public AllBlogs() {
    }


    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
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

    public String getProfilePic() {
        return ProfilePic;
    }

    public void setProfilePic(String profilePic) {
        ProfilePic = profilePic;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getCategory() {
        return Category;
    }
}
