/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.refdata;

import java.util.ArrayList;
import org.slf4j.LoggerFactory;

import com.mit.gov.ms.common.Constants;

import org.slf4j.Logger;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

public class CAResultObject {
    private static final Logger LOGGER = LoggerFactory.getLogger(CAResultObject.class);
    private JSONObject collumnIdentity;
    private JSONArray similarColumnsList = new JSONArray();


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

    public void addToSimilarColumnsList(String colRid, JSONObject columnIdentity) {
        try {
            this.similarColumnsList.add(new JSONObject().put(Constants.COLUMN_RID, colRid).put(Constants.COLUMN_IDENTITY, columnIdentity));
        } catch (JSONException e) {
            LOGGER.error("Got Error while addToSimilarColumnsList", e);
        }
    }

    public void clear() {
        similarColumnsList.clear();
    }

    @Override
    public String toString() {
        return similarColumnsList.toString();
    }
}
