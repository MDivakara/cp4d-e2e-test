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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.wink.json4j.JSONArray;
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
 * @author shaik.Nawaz
 *
 */
public class InsightsSQLRSFormater implements DBResultSetFormatter<ResultSet> {

    private final static Logger LOGGER = LoggerFactory.getLogger(InsightsSQLRSFormater.class);

    @SuppressWarnings("unused")
    private SQLDBStore sqldbStore = null;

    public static final String DEFAULT_JSON_ROWS = "rows";
    public static final String FORMAT_DEFAULT_JSON = "format_default_json";
    public static final String FORMAT_GROUP_ARRAY_JSON = "format_group_array_json";
    public static final String FORMAT_TASK_BY_STATUS = "format_task_by_status";
    public static final String FORMAT_DRD_DATA = "format_drd_data";
//    public static final String FORMAT_DRD_STATUS = "format_drd_status";
    public static final String FORMAT_DRD_EXISTS = "format_drd_exisits";
//    public static final String FORMAT_DRD_CONNECTION_DETAILS = "format_drd_connection_details";
    public static final String FORMAT_DRD_LIST = "format_drd_list";
    public static final String FORMAT_DRD_NAME = "foramt_drd_name";
    public static final String FORMAT_DRD_ASSET_AND_REQUEST_INFO = "foramt_drd_asset_and_req_info";
    public static final String FORMAT_DRD_TASK_ID_GIVEN_DRD_ID = "format_drd_task_id_given_drd_id";

    public static final String FORMAT_MODEL_STREAM = "format_model_stream";

    public static final String FORMAT_COLUMN_ARRAY_JSON = "format_column_array_json";
    public static final String FORMAT_GROUP_COLUMN_ARRAY_JSON = "format_group_column_array_json";

    public InsightsSQLRSFormater(DBStore dbStore) {
        this.sqldbStore = (SQLDBStore) dbStore;
    }

