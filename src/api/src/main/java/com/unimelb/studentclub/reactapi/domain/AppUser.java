package com.unimelb.studentclub.reactapi.domain;

public class AppUser {
    private String email;
    private int studentId;
    private int adminId;
    private String password;
    private String name;
    private String role;

    public AppUser(String email, int studentId, int adminId, String password, String name, String role) {
        this.email = email;
        this.studentId = studentId;
        this.adminId = adminId;
        this.password = password;
        this.name = name;
    }

    public AppUser(int studentId, String password, String role) {

    }


    public void setEmail(String email) {
        this.email = email;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getAdminId() {
        return adminId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
