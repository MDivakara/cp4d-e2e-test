/*
 * IBM Confidential
 * OCO Source Materials
 * 5724-Q36
 * Copyright IBM Corp. 2019
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.mit.gov.ms.store.schema;

import java.util.ArrayList;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.mit.gov.ms.store.StorageConstants;
import com.mit.gov.ms.store.StorageException;

/**
 * @author Shaik.Nawaz
 *
 */
public abstract class StorageSchema {
    
    protected String dbType = null;
    
    private int schemaVersion = 0;
    
    public StorageSchema(String dbType, int schemaVersion) {
        super();
        this.dbType = dbType.toLowerCase();
        this.schemaVersion = schemaVersion;
    }

    public int getSchemaVersion() {
        return this.schemaVersion;
    }
    
    public abstract ArrayList<String> getInitStatementList();

    public abstract ArrayList<String> getTableList();

    public abstract ArrayList<String> getUpgradeStatementList(int currVersion, int newVersion);

    /***
     * Method that will provide all required queries to initialize the schema like
     * create table and history table support
     * 
     * @param schema_name :- schema name
     * @return a JSON object that contains
     * 
     *         <pre>
     * "init_sql_strings" : [{"sql_string":<Query1>} ,{"sql_string":<Query2>} ]
     *         </pre>
     * 
     * @throws StorageException
     */
    public JSONObject getInitSchemaStatements(String schema_name) throws StorageException {
        try {
            JSONObject result = new JSONObject();
            JSONArray initStatements = new JSONArray();
            ArrayList<String> initStmtList = getInitStatementList();
            for (String statement: initStmtList) {
                initStatements
                    .put(new JSONObject()
                        .put(StorageConstants.SQL_STRING_KEY,
                            String.format(statement, schema_name, schema_name, schema_name)));
            }
            result.put(StorageConstants.INIT_SCHEMA_SQL_STRING_KEY, initStatements);
            return result;
        } catch (JSONException e) {
            throw new StorageException("Error while getCreateTableStatements", e);
        }
    }

    public JSONObject getDropTableStatements() throws StorageException {
        try {
            JSONObject result = new JSONObject();
            JSONArray initStatements = new JSONArray();
            ArrayList<String> tableList = getTableList();
            for (String tableName: tableList) {
                initStatements
                    .put(new JSONObject()
                        .put(StorageConstants.SQL_STRING_KEY, "drop table " + tableName));
            }
            result.put(StorageConstants.INIT_SCHEMA_SQL_STRING_KEY, initStatements);
            return result;
        } catch (JSONException e) {
            throw new StorageException("Error while getDropTableStatements", e);
        }
    }

    /***
     * Method to check if the schema and all the tables inside that schema exist or
     * not
     * 
     * @param schema_name
     * @return a JSONObject which will contain a query to check if last table of
     *         that schema was created or not
     * 
     *         <pre>
     *      {
     *          "sql_string" : "{@code<sql string>}"
     *      }
     *         </pre>
     * 
     * @throws StorageException
     */
    public JSONObject getSchemaVersionQuery(String schema_name) throws StorageException {
        try {
            return new JSONObject()
                .put(StorageConstants.SQL_STRING_KEY,
                    String
                        .format("SELECT SCHEMA_VERSION version FROM %s.GI_SCHEMA_VERSION".toLowerCase(), schema_name))
                .put(StorageConstants.EXPECTED_ERROR_CODE, getNotFoundCode());
        } catch (JSONException e) {
            throw new StorageException("Error while getCheckSchemaQuery", e);
        }
    }
    
    public int getNotFoundCode() {
        if (dbType.startsWith(StorageConstants.DB_TYPE_DB2)) {
            return -204;
        } else if (dbType.startsWith(StorageConstants.DB_TYPE_ORCL)) {
            return 942;
        } else if (dbType.startsWith(StorageConstants.DB_TYPE_MSSQL)) {
            return 208;
        } else {
            return -1;
        }
    }

    public JSONObject getIncrimentalUpgradeStatements(String schema_name, int currVersion, int newVersion) throws StorageException {
        try {
            JSONObject result = new JSONObject();
            JSONArray upgradeStatements = new JSONArray();
            ArrayList<String> upgradeStmtlist = getUpgradeStatementList(currVersion, newVersion);
            for (String statement: upgradeStmtlist) {
                upgradeStatements
                    .put(new JSONObject()
                        .put(StorageConstants.SQL_STRING_KEY,
                            String.format(statement, schema_name, schema_name, schema_name)));
            }
            result.put(StorageConstants.INIT_SCHEMA_SQL_STRING_KEY, upgradeStatements);
            return result;
        } catch (JSONException e) {
            throw new StorageException("Error while getCreateTableStatements", e);
        }
    }

}