    public JSONObject format(ResultSet rs, JSONObject executeJson) throws StorageException {
        JSONObject result = new JSONObject();
        try {
            switch (executeJson.getString(StorageConstants.FORMAT_RESULTS)) {
                case FORMAT_DEFAULT_JSON:
                    result = getDefaultJsonFormat(rs);
                    break;
                case FORMAT_DRD_DATA:
                    result = getDrdDataFormat(rs);
                    break;
//                case FORMAT_DRD_STATUS:
//                  result = getDrdStatusFormat(rs);
//                  break;
                case FORMAT_DRD_EXISTS:
                    result = checkDRDExists(rs);
                    break;
//                case FORMAT_DRD_CONNECTION_DETAILS:
//                  result = getDRDConnectionDetails(rs);
//                  break;
                case FORMAT_DRD_LIST:
                    result = getDRDList(rs);
                    break;
                case FORMAT_DRD_NAME:
                    result = getDRDIDIfNameExists(rs);
                    break;
                case FORMAT_DRD_ASSET_AND_REQUEST_INFO:
                    result = getAssetInfo(rs);
                    break;
                case FORMAT_DRD_TASK_ID_GIVEN_DRD_ID:
                    result = getTaskIDGivenDrdID(rs);
                    break;
                case FORMAT_GROUP_ARRAY_JSON:
                    result = getGroupJsonArray(rs);
                    break;
                case FORMAT_COLUMN_ARRAY_JSON:
                    result = getGroupColsJsonArray(rs);
                    break;
                case FORMAT_GROUP_COLUMN_ARRAY_JSON:
                    result = getGroupColumnsJsonArray(rs);
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
        try {
            if (executeJson.getString(StorageConstants.FORMAT_RESULTS).equals(FORMAT_MODEL_STREAM) && rs.next()) {
                return rs.getBlob(1).getBinaryStream();
            } else {
                throw new StorageException("Unable to find model for given model_id and version_id");
            }
        } catch (JSONException | SQLException e) {
            throw new StorageException("Error while executeQuery", e);
        }
    }
  
    private JSONObject getGroupColumnsJsonArray(ResultSet rs) throws SQLException, JSONException {
        String ref_col_id = null;
        int num_datasets = 0;
        int num_cols = 0;
        int min_sim_score = 0;
        int max_sim_score = 0;
        int total_count = 0;
        String state = null;
        JSONArray columns = new JSONArray();
        while (rs.next()) {
            num_cols = rs.getInt(2);
            num_datasets = rs.getInt(3);
            ref_col_id = rs.getString(5);
            min_sim_score = rs.getInt(7);
            max_sim_score = rs.getInt(8);
            state = rs.getString(10);
            total_count = rs.getInt(11);
            JSONObject jsonObject = new JSONObject(rs.getString(9));
            double similarity = jsonObject.getDouble(Constants.SIMILARITY);
            if(!jsonObject.isNull(Constants.QUALITY_SCORE)) {
                String quality_score = jsonObject.getString(Constants.QUALITY_SCORE);
                jsonObject.put(Constants.QUALITY_SCORE, (int) (Double.parseDouble(quality_score)*100) );
            }
            jsonObject.put(Constants.SIMILARITY, (int)(similarity*100) );
            columns.add(jsonObject);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.NUM_OF_COLUMNS, num_cols);
        jsonObject.put(Constants.NUM_DATASETS, num_datasets);
        jsonObject.put(Constants.MIN_SIM_SCORE, min_sim_score);
        jsonObject.put(Constants.MAX_SIM_SCORE, max_sim_score);
        jsonObject.put(Constants.STATE, state);
        jsonObject.put(Constants.REFERENCE_COLUMN_ID, ref_col_id);
        jsonObject.put(Constants.COLUMNS, columns);
        jsonObject.put(Constants.TOTAL_COUNT, total_count);

        return new JSONObject().put(DEFAULT_JSON_ROWS, jsonObject);
    }

    private JSONObject getGroupColsJsonArray(ResultSet rs) throws SQLException, JSONException {
        String state = null;
        String group_id = null;
        String ref_col_name = null;
        String ref_col_id = null;
        String ref_asset_id = null;
        int num_datasets = 0;
        int num_cols = 0;
        int min_sim_score = 0;
        int max_sim_score = 0;
        JSONArray columns = new JSONArray();
        while (rs.next()) {
            state = rs.getString(1);
            ref_col_id = rs.getString(3);
            columns.add(new JSONObject(rs.getString(2)));
            group_id = rs.getString(4);
            ref_col_name = rs.getString(5);
            ref_asset_id = rs.getString(6);
            num_datasets = rs.getInt(7);
            num_cols = rs.getInt(8);
            min_sim_score = rs.getInt(9);
            max_sim_score = rs.getInt(10);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.STATE, state);
        jsonObject.put(Constants.REFERENCE_COLUMN_ID, ref_col_id);
        jsonObject.put(Constants.COLUMNS, columns);
        jsonObject.put(Constants.GROUP_ID, group_id);
        jsonObject.put(Constants.REFERENCE_COLUMN_NAME, ref_col_name);
        jsonObject.put(Constants.REFERENCE_ASSET_ID, ref_asset_id);
        jsonObject.put(Constants.NUM_DATASETS, num_datasets);
        jsonObject.put(Constants.NUM_COLS, num_cols);
        jsonObject.put(Constants.MIN_SIM_SCORE, min_sim_score);
        jsonObject.put(Constants.MAX_SIM_SCORE, max_sim_score);

        // rows.add(jsonObject);
        return new JSONObject().put(DEFAULT_JSON_ROWS, jsonObject);
    }

    private JSONObject getGroupJsonArray(ResultSet rs)
            throws SQLException, JSONException, NullPointerException, IOException, StorageException {
        JSONArray rows = new JSONArray();
        while (rs.next()) {
            rows.add(rs.getString(1));
        }
        return new JSONObject().put(DEFAULT_JSON_ROWS, rows);
    }

    private JSONObject getDefaultJsonFormat(ResultSet rs)
            throws SQLException, JSONException, NullPointerException, IOException, StorageException {
        ResultSetMetaData rsmd = rs.getMetaData();
        JSONArray rows = new JSONArray();
        while (rs.next()) {
            JSONObject result = new JSONObject();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                result.put(rsmd.getColumnLabel(i).toLowerCase(),
                        SQLDBHelper.getValueFromResultSet(rsmd.getColumnType(i), rs, i));
            }
            rows.put(result);
        }
        return new JSONObject().put(DEFAULT_JSON_ROWS, rows);
    }

    private JSONObject getDrdDataFormat(ResultSet rs)
            throws SQLException, JSONException, NullPointerException, IOException, StorageException {
        JSONObject result = new JSONObject();
        JSONObject metadata = new JSONObject();
        JSONObject entity = new JSONObject();
        while (rs.next()) {
            // result.put("DRD_ID", rs.getString("DRD_ID"));
            metadata.put(Constants.CREATED_BY, rs.getString(1));
            metadata.put(Constants.CREATED_AT, rs.getString(2));
            metadata.put(Constants.DRD_ID, rs.getString(3));
            entity.put(Constants.DRD_NAME, rs.getString(4));
            entity.put(Constants.MODEL_ID, rs.getString(5));
        }
        result.put(Constants.METADATA, metadata);
        result.put(Constants.ENTITY, entity);

        return result;
    }
    
//    private JSONObject getDrdStatusFormat(ResultSet rs) throws SQLException, JSONException {
//      JSONObject result = new JSONObject();
//        while(rs.next()) {
//         result.put(Constants.STATUS, rs.getString("STATUS_MESSAGE"));
//         result.put(Constants.DRD_ID, rs.getString("DRD_ID"));
//        }
//  return result;
//    }
    
    private JSONObject getDRDList(ResultSet rs) throws SQLException, JSONException {
        JSONArray resources = new JSONArray();
        JSONObject result = new JSONObject();
        while(rs.next()) {
            JSONObject resource = new JSONObject();
            resource.put(Constants.DRD_ID, rs.getString(1));
            resource.put(Constants.DRD_NAME, rs.getString(2));
            resource.put(Constants.MODEL_ID, rs.getString(3));
            resources.put(resource);
        }
        result.put(Constants.RESOURCES, resources);
    return result;
    }
    
    private JSONObject checkDRDExists(ResultSet rs) throws SQLException, JSONException {
        JSONObject result = new JSONObject();
        while(rs.next()) {
            int rev_no = rs.getInt(1);
            int size = rs.getInt(2);
            if(size != 0 ) {
                result.put(Constants.REV_NO, rev_no);
                result.put(Constants.DRD_EXISTS, true);
            }
        }
    return result;
    }
    
//    private JSONObject getDRDConnectionDetails(ResultSet rs) throws SQLException, JSONException {
//      JSONObject result = new JSONObject();
//        while(rs.next()) {
//         result.put("connection_details", rs.getString("REQUEST_INFO"));
//        }
//  return result;
//    }
    
    private JSONObject getAssetInfo(ResultSet rs) throws SQLException, JSONException {
        JSONArray result = new JSONArray();
        while(rs.next()) {
            JSONObject res = new JSONObject();
            res.put(Constants.REQUEST_INFO, rs.getString(1));
            result.add(res);
        }
        return new JSONObject().put("result",result);
    }
    
    private JSONObject getDRDIDIfNameExists(ResultSet rs) throws JSONException, SQLException {
        JSONObject result = new JSONObject();
        while(rs.next()) {
            result.put("drdName", rs.getString(1));
            result.put("drdID", rs.getString(2));
        }
        return result;
    }
    
    private JSONObject getTaskIDGivenDrdID(ResultSet rs) throws SQLException, JSONException {
        JSONObject result = new JSONObject();
        while(rs.next()) {
            result.put("task_id", rs.getString(1));
        }
        return result;
    }

}