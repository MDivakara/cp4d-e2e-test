/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.store.queries;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.mit.gov.ms.store.StorageConstants;
import com.mit.gov.ms.store.StorageException;
import com.mit.gov.ms.store.api.QueryProvider;
import com.mit.gov.ms.store.db.sql.SQLDBHelper;

/**
 * @author Shaik.Nawaz
 *
 */
public class SQLQueryProvider implements QueryProvider {

    private final static Logger LOGGER = LoggerFactory.getLogger(SQLQueryProvider.class);
    
    private SQLQueries storageQueries = null;
    
    protected String dbType = null;

    private SQLQueryProvider() {

    }

    public SQLQueryProvider(SQLQueries storageQueries, String dbType) {
        this();
        this.dbType = dbType;
        this.storageQueries = storageQueries;
    }
    
    @Override
    public JSONObject getDBVerifyQuery() throws StorageException {
        try {
            return new JSONObject()
                .put(StorageConstants.SQL_STRING_KEY, storageQueries.getQuery("DB_VERIFY_QUERY"));
        } catch (JSONException e) {
            throw new StorageException("Exception while getDBVerifyQuery", e);
        }
    }

    /***
     * method to create the insertStatement
     * 
     * @param insertJson is an object with fields as
     * 
     * { "schema_name" : <schema_name>, "table_name" : <tablename>,
     * "columns": JSONArray[ {"column_name1","column_name2"},
     * "param_array" : JSONArray[ {"param_type" : "<type>" ,
     * "param_value" : value} ] }
     * 
     * @return JSONObject which will contain the fields as { "sql_string" : "insert
     * .....","schema_name" : <schema_name>, "table_name" : <tablename>,
     * "columns": JSONArray[ {"column_name1","column_name2"}, "param_array"
     * : JSONArray[ {"param_type" : "<type>" , "param_value" : value} ] }
     */

    @Override
    public JSONObject getInsertQuery(JSONObject insertJson) throws StorageException {

        try {
            String schema_name, tableName;
            if (insertJson.has(StorageConstants.SCHEMA_NAME)) {
                schema_name = insertJson.getString(StorageConstants.SCHEMA_NAME);
            } else {
                LOGGER.error("Error, Got null schema_name while building insert sql ");
                throw new JSONException("Got null schema_name");
            }
            if (insertJson.has(StorageConstants.TABLE_NAME)) {
                tableName = insertJson.getString(StorageConstants.TABLE_NAME);
            } else {
                LOGGER.error("Error, Got null tableName while building insert sql ");
                throw new JSONException("Got null tableName");
            }
            String insertSqlQuery = "INSERT INTO " + schema_name + "." + tableName;

            JSONArray columns = null;
            JSONArray param_array = null;
            if (insertJson.has(StorageConstants.COLUMNS)) {
                columns = insertJson.getJSONArray(StorageConstants.COLUMNS);
            } else {
                LOGGER.error("Error, Got null column Names while building insert sql ");
                throw new JSONException("Got null column Names");
            }
            if (insertJson.has(StorageConstants.PARAM_ARRAY)
                && !insertJson.getJSONArray(StorageConstants.PARAM_ARRAY).isEmpty()) {
                param_array = insertJson.getJSONArray(StorageConstants.PARAM_ARRAY);
            } else {
                LOGGER.error("Error, Got null/empty param array while building insert sql ");
                throw new JSONException("Got null/empty param array");
            }

            if (insertJson.has(StorageConstants.SELECT_FROM)
                && !insertJson.getString(StorageConstants.SELECT_FROM).isEmpty()) {
                String columnsName = " (";
                for (int i = 0; i < columns.size(); i++) {
                    columnsName = columnsName + columns.getString(i);
                    if (i != columns.size() - 1) {
                        columnsName = columnsName + ",";
                    } else {
                        columnsName = columnsName + ") ";
                    }
                }
                // add the select from statement
                String query = insertJson.getString(StorageConstants.SELECT_FROM);
                if (insertJson.has(StorageConstants.FORMAT_STRINGS)) {
                    JSONArray formats = insertJson.getJSONArray(StorageConstants.FORMAT_STRINGS);
                    List<Object> derived_formats = new ArrayList<Object>();
                    for (int i = 0; i < formats.size(); i++) {
                        derived_formats.add(formats.get(i));
                    }
                    query = String.format(query, derived_formats.toArray(new Object[derived_formats.size()]));
                } else {
                    query = String.format(query, schema_name, schema_name, schema_name);
                }
                insertSqlQuery = insertSqlQuery + columnsName + query;
            } else {
                String columnsName = " (";
                String values = " (";
                for (int i = 0; i < columns.size(); i++) {
                    columnsName = columnsName + columns.getString(i);
                    JSONObject param = param_array.getJSONObject(i);
                    String param_type = param.getString(StorageConstants.PARAM_TYPE);
                    if (param_type.equalsIgnoreCase(StorageConstants.TIMESTAMP) && SQLDBHelper.isCurrentTS(param)) {
                        values = values + StorageConstants.CURRENT_TIMESTAMP;
                    } else {
                        values = values + "?";
                    }
                    if (i != columns.size() - 1) {
                        columnsName = columnsName + ",";
                        values = values + ",";
                    } else {
                        columnsName = columnsName + ") ";
                        values = values + ") ";
                    }
                }
                // add the column names and ? mark for each value in the query
                insertSqlQuery = insertSqlQuery + columnsName + " VALUES" + values;
            }
            // Re-used the input json
            insertJson.put(StorageConstants.SQL_STRING_KEY, insertSqlQuery);

        } catch (JSONException e) {
            LOGGER.error("Error while building insert sql", e);
            throw new StorageException("Error while building insert sql", e);
        } catch (NullPointerException e) {
            LOGGER.error("Error while building insert sql ", e);
            throw new StorageException("Error while building insert sql", e);
        }

        return insertJson;
    }

