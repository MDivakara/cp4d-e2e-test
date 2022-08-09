///* 
// * Author : Shaik Magdhum Nawaz
// * Email : shaik.nawaz@mastechinfotrellis.com
// * 
// * Mastech InfoTrellis Confidential
// * Copyright InfoTrellis India Pvt. Ltd. 2022
// * The source code for this program is not published. 
// */
//
//package com.mit.gov.ms.datarules.unit;
//
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.wink.json4j.JSONArray;
//import org.apache.wink.json4j.JSONException;
//import org.apache.wink.json4j.JSONObject;
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.slf4j.LoggerFactory;
//
//import com.mit.gov.ms.datarules.Condition;
//
//public class TestMergeDataRulesByAction {
//	private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TestMergeDataRulesByAction.class);
//	// dataRulesUtilObj = new DataRulesUtilAssetTerm();
//
//	public static JSONArray suggestedAutomationRules = new JSONArray();
//	public static InputStream is = null;
//
//	@BeforeClass
//	public static void test_setup() {
//		// load suggested automation rules
//		is = TestMergeDataRulesByAction.class.getClassLoader().getResourceAsStream("mergeRulesInput.json");
//		try {
//			suggestedAutomationRules = (JSONArray) org.apache.wink.json4j.JSON.parse(is);
//		} catch (JSONException e) {
//			LOGGER.error("Error while parsing JSON", e);
//		}
//
//	}
//
//	@Test
//	public void test_MergeRules() {
//
//		assertTrue("unable to add comeple rules ", suggestedAutomationRules.size() == 13);
//		suggestedAutomationRules.clear();
//		List<String> aactions = new ArrayList<String>();
//		assertTrue("failed to merge rules", suggestedAutomationRules.size() == 6);
//		try {
//			for (int i = 0; i < suggestedAutomationRules.size(); i++) {
//
//				assertTrue("Missing condition", suggestedAutomationRules.getJSONObject(i).has("condition_"));
//				assertTrue("Missing status", suggestedAutomationRules.getJSONObject(i).has("status_"));
//				assertTrue("Missing description", suggestedAutomationRules.getJSONObject(i).has("description_"));
//				assertTrue("Missing actions", suggestedAutomationRules.getJSONObject(i).has("actions_"));
//				assertTrue("Missing name", suggestedAutomationRules.getJSONObject(i).has("name_"));
//				// get all actions after merge to check duplicates
//				String action = suggestedAutomationRules.getJSONObject(i).getJSONArray("actions_").getJSONObject(0)
//						.getJSONArray("fields_details").getJSONObject(0).getString("value");
//				aactions.add(action);
//				assertTrue("after merge there are duplicates ",
//						aactions.size() == aactions.stream().distinct().count());
//
//			}
//		} catch (JSONException e) {
//			LOGGER.error("Error while parsing JSON", e);
//		}
//		is = TestMergeDataRulesByAction.class.getClassLoader().getResourceAsStream("mergeRulesInputCompare.json");
//		try {
//			suggestedAutomationRules.clear();
//			JSONObject input = (JSONObject) org.apache.wink.json4j.JSON.parse(is);
//			suggestedAutomationRules = input.getJSONArray("input");
//
//			JSONArray expectedOutput = input.getJSONArray("output");
//			JSONObject expectedOutput1 = input.getJSONObject("output1");
//			JSONArray expectedOutput2 = input.getJSONArray("output2");
//
//
//			Condition conditionobject2 = new Condition();
//			conditionobject2.buildConditionFromJSON(expectedOutput.getJSONObject(0), null, 0);
//
//			Condition conditionobject3 = new Condition();
//			conditionobject3.buildConditionFromJSON(expectedOutput1, null, 0);
//
//			Condition conditionobject4 = new Condition();
//			conditionobject4.buildConditionFromJSON(expectedOutput2.getJSONObject(0), null, 0);
//
//
//		} catch (JSONException e) {
//			LOGGER.error("Error while parsing JSON", e);
//		}
//	}
//
//	@AfterClass
//	public static void test_cleanup() {
//		suggestedAutomationRules.clear();
//		if (is != null) {
//			try {
//				is.close();
//			} catch (IOException e) {
//				LOGGER.error("Error while closing input stream ", e);
//			}
//		}
//	}
//
//}
