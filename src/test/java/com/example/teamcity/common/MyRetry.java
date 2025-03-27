package com.example.teamcity.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class MyRetry implements IRetryAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(MyRetry.class);

    private int retryCount = 0;
    private static final int maxRetryCount = 3;

    @Override
    public boolean retry(ITestResult result) {
        logger.info("Retrying test...");
        if (retryCount < maxRetryCount) {
            retryCount++;
            return true;
        }
        return false;
    }
}