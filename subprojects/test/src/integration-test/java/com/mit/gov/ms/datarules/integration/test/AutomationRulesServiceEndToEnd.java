/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.datarules.integration.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.gov.ms.common.HttpUtils;
import com.mit.gov.ms.common.InsightsConfiguration;
import com.mit.gov.ms.common.InsightsException;
import com.mit.gov.ms.test.standalone.IntegrationTestDataSetup;


public class AutomationRulesServiceEndToEnd {

	private final static Logger LOGGER = LoggerFactory.getLogger(AutomationRulesServiceEndToEnd.class);

	private static JSONArray actionableRules = new JSONArray();
	private static JSONObject actionableRulesResult =new JSONObject();
	private static String actionableRulesurl = InsightsConfiguration.getInstance().getIISBaseUrl()
			+ "/ia/api/dataQualityConfigurationRules/";
	private static String enableSARURL = InsightsConfiguration.getInstance().getTestProperty("insights.base.url")
			+ "/api/insights/v1/suggestions/automationrules";
	private static String basic_auth = InsightsConfiguration.getInstance().getIISBasicAuthToken();
	static HttpResponse HTTPResp;
	static Map<String, String> header = new HashMap<String, String>();
	static List<String> automationRulesRids = new ArrayList<String>();
	static IntegrationTestDataSetup testDataSetupInst = null;

	@BeforeClass

	public static void test_setup() throws NullPointerException, FileNotFoundException {	
		//test data setup
//		IntegrationTestDataSetup testDataSetupInst = new IntegrationTestDataSetup();
//		assertTrue("Unable to setup the test data", testDataSetupInst.setUpTestData());
        LOGGER.info("SARURL="+enableSARURL);

		//check whether automation rules are available
		header.put("Content-Type", "application/json");
		header.put("Authorization", "Basic " + basic_auth);	
		try {
            LOGGER.info("GET " + actionableRulesurl);
			HTTPResp = HttpUtils.executeRestAPI(actionableRulesurl, "GET", header, null);
			int status1 = HTTPResp.getStatusLine().getStatusCode();
			assertTrue("failed to get automation rules List , status=" + status1, status1 == 200 || status1 == 201);
			actionableRulesResult = HttpUtils.parseResponseAsJSONObject(HTTPResp);
			assertTrue("failed to get automation rules List", actionableRulesResult != null);
			LOGGER.info(actionableRulesResult.toString());
			assertTrue("failed to get automation rules List", actionableRulesResult.has("actionableRules"));
			actionableRules = actionableRulesResult.getJSONArray("actionableRules");
			for (int i =0; i < actionableRules.size(); i++ ) {
				if ( actionableRules.getJSONObject(i).has("status_") && actionableRules.getJSONObject(i).getString("status_").equals("SUGGESTED")) {
				String suggestedRuleRid = actionableRules.getJSONObject(i).getString("rid_");
	            LOGGER.info("DELETE " + actionableRulesurl + suggestedRuleRid);
				HTTPResp = HttpUtils.executeRestAPI(actionableRulesurl + suggestedRuleRid, "DELETE", header, null);
				int status2 = HTTPResp.getStatusLine().getStatusCode();
				assertTrue("failed to delete automation rules , status=" + status2, status2 == 200 || status2 == 201);
				} else {
					automationRulesRids.add(actionableRules.getJSONObject(i).getString("rid_"));
				}
			}
		} catch (InsightsException | JSONException e) {
			LOGGER.error("Erorr while getting automation rules", e);	
			assertTrue("Erorr while getting automation rules", false);

		}
		EnableSARFeatureOnce();
	}
	
	private static void EnableSARFeatureOnce() {
		try {
			String featureEnableURL = enableSARURL + "/run";
			// enable the suggested automation feature
            LOGGER.info("GET " + featureEnableURL);
			HTTPResp = HttpUtils.executeRestAPI(featureEnableURL , "GET", header, null);
			int status2 = HTTPResp.getStatusLine().getStatusCode();
			assertTrue("failed to enable suggested automation rules feature, status=" + status2, status2 == 200 );
		} catch (InsightsException e) {
			LOGGER.error("Erorr while Enabling SAR feature", e);
			assertTrue("Erorr while Enabling SAR feature-" + e.getMessage(), false);
		}
	}

	@AfterClass
	public static void test_cleanup() {
		// delete rules in case of test failure.
		try {
			header.put("Content-Type", "application/json");
			header.put("Authorization", "Basic " + basic_auth);
            LOGGER.info("GET " + actionableRulesurl);
			HTTPResp = HttpUtils.executeRestAPI(actionableRulesurl, "GET", header, null);
			actionableRulesResult = HttpUtils.parseResponseAsJSONObject(HTTPResp);
            assertNotNull("GET response is NULL " , actionableRulesResult);
            LOGGER.info(actionableRulesResult.toString());
			actionableRules = actionableRulesResult.getJSONArray("actionableRules");
			for (int i =0; i < actionableRules.size(); i++ ) {
				if ( actionableRules.getJSONObject(i).has("status_") && actionableRules.getJSONObject(i).getString("status_").equals("SUGGESTED")) {
				String suggestedRuleRid = actionableRules.getJSONObject(i).getString("rid_");
	            LOGGER.info("DELETE " + actionableRulesurl + suggestedRuleRid);
				HTTPResp = HttpUtils.executeRestAPI(actionableRulesurl + suggestedRuleRid, "DELETE", header, null);
				int status = HTTPResp.getStatusLine().getStatusCode();
				assertTrue("failed to delete automation rules , status=" + status, status == 200 || status == 201);
				}
			}
			//assertTrue("Unable to cleanup the test data", testDataSetupInst.cleanupData());
		} catch (InsightsException | JSONException e) {
			LOGGER.error("Erorr while cleaning AutomationRulesServiceEndToEnd:test_cleanup", e);
			//assertTrue("Erorr while cleaning AutomationRulesServiceEndToEnd:test_cleanup" + e.getMessage(), false);
		}
		LOGGER.info("Clean up activity finished..");
	}