    /***
     * method to create the update Statement
     * 
     * @param updateJson is an object with fields as
     * 
     * { "schema_name" : <schema_name>, "table_name" : <tablename>,
     * "columns": JSONArray[ {"column_name1","column_name2", },
     * "param_array" : JSONArray[ {"param_type" : "<type>" ,
     * "param_value" : value} ], "condtion_columns" :"columns":
     * JSONArray[ {"column_name1","column_name2"}}
     * 
     * @return JSONObject which will contain the fields as { "sql_string" : "update
     * .....","schema_name" : <schema_name>, "table_name" : <tablename>,
     * "columns": JSONArray[ {"column_name1","column_name2", },
     * "param_array" : JSONArray[ {"param_type" : "<type>" , "param_value" :
     * value} ], "condtion_columns" : JSONArray[
     * {"column_name1","column_name2"}}
     */

    @Override
    public JSONObject getUpdateQuery(JSONObject updateJson) throws StorageException {
        try {
            String schema_name, tableName;
            if (updateJson.has(StorageConstants.SCHEMA_NAME)) {
                schema_name = updateJson.getString(StorageConstants.SCHEMA_NAME);
            } else {
                LOGGER.error("Error Got null schema_name while building update sql ");
                throw new JSONException("Got null schema_name");
            }
            if (updateJson.has(StorageConstants.TABLE_NAME)) {
                tableName = updateJson.getString(StorageConstants.TABLE_NAME);
            } else {
                LOGGER.error("Error Got null tableName while building update sql ");
                throw new JSONException("Got null tableName");
            }
            JSONArray columns = null;
            JSONArray condition_columns = null;
            JSONArray param_array = null;
            int paramIndex = 0;
            if (updateJson.has(StorageConstants.COLUMNS)) {
                columns = updateJson.getJSONArray(StorageConstants.COLUMNS);
            } else {
                LOGGER.error("Error, Got null column Names while building update sql ");
                throw new JSONException("Got null column Names");
            }
            if (updateJson.has(StorageConstants.CONDITION_COLUMNS)) {
                condition_columns = updateJson.getJSONArray(StorageConstants.CONDITION_COLUMNS);
            } else {
                LOGGER.error("Error, Got null condition columns while building update sql ");
                throw new JSONException("Got null condition columns");
            }
            if (updateJson.has(StorageConstants.PARAM_ARRAY)
                && !updateJson.getJSONArray(StorageConstants.PARAM_ARRAY).isEmpty()) {
                param_array = updateJson.getJSONArray(StorageConstants.PARAM_ARRAY);
            } else {
                LOGGER.error("Error, Got null/empty param array while building update sql ");
                throw new JSONException("Got null/empty param array");
            }

            String updateSqlQuery = "UPDATE " + schema_name + "." + tableName + " ";
            // if (updateJson.has(RDSConstants.BUSINESS_TIME)
            // && updateJson.getString(RDSConstants.BUSINESS_TIME).equals(RDSConstants.BUSINESS_AS_OF)) { // Not
            // Supported
            // updateSqlQuery += " FOR PORTION AS OF ? ";
            // } else if (updateJson.has(RDSConstants.BUSINESS_TIME)
            // && updateJson.getString(RDSConstants.BUSINESS_TIME).equals(RDSConstants.BUSINESS_AS_OF_CURRENT)) { // Not
            // Supported
            // updateSqlQuery += " FOR PORTION AS OF CURRENT TIMESTAMP ";
            // } else
            if (updateJson.has(StorageConstants.BUSINESS_TIME)
                && updateJson.getString(StorageConstants.BUSINESS_TIME).equals(StorageConstants.BUSINESS_FROM_TO)) {
                updateSqlQuery += " FOR PORTION OF BUSINESS_TIME FROM ? TO ? ";
                paramIndex += 2;
            } else if (updateJson.has(StorageConstants.BUSINESS_TIME)
                && updateJson.getString(StorageConstants.BUSINESS_TIME).equals(StorageConstants.BUSINESS_BETWEEN)) {
                updateSqlQuery += " FOR PORTION OF BUSINESS_TIME FROM ? TO ? ";
                paramIndex += 2;
            } else if (updateJson.has(StorageConstants.BUSINESS_TIME) && updateJson
                .getString(StorageConstants.BUSINESS_TIME)
                .equals(StorageConstants.BUSINESS_BETWEEN_CURRENT)) {
                updateSqlQuery += " FOR PORTION OF BUSINESS_TIME FROM " + StorageConstants.CURRENT_TIMESTAMP + " TO "
                    + StorageConstants.CURRENT_TIMESTAMP;
            } else if (updateJson.has(StorageConstants.BUSINESS_TIME) && updateJson
                .getString(StorageConstants.BUSINESS_TIME)
                .equals(StorageConstants.BUSINESS_BETWEEN_START_CURRENT)) {
                updateSqlQuery += " FOR PORTION OF BUSINESS_TIME FROM " + StorageConstants.CURRENT_TIMESTAMP + " TO ? ";
                paramIndex += 1;
            } else if (updateJson.has(StorageConstants.BUSINESS_TIME) && updateJson
                .getString(StorageConstants.BUSINESS_TIME)
                .equals(StorageConstants.BUSINESS_BETWEEN_END_CURRENT)) {
                updateSqlQuery += " FOR PORTION OF BUSINESS_TIME FROM ? TO " + StorageConstants.CURRENT_TIMESTAMP;
                paramIndex += 1;
            }

            updateSqlQuery = updateSqlQuery + " SET ";
            String columnsName = "";
            for (int i = 0; i < columns.size(); i++, paramIndex++) {
                JSONObject param = param_array.getJSONObject(paramIndex);
                String param_type = param.getString(StorageConstants.PARAM_TYPE);
                if (param_type.equalsIgnoreCase(StorageConstants.TIMESTAMP) && SQLDBHelper.isCurrentTS(param)) {
                    columnsName =
                        columnsName + columns.getString(i) + " = " + StorageConstants.CURRENT_TIMESTAMP + " ";
                } else {
                    columnsName = columnsName + columns.getString(i) + " = ? ";
                }
                if (i != columns.size() - 1) {
                    columnsName = columnsName + ",";
                }
            }
            // added the column names and ? mark for each value in the query
            updateSqlQuery = updateSqlQuery + columnsName;

            String condition = "";

            // condition field can not be missing , (update of complete table not allowed)
            updateSqlQuery = updateSqlQuery + " WHERE ";
            for (int i = 0; i < condition_columns.size(); i++, paramIndex++) {
                JSONObject param = param_array.getJSONObject(paramIndex);
                String param_type = param.getString(StorageConstants.PARAM_TYPE);
                if (param_type.equalsIgnoreCase(StorageConstants.TIMESTAMP) && SQLDBHelper.isCurrentTS(param)) {
                    condition = condition + condition_columns.getString(i) + " = "
                        + StorageConstants.CURRENT_TIMESTAMP + " ";
                } else {
                    condition = condition + condition_columns.getString(i) + " = ? ";
                }
                if (i != condition_columns.size() - 1) {
                    condition = condition + "AND ";
                }
            }
            // Add the conditions to the delete query
            updateSqlQuery = updateSqlQuery + condition;

            // Re-use the input json
            updateJson.put(StorageConstants.SQL_STRING_KEY, updateSqlQuery);

        } catch (JSONException e) {
            LOGGER.error("Error while building update sql ", e);
            throw new StorageException("Error while building update sql", e);
        } catch (NullPointerException e) {
            LOGGER.error("Error while building update sql", e);
            throw new StorageException("Error while building update sql", e);
        }
        return updateJson;
    }

