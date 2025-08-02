package com.unimelb.studentclub.reactapi.domain;

import java.util.List;
import java.util.Map;

public interface EventRepository {
    public void create(Event event);
    public void modify(Event event);
    public void cancel(String eventId);
    public Map<Event, Integer> getAll();
    public Event getDetails(Event event);
    public Map<Event, Integer> getAllAdminEvent(List<Integer> ClubIDs);
}
