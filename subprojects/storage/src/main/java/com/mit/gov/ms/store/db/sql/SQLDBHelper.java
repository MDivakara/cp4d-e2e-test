/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.store.db.sql;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Base64;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.mit.gov.ms.store.SQLTimestampWrapper;
import com.mit.gov.ms.store.StorageConstants;
import com.mit.gov.ms.store.StorageException;
import com.mit.gov.ms.store.StorageUtils;

/**
 * @author Shaik.Nawaz
 *
 */
public class SQLDBHelper {

    public SQLDBHelper() {

    }
    
    public static JSONArray getColumnsArray(String... columns) throws JSONException {
        return new JSONArray(columns);
    }

    public static JSONArray getParamsArray(JSONObject... param_values) throws JSONException {
        return new JSONArray(param_values);
    }

    private static JSONObject getNewJsonWithNullCheck(Object param_value) throws JSONException {
        JSONObject result = new JSONObject();
        if (param_value == null) {
            result.put(StorageConstants.PARAM_IS_NULL, true);
        }
        return result;
    }

    public static JSONObject createParamString(String param_value) throws JSONException {
        return getNewJsonWithNullCheck(param_value).put(StorageConstants.PARAM_VALUE, param_value).put(StorageConstants.PARAM_TYPE,
                StorageConstants.STRING);
    }

    public static JSONObject createParamInt(Integer param_value) throws JSONException {
        return getNewJsonWithNullCheck(param_value).put(StorageConstants.PARAM_VALUE, param_value).put(StorageConstants.PARAM_TYPE,
                StorageConstants.INT);
    }

    public static JSONObject createParamLong(Long param_value) throws JSONException {
        return getNewJsonWithNullCheck(param_value).put(StorageConstants.PARAM_VALUE, param_value).put(StorageConstants.PARAM_TYPE,
                StorageConstants.LONG);
    }

    public static JSONObject createParamDouble(Double param_value) throws JSONException {
        return getNewJsonWithNullCheck(param_value).put(StorageConstants.PARAM_VALUE, param_value).put(StorageConstants.PARAM_TYPE,
                StorageConstants.DOUBLE);
    }

    public static JSONObject createParamTimestamp(Date param_value) throws JSONException {
        return getNewJsonWithNullCheck(param_value)
                .put(StorageConstants.PARAM_VALUE, param_value != null ? param_value.getTime() : param_value)
                .put(StorageConstants.PARAM_TYPE, StorageConstants.TIMESTAMP);
    }

    public static JSONObject createParamTimestamp(Timestamp param_value) throws JSONException {
        return getNewJsonWithNullCheck(param_value)
                .put(StorageConstants.PARAM_VALUE, param_value != null ? param_value.getTime() : param_value)
                .put(StorageConstants.PARAM_TS_NANO, param_value != null ? param_value.getNanos() : 0)
                .put(StorageConstants.PARAM_TYPE, StorageConstants.TIMESTAMP);
    }

    public static JSONObject createParamTimestamp(SQLTimestampWrapper param_value) throws JSONException {
        JSONObject result = getNewJsonWithNullCheck(param_value);
        if ((param_value != null) && param_value.isCurrentTimeStamp()) {
             return result.put(StorageConstants.PARAM_IS_CURRENT_TS, true)
                 .put(StorageConstants.PARAM_TYPE, StorageConstants.TIMESTAMP);
        } else if (param_value != null && !param_value.isNull()) {
            result.put(StorageConstants.PARAM_VALUE, ((param_value != null) && !param_value.isNull() && !param_value.isCurrentTimeStamp())
                ? param_value.getTimestamp().getTime() : param_value)
                .put(StorageConstants.PARAM_TS_NANO, ((param_value != null) && !param_value.isNull() && !param_value.isCurrentTimeStamp())
                ? param_value.getTimestamp().getNanos() : 0)
                .put(StorageConstants.PARAM_TYPE, StorageConstants.TIMESTAMP);
        }
        return result;
    }

