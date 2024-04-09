package com.kartavya.captaincalling;

public class MyTeams {
    private String EntryId,Picture,Sport,TeamName;


    public MyTeams(String entryId, String picture, String sport, String teamName) {
        EntryId = entryId;
        Picture = picture;
        Sport = sport;
        TeamName = teamName;
    }

    public MyTeams() {
    }


    public String getEntryId() {
        return EntryId;
    }

    public void setEntryId(String entryId) {
        EntryId = entryId;
    }


    public String getPicture() {
        return Picture;
    }

    public void setPicture(String picture) {
        Picture = picture;
    }

    public String getSport() {
        return Sport;
    }

    public void setSport(String sport) {
        Sport = sport;
    }

    public String getTeamName() {
        return TeamName;
    }

    public void setTeamName(String teamName) {
        TeamName = teamName;
    }
}
