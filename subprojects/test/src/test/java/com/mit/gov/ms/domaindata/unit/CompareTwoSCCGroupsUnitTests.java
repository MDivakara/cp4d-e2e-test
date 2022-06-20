/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.domaindata.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.junit.Test;

import com.mit.gov.ms.common.Constants;
import com.mit.gov.ms.domaindata.SCANonMetadataUtils;


public class CompareTwoSCCGroupsUnitTests {

	private static final Logger LOGGER = LoggerFactory.getLogger(CompareTwoSCCGroupsUnitTests.class);
	
	private SCANonMetadataUtils scaNonMetdataUtils= new SCANonMetadataUtils();
	
	@Test
	public void test_compare2DDgroups1() {
		
		InputStream inputStream1 = CompareTwoSCCGroupsUnitTests.class.getClassLoader().getResourceAsStream("customdemo1.json");
		InputStream inputStream2 = CompareTwoSCCGroupsUnitTests.class.getClassLoader().getResourceAsStream("customdemo2.json");
		
		
		JSONObject inputJsonObject1 = new JSONObject();
		JSONObject inputJsonObject2 = new JSONObject();
		try {
		    
			inputJsonObject1 = (JSONObject) org.apache.wink.json4j.JSON.parse(inputStream1);
			inputJsonObject2 = (JSONObject) org.apache.wink.json4j.JSON.parse(inputStream2);
			
			JSONArray jsonArray1 = inputJsonObject1.getJSONObject(Constants.CUSTOM_CLASS).getJSONArray("ec1481df.64b1b87d.ur86g3dcq.anond2f.rpt0qa.teqfuj424a1245kmvr345");
			JSONArray jsonArray2 = inputJsonObject2.getJSONObject(Constants.CUSTOM_CLASS).getJSONArray("ec1481df.64b1b87d.ur86g3dcq.anond2f.rpt0qa.teqfuj424a1245kmvr345");
			
			// check 2 groups, if they can merge
			assertTrue(scaNonMetdataUtils.compareTwoSCCGroups(jsonArray1.getJSONObject(0), jsonArray2.getJSONObject(0) ));
			assertTrue(scaNonMetdataUtils.compareTwoSCCGroups(jsonArray1.getJSONObject(1), jsonArray2.getJSONObject(1) ));
			
			//check negative cases
			assertFalse(scaNonMetdataUtils.compareTwoSCCGroups(jsonArray1.getJSONObject(0), jsonArray2.getJSONObject(1) ));
			assertFalse(scaNonMetdataUtils.compareTwoSCCGroups(jsonArray1.getJSONObject(1), jsonArray2.getJSONObject(0) ));
			
			//merging two groups
			JSONArray jsonArray = scaNonMetdataUtils.mergeTwoSCCGroups(jsonArray1, jsonArray2);
			assertEquals(jsonArray1.size(), jsonArray.size());
			assertEquals(jsonArray2.size(), jsonArray.size());
			assertTrue(jsonArray.getJSONObject(0).getJSONArray(Constants.COLUMNS).size() == jsonArray1.getJSONObject(0).getJSONArray(Constants.COLUMNS).size());
		} catch ( JSONException e1) {
			LOGGER.error("Error while parsing JSON", e1);
			fail();
		}
	}
	
	
	@Test
	public void test_compare2DDgroups2() {
		
		InputStream inputStream1 = CompareTwoSCCGroupsUnitTests.class.getClassLoader().getResourceAsStream("customdemo1.json");
		InputStream inputStream2 = CompareTwoSCCGroupsUnitTests.class.getClassLoader().getResourceAsStream("customdemo4.json");
		
		JSONObject inputJsonObject1 = new JSONObject();
		JSONObject inputJsonObject2 = new JSONObject();
		try {
		    
			inputJsonObject1 = (JSONObject) org.apache.wink.json4j.JSON.parse(inputStream1);
			inputJsonObject2 = (JSONObject) org.apache.wink.json4j.JSON.parse(inputStream2);
			
			JSONArray jsonArray1 = inputJsonObject1.getJSONObject(Constants.CUSTOM_CLASS).getJSONArray("ec1481df.64b1b87d.ur86g3dcq.anond2f.rpt0qa.teqfuj424a1245kmvr345");
			JSONArray jsonArray2 = inputJsonObject2.getJSONObject(Constants.CUSTOM_CLASS).getJSONArray("ec1481df.64b1b87d.ur86g3dcq.anond2f.rpt0qa.teqfuj424a1245kmvr345");
			
			// check 2 groups, if they can merge
			assertTrue(scaNonMetdataUtils.compareTwoSCCGroups(jsonArray1.getJSONObject(0), jsonArray2.getJSONObject(0) ));
			assertTrue(scaNonMetdataUtils.compareTwoSCCGroups(jsonArray1.getJSONObject(1), jsonArray2.getJSONObject(1) ));
			
			//check negative cases 
			assertFalse(scaNonMetdataUtils.compareTwoSCCGroups(jsonArray1.getJSONObject(0), jsonArray2.getJSONObject(1) ));
			assertFalse(scaNonMetdataUtils.compareTwoSCCGroups(jsonArray1.getJSONObject(1), jsonArray2.getJSONObject(0) ));
			
			//merging two groups
			JSONArray jsonArray = scaNonMetdataUtils.mergeTwoSCCGroups(jsonArray1, jsonArray2);
			assertTrue(jsonArray.getJSONObject(0).getJSONArray(Constants.COLUMNS).size() > jsonArray1.getJSONObject(0).getJSONArray(Constants.COLUMNS).size());
		} catch (JSONException e1) {
			LOGGER.error("Error while parsing JSON", e1);
			fail();
		}
	}
	
