/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.store.db.sql;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.mit.gov.ms.store.StorageConstants;
import com.mit.gov.ms.store.StorageException;
import com.mit.gov.ms.store.StorageUtils;
import com.mit.gov.ms.store.api.DBResultSetFormatter;
import com.mit.gov.ms.store.api.DBStore;
import com.mit.gov.ms.store.api.QueryProvider;
import com.mit.gov.ms.store.queries.SQLQueries;
import com.mit.gov.ms.store.queries.SQLQueryProvider;
import com.mit.gov.ms.store.schema.StorageSchema;

/**
 * @author Shaik.Nawaz
 *
 */
public class SQLDBStore extends DBStore {

    private final static Logger LOGGER = LoggerFactory.getLogger(SQLDBStore.class);

    private StorageSchema storageSchema = null;
    private QueryProvider queryProvider = null;

    private DataSource dataSource = null;
    private HashMap<String, Connection> dataBaseConn = new HashMap<String, Connection>();

    public SQLDBStore(String dbType, DataSource dataSource, StorageSchema storageSchema, SQLQueries storageQueries)
            throws StorageException {
        super(dbType);
        this.dataSource = dataSource;
        if (this.dbType == null || this.dataSource == null) {
            throw new StorageException("DBType / Datasource is missing for " + SQLDBStore.class.getSimpleName());
        }
        this.storageSchema = storageSchema;
        this.queryProvider = new SQLQueryProvider(storageQueries, dbType);
    }

    /**
     * Get StorageSchema Instance used by current implementation of DBStore
     * 
     * @return StorageSchema Instance used by current implementation of DBStore
     */
    public StorageSchema getStorageSchema() {
        return this.storageSchema;
    }

    /**
     * Get QueryProvider Instance used by current implementation of DBStore
     * 
     * @return QueryProvider Instance used by current implementation of DBStore
     */
    public QueryProvider getQueryProvider() {
        return this.queryProvider;
    }

