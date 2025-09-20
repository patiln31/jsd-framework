package org.jsd.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsd.utils.ConfigReader;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retry analyzer that automatically retries failed test methods.
 * 
 * This class implements TestNG's IRetryAnalyzer interface to provide
 * automatic retry functionality for flaky tests. It can be configured
 * to enable/disable retry and set maximum retry count.
 * 
 * Configuration options:
 * - retry.failed.tests: Enable/disable retry (true/false)
 * - max.retry.count: Maximum number of retry attempts
 * 
 * Can be overridden via system properties:
 * -Dretry.failed.tests=false
 * -Dmax.retry.count=3
 * 
 * @author JSD Framework Team
 * @version 1.0
 */
public class RetryAnalyzer implements IRetryAnalyzer {
    /** Logger instance for this class */
    private static final Logger log = LogManager.getLogger(RetryAnalyzer.class);
    
    /** Current retry attempt counter */
    private int retryCount = 0;
    
    /** Maximum number of retry attempts allowed */
    private final int maxRetryCount;
    
    /** Flag to enable/disable retry functionality */
    private final boolean retryEnabled;
    
    /**
     * Constructor that initializes retry configuration.
     * 
     * Reads configuration from:
     * 1. System properties (highest priority)
     * 2. Configuration file (fallback)
     * 3. Default values (final fallback)
     */
    public RetryAnalyzer() {
        // Check system properties first, then config file, then defaults
        this.retryEnabled = Boolean.parseBoolean(System.getProperty("retry.failed.tests", 
            ConfigReader.getProperty("retry.failed.tests", "true")));
        this.maxRetryCount = Integer.parseInt(System.getProperty("max.retry.count", 
            ConfigReader.getProperty("max.retry.count", "2")));
    }
    
    /**
     * Determines whether a failed test should be retried.
     * 
     * This method is called by TestNG when a test fails. It decides
     * whether to retry the test based on configuration and current
     * retry count.
     * 
     * @param result The test result containing failure information
     * @return true if the test should be retried, false otherwise
     */
    @Override
    public boolean retry(ITestResult result) {
        // Check if retry is disabled globally
        if (!retryEnabled) {
            log.debug("Retry is disabled for test '{}'", result.getMethod().getMethodName());
            return false;
        }
        
        // Check if we haven't exceeded maximum retry attempts
        if (retryCount < maxRetryCount) {
            retryCount++;
            log.warn("Retrying test '{}' - Attempt {} of {}", 
                result.getMethod().getMethodName(), retryCount, maxRetryCount);
            return true;
        }
        
        // Maximum retries exceeded
        log.error("Test '{}' failed after {} attempts", 
            result.getMethod().getMethodName(), maxRetryCount);
        return false;
    }
}