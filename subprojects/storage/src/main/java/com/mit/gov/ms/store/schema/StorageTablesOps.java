/*
 * IBM Confidential
 * OCO Source Materials
 * 5724-Q36
 * Copyright IBM Corp. 2019
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.mit.gov.ms.store.schema;

import java.io.IOException;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.mit.gov.ms.store.SQLTimestampWrapper;
import com.mit.gov.ms.store.StorageConstants;
import com.mit.gov.ms.store.StorageException;
import com.mit.gov.ms.store.db.sql.SQLDBHelper;

/**
 * @author Shaik.Nawaz
 *
 */
public class StorageTablesOps {

    private String name = null;

    private String[][] columns = null;

    public StorageTablesOps(String name, String[][] columns) {
        this.name = name;
        this.columns = columns;
    }

    @Override
    public String toString() {
        return name;
    }

    public String name() {
        return name;
    }

    public String[] getColumnsArray() {
        String[] result = new String[columns.length];
        for (int i=0; i<columns.length; i++) {
            result[i] = columns[i][0];
        }
        return result;
    }

    public JSONObject getInsertJson(String schema_name, JSONObject param_values) throws StorageException {
        JSONObject insertJSON = new JSONObject();
        try {
            insertJSON.put(StorageConstants.SCHEMA_NAME, schema_name);
            insertJSON.put(StorageConstants.TABLE_NAME, name());
            JSONArray columns_array = new JSONArray();
            JSONArray param_array = new JSONArray();
            for (int i = 0; i < columns.length; i++) {
                if (param_values.has(columns[i][0].toLowerCase())) {
                    columns_array.put(columns[i][0]);
                    param_array.put(SQLDBHelper.createParams(param_values.get(columns[i][0].toLowerCase()), columns[i][1]));
                }
                else if (param_values.has(columns[i][0])) {
                    columns_array.put(columns[i][0]);
                    param_array.put(SQLDBHelper.createParams(param_values.get(columns[i][0]), columns[i][1]));
                }
            }
            insertJSON.put(StorageConstants.COLUMNS, columns_array);
            insertJSON.put(StorageConstants.PARAM_ARRAY, param_array);
            return new JSONObject().put(StorageConstants.SQL_JSON_TYPE, StorageConstants.SQL_JSON_TYPE_INSERT)
                .put(StorageConstants.SQL_JSON_JSON, insertJSON);
        } catch (JSONException | IOException e) {
            throw new StorageException("Error while getInsertJson for " + name(), e);
        }

    }

    public JSONObject getInsertJson(String schema_name, Object... param_values) throws StorageException {
        JSONObject insertJSON = new JSONObject();
        if (columns.length == param_values.length) {
            try {
                insertJSON.put(StorageConstants.SCHEMA_NAME, schema_name);
                insertJSON.put(StorageConstants.TABLE_NAME, name());
                JSONArray columns_array = new JSONArray();
                JSONArray param_array = new JSONArray();
                for (int i = 0; i < columns.length; i++) {
                    columns_array.put(columns[i][0]);
                    param_array.put(SQLDBHelper.createParams(param_values[i], columns[i][1]));
                }
                insertJSON.put(StorageConstants.COLUMNS, columns_array);
                insertJSON.put(StorageConstants.PARAM_ARRAY, param_array);
                return new JSONObject().put(StorageConstants.SQL_JSON_TYPE, StorageConstants.SQL_JSON_TYPE_INSERT)
                    .put(StorageConstants.SQL_JSON_JSON, insertJSON);
            } catch (JSONException | IOException e) {
                throw new StorageException("Error while getInsertJson for " + name(), e);
            }
        } else {
            throw new StorageException("Incorrect parameter lists while getInsertJson-"+ columns.length + ","+ param_values.length);
        }
    }

