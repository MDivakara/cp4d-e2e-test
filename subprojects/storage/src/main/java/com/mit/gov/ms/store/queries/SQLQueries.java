/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.store.queries;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.mit.gov.ms.store.StorageException;

/**
 * @author Shaik.Nawaz
 *
 */
public abstract class SQLQueries {

	private Map<String, String> Queries;
    	
    public String getQuery(String queryName) throws StorageException {
        if (Queries.containsKey(queryName)) {
            return Queries.get(queryName);
        }
        throw new StorageException("Query is not found with name=" + queryName);
    }

    public SQLQueries(String dbType) {
        Queries = Collections.unmodifiableMap(getQueries(dbType));
    }
    
    protected abstract HashMap<String, String> getQueries(String dbType);
}
