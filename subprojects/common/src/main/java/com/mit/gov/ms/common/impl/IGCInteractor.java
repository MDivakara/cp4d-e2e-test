/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.impl;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.http.HttpResponse;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.mit.gov.ms.common.CatalogsMetaDataStore;
import com.mit.gov.ms.common.Constants;
import com.mit.gov.ms.common.HttpUtils;
import com.mit.gov.ms.common.InsightsConfiguration;
import com.mit.gov.ms.common.InsightsException;
import com.mit.gov.ms.common.interfaces.CatalogInteractor;
import com.mit.gov.ms.common.interfaces.ICatalogsMetaDataStore;
import com.mit.gov.ms.common.storage.CatalogsSQLRSFormater;
import com.mit.gov.ms.store.StorageConstants;
import com.mit.gov.ms.store.db.sql.SQLDBHelper;

public class IGCInteractor implements CatalogInteractor {
	private static final Logger LOGGER = LoggerFactory.getLogger(IGCInteractor.class);
	private static String IISBASEURL = InsightsConfiguration.getInstance().getIISBaseUrl();
	private static String basic_auth = InsightsConfiguration.getInstance().getIISBasicAuthToken();
	
	private ICatalogsMetaDataStore catalogStore = null;

	@Override
	public JSONObject getConnDetailsOfDataAsset(String tableRID) throws JSONException, InsightsException {
		JSONObject res = new JSONObject();
		JSONObject finalRes = new JSONObject();
		try {
			// get table details
			res = getAssetDetails(tableRID);
			if (res.containsKey(Constants.CODE) && res.getInt(Constants.CODE) != 200) {
				return res;
			}
			parseTableResult(res, finalRes);

			// get database details which contains the given table
			res = getAssetDetails(finalRes.getString(Constants.DB_ID));
			if (res.containsKey(Constants.CODE) && res.getInt(Constants.CODE) != 200) {
				return res;
			}
			parseDatabaseResult(res, finalRes);

			// get connection details
			res = getAssetDetails(finalRes.getString(Constants.CONNECTION_ID));
			if (res.containsKey(Constants.CODE) && res.getInt(Constants.CODE) != 200) {
				return res;
			}
			parseConnectionDetails(res, finalRes);

			// get user id and password to access the database
			res = getUserDetails(finalRes.getString(Constants.CONNECTION_ID));
			if (res.containsKey(Constants.CODE) && res.getInt(Constants.CODE) != 200) {
				return res;
			}
			parseUserDetails(res, finalRes);

		} catch (JSONException e) {
			LOGGER.error("JSONException : Error while parsing connection detils" + e.getMessage());
			throw e;
		} catch (InsightsException e) {
			LOGGER.error("InsightsException : Error while getting connection details: " + e.getMessage());
			throw e;
		}
		return finalRes;
	}

	/**
	 * This methods takes two JSONObjects connRes and finalRes as input and appends
	 * required fields from dbRes to finalRes
	 * 
	 * @param dbRes: JSONObject containing database details
	 * @param finalRes: JSONObject containing connection details to which Connection
	 *        details(user and password) details to be appended
	 * @return finalRes: JSONObject which contains connection details
	 * @throws InsightsException:
	 */
	private void parseUserDetails(JSONObject connRes, JSONObject finalRes) throws JSONException {
		try {
			if (connRes.getJSONObject(Constants.PARAMETERS).containsKey("Username")
					&& connRes.getJSONObject(Constants.PARAMETERS).getString("Username") != null
					&& !connRes.getJSONObject(Constants.PARAMETERS).getString("Username").isEmpty()) {
				finalRes.put(Constants.DB_USER, connRes.getJSONObject(Constants.PARAMETERS).getString("Username"));
			}
			if (connRes.getJSONObject(Constants.PARAMETERS).containsKey("Password")
					&& connRes.getJSONObject(Constants.PARAMETERS).getString("Password") != null
					&& !connRes.getJSONObject(Constants.PARAMETERS).getString("Password").isEmpty()) {
				finalRes.put(Constants.DB_PASSWORD, connRes.getJSONObject(Constants.PARAMETERS).getString("Password"));
			}
			if (finalRes.containsKey(Constants.CONNECTION_ID)) {
				finalRes.remove(Constants.CONNECTION_ID);
			}
			if (finalRes.containsKey(Constants.DB_ID)) {
				finalRes.remove(Constants.DB_ID);
			}
		} catch (JSONException e) {
			LOGGER.error("Error while getting parsing user details : " + e.getMessage());
			throw e;
		}

	}

