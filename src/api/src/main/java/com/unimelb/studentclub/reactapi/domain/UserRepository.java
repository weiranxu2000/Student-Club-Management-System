package com.unimelb.studentclub.reactapi.domain;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
    public String getPasswordById(int id, String role);
    public AppUser findById(int id);
}
