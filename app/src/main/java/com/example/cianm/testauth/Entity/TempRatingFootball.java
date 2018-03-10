package com.example.cianm.testauth.Entity;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by cianm on 08/03/2018.
 */

@IgnoreExtraProperties
public class TempRatingFootball {

    String points, goals, wides, tackles, turnovers, yellowCards, redCards, blackCards;

    public TempRatingFootball(){}

    public TempRatingFootball(String points, String goals, String wides, String tackles, String turnovers, String yellowCards, String redCards, String blackCards) {
        this.points = points;
        this.goals = goals;
        this.wides = wides;
        this.tackles = tackles;
        this.turnovers = turnovers;
        this.yellowCards = yellowCards;
        this.redCards = redCards;
        this.blackCards = blackCards;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public String getWides() {
        return wides;
    }

    public void setWides(String wides) {
        this.wides = wides;
    }

    public String getTackles() {
        return tackles;
    }

    public void setTackles(String tackles) {
        this.tackles = tackles;
    }

    public String getTurnovers() {
        return turnovers;
    }

    public void setTurnovers(String turnovers) {
        this.turnovers = turnovers;
    }

    public String getYellowCards() {
        return yellowCards;
    }

    public void setYellowCards(String yellowCards) {
        this.yellowCards = yellowCards;
    }

    public String getRedCards() {
        return redCards;
    }

    public void setRedCards(String redCards) {
        this.redCards = redCards;
    }

    public String getBlackCards() {
        return blackCards;
    }

    public void setBlackCards(String blackCards) {
        this.blackCards = blackCards;
    }
}
