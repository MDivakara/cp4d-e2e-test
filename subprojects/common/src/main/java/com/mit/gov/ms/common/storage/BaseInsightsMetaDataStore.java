/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.storage;

import java.io.InputStream;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.mit.gov.ms.common.Constants;
import com.mit.gov.ms.common.InsightsException;
import com.mit.gov.ms.common.interfaces.IInsightsMetaDataStore;
import com.mit.gov.ms.store.StorageConstants;
import com.mit.gov.ms.store.StorageException;
import com.mit.gov.ms.store.api.DBResultSetFormatter;
import com.mit.gov.ms.store.api.DBStore;

/**
 * @author Shaik.Nawaz
 *
 */
public abstract class BaseInsightsMetaDataStore implements IInsightsMetaDataStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseInsightsMetaDataStore.class);
    
    protected DBStore dbStore = null;
    protected DBResultSetFormatter<?> resultSetFormatter = null;
    
    public BaseInsightsMetaDataStore(DBStore dbStore, DBResultSetFormatter<?> resultSetFormatter) {
        this.dbStore = dbStore;
        this.resultSetFormatter = resultSetFormatter;
    }

    public JSONObject getDBStatus(String dbName) throws StorageException {
        JSONObject status = this.dbStore.getDBStatus(dbName);
        LOGGER.info(status.toString());
        return status;
    }

    public void initSchema(String schema_name, int newVersion) throws InsightsException {
        try {
            dbStore.initSchema(schema_name, newVersion);
        } catch (StorageException e) {
            LOGGER.error("Error while initSchema", e);
            throw new InsightsException("Error while initSchema - " + e.toString(), e);
        }
    }

    public JSONObject save(String dbStoreName, String dbItemId, JSONObject jsonData) throws InsightsException {
        try {
            jsonData.put(StorageConstants.NODE_TYPE, dbStoreName);
            jsonData.put(StorageConstants.SUB_TYPE, dbItemId);
            return this.dbStore.insert(jsonData);
        } catch (StorageException | JSONException e) {
            LOGGER.error("Error while save", e);
            throw new InsightsException(e.toString(), e);
        }
    }

    public JSONObject update(String dbStoreName, String dbItemId, JSONObject jsonData, JSONObject updateCondition) throws InsightsException {
        try {
            jsonData.put(StorageConstants.NODE_TYPE, dbStoreName);
            jsonData.put(StorageConstants.SUB_TYPE, dbItemId);
            return this.dbStore.update(jsonData);
        } catch (StorageException | JSONException e) {
            LOGGER.error("Error while save", e);
            throw new InsightsException("Error while save - " + e.toString(), e);
        }
    }

    public JSONObject get(String dbStoreName, String dbItemId, String[] formats, JSONArray param_array, String resultFormatId) throws InsightsException {
        JSONObject executeJson = new JSONObject();
        try {
            executeJson.put(StorageConstants.NODE_TYPE, dbStoreName);
            if (dbItemId != null) {
                executeJson.put(StorageConstants.SUB_TYPE, dbItemId);
            }
            return this.dbStore.executeQuery(executeJson, this.resultSetFormatter);
        } catch (JSONException | StorageException e) {
            LOGGER.error("Error while get", e);
            throw new InsightsException("Error while get - " + e.toString(), e);
        }
    }

    @Override
    public JSONObject get(String dbStoreName, String dbQueryIdentifier, String[] formats, JSONArray param_array,
        String resultFormatId, String orderBy, int offSet, int limit)
        throws InsightsException {
        return get(dbStoreName, dbQueryIdentifier, formats, param_array, resultFormatId);
    }

    public JSONObject delete(String dbStoreName, String dbItemId, JSONObject deleteCondition) throws InsightsException {
        JSONObject deleteJson = new JSONObject();
        try {
            deleteJson.put(StorageConstants.NODE_TYPE, dbStoreName);
            if (dbItemId != null) {
                deleteJson.put(StorageConstants.SUB_TYPE, dbItemId);
            }
            return dbStore.delete(deleteJson);
        } catch (JSONException | StorageException e) {
            LOGGER.error("Error while delete", e);
            throw new InsightsException("Error while delete - " + e.toString(), e);
        }
    }

    public void deleteNotInList(String dbStoreName, JSONArray workspaceInfo) throws InsightsException {
        try {
            JSONObject getSubTypes = get(dbStoreName, null, null, null, null);
            JSONArray subtypes = new JSONArray(getSubTypes.getJSONObject(dbStoreName).keySet());
            JSONArray workspaceRIDs = new JSONArray();
            for (int i = 0; i < workspaceInfo.length(); i++) {
                workspaceRIDs.add(workspaceInfo.getJSONObject(i).getString(Constants.WORKSPACE_RID));
            }
            for (int i = 0; i < subtypes.length(); i++) {
                if (!workspaceRIDs.contains(subtypes.getString(i))) {
                    delete(dbStoreName, subtypes.getString(i), null);
                }
            }
        } catch (JSONException e) {
            LOGGER.error("Error while deleteNotInList", e);
            throw new InsightsException("Error while deleteNotInList - " + e.toString(), e);
        }
    }

    public void addConstraintsByID(String dbStoreName, JSONObject result) throws InsightsException {
        try {
            String assetID = result.getString(Constants.TABLE_RID);
            result.remove(Constants.TABLE_RID);
            JSONObject existingConstraint = get(dbStoreName, assetID, null, null, null);
            if (existingConstraint == null || !existingConstraint.has(dbStoreName) || !existingConstraint.getJSONObject(dbStoreName).has(assetID)) {
                save(dbStoreName, assetID, result);
            } else {
                update(dbStoreName, assetID, result, null);
            }
            result.put(Constants.TABLE_RID, assetID);
        } catch (JSONException e) {
            LOGGER.error("Error while addConstraintsByID", e);
            throw new InsightsException("Error while addConstraintsByID - " + e.toString(), e);
        }
    }

    @Override
    public JSONObject getConstraintsByID(String dbStoreName, String assetID) throws InsightsException {
        JSONObject result = new JSONObject();
        try {
            if(assetID==null) {
                LOGGER.warn("Table RID should not be null");
                return null;
            }
            result = get(Constants.ADMIN_DB, assetID, null, null, null);
            if(result==null || result.isEmpty()) {
                LOGGER.warn("Unable to find model for given table RID: "+assetID);
                return null;
            }
            result.put(Constants.TABLE_RID, assetID);
        } catch (JSONException e) {
            LOGGER.error("Error while getConstraintsByID", e);
            throw new InsightsException("Error while getConstraintsByID - " + e.toString(), e);
        }
        return result;
    }
    
    @Override
    public InputStream getInputStream(String dbStoreName, String dbQueryIdentifier, String[] formats,
            JSONArray param_array, String resultFormatId) throws InsightsException {
        // TODO Auto-generated method stub
        return null;
    }


}
