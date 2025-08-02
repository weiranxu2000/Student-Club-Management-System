package com.unimelb.studentclub.reactapi.domain;
import com.unimelb.studentclub.reactapi.port.postgres.StudentClubRepositoryImpl;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public class StudentClubService {
    private final StudentClubRepositoryImpl studentClubMapper;
    public StudentClubService(StudentClubRepositoryImpl studentClubMapper) {
        this.studentClubMapper = studentClubMapper;
    }

    public List<String> clubOfStudent(HttpServletRequest req) {
        int studentID = Integer.parseInt(req.getParameter("student_id"));
        //System.out.println(studentID);
        return studentClubMapper.clubOfStudent(studentID);
    }
}
