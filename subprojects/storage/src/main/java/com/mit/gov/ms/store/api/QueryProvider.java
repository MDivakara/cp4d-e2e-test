/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.store.api;

import org.apache.wink.json4j.JSONObject;

import com.mit.gov.ms.store.StorageException;

/**
 * @author Shaik.Nawaz
 *
 */
public interface QueryProvider {

	/***
	 * Method that will provide a query to check health of the database
	 * 
	 * @return a JSONObject which will contain a query to get The health of the
	 *         database
	 * 
	 *         <pre>
	 * 		{
	 * 			"sql_string" : "{@code<sql string>}"
	 * 		}
	 *         </pre>
	 * 
	 * @throws StorageException
	 */
	public JSONObject getDBVerifyQuery() throws StorageException;

	/***
	 * method to build the insert Query
	 * 
	 * @param insertJson is an object with fields as
	 * 
	 *                   <pre>
	 * {
	 *     "schema_name": "{@code<schema_name>}",
	 *     "table_name": "{@code<table_name>}",
	 *     "columns": [ "{@code<col1>}", "{@code<col2>}, ..." ] // {@code<JSONArray of column_names>}
	 *     "param_array": [ { "param_type": "{@code<param_type>}", "param_value": {@code<param_value>} } , ... ] // {@code<JSONArray of JSONObject containing param type(string) & value(Object)>}
	 * }
	 *                   </pre>
	 * 
	 *                   <b>Note</b> - The JSONObject should contain , schema_name,
	 *                   table_name and columns filed with proper value
	 * 
	 * @return same JSONObject which will contain an additional field "sql_string"
	 * 
	 *         <pre>
	 * {
	 *	   "sql_string" : "insert .... ",
	 *     "schema_name": "{@code<schema_name>}",
	 *     "table_name": "{@code<table_name>}",
	 *     "columns": [ "{@code<col1>}", "{@code<col2>}, ..." ] // {@code<JSONArray of column_names>}
	 *     "param_array": [ { "param_type": "{@code<param_type>}", "param_value": {@code<param_value>} } , ... ] // {@code<JSONArray of JSONObject containing param type(string) & value(Object)>}
	 * }
	 *         </pre>
	 * 
	 * @throws StorageException
	 */

	public JSONObject getInsertQuery(JSONObject insertJson) throws StorageException;

	/***
	 * method to build the Update Query
	 * 
	 * @param updateJson is an object with fields as
	 * 
	 *                   <pre>
	 *   {
	 *     "schema_name": "{@code<schema_name>}",
	 *     "table_name": "{@code<table_name>}",
	 *     "columns": [ "{@code<col1>}", "{@code<col2>}, ..." ] // {@code<JSONArray of column_names to be updated>}
	 *     "condtion_columns" : [ "{@code<col1>}", "{@code<col2>}, ..." ] // {@code<JSONArray of column_names for where clause>}
	 *     "param_array": [ { "param_type": "{@code<param_type>}", "param_value": {@code<param_value>} } , ... ] // {@code<JSONArray of JSONObject containing param type(string) & value(Object)>}
	 *   }
	 *                   </pre>
	 * 
	 *                   <b>Note</b> - The JSONObject should contain , schema_name,
	 *                   table_name and columns filed with proper value
	 * 
	 * @return same JSONObject which will contain an additional field "sql_string"
	 * 
	 *         <pre>
	 * {
	 *	   "sql_string" : "update .... ",
	 *     "schema_name": "{@code<schema_name>}",
	 *     "table_name": "{@code<table_name>}",
	 *     "columns": [ "{@code<col1>}", "{@code<col2>}, ..." ] // {@code<JSONArray of column_names to be updated>}
	 *     "condtion_columns" : [ "{@code<col1>}", "{@code<col2>}, ..." ] // {@code<JSONArray of column_names for where clause>}
	 *     "param_array": [ { "param_type": "{@code<param_type>}", "param_value": {@code<param_value>} } , ... ] // {@code<JSONArray of JSONObject containing param type(string) & value(Object)>}
	 *   }
	 *         </pre>
	 * 
	 * @throws StorageException
	 */

	public JSONObject getUpdateQuery(JSONObject updateJson) throws StorageException;

