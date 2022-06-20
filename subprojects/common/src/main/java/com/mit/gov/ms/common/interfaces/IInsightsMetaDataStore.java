/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.interfaces;

import java.io.InputStream;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONObject;

import com.mit.gov.ms.common.InsightsException;

/**
 * @author Shaik.Nawaz
 *
 */
public interface IInsightsMetaDataStore extends ICatalogsMetaDataStore {

    void initSchema(String schema_name, int newVersion) throws InsightsException;

    JSONObject save(String dbStoreName, String dbItemId, JSONObject jsonData) throws InsightsException;

    JSONObject update(String dbStoreName, String dbItemId, JSONObject jsonData, JSONObject updateCondition)
            throws InsightsException;

    JSONObject delete(String dbStoreName, String dbItemId, JSONObject deleteCondition) throws InsightsException;

    void deleteNotInList(String dbStoreName, JSONArray workspaceInfo) throws InsightsException;

    void addConstraintsByID(String dbStoreName, JSONObject result) throws InsightsException;

    JSONObject getConstraintsByID(String dbStoreName, String assetID) throws InsightsException;

    InputStream getInputStream(String dbStoreName, String dbQueryIdentifier, String[] formats, JSONArray param_array,
            String resultFormatId) throws InsightsException;
}
