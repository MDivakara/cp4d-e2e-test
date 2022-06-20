/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.store.db.cloudant;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.mit.gov.ms.store.StorageConstants;
import com.mit.gov.ms.store.StorageException;
import com.mit.gov.ms.store.StorageType;
import com.mit.gov.ms.store.StorageUtils;
import com.mit.gov.ms.store.api.DBResultSetFormatter;
import com.mit.gov.ms.store.api.DBStore;

/**
 * @author Shaik.Nawaz
 *
 */
public class CloudantDBStore extends DBStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudantDBStore.class);

    private CloudantUtils cloudantUtils = null;

    public CloudantDBStore(String cloudantAccount, String cloudantUser, String cloudantPassword) {
        super(StorageType.CLOUDANT.name());
        cloudantUtils = new CloudantUtils(cloudantAccount, cloudantUser, cloudantPassword);
    }

    @Override
    public JSONObject getDBStatus(String dbName) throws StorageException {
        try {
            JSONObject status = new JSONObject();
            try {
                boolean dbstatus = cloudantUtils.checkStatus(dbName);
                status.put(StorageConstants.DB_TYPE, StorageType.CLOUDANT);
                status
                    .put(StorageConstants.DEPENDENCY_STATUS, dbstatus ? StorageConstants.OK : StorageConstants.NOT_OK);
            } catch (JSONException e) {
                status.put(StorageConstants.ERROR, e.getMessage());
                status.put(StorageConstants.ERROR_STACK, Arrays.toString(e.getStackTrace()));
                LOGGER.error("Error while getDBStatus", e);
            }
            return status;
        } catch (JSONException | StorageException e) {
            throw new StorageException("Error while getDBStatus", e);
        }

    }

    @Override
    public String beginTransaction() throws StorageException {
        return StorageUtils.getNewUUID();
    }

    @Override
    public void commitTransaction(String connectionId) throws StorageException {
        return;
    }

    @Override
    public void rollBackTransaction(String connectionId) throws StorageException {
        return;
    }

    @Override
    public void initSchema(String schema_name, int newVersion) throws StorageException {
        return;
    }

    @Override
    public JSONObject insert(JSONObject insertJson) throws StorageException {
        try {
            String dbName, assetID = null;
            JSONObject result = new JSONObject(insertJson);
            if (insertJson.containsKey(StorageConstants.DB_NAME)) {
                dbName = insertJson.getString(StorageConstants.DB_NAME);
                result.remove(StorageConstants.DB_NAME);
            } else {
                LOGGER.error("Error, Got null db_name while insert");
                throw new JSONException("Got null db_name");
            }
            if (insertJson.containsKey(StorageConstants.ASSET_ID)) {
                assetID = insertJson.getString(StorageConstants.ASSET_ID);
                result.remove(StorageConstants.ASSET_ID);
            } else {
                LOGGER.error("Error, Got null asset_id while insert");
                throw new JSONException("Got null asset_id");
            }
            cloudantUtils.insert(dbName, assetID, result);
            result.put(StorageConstants.SQL_RESULT, "done");
            return result;
        } catch (JSONException e) {
            LOGGER.error("Error while insert", e);
            throw new StorageException("Error while insert", e);
        }
    }

    @Override
    public JSONObject update(JSONObject updateJson) throws StorageException {
        try {
            String dbName, assetID = null;
            JSONObject result = new JSONObject(updateJson);
            if (updateJson.containsKey(StorageConstants.DB_NAME)) {
                dbName = updateJson.getString(StorageConstants.DB_NAME);
                result.remove(StorageConstants.DB_NAME);
            } else {
                LOGGER.error("Error, Got null db_name while update");
                throw new JSONException("Got null db_name");
            }
            if (updateJson.containsKey(StorageConstants.ASSET_ID)) {
                assetID = updateJson.getString(StorageConstants.ASSET_ID);
                result.remove(StorageConstants.ASSET_ID);
            } else {
                LOGGER.error("Error, Got null asset_id while update");
                throw new JSONException("Got null asset_id");
            }
            cloudantUtils.update(dbName, assetID, result);
            result.put(StorageConstants.SQL_RESULT, "done");
            return result;
        } catch (JSONException e) {
            LOGGER.error("Error while update", e);
            throw new StorageException("Error while update", e);
        }
    }

    @Override
    public JSONObject delete(JSONObject deleteJson) throws StorageException {
        try {
            String dbName, assetID = null;
            JSONObject result = new JSONObject(deleteJson);
            if (deleteJson.containsKey(StorageConstants.DB_NAME)) {
                dbName = deleteJson.getString(StorageConstants.DB_NAME);
                result.remove(StorageConstants.DB_NAME);
            } else {
                LOGGER.error("Error, Got null db_name while delete");
                throw new JSONException("Got null db_name");
            }
            if (deleteJson.containsKey(StorageConstants.ASSET_ID)) {
                assetID = deleteJson.getString(StorageConstants.ASSET_ID);
                result.remove(StorageConstants.ASSET_ID);
            } else {
                LOGGER.error("Error, Got null asset_id while delete");
                // throw new JSONException("Got null asset_id");
                return new JSONObject();
            }
            cloudantUtils.delete(dbName, assetID);
            result.put(StorageConstants.SQL_RESULT, "done");
            return result;
        } catch (JSONException e) {
            LOGGER.error("Error while delete", e);
            throw new StorageException("Error while delete", e);
        }
    }

    @Override
    public JSONObject create(JSONObject createJson) throws StorageException {
        return insert(createJson);
    }

    @Override
    public JSONObject drop(JSONObject dropJson) throws StorageException {
        return delete(dropJson);
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject executeQuery(JSONObject executeJson, @SuppressWarnings("rawtypes") DBResultSetFormatter formatter)
        throws StorageException {
        try {
            String dbName, assetID = null;
            JSONObject result = new JSONObject();
            if (executeJson.containsKey(StorageConstants.DB_NAME)) {
                dbName = executeJson.getString(StorageConstants.DB_NAME);
                result.put(dbName, new JSONObject());
            } else {
                LOGGER.error("Error, Got null db_name while query");
                throw new JSONException("Got null db_name");
            }
            if (executeJson.containsKey(StorageConstants.ASSET_ID)) {
                assetID = executeJson.getString(StorageConstants.ASSET_ID);
            }
            JSONObject status = new JSONObject();
            try {
                if (assetID != null) {
                    JSONObject res = cloudantUtils.fetch(dbName, assetID);
                    if (res != null) {
                        result.getJSONObject(dbName).put(assetID, res);
                    } else {
                        result.getJSONObject(dbName).put(assetID, new JSONObject());
                    }
                } else {
                    List<JSONObject> listOfDocs = cloudantUtils.fetchMultiple(dbName);
                    for (JSONObject docsJSONObject : listOfDocs) {
                        result.getJSONObject(dbName).put(docsJSONObject.getString("_id"), docsJSONObject); // .getJSONObject(StorageConstants.DATA));
                    }
                }
                if (executeJson.has(StorageConstants.FORMAT_RESULTS)
                    && executeJson.get(StorageConstants.FORMAT_RESULTS) != null && formatter != null) {
                    status.put(StorageConstants.SQL_RESULT, formatter.format(result, executeJson));
                } else {
                    status.put(StorageConstants.SQL_RESULT, result);
                }
            } catch (StorageException e) {
                status.put(StorageConstants.ERROR, e.toString());
                status.put(StorageConstants.ERROR_CODE, -1);
                status.put(StorageConstants.ERROR_STACK, Arrays.toString(e.getStackTrace()));
            }
            return status;
        } catch (JSONException e) {
            LOGGER.error("Error while query", e);
            throw new StorageException("Error while query", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public InputStream executeQueryAndFetchAsStream(JSONObject executeJson,
        @SuppressWarnings("rawtypes") DBResultSetFormatter formatter)
        throws StorageException {
        JSONObject queryResult = executeQuery(executeJson, null);
        try {
            String result = null;
            if (executeJson.has(StorageConstants.FORMAT_RESULTS)
                && executeJson.get(StorageConstants.FORMAT_RESULTS) != null && formatter != null) {
                return formatter.formatAsStream(queryResult, executeJson);
            } else {
                result = queryResult.toString();
            }
            return new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
        } catch (JSONException e) {
            LOGGER.error("Error while query for CSV", e);
            throw new StorageException("Error while query for CSV", e);
        }
    }

    @Override
    public int getActiveConnection() {
        return 0;
    }

}
