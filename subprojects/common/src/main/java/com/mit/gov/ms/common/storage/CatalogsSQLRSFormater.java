/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.storage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.wink.json4j.JSON;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONArtifact;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.mit.gov.ms.common.Constants;
import com.mit.gov.ms.store.StorageConstants;
import com.mit.gov.ms.store.StorageException;
import com.mit.gov.ms.store.api.DBResultSetFormatter;
import com.mit.gov.ms.store.api.DBStore;
import com.mit.gov.ms.store.db.sql.SQLDBHelper;
import com.mit.gov.ms.store.db.sql.SQLDBStore;

/**
 * @author Shaik.Nawaz
 *
 */
public class CatalogsSQLRSFormater implements DBResultSetFormatter<ResultSet> {

    private final static Logger LOGGER = LoggerFactory.getLogger(CatalogsSQLRSFormater.class);

    @SuppressWarnings("unused")
    private SQLDBStore sqldbStore = null;

    public static final String DEFAULT_JSON_ROWS = "rows";
    public static final String FORMAT_DEFAULT_JSON = "format_default_json";
    public static final String FORMAT_XMETA_CARESULTS_JSON = "format_xmeta_caresults_json";

    public CatalogsSQLRSFormater(DBStore dbStore) {
        this.sqldbStore = (SQLDBStore) dbStore;
    }

    public JSONObject format(ResultSet rs, JSONObject executeJson) throws StorageException {
        JSONObject result = new JSONObject();
        try {
            switch (executeJson.getString(StorageConstants.FORMAT_RESULTS)) {
                case FORMAT_DEFAULT_JSON:
                    result = getDefaultJsonFormat(rs);
                    break;
                case FORMAT_XMETA_CARESULTS_JSON:
                    result = getXmetaCAResultsFormat(rs);
                    break;
            }
        } catch (JSONException | IOException e) {
            throw new StorageException("Error while executeQuery", e);
        } catch (SQLException e) {
            try {
                if (!executeJson.has(StorageConstants.EXPECTED_ERROR_CODE)
                    || executeJson.getInt(StorageConstants.EXPECTED_ERROR_CODE) != e.getErrorCode()) {
                    LOGGER.error("Error while executeQuery", e);
                    // Throw Error if not as expected
                    throw new StorageException("Error while executeQuery", e);
                }
                result.put(StorageConstants.ERROR, e.getMessage());
                result.put(StorageConstants.ERROR_CODE, e.getErrorCode());
                result.put(StorageConstants.ERROR_STACK, Arrays.toString(e.getStackTrace()));
            } catch (JSONException e1) {
                throw new StorageException("Error while executeQuery", e1);
            }
        }
        return result;
    }

    public InputStream formatAsStream(ResultSet rs, JSONObject executeJson) throws StorageException {
        InputStream result = null;
        if (executeJson.has(StorageConstants.INNER_QUERY)) {
        }
        return result;
    }

    private JSONObject getDefaultJsonFormat(ResultSet rs)
        throws SQLException, JSONException, NullPointerException, IOException, StorageException {
        ResultSetMetaData rsmd = rs.getMetaData();
        JSONArray rows = new JSONArray();
        while (rs.next()) {
            JSONObject result = new JSONObject();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                result
                    .put(rsmd.getColumnLabel(i).toLowerCase(),
                        SQLDBHelper.getValueFromResultSet(rsmd.getColumnType(i), rs, i));
            }
            rows.put(result);
        }
        return new JSONObject().put(DEFAULT_JSON_ROWS, rows);
    }
    
    private JSONObject getXmetaCAResultsFormat(ResultSet rs)
            throws SQLException, JSONException, NullPointerException, IOException, StorageException {
        JSONArray rows = new JSONArray();
        while (rs.next()) {
            JSONObject cAResults = new JSONObject();
            cAResults.put(Constants.CA_RESULTS_JSON, getJSONFromClob(rs.getClob(3)));
            JSONObject columnIdentity = new JSONObject();
            columnIdentity.put(Constants.TABLE_RID, rs.getString(1));
            columnIdentity.put(Constants.COLUMN_RID, rs.getString(2));
            columnIdentity.put(Constants.TABLE_NAME, rs.getString(4));
            columnIdentity.put(Constants.COLUMN_NAME, rs.getString(5));
            columnIdentity.put(Constants.DATABASE_NAME, rs.getString(6));
            columnIdentity.put(Constants.SCHEMA_NAME, rs.getString(7));
            columnIdentity.put(Constants.HOST_NAME, rs.getString(8));
            columnIdentity.put(Constants.COLUMN_DESC, rs.getString(9));
            
            columnIdentity.put(Constants.IS_DB_TABLE, rs.getBoolean(11));
            
            columnIdentity.put(Constants.QUALITY_SCORE, rs.getString(13));
            columnIdentity.put(Constants.LAST_IMPORT, rs.getString(14));
            
            cAResults.put(Constants.COLUMN_IDENTITY, columnIdentity);
            rows.put(cAResults);
        }
        return new JSONObject().put(DEFAULT_JSON_ROWS, rows);
    }
    
    public static JSONArtifact getJSONFromClob(Clob clob)
            throws NullPointerException, IOException, SQLException, JSONException {
        if (clob != null) {
            String str = clob.getSubString(1, (int) clob.length());
            JSONArtifact jsonStr = JSON.parse(str);
            if (jsonStr instanceof JSONObject) {
                return ((JSONObject) jsonStr);
            } else {
                return ((JSONArray) jsonStr);
            }
        }
        return null;
    }

}
