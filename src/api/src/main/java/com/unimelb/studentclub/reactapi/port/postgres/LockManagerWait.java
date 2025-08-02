package com.unimelb.studentclub.reactapi.port.postgres;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LockManagerWait {

    private static LockManagerWait instance;

    //Key: lockable
    //Value: owner
    private ConcurrentMap<String, String> lockMap;

    public static synchronized LockManagerWait getInstance() {
        if(instance == null) {
            instance = new LockManagerWait();
        }
        return instance;
    }

    private LockManagerWait() {
        lockMap = new ConcurrentHashMap<String, String>();
    }

    public synchronized void acquireLock(String lockable, String owner) {
        while(lockMap.containsKey(lockable)) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        lockMap.put(lockable, owner);
        System.out.println("Lock by " + owner);
    }

    public synchronized void releaseLock(String lockable, String owner) {
        // Check the owner of the lock is the one attempting to release it
        if (lockMap.containsKey(lockable)) {
            String currentOwner = lockMap.get(lockable);
            if (!currentOwner.equals(owner)) {
                throw new IllegalMonitorStateException("Attempt to release a lock not owned by the current owner");
            }
            lockMap.remove(lockable);
            System.out.println("Lock released by " + owner);
            notifyAll();
        } else {
            throw new IllegalArgumentException("No lock present for the specified lockable resource");
        }
    }
}