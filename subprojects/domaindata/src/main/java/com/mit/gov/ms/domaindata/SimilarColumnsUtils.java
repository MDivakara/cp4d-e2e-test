/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.domaindata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.mit.gov.ms.common.CatalogsMetaDataStore;
import com.mit.gov.ms.common.Constants;
import com.mit.gov.ms.common.HttpUtils;
import com.mit.gov.ms.common.InsightsConfiguration;
import com.mit.gov.ms.common.InsightsException;
import com.mit.gov.ms.common.InsightsMetaDataStore;
import com.mit.gov.ms.common.security.SessionManager;
import com.mit.gov.ms.common.storage.CatalogsSQLRSFormater;
import com.mit.gov.ms.common.storage.InsightsSQLQueries;
import com.mit.gov.ms.common.storage.InsightsSQLRSFormater;
import com.mit.gov.ms.common.storage.InsightsTables;
import com.mit.gov.ms.store.SQLTimestampWrapper;
import com.mit.gov.ms.store.StorageConstants;
import com.mit.gov.ms.store.StorageException;
import com.mit.gov.ms.store.db.sql.SQLDBHelper;

public class SimilarColumnsUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimilarColumnsUtils.class);

    public void persistSCAResults(String tenantId, String customClass, String workspace_rid,
            JSONArray resultantJsonArray, String is_explicit_run, String last_run_by) throws InsightsException, JSONException {

        // check if analysis is already run for workspace_rid
        JSONArray jsonArray = getByWorkspaceId(tenantId, workspace_rid);

        JSONArray results = new JSONArray();
        

        if (jsonArray.isEmpty()) {
            results = resultantJsonArray;
        } else {
            SCANonMetadataUtils scaNonMetdataUtils= new SCANonMetadataUtils();
            results = scaNonMetdataUtils.mergeTwoSCCGroups(jsonArray, resultantJsonArray);
        }
        LOGGER.debug("Existing SCA Groups - " + results);

        // delete saved data from tables sca_groups, sca_group_columns
        deleteSCAGroupsById(tenantId, workspace_rid);
        
        int num_groups = results.size();
        for (int i = 0; i < num_groups; i++) {
            JSONObject jsonObject = new JSONObject();

            try {
                SQLTimestampWrapper now = new SQLTimestampWrapper(StorageConstants.CURRENT_TIMESTAMP);
                JSONObject scaGroupJsObject = results.getJSONObject(i);

                // create json object for group level information
                jsonObject.put(Constants.TENANT_ID, tenantId);
                jsonObject.put(Constants.WKSP_ID, workspace_rid);
                jsonObject.put(Constants.GROUP_ID, scaGroupJsObject.getString(Constants.GROUP_ID));
                jsonObject.put(Constants.REFERENCE_COLUMN_NAME,
                        scaGroupJsObject.getString(Constants.REFERENCE_COLUMN_NAME));
                jsonObject.put(Constants.REFERENCE_COLUMN_ID,
                        scaGroupJsObject.getString(Constants.REFERENCE_COLUMN_ID));
                jsonObject.put(Constants.REFERENCE_ASSET_ID, scaGroupJsObject.getString(Constants.REFERENCE_ASSET_ID));
                jsonObject.put(Constants.NUM_DATASETS,
                        Integer.valueOf(scaGroupJsObject.getString(Constants.NUM_DATASETS)));
                jsonObject.put(Constants.NUM_COLS, Integer.valueOf(scaGroupJsObject.getString(Constants.NUM_COLS)));
                
                if(scaGroupJsObject.has(Constants.MIN_SIMILARITY)) {
                    jsonObject.put(Constants.MIN_SIM_SCORE,
                            (int) (scaGroupJsObject.getDouble(Constants.MIN_SIMILARITY) * 100));
                } else {
                    jsonObject.put(Constants.MIN_SIM_SCORE, (int)scaGroupJsObject.getDouble(Constants.MIN_SIM_SCORE));
                }
               
                if(scaGroupJsObject.has(Constants.MAX_SIMILARITY)) {
                    jsonObject.put(Constants.MAX_SIM_SCORE,
                            (int) (scaGroupJsObject.getDouble(Constants.MAX_SIMILARITY) * 100));
                } else {
                    jsonObject.put(Constants.MAX_SIM_SCORE, (int)scaGroupJsObject.getDouble(Constants.MAX_SIM_SCORE));
                }
                
                jsonObject.put(Constants.STATE, scaGroupJsObject.getString(Constants.STATE));
                jsonObject.put(Constants.IS_EXPLICIT_RUN, is_explicit_run);
                jsonObject.put(Constants.LAST_RUN_AT, now.toString());
                jsonObject.put(Constants.LAST_RUN_BY, last_run_by);

                LOGGER.info("Save SCA Groups - " + jsonObject.toString());
                InsightsMetaDataStore.getInstance().save(InsightsTables.GI_SCA_GROUPS.name(), workspace_rid, jsonObject);
                LOGGER.info("Done SCA Groups - ");

                // create json object for column level information relative to group
                JSONArray colsJSONArray = scaGroupJsObject.getJSONArray(Constants.COLUMNS);
                int length = colsJSONArray.size();

                for (int j = 0; j < length; j++) {
                    JSONObject jsObject = new JSONObject();
                    JSONObject colsJSONObject = colsJSONArray.getJSONObject(j);
                    jsObject.put(Constants.TENANT_ID, tenantId);
                    jsObject.put(Constants.WKSP_ID, workspace_rid);
                    jsObject.put(Constants.GROUP_ID, scaGroupJsObject.getString(Constants.GROUP_ID));
                    jsObject.put(Constants.COL_ID, colsJSONObject.getString(Constants.COLUMN_RID));
                    jsObject.put(Constants.COL_NAME, colsJSONObject.getString(Constants.COLUMN_NAME));
                    jsObject.put(Constants.SIM_SCORE, (int) ( colsJSONObject.getDouble(Constants.SIMILARITY)*100 ));

                    // creating location
                    // "host_name>database_name>schema_name>table_name>column_name" 
                    String location = (colsJSONObject.isNull(Constants.HOST_NAME) ? "" : colsJSONObject.getString(Constants.HOST_NAME)) + Constants.LOCATION_PATH_DELIMITER
                            + (colsJSONObject.isNull(Constants.DATABASE_NAME) ? "" : colsJSONObject.getString(Constants.DATABASE_NAME)) + Constants.LOCATION_PATH_DELIMITER
                            + (colsJSONObject.isNull(Constants.SCHEMA_NAME) ? "" : colsJSONObject.getString(Constants.SCHEMA_NAME)) + Constants.LOCATION_PATH_DELIMITER
                            + (colsJSONObject.isNull(Constants.TABLE_NAME) ? "" : colsJSONObject.getString(Constants.TABLE_NAME)) + Constants.LOCATION_PATH_DELIMITER
                            + (colsJSONObject.isNull(Constants.COLUMN_NAME) ? "" : colsJSONObject.getString(Constants.COLUMN_NAME));

                    if(location.length() > Constants.COL_LOCATION_MAX_LENGTH) {
                        int num_of_chars_to_remove = location.length() - Constants.COL_LOCATION_MAX_LENGTH;
                        location = location.substring(0, location.length() - num_of_chars_to_remove);
                    }
                    colsJSONObject.put(Constants.COL_LOCATION, location);
                    jsObject.put(Constants.COL_LOCATION, location);
                    jsObject.put(Constants.COL_INFO, colsJSONObject.toString());

                    LOGGER.info("Save Column Info Relative to Group - " + jsObject.toString());
                    InsightsMetaDataStore.getInstance().save(InsightsTables.GI_SCA_GROUP_COLUMNS.name(), workspace_rid, jsObject);
                    LOGGER.info("Done Column Info Relative to Group - ");
                }

            } catch (InsightsException | JSONException | StorageException e) {
                //TODO: clean sca groups until transaction support (only when exception occur while saving sca_groups/sca_group_columns)
                deleteSCAGroupsById(tenantId, workspace_rid);
                JSONObject status = getSCAResultsStatus(tenantId, workspace_rid);
                updateSCAResults(tenantId, workspace_rid, "n", 0, status.getInt(Constants.REV_NO));
                String exp_message = "Exception while saving SCA results for workspace_rid=" + workspace_rid;
                LOGGER.error(exp_message, e);
                throw new InsightsException(exp_message, e);
            }

        }
        
        try {
            JSONObject status = getSCAResultsStatus(tenantId, workspace_rid);
            updateSCAResults(tenantId, workspace_rid, "n", num_groups, status.getInt(Constants.REV_NO));
        } catch (JSONException e) {
            String exp_message = "Exception while update status for workspace_id : " + workspace_rid;
            LOGGER.error(exp_message, e);
            throw new InsightsException(exp_message, e);
        }
    }

    /**
     * @param workspace_rid
     * @return JSONArray of all groups which contain column similarity
     * @throws InsightsException
     */
    public JSONArray getByWorkspaceId(String tenantId, String workspace_rid) throws InsightsException {

        String queryIdentifier1 = InsightsSQLQueries.GET_SCA_WORKSPACE_RESULTS_QUERY;
        String queryIdentifier2 = InsightsSQLQueries.GET_SCA_WORKSPACE_RESULTS_GROUP_QUERY;
        JSONArray param_array1 = new JSONArray();
        JSONArray param_array2 = new JSONArray();
        String[] formats = {};

        JSONArray groupArrayForExistingWorkspace = new JSONArray();

        try {
            param_array1.put(SQLDBHelper.createParamString(workspace_rid));
            param_array1.put(SQLDBHelper.createParamString(tenantId));
            JSONObject result = InsightsMetaDataStore.getInstance().get("WORKSPACE", queryIdentifier2, formats, param_array1,
                    InsightsSQLRSFormater.FORMAT_GROUP_ARRAY_JSON);
            JSONArray groupArrayForWorkspace = result.getJSONObject(StorageConstants.SQL_RESULT)
                    .getJSONArray(Constants.ROWS);

            if (groupArrayForWorkspace.isEmpty()) {
                return new JSONArray();
            }

            int len = groupArrayForWorkspace.size();
            for (int i = 0; i < len; i++) {
                param_array2.put(SQLDBHelper.createParamString(groupArrayForWorkspace.getString(i)));
                param_array2.put(SQLDBHelper.createParamString(tenantId));
                JSONObject result1 = InsightsMetaDataStore.getInstance().get("GET_GROUP_INFO", queryIdentifier1, formats, param_array2,
                        InsightsSQLRSFormater.FORMAT_COLUMN_ARRAY_JSON);
                param_array2.clear();
                groupArrayForExistingWorkspace
                        .add(result1.getJSONObject(StorageConstants.SQL_RESULT).getJSONObject(Constants.ROWS));
            }
        } catch (InsightsException | JSONException e) {
            String exp_message = "Exception while getByWorkspaceId - workspace_rid=" + workspace_rid;
            LOGGER.error(exp_message, e);
            throw new InsightsException(exp_message, e);
        }
        return groupArrayForExistingWorkspace;

    }

    

    /**
     * A method which delete all existing groups from a db table
     * sca_groups/sca_group_columns
     * 
     * @param workspace_rid
     * @throws InsightsException
     */
    public void deleteSCAGroupsById(String tenantId, String workspace_rid) throws InsightsException {
        try {
            
            JSONObject condition_colums = new JSONObject().put(Constants.WKSP_ID, workspace_rid);
            condition_colums.put(Constants.TENANT_ID, tenantId);
            InsightsMetaDataStore.getInstance().delete(InsightsTables.GI_SCA_GROUPS.name(), workspace_rid, condition_colums);
            InsightsMetaDataStore.getInstance().delete(InsightsTables.GI_SCA_GROUP_COLUMNS.name(), workspace_rid, condition_colums);
        } catch (InsightsException | JSONException e) {
            String exp_message = "Exception while deleteSCAGroupsById - workspace_rid=" + workspace_rid;
            LOGGER.error(exp_message, e);
            throw new InsightsException(exp_message, e);
        }
    }

    /**
     * @param projectRid
     * @return true if project_rid is valid else false
     * @throws InsightsException
     */
    public boolean isValidProjectRid(String projectRid) throws InsightsException {

        // validating workspace_rid and verify if it is present in db
        try {
            if (projectRid == null || projectRid.trim().isEmpty()
                    || projectRid.length() != Constants.WORKSPACE_ID_LENGTH) {
                return false;
            } else {
                String[] formats = {};
                JSONArray param_array = new JSONArray();
                param_array.put(SQLDBHelper.createParamString(projectRid));
                JSONObject result = CatalogsMetaDataStore.getInstance().get("IGC_WORKSPACE", "XMETA_WORKSPACE_QUERY", formats, param_array,
                        CatalogsSQLRSFormater.FORMAT_DEFAULT_JSON);
                JSONArray resultantArray = result.getJSONObject(StorageConstants.SQL_RESULT)
                        .getJSONArray(Constants.ROWS);

                if (resultantArray.isEmpty()) {
                    return false;
                }
            }
        } catch (InsightsException | JSONException e) {
            LOGGER.error("Got error while isValidProjectRid", e);
        }
        return true;
    }

    /**
     * @param column_rid
     * @return true if column_rid is valid else false
     * @throws InsightsException
     */
    public boolean isValidColumnRid(String column_rid) throws InsightsException {

        // validating workspace_rid and verify if it is present in db
        try {
            if (column_rid == null || column_rid.trim().isEmpty()
                    || column_rid.length() != Constants.WORKSPACE_ID_LENGTH) {
                return false;
            } else {
                String[] formats = {};
                JSONArray param_array = new JSONArray();
                param_array.put(SQLDBHelper.createParamString(column_rid));
                JSONObject result = CatalogsMetaDataStore.getInstance().get("IGC_WORKSPACE", "XMETA_COLUMNS_QUERY", formats, param_array,
                        CatalogsSQLRSFormater.FORMAT_DEFAULT_JSON);
                JSONArray resultantArray = result.getJSONObject(StorageConstants.SQL_RESULT)
                        .getJSONArray(Constants.ROWS);

                if (resultantArray.isEmpty()) {
                    return false;
                }
            }
        } catch (InsightsException | JSONException e) {
            LOGGER.error("Got error while isValidColumnRid", e);
        }
        return true;
    }

    /**
     * @param group_id
     * @return true if group_id is valid else false
     */
    public boolean isValidGroupID(String group_id) {

        if (group_id == null || group_id.trim().isEmpty()) {
            return false;
        }

        // generating pattern object
        Pattern pattern = Pattern.compile(Constants.GROUPID_REGEX);

        if (!pattern.matcher(group_id).find()) {
            return false;
        }
        return true;
    }

    /**
     * A methode which register sca task in sca_result table if analysis is not yet
     * started
     * @param tenant_id TODO
     * @param workspace_rid
     * 
     * @return a jsonobject contains status and variable to tell either we need to
     *         register task or not.
     * @throws InsightsException 
     * 
     */
    public JSONObject registerSCATask(String tenant_id, String workspace_rid) throws InsightsException {
        JSONObject jsonObject = new JSONObject();
        JSONObject response = new JSONObject();

        try {
            // check if analysis is already registered for this workspace_id
            String[] formats = { InsightsConfiguration.getInstance().getInsightsSchemaName()};
            JSONArray param_array = new JSONArray();
            param_array.put(SQLDBHelper.createParamString(workspace_rid));
            param_array.put(SQLDBHelper.createParamString(tenant_id));
            JSONObject result = InsightsMetaDataStore.getInstance().get(InsightsTables.GI_SCA_RESULTS.name(), InsightsSQLQueries.GET_SCA_STATUS, formats,
                    param_array, InsightsSQLRSFormater.FORMAT_DEFAULT_JSON);
            JSONArray resultantArray = result.getJSONObject(StorageConstants.SQL_RESULT).getJSONArray(Constants.ROWS);

            if (resultantArray.isEmpty()) {
                // create json object for group level information
                jsonObject.put(Constants.TENANT_ID, tenant_id);
                jsonObject.put(Constants.WKSP_ID, workspace_rid);
                jsonObject.put(Constants.UPDATED_AT, StorageConstants.CURRENT_TIMESTAMP);
                jsonObject.put(Constants.ISBEINGANALYSED, "y");
                jsonObject.put(Constants.NUM_GROUPS, 0);
                LOGGER.info("Save SCA Task - " + jsonObject.toString());
                InsightsMetaDataStore.getInstance().save(InsightsTables.GI_SCA_RESULTS.name(), workspace_rid, jsonObject);
                LOGGER.info("Done SCA Task - " + jsonObject.toString());
            } else {
                JSONObject jsObject = resultantArray.getJSONObject(0);
                updateSCAResults(tenant_id, workspace_rid, "y", -1, jsObject.getInt(Constants. REV_NO));
            }

        } catch (JSONException | InsightsException e) {
            String exp_message = "Got error while registerSCATask for workspace_id : " + workspace_rid;
            LOGGER.error(exp_message, e);
            throw new InsightsException(exp_message, e);
        }
        return response;
    }

    /**
     * This method will update SCA status and num_groups
     * or not
     * 
     * @param workspace_rid
     * @param is_being_analysed
     * @param num_groups 
     * @throws InsightsException 
     */
    public void updateSCAResults(String tenantId, String workspace_rid, String is_being_analysed, int num_groups, int rev_no) throws InsightsException {

        JSONObject newJson = new JSONObject();
        JSONObject condition_colums = new JSONObject();
        try {
            newJson.put(Constants.ISBEINGANALYSED, is_being_analysed);
            if(num_groups > 0) {
                newJson.put(Constants.NUM_GROUPS, num_groups);
            }
            condition_colums.put(Constants.WKSP_ID, workspace_rid);
            condition_colums.put(Constants.TENANT_ID, tenantId);
            condition_colums.put(Constants.REV_NO, rev_no);
            JSONObject res = InsightsMetaDataStore.getInstance().update(InsightsTables.GI_SCA_RESULTS.name(), workspace_rid, newJson, condition_colums);
            if (res == null || res.isEmpty() || res.isNull(StorageConstants.SQL_RESULT) || res.getInt(StorageConstants.SQL_RESULT) <= 0) { 
                throw new InsightsException("Error: Updation failed for drd: " + workspace_rid + " Update Condition: " + condition_colums);
            }
        } catch (InsightsException | JSONException e) {
            String exp_message = "Got error while updateSCAStatus for - workspace_id  : " + workspace_rid;
            LOGGER.error(exp_message, e);
            throw new InsightsException(exp_message, e);
        }
    }

    /**
     * A method which changes SCA state (accepted/rejected)
     * 
     * @param workspace_rid
     * @param group_id
     * @param state
     * @throws InsightsException 
     */
    public void updateSCAGroupState(String tenantId, String workspace_rid, String group_id, String state, int rev_no) throws InsightsException {

        JSONObject newJson = new JSONObject();
        JSONObject condition_colums = new JSONObject();
        try {
            newJson.put(Constants.STATE, state);
            condition_colums.put(Constants.GROUP_ID, group_id);
            condition_colums.put(Constants.TENANT_ID, tenantId);
            condition_colums.put(Constants.REV_NO, rev_no);
            JSONObject res = InsightsMetaDataStore.getInstance().update(InsightsTables.GI_SCA_GROUPS.name(), workspace_rid, newJson, condition_colums);
            if (res == null || res.isEmpty() || res.isNull(StorageConstants.SQL_RESULT) || res.getInt(StorageConstants.SQL_RESULT) <= 0) { 
                throw new InsightsException("Error: Updation failed for drd: " + workspace_rid + " Update Condition: " + condition_colums);
            }
        } catch (InsightsException | JSONException e) {
            String exp_message = "Got error while updateSCAStatus for - workspace_id " + workspace_rid + "& group_id=" + group_id;
            LOGGER.error(exp_message, e);
            throw new InsightsException(exp_message, e);
        }
    }

    /**
     * @param workspace_rid
     * @param group_id
     * @return a JSON with current state of a group and rev_no
     * @throws InsightsException
     */
    public JSONObject getSCAGroupState(String tenantId, String workspace_rid, String group_id) throws InsightsException {

        JSONObject group_resp = new JSONObject();
        try {
            String[] formats = {InsightsConfiguration.getInstance().getInsightsSchemaName()};
            JSONArray param_array = new JSONArray();
            param_array.put(SQLDBHelper.createParamString(group_id));
            param_array.put(SQLDBHelper.createParamString(tenantId));
            JSONObject result = InsightsMetaDataStore.getInstance().get(InsightsTables.GI_SCA_RESULTS.name(), InsightsSQLQueries.GET_SCA_GROUP_STATE, formats,
                    param_array, InsightsSQLRSFormater.FORMAT_DEFAULT_JSON);
            JSONArray resultantArray = result.getJSONObject(StorageConstants.SQL_RESULT).getJSONArray(Constants.ROWS);
            
            if (!resultantArray.isEmpty()) {
                group_resp = resultantArray.getJSONObject(0);
            }
        } catch (InsightsException | JSONException e) {
            String exp_message = "Exception while getSCAGroupStatus - group_id=" + group_id;
            LOGGER.error(exp_message, e);
            throw new InsightsException(exp_message, e);
        }
        return group_resp;

    }

    /**
     * @param workspace_rid
     * @return
     * @throws InsightsException
     */
    public JSONObject getSCAResultsStatus(String tenantId, String workspace_rid) throws InsightsException {

        JSONObject state = new JSONObject();
        try {
            String schema_name = InsightsConfiguration.getInstance().getInsightsSchemaName();
            String[] formats = {schema_name,schema_name};
            JSONArray param_array = new JSONArray();
            param_array.put(SQLDBHelper.createParamString(workspace_rid));
            param_array.put(SQLDBHelper.createParamString(tenantId));
            JSONObject result = InsightsMetaDataStore.getInstance().get("SCA_STATUS", InsightsSQLQueries.GET_SCA_BY_STATUS, formats, param_array,
                    InsightsSQLRSFormater.FORMAT_DEFAULT_JSON);
            JSONArray resultantArray = result.getJSONObject(StorageConstants.SQL_RESULT).getJSONArray(Constants.ROWS);
            if (!resultantArray.isEmpty()) {
                state = resultantArray.getJSONObject(0);
            }

        } catch (InsightsException | JSONException e) {
            String exp_message = "Exception while getSCAResultsStatus - workspace_rid=" + workspace_rid;
            LOGGER.error(exp_message, e);
            throw new InsightsException(exp_message, e);
        }
        return state;
    }

    /**
     * @param workspace_rid
     * @param filter_state
     * @param sort_numcols
     * @param sort_numassets
     * @param sort_status
     * @param limit
     * @param offset
     * @return
     * @throws InsightsException
     */
    public JSONArray getSCAGroups(String tenantId, String workspace_rid, String filter_state, String filter_name, String sort_numcols,
            String sort_numassets, String sort_status, int limit, int offset) throws InsightsException {

        // create orderby string
        String orderby = "";
        if (sort_numcols != null && !sort_numcols.trim().isEmpty() && sort_numcols.equalsIgnoreCase("ASC")) {
            orderby = Constants.NUM_COLS;
        } else if( sort_numcols != null && !sort_numcols.trim().isEmpty()){
            orderby = Constants.NUM_COLS + " DESC";
        }

        if (sort_numassets != null && !sort_numassets.trim().isEmpty() && sort_numassets.equalsIgnoreCase("ASC")) {
            orderby = Constants.NUM_DATASETS;
        } else if( sort_numassets != null && !sort_numassets.trim().isEmpty()){
            orderby = Constants.NUM_DATASETS + " DESC";
        }
        
        if (sort_status != null && !sort_status.trim().isEmpty() && sort_status.equalsIgnoreCase("ASC")) {
            orderby = Constants.STATE;
        } else if( sort_status != null && !sort_status.trim().isEmpty()){
            orderby = Constants.STATE + " DESC";
        }
        
        //if no sort request came, then sort on numcols
        if(orderby.isEmpty()) {
            orderby = Constants.NUM_COLS;
        }

        JSONArray resultantArray = new JSONArray();
        try {

            JSONArray param_array = new JSONArray();
            param_array.put(SQLDBHelper.createParamString(workspace_rid));
            param_array.put(SQLDBHelper.createParamString(tenantId));
            JSONObject result = new JSONObject();

            // check if filter_name is present or not
            if (filter_name == null || filter_name.trim().isEmpty()) {
                // check if filter_state is present
                if (filter_state != null && !filter_state.isEmpty()) {
                    String[] formats = { InsightsConfiguration.getInstance().getInsightsSchemaName() };
                    param_array.put(SQLDBHelper.createParamString(filter_state));
                    result = InsightsMetaDataStore
                        .getInstance()
                        .get(InsightsTables.GI_SCA_GROUPS.name(), InsightsSQLQueries.GET_SCA_GROUPS2, formats,
                            param_array, InsightsSQLRSFormater.FORMAT_DEFAULT_JSON, orderby, offset, limit);
                } else {
                    String[] formats = { InsightsConfiguration.getInstance().getInsightsSchemaName() };
                    result = InsightsMetaDataStore
                        .getInstance()
                        .get(InsightsTables.GI_SCA_GROUPS.name(), InsightsSQLQueries.GET_SCA_GROUPS1, formats,
                            param_array, InsightsSQLRSFormater.FORMAT_DEFAULT_JSON, orderby, offset, limit);
                }
            } else {
                if (filter_state != null && !filter_state.isEmpty()) {
                    String[] formats = { InsightsConfiguration.getInstance().getInsightsSchemaName(), filter_name.toUpperCase() };
                    param_array.put(SQLDBHelper.createParamString(filter_state));
                    result = InsightsMetaDataStore
                            .getInstance()
                            .get(InsightsTables.GI_SCA_GROUPS.name(), InsightsSQLQueries.GET_SCA_GROUPS_SEARCH_STATE, formats,
                                param_array, InsightsSQLRSFormater.FORMAT_DEFAULT_JSON, orderby, offset, limit);
                    
                } else {
                    String[] formats = { InsightsConfiguration.getInstance().getInsightsSchemaName(), filter_name.toUpperCase() };
                    result = InsightsMetaDataStore
                        .getInstance()
                        .get(InsightsTables.GI_SCA_GROUPS.name(), InsightsSQLQueries.GET_SCA_GROUPS_SEARCH, formats,
                            param_array, InsightsSQLRSFormater.FORMAT_DEFAULT_JSON, orderby, offset, limit);
                }
            }
            resultantArray = result.getJSONObject(StorageConstants.SQL_RESULT).getJSONArray(Constants.ROWS);
        } catch (InsightsException | JSONException e) {
            String exp_message = "Exception while getSCAGroups - workspace_rid=" + workspace_rid;
            LOGGER.error(exp_message, e);
            throw new InsightsException(exp_message, e);
        }
        return resultantArray;

    }

    /**
     * @param column_id
     * @return
     * @throws InsightsException
     */
    public JSONObject getRefColumnForAGroup(String tenantId, String column_id) throws InsightsException {

        JSONObject state = new JSONObject();
        try {
            String[] formats = {InsightsConfiguration.getInstance().getInsightsSchemaName()};
            JSONArray param_array = new JSONArray();
            param_array.put(SQLDBHelper.createParamString(column_id));
            param_array.put(SQLDBHelper.createParamString(tenantId));
            JSONObject result = InsightsMetaDataStore.getInstance().get(InsightsTables.GI_SCA_GROUP_COLUMNS.name(), InsightsSQLQueries.GET_SCA_REFERENCE_COLUMN_FOR_GROUP,
                    formats, param_array, InsightsSQLRSFormater.FORMAT_DEFAULT_JSON);
            JSONArray resultantArray = result.getJSONObject(StorageConstants.SQL_RESULT).getJSONArray(Constants.ROWS);
            state = resultantArray.getJSONObject(0);
        } catch (JSONException e) {
            String exp_message = "Exception while getRefColumnForAGroup - column_id=" + column_id;
            LOGGER.error(exp_message, e);
            throw new InsightsException(exp_message, e);
        }
        return state;
    }

    /**
     * @param workspace_rid
     * @param group_id
     * @param min_threshold
     * @param max_threshold
     * @param filter_colname
     * @param sort_similarityscore
     * @param sort_location
     * @param limit
     * @param offset
     * @return
     * @throws InsightsException
     */
    public JSONObject getAllSimilarColumnForGroup(String tenantId, String workspace_rid, String group_id, int min_threshold,
            int max_threshold, String filter_colname, String sort_similarityscore, String sort_location, int limit,
            int offset) throws InsightsException {

        // create orderby string
        String orderby = "";
        if (sort_similarityscore != null && !sort_similarityscore.trim().isEmpty() && sort_similarityscore.equalsIgnoreCase("ASC")) {
            orderby = Constants.SIM_SCORE;
        } else if ( sort_similarityscore != null && !sort_similarityscore.trim().isEmpty()){
            orderby = Constants.SIM_SCORE + " DESC";
        }

        if (sort_location != null && !sort_location.trim().isEmpty() && sort_location.equalsIgnoreCase("ASC")) {
            orderby = Constants.COL_LOCATION;
        } else if ( sort_location != null && !sort_location.trim().isEmpty()){
            orderby = Constants.COL_LOCATION + " DESC";
        }

        // if no sort request came, then sort on similarityscore
        if (orderby.isEmpty()) {
            orderby = Constants.SIM_SCORE;
        }
        
        String search = "";
        if (filter_colname != null && !filter_colname.isEmpty()) {
            search = filter_colname;
        }

        JSONObject resultantJson = new JSONObject();
        try {
            JSONArray param_array = new JSONArray();
            param_array.put(SQLDBHelper.createParamString(workspace_rid));
            param_array.put(SQLDBHelper.createParamString(group_id));
            param_array.put(SQLDBHelper.createParamString(tenantId));

            String[] formats = {
                InsightsConfiguration.getInstance().getInsightsSchemaName(),
                InsightsConfiguration.getInstance().getInsightsSchemaName(),
                search.toUpperCase(),
                String.valueOf(min_threshold),
                String.valueOf(max_threshold),
            };

            JSONObject result = InsightsMetaDataStore.getInstance().get("GETSCAGROUPCOLUMNS", InsightsSQLQueries.GET_SCA_GROUP_COLUMNS, formats, param_array,
                    InsightsSQLRSFormater.FORMAT_GROUP_COLUMN_ARRAY_JSON, orderby, offset, limit);

            resultantJson = result.getJSONObject(StorageConstants.SQL_RESULT).getJSONObject(Constants.ROWS);
        } catch (JSONException | InsightsException e) {
            String exp_message = "Exception while getAllSimilarColumnForGroup - workspace_rid=" + workspace_rid;
            LOGGER.error(exp_message, e);
            throw new InsightsException(exp_message, e);
        }
        return resultantJson;

    }
    
    public String getWorkspaceId(String workspace_name) throws InsightsException {

        String workspace_id = null;
        try {
            String[] formats = {};
            JSONArray param_array = new JSONArray();
            param_array.put(SQLDBHelper.createParamString(workspace_name));
            JSONObject result = CatalogsMetaDataStore.getInstance().get("XMETA_GET_WORKSPACE_ID", "XMETA_GET_WORKSPACE_ID_QUERY", formats,
                    param_array, CatalogsSQLRSFormater.FORMAT_DEFAULT_JSON);
            JSONArray resultantArray = result.getJSONObject(StorageConstants.SQL_RESULT).getJSONArray(Constants.ROWS);
            
            if(!resultantArray.isEmpty()) {
                workspace_id = resultantArray.getJSONObject(0).getString(Constants.RID);
            }
        } catch (InsightsException | JSONException e) {
            String exp_message = "Exception while getWorkspaceId - workspace_name=" + workspace_name;
            LOGGER.error(exp_message, e);
            throw new InsightsException(exp_message, e);
        }
        return workspace_id;
    }

    public int getNumOfGroups(String tenantId, String workspace_rid) throws InsightsException {

        int num_groups = 0; 
        try {
            String[] formats = { InsightsConfiguration.getInstance().getInsightsSchemaName() };
            JSONArray param_array = new JSONArray();
            param_array.put(SQLDBHelper.createParamString(workspace_rid));
            param_array.put(SQLDBHelper.createParamString(tenantId));
            JSONObject result = InsightsMetaDataStore.getInstance().get(InsightsTables.GI_SCA_RESULTS.name(), InsightsSQLQueries.GET_SCA_STATUS, formats,
                    param_array, InsightsSQLRSFormater.FORMAT_DEFAULT_JSON);
            JSONArray resultantArray = result.getJSONObject(StorageConstants.SQL_RESULT).getJSONArray(Constants.ROWS);
            if(!resultantArray.isEmpty()) {
                num_groups = resultantArray.getJSONObject(0).getInt(Constants.NUM_GROUPS);
            }
        } catch (JSONException | InsightsException e) {
            String exp_message = "Exception while getNumOfGroups - workspace_rid=" + workspace_rid;
            LOGGER.error(exp_message, e);
            throw new InsightsException(exp_message, e);
        } 
        return num_groups;
    }
    
    /**
     * @param classCode
     *            : class code for a new column
     * @param className
     *            : class name for a new column
     * @param classDescription
     *            : description of a new data class
     * @param projectName
     *            : name of a workspace
     * @param referenceColumn
     *            : reference column which contain
     *            database_name.schema_name.table_name.column_name
     * @param confidenceThreshold
     *            : threshold on which we are making classes are similar
     * @return
     * @throws InsightsException
     */
    public JSONObject createCustomClass(String classCode, String className, String classDescription, String projectName,
            String referenceColumn, double confidenceThreshold) throws InsightsException {

        String createSuggestedCustomClassurl = InsightsConfiguration.getInstance().getIISBaseUrl()
                + "/ia/api/createColumnSimilarityDataClass";
        String basicAuth = InsightsConfiguration.getInstance().getIISBasicAuthToken();

        JSONObject resp = new JSONObject();
        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "application/json");
        header.put("Authorization", "Basic " + basicAuth);

        try {
            if (classCode == null || classCode.trim().isEmpty() || className == null || className.trim().isEmpty()
                    || projectName == null || projectName.trim().isEmpty() || referenceColumn == null
                    || referenceColumn.trim().isEmpty()) {
                String exp_message = "Error while createColumnSimilarityDataClass : Some Parameters are missing";
                LOGGER.error(exp_message);
                throw new InsightsException(exp_message);
            }
            // creating queryParam URL
            if (classDescription == null || classDescription.trim().isEmpty()) {
                createSuggestedCustomClassurl = createSuggestedCustomClassurl + "?classCode=" + classCode
                        + "&className=" + URLEncoder.encode(className, "UTF-8") + "&projectName="
                        + URLEncoder.encode(projectName, "UTF-8") + "&referenceColumn=" + referenceColumn
                        + "&confidenceThreshold=" + confidenceThreshold;
            } else {
                createSuggestedCustomClassurl = createSuggestedCustomClassurl + "?classCode=" + classCode
                        + "&className=" + URLEncoder.encode(className, "UTF-8") + "&projectName="
                        + URLEncoder.encode(projectName, "UTF-8") + "&classDescription="
                        + URLEncoder.encode(classDescription, "UTF-8") + "&referenceColumn=" + referenceColumn
                        + "&confidenceThreshold=" + confidenceThreshold;

            }
            // making REST GET call for creating custom column similarity classifier
            HttpResponse HTTPResp = HttpUtils.executeRestAPI(createSuggestedCustomClassurl, "GET", header, null);
            resp = HttpUtils.parseResponseAsJSONObject(HTTPResp);
            if (resp.has(Constants.RESULT) && !resp.getString(Constants.RESULT).isEmpty()) {
                resp.put(Constants.STATUS_MESSAGE, resp.getString(Constants.RESULT));
                resp.remove(Constants.RESULT);
            }
            resp.put(Constants.STATUS, HTTPResp.getStatusLine().getStatusCode());
        } catch (JSONException | InsightsException | UnsupportedEncodingException e) {
            String exp_message = "Exception while createCustomClass with a given ClassCode:" + classCode
                    + "\nClassName :" + className + "\nreferenceColumn : " + referenceColumn + "\n Project Name :"
                    + projectName;
            LOGGER.error(exp_message, e);
            throw new InsightsException(exp_message, e);
        }
        return resp;
    }

    /**
     * @param tenantId
     * @param workspace_rid
     * @param group_id
     * @return a suggested group row information if present
     * @throws InsightsException
     */
    public JSONObject getSCAGroups(String tenantId, String workspace_rid, String group_id) throws InsightsException {
        
        JSONObject res_group = new JSONObject();
        try {
            String[] formats = {};
            JSONArray param_array = new JSONArray();
            param_array.put(SQLDBHelper.createParamString(workspace_rid));
            param_array.put(SQLDBHelper.createParamString(group_id));
            param_array.put(SQLDBHelper.createParamString(tenantId));
            JSONObject result = InsightsMetaDataStore.getInstance().get(InsightsTables.GI_SCA_GROUPS.name(), "SCA_GET_GROUP_INFO", formats,
                    param_array, InsightsSQLRSFormater.FORMAT_DEFAULT_JSON);
            JSONArray resultantArray = result.getJSONObject(StorageConstants.SQL_RESULT).getJSONArray(Constants.ROWS);
            
            if(!resultantArray.isEmpty()) {
                res_group = resultantArray.getJSONObject(0);
            }
        } catch (InsightsException | JSONException e) {
            String exp_message = "Exception while getSCAGroups - workspace_name=" + workspace_rid + " group_id=" + group_id;
            LOGGER.error(exp_message, e);
            throw new InsightsException(exp_message, e);
        }
        return res_group;
    }

    /**
     * @param class_code
     * @param class_name
     * @param description
     * @param ref_col_id
     * @param ref_col_name
     * @param confidence
     * @return A utility method to create column similarity data class using BG endpoint
     * @throws InsightsException
     */
    public JSONObject createCustomClassBG(String class_code, String class_name, String description, String ref_col_id,
            String ref_col_name, double confidence) throws InsightsException {
        String createSuggestedCustomClassurl = InsightsConfiguration.getInstance().getInsightsZenUrl()
                + "/v3/data_classes";
        String access_token = SessionManager.getCurrentSession().getAccessToken();

        JSONObject resp = new JSONObject();
        try {
            InputStream inputStream = SimilarColumnsUtils.class.getClassLoader()
                    .getResourceAsStream("create_data_class.xml");
            InputStreamReader isReader = new InputStreamReader(inputStream);
            // Creating a BufferedReader object
            BufferedReader reader = new BufferedReader(isReader);
            StringBuffer sb = new StringBuffer();
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }

            String res = sb.toString();
            res = res.replaceAll("data_class_id", class_code);
            res = res.replaceAll("data_class_name", class_name);
            res = res.replaceAll("description_val", description);
            res = res.replaceAll("threshold_val", Double.toString(confidence));

            // making columns metadata json array
            JSONArray cols_metadata_jsonarray = new JSONArray();
            JSONObject col_jsonobject = new JSONObject();
            col_jsonobject.put(Constants.GET_CA_RESULTS, getCAResultsByColId(ref_col_id));
            col_jsonobject.put(Constants.NAME, ref_col_name);
            col_jsonobject.put(Constants.COLUMN_WEIGHT, 1);
            cols_metadata_jsonarray.add(col_jsonobject);
            String str1 = cols_metadata_jsonarray.toString();
            res = res.replace("column_metadata_val", str1);

            LOGGER.info("XML for creating drd:" + res);
            LOGGER.info("Create data class URL : " + createSuggestedCustomClassurl);
            StringEntity stringEntity = new StringEntity(res, "UTF-8");

            Map<String, String> header = new HashMap<String, String>();
            header.put("Content-Type", "application/xml");
            header.put("Authorization", "Bearer " + access_token);

            // making REST GET call for creating custom column similarity classifier
            HttpResponse HTTPResp = HttpUtils.executeRestAPI(createSuggestedCustomClassurl, "POST", header,
                    stringEntity);
            resp = HttpUtils.parseResponseAsJSONObject(HTTPResp);

            if (resp.has(Constants.RESULT) && !resp.getString(Constants.RESULT).isEmpty()) {
                resp.put(Constants.STATUS_MESSAGE, resp.getString(Constants.RESULT));
                resp.remove(Constants.RESULT);
            }
            resp.put(Constants.STATUS, HTTPResp.getStatusLine().getStatusCode());
        } catch (InsightsException | IOException | JSONException e) {
            String exp_message = "Error while create data class from BG";
            LOGGER.error(exp_message, e);
            throw new InsightsException(exp_message, e);
        }
        return resp;
    }

    public JSONObject getCAResultsByColId(String column_id) throws InsightsException {

        JSONObject caResults = null;
        try {
            String[] formats = {};
            JSONArray param_array = new JSONArray();
            param_array.put(SQLDBHelper.createParamString(column_id));
            JSONObject result = CatalogsMetaDataStore.getInstance().get("FETCH_CA_RESULTS_BY_COLUMN_ID",
                    "FETCH_CA_RESULTS_BY_COLUMN_ID", formats, param_array, CatalogsSQLRSFormater.FORMAT_DEFAULT_JSON);
            JSONArray resultantArray = result.getJSONObject(StorageConstants.SQL_RESULT).getJSONArray(Constants.ROWS);

            if (!resultantArray.isEmpty()) {
                caResults = resultantArray.getJSONObject(0);
                caResults = new JSONObject(caResults.getString(Constants.GET_ADVANCEDRESULTS_XMETA));
            }
        } catch (InsightsException | JSONException e) {
            String exp_message = "Exception while getCAResultsByColId - column_id=" + column_id;
            LOGGER.error(exp_message, e);
            throw new InsightsException(exp_message, e);
        }
        return caResults;
    }
    
}
