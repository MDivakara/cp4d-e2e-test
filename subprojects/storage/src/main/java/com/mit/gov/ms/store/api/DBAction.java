/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.store.api;

import java.io.InputStream;
import org.apache.wink.json4j.JSONObject;

import com.mit.gov.ms.store.StorageException;

/**
 * @author Shaik.Nawaz
 *
 */
public interface DBAction {

    /** Get The health of the database
     * @param dbName Name of the db when needed
     * @return JSONObject with DB Status
     * <pre>
     *         {
     *             "current_schema": "DASH101166",
     *             "db_type": "DB2\/LINUXX8664",
     *             "health": "ok"
     *         }
     *         {
     *             "error": "{@code<Error Message>}",
     *             "error_stack": "{@code<Stack trace of SQLException>}",
     *             "health": "not_ok"
     *         }
     * </pre>
     * 
     * @throws StorageException
     */
    public JSONObject getDBStatus(String dbName) throws StorageException;
    
    
    /** Begin a Transaction. It creates a connection to the database and returns a connectionID
     * @return connectionID for the transaction being started.
     * @throws StorageException
     * 
     * @see {@link #commitTransaction(String)}
     * @see {@link #rollBackTransaction(String)}
     */
    public String beginTransaction() throws StorageException;
    
    
    /** Commits a Transaction for specified connectionID
     * Implicitly closes the connection after commit
     * @param connectionId for the transaction to be committed
     * @throws StorageException
     * 
     * @see {@link #beginTransaction()}
     * @see {@link #rollBackTransaction(String)}
     */
    public void commitTransaction(String connectionId) throws StorageException;
    
    
    /** Rolls Back a Transaction for specified connectionID
     * Implicitly closes the connection after rollback
     * @param connectionId for the transaction to be rolled back
     * @throws StorageException
     * @see {@link #beginTransaction()}
     * @see {@link #commitTransaction(String)}
     */
    public void rollBackTransaction(String connectionId) throws StorageException;
    
    
    /** Initialize the schema; by creating required database entities (like table, views etc)
     * @param schema_name 
     * @param newVersion new version of the schema
     * @throws StorageException
     */
    public void initSchema(String schema_name, int newVersion) throws StorageException;
    
    
    /** Insert a row to the DataStore for specific schema 
     * @param insertJson JSONObject with data to be inserted and it's target DataEntity
     * <pre>
     * <b>FOR SQL DB</b> - 
     * - If "sql_string" is provided, that will be used with "param_array" to execute insert statement
     *   {
     *     "schema_name": "{@code<schema_name>}",
     *     "sql_string": "{@code<insert statement sql string>}",
     *     "param_array": [ { "param_type": "{@code<param_type>}", "param_value": {@code<param_value>} } , ... ] // {@code<JSONArray of JSONObject containing param type(string) & value(Object)>}
     *   }
     * - Else the same JSON will be passed to QueryProvider/Builder to form insert sql string using "schema_name", "table_name" & "columns"
     *   QueryProvider/Builder will build the insert sql string and add "sql_string" to sameJSON and return, 
     *   which will then be used with "param_array" to execute insert statement
     *   {
     *     "schema_name": "{@code<schema_name>}",
     *     "table_name": "{@code<table_name>}",
     *     "columns": [ "{@code<col1>}", "{@code<col2>}, ..." ] // {@code<JSONArray of column_names>}
     *     "param_array": [ { "param_type": "{@code<param_type>}", "param_value": {@code<param_value>} } , ... ] // {@code<JSONArray of JSONObject containing param type(string) & value(Object)>}
     *   }
     * <b>Note</b> -
     *   The sequence of columns in columns array / sql_string should match with sequence of parameters passed using param_array
     *   If "sql_string" is passed as part of inputJson, it must take care of putting schema_name as schema for DataEntity
     * </pre>
     * @return JSONObject with result of insert statement
     * <pre>
     *         {
     *             "sql_result": "{@code<row_count>}" // See {@link java.sql.PreparedStatement#executeUpdate()}
     *         }
     *         </>When Error</b>
     *         {
     *             "error": "{@code<Error Message>}",
     *             "error_code": "{@code<Sql Error Code>}", // {@link java.sql#SQLException()}
     *             "error_stack": "{@code<Stack trace of SQLException>}"
     *         }
     * </pre>
     * @throws StorageException
     */
    public JSONObject insert(JSONObject insertJson) throws StorageException;
    
    
    /** Update a row in the DataStore for specific schema 
     * @param updateJson JSONObject with data to be updated and it's target DataEntity
     * <pre>
     * <b>FOR SQL DB</b> - 
     * - If "sql_string" is provided, that will be used with "param_array" to execute update statement
     *   {
     *     "schema_name": "{@code<schema_name>}",
     *     "sql_string": "{@code<update statement sql string>}",
     *     "param_array": [ { "param_type": "{@code<param_type>}", "param_value": {@code<param_value>} } , ... ] // {@code<JSONArray of JSONObject containing param type(string) & value(Object)>}
     *   }
     * - Else the same JSON will be passed to QueryProvider/Builder to form update sql string using "schema_name", "table_name" & "columns"
     *   QueryProvider/Builder will build the update sql string and add "sql_string" to sameJSON and return, 
     *   which will then be used with "param_array" to execute update statement
     *   {
     *     "schema_name": "{@code<schema_name>}",
     *     "table_name": "{@code<table_name>}",
     *     "columns": [ "{@code<col1>}", "{@code<col2>}, ..." ] // {@code<JSONArray of column_names to be updated>}
     *     "condtion_columns" : [ "{@code<col1>}", "{@code<col2>}, ..." ] // {@code<JSONArray of column_names for where clause>}
     *     "param_array": [ { "param_type": "{@code<param_type>}", "param_value": {@code<param_value>} } , ... ] // {@code<JSONArray of JSONObject containing param type(string) & value(Object)>}
     *   }
     * <b>Note</b> -
     *   The sequence of columns in columns array / sql_string should match with sequence of parameters passed using param_array
     *   The parameters needed for where clause (i.e. condtion_columns) should follow after parameters for update in same param_array
     *   If "sql_string" is passed as part of updateJson, it must take care of putting schema_name as schema for DataEntity
     * </pre>
     * @return JSONObject with result of update statement
     * <pre>
     *         {
     *             "sql_result": "{@code<row_count>}" // See {@link java.sql.PreparedStatement#executeUpdate()}
     *         }
     *         </>When Error</b>
     *         {
     *             "error": "{@code<Error Message>}",
     *             "error_code": "{@code<Sql Error Code>}", // {@link java.sql#SQLException()}
     *             "error_stack": "{@code<Stack trace of SQLException>}"
     *         }
     * </pre>
     * @throws StorageException
     */
    public JSONObject update(JSONObject updateJson) throws StorageException;
    
    
    /** Delete a row from the DataStore for specific schema 
     * @param deleteJson JSONObject with condition for rows to be deleted and it's target DataEntity
     * <pre>
     * <b>FOR SQL DB</b> - 
     * - If "sql_string" is provided, that will be used with "param_array" to execute delete statement
     *   {
     *     "schema_name": "{@code<schema_name>}",
     *     "sql_string": "{@code<delete statement sql string>}",
     *     "param_array": [ { "param_type": "{@code<param_type>}", "param_value": {@code<param_value>} } , ... ] // {@code<JSONArray of JSONObject containing param type(string) & value(Object)>}
     *   }
     * - Else the same JSON will be passed to QueryProvider/Builder to form delete sql string using "schema_name", "table_name" & "columns"
     *   QueryProvider/Builder will build the delete sql string and add "sql_string" to sameJSON and return, 
     *   which will then be used with "param_array" to execute delete statement
     *   {
     *     "schema_name": "{@code<schema_name>}",
     *     "table_name": "{@code<table_name>}",
     *     "columns": [ "{@code<col1>}", "{@code<col2>}, ..." ] // {@code<JSONArray of column_names to be updated>}
     *     "condtion_columns" : [ "{@code<col1>}", "{@code<col2>}, ..." ] // {@code<JSONArray of column_names for where clause>}
     *     "param_array": [ { "param_type": "{@code<param_type>}", "param_value": {@code<param_value>} } , ... ] // {@code<JSONArray of JSONObject containing param type(string) & value(Object)>}
     *   }
     * <b>Note</b> -
     *   The sequence of columns in condtion_columns array / sql_string should match with sequence of parameters passed using param_array
     *   The parameters needed for where clause (i.e. condtion_columns) should be passed using param_array
     *   If "sql_string" is passed as part of deleteJson, it must take care of putting schema_name as schema for DataEntity
     * </pre>
     * @return JSONObject with result of delete statement
     * <pre>
     *         {
     *             "sql_result": "{@code<row_count>}" // See {@link java.sql.PreparedStatement#executeUpdate()}
     *         }
     *         </>When Error</b>
     *         {
     *             "error": "{@code<Error Message>}",
     *             "error_code": "{@code<Sql Error Code>}", // {@link java.sql#SQLException()}
     *             "error_stack": "{@code<Stack trace of SQLException>}"
     *         }
     * </pre>
     * @throws StorageException
     */
    public JSONObject delete(JSONObject deleteJson) throws StorageException;
    
    
    /** Create a DataEntity in DataStore for specific schema
     * @param createJson JSONObject with details of DataEntity to be created
     * <pre>
     * <b>FOR SQL DB</b> - 
     * - If "sql_string" is provided, that will be used to execute create statement
     *   {
     *     "schema_name": "{@code<schema_name>}",
     *     "sql_string": "{@code<create statement sql string>}"
     *   }
     * - Else the same JSON will be passed to QueryProvider/Builder to get create sql string using "schema_name", "query_name"
     *   QueryProvider/Builder will build the create sql string and add "sql_string" to sameJSON and return, 
     *   which will then be used with "param_array" to execute create statement
     *   {
     *     "schema_name": "{@code<schema_name>}",
     *     "query_name": "{@code<query_name>}" // pre-defined
     *   }
     * <b>Note</b> -
     *   If "sql_string" is passed as part of createJson, it must take care of putting schema_name as schema for DataEntity
     * </pre>
     * @return JSONObject with result of create statement
     * <pre>
     *         {
     *             "sql_result": "{@code<row_count>}" // See {@link java.sql.PreparedStatement#executeUpdate()}
     *         }
     *         </>When Error</b>
     *         {
     *             "error": "{@code<Error Message>}",
     *             "error_code": "{@code<Sql Error Code>}", // {@link java.sql#SQLException()}
     *             "error_stack": "{@code<Stack trace of SQLException>}"
     *         }
     * </pre>
     * @throws StorageException
     */
    public JSONObject create(JSONObject createJson) throws StorageException;
    
    
    /** Drop a DataEntity in DataStore for specific schema
     * @param dropJson JSONObject with details of DataEntity to be droped
     * <pre>
     * <b>FOR SQL DB</b> - 
     * - If "sql_string" is provided, that will be used to execute drop statement
     *   {
     *     "schema_name": "{@code<schema_name>}",
     *     "sql_string": "{@code<drop statement sql string>}"
     *   }
     * - Else the same JSON will be passed to QueryProvider/Builder to get drop sql string using "schema_name" & table_name
     *   QueryProvider/Builder will build the drop sql string and add "sql_string" to same JSON and return, 
     *   which will then be used to execute drop statement
     *   {
     *     "schema_name": "{@code<schema_name>}",
     *     "table_name": "{@code<table_name>}"
     *   }
     * <b>Note</b> -
     *   If "sql_string" is passed as part of dropJson, it must take care of putting schema_name as schema for DataEntity
     * </pre>
     * @return JSONObject with result of drop statement
     * <pre>
     *         {
     *             "sql_result": "{@code<row_count>}" // See {@link java.sql.PreparedStatement#executeUpdate()}
     *         }
     *         </>When Error</b>
     *         {
     *             "error": "{@code<Error Message>}",
     *             "error_code": "{@code<Sql Error Code>}", // {@link java.sql#SQLException()}
     *             "error_stack": "{@code<Stack trace of SQLException>}"
     *         }
     * </pre>
     * @throws StorageException
     */
    public JSONObject drop(JSONObject dropJson) throws StorageException;
    
    
    /** Insert a row to the DataStore for specific schema within a transaction.
     * Same as {@link #insert(JSONObject)} , but within in a transaction already started using {@link #beginTransaction()}
     * @param connectionId of the transaction being started using {@link #beginTransaction()}
     * @param insertJson see {@link #insert(JSONObject)}
     * @return see {@link #insert(JSONObject)}
     * @throws StorageException
     * @see {@link #insert(JSONObject)}
     * @see {@link #beginTransaction()}
     * @see {@link #commitTransaction(String)}
     * @see {@link #rollBackTransaction(String)}
     */
    public JSONObject insert(String connectionId, JSONObject insertJson) throws StorageException;
    
    
    /** Update a row in the DataStore for specific schema within a transaction.
     * Same as {@link #update(JSONObject)} , but within in a transaction already started using {@link #beginTransaction()}
     * @param connectionId of the transaction being started using {@link #beginTransaction()}
     * @param updateJson see {@link #update(JSONObject)}
     * @return see {@link #update(JSONObject)}
     * @throws StorageException
     * @see {@link #update(JSONObject)}
     * @see {@link #beginTransaction()}
     * @see {@link #commitTransaction(String)}
     * @see {@link #rollBackTransaction(String)}
     */
    public JSONObject update(String connectionId, JSONObject updateJson) throws StorageException;
    
    
    /** Delete a row in the DataStore for specific schema within a transaction.
     * Same as {@link #delete(JSONObject)} , but within in a transaction already started using {@link #beginTransaction()}
     * @param connectionId of the transaction being started using {@link #beginTransaction()}
     * @param deleteJson see {@link #delete(JSONObject)}
     * @return see {@link #delete(JSONObject)}
     * @throws StorageException
     * @see {@link #delete(JSONObject)}
     * @see {@link #beginTransaction()}
     * @see {@link #commitTransaction(String)}
     * @see {@link #rollBackTransaction(String)}
     */
    public JSONObject delete(String connectionId, JSONObject deleteJson) throws StorageException;

    
    /** Create a DataEntity in DataStore for specific schema within a transaction.
     * Same as {@link #create(JSONObject)} , but within in a transaction already started using {@link #beginTransaction()}
     * @param connectionId of the transaction being started using {@link #beginTransaction()}
     * @param createJson see {@link #create(JSONObject)}
     * @return see {@link #create(JSONObject)}
     * @throws StorageException
     * @see {@link #create(JSONObject)}
     * @see {@link #beginTransaction()}
     * @see {@link #commitTransaction(String)}
     * @see {@link #rollBackTransaction(String)}
     */
    public JSONObject create(String connectionId, JSONObject createJson) throws StorageException;
    
    
    /** Drop a DataEntity in DataStore for specific schema within a transaction.
     * Same as {@link #create(JSONObject)} , but within in a transaction already started using {@link #beginTransaction()}
     * @param connectionId of the transaction being started using {@link #beginTransaction()}
     * @param dropJson see {@link #drop(JSONObject)}
     * @return see {@link #drop(JSONObject)}
     * @throws StorageException
     * @see {@link #drop(JSONObject)}
     * @see {@link #beginTransaction()}
     * @see {@link #commitTransaction(String)}
     * @see {@link #rollBackTransaction(String)}
     */
    public JSONObject drop(String connectionId, JSONObject dropJson) throws StorageException;
    
    
    /** Execute a get/query on DataStore for specific schema
     * @param executeJson JSONObject with details of get/query to be executed
     * <pre>
     * <b>FOR SQL DB</b> - 
     * - If "sql_string" is provided, that will be used with "param_array" to execute query statement
     *   {
     *     "schema_name": "{@code<schema_name>}",
     *     "sql_string": "{@code<sql query string>}",
     *     "param_array": [ { "param_type": "{@code<param_type>}", "param_value": {@code<param_value>} } , ... ] // {@code<JSONArray of JSONObject containing param type(string) & value(Object)>}
     *   }
     * - Else the same JSON will be passed to QueryProvider/Builder to get sql query string using "schema_name", "query_name"
     *   QueryProvider/Builder will build the sql query string and add "sql_string" to sameJSON and return, 
     *   which will then be used with "param_array" to execute sql query statement
     *   {
     *     "schema_name": "{@code<schema_name>}",
     *     "query_name": "{@code<query_name>}" // pre-defined
     *   }
     * <b>Note</b> -
     *   The sequence of parameters passed using param_array must match as per the pre-defined query string
     *   If "sql_string" is passed as part of executeJson, it must take care of putting schema_name as schema for DataEntity
     * </pre>
     * @param formatter TODO
     * @return JSONObject with result of sql query statement
     * <pre>
     *         {
     *             "sql_result": { 
     *                   "columns": [ { "column_name": "{@code<column_name>}", "column_type": {@code<column_type>} } , ... ], // {@code<JSONArray of JSONObject containing column name & column type>}
     *                   "rows": [ { "{@code<column_name>}": {@code<value>} } , ... ] // {@code<JSONArray containing row data in JSONObject column_name:value(Object)>}
     *         }
     *         </>When Error</b>
     *         {
     *             "error": "{@code<Error Message>}",
     *             "error_code": "{@code<Sql Error Code>}", // {@link java.sql#SQLException()}
     *             "error_stack": "{@code<Stack trace of SQLException>}"
     *         }
     * </pre>
     * @throws StorageException
     */
    public JSONObject executeQuery(JSONObject executeJson, DBResultSetFormatter<?> formatter) throws StorageException;
    
    
    /** Execute a get/query on DataStore for specific schema
     * Same as {@link #executeQuery(JSONObject, DBResultSetFormatter)}
     * @param executeJson JSONObject with details of get/query to be executed {@link #executeQuery(JSONObject, DBResultSetFormatter)}
     * @param formatter TODO
     * @return InputStream with formatted result of sql query statement. Will return null in case of any error.
     * default format is CSV.
     * <pre>
     * column_name1, column_name2, ....
     * row1_col1_value, row1_col2_value, ...
     * row2_col1_value, row2_col2_value, ...
     * ....
     * .....
     * </pre>
     * @throws StorageException
     */
    public InputStream executeQueryAndFetchAsStream(JSONObject executeJson, DBResultSetFormatter<?> formatter) throws StorageException;
    
}
