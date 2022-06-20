/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.store;

/**
 * @author Shaik.Nawaz
 *
 */
public class StorageConstants {

    public final static String DEPENDENCY_STATUS = "health";
    public final static String CHECKED_AT = "checked_at";
    public final static String OK = "ok";
    public final static String NOT_OK = "not_ok";

    public final static String DB_TYPE = "db_type";
    public final static String DB_TYPE_DB2 = "db2";
    public final static String DB_TYPE_ORCL = "oracle";
    public final static String DB_TYPE_MSSQL = "sqlserver";
    public final static String ERROR = "error";
    public final static String ERROR_STACK = "error_stack";
    public final static String FILE_DB_LOCATION = "db_location";
    public final static String ROOT_NODE = "root_node";

    public final static String SQL_STRING_KEY = "sql_string";
    public final static String SQL_RESULT = "sql_result";

    public final static String ROWS = "rows";
    public final static String COLUMNS = "columns";
    public final static String COLUMN_NAME = "column_name";
    public final static String COLUMN_TYPE = "column_type";
    public final static String PARAM_ARRAY = "param_array";
    public final static String PARAM_TYPE = "param_type";
    public final static String PARAM_IS_NULL = "param_is_null";
    public final static String PARAM_IS_CURRENT_TS = "param_is_current_ts";
    public final static String PARAM_VALUE = "param_value";
    public final static String PARAM_TS_NANO = "param_ts_nano";
    public final static String SCHEMA = "current_schema";
    public final static String SELECT_FROM = "select_from";
    public final static String SQL_JSON_TYPE = "TYPE";
    public final static String SQL_JSON_JSON = "SQL_JSON";
    public final static String SQL_JSON_TYPE_INSERT = "INSERT";
    public final static String SQL_JSON_TYPE_INSERT_FROM = "INSERT_FROM";
    public final static String SQL_JSON_TYPE_UPDATE = "UPDATE";
    public final static String SQL_JSON_TYPE_DELETE = "DELETE";

    public final static String INIT_SCHEMA_SQL_STRING_KEY = "init_sql_strings";
    public final static String ERROR_CODE = "error_code";
    public final static String EXPECTED_ERROR_CODE = "expected_error_code";
    
    public final static String SCHEMA_NAME = "schema_name";
    public final static String TABLE_NAME = "table_name";
    public final static String CONDITION_COLUMNS = "condition_columns";
    public final static String QUERY_NAME = "query_name";
    public final static String FORMAT_STRINGS = "format_strings";
    public final static String INNER_QUERY = "inner_query";
    public final static String DATASOURCE_USED = "datasource_used";
    public final static String BUSINESS_TIME = "business_time";
    public final static String BUSINESS_AS_OF = "business_time_as_of";
    public final static String BUSINESS_AS_OF_CURRENT = "business_time_as_of_current";
    public final static String BUSINESS_FROM_TO = "business_time_from_to";
    public final static String BUSINESS_BETWEEN = "business_time_between";
    public final static String BUSINESS_BETWEEN_CURRENT = "business_time_between_current";
    public final static String BUSINESS_BETWEEN_START_CURRENT = "business_time_between_start_current";
    public final static String BUSINESS_BETWEEN_END_CURRENT = "business_time_between_end_current";
    public final static String TECHNICAL_TIME = "technical_time";
    public final static String TECHNICAL_AS_OF = "technical_time_as_of";
    public final static String TECHNICAL_AS_OF_CURRENT = "technical_time_as_of_current";
    public final static String TECHNICAL_FROM_TO = "technical_time_from_to";
    public final static String TECHNICAL_BETWEEN = "technical_time_between";
    public final static String TECHNICAL_BETWEEN_CURRENT = "technical_time_between_current";
    public final static String TECHNICAL_BETWEEN_START_CURRENT = "technical_time_between_start_current";
    public final static String TECHNICAL_BETWEEN_END_CURRENT = "technical_time_between_end_current";
    public final static String FORMAT_RESULTS = "format_results";
    public final static String CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP";
    //public final static String CURRENT_SPACE_TIMESTAMP = "CURRENT_TIMESTAMP";

    // FileUtils
    public final static String NODE_TYPE = "dbstore_name";
    public final static String SUB_TYPE = "db_item_id";
    
    // CloudantUtil
    public static final String DB_NAME = NODE_TYPE;
    public static final String ASSET_ID = SUB_TYPE;
    public static final String DATA = "data";
    public static final String DOC_ID = "_id";

    // Data Types to put in setParamater
    public final static String STRING = "string";
    public final static String INT = "int";
    public final static String LONG = "long";
    public final static String DOUBLE = "double";
    public final static String TIMESTAMP = "timestamp";
    public final static String BLOB = "blob";
    public final static String CLOB = "clob";
    
    public final static String ORDER_BY  = "order_by";
    public final static String LIMIT  = "limit";
    public final static String OFFSET  = "offset";
                
}
