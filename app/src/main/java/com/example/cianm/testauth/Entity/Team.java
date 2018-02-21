package com.example.cianm.testauth.Entity;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by cianm on 26/01/2018.
 */

@IgnoreExtraProperties
public class Team {

    String name, type, division;

    public Team(){
    }

    public Team(String name, String type, String division) {
        this.name = name;
        this.type = type;
        this.division = division;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String division) {
        this.name = name;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }
}
