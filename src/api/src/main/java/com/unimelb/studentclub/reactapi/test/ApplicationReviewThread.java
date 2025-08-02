package com.unimelb.studentclub.reactapi.test;

import com.unimelb.studentclub.reactapi.domain.Application;
import com.unimelb.studentclub.reactapi.domain.ApplicationRepository;
import com.unimelb.studentclub.reactapi.port.postgres.ApplicationRepositoryImpl;
import com.unimelb.studentclub.reactapi.port.postgres.ConnectionProvider;
import com.unimelb.studentclub.reactapi.port.postgres.ApplicationRepositoryImpl;

import java.sql.Connection;
import java.sql.SQLException;

public class ApplicationReviewThread extends Thread {
    private final Application application;
    private final ApplicationRepository applicationRepository;
    private final ConnectionProvider connectionProvider;

    public ApplicationReviewThread(Application application, ConnectionProvider connectionProvider) {
        this.application = application;
        this.connectionProvider = connectionProvider;
        this.applicationRepository = new ApplicationRepositoryImpl(connectionProvider);
    }

    @Override
    public void run() {
        applicationRepository.review(application.getId());
    }

}