    private synchronized String getConnection() throws SQLException {
        String connname = UUID.randomUUID().toString();
        long st = System.nanoTime();
        Connection dbConn = this.dataSource.getConnection();
        LOGGER.debug("Time taken to get connectionID=" + connname + " => "
                + TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - st) + " micros "
                + dbConn.getClientInfo("ApplicationName"));
        dbConn.setAutoCommit(true);
        dataBaseConn.put(connname, dbConn);
        return connname;
    }

    @Override
    public String beginTransaction() throws StorageException {
        String connectionId;
        try {
            connectionId = getConnection();
            dataBaseConn.get(connectionId).setAutoCommit(false);
            LOGGER.debug("Begin Transaction - " + connectionId);
            return connectionId;
        } catch (SQLException e) {
            throw new StorageException("Error while beginTransaction", e);
        }
    }

    @Override
    public void commitTransaction(String connectionId) throws StorageException {
        if (connectionId != null) {
            if (dataBaseConn.get(connectionId) == null) {
                return;
            }
            try {
                dataBaseConn.get(connectionId).commit();
                LOGGER.debug("Commit Transaction - " + connectionId);
                // Implicitly close connection
                closeConnection(dataBaseConn.get(connectionId));
            } catch (SQLException e) {
                throw new StorageException("Error while commitTransaction", e);
            }
        }
    }

    @Override
    public void rollBackTransaction(String connectionId) throws StorageException {
        if (connectionId != null) {
            if (dataBaseConn.get(connectionId) == null) {
                return;
            }
            rollback(dataBaseConn.get(connectionId));
            LOGGER.warn("RollBack Transaction - " + connectionId);
            // Implicitly close connection
            closeConnection(dataBaseConn.get(connectionId));
        }
    }

    @Override
    public int getActiveConnection() {
        return dataBaseConn.size();
    }

    protected void closeConnection(String connectionId) {
        if (connectionId != null) {
            closeConnection(dataBaseConn.get(connectionId));
            LOGGER.debug("Closed Connection=" + connectionId);
            dataBaseConn.remove(connectionId);
        }
    }

    @Override
    public JSONObject getDBStatus(String dbName) throws StorageException {
        try {
            JSONObject status = new JSONObject();
            JSONObject statusQuery = this.getQueryProvider().getDBVerifyQuery();
            status.put(StorageConstants.DEPENDENCY_STATUS, StorageConstants.NOT_OK);
            // status.put(StorageConstants.DATASOURCE_USED, dataSourceUsed);
            if (statusQuery != null && !statusQuery.isEmpty() && statusQuery.has(StorageConstants.SQL_STRING_KEY)) {
                PreparedStatement ps = null;
                String connectionId = null;
                try {
                    connectionId = getConnection();
                    status.put(StorageConstants.DB_TYPE,
                            dataBaseConn.get(connectionId).getMetaData().getDatabaseProductName());
                    if (this.getDbType().equalsIgnoreCase(StorageConstants.DB_TYPE_DB2)) {
                        status.put(StorageConstants.SCHEMA, dataBaseConn.get(connectionId).getSchema().trim());
                    }
                    ps = dataBaseConn.get(connectionId)
                            .prepareStatement(statusQuery.getString(StorageConstants.SQL_STRING_KEY));
                    ps.executeQuery();
                    status.put(StorageConstants.DEPENDENCY_STATUS, StorageConstants.OK);
                } catch (SQLException e) {
                    status.put(StorageConstants.ERROR, e.getMessage());
                    status.put(StorageConstants.ERROR_STACK, Arrays.toString(e.getStackTrace()));
                    LOGGER.error("Error while getDBStatus", e);
                } finally {
                    closeStatement(ps);
                    closeConnection(connectionId);
                }
            } else {
                status.put(StorageConstants.ERROR, "Could not get query for DB status");
            }
            return status;
        } catch (JSONException e) {
            throw new StorageException("Error while getDBStatus", e);
        }
    }

    protected void closeStatement(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                LOGGER.error("Error while closeStatement", e);
            }
        }
    }

    private void closeConnection(Connection dbConn) {
        if (dbConn != null) {
            try {
                dbConn.close();
            } catch (SQLException e) {
                LOGGER.error("Error while closeConnection", e);
            }
        }
    }

    private void rollback(Connection dbConn) {
        if (dbConn != null) {
            try {
                dbConn.rollback();
            } catch (SQLException e) {
                LOGGER.error("Error while rollback", e);
            }
        }
    }

    @Override
    public void initSchema(String schema_name, int newVersion) throws StorageException {

        JSONObject checkSchemaJson = this.getStorageSchema().getSchemaVersionQuery(schema_name);
        if (!checkSchemaJson.has(StorageConstants.SQL_STRING_KEY)) {
            throw new StorageException("Could not get Schema state details");
        }

        String connectionId = null;
        boolean upgradeSchema = false;
        try {
            int currVersion = newVersion;
            JSONObject state = executeQuery(checkSchemaJson, null);
            if (state != null && !state.has(StorageConstants.ERROR)) {
                LOGGER.info(state.toString());
                currVersion = state.getJSONObject(StorageConstants.SQL_RESULT).getJSONArray(StorageConstants.ROWS)
                        .getJSONObject(0).getInt("version");
                if (newVersion == currVersion) {
                    LOGGER.info("Schema already initialized - " + schema_name + " - " + state.toString());
                    return;
                } else {
                    upgradeSchema = true;
                }
            } else if (!state.has(StorageConstants.ERROR_CODE) || state.getInt(StorageConstants.ERROR_CODE) != this.getStorageSchema().getNotFoundCode()) {
                // -204 = table does not exist, so don't throw error, create schema tables
                LOGGER.info(state.toString());
                throw new StorageException(state.getString(StorageConstants.ERROR));
            }

            JSONObject initSchemaJson = null;
            String message = "Initializing Schema - " + schema_name;
            if (!upgradeSchema) {
                initSchemaJson = this.getStorageSchema().getInitSchemaStatements(schema_name);
            } else {
                message = "Upgrading Schema - " + schema_name;
                initSchemaJson = this.getStorageSchema().getIncrimentalUpgradeStatements(schema_name, currVersion,
                        newVersion);
            }
            if (!initSchemaJson.has(StorageConstants.INIT_SCHEMA_SQL_STRING_KEY)) {
                throw new StorageException("Could not get Schema details");
            }

            JSONArray createStatements = initSchemaJson.getJSONArray(StorageConstants.INIT_SCHEMA_SQL_STRING_KEY);
            LOGGER.info(message);

            connectionId = beginTransaction();
            for (int i = 0; i < createStatements.length(); i++) {
                LOGGER.info(createStatements.getJSONObject(i).toString());
                JSONObject status = create(createStatements.getJSONObject(i));
                LOGGER.info(status.toString());
                // if (status != null && status.has(StorageConstants.ERROR)) {
                // LOGGER.error(status.toString());
                // throw new StorageException(status.getString(StorageConstants.ERROR));
                // }
            }
            commitTransaction(connectionId);
            connectionId = null;
            LOGGER.info("Schema created for schema=" + schema_name);
        } catch (JSONException e) {
            throw new StorageException("Could not get Schema details", e);
        } finally {
            if (connectionId != null) {
                rollBackTransaction(connectionId);
            }
        }
    }

    private JSONObject executeUpdate(JSONObject updateSQL) throws StorageException {
        try {
            LOGGER.debug(updateSQL.toString());
            String sqlString = updateSQL.getString(StorageConstants.SQL_STRING_KEY);
            String connectionId = updateSQL.has("CONNECTION_ID") ? updateSQL.getString("CONNECTION_ID") : null;
            JSONObject status = new JSONObject();
            PreparedStatement stmt = null;
            boolean closeConnection = false;
            try {
                if (connectionId == null) {
                    connectionId = getConnection();
                    closeConnection = true;
                }
                stmt = dataBaseConn.get(connectionId).prepareStatement(sqlString);
                if (updateSQL.has(StorageConstants.PARAM_ARRAY)) {
                    JSONArray param_array = updateSQL.getJSONArray(StorageConstants.PARAM_ARRAY);
                    setParamValues(connectionId, stmt, param_array);
                }

                int row_count = stmt.executeUpdate();
                status.put(StorageConstants.SQL_RESULT, row_count);
            } catch (SQLException e) {
                if (!updateSQL.has(StorageConstants.EXPECTED_ERROR_CODE)
                        || updateSQL.getInt(StorageConstants.EXPECTED_ERROR_CODE) != e.getErrorCode()) {
                    LOGGER.error("Error while executeUpdate", e);
                    // Throw Error if not as expected
                    throw new StorageException("Error while executeUpdate", e);
                }
                status.put(StorageConstants.ERROR, e.getMessage());
                status.put(StorageConstants.ERROR_CODE, e.getErrorCode());
                status.put(StorageConstants.ERROR_STACK, Arrays.toString(e.getStackTrace()));
            } finally {
                closeStatement(stmt);
                if (closeConnection) {
                    // If this method initialized the connection, it should close as well
                    closeConnection(connectionId);
                }
            }
            return status;
        } catch (JSONException e) {
            throw new StorageException("Error while executeUpdate", e);
        }
    }

    @Override
    public JSONObject insert(JSONObject insertJson) throws StorageException {
        JSONObject status = new JSONObject();
        JSONObject insertSQL = null;
        if (insertJson != null && !insertJson.isEmpty() && insertJson.has(StorageConstants.SQL_STRING_KEY)) {
            insertSQL = insertJson;
        } else {
            insertSQL = this.getQueryProvider().getInsertQuery(insertJson);
        }
        if (insertSQL != null && !insertSQL.isEmpty() && insertSQL.has(StorageConstants.SQL_STRING_KEY)) {
            status = executeUpdate(insertSQL);
        } else {
            try {
                status.put(StorageConstants.ERROR, "Could not get insert SQLstring");
            } catch (JSONException e) {
                throw new StorageException("Error while insert", e);
            }
        }
        return status;
    }

    @Override
    public JSONObject update(JSONObject updateJson) throws StorageException {
        JSONObject status = new JSONObject();
        JSONObject updateSQL = null;
        if (updateJson != null && !updateJson.isEmpty() && updateJson.has(StorageConstants.SQL_STRING_KEY)) {
            updateSQL = updateJson;
        } else {
            updateSQL = this.getQueryProvider().getUpdateQuery(updateJson);
        }
        if (updateSQL != null && !updateSQL.isEmpty() && updateSQL.has(StorageConstants.SQL_STRING_KEY)) {
            status = executeUpdate(updateSQL);
        } else {
            try {
                status.put(StorageConstants.ERROR, "Could not get update SQLstring");
            } catch (JSONException e) {
                throw new StorageException("Error while update", e);
            }
        }
        return status;
    }

    @Override
    public JSONObject delete(JSONObject deleteJson) throws StorageException {
        JSONObject status = new JSONObject();
        JSONObject deleteSQL = null;
        if (deleteJson != null && !deleteJson.isEmpty() && deleteJson.has(StorageConstants.SQL_STRING_KEY)) {
            deleteSQL = deleteJson;
        } else {
            deleteSQL = this.getQueryProvider().getDeleteQuery(deleteJson);
        }
        if (deleteSQL != null && !deleteSQL.isEmpty() && deleteSQL.has(StorageConstants.SQL_STRING_KEY)) {
            status = executeUpdate(deleteSQL);
        } else {
            try {
                status.put(StorageConstants.ERROR, "Could not get delete SQLstring");
            } catch (JSONException e) {
                throw new StorageException("Error while delete", e);
            }
        }
        return status;
    }

    @Override
    public JSONObject create(JSONObject createJson) throws StorageException {
        JSONObject status = new JSONObject();
        JSONObject createSQL = null;
        if (createJson != null && !createJson.isEmpty() && createJson.has(StorageConstants.SQL_STRING_KEY)) {
            createSQL = createJson;
        } else {
            createSQL = this.getQueryProvider().getCreateQuery(createJson);
        }
        if (createSQL != null && !createSQL.isEmpty() && createSQL.has(StorageConstants.SQL_STRING_KEY)) {
            status = executeUpdate(createSQL);
        } else {
            try {
                status.put(StorageConstants.ERROR, "Could not get create SQLstring");
            } catch (JSONException e) {
                throw new StorageException("Error while create", e);
            }
        }
        return status;
    }

    @Override
    public JSONObject drop(JSONObject dropJson) throws StorageException {
        JSONObject status = new JSONObject();
        JSONObject dropSQL = null;
        if (dropJson != null && !dropJson.isEmpty() && dropJson.has(StorageConstants.SQL_STRING_KEY)) {
            dropSQL = dropJson;
        } else {
            dropSQL = this.getQueryProvider().getDropQuery(dropJson);
        }
        if (dropSQL != null && !dropSQL.isEmpty() && dropSQL.has(StorageConstants.SQL_STRING_KEY)) {
            status = executeUpdate(dropSQL);
        } else {
            try {
                status.put(StorageConstants.ERROR, "Could not get create SQLstring");
            } catch (JSONException e) {
                throw new StorageException("Error while create", e);
            }
        }
        return status;
    }

    @SuppressWarnings("unchecked")
    @Override
    public InputStream executeQueryAndFetchAsStream(JSONObject executeJson,
            @SuppressWarnings("rawtypes") DBResultSetFormatter formatter) throws StorageException {
        JSONObject querySQL = null;
        if (executeJson != null && !executeJson.isEmpty() && executeJson.has(StorageConstants.SQL_STRING_KEY)) {
            querySQL = executeJson;
        } else {
            querySQL = this.getQueryProvider().getSelectQuery(executeJson);
        }
        if (querySQL != null && !querySQL.isEmpty() && querySQL.has(StorageConstants.SQL_STRING_KEY)) {
            LOGGER.debug(querySQL.toString());
            PreparedStatement ps = null;
            String connectionId = null;
            try {
                connectionId = getConnection();
                PreparedStatement stmt = dataBaseConn.get(connectionId)
                        .prepareStatement(querySQL.getString(StorageConstants.SQL_STRING_KEY));
                if (querySQL.has(StorageConstants.PARAM_ARRAY)) {
                    JSONArray param_array = querySQL.getJSONArray(StorageConstants.PARAM_ARRAY);
                    setParamValues(connectionId, stmt, param_array);
                }

                ResultSet rs = stmt.executeQuery();
                if (rs == null) {
                    throw new StorageException("Got null Resultset");
                }
                String result = "";
                if (!querySQL.has(StorageConstants.FORMAT_RESULTS) || formatter == null) {
                    result = getCSVFromResultSet(rs);
                    return new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
                } else {
                    return formatter.formatAsStream(rs, querySQL);
                }
            } catch (SQLException e) {
                // Always Throw Error when error
                throw new StorageException("Error while executeQueryAndFetchAsCSVStream", e);
            } catch (JSONException | IOException e1) {
                throw new StorageException("Error while executeQueryAndFetchAsCSVStream", e1);
            } finally {
                closeStatement(ps);
                closeConnection(connectionId);
            }
        } else {
            throw new StorageException("Could not get the requested query");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject executeQuery(JSONObject executeJson, @SuppressWarnings("rawtypes") DBResultSetFormatter formatter)
            throws StorageException {
        JSONObject status = new JSONObject();
        JSONObject querySQL = null;
        if (executeJson != null && !executeJson.isEmpty() && executeJson.has(StorageConstants.SQL_STRING_KEY)) {
            querySQL = executeJson;
        } else {
            querySQL = this.getQueryProvider().getSelectQuery(executeJson);
        }
        if (querySQL != null && !querySQL.isEmpty() && querySQL.has(StorageConstants.SQL_STRING_KEY)) {
            LOGGER.info(querySQL.toString());
            PreparedStatement ps = null;
            String connectionId = null;
            try {
                connectionId = getConnection();
                PreparedStatement stmt = dataBaseConn.get(connectionId)
                        .prepareStatement(querySQL.getString(StorageConstants.SQL_STRING_KEY));
                if (querySQL.has(StorageConstants.PARAM_ARRAY)) {
                    JSONArray param_array = querySQL.getJSONArray(StorageConstants.PARAM_ARRAY);
                    setParamValues(connectionId, stmt, param_array);
                }

                ResultSet rs = stmt.executeQuery();
                if (rs == null) {
                    throw new SQLException("Got null Resultset");
                }
                JSONObject result = new JSONObject();
                if (querySQL.has(StorageConstants.FORMAT_RESULTS)
                        && querySQL.get(StorageConstants.FORMAT_RESULTS) != null && formatter != null) {
                    result = formatter.format(rs, querySQL);
                    status.put(StorageConstants.SQL_RESULT, result);
                } else {
                    result.put(StorageConstants.COLUMNS, getMetaDataFromResultSet(rs));
                    result.put(StorageConstants.ROWS, getRowsFromResultSet(rs));
                    status.put(StorageConstants.SQL_RESULT, result);
                }
            } catch (SQLException e) {
                try {
                    if (!querySQL.has(StorageConstants.EXPECTED_ERROR_CODE)
                            || querySQL.getInt(StorageConstants.EXPECTED_ERROR_CODE) != e.getErrorCode()) {
                        LOGGER.error("Error while executeQuery, Error Code=" + e.getErrorCode(), e);
                        // Throw Error if not as expected
                        throw new StorageException("Error while executeQuery", e);
                    }
                    status.put(StorageConstants.ERROR, e.toString());
                    status.put(StorageConstants.ERROR_CODE, e.getErrorCode());
                    status.put(StorageConstants.ERROR_STACK, Arrays.toString(e.getStackTrace()));
                } catch (JSONException e1) {
                    throw new StorageException("Error while executeQuery", e1);
                }
            } catch (JSONException | IOException e1) {
                throw new StorageException("Error while executeQuery", e1);
            } finally {
                closeStatement(ps);
                closeConnection(connectionId);
            }
        } else {
            try {
                status.put(StorageConstants.ERROR, "Could not get query SQLstring");
            } catch (JSONException e) {
                throw new StorageException("Error while executeQuery", e);
            }
        }
        return status;
    }

    private void setParamValues(String connectionID, PreparedStatement stmt, JSONArray param_array) throws JSONException, SQLException {
        for (int parameterIndex = 1, paramValueindex = 0; paramValueindex < param_array
                .size(); parameterIndex++, paramValueindex++) {
            JSONObject param = param_array.getJSONObject(paramValueindex);
            String param_type = param.getString(StorageConstants.PARAM_TYPE);
            switch (param_type) {
            case StorageConstants.STRING:
                if (SQLDBHelper.isNullParam(param)) {
                    stmt.setNull(parameterIndex, Types.VARCHAR);
                } else {
                    stmt.setString(parameterIndex, SQLDBHelper.getParamString(param));
                }
                break;
            case StorageConstants.INT:
                if (SQLDBHelper.isNullParam(param)) {
                    stmt.setNull(parameterIndex, Types.INTEGER);
                } else {
                    stmt.setInt(parameterIndex, SQLDBHelper.getParamInt(param));
                }
                break;
            case StorageConstants.LONG:
                if (SQLDBHelper.isNullParam(param)) {
                    stmt.setNull(parameterIndex, Types.INTEGER);
                } else {
                    stmt.setLong(parameterIndex, SQLDBHelper.getParamLong(param));
                }
                break;
            case StorageConstants.DOUBLE:
                if (SQLDBHelper.isNullParam(param)) {
                    stmt.setNull(parameterIndex, Types.DOUBLE);
                } else {
                    stmt.setDouble(parameterIndex, SQLDBHelper.getParamDouble(param));
                }
                break;
            case StorageConstants.TIMESTAMP:
                if (SQLDBHelper.isNullParam(param)) {
                    stmt.setNull(parameterIndex, Types.TIMESTAMP);
                } else if (SQLDBHelper.isCurrentTS(param)) {
                    parameterIndex--; // No placeholder (/?) was passed for insert but param value is passed
                    // Do nothing , expect Query builder has already added CURRENT_TIMESTAMP
                    // and no need of setParam.
                } else {
                    stmt.setTimestamp(parameterIndex, SQLDBHelper.getParamTimestamp(param));
                }
                break;
            case StorageConstants.BLOB:
                if (SQLDBHelper.isNullParam(param)) {
                    stmt.setNull(parameterIndex, Types.BLOB);
                } else {
                    stmt.setBlob(parameterIndex, SQLDBHelper.getParamBlob(param));
                }
                break;
            case StorageConstants.CLOB:
                if (SQLDBHelper.isNullParam(param)) {
                    stmt.setNull(parameterIndex, Types.CLOB);
                } else {
                    Clob clob = null;
                    clob = dataBaseConn.get(connectionID).createClob();
                    clob.setString(0, SQLDBHelper.getParamClob(param));
                    stmt.setClob(parameterIndex, clob);
                }
                break;
            default:
                if (SQLDBHelper.isNullParam(param)) {
                    stmt.setNull(parameterIndex, Types.VARCHAR);
                } else {
                    stmt.setString(parameterIndex, SQLDBHelper.getParamString(param));
                }
                break;
            }
            // TODO - check for more data types for parameter
        }
    }

    private JSONArray getMetaDataFromResultSet(ResultSet rs) throws JSONException, SQLException {
        JSONArray rows = new JSONArray();
        ResultSetMetaData rsmd = rs.getMetaData();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            JSONObject row = new JSONObject();
            row.put(StorageConstants.COLUMN_NAME, rsmd.getColumnLabel(i));
            row.put(StorageConstants.COLUMN_TYPE, rsmd.getColumnTypeName(i));
            rows.put(row);
        }
        return rows;
    }

    private JSONArray getRowsFromResultSet(ResultSet rs)
            throws SQLException, JSONException, StorageException, IOException {
        JSONArray rows = new JSONArray();
        ResultSetMetaData rsmd = rs.getMetaData();
        while (rs.next()) {
            JSONObject row = new JSONObject();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                row.put(rsmd.getColumnLabel(i).toLowerCase(),
                        SQLDBHelper.getValueFromResultSet(rsmd.getColumnType(i), rs, i));
            }
            rows.put(row);
        }
        return rows;
    }

    private String getCSVFromResultSet(ResultSet rs) throws JSONException, SQLException, StorageException, IOException {
        String colHeader = "";
        String sep = "";
        ResultSetMetaData rsmd = rs.getMetaData();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            colHeader += sep + rsmd.getColumnLabel(i);
            sep = ",";
        }

        String rows = colHeader;
        while (rs.next()) {
            String row = "";
            sep = "";
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                switch (rsmd.getColumnType(i)) {
                case Types.TIMESTAMP:
                case Types.TIMESTAMP_WITH_TIMEZONE:
                    row += sep + StorageUtils.getUTCStringFromTimeStamp(rs.getTimestamp(i));
                    break;
                case Types.DATE:
                    row += sep + StorageUtils.getUTCStringFromDate(rs.getDate(i));
                    break;
                case Types.BLOB:
                    row += sep + IOUtils.toString(rs.getBlob(i).getBinaryStream(), StandardCharsets.UTF_8);
                    break;
                case Types.CLOB:
                case Types.NCLOB:
                    row += sep + IOUtils.toString(rs.getClob(i).getCharacterStream());
                    break;
                default:
                    row += sep + rs.getString(i);
                }
            }
            rows += System.lineSeparator() + row;
        }
        return colHeader + rows;
    }

}