	/**
	 * This methods takes JSONObject tableRes and returns connection details
	 * JSONObject
	 * 
	 * @param dbRes: JSONObject containing database details
	 * @return finalRes: JSONObject which contains connection details
	 * @throws InsightsException:
	 */
	private void parseTableResult(JSONObject tableRes, JSONObject finalRes) throws JSONException {
		try {

			finalRes.put(Constants.DB_TABLE_NAME, tableRes.getString(Constants._NAME));
			JSONArray context = new JSONArray();
			context = tableRes.getJSONArray(Constants._CONTEXT);
			for (int i = 0, size = context.length(); i < size; i++) {
				JSONObject cont = context.getJSONObject(i);
				switch (cont.getString(Constants._TYPE)) {
				case Constants.HOST:
					finalRes.put(Constants.DB_HOST_NAME, cont.getString(Constants._NAME));
					break;
				case Constants.DATABASE:
					finalRes.put(Constants.DB_NAME, cont.getString(Constants._NAME));
					finalRes.put(Constants.DB_ID, cont.getString(Constants._ID));
					break;
				case Constants.DATABASE_SCHEMA:
					finalRes.put(Constants.DB_SCHEMA_NAME, cont.getString(Constants._NAME));
					break;
				default:
					break;
				}

			}
		} catch (JSONException e) {
			LOGGER.error("Error while parsing table details : " + e.getMessage());
			throw e;
		}

	}

	/**
	 * This methods takes two JSONObjects connRes and finalRes as input and appends
	 * required fields from dbRes to finalRes
	 * 
	 * @param connRes: JSONObject containing database details
	 * @param finalRes: JSONObject containing connection details to which extra
	 *        connection details to be appended
	 * @return finalRes: JSONObject which contains connection details
	 * @throws InsightsException:
	 */
	private void parseConnectionDetails(JSONObject connRes, JSONObject finalRes) throws JSONException {
		try {
			// to add jdbc support.
			finalRes.put(Constants.DB_CONNECTOR_NAME,
					connRes.getJSONObject(Constants.DATA_CONNECTORS).getString(Constants._NAME));
			if(finalRes.containsKey("db_connector_name")&&finalRes.getString("db_connector_name").toUpperCase().replace("CONNECTOR", "").contentEquals("JDBC")) {
				//jdbc:db2:\/\/dashdb-txn-sbox-yp-lon02-02.services.eu-gb.bluemix.net:50000\/BLUDB
				String connStringUrlPart[]=connRes.getString("connection_string").split("//",2);
				String connStringDbmsTypePart[] = connStringUrlPart[0].split(":"); // jdbc:db2 , jdbc:ibm:hive
				String dbmsType = connStringDbmsTypePart[connStringDbmsTypePart.length -1]; //db2
				String databaseName=null;
				if (connStringDbmsTypePart.length == 3) { // jdbc:ibm:hive
                    databaseName = connStringDbmsTypePart[1]; 
                }
				if (connStringUrlPart.length > 1) {
                    String connStringHostPart[] = connStringUrlPart[1].split(":", 3);
                    String dbHost = connStringHostPart[0];
                    String connStringPortAndDBPart = connStringHostPart[1];
                    String portPart = connStringPortAndDBPart.split("/")[0];
                    portPart = portPart.split(";")[0];
                    // 32051/bigsql , 10000;MaxStringSize=8192
                    // 52000/BIGSQL:sslConnection=true;sslTrustStoreLocation=/etc/pki/java/client_truststore.jks;
                    int dbPort = Integer.parseInt(portPart);
                    
                    
                    if (connStringDbmsTypePart.length == 2) { // jdbc:db2
                        String databasePart[] = connStringPortAndDBPart.split("/");
                        if (databasePart.length > 1) {
                            databaseName = databasePart[1]; 
                            
                        }
                    }
                    finalRes.put(Constants.DB_HOST, dbHost);
                    finalRes.put(Constants.DB_PORT, dbPort);
                    finalRes.put(Constants.DB_NAME, databaseName);  
                }
			}
		} catch (JSONException e) {
			LOGGER.error("Error while parsing connection details : " + e.getMessage());
			throw e;
		}
	}

