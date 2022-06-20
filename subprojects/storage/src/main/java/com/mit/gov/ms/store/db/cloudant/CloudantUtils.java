/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.store.db.cloudant;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.SSLInitializationException;
import org.apache.wink.json4j.JSONArtifact;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.org.lightcouch.CouchDbException;
import com.cloudant.client.org.lightcouch.NoDocumentException;
import com.cloudant.http.Http;
import com.mit.gov.ms.store.StorageConstants;
import com.mit.gov.ms.store.StorageException;

/**
 * @author Shaik.Nawaz
 *
 */
public class CloudantUtils {

    private volatile CloudantClient cloudant = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudantUtils.class);
    private Map<String, Database> dbMap = new HashMap<String, Database>();
    private String cloudantURL = null;
    private String cloudantUser = null;
    private String cloudantPassword = null;

    public CloudantUtils(String cloudantURL, String cloudantUser, String cloudantPassword) {
        this.cloudantURL = cloudantURL;
        this.cloudantUser = cloudantUser;
        this.cloudantPassword = cloudantPassword;
    }

    private void initClient() throws StorageException {
        if (cloudant == null) {
            synchronized (CloudantUtils.class) {
                if (cloudant != null) {
                    return;
                }
                cloudant = createClient();
            }
        }
    }

    /**
     * to create a cloudant client using user credential update cloudant client
     * 
     * @throws StorageException
     */
    private CloudantClient createClient() throws StorageException {
        try {
            LOGGER.info("Connecting to Cloudant : ");
            CloudantClient client = ClientBuilder
                .url(new URL(cloudantURL))
                .username(cloudantUser)
                .password(cloudantPassword)
                .customSSLSocketFactory(SSLContexts.createDefault().getSocketFactory())
                .build();
            return client;
        } catch (CouchDbException | SSLInitializationException | MalformedURLException ce) {
            LOGGER.error("Error while creating cloudant client instance");
            throw new StorageException("Error while createClient", ce);
        }
    }

    public boolean checkStatus(String dbName) throws StorageException {
        if (cloudant == null) {
            initClient();
        }
        Database cdb = cloudant.database(dbName, false);
        try {
            cloudant.executeRequest(Http.HEAD(cdb.getDBUri()));
        } catch (NoDocumentException e) {
            return false;
        }
        return true;
    }

    /**
     * To delete the database
     * 
     * @param dbName
     * @throws StorageException
     */
    public void dropDB(String dbName) throws StorageException {
        if (cloudant == null) {
            initClient();
        }
        try {
            cloudant.deleteDB(dbName);
        } catch (NoDocumentException e) {
            LOGGER.error("Database not Found -", e);
        } catch (Exception e) {
            LOGGER.error("Error while deleting the database", e);
            throw new StorageException("Error while deleting the database", e);
        }
    }

    /**
     * To connect with the particular database of cloudant
     * 
     * @param dbName
     * : name of database
     * @return database object
     * @throws StorageException
     */
    public Database getDb(String dbName, Boolean create) throws StorageException {
        if (cloudant == null) {
            initClient();
        }

        Database db = null;
        synchronized (dbMap) {
            if (!dbMap.containsKey(dbName)) {
                try {
                    if (dbMap.size() >= 100) {
                        dbMap.clear();
                    }
                    db = cloudant.database(dbName, create);
                    dbMap.put(dbName, db);
                } catch (CouchDbException | SSLInitializationException ce) {
                    LOGGER.error("Database not Found", dbName);
                    throw new StorageException("Error while getDB", ce);
                }
            } else {
                db = cloudant.database(dbName, create);
            }
        }
        if (db == null) {
            LOGGER.error("Database can not be null");
            throw new StorageException("Database can not be null");
        }
        return db;
    }

    /**
     * @param dbName
     * @param id
     * @param data
     * @throws StorageException
     */
    public void insert(String dbName, String id, JSONArtifact data) throws StorageException {
        try {
            JSONObject jData = new JSONObject(data);
            jData.put(StorageConstants.DOC_ID, id);
            //jData.put(StorageConstants.DATA, data);

            Database db = getDb(dbName, true);
            db.save(jData);
        } catch (JSONException e) {
            LOGGER.error("Error while inserting the data", e);
            throw new StorageException("Error while adding id field in the data ", e);
        }

    }

    /**
     * @param dbName
     * @param id
     * @param data
     * @throws StorageException
     */
    public void update(String dbName, String id, JSONArtifact data) throws StorageException {
        try {
            JSONObject jData = new JSONObject(data);

            jData.put(StorageConstants.DOC_ID, id);
            //jData.put(StorageConstants.DATA, data);
            JSONObject jo = fetch(dbName, id);
            // TODO ?? why - _rev shoud be part of input data ot conflict may happen
            if (jo != null) {
                jData.put("_rev", jo.get("_rev"));
            }
            Database db = getDb(dbName, true);
            db.update(jData);
        } catch (JSONException e) {
            LOGGER.error("Error while updating the data", e);
            throw new StorageException("Error while updating the data ", e);
        }
    }

    /**
     * Fetch a document for a given doc_id
     * 
     * @param dbName
     * : name of a database
     * @param id
     * : document id
     * @return
     * @throws StorageException
     */
    public JSONObject fetch(String dbName, String id) throws StorageException {
        Database db = getDb(dbName, false);
        try {
            JSONObject obj = db.find(JSONObject.class, id);
            return obj;
        } catch (NoDocumentException e) {
            LOGGER.error("No Document found", e);
            return null;
        } catch (Exception e) {
            LOGGER.error("Error while fetching the id", e);
            throw new StorageException("Error while fetching the value ", e);
        }
    }

    public List<JSONObject> fetchMultiple(String dbName) throws StorageException {
        Database db = getDb(dbName, false);

        List<JSONObject> totalDocList = null;
        try {
            totalDocList =
                db.getAllDocsRequestBuilder().includeDocs(true).build().getResponse().getDocsAs(JSONObject.class);
        } catch (IOException e) {
            LOGGER.error("error while feching multiple documents", e);
            throw new StorageException("error while feching multiple documents  ", e);
        }
        return totalDocList;
    }

    /**
     * delete a document for a given doc_id
     * 
     * @param dbName
     * : name of database
     * @param id
     * : document ID
     * @throws StorageException
     */
    public void delete(String dbName, String id) throws StorageException {
        if (id == null || id.isEmpty()) {
            return;
        }
        
        Database db = getDb(dbName, false);
        try {
            JSONObject obj = db.find(JSONObject.class, id);
            if (obj != null) {
                db.remove(obj);
            }
        } catch (NoDocumentException e) {
            LOGGER.error("No Document found", e);
            return;
        } catch (Exception e) {
            LOGGER.error("Error while deleting the id", e);
            throw new StorageException("Error while deleting the value ", e);
        }

    }

}