    public JSONObject getInsertFromSelectJson(String schema_name, String selectQry, String[] formats, Object... param_values) throws StorageException {
        JSONObject insertJSON = new JSONObject();
        try {
            insertJSON.put(StorageConstants.SCHEMA_NAME, schema_name);
            insertJSON.put(StorageConstants.TABLE_NAME, name());
            JSONArray columns_array = new JSONArray();
            JSONArray param_array = new JSONArray();
            for (int i = 0; i < columns.length; i++) {
                columns_array.put(columns[i][0]);
            }
            for (int i = 0; i < param_values.length; i++) {
                param_array.put(param_values[i]);
            }
            insertJSON.put(StorageConstants.COLUMNS, columns_array);
            insertJSON.put(StorageConstants.SELECT_FROM, selectQry);
            if (formats != null && formats.length > 0) {
                insertJSON.put(StorageConstants.FORMAT_STRINGS, formats);
            }
            insertJSON.put(StorageConstants.PARAM_ARRAY, param_array);
            return new JSONObject().put(StorageConstants.SQL_JSON_TYPE, StorageConstants.SQL_JSON_TYPE_INSERT_FROM)
                .put(StorageConstants.SQL_JSON_JSON, insertJSON);
        } catch (JSONException e) {
            throw new StorageException("Error while getInsertJson for " + name(), e);
        }
    }

    public JSONObject getDeleteJson(String schema_name, boolean business_time, SQLTimestampWrapper bstart, SQLTimestampWrapper bend,
        String[] condition_columns, Object... param_values) throws StorageException {
        JSONObject deleteJSON = new JSONObject();
        if (condition_columns.length == param_values.length) {
            try {
                JSONArray condition_columns_array = new JSONArray();
                JSONArray param_array = new JSONArray();
                deleteJSON.put(StorageConstants.SCHEMA_NAME, schema_name);
                deleteJSON.put(StorageConstants.TABLE_NAME, name());
                if (business_time) {
                    if (bstart != null && bend!= null&& !bstart.isNull() && !bend.isNull()) {
                        if (bstart.isCurrentTimeStamp() && bend.isCurrentTimeStamp()) {
                            deleteJSON.put(StorageConstants.BUSINESS_TIME, StorageConstants.BUSINESS_BETWEEN_CURRENT);
                        } else if (bstart.isCurrentTimeStamp()) {
                            deleteJSON.put(StorageConstants.BUSINESS_TIME, StorageConstants.BUSINESS_BETWEEN_START_CURRENT);
                            param_array.put(SQLDBHelper.createParams(bend, StorageConstants.TIMESTAMP));
                        } else if (bend.isCurrentTimeStamp()) {
                            deleteJSON.put(StorageConstants.BUSINESS_TIME, StorageConstants.BUSINESS_BETWEEN_END_CURRENT);
                            param_array.put(SQLDBHelper.createParams(bstart, StorageConstants.TIMESTAMP));
                        } else {
                            deleteJSON.put(StorageConstants.BUSINESS_TIME, StorageConstants.BUSINESS_BETWEEN);
                            param_array.put(SQLDBHelper.createParams(bstart, StorageConstants.TIMESTAMP));
                            param_array.put(SQLDBHelper.createParams(bend, StorageConstants.TIMESTAMP));
                        }
                    }
                }
                if (condition_columns !=null) {
                    for (int c = 0; c < condition_columns.length; c++) {
                        for (int i = 0; i < columns.length; i++) {
                            if (condition_columns[c].equalsIgnoreCase(columns[i][0])) {
                                condition_columns_array.put(columns[i][0]);
                                param_array.put(SQLDBHelper.createParams(param_values[c], columns[i][1]));
                            }
                        }
                    }
                }
                deleteJSON.put(StorageConstants.CONDITION_COLUMNS, condition_columns_array);
                deleteJSON.put(StorageConstants.PARAM_ARRAY, param_array);
                return new JSONObject().put(StorageConstants.SQL_JSON_TYPE, StorageConstants.SQL_JSON_TYPE_DELETE)
                    .put(StorageConstants.SQL_JSON_JSON, deleteJSON);
            } catch (JSONException | IOException e) {
                throw new StorageException("Error while getDeleteJson for " + name(), e);
            }
        } else {
            throw new StorageException("Incorrect parameter lists for getDeleteJson-"+ condition_columns.length + ","+ param_values.length);
        }
    }

    public JSONObject getDeleteJson(String schema_name, JSONObject condition_columns)
        throws StorageException {
        return getDeleteJson(schema_name, false, null, null, condition_columns);
    }

