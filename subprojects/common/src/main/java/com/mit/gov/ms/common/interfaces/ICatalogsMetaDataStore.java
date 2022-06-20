/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.interfaces;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONObject;

import com.mit.gov.ms.common.InsightsException;
import com.mit.gov.ms.store.StorageException;

public interface ICatalogsMetaDataStore {

    JSONObject get(String dbStoreName, String dbQueryIdentifier, String[] formats, JSONArray param_array,
        String resultFormatId)
        throws InsightsException;
    
    JSONObject get(String dbStoreName, String dbQueryIdentifier, String[] formats, JSONArray param_array,
        String resultFormatId, String orderBy, int offSet, int limit)
        throws InsightsException;
    
    JSONObject getDBStatus(String dbName) throws StorageException;

}
