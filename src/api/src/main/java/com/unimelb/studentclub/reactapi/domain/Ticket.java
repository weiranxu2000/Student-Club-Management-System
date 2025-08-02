package com.unimelb.studentclub.reactapi.domain;

public class Ticket {
    private String rsvpId;
    private int studentId;
    private Status status;

    public static enum Status {
        created,
        cancelled
    }

    public String getRsvpId() {
        return rsvpId;
    }

    public void setRSVPId(String rsvpId) {
        this.rsvpId = rsvpId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