	/***
	 * method to build the Delete Query
	 * 
	 * @param deleteJson is an object with fields as
	 * 
	 *                   <pre>
	 *   {
	 *     "schema_name": "{@code<schema_name>}",
	 *     "table_name": "{@code<table_name>}",
	 *     "columns": [ "{@code<col1>}", "{@code<col2>}, ..." ] // {@code<JSONArray of column_names to be deleted>}
	 *     "condtion_columns" : [ "{@code<col1>}", "{@code<col2>}, ..." ] // {@code<JSONArray of column_names for where clause>}
	 *     "param_array": [ { "param_type": "{@code<param_type>}", "param_value": {@code<param_value>} } , ... ] // {@code<JSONArray of JSONObject containing param type(string) & value(Object)>}
	 *   }
	 *                   </pre>
	 * 
	 *                   <b>Note</b> - The JSONObject should contain , schema_name and
	 *                   table_name filed with proper value
	 * 
	 * @return same JSONObject which will contain an additional field "sql_string"
	 * 
	 *         <pre>
	 * {
	 *	   "sql_string" : "delete .... ",
	 *     "schema_name": "{@code<schema_name>}",
	 *     "table_name": "{@code<table_name>}",
	 *     "columns": [ "{@code<col1>}", "{@code<col2>}, ..." ] // {@code<JSONArray of column_names to be deleted>}
	 *     "condtion_columns" : [ "{@code<col1>}", "{@code<col2>}, ..." ] // {@code<JSONArray of column_names for where clause>}
	 *     "param_array": [ { "param_type": "{@code<param_type>}", "param_value": {@code<param_value>} } , ... ] // {@code<JSONArray of JSONObject containing param type(string) & value(Object)>}
	 *   }
	 *         </pre>
	 * 
	 * @throws StorageException
	 */

	public JSONObject getDeleteQuery(JSONObject deleteJson) throws StorageException;

	/***
	 * Method to fetch the create statement from existing statements and format it
	 * based on schema_name
	 * 
	 * @param deleteJson is an object with fields as
	 * 
	 *                   <pre>
	 * {
	 *     "schema_name": "{@code<schema_name>}",
	 *     "query_name": "{@code<query_name>}" // pre-defined
	 * }
	 *                   </pre>
	 * 
	 * @return same JSONObject which will contain an additional field "sql_string"
	 * 
	 *         <pre>
	 * {
	 * 	   "sql_string" : "create .... ",
	 *     "schema_name": "{@code<schema_name>}",
	 *     "query_name": "{@code<query_name>}" // pre-defined
	 * }
	 *         </pre>
	 * 
	 * @throws StorageException
	 */
	public JSONObject getCreateQuery(JSONObject createJson) throws StorageException;

    /***
     * Method to fetch the drop statement from tabble_name and schema_name
     * 
     * @param dropJson is an object with fields as
     * 
     *                   <pre>
     * {
     *     "schema_name": "{@code<schema_name>}",
     *     "table_name": "{@code<table_name>}"
     * }
     *                   </pre>
     * 
     * @return same JSONObject which will contain an additional field "sql_string"
     * 
     *         <pre>
     * {
     *     "sql_string" : "drop .... ",
     *     "schema_name": "{@code<schema_name>}",
     *     "table_name": "{@code<table_name>}" // pre-defined
     * }
     *         </pre>
     * 
     * @throws StorageException
     */
    public JSONObject getDropQuery(JSONObject dropJson) throws StorageException;

	/***
	 * -Method is used to build the select sql query string and add "sql_string" to
	 * sameJSON and return, which will then be used with "param_array" to execute
	 * sql query statement
	 * 
	 * @param queryJson
	 * 
	 *                  <pre>
	 * {
	 *     "schema_name": "{@code<schema_name>}",
	 *     "query_name": "{@code<query_name>}" // pre-defined
	 *     "offset" : "{@code<offset>}",   	// optional
	 *     "limit" : "{@code<limit>}",	   	// optional
	 *     "order_by" : "{@code<order_by>}" // optional
	 *   }
	 *                  </pre>
	 * 
	 * @return same JSONObject which will contain an additional field "sql_string"
	 * 
	 *         <pre>
	 * {
	 * 	   "sql_string" : "create .... ",
	 *     "schema_name": "{@code<schema_name>}",
	 *     "query_name": "{@code<query_name>}" // pre-defined
	 *     "offset" : "{@code<offset>}",
	 *     "limit" : "{@code<limit>}",
	 *     "order_by" : "{@code<order_by>}"
	 * }
	 *         </pre>
	 * 
	 * @throws StorageException
	 */
	public JSONObject getSelectQuery(JSONObject queryJson) throws StorageException;

}
