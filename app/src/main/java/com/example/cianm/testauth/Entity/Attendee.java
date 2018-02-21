package com.example.cianm.testauth.Entity;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by cianm on 15/02/2018.
 */

@IgnoreExtraProperties
public class Attendee {

    String eventDate, userName, availability, eventType, userType, teamID;

    public Attendee(){}

    public Attendee(String eventDate, String userName, String availability, String eventType, String userType, String teamID) {
        this.eventDate = eventDate;
        this.userName = userName;
        this.availability = availability;
        this.eventType = eventType;
        this.userType = userType;
        this.teamID = teamID;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getTeamID() {
        return teamID;
    }

    public void setTeamID(String teamID) {
        this.teamID = teamID;
    }
}