    /***
     * method to create the delete Statement
     * 
     * @param deleteJson is an object with fields as
     * 
     * { "schema_name" : <schema_name>, "table_name" : <tablename>,
     * "condtion_columns": JSONArray[
     * {"column_name1","column_name2", }, "param_array" :
     * JSONArray[ {"param_type" : "<type>" , "param_value" :
     * value} ] }
     * 
     * @return JSONObject which will contain the fields as { "sql_string" : "delete
     * .....","schema_name" : <schema_name>, "table_name" : <tablename>,
     * "condtion_columns": JSONArray[ {"column_name1","column_name2"},
     * "param_array" : JSONArray[ {"param_type" : "<type>" , "param_value" :
     * value} ] }
     */

    @Override
    public JSONObject getDeleteQuery(JSONObject deleteJson) throws StorageException {
        try {
            String schema_name, tableName;
            if (deleteJson.has(StorageConstants.SCHEMA_NAME)) {
                schema_name = deleteJson.getString(StorageConstants.SCHEMA_NAME);
            } else {
                LOGGER.error("Error Got null schema_name while building delete sql");
                throw new JSONException("Got null schema_name");
            }
            if (deleteJson.has(StorageConstants.TABLE_NAME)) {
                tableName = deleteJson.getString(StorageConstants.TABLE_NAME);
            } else {
                LOGGER.error("Error Got null tableName while building delete sql");
                throw new JSONException("Got null tableName");
            }
            JSONArray condition_columns = null;
            JSONArray param_array = null;
            int paramIndex = 0;
            if (deleteJson.has(StorageConstants.CONDITION_COLUMNS)) {
                condition_columns = deleteJson.getJSONArray(StorageConstants.CONDITION_COLUMNS);
            } else {
                LOGGER.error("Error, Got null condition columns while building delete sql ");
                throw new JSONException("Got null condition columns");
            }
            if (deleteJson.has(StorageConstants.PARAM_ARRAY)
                && !deleteJson.getJSONArray(StorageConstants.PARAM_ARRAY).isEmpty()) {
                param_array = deleteJson.getJSONArray(StorageConstants.PARAM_ARRAY);
            } else {
                LOGGER.error("Error, Got null/empty param array while building delete sql ");
                throw new JSONException("Got null/empty param array");
            }

            String deleteSqlQuery = "DELETE FROM " + schema_name + "." + tableName;

            // if (deleteJson.has(RDSConstants.BUSINESS_TIME)
            // && deleteJson.getString(RDSConstants.BUSINESS_TIME).equals(RDSConstants.BUSINESS_AS_OF)) { // Not
            // Supported
            // deleteSqlQuery += " FOR PORTION AS OF ? ";
            // } else if (deleteJson.has(RDSConstants.BUSINESS_TIME)
            // && deleteJson.getString(RDSConstants.BUSINESS_TIME).equals(RDSConstants.BUSINESS_AS_OF_CURRENT)) { // Not
            // Supported
            // deleteSqlQuery += " FOR PORTION AS OF CURRENT TIMESTAMP ";
            // } else
            if (deleteJson.has(StorageConstants.BUSINESS_TIME)
                && deleteJson.getString(StorageConstants.BUSINESS_TIME).equals(StorageConstants.BUSINESS_FROM_TO)) {
                deleteSqlQuery += " FOR PORTION OF BUSINESS_TIME FROM ? TO ? ";
                paramIndex += 2;
            } else if (deleteJson.has(StorageConstants.BUSINESS_TIME)
                && deleteJson.getString(StorageConstants.BUSINESS_TIME).equals(StorageConstants.BUSINESS_BETWEEN)) {
                deleteSqlQuery += " FOR PORTION OF BUSINESS_TIME FROM ? TO ? ";
                paramIndex += 2;
            } else if (deleteJson.has(StorageConstants.BUSINESS_TIME) && deleteJson
                .getString(StorageConstants.BUSINESS_TIME)
                .equals(StorageConstants.BUSINESS_BETWEEN_CURRENT)) {
                deleteSqlQuery += " FOR PORTION OF BUSINESS_TIME FROM " + StorageConstants.CURRENT_TIMESTAMP + " TO "
                    + StorageConstants.CURRENT_TIMESTAMP;
            } else if (deleteJson.has(StorageConstants.BUSINESS_TIME) && deleteJson
                .getString(StorageConstants.BUSINESS_TIME)
                .equals(StorageConstants.BUSINESS_BETWEEN_START_CURRENT)) {
                deleteSqlQuery += " FOR PORTION OF BUSINESS_TIME FROM " + StorageConstants.CURRENT_TIMESTAMP + " TO ? ";
                paramIndex += 1;
            } else if (deleteJson.has(StorageConstants.BUSINESS_TIME) && deleteJson
                .getString(StorageConstants.BUSINESS_TIME)
                .equals(StorageConstants.BUSINESS_BETWEEN_END_CURRENT)) {
                deleteSqlQuery += " FOR PORTION OF BUSINESS_TIME FROM ? TO " + StorageConstants.CURRENT_TIMESTAMP;
                paramIndex += 1;
            }

            String condition = "";

            // condition field can not be missing , delete of complete table data is not allowed
            deleteSqlQuery = deleteSqlQuery + " WHERE ";
            for (int i = 0; i < condition_columns.size(); i++, paramIndex++) {
                JSONObject param = param_array.getJSONObject(paramIndex);
                String param_type = param.getString(StorageConstants.PARAM_TYPE);
                if (param_type.equalsIgnoreCase(StorageConstants.TIMESTAMP) && SQLDBHelper.isCurrentTS(param)) {
                    condition = condition + condition_columns.getString(i) + " = "
                        + StorageConstants.CURRENT_TIMESTAMP + " ";
                } else {
                    condition = condition + condition_columns.getString(i) + " = ? ";
                }
                if (i != condition_columns.size() - 1) {
                    condition = condition + "AND ";
                }
            }
            // Add the conditions to the delete query
            deleteSqlQuery = deleteSqlQuery + condition;

            // Re-use the input json
            deleteJson.put(StorageConstants.SQL_STRING_KEY, deleteSqlQuery);

        } catch (JSONException e) {
            LOGGER.error("Error while building delete sql", e);
            throw new StorageException("Error while building delete sql", e);
        } catch (NullPointerException e) {
            LOGGER.error("Error while building delete sql", e);
            throw new StorageException("Error while building delete sql", e);
        }

        return deleteJson;
    }

