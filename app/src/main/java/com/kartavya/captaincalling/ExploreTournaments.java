package com.kartavya.captaincalling;

public class ExploreTournaments {
    private String tournamentName, tournamentDate, tournamentBanner, tournamentState, tournamentDistrict, tournamentTeams, tournamentAddress, tournamentOrganiser;

    public ExploreTournaments(String name, String date, String picture, String state, String district, String address, String teams){
        tournamentName = name;
        tournamentDate = date;
        tournamentBanner = picture;
        tournamentState = state;
        tournamentDistrict = district;
        tournamentTeams = teams;
        tournamentAddress = address;
    }

    public ExploreTournaments(){}


    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public String getTournamentDate() {
        return tournamentDate;
    }

    public void setTournamentDate(String tournamentDate) {
        this.tournamentDate = tournamentDate;
    }

    public String getTournamentBanner() {
        return tournamentBanner;
    }

    public void setTournamentBanner(String tournamentBanner) {
        this.tournamentBanner = tournamentBanner;
    }

    public String getTournamentState() {
        return tournamentState;
    }

    public void setTournamentState(String tournamentState) {
        this.tournamentState = tournamentState;
    }

    public String getTournamentDistrict() {
        return tournamentDistrict;
    }

    public void setTournamentDistrict(String tournamentDistrict) {
        this.tournamentDistrict = tournamentDistrict;
    }

    public String getTournamentTeams() {
        return tournamentTeams;
    }

    public void setTournamentTeams(String tournamentTeams) {
        this.tournamentTeams = tournamentTeams;
    }

    public String getTournamentAddress() {
        return tournamentAddress;
    }

    public void setTournamentAddress(String tournamentAddress) {
        this.tournamentAddress = tournamentAddress;
    }

    public String getTournamentOrganiser() {
        return tournamentOrganiser;
    }

    public void setTournamentOrganiser(String tournamentOrganiser) {
        this.tournamentOrganiser = tournamentOrganiser;
    }
}
