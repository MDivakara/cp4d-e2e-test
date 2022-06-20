/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.storage;

import java.util.HashMap;

import com.mit.gov.ms.store.StorageConstants;
import com.mit.gov.ms.store.queries.SQLQueries;

/**
 * @author Shaik.Nawaz
 *
 */
public class InsightsSQLQueries extends SQLQueries {

    public static final String DB_VERIFY_QUERY = "DB_VERIFY_QUERY";
    
    // Task Management Module
    public static final String GET_TASK_BY_ID = "GET_TASK_BY_ID";
    public static final String GET_LATEST_TASK_BY_SOURCE_ID = "GET_TASK_BY_SOURCE_ID";
    public static final String GET_TASK_BY_STATUS = "GET_TASK_BY_STATUS";
    
    // Anomaly/DRD Module
    public static final String DRD_DATA = "DRD_DATA";
    public static final String DRD_STATUS = "DRD_STATUS";
    public static final String DRD_EXISTS = "DRD_EXISTS";
    public static final String DRD_NAME_CHECK = "DRD_NAME_CHECK";
    public static final String DRD_ASSET_AND_COLUMNS = "DRD_ASSET_AND_COLUMNS";
    public static final String DRD_CONNECTION_DETAILS = "DRD_CONNECTION_DETAILS";
    public static final String DRD_GET_ALL_DRD = "DRD_GET_ALL_DRD";
    public static final String DRD_CHECK_STATE = "DRD_CHECK_STATE";
    public static final String DRD_GET_MODEL_ID_FROM_DRD_ID = "DRD_GET_MODEL_ID_FROM_DRD_ID";
    
    // Model Management Module
    public static final String GET_ML_MODEL_BY_VERSION = "GET_ML_MODEL_BY_VERSION";
    public static final String GET_ML_MODEL_BY_CURRENT_VERSION = "GET_ML_MODEL_BY_CURRENT_VERSION";
    public static final String GET_ML_REV_NO_BY_MODEL_ID = "GET_ML_REV_NO_BY_MODEL_ID";

    // SCA Queries
    public static final String GET_SCA_WORKSPACE_RESULTS_QUERY = "GET_SCA_WORKSPACE_RESULTS_QUERY";
    public static final String GET_SCA_WORKSPACE_RESULTS_GROUP_QUERY = "GET_SCA_WORKSPACE_RESULTS_GROUP_QUERY";
    public static final String GET_SCA_BY_STATUS = "GET_SCA_BY_STATUS";
    public static final String GET_SCA_GROUPS1 = "GET_SCA_GROUPS1";
    public static final String GET_SCA_GROUPS2 = "GET_SCA_GROUPS2";
    public static final String GET_SCA_GROUPS_SEARCH = "GET_SCA_GROUPS_SEARCH";
    public static final String GET_SCA_GROUP_COLUMNS = "GET_SCA_GROUP_COLUMNS";
    public static final String GET_SCA_STATUS = "GET_SCA_STATUS";
    public static final String GET_SCA_GROUP_STATE = "GET_SCA_GROUP_STATE";
    public static final String GET_SCA_REFERENCE_COLUMN_FOR_GROUP = "GET_SCA_REFERENCE_COLUMN_FOR_GROUP";
    public static final String GET_SCA_GROUPS_SEARCH_STATE = "GET_SCA_GROUPS_SEARCH_STATE";
    public static final String SCA_GET_GROUP_INFO = "SCA_GET_GROUP_INFO";

    public InsightsSQLQueries(String dbType) {
        super(dbType);
    }