    @Override
    public JSONObject getDropQuery(JSONObject dropJson) throws StorageException {
        try {
            String schema_name, tableName;
            if (dropJson.has(StorageConstants.SCHEMA_NAME)) {
                schema_name = dropJson.getString(StorageConstants.SCHEMA_NAME);
            } else {
                LOGGER.error("Error Got null schema_name while building delete sql");
                throw new JSONException("Got null schema_name");
            }
            if (dropJson.has(StorageConstants.TABLE_NAME)) {
                tableName = dropJson.getString(StorageConstants.TABLE_NAME);
            } else {
                LOGGER.error("Error Got null tableName while building delete sql");
                throw new JSONException("Got null tableName");
            }

            String dropSqlQuery = "DROP TABLE " + schema_name + "." + tableName;

            // Re-used the input json
            dropJson.put(StorageConstants.SQL_STRING_KEY, dropSqlQuery);

        } catch (JSONException e) {
            LOGGER.error("Error while building drop sql", e);
            throw new StorageException("Error while building drop sql", e);
        } catch (NullPointerException e) {
            LOGGER.error("Error while building drop sql", e);
            throw new StorageException("Error while building drop sql", e);
        }

        return dropJson;
    }

    /***
     * Method to fetch the create statement from existing statements and format it based on schema_name
     */
    @Override
    public JSONObject getCreateQuery(JSONObject createJson) throws StorageException {
        try {
            String schema_name, query_name;
            if (createJson.has(StorageConstants.SCHEMA_NAME)) {
                schema_name = createJson.getString(StorageConstants.SCHEMA_NAME);
            } else {
                LOGGER.error("Error Got null schema_name while building create sql");
                throw new StorageException("Got null schema_name");
            }
            if (createJson.has(StorageConstants.QUERY_NAME)) {
                query_name = createJson.getString(StorageConstants.QUERY_NAME);
            } else {
                LOGGER.error("Error Got null QUERY_NAME while  building create sql");
                throw new StorageException("Got null QUERY_NAME");
            }
            String query = storageQueries.getQuery(query_name);
            query = String.format(query, schema_name, schema_name);
            createJson.put(StorageConstants.SQL_STRING_KEY, query);
        } catch (JSONException e) {
            throw new StorageException("Error while builiding create query", e);
        }

        return createJson;
    }

