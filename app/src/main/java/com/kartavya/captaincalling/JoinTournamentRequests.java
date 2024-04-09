package com.kartavya.captaincalling;

public class JoinTournamentRequests {
    public String tournamentTeamName, tournamentTeamCaptainName, joinTournamentStatus;

    public JoinTournamentRequests(String teamName, String teamCaptainName, String joinStatus){
        this.tournamentTeamName = teamName;
        this.tournamentTeamCaptainName = teamCaptainName;
        this.joinTournamentStatus = joinStatus;
    }

    public JoinTournamentRequests(){}

    public String getTournamentTeamName() {
        return tournamentTeamName;
    }

    public void setTournamentTeamName(String tournamentTeamName) {
        this.tournamentTeamName = tournamentTeamName;
    }

    public String getTournamentTeamCaptainName() {
        return tournamentTeamCaptainName;
    }

    public void setTournamentTeamCaptainName(String tournamentTeamCaptainName) {
        this.tournamentTeamCaptainName = tournamentTeamCaptainName;
    }

    public String getJoinTournamentStatus() {
        return joinTournamentStatus;
    }

    public void setJoinTournamentStatus(String joinTournamentStatus) {
        this.joinTournamentStatus = joinTournamentStatus;
    }
}
