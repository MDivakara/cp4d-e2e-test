/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.gov.ms.test.standalone.IntegrationTestDataSetup;

public class TestSetupAndCleanup {

    private final Logger LOGGER = LoggerFactory.getLogger(TestSetupAndCleanup.class);
    
    IntegrationTestDataSetup integrationTestDataSetup = new IntegrationTestDataSetup();
    
    @Test
    public void test_dataSetup() {
        LOGGER.info("TEST DATA SETUP - BEGIN");
        boolean res = integrationTestDataSetup.setUpTestData();
        LOGGER.info("TEST DATA SETUP - END WITH RESPONSE=" + res);
    }
    
    @Test
    public void test_dataCleanup() {
        LOGGER.info("TEST DATA CLEANUP - BEGIN");
        boolean res = integrationTestDataSetup.cleanupData();
        LOGGER.info("TEST DATA CLEANUP - END WITH RESPONSE=" + res);
    }

}
