/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.storage.versions;

import java.util.ArrayList;

import com.mit.gov.ms.store.StorageConstants;
import com.mit.gov.ms.store.schema.StorageSchema;

/**
 * @author shaik.Nawaz
 *
 */
public class InsightsStorageSchemaV2 extends StorageSchema {

    private String organizeby = (this.dbType.startsWith(StorageConstants.DB_TYPE_DB2)) ? " ORGANIZE BY ROW" : "";
    private String blobDef = (this.dbType.startsWith(StorageConstants.DB_TYPE_DB2)) ? "BLOB(100M)"
        : (this.dbType.startsWith(StorageConstants.DB_TYPE_MSSQL)) ? "VARBINARY(MAX)" : "BLOB";
    private String clobDef = (this.dbType.startsWith(StorageConstants.DB_TYPE_DB2)) ? "DBCLOB"
        : (this.dbType.startsWith(StorageConstants.DB_TYPE_MSSQL)) ? "NVARCHAR(MAX)" : "NCLOB";
    private String timestampDef = (this.dbType.startsWith(StorageConstants.DB_TYPE_DB2)) ? "TIMESTAMP(12)"
        : (this.dbType.startsWith(StorageConstants.DB_TYPE_MSSQL)) ? "DATETIME2" : "TIMESTAMP";

    private ArrayList<String> tableList = new ArrayList<String>();
    private ArrayList<String> allInitStatementList = new ArrayList<String>();
    private ArrayList<String> upgradeStatementList = new ArrayList<String>();

