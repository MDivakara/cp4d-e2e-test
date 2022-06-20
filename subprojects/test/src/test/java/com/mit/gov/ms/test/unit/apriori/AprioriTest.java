/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.test.unit.apriori;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mit.gov.ms.common.CommonUtils;
import com.mit.gov.ms.common.apriori.NamedItem;

import de.mrapp.apriori.AssociationRule;
import de.mrapp.apriori.FrequentItemSets;
import de.mrapp.apriori.ItemSet;
import de.mrapp.apriori.Output;
import de.mrapp.apriori.RuleSet;

public class AprioriTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(AprioriTest.class);
	
	private static JSONArray dataRules = new JSONArray();
	private static JSONArray numericTrans = new JSONArray();
	private static JSONArray stringTrans = new JSONArray();
	private static JSONArray uniqueTran = new JSONArray();
	private static JSONArray singleTran = new JSONArray();	

	@BeforeClass
    public static void test_setup() throws NullPointerException, FileNotFoundException {
		// prepare input for apriori Ex: [[1,2,3], [2,3,4], [1,3,4], [1,3,5],[1,5,3],[3,5]]
		try {
			InputStream is = AprioriTest.class.getClassLoader().getResourceAsStream("aprioriInput.json");
			JSONObject input = (JSONObject)org.apache.wink.json4j.JSON.parse(is);
		    
 			stringTrans = input.getJSONArray("input1");
 			numericTrans = input.getJSONArray("input2");
		    dataRules = input.getJSONArray("input3");
		    uniqueTran = input.getJSONArray("input4");
		    singleTran = input.getJSONArray("input5");
		    
			
		} catch (JSONException e) {
			LOGGER.error("Error while parsing JSON", e);
		}
		
	}
	
	@AfterClass
    public static void test_cleanup() {
		//clear input data
		stringTrans.clear();;
		dataRules.clear();
		numericTrans.clear();
		uniqueTran.clear();
		singleTran.clear();
	}
	
	//@SuppressWarnings("unchecked")
	@Test
    public void test_string() {
		//TODO
		
		//Validating string transactions 
		
		
		//Validating numeric transactions
		//Validating unique transaction
		//validating single transaction
		
		
	}
	
	@Test
    public void test_dataRules() {
		
		//TODO
		
	}
	
	

}
