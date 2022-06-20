/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.datarules;

import org.apache.wink.json4j.JSONObject;

public class DataRulesResultCache {

    // Keeping this static as the datarules results are not written to Database
    private static JSONObject dataRulesJson = new JSONObject();

    /**
     * @param dataRulesJson the dataRulesJson to set
     */
    public void setDataRulesJson(JSONObject dataRulesJson) {
        DataRulesResultCache.dataRulesJson = dataRulesJson;
    }

    public JSONObject getDataRulesJson() {
    	return dataRulesJson;
    }

}
