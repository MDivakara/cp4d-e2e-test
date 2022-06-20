/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.datarules.functional.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.gov.ms.common.CommonUtils;
import com.mit.gov.ms.common.apriori.NamedItem;

import de.mrapp.apriori.Output;

public class TestAutomationRulesFunctional {

	private final static Logger LOGGER = LoggerFactory.getLogger(TestAutomationRulesFunctional.class);

	//
	public static JSONArray aprioriInputResult = new JSONArray();
	public static JSONObject jsonResponse = new JSONObject();
	public static JSONObject dataRulesInput;
	public static InputStream is = null;
	public static JSONArray suggestedRules = new JSONArray();
	static Map<String, String> termsRIDMap = new HashMap<String, String>();

	@BeforeClass
	public static void test_setup() throws NullPointerException, FileNotFoundException {

		try {
			InputStream is = TestAutomationRulesFunctional.class.getClassLoader()
					.getResourceAsStream("TermRIDMap.json");
			dataRulesInput = (JSONObject) org.apache.wink.json4j.JSON.parse(is);
			@SuppressWarnings("unchecked")
			Iterator<String> keys = dataRulesInput.keys();
			while (keys.hasNext()) {
				String key = keys.next();
				termsRIDMap.put(key, dataRulesInput.getString(key));
			}
			dataRulesInput.clear();
			is = TestAutomationRulesFunctional.class.getClassLoader().getResourceAsStream("FunTestDataRulesInput.json");

			dataRulesInput = (JSONObject) org.apache.wink.json4j.JSON.parse(is);
			assertTrue("unable to read input resource file ", dataRulesInput != null);
		} catch (NullPointerException | JSONException e) {
			LOGGER.error("unable to read input resource file ", e);
			assertTrue("unable to read input resource file", false);
		}

	}

	@AfterClass
	public static void test_cleanup() {
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				LOGGER.warn("failed to close input stream " + e.getMessage());
			}
		}
	}

	@Test
	public void test() {

		// Data Rule suggestion for *The asset* has the term assigned to it.
		try {
			JSONArray aprioriInputResult = dataRulesInput.getJSONArray("AssetHasTerm");
			assertFalse("aprioriInputResult is null", aprioriInputResult.isEmpty());
			assertFalse("aprioriInputResult is null", aprioriInputResult == null);

		} catch (JSONException e) {
			LOGGER.error("JSON Exception", e);

		}

	}
}
