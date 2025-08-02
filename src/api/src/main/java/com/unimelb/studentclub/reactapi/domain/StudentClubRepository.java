package com.unimelb.studentclub.reactapi.domain;

import java.util.List;
import java.util.Map;

public interface StudentClubRepository {
    public boolean isAdmin(int studentID,String clubName);
    public List<String> clubOfStudent(int studentID);
    public boolean isStudentAdmin(int studentID);
    public List<Integer> clubIDOfStudent(int studentID);
    public List<Integer> studentIDOfClub(int studentID, List<Integer> ClubIDs);
    public List<String> studentNameOfClub(List<Integer> studentIDs);

    public List<Map<String, Object>> getAdminClubs(int studentId);
    public void deleteAdmin(int student_id, String clubName);
    public void addAdmin(String username ,int student_id, String clubName);
}