    public InsightsStorageSchemaV2(String dbType, int schemaVersion) {
        super(dbType, schemaVersion);
        ArrayList<String> initStatementList = new ArrayList<String>();
        ArrayList<String> updStatementList = new ArrayList<String>();
        if (this.dbType.startsWith(StorageConstants.DB_TYPE_MSSQL)) {
            initStatementList.add("CREATE SCHEMA %s");
        }
        /**
         * Notes - 
         * DB2 - CLOB for UTF-8, DBCLOB(double-byte) for UTF-16, 
         * ORACLE - DEFAULT CLAUSE SHOULD BE BEFOR NOT NULL 
         * ORACLE - Don't define a size when setting a CLOB (unlike a VARCHAR). it is just simply CLOB
         * ORACLE - NCLOB is for multi-byte character
         * SQLSERVER - N/VARCHAR(8000) is max size or use N/VARCHAR(MAX) for CLOB
         * SQLSERVER - VARBINARY(8000) is max size or use VARBINARY(MAX) for BLOB
         * SQLSERVER - TEXT data type is deprecated
         * SQLSERVER - create index does not take schema.indexname format
         * 
         */
        initStatementList
            .add("CREATE TABLE %s.GI_TASKS (TENANT_ID VARCHAR(36) NOT NULL, TASK_ID VARCHAR(36) NOT NULL, "
                + "REV_NO INTEGER DEFAULT 0 NOT NULL, TASK_TYPE CHAR(3) NOT NULL, SOURCE_ID VARCHAR(64) NOT NULL, "
                + "CREATED_BY VARCHAR(64) NOT NULL, CREATED_AT " + timestampDef
                + " DEFAULT CURRENT_TIMESTAMP NOT NULL, " + "UPDATED_AT " + timestampDef + ", STARTED_AT "
                + timestampDef + ", STATUS SMALLINT NOT NULL, STATUS_MESSAGE VARCHAR(256), "
                + " REQUEST_INFO VARCHAR(2000), OUTPUT_INFO VARCHAR(2000), " + " PRIMARY KEY (TENANT_ID, TASK_ID) )"
                + organizeby);
        initStatementList
            .add("CREATE TABLE %s.GI_SCA_RESULTS (TENANT_ID VARCHAR(36) NOT NULL, WKSP_ID VARCHAR(64) NOT NULL, "
                + "REV_NO INTEGER DEFAULT 0 NOT NULL, " + "UPDATED_AT " + timestampDef
                + " DEFAULT CURRENT_TIMESTAMP, NUM_GROUPS INTEGER, ISBEINGANALYSED CHAR(1), "
                + " PRIMARY KEY (TENANT_ID, WKSP_ID) )" + organizeby);
        initStatementList
            .add(
                "CREATE TABLE %s.GI_SCA_GROUPS (TENANT_ID VARCHAR(36) NOT NULL, WKSP_ID VARCHAR(64) NOT NULL, GROUP_ID VARCHAR(36) NOT NULL, "
                    + "REV_NO INTEGER DEFAULT 0 NOT NULL, REF_COL_NAME VARCHAR(256) NOT NULL, REF_COL_ID VARCHAR(64) NOT NULL, "
                    + "REF_ASSET_ID VARCHAR(64) NOT NULL, NUM_DATASETS INTEGER, NUM_COLS INTEGER, MIN_SIM_SCORE INTEGER, "
                    + "MAX_SIM_SCORE INTEGER, STATE VARCHAR(10), IS_EXPLICIT_RUN VARCHAR(5), " + "LAST_RUN_AT " + timestampDef
                    + " DEFAULT CURRENT_TIMESTAMP NOT NULL,  LAST_RUN_BY VARCHAR(64) NOT NULL, "
                    + " PRIMARY KEY (TENANT_ID, WKSP_ID, GROUP_ID) )" + organizeby);
        initStatementList
            .add(
                "CREATE TABLE %s.GI_SCA_GROUP_COLUMNS (TENANT_ID VARCHAR(36) NOT NULL, WKSP_ID VARCHAR(64) NOT NULL, GROUP_ID VARCHAR(36) NOT NULL, "
                    + "COL_ID VARCHAR(64) NOT NULL, REV_NO INTEGER DEFAULT 0 NOT NULL, COL_NAME VARCHAR(256), SIM_SCORE INTEGER, "
                    + "COL_LOCATION VARCHAR(1600), COL_INFO " + clobDef + ", "
                    + " PRIMARY KEY (TENANT_ID, WKSP_ID, GROUP_ID, COL_ID) )" + organizeby);
        if (dbType.startsWith(StorageConstants.DB_TYPE_DB2)) {
            initStatementList
                .add(
                    "CREATE TABLE %s.GI_MODELS (TENANT_ID VARCHAR(36) NOT NULL, MODEL_ID VARCHAR(36) NOT NULL, NAME VARCHAR(64) NOT NULL, "
                        + "DESCRIPTION VARCHAR(512), MODEL_TYPE VARCHAR(16) NOT NULL, MODEL_VERSION_ID VARCHAR(36) NOT NULL, "
                        + "REV_NO INTEGER DEFAULT 0 NOT NULL, MODEL_METADATA VARCHAR(256), "
                        + "CREATED_BY VARCHAR(64) NOT NULL, CREATED_AT " + timestampDef
                        + " DEFAULT CURRENT_TIMESTAMP NOT NULL, " + "UPDATED_BY VARCHAR(64),  UPDATED_AT "
                        + timestampDef + ", " + "SYSTEM_TIME_START " + timestampDef
                        + " NOT NULL IMPLICITLY HIDDEN GENERATED ALWAYS AS ROW BEGIN, " + "SYSTEM_TIME_END "
                        + timestampDef + " NOT NULL IMPLICITLY HIDDEN GENERATED ALWAYS AS ROW END, " + "TS_ID "
                        + timestampDef + " IMPLICITLY HIDDEN GENERATED ALWAYS AS TRANSACTION START ID, "
                        + " PRIMARY KEY (TENANT_ID, MODEL_ID, MODEL_VERSION_ID), PERIOD SYSTEM_TIME (SYSTEM_TIME_START, SYSTEM_TIME_END) )"
                        + organizeby);
            initStatementList.add("CREATE TABLE %s.GI_MODELS_HIST LIKE %s.GI_MODELS" + organizeby);
            initStatementList.add("ALTER TABLE %s.GI_MODELS ADD VERSIONING USE HISTORY TABLE %s.GI_MODELS_HIST");
        } else if (dbType.startsWith(StorageConstants.DB_TYPE_ORCL)) {
            initStatementList
                .add(
                    "CREATE TABLE %s.GI_MODELS (TENANT_ID VARCHAR(36) NOT NULL, MODEL_ID VARCHAR(36) NOT NULL, NAME VARCHAR(64) NOT NULL, "
                        + "DESCRIPTION VARCHAR(512), MODEL_TYPE VARCHAR(16) NOT NULL, MODEL_VERSION_ID VARCHAR(36) NOT NULL, "
                        + "REV_NO INTEGER DEFAULT 0 NOT NULL, MODEL_METADATA VARCHAR(256), "
                        + "CREATED_BY VARCHAR(64) NOT NULL, CREATED_AT " + timestampDef
                        + " DEFAULT CURRENT_TIMESTAMP NOT NULL, " + "UPDATED_BY VARCHAR(64),  UPDATED_AT "
                        + timestampDef + ", " + " PRIMARY KEY (TENANT_ID, MODEL_ID, MODEL_VERSION_ID) )" + organizeby);
            // , PERIOD FOR SYSTEM_TIME for business time, flashback query for system time
        } else if (dbType.startsWith(StorageConstants.DB_TYPE_MSSQL)) {
            initStatementList
                .add(
                    "CREATE TABLE %s.GI_MODELS (TENANT_ID VARCHAR(36) NOT NULL, MODEL_ID VARCHAR(36) NOT NULL, NAME VARCHAR(64) NOT NULL, "
                        + "DESCRIPTION VARCHAR(512), MODEL_TYPE VARCHAR(16) NOT NULL, MODEL_VERSION_ID VARCHAR(36) NOT NULL, "
                        + "REV_NO INTEGER DEFAULT 0 NOT NULL, MODEL_METADATA VARCHAR(256), "
                        + "CREATED_BY VARCHAR(64) NOT NULL, CREATED_AT " + timestampDef
                        + " DEFAULT CURRENT_TIMESTAMP NOT NULL, " + "UPDATED_BY VARCHAR(64),  UPDATED_AT "
                        + timestampDef + ", " + "SYSTEM_TIME_START " + timestampDef
                        + " GENERATED ALWAYS AS ROW START HIDDEN NOT NULL, " + "SYSTEM_TIME_END " + timestampDef
                        + " GENERATED ALWAYS AS ROW END HIDDEN NOT NULL, "
                        + " PRIMARY KEY (TENANT_ID, MODEL_ID, MODEL_VERSION_ID), "
                        + "PERIOD FOR SYSTEM_TIME (SYSTEM_TIME_START, SYSTEM_TIME_END) ) "
                        + "WITH(SYSTEM_VERSIONING = ON) ");
        }
        initStatementList
            .add("CREATE TABLE %s.GI_MODEL_STORE (TENANT_ID VARCHAR(36) NOT NULL, MODEL_ID VARCHAR(36) NOT NULL, "
                + "MODEL_VERSION_ID VARCHAR(36) NOT NULL, REV_NO INTEGER DEFAULT 0 NOT NULL, MODEL " + blobDef
                + " NOT NULL, " + " PRIMARY KEY (TENANT_ID,  MODEL_ID, MODEL_VERSION_ID) )" + organizeby);
        initStatementList
            .add(
                "CREATE TABLE %s.GI_DATA_RULE_DEFINITIONS (TENANT_ID VARCHAR(36) NOT NULL, DRD_ID VARCHAR(36) NOT NULL, "
                    + "REV_NO INTEGER DEFAULT 0 NOT NULL, DRD_NAME VARCHAR(64) NOT NULL, TASK_ID VARCHAR(36), "
                    + "ASSET_ID VARCHAR(64), UPDATED_AT " + timestampDef + ", " + "UPDATED_BY VARCHAR(64), CREATED_AT "
                    + timestampDef + " DEFAULT CURRENT_TIMESTAMP NOT NULL, CREATED_BY VARCHAR(64) NOT NULL, "
                    + "MODEL_ID VARCHAR(64), USER_TUNED VARCHAR(1), IS_BEING_ANALYSED VARCHAR(1), STATE VARCHAR(8), "
                    + " DRD_DESCRIPTION VARCHAR(256), REQUEST_INFO VARCHAR(2000), "
                    + " PRIMARY KEY (TENANT_ID, DRD_ID) )" + organizeby);
        
        // Indexes
        String schemaStr = "%s.";
        if (dbType.startsWith(StorageConstants.DB_TYPE_MSSQL)) {
            schemaStr = "";
        }
        // Task
        initStatementList.add("CREATE INDEX " + schemaStr + "IDX_GI_TASKS ON %s.GI_TASKS (TASK_TYPE, STATUS)");
        initStatementList.add("CREATE INDEX " + schemaStr + "IDX_GI_TASKS_SOURCE ON %s.GI_TASKS (TENANT_ID, SOURCE_ID, TASK_TYPE)");
        // SCA
        initStatementList
            .add("CREATE INDEX " + schemaStr + "IDX_GI_SCA_GROUPS_REFCOL ON %s.GI_SCA_GROUPS (TENANT_ID, WKSP_ID, REF_COL_NAME)");
        initStatementList.add("CREATE INDEX " + schemaStr + "IDX_GI_SCA_GROUPS_STAT ON %s.GI_SCA_GROUPS (TENANT_ID, WKSP_ID, STATE)");
        // DRD
        initStatementList.add("CREATE INDEX " + schemaStr + "IDX_GI_DRD_ASSET ON %s.GI_DATA_RULE_DEFINITIONS (TENANT_ID, ASSET_ID)");
        initStatementList
            .add(
                "CREATE INDEX " + schemaStr + "IDX_GI_DRD_STATE ON %s.GI_DATA_RULE_DEFINITIONS (TENANT_ID, DRD_ID, DRD_NAME, STATE, IS_BEING_ANALYSED)");
        
        // Let it be the last in list

        initStatementList
            .add("CREATE TABLE %s.GI_SCHEMA_VERSION (SCHEMA_VERSION INTEGER NOT NULL, " + "CREATED_AT " + timestampDef
                + " DEFAULT CURRENT_TIMESTAMP NOT NULL " + ", PRIMARY KEY (SCHEMA_VERSION) )" + organizeby);
        initStatementList
            .add("INSERT INTO %s.GI_SCHEMA_VERSION ( SCHEMA_VERSION ) VALUES (" + this.getSchemaVersion() + ")");

        for (String stmt: initStatementList) {
            // Change all statements to lower case (for case sensitive databases)
            allInitStatementList.add(stmt.toLowerCase());
        }
        
        if (dbType.startsWith(StorageConstants.DB_TYPE_DB2)) {
            updStatementList.add("ALTER TABLE %s.GI_SCA_GROUPS ALTER COLUMN REF_COL_NAME SET DATA TYPE VARCHAR(256)");
            updStatementList.add("ALTER TABLE %s.GI_SCA_GROUP_COLUMNS ALTER COLUMN COL_NAME SET DATA TYPE VARCHAR(256)");
            updStatementList.add("ALTER TABLE %s.GI_SCA_GROUP_COLUMNS ALTER COLUMN COL_LOCATION SET DATA TYPE VARCHAR(1600)");
            updStatementList.add("UPDATE %s.GI_SCHEMA_VERSION SET SCHEMA_VERSION =" + this.getSchemaVersion());
        } else if (dbType.startsWith(StorageConstants.DB_TYPE_ORCL)) {
            updStatementList.add("ALTER TABLE %s.GI_SCA_GROUPS MODIFY REF_COL_NAME VARCHAR(256)");
            updStatementList.add("ALTER TABLE %s.GI_SCA_GROUP_COLUMNS MODIFY COL_NAME VARCHAR(256)");
            updStatementList.add("ALTER TABLE %s.GI_SCA_GROUP_COLUMNS MODIFY COL_LOCATION VARCHAR(1600)");
            updStatementList.add("UPDATE %s.GI_SCHEMA_VERSION SET SCHEMA_VERSION =" + this.getSchemaVersion());
        } else if (dbType.startsWith(StorageConstants.DB_TYPE_MSSQL)) {
            updStatementList.add("ALTER TABLE %s.GI_SCA_GROUPS ALTER COLUMN REF_COL_NAME VARCHAR(256)");
            updStatementList.add("ALTER TABLE %s.GI_SCA_GROUP_COLUMNS ALTER COLUMN COL_NAME VARCHAR(256)");
            updStatementList.add("ALTER TABLE %s.GI_SCA_GROUP_COLUMNS ALTER COLUMN COL_LOCATION VARCHAR(1600)");
            updStatementList.add("UPDATE %s.GI_SCHEMA_VERSION SET SCHEMA_VERSION =" + this.getSchemaVersion());
        }
        
        for (String stmt: updStatementList) {
            // Change all statements to lower case (for case sensitive databases)
            upgradeStatementList.add(stmt.toLowerCase());
        }
    }

