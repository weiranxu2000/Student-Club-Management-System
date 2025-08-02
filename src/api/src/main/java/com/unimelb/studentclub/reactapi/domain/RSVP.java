package com.unimelb.studentclub.reactapi.domain;

public class RSVP {
    private String id;
    private int studentId;
    private String eventId;

    public RSVP(String id, int studentId, String eventId) {
        this.id = id;
        this.studentId = studentId;
        this.eventId = eventId;
    }

    public RSVP(){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
