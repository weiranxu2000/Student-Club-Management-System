package com.unimelb.studentclub.reactapi.domain;

public interface VenueRepository {
    public int getId(String venueName);
    public int getCapacity(String venueName);
}
