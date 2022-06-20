/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.store.api;

import org.slf4j.LoggerFactory;

import com.mit.gov.ms.store.StorageException;

import org.slf4j.Logger;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

/**
 * @author Shaik.Nawaz
 *
 */
public abstract class DBStore implements DBAction {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(DBStore.class);
    
    protected String dbType = null;
    
    protected DBStore() {
        this(null);
    }
    
    protected DBStore(String dbType) {
        this.dbType = dbType;
    }

    /** Get number of Active Connections
     * @return int number of Active Connections
     */
    public abstract int getActiveConnection();
    
    /** Get default singleton instance of DBStore implementation for passed dbType
     * @param dbType name of DB provider (db2, oracle etc)
     * @return default singleton instance of DBStore implementation for passed dbType
     */
//    public static DBStore getInstance(String dbType) {
//        if (dbType.equalsIgnoreCase("db2") || dbType.equalsIgnoreCase("dashdb") || dbType.equalsIgnoreCase("oracle")
//            || dbType.equalsIgnoreCase("sqlserver")) {
//            return SQLDBStore.getInstance(dbType.toLowerCase());
//        }
//        return null;
//    }

    /** Get dbType of current implementation of DBStore
     * @return dbType (db2, oracle etc) of current implementation of DBStore
     */
    public String getDbType() {
        return this.dbType;
    }
        
    /* (non-Javadoc)
     * @see com.ibm.analytics.referencedata.store.DBAction#insert(java.lang.String, org.apache.wink.json4j.JSONObject)
     */
    @Override
    public JSONObject insert(String connectionId, JSONObject insertJson) throws StorageException {
        if (connectionId != null && insertJson != null) {
            try {
                insertJson.put("CONNECTION_ID", connectionId);
            } catch (JSONException e) {
                LOGGER.error("Error while attaching connectioID for insert", e);
                throw new StorageException("Error while attaching connectioID for insert", e);
            }
            return insert(insertJson);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.analytics.referencedata.store.DBAction#update(java.lang.String, org.apache.wink.json4j.JSONObject)
     */
    @Override
    public JSONObject update(String connectionId, JSONObject updateJson) throws StorageException {
        if (connectionId != null && updateJson != null) {
            try {
                updateJson.put("CONNECTION_ID", connectionId);
            } catch (JSONException e) {
                LOGGER.error("Error while attaching connectioID for update", e);
                throw new StorageException("Error while attaching connectioID for update", e);
            }
            return update(updateJson);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.analytics.referencedata.store.DBAction#delete(java.lang.String, org.apache.wink.json4j.JSONObject)
     */
    @Override
    public JSONObject delete(String connectionId, JSONObject deleteJson) throws StorageException {
        if (connectionId != null && deleteJson != null) {
            try {
                deleteJson.put("CONNECTION_ID", connectionId);
            } catch (JSONException e) {
                LOGGER.error("Error while attaching connectioID for delete", e);
                throw new StorageException("Error while attaching connectioID for delete", e);
            }
            return delete(deleteJson);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.analytics.referencedata.store.DBAction#create(java.lang.String, org.apache.wink.json4j.JSONObject)
     */
    @Override
    public JSONObject create(String connectionId, JSONObject createJson) throws StorageException {
        if (connectionId != null && createJson != null) {
            try {
                createJson.put("CONNECTION_ID", connectionId);
            } catch (JSONException e) {
                LOGGER.error("Error while attaching connectioID for create", e);
                throw new StorageException("Error while attaching connectioID for create", e);
            }
            return create(createJson);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.analytics.referencedata.store.DBAction#drop(java.lang.String, org.apache.wink.json4j.JSONObject)
     */
    @Override
    public JSONObject drop(String connectionId, JSONObject dropJson) throws StorageException {
        if (connectionId != null && dropJson != null) {
            try {
                dropJson.put("CONNECTION_ID", connectionId);
            } catch (JSONException e) {
                LOGGER.error("Error while attaching connectioID for drop", e);
                throw new StorageException("Error while attaching connectioID for drop", e);
            }
            return drop(dropJson);
        }
        return null;
    }
    
    
}
