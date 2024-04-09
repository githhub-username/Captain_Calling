package com.kartavya.captaincalling;

public class ManageTournaments {
    private String manageTournamentName, manageTournamentButton, manageTournamentBanner;

    public ManageTournaments(String name, String banner, String button){
        manageTournamentName = name;
        manageTournamentBanner = banner;
        manageTournamentButton = button;
    }

    public ManageTournaments(){}

    public String getManageTournamentBanner() {
        return manageTournamentBanner;
    }

    public void setManageTournamentBanner(String manageTournamentBanner) {
        this.manageTournamentBanner = manageTournamentBanner;
    }

    public String getManageTournamentButton() {
        return manageTournamentButton;
    }

    public void setManageTournamentButton(String manageTournamentButton) {
        this.manageTournamentButton = manageTournamentButton;
    }

    public String getManageTournamentName() {
        return manageTournamentName;
    }

    public void setManageTournamentName(String manageTournamentName) {
        this.manageTournamentName = manageTournamentName;
    }
}
