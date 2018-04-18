package com.example.cianm.testauth.Entity;

import android.app.Application;

/**
 * Created by cianm on 02/02/2018.
 */

public class GlobalVariables extends Application {

    String currentTeam, currentEvent, currentPlayer;

    public String getCurrentTeam(){
        return currentTeam;
    }

    public void setCurrentTeam (String currentTeam){
        this.currentTeam = currentTeam;
    }

    public String getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(String currentEvent) {
        this.currentEvent = currentEvent;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
}
