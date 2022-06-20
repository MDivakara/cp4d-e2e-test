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
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.mit.gov.ms.store.StorageConstants;
import com.mit.gov.ms.store.StorageException;
import com.mit.gov.ms.store.api.DBResultSetFormatter;
import com.mit.gov.ms.store.api.DBStore;

/**
 * @author Shaik.Nawaz
 *
 */
public class InsightsJSONRSFormater implements DBResultSetFormatter<JSONObject> {

    @SuppressWarnings("unused")
    private final static Logger LOGGER = LoggerFactory.getLogger(InsightsJSONRSFormater.class);

    @SuppressWarnings("unused")
    private DBStore dbStore = null;

    public static final String FORMAT_DEFAULT = "format_default";

    public InsightsJSONRSFormater(DBStore dbStore) {
        this.dbStore = dbStore;
    }

    public JSONObject format(JSONObject rs, JSONObject executeJson) throws StorageException {
        JSONObject result = new JSONObject();
        try {
            switch (executeJson.getString(StorageConstants.FORMAT_RESULTS)) {
                case FORMAT_DEFAULT:
                    result = rs;
                    break;
            }
        } catch (Exception e) {
            try {
                result.put(StorageConstants.ERROR, e.getMessage());
                result.put(StorageConstants.ERROR_STACK, Arrays.toString(e.getStackTrace()));
            } catch (JSONException e1) {
                throw new StorageException("Error while executeQuery", e1);
            }
        }
        return result;
    }

    public InputStream formatAsStream(JSONObject rs, JSONObject executeJson) throws StorageException {
        InputStream result = null;
        if (executeJson.has(StorageConstants.INNER_QUERY)) {
        }
        return result;
    }
}
