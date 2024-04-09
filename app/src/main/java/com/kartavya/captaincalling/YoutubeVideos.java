package com.kartavya.captaincalling;

public class YoutubeVideos {
    private String Description,Title,Url,Picture,Status;

    public YoutubeVideos(String description, String title, String url, String picture, String status) {
        Description = description;
        Title = title;
        Url = url;
        Picture = picture;
        Status = status;
    }

    public YoutubeVideos() {
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getPicture() {
        return Picture;
    }

    public void setPicture(String picture) {
        Picture = picture;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
