package com.unimelb.studentclub.reactapi.security;

import com.unimelb.studentclub.reactapi.port.postgres.UserRepositoryImpl;
import jakarta.servlet.annotation.WebServlet;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.unimelb.studentclub.reactapi.domain.AppUser;


// Custom class to load users' details for authentication/authorization purposes
@WebServlet
public class UserDetailServiceImpl implements UserDetailsService {

    UserRepositoryImpl userMapper;

    public UserDetailServiceImpl(UserRepositoryImpl userMapper) {
        this.userMapper = userMapper;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        int id = Integer.parseInt(username);
        AppUser user = userMapper.findById(id);

        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        UserBuilder builder;
        if (user.getAdminId() != 0) {
            builder = User.withUsername(String.valueOf(user.getAdminId()));
        } else if (user.getStudentId() != 0) {
            builder = User.withUsername(String.valueOf(user.getStudentId()));
        } else {
            throw new UsernameNotFoundException("Neither adminId nor studentId found for user");
        }

        builder.password(user.getPassword());
        builder.roles(user.getRole());

        return builder.build();
    }
}
