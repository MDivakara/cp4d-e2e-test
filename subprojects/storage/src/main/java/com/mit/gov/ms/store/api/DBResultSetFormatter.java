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
public interface DBResultSetFormatter<T> {

    public JSONObject format(T rs, JSONObject executeJson) throws StorageException;

    public InputStream formatAsStream(T rs, JSONObject executeJson) throws StorageException;

}