    public JSONObject getDeleteJson(String schema_name, boolean business_time, SQLTimestampWrapper bstart, SQLTimestampWrapper bend,
        JSONObject condition_columns) throws StorageException {
        JSONObject deleteJSON = new JSONObject();
        try {
            JSONArray condition_columns_array = new JSONArray();
            JSONArray param_array = new JSONArray();
            deleteJSON.put(StorageConstants.SCHEMA_NAME, schema_name);
            deleteJSON.put(StorageConstants.TABLE_NAME, name());
            if (business_time) {
                if (bstart != null && bend!= null&& !bstart.isNull() && !bend.isNull()) {
                    if (bstart.isCurrentTimeStamp() && bend.isCurrentTimeStamp()) {
                        deleteJSON.put(StorageConstants.BUSINESS_TIME, StorageConstants.BUSINESS_BETWEEN_CURRENT);
                    } else if (bstart.isCurrentTimeStamp()) {
                        deleteJSON.put(StorageConstants.BUSINESS_TIME, StorageConstants.BUSINESS_BETWEEN_START_CURRENT);
                        param_array.put(SQLDBHelper.createParams(bend, StorageConstants.TIMESTAMP));
                    } else if (bend.isCurrentTimeStamp()) {
                        deleteJSON.put(StorageConstants.BUSINESS_TIME, StorageConstants.BUSINESS_BETWEEN_END_CURRENT);
                        param_array.put(SQLDBHelper.createParams(bstart, StorageConstants.TIMESTAMP));
                    } else {
                        deleteJSON.put(StorageConstants.BUSINESS_TIME, StorageConstants.BUSINESS_BETWEEN);
                        param_array.put(SQLDBHelper.createParams(bstart, StorageConstants.TIMESTAMP));
                        param_array.put(SQLDBHelper.createParams(bend, StorageConstants.TIMESTAMP));
                    }
                }
            }
            if (condition_columns !=null) {
                for (int c = 0; c < condition_columns.length(); c++) {
                    for (int i = 0; i < columns.length; i++) {
                        if (condition_columns.has(columns[i][0].toLowerCase())) {
                            condition_columns_array.put(columns[i][0]);
                            param_array.put(SQLDBHelper.createParams(condition_columns.get(columns[i][0].toLowerCase()), columns[i][1]));
                        } else if (condition_columns.has(columns[i][0])) {
                            condition_columns_array.put(columns[i][0]);
                            param_array.put(SQLDBHelper.createParams(condition_columns.get(columns[i][0]), columns[i][1]));
                        }
                    }
                }
            }
            deleteJSON.put(StorageConstants.CONDITION_COLUMNS, condition_columns_array);
            deleteJSON.put(StorageConstants.PARAM_ARRAY, param_array);
            return new JSONObject().put(StorageConstants.SQL_JSON_TYPE, StorageConstants.SQL_JSON_TYPE_DELETE)
                .put(StorageConstants.SQL_JSON_JSON, deleteJSON);
        } catch (JSONException | IOException e) {
            throw new StorageException("Error while getDeleteJson for " + name(), e);
        }
    }
    public JSONObject getUpdateJson(String schema_name, boolean business_time, SQLTimestampWrapper bstart, SQLTimestampWrapper bend, String[] upd_columns,
        String[] condition_columns, Object... param_values) throws StorageException {
        JSONObject updateJSON = new JSONObject();
        if ((upd_columns.length + condition_columns.length) == param_values.length) {
            try {
                JSONArray param_array = new JSONArray();
                updateJSON.put(StorageConstants.SCHEMA_NAME, schema_name);
                updateJSON.put(StorageConstants.TABLE_NAME, name());
                if (business_time) {
                    if (bstart != null && bend!= null && !bstart.isNull() && !bend.isNull()) {
                        if (bstart.isCurrentTimeStamp() && bend.isCurrentTimeStamp()) {
                            updateJSON.put(StorageConstants.BUSINESS_TIME, StorageConstants.BUSINESS_BETWEEN_CURRENT);
                        } else if (bstart.isCurrentTimeStamp()) {
                            updateJSON.put(StorageConstants.BUSINESS_TIME, StorageConstants.BUSINESS_BETWEEN_START_CURRENT);
                            param_array.put(SQLDBHelper.createParams(bend, StorageConstants.TIMESTAMP));
                        } else if (bend.isCurrentTimeStamp()) {
                            updateJSON.put(StorageConstants.BUSINESS_TIME, StorageConstants.BUSINESS_BETWEEN_END_CURRENT);
                            param_array.put(SQLDBHelper.createParams(bstart, StorageConstants.TIMESTAMP));
                        } else {
                            updateJSON.put(StorageConstants.BUSINESS_TIME, StorageConstants.BUSINESS_BETWEEN);
                            param_array.put(SQLDBHelper.createParams(bstart, StorageConstants.TIMESTAMP));
                            param_array.put(SQLDBHelper.createParams(bend, StorageConstants.TIMESTAMP));
                        }
                    }
                }
                JSONArray update_columns_array = new JSONArray();
                if (upd_columns !=null) {
                    for (int u = 0; u < upd_columns.length; u++) {
                        for (int i = 0; i < columns.length; i++) {
                            if (upd_columns[u].equalsIgnoreCase(columns[i][0])) {
                                if (business_time && (upd_columns[u].equalsIgnoreCase("BSTART") || upd_columns[u].equalsIgnoreCase("BEND"))) {
                                    // BSTART & BEND COLUMNS CAN NOT be UPDATED with BUSINESS TIME CLAUSE
                                    // BUT STILL PASSED BY CALLER TO MARK CHANGES COLUMN (Note- DRAFTS Table has BSTART_NEW/BEND_NEW)
                                } else {
                                    update_columns_array.put(columns[i][0]);
                                    param_array.put(SQLDBHelper.createParams(param_values[u], columns[i][1]));
                                }
                            }
                        }
                    }
                }

                updateJSON.put(StorageConstants.COLUMNS, update_columns_array);
                JSONArray condition_columns_array = new JSONArray();
                if (condition_columns !=null) {
                    for (int c = 0; c < condition_columns.length; c++) {
                        for (int i = 0; i < columns.length; i++) {
                            if (condition_columns[c].equalsIgnoreCase(columns[i][0])) {
                                condition_columns_array.put(columns[i][0]);
                                param_array.put(SQLDBHelper.createParams(param_values[upd_columns.length+c], columns[i][1]));
                            }
                        }
                    }
                }
                updateJSON.put(StorageConstants.CONDITION_COLUMNS, condition_columns_array);
                updateJSON.put(StorageConstants.PARAM_ARRAY, param_array);
                return new JSONObject().put(StorageConstants.SQL_JSON_TYPE, StorageConstants.SQL_JSON_TYPE_UPDATE)
                    .put(StorageConstants.SQL_JSON_JSON, updateJSON);
            } catch (JSONException | IOException e) {
                throw new StorageException("Error while getUpdateJson for " + name(), e);
            }
        } else {
            throw new StorageException("Incorrect parameter lists getUpdateJson-" + upd_columns.length + "," + condition_columns.length + ","+ param_values.length);
        }
    }

