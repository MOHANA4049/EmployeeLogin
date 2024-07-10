
//    public EmployeeLogin getFromMapByEmployeeId(int empId) {
//        try {
//            RBucket<EmployeeLogin> loginRBucket = redissonClient.getBucket(employeeLoginBucketKey + empId);
//            return loginRBucket.get();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

package com.kapturecx.employeelogin.service;

import com.kapturecx.employeelogin.entity.EmployeeLogin;
import org.redisson.api.RBucket;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedissonClient redissonClient;

    private final String sessionPrefix = "session:";
    private final String employeeLoginBucketKey = "EMPID:EmployeeLogin";

    public void saveInMap(EmployeeLogin employeeLogin) {
        try {
            RBucket<EmployeeLogin> loginRBucket = redissonClient.getBucket(employeeLoginBucketKey + employeeLogin.getClientId());
            loginRBucket.set(employeeLogin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveSession(int  id, int clientId, int timeoutSeconds) {
        String sessionKey = sessionPrefix + id;
        RBucket<Integer> sessionBucket = redissonClient.getBucket(sessionKey);
        sessionBucket.set(clientId, timeoutSeconds, TimeUnit.SECONDS);
    }

    public void removeSession(int id) {
        String sessionKey = sessionPrefix + id;
        RBucket<Integer> sessionBucket = redissonClient.getBucket(sessionKey);
        sessionBucket.delete();
    }
}
