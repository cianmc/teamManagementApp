package com.example.cianm.testauth.Entity;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by cianm on 08/03/2018.
 */

@IgnoreExtraProperties
public class TempRatingFootball {

    int points, goals, wides, tackles, turnovers, yellowCards, redCards, blackCards;

    public TempRatingFootball(){}

    public TempRatingFootball(int points, int goals, int wides, int tackles, int turnovers, int yellowCards, int redCards, int blackCards) {
        this.points = points;
        this.goals = goals;
        this.wides = wides;
        this.tackles = tackles;
        this.turnovers = turnovers;
        this.yellowCards = yellowCards;
        this.redCards = redCards;
        this.blackCards = blackCards;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public int getWides() {
        return wides;
    }

    public void setWides(int wides) {
        this.wides = wides;
    }

    public int getTackles() {
        return tackles;
    }

    public void setTackles(int tackles) {
        this.tackles = tackles;
    }

    public int getTurnovers() {
        return turnovers;
    }

    public void setTurnovers(int turnovers) {
        this.turnovers = turnovers;
    }

    public int getYellowCards() {
        return yellowCards;
    }

    public void setYellowCards(int yellowCards) {
        this.yellowCards = yellowCards;
    }

    public int getRedCards() {
        return redCards;
    }

    public void setRedCards(int redCards) {
        this.redCards = redCards;
    }

    public int getBlackCards() {
        return blackCards;
    }

    public void setBlackCards(int blackCards) {
        this.blackCards = blackCards;
    }
}
