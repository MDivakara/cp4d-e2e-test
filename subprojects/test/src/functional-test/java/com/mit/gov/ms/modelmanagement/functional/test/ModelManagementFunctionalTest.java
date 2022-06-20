/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.modelmanagement.functional.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.gov.ms.common.Constants;
import com.mit.gov.ms.common.InsightsException;
import com.mit.gov.ms.common.modelmanagement.InsightsModelManager;
import com.mit.gov.ms.test.standalone.UnitTestUtils;

public class ModelManagementFunctionalTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(ModelManagementFunctionalTest.class);

    
    private static InputStream modelInputStream = null;
    private static String model_id = "";
    private static int rev_no = 0;
    private static String model_version_id = "";
    private final static String tenant_id = "9999";
    private static InsightsModelManager modelMngInst = null;

    @BeforeClass
    public static void testSetup() {
        try {
            UnitTestUtils.initMetaDataStores();
            modelInputStream = ModelManagementFunctionalTest.class.getClassLoader().getResourceAsStream("DemoModel.json");
            modelMngInst = new InsightsModelManager(tenant_id);
        } catch (InsightsException e) {
            String err_message = "Error while testSetup";
            LOGGER.info(err_message);
            assertTrue(err_message, false);
            
        }
    }

    @AfterClass
    public static void cleanup() {
        
    }
    
    

    /**
     * store a sample model in model store.
     */
    @Test
    public void test_storeModel() {
    	JSONObject res = modelMngInst.storeModel("func_test_model", "demo model stored to perform functional test", modelInputStream);
    	assertFalse("unable to store the model", res == null);
    	assertFalse("unable to store the model", res.isEmpty());
    	assertFalse("unable to store the model", res.isEmpty());
    	assertTrue("failed to get the status code after storing model", res.containsKey(Constants.STATUS_CODE));
    	assertTrue("failed to get the status code after storing model", res.containsKey(Constants.MODEL_ID));
    	assertTrue("failed to get the status code after storing model", res.containsKey(Constants.MODEL_VERSION_ID));
    	try {
			model_id = res.getString(Constants.MODEL_ID);
			model_version_id = res.getString(Constants.MODEL_VERSION_ID);
			assertTrue("failed to get model ID", res.getString(Constants.MODEL_ID) != null);
			assertTrue("failed to get model ID", !res.getString(Constants.MODEL_ID).isEmpty());
			
			assertTrue("failed to get model version ID", res.getString(Constants.MODEL_VERSION_ID) != null);
			assertTrue("failed to get model version ID", !res.getString(Constants.MODEL_VERSION_ID).isEmpty());
			
		} catch (JSONException e) {
			 String err_message = "Error while fetching model details";
			 LOGGER.error(err_message);
	         assertTrue(err_message, false);
		}
    	
    	test_getModelbyVersion();
    	test_updateModel();
    	test_delete();
    }

    /**
     * fetch the model by model_id and model_version_id
     */
    public void test_getModelbyVersion() {
        InputStream is = modelMngInst.getModelbyVersion(model_id, model_version_id);
		assertTrue("model is empty or null", is != null);
    }

    /**
     * updateModel test
     */
    public void test_updateModel() {
        JSONArray updateInfos = null;
        InputStream modelStream = ModelManagementFunctionalTest.class.getClassLoader().getResourceAsStream("UpdatedDemoModel.json");;
        JSONObject res = modelMngInst.updateModel(model_id, updateInfos, modelStream);
        assertFalse("invalid response after updating model", res == null);
        assertFalse("invalid response after updating model", res.isEmpty());
        try {
            assertTrue("no status code after updaing model", res.containsKey(Constants.STATUS_CODE));
            assertEquals("invalid status code", res.getInt(Constants.STATUS_CODE), 200);
            
            assertTrue("no status code after updaing model", res.containsKey(Constants.MODEL_ID));
            assertEquals("invalid status code", res.getString(Constants.MODEL_ID), model_id);
        } catch (JSONException e) {
            String err_message = "Error while updating model details";
            LOGGER.error(err_message);
            assertTrue(err_message, false);
        }
    }

//    /**
//     * get model rev number test
//     */
//    @Test
//    public void test_getRevisionNobyModleID() {
//
//    }

    /**
     * delete model stored
     */
    public void test_delete() {
        
        JSONObject res = modelMngInst.delete(model_id);
        assertFalse("invalid respose while deleting model", res.isEmpty());
        assertFalse("invalid respose while deleting model", res == null);
        try {
            assertTrue("invalid model ID", res.containsKey(Constants.MODEL_ID));
            assertEquals("invalid model ID", res.getString(Constants.MODEL_ID), model_id);
        } catch (JSONException e) {
            String err_message = "Error while deleting model";
            LOGGER.error(err_message);
            assertTrue(err_message, false);
        }

    }

}
