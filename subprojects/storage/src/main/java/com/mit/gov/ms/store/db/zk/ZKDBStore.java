/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.store.db.zk;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.wink.json4j.JSONArray;
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
public class ZKDBStore extends DBStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZKDBStore.class);

    private ZKUtils zkUtils = null;

    public ZKDBStore(String zooKeeperHost, String rootNodeName) throws StorageException {
        super(StorageType.ZOOKEEPER.name());
        zkUtils = new ZKUtils(zooKeeperHost, rootNodeName);
    }

    @Override
    public JSONObject getDBStatus(String dbName) throws StorageException {
        try {
            JSONObject status = new JSONObject();
            try {
                status.put(StorageConstants.DB_TYPE, StorageType.FILE);
                status.put(StorageConstants.ROOT_NODE, zkUtils.getRootNodeName());
                status.put(StorageConstants.DEPENDENCY_STATUS, StorageConstants.OK);
            } catch (JSONException e) {
                status.put(StorageConstants.ERROR, e.getMessage());
                status.put(StorageConstants.ERROR_STACK, Arrays.toString(e.getStackTrace()));
                LOGGER.error("Error while getDBStatus", e);
            }
            return status;
        } catch (JSONException e) {
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
            String nodeType, subType = null;
            JSONObject result = new JSONObject(insertJson);
            if (insertJson.containsKey(StorageConstants.NODE_TYPE)) {
                nodeType = insertJson.getString(StorageConstants.NODE_TYPE);
                result.remove(StorageConstants.NODE_TYPE);
            } else {
                LOGGER.error("Error, Got null node_type while insert ");
                throw new JSONException("Got null node_type");
            }
            if (insertJson.containsKey(StorageConstants.SUB_TYPE)) {
                subType = insertJson.getString(StorageConstants.SUB_TYPE);
                result.remove(StorageConstants.SUB_TYPE);
            } else {
                subType = "all";
            }
            zkUtils.saveWithRetries(nodeType, subType, result);
            result.put(StorageConstants.SQL_RESULT, "done");
            return result;
        } catch (JSONException e) {
            LOGGER.error("Error while insert", e);
            throw new StorageException("Error while insert", e);
        }
    }

    @Override
    public JSONObject update(JSONObject updateJson) throws StorageException {
        return insert(updateJson);
    }

    @Override
    public JSONObject delete(JSONObject deleteJson) throws StorageException {
        try {
            String nodeType, subType = null;
            JSONObject result = new JSONObject(deleteJson);
            if (deleteJson.containsKey(StorageConstants.NODE_TYPE)) {
                nodeType = deleteJson.getString(StorageConstants.NODE_TYPE);
                result.remove(StorageConstants.NODE_TYPE);
            } else {
                LOGGER.error("Error, Got null node_type while delete ");
                throw new JSONException("Got null node_type");
            }
            if (deleteJson.containsKey(StorageConstants.SUB_TYPE)) {
                subType = deleteJson.getString(StorageConstants.SUB_TYPE);
                result.remove(StorageConstants.SUB_TYPE);
            }
            zkUtils.delete(nodeType, subType);
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
            String nodeType, subType = null;
            JSONObject result = new JSONObject();
            if (executeJson.containsKey(StorageConstants.NODE_TYPE)) {
                nodeType = executeJson.getString(StorageConstants.NODE_TYPE);
                result.put(nodeType, new JSONObject());
            } else {
                LOGGER.error("Error, Got null node_type while delete ");
                throw new JSONException("Got null node_type");
            }
            if (executeJson.containsKey(StorageConstants.SUB_TYPE)) {
                subType = executeJson.getString(StorageConstants.SUB_TYPE);
            }
            JSONObject status = new JSONObject();
            try {
                if (subType != null) {
                    result.getJSONObject(nodeType).put(subType, zkUtils.get(nodeType, subType));
                } else {
                    JSONArray subtypes = (JSONArray) zkUtils.get(nodeType, null);
                    for (int i = 0; i < subtypes.length(); i++) {
                        result
                            .getJSONObject(nodeType)
                            .put(subtypes.getString(i), zkUtils.get(nodeType, subtypes.getString(i)));
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
        } catch (JSONException | NullPointerException e) {
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
