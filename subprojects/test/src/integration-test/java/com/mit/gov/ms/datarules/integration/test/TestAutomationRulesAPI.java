package com.mit.gov.ms.datarules.integration.test;
/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

//
//
//import static org.junit.Assert.assertTrue;
//
//import java.io.FileNotFoundException;
//import java.io.InputStream;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//import org.slf4j.LoggerFactory;
//import org.slf4j.Logger;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.wink.json4j.JSONException;
//import org.apache.wink.json4j.JSONObject;
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//
//public class TestAutomationRulesAPI {
//	private static HttpEntity bodyEntity;
//	private static HttpResponse HTTPResp;
//	private static String POSTurl;
//	private static String GETurl;
//	private static String DELETEurl;
//	private static Map<String, String> header;
//	private static JSONObject input;
//	private static final Logger LOGGER = LoggerFactory.getLogger(DataRulesUtilAssetDataclass.class);
//	
//	@BeforeClass public static void test_setup() throws NullPointerException, FileNotFoundException {
//        String iis_base_url = InsightsConfiguration.getInstance().getIISBaseUrl();
//		POSTurl = iis_base_url + "/ia/api/dataQualityConfigurationRules/" ;
//		DELETEurl=iis_base_url + "/ia/api/dataQualityConfigurationRules/" ;
//		GETurl= iis_base_url + "/igc-rest/v1/assets";
//		
//		String basicAuth = InsightsConfiguration.getInstance().getIISBasicAuthToken();
//		header = new HashMap<String, String>();
//		header.put("Content-Type", "application/json");
//		header.put("Authorization", "Basic " + basicAuth);
//		InputStream is = AprioriTest.class.getClassLoader().getResourceAsStream("AutomationRulePOST.json");
//		input = new JSONObject();
//		try {
//			input = (JSONObject) org.apache.wink.json4j.JSON.parse(is);
//			UUID uuid = UUID.randomUUID();
//			input.put("name_", "AutomationRuleUnitTest "+uuid.toString());
//			LOGGER.info(input.toString());
//		} catch (JSONException e1) {
//			LOGGER.error("Error while parsing JSON", e1);
//			assertTrue("Error while parsing JSON", false);
//		}
//		
//
//	}
//	
//	@AfterClass
//    public static void test_cleanup() {
//		//clear input data
//		bodyEntity=null;
//		HTTPResp=null;
//		POSTurl=null;
//		GETurl=null;
//		DELETEurl=null;
//		header=null;
//		input=null;
//	}
//
//	@Test
//	public void test() {
//
//		try {
//			
//			bodyEntity = HttpUtils.getHTTPEntity(input.toString());
//			LOGGER.info("POST " + POSTurl);
//			HTTPResp = HttpUtils.executeRestAPI(POSTurl, "POST", header, bodyEntity);
//			JSONObject httpRespJSON = HttpUtils.parseResponseAsJSONObject(HTTPResp);
//			int status = HTTPResp.getStatusLine().getStatusCode();
//			int PSOTStatusCode = HTTPResp.getStatusLine().getStatusCode();
//			boolean POSTStatus = PSOTStatusCode == 200 || PSOTStatusCode == 201;
//			assertTrue("POST response is " + PSOTStatusCode, POSTStatus);
//
//			if (status == 200 || status == 201) {
//				try {
//					GETurl += "/" + httpRespJSON.getString("rid");
//					DELETEurl += httpRespJSON.getString("rid");
//				} catch (JSONException e) {
//					LOGGER.error("Error while parsing JSON", e);
//				}
//
//	            LOGGER.info("GET " + GETurl);
//				HTTPResp = HttpUtils.executeRestAPI(GETurl, "GET", header, null);
//				int GETStatusCode = HTTPResp.getStatusLine().getStatusCode();
//				boolean GETStatus = GETStatusCode == 200;
//				assertTrue("GET response is " + GETStatusCode, GETStatus);
//
//	            LOGGER.info("DELETE " + DELETEurl);
//				HTTPResp = HttpUtils.executeRestAPI(DELETEurl, "DELETE", header, null);
//				int DELETEStatusCode = HTTPResp.getStatusLine().getStatusCode();
//				boolean DELETEStatus = DELETEStatusCode == 200 || DELETEStatusCode == 202 || DELETEStatusCode == 204;
//				assertTrue("DELETE response is " + DELETEStatusCode, DELETEStatus);
//
//			}
//
//		} catch (InsightsException e) {
//			LOGGER.error("Error while making/retrieving/deleting automation rule", e);
//			assertTrue("Error while making/retrieving/deleting automation rule", false);
//		}
//	}
//
//}