    @Override
    public JSONObject getSelectQuery(JSONObject queryJson) throws StorageException {
        // Note - queryJson may have skip and limit as well
        try {
            String schema_name, query_name;
            if (queryJson.has(StorageConstants.SCHEMA_NAME)) {
                schema_name = queryJson.getString(StorageConstants.SCHEMA_NAME);
            } else {
                LOGGER.error("Error Got null schema_name while building select sql");
                throw new StorageException("Got null schema_name");
            }
            if (queryJson.has(StorageConstants.QUERY_NAME)) {
                query_name = queryJson.getString(StorageConstants.QUERY_NAME);
            } else {
                LOGGER.error("Error Got null QUERY_NAME while  building select sql");
                throw new StorageException("Got null QUERY_NAME");
            }
            String query = storageQueries.getQuery(query_name);
            if (queryJson.has(StorageConstants.FORMAT_STRINGS) && !queryJson.getJSONArray(StorageConstants.FORMAT_STRINGS).isEmpty()) {
                JSONArray formats = queryJson.getJSONArray(StorageConstants.FORMAT_STRINGS);
                List<Object> derived_formats = new ArrayList<Object>();
                for (int i = 0; i < formats.size(); i++) {
                    if (formats.get(i).equals(StorageConstants.BUSINESS_AS_OF)) {
                        derived_formats.add(" FOR BUSINESS_TIME AS OF ? ");
                    } else if (formats.get(i).equals(StorageConstants.BUSINESS_AS_OF_CURRENT)) {
                        derived_formats.add(" FOR BUSINESS_TIME AS OF CURRENT TIMESTAMP ");
                    } else if (formats.get(i).equals(StorageConstants.BUSINESS_FROM_TO)) {
                        derived_formats.add(" FOR BUSINESS_TIME FROM ? TO ? ");
                    } else if (formats.get(i).equals(StorageConstants.BUSINESS_BETWEEN)) {
                        derived_formats.add(" FOR BUSINESS_TIME FROM ? TO ? ");
                    } else if (formats.get(i).equals(StorageConstants.BUSINESS_BETWEEN_CURRENT)) {
                        derived_formats
                            .add(" FOR BUSINESS_TIME FROM " + StorageConstants.CURRENT_TIMESTAMP + " TO "
                                + StorageConstants.CURRENT_TIMESTAMP);
                    } else if (formats.get(i).equals(StorageConstants.BUSINESS_BETWEEN_START_CURRENT)) {
                        derived_formats.add(" FOR BUSINESS_TIME FROM " + StorageConstants.CURRENT_TIMESTAMP + " TO ? ");
                    } else if (formats.get(i).equals(StorageConstants.BUSINESS_BETWEEN_END_CURRENT)) {
                        derived_formats.add(" FOR BUSINESS_TIME FROM ? TO " + StorageConstants.CURRENT_TIMESTAMP);
                    } else if (formats.get(i).equals(StorageConstants.TECHNICAL_AS_OF)) {
                        derived_formats.add(" FOR SYSTEM_TIME AS OF ? ");
                    } else if (formats.get(i).equals(StorageConstants.TECHNICAL_AS_OF_CURRENT)) {
                        derived_formats.add(" FOR SYSTEM_TIME AS OF CURRENT TIMESTAMP ");
                    } else if (formats.get(i).equals(StorageConstants.TECHNICAL_FROM_TO)) {
                        derived_formats.add(" FOR SYSTEM_TIME FROM ? TO ? ");
                    } else if (formats.get(i).equals(StorageConstants.TECHNICAL_BETWEEN)) {
                        derived_formats.add(" FOR SYSTEM_TIME FROM ? TO ? ");
                    } else if (formats.get(i).equals(StorageConstants.TECHNICAL_BETWEEN_CURRENT)) {
                        derived_formats
                            .add(" FOR SYSTEM_TIME FROM " + StorageConstants.CURRENT_TIMESTAMP + " TO "
                                + StorageConstants.CURRENT_TIMESTAMP);
                    } else if (formats.get(i).equals(StorageConstants.TECHNICAL_BETWEEN_START_CURRENT)) {
                        derived_formats.add(" FOR SYSTEM_TIME FROM " + StorageConstants.CURRENT_TIMESTAMP + " TO ? ");
                    } else if (formats.get(i).equals(StorageConstants.TECHNICAL_BETWEEN_END_CURRENT)) {
                        derived_formats.add(" FOR SYSTEM_TIME FROM ? TO " + StorageConstants.CURRENT_TIMESTAMP);
                    } else {
                        derived_formats.add(formats.get(i));
                    }
                }
                query = String.format(query, derived_formats.toArray(new Object[derived_formats.size()]));
            } else {
                query = String.format(query, schema_name, schema_name);
            }
            // if order_by is present in queryJson
            if (!queryJson.isNull(StorageConstants.ORDER_BY)) {
                query = query + " ORDER BY " + queryJson.getString(StorageConstants.ORDER_BY);
            }
            // if limit is present in queryJson
            if (!queryJson.isNull(StorageConstants.LIMIT)) {
                if (queryJson.isNull(StorageConstants.ORDER_BY)) {
                    throw new StorageException("Using Limit/Offset without order by.."); // sqlserver throws error.
                }
                String limitOffsetString = null;
                limitOffsetString = " FETCH NEXT " + queryJson.getInt(StorageConstants.LIMIT) + " ROWS ONLY";
//                if (this.dbType.startsWith(StorageConstants.DB_TYPE_MSSQL)) {
//                    limitOffsetString = " FETCH NEXT " + queryJson.getInt(StorageConstants.LIMIT) + " ROWS ONLY";
//                } else if (this.dbType.startsWith(StorageConstants.DB_TYPE_ORCL)) {
//                    limitOffsetString = " FETCH NEXT " + queryJson.getInt(StorageConstants.LIMIT) + " ROWS ONLY";
//                } else {
//                    limitOffsetString = " LIMIT " + queryJson.getInt(StorageConstants.LIMIT);
//                }
                if (!queryJson.isNull(StorageConstants.OFFSET)) {
                    limitOffsetString =
                        " OFFSET " + queryJson.getInt(StorageConstants.OFFSET) + " ROWS " + limitOffsetString;
//                    if (this.dbType.startsWith(StorageConstants.DB_TYPE_MSSQL)) {
//                        limitOffsetString =
//                            " OFFSET " + queryJson.getInt(StorageConstants.OFFSET) + " ROWS " + limitOffsetString;
//                    } else if (this.dbType.startsWith(StorageConstants.DB_TYPE_ORCL)) {
//                        limitOffsetString =
//                            " OFFSET " + queryJson.getInt(StorageConstants.OFFSET) + " ROWS " + limitOffsetString;
//                    } else {
//                        limitOffsetString += " OFFSET " + queryJson.getInt(StorageConstants.OFFSET);
//                    }
                }
                query = query + limitOffsetString;
            }
            queryJson.put(StorageConstants.SQL_STRING_KEY, query);
        } catch (JSONException e) {
            throw new StorageException("Error while builiding select query", e);
        }

        return queryJson;
    }

}
