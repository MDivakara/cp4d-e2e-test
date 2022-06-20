/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.interfaces;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.mit.gov.ms.common.InsightsException;

/**
 * This class connects to catalog's meta data source and retrieves information
 * related to assets from the meta data sore.
 */
public interface CatalogInteractor {

    /**
     * This methods takes asset id and returns the connection details corresponding
     * to that asset.
     * 
     * @param assetId
     *            This is the id of the asset
     * @return JSONObject which contains connection details(host, port, user-name,
     *         password, etc) for a given tableRID
     * @throws InsightsException
     * @throws JSONException
     */
    JSONObject getConnDetailsOfDataAsset(String assetId) throws JSONException, InsightsException;

    /**
     * @param projectRid
     *            : ID of a project/workspace/catalog.
     * @param runOnNonClassifiedOnly
     *            : either run similar column analysis on NoClassDetected or for all
     *            data class.
     * @return JSONArray containing columnAnalysisResults(in JSONObject) for a
     *         respective columns.
     * @throws InsightsException 
     */
    JSONArray getColumnAnalysisResults(String projectRid) throws InsightsException;

}
