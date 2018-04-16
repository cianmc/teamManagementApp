package com.example.cianm.testauth.Entity;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by cianm on 16/03/2018.
 */

@IgnoreExtraProperties
public class FixtureRatingHurling {

    String dateOfEvent, playerName;
    int goals, points, wides, tackles, turnovers, yellowCards, redCards;
    double attackerRating, defenderRating, overallRating;

    public FixtureRatingHurling(){}

    public FixtureRatingHurling(String dateOfEvent, String playerName, int goals, int points, int wides, int tackles, int turnovers, int yellowCards, int redCards, double attackerRating, double defenderRating, double overallRating) {
        this.dateOfEvent = dateOfEvent;
        this.playerName = playerName;
        this.goals = goals;
        this.points = points;
        this.wides = wides;
        this.tackles = tackles;
        this.turnovers = turnovers;
        this.yellowCards = yellowCards;
        this.redCards = redCards;
        this.attackerRating = attackerRating;
        this.defenderRating = defenderRating;
        this.overallRating = overallRating;
    }

    public String getDateOfEvent() {
        return dateOfEvent;
    }

    public void setDateOfEvent(String dateOfEvent) {
        this.dateOfEvent = dateOfEvent;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
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

    public double getAttackerRating() {
        return attackerRating;
    }

    public void setAttackerRating(double attackerRating) {
        this.attackerRating = attackerRating;
    }

    public double getDefenderRating() {
        return defenderRating;
    }

    public void setDefenderRating(double defenderRating) {
        this.defenderRating = defenderRating;
    }

    public double getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(double overallRating) {
        this.overallRating = overallRating;
    }
}
