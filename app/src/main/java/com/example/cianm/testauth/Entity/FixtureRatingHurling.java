package com.example.cianm.testauth.Entity;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by cianm on 16/03/2018.
 */

@IgnoreExtraProperties
public class FixtureRatingHurling {

    String goals, points, wides, tackles, turnovers, yellowCards, redCards, attackerRating, defenderRating, overallRating, dateOfEvent, playerName;

    public FixtureRatingHurling(){}

    public FixtureRatingHurling(String goals, String points, String wides, String tackles, String turnovers, String yellowCards, String redCards, String attackerRating, String defenderRating, String overallRating, String dateOfEvent, String playerName) {
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
        this.dateOfEvent = dateOfEvent;
        this.playerName = playerName;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
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

    public String getAttackerRating() {
        return attackerRating;
    }

    public void setAttackerRating(String attackerRating) {
        this.attackerRating = attackerRating;
    }

    public String getDefenderRating() {
        return defenderRating;
    }

    public void setDefenderRating(String defenderRating) {
        this.defenderRating = defenderRating;
    }

    public String getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(String overallRating) {
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
}
