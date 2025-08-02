package com.unimelb.studentclub.reactapi.domain;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;

public class Application {
    private String id;
    private int clubId;
    private int amount;
    private String clubName;
    private Status status;
    private String description;
    //private Date date;
    //private LocalTime time;
    private ZonedDateTime date;
    public static enum Status {
        inDraft,
        submitted,
        inReview,
        approved,
        rejected,
        cancelled
    }
    public Application() {
    }

    public void setId (String id){
        this.id = id;
    }

    public void setClubId (int clubId){
        this.clubId = clubId;
    }

    public void setDescription (String description){
        this.description = description;
    }

    public void setDate (ZonedDateTime date){
        this.date = date;
    }

    public void setStatus (Application.Status status){
        this.status = status;
    }

    public void setClubName (String clubName){
        this.clubName = clubName;
    }

    public void setAmount (int amount){
        this.amount = amount;
    }

    // Methods to get a specific field's value of an event.
    public String getId(){
        return id;
    }

    public int getClubId(){
        return clubId;
    }

    public String getDescription(){
        return description;
    }

    public ZonedDateTime getDate(){
        return date;
    }

    public int getAmount(){
        return amount;
    }

    public Application.Status getStatus(){
        return status;
    }

    public String getClubName(){
        return clubName;
    }
}