	@Test
	public void test() {
		//wait for SAR generation.
		boolean isRulesGenerated = false;
		for (int count = 0; count< 15; count++) {
			LOGGER.info("Checking if Rules generated");
			try {
		        String featureStatusURL = enableSARURL + "/status";
		        LOGGER.info("featureStatusURL=" + featureStatusURL);
		        // get status of the suggested automation feature
	            LOGGER.info("GET " + featureStatusURL);
		        HTTPResp = HttpUtils.executeRestAPI(featureStatusURL , "GET", header, null);
		        int status2 = HTTPResp.getStatusLine().getStatusCode();
		        assertTrue("failed to get status for suggested automation rules feature, status=" + status2, status2 == 200 );
		        LOGGER.info(HttpUtils.parseResponseAsJSONObject(HTTPResp).toString());
			    
	            LOGGER.info("GET " + actionableRulesurl);
				HTTPResp = HttpUtils.executeRestAPI(actionableRulesurl, "GET", header, null);
				actionableRulesResult = HttpUtils.parseResponseAsJSONObject(HTTPResp);
	            assertNotNull("GET response is NULL " , actionableRulesResult);
	            LOGGER.info(actionableRulesResult.toString());
				actionableRules = actionableRulesResult.getJSONArray("actionableRules");
				for (int i =0; i < actionableRules.size(); i++ ) {
					if ( actionableRules.getJSONObject(i).has("status_") && actionableRules.getJSONObject(i).getString("status_").equals("SUGGESTED")) {
						isRulesGenerated = true;
						break;
					}
				}
				if ( isRulesGenerated ) {
				    LOGGER.info("Rules generated");
					break;
				}
				int status1 = HTTPResp.getStatusLine().getStatusCode();
				assertTrue("failed to get automation rules from IIS server, status=" + status1, status1 == 200 || status1 == 201);
	            LOGGER.info("Waiting for Rules to generated");
				Thread.sleep(60 * 1000);
			} catch (InsightsException | JSONException | InterruptedException e) {
				LOGGER.error("failed to retrive suggested rules" + e);
				assertTrue("failed to retrive suggested rules", false);
			}
		}
		
		// validate suggested automation rules.
		assertTrue("unexpected rules size",actionableRules.size() > 0);
		
		for (int i = 0; i < actionableRules.size(); i++) {
			try {
				//don't validate for existing automation rules 
				if (automationRulesRids.contains(actionableRules.getJSONObject(i).getString("rid_"))) {
					continue;
				}
				
				assertTrue("failed to get condition_ from suggestion",
						actionableRules.getJSONObject(i).has("condition_"));
				
				assertTrue("failed to get status_ from suggestion", actionableRules.getJSONObject(i).has("status_"));
				
				assertTrue("failed to get status_ from suggestion",
						actionableRules.getJSONObject(i).getString("status_").equals("SUGGESTED"));
				
				assertTrue("failed to get valid_ from suggestion", actionableRules.getJSONObject(i).has("valid_"));
				
				assertTrue("failed to get valid_ from suggestion",
						actionableRules.getJSONObject(i).getString("valid_").equals("true"));
				
				assertTrue("failed to get rid_ from suggestion", actionableRules.getJSONObject(i).has("rid_"));
				
				assertTrue("failed to get description_ from suggestion",
						actionableRules.getJSONObject(i).has("description_"));
				
				assertTrue("failed to get actions_ from suggestion", actionableRules.getJSONObject(i).has("actions_"));
				
				assertTrue("failed to get createdOn_ from suggestion",
						actionableRules.getJSONObject(i).has("createdOn_"));
				
				assertTrue("failed to get name_ from suggestion", actionableRules.getJSONObject(i).has("name_"));
				
				assertTrue("failed to get isSystemGenerated_ from suggestion",
						actionableRules.getJSONObject(i).has("isSystemGenerated_"));
				
				assertTrue("invalid isSystemGenerated_ from suggestion",
						actionableRules.getJSONObject(i).getString("isSystemGenerated_").equals("false"));
				
				assertTrue("failed to get name_ from suggestion",
						actionableRules.getJSONObject(i).has("name_"));
				
				assertTrue("invalid name_ from suggestion",
						actionableRules.getJSONObject(i).getString("name_").startsWith("SAR_"));
				
			} catch (JSONException e) {
				LOGGER.error("JSON Exception" + e);
				assertTrue("JSON Exception-" + e.getMessage(), false);
			}
		}

	}

}