	@Test
	public void test_compare2DDgroups3() {
		
		InputStream inputStream1 = CompareTwoSCCGroupsUnitTests.class.getClassLoader().getResourceAsStream("customdemo1.json");
		InputStream inputStream2 = CompareTwoSCCGroupsUnitTests.class.getClassLoader().getResourceAsStream("customdemo5.json");
		
		
		JSONObject inputJsonObject1 = new JSONObject();
		JSONObject inputJsonObject2 = new JSONObject();
		try {
		    
			inputJsonObject1 = (JSONObject) org.apache.wink.json4j.JSON.parse(inputStream1);
			inputJsonObject2 = (JSONObject) org.apache.wink.json4j.JSON.parse(inputStream2);
			
			JSONArray jsonArray1 = inputJsonObject1.getJSONObject(Constants.CUSTOM_CLASS).getJSONArray("ec1481df.64b1b87d.ur86g3dcq.anond2f.rpt0qa.teqfuj424a1245kmvr345");
			JSONArray jsonArray2 = inputJsonObject2.getJSONObject(Constants.CUSTOM_CLASS).getJSONArray("ec1481df.64b1b87d.ur86g3dcq.anond2f.rpt0qa.teqfuj424a1245kmvr345");
			
			// check 2 groups, if they can merge
			assertTrue(scaNonMetdataUtils.compareTwoSCCGroups(jsonArray1.getJSONObject(0), jsonArray2.getJSONObject(0) ));
			
			//check negative cases
			assertFalse(scaNonMetdataUtils.compareTwoSCCGroups(jsonArray1.getJSONObject(0), jsonArray2.getJSONObject(1) ));
			assertFalse(scaNonMetdataUtils.compareTwoSCCGroups(jsonArray1.getJSONObject(1), jsonArray2.getJSONObject(0) ));
			
			//merging two groups
			JSONArray jsonArray = scaNonMetdataUtils.mergeTwoSCCGroups(jsonArray1, jsonArray2);
			assertTrue(jsonArray.size() > jsonArray1.size());
		} catch (JSONException e1) {
			LOGGER.error("Error while parsing JSON", e1);
			fail();
		}
	}
	
	@Test
	public void test_compare2DDgroups4() {
		
		InputStream inputStream1 = CompareTwoSCCGroupsUnitTests.class.getClassLoader().getResourceAsStream("customdemo1.json");
		InputStream inputStream2 = CompareTwoSCCGroupsUnitTests.class.getClassLoader().getResourceAsStream("customdemo3.json");
		
		JSONObject inputJsonObject1 = new JSONObject();
		JSONObject inputJsonObject2 = new JSONObject();
		try {
		    
			inputJsonObject1 = (JSONObject) org.apache.wink.json4j.JSON.parse(inputStream1);
			inputJsonObject2 = (JSONObject) org.apache.wink.json4j.JSON.parse(inputStream2);
			
			JSONArray jsonArray1 = inputJsonObject1.getJSONObject(Constants.CUSTOM_CLASS).getJSONArray("ec1481df.64b1b87d.ur86g3dcq.anond2f.rpt0qa.teqfuj424a1245kmvr345");
			JSONArray jsonArray2 = inputJsonObject2.getJSONObject(Constants.CUSTOM_CLASS).getJSONArray("ec1481df.64b1b87d.ur86g3dcq.anond2f.rpt0qa.teqfuj424a1245kmvr345");
			
			// check 2 groups, if can merge
			assertTrue(scaNonMetdataUtils.compareTwoSCCGroups(jsonArray1.getJSONObject(0), jsonArray2.getJSONObject(0) ));
			
			//check negative cases
			assertFalse(scaNonMetdataUtils.compareTwoSCCGroups(jsonArray1.getJSONObject(0), jsonArray2.getJSONObject(1) ));
			assertFalse(scaNonMetdataUtils.compareTwoSCCGroups(jsonArray1.getJSONObject(1), jsonArray2.getJSONObject(0) ));
			
			//merging two groups
			JSONArray jsonArray = scaNonMetdataUtils.mergeTwoSCCGroups(jsonArray1, jsonArray2);
			assertTrue(jsonArray.getJSONObject(0).getJSONArray(Constants.COLUMNS).size() < jsonArray1.getJSONObject(0).getJSONArray(Constants.COLUMNS).size());
		} catch (JSONException e1) {
			LOGGER.error("Error while parsing JSON", e1);
			fail();
		}
	}
	
	/*passing empty jsons
	 * */
	@Test
	public void test_compare2DDgroupsNegativeCases() {
		
		InputStream inputStream1 = CompareTwoSCCGroupsUnitTests.class.getClassLoader().getResourceAsStream("customdemo1.json");
		
		JSONObject inputJsonObject1 = new JSONObject();
		try {
		    
			inputJsonObject1 = (JSONObject) org.apache.wink.json4j.JSON.parse(inputStream1);
			
			JSONArray jsonArray1 = inputJsonObject1.getJSONObject(Constants.CUSTOM_CLASS).getJSONArray("ec1481df.64b1b87d.ur86g3dcq.anond2f.rpt0qa.teqfuj424a1245kmvr345");
			
			assertFalse(scaNonMetdataUtils.compareTwoSCCGroups(jsonArray1.getJSONObject(0), new JSONObject() ));
			assertFalse(scaNonMetdataUtils.compareTwoSCCGroups(jsonArray1.getJSONObject(1), new JSONObject() ));
			assertFalse(scaNonMetdataUtils.compareTwoSCCGroups(jsonArray1.getJSONObject(1), null ));
			assertFalse(scaNonMetdataUtils.compareTwoSCCGroups(null, null ));
			
		} catch (JSONException  e1) {
			LOGGER.error("Error while parsing JSON", e1);
			fail();
		}
		
	}
	
}