    public static String encodedValueForBlob(Object obj) throws StorageException {
        try {
            if (obj instanceof InputStream) {
                return new String(Base64.getEncoder().encode(IOUtils.toByteArray((InputStream) obj)),
                    StandardCharsets.UTF_8);
            } else if (obj instanceof String) {
                return new String(Base64.getEncoder().encode(((String) obj).getBytes()), StandardCharsets.UTF_8);
            } else if (obj instanceof byte[]) {
                return new String(Base64.getEncoder().encode((byte[]) obj), StandardCharsets.UTF_8);
            } else {
                return new String(Base64.getEncoder().encode(obj.toString().getBytes()), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new StorageException("Exception while encodedValueForBlob", e);
        }
    }
    
    public static JSONObject createParamBlob(String param_value) throws JSONException {
        return getNewJsonWithNullCheck(param_value)
            .put(StorageConstants.PARAM_VALUE, param_value)
            .put(StorageConstants.PARAM_TYPE, StorageConstants.BLOB);
    }
    
    public static JSONObject createParamClob(String param_value) throws JSONException {
        return createParamString(param_value);
    }
    
    public static JSONObject createParams(Object param_value, String param_type) throws JSONException, IOException, StorageException {
        switch (param_type) {
        case StorageConstants.STRING:
            return createParamString((String) param_value);
        case StorageConstants.INT:
            return createParamInt((Integer) param_value);
        case StorageConstants.LONG:
            return createParamLong((Long) param_value);
        case StorageConstants.DOUBLE:
            return createParamDouble((Double) param_value);
        case StorageConstants.TIMESTAMP:
            if (param_value instanceof Timestamp) {
                return createParamTimestamp((Timestamp) param_value);
            } else if (param_value instanceof Date) {
                return createParamTimestamp((Date) param_value);
            } else if (param_value instanceof SQLTimestampWrapper) {
                return createParamTimestamp((SQLTimestampWrapper)param_value);
            } else {
                return createParamTimestamp(SQLTimestampWrapper.valueOf(param_value));
            }
        case StorageConstants.BLOB:
            if (param_value instanceof String) {
                return createParamBlob(((String) param_value));
            }
            throw new StorageException("Convert inputstream to encoded String using getEncodedStringForBlob method");
        case StorageConstants.CLOB:
            if (param_value instanceof String) {
                return createParamClob(((String) param_value));
            }
            throw new StorageException("Convert CLOB parameter to String ..");
        default:
            break;
        }
        return getNewJsonWithNullCheck(param_value).put(StorageConstants.PARAM_VALUE, param_value).put(StorageConstants.PARAM_TYPE,
                param_type);
    }

    public static boolean isCurrentTS(JSONObject param) {
        return (param.has(StorageConstants.PARAM_IS_CURRENT_TS)) ? param.optBoolean(StorageConstants.PARAM_IS_CURRENT_TS, false) : false;
    }

    public static boolean isNullParam(JSONObject param) {
        return (param.has(StorageConstants.PARAM_IS_NULL)) ? param.optBoolean(StorageConstants.PARAM_IS_NULL, false) : false;
    }

    public static String getParamString(JSONObject param) throws JSONException {
        return param.getString(StorageConstants.PARAM_VALUE);
    }

    public static int getParamInt(JSONObject param) throws JSONException {
        return param.getInt(StorageConstants.PARAM_VALUE);
    }

    public static long getParamLong(JSONObject param) throws JSONException {
        return param.getLong(StorageConstants.PARAM_VALUE);
    }

    public static double getParamDouble(JSONObject param) throws JSONException {
        return param.getDouble(StorageConstants.PARAM_VALUE);
    }

    public static Timestamp getParamTimestamp(JSONObject param) throws JSONException {
        if (param.has(StorageConstants.PARAM_TS_NANO)  && param.getInt(StorageConstants.PARAM_TS_NANO) > 0) {
            Timestamp ts = new Timestamp(param.getLong(StorageConstants.PARAM_VALUE));
            ts.setNanos(param.getInt(StorageConstants.PARAM_TS_NANO));
            return ts;
        }
        return new Timestamp(param.getLong(StorageConstants.PARAM_VALUE));
    }

    public static InputStream getParamBlob(JSONObject param) throws JSONException {
        return new ByteArrayInputStream(
            Base64.getDecoder().decode(param.getString(StorageConstants.PARAM_VALUE).getBytes(StandardCharsets.UTF_8)));
    }

    public static String getParamClob(JSONObject param) throws JSONException {
        return getParamString(param);
    }

    public static Object getValueFromResultSet(int type, ResultSet rs, int colIndex) throws IOException, SQLException, StorageException {
        //System.out.println(colLabel + "-" + type);
        switch (type) {
        case Types.CHAR:
        case Types.VARCHAR:
        case Types.NVARCHAR:
        case Types.LONGVARCHAR:
            return rs.getString(colIndex);
        case Types.INTEGER:
        case Types.SMALLINT:
        case Types.TINYINT:
            return rs.getInt(colIndex);
        case Types.TIMESTAMP :
        case Types.TIMESTAMP_WITH_TIMEZONE:
            return StorageUtils.getUTCStringFromTimeStamp(rs.getTimestamp(colIndex));
        case Types.DATE:
            return StorageUtils.getUTCStringFromDate(rs.getDate(colIndex));
        case Types.VARBINARY:
        case Types.LONGVARBINARY:
            return IOUtils.toString(rs.getBinaryStream(colIndex), StandardCharsets.UTF_8);
            //return IOUtils.toString(rs.getBytes(colIndex), StandardCharsets.UTF_8.name());
            //return new String(rs.getBytes(colIndex), StandardCharsets.UTF_8);
        case Types.BLOB:
            return IOUtils.toString(rs.getBlob(colIndex).getBinaryStream(), StandardCharsets.UTF_8);
        case Types.CLOB:
        case Types.NCLOB:
            return IOUtils.toString(rs.getClob(colIndex).getCharacterStream());
        case Types.BIGINT:
            return rs.getLong(colIndex);
        case Types.DECIMAL:
        case Types.DOUBLE:
        case Types.NUMERIC:
            return rs.getDouble(colIndex);
        default:
            return rs.getObject(colIndex);
        // TODO MORE DATA TYPE CHECK
        }

    }
}
