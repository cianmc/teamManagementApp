package com.example.cianm.testauth.Entity;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by cianm on 25/04/2018.
 */

@IgnoreExtraProperties
public class BestPlayer {

    String nameBP;
    double attackerRating, defenderRating, overallRating;
    int noOfEventsPlayed;

    public BestPlayer() {
    }

    public BestPlayer(String nameBP, double attackerRating, double defenderRating, double overallRating, int noOfEventsPlayed) {
        this.nameBP = nameBP;
        this.attackerRating = attackerRating;
        this.defenderRating = defenderRating;
        this.overallRating = overallRating;
        this.noOfEventsPlayed = noOfEventsPlayed;
    }

    public String getNameBP() {
        return nameBP;
    }

    public void setNameBP(String nameBP) {
        this.nameBP = nameBP;
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

    public int getNoOfEventsPlayed() {
        return noOfEventsPlayed;
    }

    public void setNoOfEventsPlayed(int noOfEventsPlayed) {
        this.noOfEventsPlayed = noOfEventsPlayed;
    }
}