    public JSONObject getUpdateJson(String schema_name, JSONObject upd_columns, JSONObject condition_columns)
        throws StorageException {
        return getUpdateJson(schema_name, false, null, null, upd_columns, condition_columns);
    }

    public JSONObject getUpdateJson(String schema_name, boolean business_time, SQLTimestampWrapper bstart, SQLTimestampWrapper bend, JSONObject upd_columns,
        JSONObject condition_columns) throws StorageException {
        JSONObject updateJSON = new JSONObject();
        try {
            JSONArray param_array = new JSONArray();
            updateJSON.put(StorageConstants.SCHEMA_NAME, schema_name);
            updateJSON.put(StorageConstants.TABLE_NAME, name());
            if (business_time) {
                if (bstart != null && bend!= null && !bstart.isNull() && !bend.isNull()) {
                    if (bstart.isCurrentTimeStamp() && bend.isCurrentTimeStamp()) {
                        updateJSON.put(StorageConstants.BUSINESS_TIME, StorageConstants.BUSINESS_BETWEEN_CURRENT);
                    } else if (bstart.isCurrentTimeStamp()) {
                        updateJSON.put(StorageConstants.BUSINESS_TIME, StorageConstants.BUSINESS_BETWEEN_START_CURRENT);
                        param_array.put(SQLDBHelper.createParams(bend, StorageConstants.TIMESTAMP));
                    } else if (bend.isCurrentTimeStamp()) {
                        updateJSON.put(StorageConstants.BUSINESS_TIME, StorageConstants.BUSINESS_BETWEEN_END_CURRENT);
                        param_array.put(SQLDBHelper.createParams(bstart, StorageConstants.TIMESTAMP));
                    } else {
                        updateJSON.put(StorageConstants.BUSINESS_TIME, StorageConstants.BUSINESS_BETWEEN);
                        param_array.put(SQLDBHelper.createParams(bstart, StorageConstants.TIMESTAMP));
                        param_array.put(SQLDBHelper.createParams(bend, StorageConstants.TIMESTAMP));
                    }
                }
            }
            JSONArray update_columns_array = new JSONArray();
            if (upd_columns !=null) {
                for (int i = 0; i < columns.length; i++) {
                    if (upd_columns.has(columns[i][0].toLowerCase())) {
                        if (business_time && (columns[i][0].equalsIgnoreCase("BSTART") || columns[i][0].equalsIgnoreCase("BEND"))) {
                            // BSTART & BEND COLUMNS CAN NOT be UPDATED with BUSINESS TIME CLAUSE
                            // BUT STILL PASSED BY CALLER TO MARK CHANGES COLUMN (Note- DRAFTS Table has BSTART_NEW/BEND_NEW)
                        } else {
                            update_columns_array.put(columns[i][0]);
                            param_array.put(SQLDBHelper.createParams(upd_columns.get(columns[i][0].toLowerCase()), columns[i][1]));
                        }
                    } else if (upd_columns.has(columns[i][0])) {
                        if (business_time && (columns[i][0].equalsIgnoreCase("BSTART") || columns[i][0].equalsIgnoreCase("BEND"))) {
                            // BSTART & BEND COLUMNS CAN NOT be UPDATED with BUSINESS TIME CLAUSE
                            // BUT STILL PASSED BY CALLER TO MARK CHANGES COLUMN (Note- DRAFTS Table has BSTART_NEW/BEND_NEW)
                        } else {
                            update_columns_array.put(columns[i][0]);
                            param_array.put(SQLDBHelper.createParams(upd_columns.get(columns[i][0]), columns[i][1]));
                        }
                    }
                }
            }

            updateJSON.put(StorageConstants.COLUMNS, update_columns_array);
            JSONArray condition_columns_array = new JSONArray();
            if (condition_columns !=null) {
                for (int i = 0; i < columns.length; i++) {
                    if (condition_columns.has(columns[i][0].toLowerCase())) {
                        condition_columns_array.put(columns[i][0]);
                        param_array.put(SQLDBHelper.createParams(condition_columns.get(columns[i][0].toLowerCase()), columns[i][1]));
                    } else if (condition_columns.has(columns[i][0]) ) {
                        condition_columns_array.put(columns[i][0]);
                        param_array.put(SQLDBHelper.createParams(condition_columns.get(columns[i][0]), columns[i][1]));
                    }
                }
            }
            updateJSON.put(StorageConstants.CONDITION_COLUMNS, condition_columns_array);
            updateJSON.put(StorageConstants.PARAM_ARRAY, param_array);
            return new JSONObject().put(StorageConstants.SQL_JSON_TYPE, StorageConstants.SQL_JSON_TYPE_UPDATE)
                .put(StorageConstants.SQL_JSON_JSON, updateJSON);
        } catch (JSONException | IOException e) {
            throw new StorageException("Error while getUpdateJson for " + name(), e);
        }
    }

    public JSONObject getDropTableJson(String schema_name) throws StorageException {
        JSONObject dropJSON = new JSONObject();
        try {
            dropJSON.put(StorageConstants.SCHEMA_NAME, schema_name);
            dropJSON.put(StorageConstants.TABLE_NAME, name());
            dropJSON.put(StorageConstants.EXPECTED_ERROR_CODE, -204);
        } catch (JSONException e) {
            throw new StorageException("Error while getDroptableJson for " + name(), e);
        }
        return dropJSON;

    }

}
