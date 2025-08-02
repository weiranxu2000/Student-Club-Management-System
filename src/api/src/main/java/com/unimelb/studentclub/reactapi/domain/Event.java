package com.unimelb.studentclub.reactapi.domain;

import java.time.LocalTime;
import java.util.Date;

public class Event {
    // Parameters of an event in the database.
    private String id;
    private int clubId;
    private String title;
    private int cost;

    //parameters need to be hidden
    private String description;
    private int venueId;
    private Date date;
    private LocalTime time;
    private Status status;

    // Parameters of an event in the java object.
    private String clubName;
    private String venueName;
    private int capacity;

    public static enum Status {
        created,
        cancelled
    }

    // Method to create an event.
    public Event() {
    }

    // Methods to set a specific field's value of an event.
    public void setId (String id){
        this.id = id;
    }

    public void setClubId (int clubId){
        this.clubId = clubId;
    }

    public void setTitle (String title){
        this.title = title;
    }

    public void setDescription (String description){
        this.description = description;
    }

    public void setVenueId (int venueId){
        this.venueId = venueId;
    }

    public void setDate (Date date){
        this.date = date;
    }

    public void setTime (LocalTime time){
        this.time = time;
    }

    public void setCost (int cost){
        this.cost = cost;
    }

    public void setStatus (Status status){
        this.status = status;
    }

    public void setClubName (String clubName){
        this.clubName = clubName;
    }

    public void setVenueName (String venueName){
        this.venueName = venueName;
    }

    public void setCapacity (int capacity){
        this.capacity = capacity;
    }

    // Methods to get a specific field's value of an event.
    public String getId(){
        return id;
    }

    public int getClubId(){
        return clubId;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public int getVenueId(){
        return venueId;
    }

    public Date getDate(){
        return date;
    }

    public LocalTime getTime(){
        return time;
    }

    public int getCost(){
        return cost;
    }

    public Status getStatus(){
        return status;
    }

    public String getClubName(){
        return clubName;
    }

    public String getVenueName(){
        return venueName;
    }

    public int getCapacity(){
        return capacity;
    }
}