    protected HashMap<String, String> getQueries(String dbType) {
        HashMap<String, String> db2Queries = new HashMap<>();
        HashMap<String, String> oracleOveridesQueries = new HashMap<>();
        HashMap<String, String> sqlServerQueridess = new HashMap<>();
        String current_timestamp = (!dbType.startsWith(StorageConstants.DB_TYPE_ORCL)) ? "CURRENT_TIMESTAMP"
            : "CAST (CURRENT_TIMESTAMP AS TIMESTAMP)";

        db2Queries.put(DB_VERIFY_QUERY, "SELECT 1 FROM SYSIBM.SYSDUMMY1");
        oracleOveridesQueries.put(DB_VERIFY_QUERY, "SELECT 1 FROM DUAL");
        sqlServerQueridess.put(DB_VERIFY_QUERY, "SELECT 1");

        // TASK Module Queries
        db2Queries
            .put(GET_TASK_BY_ID,
                "SELECT TENANT_ID, TASK_ID, REV_NO, TASK_TYPE, SOURCE_ID, CREATED_BY, CREATED_AT, UPDATED_AT, STARTED_AT, "
                    + " STATUS, STATUS_MESSAGE, REQUEST_INFO, OUTPUT_INFO, " + current_timestamp + " AS CURRENT_TS "
                    + " FROM %s.GI_TASKS WHERE TENANT_ID = ? AND TASK_ID = ?");

        db2Queries
            .put(GET_LATEST_TASK_BY_SOURCE_ID,
                "SELECT TENANT_ID, TASK_ID, REV_NO, TASK_TYPE, SOURCE_ID, CREATED_BY, CREATED_AT, UPDATED_AT, STARTED_AT, "
                    + " STATUS, STATUS_MESSAGE, REQUEST_INFO, OUTPUT_INFO, " + current_timestamp + "  AS CURRENT_TS "
                    + " FROM %s.GI_TASKS WHERE TENANT_ID = ? AND SOURCE_ID = ? AND TASK_TYPE= ? ");

        db2Queries
            .put(GET_TASK_BY_STATUS,
                "SELECT TENANT_ID, TASK_ID, TASK_TYPE, SOURCE_ID, CREATED_BY, CREATED_AT, UPDATED_AT, STARTED_AT, STATUS, "
                + " REQUEST_INFO, " + current_timestamp + "  AS CURRENT_TS "
                + " FROM %s.GI_TASKS WHERE STATUS IN ( %s ) %s");

        // SCA Module Queries
        db2Queries
            .put(GET_SCA_WORKSPACE_RESULTS_QUERY,
                "SELECT SC1.STATE, SC2.COL_INFO, SC1.REF_COL_ID, SC1.GROUP_ID, SC1.REF_COL_NAME, SC1.REF_ASSET_ID, SC1.NUM_DATASETS, "
                    + "SC1.NUM_COLS, SC1.MIN_SIM_SCORE, SC1.MAX_SIM_SCORE "
                    + "FROM %s.GI_SCA_GROUPS SC1 INNER JOIN %s.GI_SCA_GROUP_COLUMNS SC2 "
                    + "ON SC1.GROUP_ID = SC2.GROUP_ID WHERE SC1.GROUP_ID = ? AND SC1.TENANT_ID = ?");

        db2Queries
            .put(GET_SCA_WORKSPACE_RESULTS_GROUP_QUERY,
                "SELECT GROUP_ID FROM %s.GI_SCA_GROUPS WHERE WKSP_ID = ? AND TENANT_ID = ?");

        db2Queries
            .put(GET_SCA_BY_STATUS, "SELECT WKSP_ID, ISBEINGANALYSED, REV_NO "
                + "FROM %s.GI_SCA_RESULTS WHERE WKSP_ID = ? AND TENANT_ID = ?");

        db2Queries
            .put(GET_SCA_GROUPS1,
                "SELECT count(*) OVER() AS TOTAL_COUNT, GROUP_ID,NUM_COLS,NUM_DATASETS,MAX_SIM_SCORE,"
                    + "MIN_SIM_SCORE,STATE,REF_COL_NAME,REF_COL_ID, LAST_RUN_AT, LAST_RUN_BY, REF_ASSET_ID "
                    + " FROM %s.GI_SCA_GROUPS WHERE WKSP_ID = ? AND TENANT_ID = ? ");

        db2Queries
            .put(GET_SCA_GROUPS2,
                "SELECT count(*) OVER() AS TOTAL_COUNT, GROUP_ID,NUM_COLS,NUM_DATASETS,MAX_SIM_SCORE,MIN_SIM_SCORE,"
                    + "STATE,REF_COL_NAME,REF_COL_ID, LAST_RUN_AT, LAST_RUN_BY, REF_ASSET_ID "
                    + " FROM %s.GI_SCA_GROUPS WHERE WKSP_ID = ? AND TENANT_ID = ? AND STATE = ? ");

        db2Queries
            .put(GET_SCA_GROUPS_SEARCH,
                "SELECT count(*) OVER() AS TOTAL_COUNT, GROUP_ID,NUM_COLS,NUM_DATASETS,MAX_SIM_SCORE,"
                    + "MIN_SIM_SCORE,STATE,REF_COL_NAME,REF_COL_ID, LAST_RUN_AT, LAST_RUN_BY, REF_ASSET_ID "
                    + " FROM %s.GI_SCA_GROUPS WHERE UPPER(REF_COL_NAME) like '%%%s%%' and WKSP_ID = ? AND TENANT_ID = ? ");
        
        db2Queries
        .put(GET_SCA_GROUPS_SEARCH_STATE,
            "SELECT count(*) OVER() AS TOTAL_COUNT, GROUP_ID,NUM_COLS,NUM_DATASETS,MAX_SIM_SCORE,"
                + "MIN_SIM_SCORE,STATE,REF_COL_NAME,REF_COL_ID, LAST_RUN_AT, LAST_RUN_BY, REF_ASSET_ID "
                + " FROM %s.GI_SCA_GROUPS WHERE UPPER(REF_COL_NAME) like '%%%s%%' and WKSP_ID = ? AND TENANT_ID = ? AND STATE = ? ");

        db2Queries
            .put(GET_SCA_GROUP_COLUMNS,
                "SELECT SC2.COL_NAME, SC1.NUM_COLS, SC1.NUM_DATASETS, SC2.SIM_SCORE, SC1.REF_COL_ID ,SC2.COL_LOCATION, SC1.MIN_SIM_SCORE, "
                    + "SC1.MAX_SIM_SCORE, SC2.COL_INFO, SC1.STATE, count(*) OVER() AS TOTAL_COUNT "
                    + "FROM %s.GI_SCA_GROUPS SC1 INNER JOIN %s.GI_SCA_GROUP_COLUMNS SC2 "
                    + "ON SC1.GROUP_ID = SC2.GROUP_ID WHERE SC2.WKSP_ID = ? AND SC2.GROUP_ID = ? AND SC2.TENANT_ID = ? "
                    + "AND UPPER(SC2.COL_NAME) LIKE '%%%s%%' AND SC2.SIM_SCORE >= %s AND SC2.SIM_SCORE <= %s ");
        
        db2Queries.put(SCA_GET_GROUP_INFO,
                "SELECT  NUM_COLS, NUM_DATASETS, REF_COL_ID , MIN_SIM_SCORE, MAX_SIM_SCORE, STATE, count(*) OVER() AS TOTAL_COUNT "
                + "FROM %s.GI_SCA_GROUPS where WKSP_ID = ? AND GROUP_ID = ? AND TENANT_ID = ?");
        
        db2Queries
            .put(GET_SCA_STATUS,
                "SELECT ISBEINGANALYSED, NUM_GROUPS, REV_NO FROM %s.GI_SCA_RESULTS WHERE WKSP_ID = ? AND TENANT_ID = ?");
        
        db2Queries
            .put(GET_SCA_GROUP_STATE,
                "SELECT STATE, REV_NO FROM %s.GI_SCA_GROUPS WHERE GROUP_ID = ? AND TENANT_ID = ?");
        
        db2Queries
            .put(GET_SCA_REFERENCE_COLUMN_FOR_GROUP,
                "SELECT COL_INFO FROM %s.GI_SCA_GROUP_COLUMNS WHERE COL_ID = ? AND TENANT_ID = ?");

        // Anomaly Module Queries
        db2Queries
            .put(DRD_DATA, "SELECT CREATED_BY, CREATED_AT, DRD_ID, DRD_NAME, MODEL_ID "
                + "FROM %s.GI_DATA_RULE_DEFINITIONS WHERE TENANT_ID = ? AND DRD_ID = ? AND STATE != ? ");
        
        db2Queries
            .put(DRD_STATUS, "SELECT DRD_ID, STATUS_MESSAGE  " + "FROM %s.GI_DATA_RULE_DEFINITIONS "
                + "WHERE TENANT_ID = ? AND DRD_ID = ?");
        
        db2Queries
            .put(DRD_EXISTS, "SELECT REV_NO, COUNT(*) AS count FROM %s.GI_DATA_RULE_DEFINITIONS "
                + "WHERE TENANT_ID = ? AND DRD_ID = ? AND STATE = ? GROUP BY REV_NO");
        
        db2Queries
            .put(DRD_CONNECTION_DETAILS,
                "SELECT REQUEST_INFO, STATE, REV_NO from %s.GI_DATA_RULE_DEFINITIONS WHERE TENANT_ID = ? AND DRD_ID = ?");
        
        db2Queries
            .put(DRD_NAME_CHECK,
                "SELECT DRD_NAME, DRD_ID FROM %s.GI_DATA_RULE_DEFINITIONS WHERE TENANT_ID = ? AND DRD_NAME = ? "
                    + "AND (STATE != ? OR IS_BEING_ANALYSED = ?)");
        
        db2Queries
            .put(DRD_ASSET_AND_COLUMNS, "SELECT REQUEST_INFO "
                + "FROM %s.GI_DATA_RULE_DEFINITIONS WHERE TENANT_ID = ? AND ASSET_ID = ? AND (STATE = ? OR IS_BEING_ANALYSED = ?)");
        
        db2Queries
            .put(DRD_GET_ALL_DRD,
                "SELECT DRD_ID, DRD_NAME, MODEL_ID FROM %s.GI_DATA_RULE_DEFINITIONS WHERE TENANT_ID = ? "
                    + "AND STATE = ? ");
        
        db2Queries
            .put(DRD_CHECK_STATE,
                "SELECT STATE, IS_BEING_ANALYSED, REV_NO FROM %s.GI_DATA_RULE_DEFINITIONS WHERE TENANT_ID = ? AND DRD_ID = ?");
        
        db2Queries
            .put(DRD_GET_MODEL_ID_FROM_DRD_ID,
                "SELECT MODEL_ID FROM %s.GI_DATA_RULE_DEFINITIONS WHERE TENANT_ID = ? AND DRD_ID = ?");

        // Model Management Module Queries
        db2Queries
            .put(GET_ML_MODEL_BY_VERSION,
                "SELECT MODEL FROM %s.GI_MODEL_STORE WHERE TENANT_ID = ? AND MODEL_ID = ? AND MODEL_VERSION_ID = ? ");

        db2Queries
            .put(GET_ML_MODEL_BY_CURRENT_VERSION,
                "SELECT MODEL FROM %s.GI_MODELS AS M, %s.GI_MODEL_STORE AS MS WHERE M.TENANT_ID = MS.TENANT_ID AND M.MODEL_ID = MS.MODEL_ID "
                    + "AND M.MODEL_VERSION_ID = MS.MODEL_VERSION_ID AND M.TENANT_ID = ? AND M.MODEL_ID = ?");
        db2Queries
        .put(GET_ML_REV_NO_BY_MODEL_ID,
            "SELECT REV_NO FROM %s.GI_MODEL_STORE WHERE TENANT_ID = ? AND MODEL_ID = ? ");

        if (dbType.startsWith(StorageConstants.DB_TYPE_ORCL)) {
            db2Queries.putAll(oracleOveridesQueries);
        } else if (dbType.startsWith(StorageConstants.DB_TYPE_MSSQL)) {
            db2Queries.putAll(sqlServerQueridess);
        }
        HashMap<String, String> queries = new HashMap<String, String>();
        for (String key : db2Queries.keySet()) {
            // Change all queries to lower case (for case sensitive databases)
            queries.put(key, db2Queries.get(key).toLowerCase());
        }
        return queries;
    }
}
