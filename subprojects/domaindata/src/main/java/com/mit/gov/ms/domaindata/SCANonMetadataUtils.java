/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.domaindata;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;

import com.mit.gov.ms.common.Constants;

import org.slf4j.Logger;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

public class SCANonMetadataUtils {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SCANonMetadataUtils.class);
    /*
     * error codes : return true : partial match, percentOfMatches > 10% return
     * false : no match
     */
    public boolean compareTwoSCCGroups(JSONObject jsonObject1, JSONObject jsonObject2) {

        if (jsonObject1 == null || jsonObject1.isEmpty() || jsonObject2 == null || jsonObject2.isEmpty()) {
            return false;
        }
        List<String> listOfRidsGroup1 = new ArrayList<String>();
        try {
            JSONArray domainDataJsonArray1 = jsonObject1.getJSONArray(Constants.COLUMNS);
            JSONArray domainDataJsonArray2 = jsonObject2.getJSONArray(Constants.COLUMNS);
            for (int i = 0; i < domainDataJsonArray1.length(); i++) {
                listOfRidsGroup1.add(domainDataJsonArray1.getJSONObject(i).getString(Constants.COLUMN_RID));
            }

            int numOfMatches = 0;
            for (int i = 0; i < domainDataJsonArray2.length(); i++) {
                if (listOfRidsGroup1.contains(domainDataJsonArray2.getJSONObject(i).getString(Constants.COLUMN_RID))) {
                    numOfMatches++;
                }
            }

            double percentOfMatches = numOfMatches * (1.0) / listOfRidsGroup1.size();

            // partial match greater than 10% will true for merging two groups
            if (percentOfMatches > 0.1) {
                return true;
            }

        } catch (JSONException e) {
            LOGGER.error("Error while compareTwoDDGroups", e);
        }
        return false;
    }

    public JSONArray mergeTwoSCCGroups(JSONArray jsonArray1, JSONArray jsonArray2) {
        JSONArray jsonArrayResults = new JSONArray();

        try {
            for (int i = 0; i < jsonArray2.length(); i++) {
                JSONObject newAnalysedJsonObject = jsonArray2.getJSONObject(i);
                boolean flag = false;
                for (int j = 0; j < jsonArray1.length(); j++) {
                    JSONObject existinJsonObject = jsonArray1.getJSONObject(j);
                    // invoke comparison only if, suggested state != accepted
                    if (!existinJsonObject.getString(Constants.STATE).trim().equals(Constants.ACCEPTED)) {

                        boolean ret_val = compareTwoSCCGroups(newAnalysedJsonObject, existinJsonObject);
                        if (ret_val) {
                            // restoring REJECTED STATE for
                            if (existinJsonObject.getString(Constants.STATE).trim().equals(Constants.REJECTED)) {
                                newAnalysedJsonObject.put(Constants.STATE, Constants.REJECTED);
                            }
                            flag = true;
                            jsonArrayResults.add(newAnalysedJsonObject);
                            
                            //mark already_added field to a group, to check if existing json object is added or not
                            existinJsonObject.put(Constants.IS_ADDED, true);
                            break; // as we already find the group
                        }
                    }
                }
                // a new group not matching with any existing group
                if (!flag) {
                    jsonArrayResults.add(newAnalysedJsonObject);
                }
            }
            
            for (int i = 0; i < jsonArray1.length(); i++) {
                JSONObject existingJSONObject = jsonArray1.getJSONObject(i);

                // check if existing json is missing in final json array
                if (!existingJSONObject.has(Constants.IS_ADDED)) {
                    jsonArrayResults.add(existingJSONObject);
                    // remove the flag is_added
                    existingJSONObject.remove(Constants.IS_ADDED);
                }
            }
            
        } catch (JSONException e) {
            LOGGER.error("Error while mergeTwoSCCGroups");
        }
        return jsonArrayResults;
    }

}
