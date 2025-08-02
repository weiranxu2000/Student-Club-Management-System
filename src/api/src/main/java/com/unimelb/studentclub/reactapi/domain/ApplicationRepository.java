package com.unimelb.studentclub.reactapi.domain;

import java.util.List;

public interface ApplicationRepository {
    public void create(Application application);
    public void modify(Application application);
    public void cancel(String applicationId);
    public void approve(String applicationId);
    public void reject(String applicationId);
    public void review(String applicationId);
    public List<Application> getAllAdminApplication(List<Integer> ClubIDs);
    public List<Application> getAllApplication();
}
