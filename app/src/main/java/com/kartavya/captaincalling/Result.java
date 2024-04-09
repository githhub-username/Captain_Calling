package com.kartavya.captaincalling;

public class Result {
    private String Team1,Team2,Status,Msg,Date,Title;


    public Result(String team1, String team2, String status, String msg, String date, String title) {
        Team1 = team1;
        Team2 = team2;
        Status = status;
        Msg = msg;
        Date = date;
        Title = title;
    }

    public Result() {
    }

    public String getTeam1() {
        return Team1;
    }

    public void setTeam1(String team1) {
        Team1 = team1;
    }

    public String getTeam2() {
        return Team2;
    }

    public void setTeam2(String team2) {
        Team2 = team2;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }
}