    public ArrayList<String> getInitStatementList() {
        return allInitStatementList;
    }

    public ArrayList<String> getUpgradeStatementList(int currVersion, int newVersion) {
        /**
         * For future schema upgradation understanding
         * 
         * ArrayList<String> statementList = new ArrayList<String>(); 
         * //upgrade list from V1 
         * statementList.addAll(new InsightsStorageSchemaV1(dbType,1).getUpgradeStatementList(0, 1));
         * 
         * //upgrade list V2 statementList.addAll(upgradeStatementList);
         * return statementList;
         */
        
        return upgradeStatementList;
    }

    @Override
    public ArrayList<String> getTableList() {

        if (tableList.isEmpty()) {
            tableList.add("%s.GI_TASKS".toLowerCase());
            tableList.add("%s.GI_SCA_RESULTS".toLowerCase());
            tableList.add("%s.GI_SCA_GROUPS".toLowerCase());
            tableList.add("%s.GI_SCA_GROUP_COLUMNS".toLowerCase());
            tableList.add("%s.GI_MODELS".toLowerCase());
            tableList.add("%s.GI_MODEL_STORE".toLowerCase());
            tableList.add("%s.GI_DATA_RULE_DEFINITIONS".toLowerCase());
            tableList.add("%s.GI_SCHEMA_VERSION".toLowerCase());
        }

        return tableList;
    }

}
