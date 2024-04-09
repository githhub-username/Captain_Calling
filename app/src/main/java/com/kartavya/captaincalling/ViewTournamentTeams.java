package com.kartavya.captaincalling;

public class ViewTournamentTeams {
    private String viewTournamentTeamName, viewTournamentTeamCaptainName;

    public ViewTournamentTeams(String teamName, String captainName){
        this.viewTournamentTeamName = teamName;
        this.viewTournamentTeamCaptainName = captainName;
    }

    public ViewTournamentTeams(){}

    public String getViewTournamentTeamName() {
        return viewTournamentTeamName;
    }

    public void setViewTournamentTeamName(String viewTournamentTeamName) {
        this.viewTournamentTeamName = viewTournamentTeamName;
    }

    public String getViewTournamentTeamCaptainName() {
        return viewTournamentTeamCaptainName;
    }

    public void setViewTournamentTeamCaptainName(String viewTournamentTeamCaptainName) {
        this.viewTournamentTeamCaptainName = viewTournamentTeamCaptainName;
    }
}