	/**
	 * This methods takes two JSONObjects dbRes and finalRes as input and appends
	 * required fields from dbRes to finalRes
	 * 
	 * @param dbRes: JSONObject containing database details
	 * @param finalRes: JSONObject containing connection details to which db details
	 *        to be appended
	 * @return finalRes: JSONObject which contains connection details
	 * @throws InsightsException:
	 */
	private void parseDatabaseResult(JSONObject dbRes, JSONObject finalRes) throws JSONException {
		try {
			// finalRes.put(Constants.DB_SERVER_INSTANCE_NAME,
			// dbRes.getString(Constants.DBMS_SERVER_INSTANCE));

			if (dbRes.containsKey(Constants.DBMS)) {
				if (dbRes.getString(Constants.DBMS).toUpperCase().contains(Constants.DB2)) {
					finalRes.put(Constants.DB_PROVIDER, Constants.DB2); // for db type like: DB2/Linux...
				} else { // add further conditions if formatting required for other db types.
					finalRes.put(Constants.DB_PROVIDER, dbRes.getString(Constants.DBMS));
				}
				finalRes.put(Constants.DB_TYPE, dbRes.getString(Constants._TYPE));
			}
			JSONArray items = new JSONArray();
			items = dbRes.getJSONObject(Constants.DATA_CONNECTIONS).getJSONArray(Constants.ITEMS);
			for (int i = 0, size = items.length(); i < size; i++) {
				JSONObject item = items.getJSONObject(i);
				switch (item.getString(Constants._TYPE)) {
				case Constants.DATA_CONNECTION:
					finalRes.put(Constants.DB_CONNECTION_NAME, item.getString(Constants._NAME));
					finalRes.put(Constants.CONNECTION_ID, item.getString(Constants._ID));
					break;
				default:
					break;
				}
			}

		} catch (JSONException e) {
			LOGGER.error("Error while parsing database results : " + e.getMessage());
			throw e;
		}
	}

	/**
	 * This methods take connID as input and retrieves metadata related to that
	 * connection via get HTTP call
	 * 
	 * @param assetId: String which contains assetId
	 * @return resp: JSONObject which contains metadata of that asset (gives
	 *         username and password )
	 * @throws InsightsException:
	 */
	private JSONObject getUserDetails(String assetId) throws JSONException, InsightsException {
		String connURL = IISBASEURL + Constants.IMAM_CONN_URL + assetId + "?maskPassword=false";
		Map<String, String> header = new HashMap<String, String>();
		header.put("Content-Type", "application/json");
		header.put("Authorization", "Basic " + basic_auth);

		return getResponse(connURL, header);
	}

	/**
	 * This methods take assetId as input and retrieves metadata related to that
	 * asset via get HTTP call
	 * 
	 * @param assetId: String which contains assetId
	 * @return resp: JSONObject which contains metadata of that asset
	 * @throws JSONException InsightsException:
	 */
	private JSONObject getAssetDetails(String assetId) throws JSONException, InsightsException {
		String tableURL = IISBASEURL + Constants.ASSET_URL + assetId;
		Map<String, String> header = new HashMap<String, String>();
		header.put("Content-Type", "application/json");
		header.put("Authorization", "Basic " + basic_auth);
		return getResponse(tableURL, header);
	}

	/**
	 * This methods take URL String as input and retrieves metadata related to that
	 * asset via get HTTP call
	 * 
	 * @param assetId: String which contains assetId
	 * @return resp: JSONObject which contains metadata of that asset
	 * @throws JSONException,InsightsException , InsightsException
	 */
	private JSONObject getResponse(String url, Map<String, String> header) throws JSONException, InsightsException {
		JSONObject resp = new JSONObject();
		try {
			// making REST GET call for fetching connection details given the collectionRID
			HttpResponse HTTPResp = HttpUtils.executeRestAPI(url, "GET", header, null);
			resp = HttpUtils.parseResponseAsJSONObject(HTTPResp);

		} catch (InsightsException e) {
			LOGGER.error(
					"InsightsException exception : error while retrieving asset details  " + e.getMessage());
			throw e;
		}

		return resp;
	}

    @Override
    public JSONArray getColumnAnalysisResults(String projectRid) throws InsightsException {
        // TODO Auto-generated method stub
        catalogStore = CatalogsMetaDataStore.getInstance();
        JSONArray cAResultsArray = new JSONArray();

        try {
            String queryIdentifier = "XMETA_COLUMN_ANALYSIS_RESULTS_QUERY";
            JSONArray param_array = new JSONArray();
            String[] formats = {};

            param_array.put(SQLDBHelper.createParamString(projectRid));

            JSONObject result = catalogStore.get("WORKSPACE", queryIdentifier, formats, param_array,
                    CatalogsSQLRSFormater.FORMAT_XMETA_CARESULTS_JSON);

            if (result != null && !result.isEmpty() && result.has(StorageConstants.SQL_RESULT)) {
                cAResultsArray = result.getJSONObject(StorageConstants.SQL_RESULT).getJSONArray(Constants.ROWS);
            }

        } catch (JSONException e) {
            LOGGER.error("Exception while getColumnAnalysisResults - projectRid=" + projectRid, e);
            throw new InsightsException("Exception while getColumnAnalysisResults - projectRid=" + projectRid, e);
        }

        return cAResultsArray;
    }

}
