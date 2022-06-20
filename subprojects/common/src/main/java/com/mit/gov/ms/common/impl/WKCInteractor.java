/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.impl;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.mit.gov.ms.common.InsightsException;
import com.mit.gov.ms.common.interfaces.CatalogInteractor;

public class WKCInteractor implements CatalogInteractor{

	@Override
	public JSONObject getConnDetailsOfDataAsset(String assetId) throws JSONException, InsightsException {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public JSONArray getColumnAnalysisResults(String projectRid) {
        // TODO Auto-generated method stub
        return null;
    }

}
