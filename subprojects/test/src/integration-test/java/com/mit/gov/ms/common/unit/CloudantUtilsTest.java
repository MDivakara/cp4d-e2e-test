package com.mit.gov.ms.common.unit;
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
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//import static org.junit.Assume.assumeTrue;
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

//public class CloudantUtilsTest {
//	private static final Logger LOGGER = LoggerFactory.getLogger(CloudantUtilsTest.class);
//
//	static InsightsMetaDataStore insightsMetaDataStore = null;
//	static String docID = "ec1481df.64b1b87d.bdemnph0b.1g43f9c.0cin9v.abvijdujodj86tiv1duu1";
//	
//	@BeforeClass
//	public static void setUp() throws Exception {
//		// skip the test if db is not cloudant
//		assumeTrue(InsightsConfiguration.getInstance().getInsightsMetaDataStoreType().toString().equals("CLOUDANT"));
//	}
//
//	@AfterClass
//	public static void tearDown() throws Exception {
//
//	}
//		
//	/**
//	 * test to validate persist and retrieve results from cloudant db 
//	 * when data is JSONArray
//	 * @throws InsightsException 
//	 */
//	@Test
//	public void test_toPersistAndRetrieve1() throws InsightsException {
//		String projectID = "ec1481df.64b1b87d.bdemnph0b.1g43f9c.0cin9v.abvijdujodj86tiv1duu1";
//		InputStream inputStream = CloudantUtilsTest.class.getClassLoader()
//				.getResourceAsStream("customclassdemo1.json");
//
//		JSONObject jsonObject = new JSONObject();
//
//		try {
//			jsonObject = (JSONObject) org.apache.wink.json4j.JSON.parse(inputStream);
//			
//			
//			JSONArray jsonArray1 = jsonObject.getJSONObject(Constants.CUSTOM_CLASS).getJSONArray(projectID);
//			
//			InsightsMetaDataStore.getInstance().save("insights", projectID, new JSONObject().put("custom_classes", jsonArray1));
//			
//			JSONObject result = InsightsMetaDataStore.getInstance().get("insights", projectID, null, null, null);
//			
//			assertTrue(result.containsKey("insights"));
//			
//			JSONObject jsonObject2 = result.getJSONObject("insights");
//			
//			assertTrue(jsonObject2.containsKey(projectID));
//			
//			InsightsMetaDataStore.getInstance().delete("insights", docID, null);
//			
//		} catch (JSONException e) {
//			LOGGER.error("Error while parsing JSON", e);
//		}
//	}
//	
//	/**
//	 * test to validate persist and retrieve results from cloudant db
//	 * when data is JSONObject 
//	 * @throws InsightsException 
//	 */
//	@Test
//	public void test_toPersistAndRetrieve2() throws InsightsException {
//		
//		JSONObject jsonObject = new JSONObject();
//		try {
//			jsonObject.put("name1", "insights1");
//			jsonObject.put("name2", "insights2");
//			
//			InsightsMetaDataStore.getInstance().save("insights", docID, jsonObject);
//			
//			JSONObject result = InsightsMetaDataStore.getInstance().get("insights", docID, null, null, null);
//			
//			assertTrue(result.containsKey("insights"));
//			
//			JSONObject jsonObject2 = result.getJSONObject("insights");
//			
//			assertTrue(jsonObject2.containsKey(docID));
//			
//			InsightsMetaDataStore.getInstance().delete("insights", docID, null);
//			
//		} catch (JSONException e) {
//			LOGGER.error("Error while parsing JSON", e);
//		}
//	}
//	
//	/**
//	 * test to validate persist and retrieve results from cloudant db
//	 * when passing negative / NULL values 
//	 * @throws InsightsException 
//	 */
//	@Test
//	public void test_toPersistAndRetrieve3() throws InsightsException {
//		
//		JSONObject jsonObject = new JSONObject();
//		try {
//			jsonObject.put("name1", "insights1");
//			jsonObject.put("name2", "insights2");
//			
//			InsightsMetaDataStore.getInstance().save("insights", "", null);
//			
//			JSONObject result = InsightsMetaDataStore.getInstance().get("insights", docID, null, null, null);
//			
//			assertTrue(result.getJSONObject("insights").isEmpty());
//		} catch (JSONException e) {
//			LOGGER.error("Error while parsing JSON", e);
//		}
//	}
//	
//	/**
//	 * test to validate persist and update results from cloudant db
//	 * when data is JSONObject 
//	 * @throws InsightsException 
//	 */
//	@Test
//	public void test_toPersistAndUpdate() throws InsightsException {
//		String docID = "ec1481df.64b1b87d.bdemnph0b.1g43f9c.0cin9v.abvijdujodj86tiv1duu1";
//		
//		JSONObject jsonObject = new JSONObject();
//		try {
//			jsonObject.put("name1", "insights1");
//			jsonObject.put("name2", "insights2");
//			
//			InsightsMetaDataStore.getInstance().save("insights", docID, jsonObject);
//			
//			JSONObject result = InsightsMetaDataStore.getInstance().get("insights", docID, null, null, null).getJSONObject("insights").getJSONObject(docID);
//			result.put("name3", "insights3");
//			result.put("name2", "insights5");
//			InsightsMetaDataStore.getInstance().save("insights", docID, result);
//			
//			
//			JSONObject result1 = InsightsMetaDataStore.getInstance().get("insights", docID, null, null, null);
//			
//			JSONObject jsonObject3 = result1.getJSONObject("insights").getJSONObject(docID);
//			
//			assertEquals(jsonObject3.size(), 3);
//			
//			assertTrue(result1.containsKey("insights"));
//			
//			JSONObject jsonObject2 = result1.getJSONObject("insights");
//			
//			assertTrue(jsonObject2.containsKey(docID));
//			
//			InsightsMetaDataStore.getInstance().delete("insights", docID, null);
//			
//		} catch (JSONException e) {
//			LOGGER.error("Error while parsing JSON", e);
//		}
//	}
//	
//	/**
//	 * test to validate delete document in a given cloudant db
//	 * when data is JSONObject 
//	 * @throws InsightsException 
//	 */
//	@Test
//	public void test_toDelete() throws InsightsException {
//		
//		JSONObject jsonObject = new JSONObject();
//		try {
//			jsonObject.put("name1", "insights1");
//			jsonObject.put("name2", "insights2");
//			
//			InsightsMetaDataStore.getInstance().save("insights", docID, jsonObject);
//			
//			InsightsMetaDataStore.getInstance().delete("insights", docID, null);
//			
//			JSONObject result = InsightsMetaDataStore.getInstance().get("insights", docID, null, null, null);
//			
//			assertTrue(result.containsKey("insights"));
//			assertTrue(result.getJSONObject("insights").isEmpty());
//			
//			InsightsMetaDataStore.getInstance().delete("insights", docID, null);
//			
//		} catch (JSONException e) {
//			LOGGER.error("Error while parsing JSON", e);
//		}
//	}
//	
//	/**
//	 * test to delete all documents which are not present in a given doc list
//	 * when data is JSONObject 
//	 * @throws InsightsException 
//	 */
//	@Test
//	public void test_todeleteNotInList() throws InsightsException {
//		
//		JSONObject jsonObject = new JSONObject();
//		try {
//			jsonObject.put("name1", "insights1");
//			jsonObject.put("name2", "insights2");
//			
//			InsightsMetaDataStore.getInstance().save("insights", docID, jsonObject);
//			
//			InsightsMetaDataStore.getInstance().save("insights", "doc12345", jsonObject);
//			
//			InsightsMetaDataStore.getInstance().save("insights", "doc1234567", jsonObject);
//			
//			
//			//fetch all available docs
//			JSONObject result = InsightsMetaDataStore.getInstance().get("insights", "doc12345", null, null, null);
//			assertTrue(!result.getJSONObject("insights").isEmpty());
//			
//			JSONObject result1 = InsightsMetaDataStore.getInstance().get("insights", "doc1234567", null, null, null);
//			assertTrue(!result1.getJSONObject("insights").isEmpty());
//			
//			JSONObject result2 = InsightsMetaDataStore.getInstance().get("insights", docID, null, null, null);
//			assertTrue(!result2.getJSONObject("insights").isEmpty());
//			
//			JSONObject jsonObject2 = new JSONObject();
//			
//			jsonObject2.put(Constants.WORKSPACE_RID, docID);
//			jsonObject2.put(Constants.WORKSPACE, "InsightsService");
//			
//			JSONArray workspaceInfo = new JSONArray();
//			workspaceInfo.add(jsonObject2);
//			
//			//delete which is not in list, putting docID and all others will be deleted
//			InsightsMetaDataStore.getInstance().deleteNotInList("insights", workspaceInfo);;
//			
//			JSONObject result3 = InsightsMetaDataStore.getInstance().get("insights", "doc1234567", null, null, null);
//			assertTrue(result3.getJSONObject("insights").isEmpty());
//			
//			JSONObject result4 = InsightsMetaDataStore.getInstance().get("insights", "doc12345", null, null, null);
//			assertTrue(result4.getJSONObject("insights").isEmpty());
//			
//			InsightsMetaDataStore.getInstance().delete("insights", docID, null);
//			
//		} catch (JSONException e) {
//			LOGGER.error("Error while parsing JSON", e);
//		}
//	}
//	
//}
