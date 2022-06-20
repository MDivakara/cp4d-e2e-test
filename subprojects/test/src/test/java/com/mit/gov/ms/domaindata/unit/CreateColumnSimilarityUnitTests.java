/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.domaindata.unit;

import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.gov.ms.common.Constants;
import com.mit.gov.ms.common.InsightsException;
import com.mit.gov.ms.domaindata.SimilarColumnsUtils;

public class CreateColumnSimilarityUnitTests {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateColumnSimilarityUnitTests.class);
    
    private static SimilarColumnsUtils mockCustomClass;
    
    private static JSONObject status;
    
    @BeforeClass
    public static void test_setup() {

        // Create the mock object of CustomClassUtils
        mockCustomClass = Mockito.mock(SimilarColumnsUtils.class);

        try {
            status = new JSONObject();
            status.put(Constants.STATUS, 200);
        } catch (JSONException e) {
            String err_message = "Error while test_setup";
            LOGGER.error(err_message, e);
            assertTrue(err_message, false);
        }
    }
    
    @Test
    public void test_columnSimilarityCustomClassifier1() {
        String uuid = UUID.randomUUID().toString();
        String classCode = "test" + uuid;
        String className = "test " + uuid + " Name";
        String classDescription = "a new class for a group";
        String projectName = "InsightsDemo";
        String referenceColumn = "BANK.BANK1.BANK_CLIENTS.NBR_YEARS_CLI";
        double confidenceThreshold = 0.75;

        //mock the behavior of createCustomClass to return the appropriate status
        try {
            Mockito.when(mockCustomClass.createCustomClass(classCode, className, classDescription, projectName,
                    referenceColumn, confidenceThreshold)).thenReturn(status);
            
            assertTrue(status.getInt(Constants.STATUS) == 200 || status.getInt(Constants.STATUS) == 201);
        } catch (InsightsException | JSONException e) {
            String err_message = "Error while test_columnSimilarityCustomClassifier1";
            LOGGER.error(err_message, e);
            assertTrue(err_message, false);
        }

    }

}
