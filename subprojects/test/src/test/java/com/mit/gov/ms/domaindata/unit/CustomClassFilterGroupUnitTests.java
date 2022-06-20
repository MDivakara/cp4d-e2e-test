package com.mit.gov.ms.domaindata.unit;
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
//
//import static org.junit.Assert.*;
//
//import java.io.InputStream;
//import org.slf4j.LoggerFactory;
//import org.slf4j.Logger;
//
//import org.apache.wink.json4j.JSONArray;
//import org.apache.wink.json4j.JSONException;
//import org.apache.wink.json4j.JSONObject;
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//
//public class CustomClassFilterGroupUnitTests {
//	
//	private static final Logger LOGGER = LoggerFactory.getLogger(CustomClassFilterGroupUnitTests.class);
//	private static IInsightsMetaDataStore insightsMetaDataStore;
//	
//	@BeforeClass
//    public static void test_setup() throws InsightsException {
//	    UnitTestUtils.initMetaDataStores();
//        insightsMetaDataStore = InsightsMetaDataStore.getInstance();
//    }
//	
//	@AfterClass
//    public static void test_cleanup() throws InsightsException {
//
//        // cleaning previous data from store
//        insightsMetaDataStore.delete(Constants.CUSTOM_CLASS, null, null);
//
//    }
//	
//	@Test
//	public void test_showAcceptedGroups() {
//		InputStream inputStream = CustomClassFilterGroupUnitTests.class.getClassLoader()
//				.getResourceAsStream("customclass1.json");
//
//		CustomClassUtils customClassUtils = new CustomClassUtils();
//		JSONObject jsonObject = new JSONObject();
//
//		try {
//			jsonObject = (JSONObject) org.apache.wink.json4j.JSON.parse(inputStream);
//			
//			JSONArray jsonArray1 = jsonObject.getJSONObject(Constants.CUSTOM_CLASS).getJSONArray("ec1481df.64b1b87d.ur86g3dcq.anond2f.rpt0qa.teqfuj424a1245kmvr345");
//			JSONArray jsonArray = customClassUtils.fetchGroupsBasedOnSuggestedState(jsonArray1, true, false);
//			assertTrue(jsonArray.length() > 0);
//			assertEquals(jsonArray.length(), 1);
//			assertTrue(jsonArray.getJSONObject(0).getString(Constants.SUGGESTED_STATE).equals(Constants.ACCEPTED));
//			
//		} catch (JSONException e) {
//			LOGGER.error("Error while parsing JSON", e);
//			fail();
//		}
//
//	}
//	
//	@Test
//	public void test_showAcceptedGroups2() {
//		InputStream inputStream = CustomClassFilterGroupUnitTests.class.getClassLoader()
//				.getResourceAsStream("customclass1.json");
//
//		CustomClassUtils customClassUtils = new CustomClassUtils();
//		JSONObject jsonObject = new JSONObject();
//
//		try {
//			jsonObject = (JSONObject) org.apache.wink.json4j.JSON.parse(inputStream);
//
//			JSONArray jsonArray1 = jsonObject.getJSONObject(Constants.CUSTOM_CLASS).getJSONArray("ec1481df.64b1b87d.ur86g3dcq.anond2f.rpt0qa.teqfuj424a1245kmvr345");
//			JSONArray jsonArray = customClassUtils.fetchGroupsBasedOnSuggestedState(jsonArray1, false, true);
//			assertTrue(jsonArray.length() > 0);
//			assertEquals(jsonArray.length(), 1);
//			assertTrue(jsonArray.getJSONObject(0).getString(Constants.SUGGESTED_STATE).equals(Constants.REJECTED));
//		} catch (JSONException e) {
//			LOGGER.error("Error while parsing JSON", e);
//			fail();
//		}
//
//	}
//	
//	@Test
//	public void test_showAcceptedGroups3() {
//		InputStream inputStream = CustomClassFilterGroupUnitTests.class.getClassLoader()
//				.getResourceAsStream("customclass1.json");
//
//		CustomClassUtils customClassUtils = new CustomClassUtils();
//		JSONObject jsonObject = new JSONObject();
//
//		try {
//			jsonObject = (JSONObject) org.apache.wink.json4j.JSON.parse(inputStream);
//
//			JSONArray jsonArray1 = jsonObject.getJSONObject(Constants.CUSTOM_CLASS).getJSONArray("ec1481df.64b1b87d.ur86g3dcq.anond2f.rpt0qa.teqfuj424a1245kmvr345");
//			JSONArray jsonArray = customClassUtils.fetchGroupsBasedOnSuggestedState(jsonArray1, true, true);
//			assertTrue(jsonArray.size() > 0);
//			assertEquals(jsonArray.length(), 2);
//			assertTrue(jsonArray.getJSONObject(0).getString(Constants.SUGGESTED_STATE).equals(Constants.ACCEPTED));
//			assertTrue(jsonArray.getJSONObject(1).getString(Constants.SUGGESTED_STATE).equals(Constants.REJECTED));
//		} catch (JSONException e) {
//			LOGGER.error("Error while parsing JSON", e);
//			fail();
//		}
//	}
//	
//	@Test
//	public void test_showAcceptedGroups4() {
//		InputStream inputStream = CustomClassFilterGroupUnitTests.class.getClassLoader()
//				.getResourceAsStream("customclass2.json");
//
//		CustomClassUtils customClassUtils = new CustomClassUtils();
//		JSONObject jsonObject = new JSONObject();
//
//		try {
//			jsonObject = (JSONObject) org.apache.wink.json4j.JSON.parse(inputStream);
//
//			JSONArray jsonArray1 = jsonObject.getJSONObject(Constants.CUSTOM_CLASS).getJSONArray("ec1481df.64b1b87d.ur86g3dcq.anond2f.rpt0qa.teqfuj424a1245kmvr345");
//			JSONArray jsonArray = customClassUtils.fetchGroupsBasedOnSuggestedState(jsonArray1, true, true);
//			assertTrue(jsonArray.size() > 0);
//			assertEquals(jsonArray.length(), 3);
//			assertTrue(jsonArray.getJSONObject(0).getString(Constants.SUGGESTED_STATE).equals(Constants.ACCEPTED));
//			assertTrue(jsonArray.getJSONObject(1).getString(Constants.SUGGESTED_STATE).equals(Constants.REJECTED));
//			assertTrue(jsonArray.getJSONObject(2).getString(Constants.SUGGESTED_STATE).equals(Constants.SUGGESTED));
//		} catch (JSONException e) {
//			LOGGER.error("Error while parsing JSON", e);
//			fail();
//		}
//	}
//	
////	@Test
////	public void test_showAcceptedGroups5() {
////		try {
////	        InputStream inputStream = CustomClassFilterGroupUnitTests.class.getClassLoader()
////                .getResourceAsStream("customclassdemo2.json");
////
////	        CustomClassUtils customClassUtils = new CustomClassUtils();
////	        JSONObject jsonObject = new JSONObject();
////
////			jsonObject = (JSONObject) org.apache.wink.json4j.JSON.parse(inputStream);
////
////			String project_rid = "ec1481df.64b1b87d.bdemnph0b.1g43f9c.0cin9v.abvijdujodj86tiv1duu1";
////			JSONArray jsonArray1 = jsonObject.getJSONObject(Constants.CUSTOM_CLASS).getJSONArray(project_rid);
////			
////			insightsMetaDataStore.delete(Constants.CUSTOM_CLASS, project_rid, null);
////			
////			insightsMetaDataStore.save(Constants.CUSTOM_CLASS, project_rid, new JSONObject().put("custom_classes", jsonArray1));
////			
////			JSONObject result = customClassUtils.getCustomClassJsonForGroup(project_rid, "3c231c02-1e29-4055-88b6-f7dfd247ec06", 90, 0, 2);
////			assertTrue(result.size() > 0);
////			assertEquals(result.getString(Constants.GROUP_ID), "3c231c02-1e29-4055-88b6-f7dfd247ec06");
////			assertEquals(result.getJSONArray(Constants.COLUMNS).size(), 2);
////			assertEquals(result.getInt(Constants.COLUMNS_SIZE), 8);
////			assertEquals(result.getInt(Constants.DATASET_SIZE), 6);
////			assertTrue(!result.getJSONObject(Constants.REFERENCE_COLUMN).isEmpty());
////			assertEquals(result.getJSONObject(Constants.REFERENCE_COLUMN).getString(Constants.COLUMN_NAME), "COL1");
////			
////			insightsMetaDataStore.delete(Constants.CUSTOM_CLASS, project_rid, null);
////		} catch (JSONException | InsightsException e) {
////			LOGGER.error("Error while parsing JSON", e);
////			fail();
////		}
////	}
////	
////	@Test
////	public void test_showAcceptedGroups6() {
////		try {
////	        InputStream inputStream = CustomClassFilterGroupUnitTests.class.getClassLoader()
////                .getResourceAsStream("customclassdemo2.json");
////
////	        IInsightsMetaDataStore insightsMetaDataStore = InsightsMetaDataStore.getInstance();
////	        CustomClassUtils customClassUtils = new CustomClassUtils();
////	        JSONObject jsonObject = new JSONObject();
////
////			jsonObject = (JSONObject) org.apache.wink.json4j.JSON.parse(inputStream);
////
////			String project_rid = "ec1481df.64b1b87d.bdemnph0b.1g43f9c.0cin9v.abvijdujodj86tiv1duu1";
////			JSONArray jsonArray1 = jsonObject.getJSONObject(Constants.CUSTOM_CLASS).getJSONArray(project_rid);
////			
////			insightsMetaDataStore.delete(Constants.CUSTOM_CLASS, project_rid, null);
////			insightsMetaDataStore.save(Constants.CUSTOM_CLASS, project_rid, new JSONObject().put("custom_classes", jsonArray1));
////			
////			JSONObject result = customClassUtils.getCustomClassJsonForGroup(project_rid, "3c231c02-1e29-4055-88b6-f7dfd247ec06", 99999, 0, 2);
////			assertEquals(result.getString(Constants.STATUS_MESSAGE), "threshold is not valid");
////			
////			JSONObject result1 = customClassUtils.getCustomClassJsonForGroup(project_rid, "3c231c02-1e29-4055-88b6-f7dfd247ec01", 90, 0, 2);
////			assertEquals(result1.getString(Constants.STATUS_MESSAGE), "unable to find a group");
////			
////			JSONObject result2 = customClassUtils.getCustomClassJsonForGroup(project_rid, "3c231c02-1e29-4055-88b6-f7dfd247ec06", 90, -1, 2);
////			assertEquals(result2.getJSONArray(Constants.COLUMNS).size(), 5);
////			
////			JSONObject result3 = customClassUtils.getCustomClassJsonForGroup(project_rid, "3c231c02-1e29-4055-88b6-f7dfd247ec06", 75, 0, 0);
////			assertTrue(result3.getJSONArray(Constants.COLUMNS).isEmpty());
////			
////			insightsMetaDataStore.delete(Constants.CUSTOM_CLASS, project_rid, null);
////		} catch (JSONException | InsightsException e) {
////			LOGGER.error("Error while parsing JSON", e);
////			fail();
////		}
////	}
//
//}
