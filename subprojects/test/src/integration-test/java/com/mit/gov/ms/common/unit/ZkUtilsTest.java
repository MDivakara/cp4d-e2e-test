package com.mit.gov.ms.common.unit;
/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

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
//
//
//public class ZkUtilsTest {
//	
//	private static final Logger LOGGER = LoggerFactory.getLogger(CloudantUtilsTest.class);
//
//	static InsightsMetaDataStore insightsMetaDataStore = null;
//	static String docID = "ec1481df.64b1b87d.bdemnph0b.1g43f9c.0cin9v.abvijdujodj86tiv1duu1";
//	
//	@BeforeClass
//	public static void setUp() throws Exception {
//		// skip the test if db is not zookeeper
//		assumeTrue(InsightsConfiguration.getInstance().getInsightsMetaDataStoreType().toString().equals("ZOOKEEPER"));
//	}
//
//	@AfterClass
//	public static void tearDown() throws Exception {
//
//	}
//		
//	/**
//	 * test to validate persist and retrieve results from zookeeper db 
//	 * @throws InsightsException 
//	 */
//	@Test
//	public void test_getConstraintsByID() throws InsightsException {
//		JSONObject jsonObject = new JSONObject();
//		try {
//			jsonObject.put("name1", "insights1");
//			jsonObject.put("name2", "insights2");
//			jsonObject.put(Constants.FILE_ID, docID);
//			
//			InsightsMetaDataStore.getInstance().addConstraintsByID(Constants.ADMIN_DB, jsonObject);
//			
//			JSONObject result = InsightsMetaDataStore.getInstance().getConstraintsByID(Constants.ADMIN_DB, docID);
//			
//			assertTrue(result.containsKey("name1"));
//			assertTrue(result.containsKey("name2"));
//			assertEquals(docID, result.getString(Constants.FILE_ID));;
//			
//			
//			InsightsMetaDataStore.getInstance().delete(Constants.ADMIN_DB, docID, null);
//			
//		} catch (JSONException e) {
//			LOGGER.error("Error while parsing JSON", e);
//			assertTrue(false);
//		}
//	}
//
//}
