package com.pratik.hotelreservation.service.impl;

import com.pratik.hotelreservation.service.DistributedLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class DistributedLockServiceImpl implements DistributedLockService {

    private final RedissonClient redissonClient;

    private static final long WAIT_TIME = 5;
    private static final long LEASE_TIME = 30;

    @Override
    public boolean tryLock(String lockKey) {

        RLock lock = redissonClient.getLock(lockKey);

        try {

            boolean acquired = lock.tryLock(
                    WAIT_TIME,
                    LEASE_TIME,
                    TimeUnit.SECONDS
            );

            if (acquired) {
                log.info("Distributed lock acquired: {}", lockKey);
            } else {
                log.warn("Could not acquire distributed lock: {}", lockKey);
            }

            return acquired;

        } catch (InterruptedException ex) {

            Thread.currentThread().interrupt();

            log.error(
                    "Interrupted while acquiring distributed lock: {}",
                    lockKey,
                    ex
            );

            return false;
        }
    }

    @Override
    public void unlock(String lockKey) {

        RLock lock = redissonClient.getLock(lockKey);

        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
            log.info("Distributed lock released: {}", lockKey);
        }
    }
}