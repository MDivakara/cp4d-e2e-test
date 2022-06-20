/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.refdata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.slf4j.LoggerFactory;

import com.mit.gov.ms.common.Constants;

import org.slf4j.Logger;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;


public class ReferenceDataDetector {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReferenceDataDetector.class);
    private HashMap<String, CAResultObject> allColumnsCAResultObjectMap = new HashMap<String, CAResultObject>();

   
    public void reset() {
        allColumnsCAResultObjectMap.clear();
    }

	@SuppressWarnings("unchecked")
	public JSONArray fetchResult() throws JSONException {

        // create filter list to avoid reevaluating the rid as by rule of transitivity we have already grouped them
        List<String> processedRids = new ArrayList<String>();
        
        JSONArray refDataGroups = new JSONArray();

        //sort the hashmap
        Map<String, CAResultObject> map = new TreeMap<String, CAResultObject>(allColumnsCAResultObjectMap);
        Set<Map.Entry<String, CAResultObject>> mapEntries = map.entrySet();

        for (Map.Entry<String, CAResultObject> entry : mapEntries) {
            CAResultObject caResultObject = entry.getValue();

            if (!caResultObject.getSimilarColumnsRids().isEmpty() && !processedRids.contains(entry.getKey())) {
                JSONArray columnIdentities = new JSONArray();
                JSONArray currSimilarColumnsIdentityList = caResultObject.getSimilarColumnsIdentityList();
                ArrayList<String> currSimilarColumnsRids = caResultObject.getSimilarColumnsRids();
                addUniqueColumnIdentities(columnIdentities, currSimilarColumnsIdentityList);
                processedRids.addAll(currSimilarColumnsRids);

                for (int i=0; i< currSimilarColumnsIdentityList.size(); i++){
                    try {
                        String col_rid = currSimilarColumnsIdentityList.getJSONObject(i).getString(Constants.COLUMN_RID);
                        if (allColumnsCAResultObjectMap.containsKey(col_rid)) {
                            addUniqueColumnIdentities(columnIdentities,allColumnsCAResultObjectMap.get(col_rid).getSimilarColumnsIdentityList());
                        }
                    } catch (JSONException e) {
                        LOGGER.error("Got Error while fetchResult", e);
                    }
                }
                addUniqueColumnIdentities(columnIdentities,caResultObject.getCollumnIdentity());
                refDataGroups.add(columnIdentities);
            }
        }
        
        try {
	        Collections.sort(refDataGroups, new RefJsonArrayComparator());
        } catch(Exception exp ) {
        		System.out.println(exp);
        		LOGGER.error("Got Error while fetchResult", exp);
        }
        
        return refDataGroups;
    }
	
	private static class RefJsonArrayComparator implements Comparator<JSONArray>, Serializable {

        private static final long serialVersionUID = 1L;

        @Override
        public int compare(JSONArray s1, JSONArray s2) {
            if (s1.length() < s2.length())
                return 1;
            else if (s1.length() > s2.length())
                return -1;
            else return 0;
        }
	    
	}
    
    private JSONArray addUniqueColumnIdentities(JSONArray target, JSONArray source) {

        try {
            for (int i = 0; i < source.size(); i++) {
                boolean found = false;
                for (int j = 0; j < target.size(); j++) {
                    if (target.getJSONObject(j).getString(Constants.COLUMN_RID)
                            .equals(source.getJSONObject(i).getString(Constants.COLUMN_RID))) {
                        found = true;
                        //System.out.println("dup found");
                        break;
                    }
                }
                if (!found) {
                    target.add(source.getJSONObject(i));
                }
            }
        } catch (JSONException e) {
            LOGGER.error("Got Error while addUniqueColumnIdentities", e);
        }
        return target;
    }
    
    private JSONArray addUniqueColumnIdentities(JSONArray target, JSONObject source) {
        try {
            boolean found = false;
            for (int j = 0; j < target.size(); j++) {
                if (target.getJSONObject(j).getString(Constants.COLUMN_RID)
                        .equals(source.getString(Constants.COLUMN_RID))) {
                    //System.out.println("dup found 2");
                    found = true;
                    break;
                }
            }
            if (!found) {
                target.add(source);
            }
        } catch (JSONException e) {
            LOGGER.error("Got Error while addUniqueColumnIdentities", e);
        }
        return target;
    }
}
