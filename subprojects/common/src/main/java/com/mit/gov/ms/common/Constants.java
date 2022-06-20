/*
 * IBM Confidential
 * OCO Source Materials
 * 5724-Q36
 * Copyright IBM Corp. 2019
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common;

public class Constants {

    public static final String INSIGHTS_JNDI_DS = "jdbc/insightsServiceDS";
    public static final String XMETA_JNDI_DS = "jdbc/xmetaServiceDS";
    
    public static final String DATABASE_ID = "database_id";
    public static final String DATABASE_NAME = "database_name";
    public static final String IS_DB_TABLE = "is_db_table";

    public static final String RID = "rid";
    public static final String TABLE_RID = "table_rid";
    public static final String TABLE_NAME = "table_name";

    public static final String COLUMN_RID = "column_rid";
    public static final String COLUMN_NAME = "column_name";
    public static final String COLUMN_DESC = "column_description";

    public static final String CA_RESULTS_JSON = "results_json";
    public static final String COLUMN_IDENTITY = "column_identity";

    public static final String DISTINCT_VALUES = "num_of_distinct_values";
    public static final String TOTAL_VALUES = "total_num_of_values";

    public static final String INFERRED_DATA_CLASS = "inferred_data_class";
    public static final String SAMPLE_VALUES = "sample_values";

    public static final String SCHEMA_NAME = "schema_name";
    public static final String HOST_NAME = "host_name";

    public static final String TERM_ASSIGNMENTS = "term_assignments";
    public static final String SIMILARITY = "similarity_score";

    public static final String SIMILAR_COLUMNS = "similar_columns";
    public static final String REF_DATA = "ref_data";
    public static final String CUSTOM_CLASS = "custom_class";
    public static final String DATA_RULES = "data_rules";
    public static final String PII_DATA = "pii_data";
    public static final String CRITICAL_DATA_ELEMENTS = "critical_data_elements";
    public static final String AUTOMATIONRULES = "AUTOMATIONRULES";

    public static final String LABEL_NAME = "label_name";
    public static final String TERM_RID = "term_rid";
    public static final String CONTEXT_PATH = "context_path";

    public static final String WORKSPACE = "workspace_name";
    public static final String WORKSPACE_RID = "workspace_rid";
    public static final String QUALITY_SCORE_BENCHMARK = "quality_score_benchmark";
    public static final String QUALITY_SCORE = "quality_score";
    public static final String TYPE = "type";
    public static final String DATA_ELEMENT_RID = "data_element_rid";
    public static final String COLUMN = "column";

    public static final String ENABLE = "enable";
    public static final String FEATURE = "feature";
    public static final String NOT_AVAILABLE = "not available";
    public static final String FEATURES = "features";
    public static final String EXECUTION_PERIOD_HOUR = "execution_period_hours";
    public static final String EXECUTION_COUNT = "execution_count";
    public static final String MAX_EXECUTION_COUNT = "max_execution_count";
    public static final String LAST_EXECUTION_TIME = "last_execution_time";
    public static final String LAST_COMPLETION_TIME = "last_completion_time";
    public static final String EXECUTION_INTERVAL_HOURS = "execution_interval_hours";
    public static final String ONE_TIME_EXECUTION = "one_time_execution";
    public static final String STARTED_BY = "started_by";
    public static final String ENABLED_BY = "enabled_by";
    public static final String ENABLED_AT = "enabled_at";
    public static final String SCHEDULED_AT = "scheduled_at";
    public static final String SUGGESTED_CLASSIFIER = "suggested_classifier_type";
    public static final String VALUE_LIST_CLASSIFIER = "valid_values";
    public static final String DOMAIN_FINGER_PRINT_CLASSIFIER = "column_similarity";

    public static final String SUGGESTED_STATE = "suggested_state";
    public static final String SUGGESTED = "suggested";
    public static final String REJECTED = "rejected";
    public static final String ACCEPTED = "accepted";
    public static final String COLUMNS = "columns";

    public static final String CLASS_CODE = "class_code";
    public static final String CLASS_NAME = "class_name";
    public static final String CLASS_DESCRIPTION = "class_description";

    public static final String ACTION = "action";
    public static final String REJECT = "reject";
    public static final String ACCEPT = "accept";
    public static final int WORKSPACE_ID_LENGTH = 64;
    public static final double MINIMUM_CONFIDENCE = 0.75;
    public static final String CONFIDENCE = "confidence_threshold";

    public static final String GROUPID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    public static final String AUTH_TOKEN_PATH = "/api/auth_token/v1/tokens";

    public static final String MAX_VALUE = "max_value";
    public static final String MIN_VALUE = "min_value";
    public static final String MEAN_VALUE = "mean_value";
    public static final String STANDARD_DEVIATION = "standard_deviation";
    public static final String COLUMNS_SIZE = "columns_size";
    public static final String DATASET_SIZE = "datasets_size";
    public static final String IS_REFERENCE_COLUMN = "is_reference_column";
    public static final String MAX_SIMILARITY = "max_similarity";
    public static final String MIN_SIMILARITY = "min_similarity";
    public static final String REFERENCE_COLUMN = "reference_column";
    public static final String CONNECTION_ID = "connection_id";
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String CONNECTOR_NAME = "connector_name";
    public static final String DBMS_SERVER_INSTANCE = "dbms_server_instance";
    public static final String DB_TYPE = "db_type";
    public static final String DBMS = "dbms";
    public static final String CONNECTION_NAME = "connection_name";
    public static final String RESULT = "result";
    public static final String CONTAINS_USER = "contains_user";
    public static final String ASSET_URL = "/igc-rest/v1/assets/";
    public static final String IMAM_CONN_URL = "/imam/dcm/getConn/";
    public static final String _NAME = "_name";
    public static final String DB_USER = "db_user";
    public static final String DB_PASSWORD = "db_password";
    public static final String DB_TABLE_NAME = "db_table_name";
    public static final String DB_HOST_NAME = "db_host_name";
    public static final String DB_NAME = "db_name";
    public static final String DB_ID = "db_id";
    public static final String DB_SCHEMA_NAME = "db_schema_name";
    public static final String DB_CONNECTOR_NAME = "db_connector_name";
    public static final String DB_SERVER_INSTANCE_NAME = "db_sever_instance_name";
    public static final String DB_PROVIDER = "db_provider";
    public static final String DB_CONNECTION_NAME = "db_connection_name";
    public static final String _TYPE = "_type";
    public static final String DATA_CONNECTIONS = "data_connections";
    public static final String ITEMS = "items";
    public static final String _ID = "_id";
    public static final String DATA_CONNECTION = "data_connection";
    public static final String PARAMETERS = "parameters";
    public static final String _CONTEXT = "_context";
    public static final String HOST = "host";
    public static final String DATABASE = "database";
    public static final String DATABASE_SCHEMA = "database_schema";
    public static final String DATA_CONNECTORS = "data_connectors";

    public static final String DATA = "data";
    public static final String DOC_ID = "_id";
    public static final String ADMIN_DB = "anomaly";
    public static final String DB_HOST = "db_host";
    public static final String DB_PORT = "db_port";
    public static final String STATUS_CODE = "status_code";
    public static final String CODE = "code";
    public static final String ANOMALY_STATUS_MESSAGE = "anomaly_status";
    public static final String ASSOCIATIONS = "associations";
    public static final String NUM_OF_COLUMNS = "num_columns";
    public static final String ANOMALOUS = "anomalous";
    public static final String EXPLANATION = "explanations";
    public static final String ANOMALY_DETAILS = "anomaly_details";
    public static final String ROW_INDEX = "row_index";
    public static final String ERROR_MESSAGE = "error_message";
    public static final String SCORING_RESULTS = "scoring_results";
    public static final String DIAGNOSIS_DETAILS = "diagnosis_details";
    public static final String MODEL_IS_STORED_IN_DB = "The model is stored in DB";

    public static final String PORT = "port";
    public static final String USERNAME = "username";
    public static final String FILEPATH = "filepath";
    public static final String FILE_NAME = "file_name";
    public static final String DB2 = "DB2";
    public static final String MYSQL = "MYSQL";
    public static final String ORACLE = "ORACLE";
    public static final String SQLSERVER = "SQLSERVER";
    public static final String LOCALFS = "LOCALFS";
    public static final String RECORD_COUNT = "record_count";
    public static final String GET_RECORD_COUNT = "get_record_count";
    public static final String LAST_IMPORT = "last_import";
    public static final String CUSTOM_CLASSES = "custom_classes";
    
    public static final String ROWS = "rows";
    public static final String RUN_ON_NONCLASSIFIED_ONLY = "run_on_nonclassified_only";
    
    public static final String REFERENCE_COLUMN_NAME = "ref_col_name";
    public static final String REFERENCE_COLUMN_ID = "ref_col_id";
    public static final String REFERENCE_ASSET_ID = "ref_asset_id";
    public static final String NO_ACTION = "no_action";
    public static final String COLUMN_ID = "column_id";
    public static final String YES = "yes";
    public static final String TASK_NEEDS_TO_REGISTER = "task_needs_to_be_register";
    public static final String RESTORE = "restore";
    public static final String METADATA = "metadata";
    public static final String ENTITY = "entity";
    public static final String GROUPS = "groups";
    
    public static final String WORKSPACE_NAME = "workspace_name";
    public static final String DATA_SET = "data_set";
    public static final String DRD_EXISTS = "drd_exists";
    public static final String RESOURCES = "resources";
    public static final String DATATYPE = "datatype";
    public static final String NUMERIC = "numeric";
    public static final String INTEGER = "integer";
    public static final String IS_DUPLICATE = "is_duplicate";
    public static final String COLS_JSON = "cols_json";
    public static final String GET_ADVANCEDRESULTS_XMETA = "advancedresults_xmeta";

    public static final String LOCATION_PATH_DELIMITER = ">";

    public static final String RUN_FOR_SINGLE_COLUMN = "run_for_single_column";
    public static final String TOTAL_COUNT = "total_count";
    public static final String TASKS = "tasks";
    public static final String IS_ADDED = "is_added";

    // Messages & Error
    public static final String MESSAGE = "message";
    public static final String ERROR = "error";
    
    public static final String AUTH_STRING_MSG = "User Basic/JWT Bearer access token";
    
    public static final String ANALYSING = "analysing";
    public static final String ANALYSIS_NOT_STARTED = "Analysis not started";
    public static final String ANALYSIS_ABORTED = ANALYSIS_NOT_STARTED; // "Last Run Aborted, Please submit new request
                                                                        // to Run analysis"
    public static final String ANALYSIS_INITIATED_SAR = "Analysis started. You can find the generated rules in Catalog, in the Glossary and Governance asset group, with names SAR_<ruleaction>_<serialnumber>";
    public static final String ANALYSIS_SCHEDULED_SAR = ANALYSIS_INITIATED_SAR;
    public static final String ANALYSIS_INITIATED_SCC = "Analysis started. You can find the suggested custom class by calling given endpoints : a) <PATH>/suggestions/custom_class, b) <PATH>/suggestions/custom_class/{workspace_rid}";
    public static final String ANALYSIS_SCHEDULED_SCC = ANALYSIS_INITIATED_SCC;
    public static final String ANALYSIS_INITIATED = "Analysis started";
    public static final String ANALYSIS_SCHEDULED = ANALYSIS_INITIATED;

    public static final String LEARNING_COLUMN_CONSTRAINTS = "Learning column constraints";
    public static final String LEARNING_ASSOCIATIONS = "Learning associations";
    public static final String EXCEPTION_WHILE_READING_CONNECTION_DETAILS = "Exception while trying to read connection details.";
    public static final String EXCEPTION_WHILE_VALIDATING_CONNECTION = "Exception while Validating Connection Details.";
    public static final String EXCEPTION_WHILE_TRAINING = "Exception while training";
    public static final String UNSUPPORTED_DB_TYPE = "unsupported db type";
    public static final String ANALYSIS_HAS_STARTED = "analysis has started"; // Change message(see scc and sar
                                                                                // messages)
    public static final String STARTING_TRAINING_PROCESS = "Starting the training process.";
    public static final String IGCINTERACTOR_TEST_TABLERID_QUERY = "select table.RID, table.NAME from cmviews.PDRDATABASETABLE as table inner join cmviews.PDRDATABASESCHEMA as schema on table.ofdataschemarid = schema.rid WHERE table.NAME = ? AND schema.name = 'TEST'";
    public static final String TRAINING_CONTROLLER_TEST_TABLERID_QUERY = "select table.RID, table.NAME from cmviews.PDRDATABASETABLE as table inner join cmviews.PDRDATABASESCHEMA as schema on table.ofdataschemarid = schema.rid WHERE table.NAME IN ('BANK_MARKET1', 'BANK_MARKET2', 'BANK_MARKET3') AND schema.name = 'TEST'";
    public static final String NO_TRAINING_REQUEST_FOUND = "no training request found for the given table rid";
    public static final String TRAINING_ACCEPED_SUCCESFULLY = "Training request accepted successfully";
    public static final String ACCEPTED_FOR_SCORING = "Accepted for Scoring";
    public static final String UNAUTHORISED = "UNAUTHORIZED";
  
    public static final String DRD_WITH_SAME_NAME_EXISITS = "DRD with same name present";
    public static final String DRD_WITH_SAME_ASSET_AND_COLS_PRESENT = "DRD with same asset and columns combination present";
    
    public static final String SYSTEM_USER = "system";
    
    // Insights Metadata columns
    public static final String TENANT_ID = "tenant_id";
    public static final String TASK_ID = "task_id";
    public static final String REV_NO = "rev_no";
    public static final String TASK_TYPE = "task_type";
    public static final String SOURCE_ID = "source_id";
    public static final String CREATED_BY = "created_by";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_BY = "updated_by";
    public static final String UPDATED_AT = "updated_at";
    public static final String STARTED_AT = "started_at";
    public static final String CURRENT_TS = "current_ts";
    public static final String STATUS = "status";
    public static final String STATUS_MESSAGE = "status_message";
    public static final String REQUEST_INFO = "request_info";
    public static final String OUTPUT_INFO = "output_info";
    public static final String WKSP_ID = "wksp_id";
    public static final String ISBEINGANALYSED = "isbeinganalysed";
    public static final String NUM_GROUPS = "num_groups";
    public static final String GROUP_ID = "group_id";
    public static final String REF_COL_NAME = "ref_col_name";
    public static final String REF_COL_ID = "ref_col_id";
    public static final String REF_ASSET_ID = "ref_asset_id";
    public static final String NUM_DATASETS = "num_datasets";
    public static final String NUM_COLS = "num_cols";
    public static final String MIN_SIM_SCORE = "min_sim_score";
    public static final String MAX_SIM_SCORE = "max_sim_score";
    public static final String STATE = "state";
    public static final String IS_EXPLICIT_RUN = "is_explicit_run";
    public static final String LAST_RUN_AT = "last_run_at";
    public static final String LAST_RUN_BY = "last_run_by";
    public static final String COL_ID = "col_id";
    public static final String COL_NAME = "col_name";
    public static final String SIM_SCORE = "sim_score";
    public static final String COL_LOCATION = "col_location";
    public static final String COL_INFO = "col_info";
    public static final String MODEL_ID = "model_id";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String MODEL_TYPE = "model_type";
    public static final String MODEL_VERSION_ID = "model_version_id";
    public static final String MODEL_METADATA = "model_metadata";
    public static final String MODEL = "model";
    public static final String DRD_ID = "drd_id";
    public static final String DRD_NAME = "drd_name";
    public static final String ASSET_ID = "asset_id";
    public static final String USER_TUNED = "user_tuned";
    public static final String IS_BEING_ANALYSED = "is_being_analysed";
    public static final int COL_LOCATION_MAX_LENGTH = 1600;
    
    //public static final String IS_ACTIVE = "is_active";
    public static final String DRD_DESCRIPTION = "drd_description";
    // End Insights Metadata columns
	public static final String COLUMNS_DATATYPE = "columns_datatype";
	
	//for creating custom columns
    public static final String COLUMN_WEIGHT = "columnWeight";
    public static final Object GET_CA_RESULTS = "caResults";
    public static final String IS_WKC = "is_wkc";

}
