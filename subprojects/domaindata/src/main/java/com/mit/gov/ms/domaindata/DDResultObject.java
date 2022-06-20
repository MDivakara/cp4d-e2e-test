/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.domaindata;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import java.util.ArrayList;
import org.slf4j.LoggerFactory;

import com.mit.gov.ms.common.Constants;

import org.slf4j.Logger;

public class DDResultObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(DDResultObject.class);
    private JSONObject collumnIdentity;
    private JSONArray similarColumnsList = new JSONArray();

//    public JSONObject getColumnCAResult() {
//        return columnCAResult;
//    }
//    
//    public void setColumnCAResult(JSONObject columnCAResult) {
//        this.columnCAResult = columnCAResult;
//    }

    public JSONObject getCollumnIdentity() {
        return collumnIdentity;
    }

    public void setCollumnIdentity(JSONObject collumnIdentity) {
        this.collumnIdentity = collumnIdentity;
    }

    public JSONArray getSimilarColumnsIdentityList() {
        JSONArray ridsJson = new JSONArray();
        for (int i = 0; i < similarColumnsList.size(); i++) {
            try {
                ridsJson.add(similarColumnsList.getJSONObject(i).getJSONObject(Constants.COLUMN_IDENTITY));
            } catch (JSONException e) {
                LOGGER.error("Got Error while getSimilarColumnsIdentityList", e);
            }
        }
        return ridsJson;
    }

    public ArrayList<String> getSimilarColumnsRids() {
        ArrayList<String> rids = new ArrayList<String>();
        for (int i = 0; i < similarColumnsList.size(); i++) {
            try {
                rids.add(similarColumnsList.getJSONObject(i).getString(Constants.COLUMN_RID));
            } catch (JSONException e) {
                LOGGER.error("Got Error while getSimilarColumnsRids", e);
            }
        }
        return rids;
    }

    /*
    Method to add a similar column to the list of similar columns
     */
    public void addToSimilarColumnsList(String colRid, JSONObject columnIdentity) {
        try {
            this.similarColumnsList.add(new JSONObject().put(Constants.COLUMN_RID, colRid)
                    .put(Constants.COLUMN_IDENTITY, columnIdentity));
        } catch (JSONException e) {
            LOGGER.error("Got Error while addToSimilarColumnsList", e);
        }
    }

    public JSONArray getSimilarColumnsList(){
        return similarColumnsList;
    }

    public void clear() {
        similarColumnsList.clear();
    }

    @Override
    public String toString() {
        return similarColumnsList.toString();
    }

}
