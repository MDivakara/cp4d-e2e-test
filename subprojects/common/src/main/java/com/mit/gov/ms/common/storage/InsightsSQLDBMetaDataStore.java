/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.storage;

import java.io.InputStream;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.mit.gov.ms.common.Constants;
import com.mit.gov.ms.common.InsightsConfiguration;
import com.mit.gov.ms.common.InsightsException;
import com.mit.gov.ms.store.StorageConstants;
import com.mit.gov.ms.store.StorageException;
import com.mit.gov.ms.store.api.DBResultSetFormatter;
import com.mit.gov.ms.store.api.DBStore;
import com.mit.gov.ms.store.schema.StorageTables;


public class InsightsSQLDBMetaDataStore extends BaseInsightsMetaDataStore {

    private String schema_name = InsightsConfiguration.getInstance().getInsightsSchemaName();
    
    public InsightsSQLDBMetaDataStore(DBStore dbStore, DBResultSetFormatter<?> resultSetFormatter) {
        super(dbStore, resultSetFormatter);
    }

    private StorageTables getTable(String tableName) throws InsightsException {
        try {
            return InsightsTables.valueOf(tableName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InsightsException("Invalid table name");
        }
    }
    
    private void checkforTenantId(JSONObject conditionJson) throws InsightsException {
        if (!conditionJson.isNull(Constants.TENANT_ID)) {
            return;
        }
        throw new InsightsException("TENANT_ID not specified.");
    }
    
    private int getRevisionNo(JSONObject conditionJson) throws InsightsException {
        try {
            if (!conditionJson.isNull(Constants.REV_NO)) {
                return conditionJson.getInt(Constants.REV_NO);
            }
            if (!conditionJson.isNull(Constants.REV_NO)) {
                return conditionJson.getInt(Constants.REV_NO);
            }
        } catch (JSONException e) {
            throw new InsightsException(e);
        }
        //return 0;
        throw new InsightsException("REV_NO not specified.");
    }
    
    @Override
    public JSONObject save(String dbStoreName, String dbItemId, JSONObject jsonData) throws InsightsException {
        StorageTables table = getTable(dbStoreName);
        try {
            checkforTenantId(jsonData);                
            JSONObject insertJson =
                table.getOps().getInsertJson(schema_name, jsonData).getJSONObject(StorageConstants.SQL_JSON_JSON);
            return dbStore.insert(insertJson);
        } catch (StorageException | JSONException e) {
            throw new InsightsException(e);
        }
    }

    @Override
    public JSONObject update(String dbStoreName, String dbItemId, JSONObject jsonData, JSONObject updateCondition) throws InsightsException {
        StorageTables table = getTable(dbStoreName);
        try {
            checkforTenantId(updateCondition);
            int rev_no_for_update = getRevisionNo(updateCondition);
            jsonData.put(Constants.REV_NO, ++rev_no_for_update);
            JSONObject updateJson = table
                .getOps()
                .getUpdateJson(schema_name, jsonData, updateCondition)
                .getJSONObject(StorageConstants.SQL_JSON_JSON);
            return dbStore.update(updateJson);
        } catch (StorageException | JSONException e) {
            throw new InsightsException(e);
        }
    }

    @Override
    public JSONObject get(String dbStoreName, String dbQueryIdentifier, String[] formats, JSONArray param_array, String resultFormatId) throws InsightsException {
        return get(dbStoreName, dbQueryIdentifier, formats, param_array, resultFormatId, null, 0, 0);
    }
    
    @Override
    public JSONObject get(String dbStoreName, String dbQueryIdentifier, String[] formats, JSONArray param_array,
        String resultFormatId, String orderBy, int offSet, int limit)
        throws InsightsException {
        JSONObject executeJson = new JSONObject();
        try {
            executeJson.put(StorageConstants.SCHEMA_NAME, schema_name);
            executeJson.put(StorageConstants.QUERY_NAME, dbQueryIdentifier);
            executeJson.put(StorageConstants.FORMAT_RESULTS, resultFormatId);
            executeJson.put(StorageConstants.FORMAT_STRINGS, formats);
            executeJson.put(StorageConstants.PARAM_ARRAY, param_array);
            if (orderBy != null) {
                executeJson.put(StorageConstants.ORDER_BY, orderBy.toLowerCase());
            }
            if (limit > 0) {
                executeJson.put(StorageConstants.LIMIT, limit);
                executeJson.put(StorageConstants.OFFSET, (offSet < 0 ? 0 : offSet));
            }
            JSONObject result = dbStore.executeQuery(executeJson, this.resultSetFormatter);
            return result;
        } catch (JSONException | StorageException e) {
            throw new InsightsException(e);
        }
    }
    
    @Override
    public JSONObject delete(String dbStoreName, String dbItemId, JSONObject deleteCondition) throws InsightsException {
        StorageTables table = getTable(dbStoreName);
        try {
            checkforTenantId(deleteCondition);
            //getRevisionNo(deleteCondition);
            JSONObject deleteJson =
                table.getOps().getDeleteJson(schema_name, deleteCondition).getJSONObject(StorageConstants.SQL_JSON_JSON);
            return dbStore.delete(deleteJson);
        } catch (StorageException | JSONException e) {
            throw new InsightsException(e);
        }
    }

    @Override
    public void deleteNotInList(String dbStoreName, JSONArray workspaceInfo) throws InsightsException {
        // TODO Auto-generated method stub

    }

    @Override
    public void addConstraintsByID(String dbStoreName, JSONObject result) throws InsightsException {
        // TODO Auto-generated method stub

    }

    @Override
    public JSONObject getConstraintsByID(String dbStoreName, String assetID) throws InsightsException {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public InputStream getInputStream(String dbStoreName, String dbQueryIdentifier, String[] formats,
            JSONArray param_array, String resultFormatId) throws InsightsException {
        JSONObject executeJson = new JSONObject();
        try {
            executeJson.put(StorageConstants.SCHEMA_NAME, schema_name);
            executeJson.put(StorageConstants.QUERY_NAME, dbQueryIdentifier);
            executeJson.put(StorageConstants.FORMAT_RESULTS, resultFormatId);
            executeJson.put(StorageConstants.FORMAT_STRINGS, formats);
            executeJson.put(StorageConstants.PARAM_ARRAY, param_array);
            return dbStore.executeQueryAndFetchAsStream(executeJson, this.resultSetFormatter);

        } catch (JSONException | StorageException e) {
            throw new InsightsException(e);
        }
    }

}
