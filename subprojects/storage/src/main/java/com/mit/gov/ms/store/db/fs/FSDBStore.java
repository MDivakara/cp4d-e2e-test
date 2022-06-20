/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.store.db.fs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.wink.json4j.JSON;
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
public class FSDBStore extends DBStore {

    private final static Logger LOGGER = LoggerFactory.getLogger(FSDBStore.class);
    FileUtils fileUtils = null;

    public FSDBStore(String rootNodeName) {
        super(StorageType.FILE.name());
        fileUtils = new FileUtils(rootNodeName);
    }

    @Override
    public JSONObject getDBStatus(String dbName) throws StorageException {
        try {
            JSONObject status = new JSONObject();
            try {
                status.put(StorageConstants.DB_TYPE, StorageType.FILE);
                status.put(StorageConstants.ROOT_NODE, fileUtils.getRootNode());
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
        fileUtils.checkPermission();
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
                throw new StorageException("Got null node_type");
            }
            if (insertJson.containsKey(StorageConstants.SUB_TYPE)) {
                subType = insertJson.getString(StorageConstants.SUB_TYPE);
                result.remove(StorageConstants.SUB_TYPE);
            } else {
                subType = "all";
            }
            fileUtils.writeToFileSystem(nodeType, subType, new ByteArrayInputStream(result.toString().getBytes()));
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
                throw new StorageException("Got null node_type");
            }
            if (deleteJson.containsKey(StorageConstants.SUB_TYPE)) {
                subType = deleteJson.getString(StorageConstants.SUB_TYPE);
                result.remove(StorageConstants.SUB_TYPE);
            }
            if (subType != null) {
                fileUtils.deleteFromFileSystem(nodeType, subType);
            } else {
                fileUtils.cleanUpFromFileSystem(nodeType);
            }
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
                    InputStream is = fileUtils.readFromFileSystem(nodeType, subType);
                    if (is != null) {
                        result.getJSONObject(nodeType).put(subType, JSON.parse(IOUtils.toString(is)));
                        try {
                            is.close();
                        } catch (IOException e) {
                            LOGGER.error("Error closing inputstream for " + nodeType + "/" + subType, e);
                        }
                    } else {
                        result.getJSONObject(nodeType).put(subType, new JSONObject());
                    }
                } else {
                    ArrayList<String> subtypes = fileUtils.getFileList(nodeType);
                    for (int i = 0; i < subtypes.size(); i++) {
                        InputStream is = fileUtils.readFromFileSystem(nodeType, subtypes.get(i));
                        if (is != null) {
                            result.getJSONObject(nodeType).put(subtypes.get(i), JSON.parse(IOUtils.toString(is)));
                            try {
                                is.close();
                            } catch (IOException e) {
                                LOGGER
                                    .error(
                                        "Error closing inputstream for " + nodeType + "/" + subtypes.get(i), e);
                            }
                        } else {
                            result.getJSONObject(nodeType).put(subtypes.get(i), new JSONObject());
                        }
                    }
                }
                if (executeJson.has(StorageConstants.FORMAT_RESULTS)
                    && executeJson.get(StorageConstants.FORMAT_RESULTS) != null && formatter != null) {
                    status.put(StorageConstants.SQL_RESULT, formatter.format(result, executeJson));
                } else {
                    status.put(StorageConstants.SQL_RESULT, result);
                }
            } catch (StorageException | IOException | NullPointerException | JSONException e) {
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
