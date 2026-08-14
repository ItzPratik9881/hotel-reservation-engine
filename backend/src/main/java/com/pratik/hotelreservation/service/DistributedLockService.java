package com.pratik.hotelreservation.service;

public interface DistributedLockService {

    boolean tryLock(String lockKey);

    void unlock(String lockKey);
}