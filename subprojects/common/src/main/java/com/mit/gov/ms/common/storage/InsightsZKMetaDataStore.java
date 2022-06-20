/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.storage;

import com.mit.gov.ms.store.api.DBResultSetFormatter;
import com.mit.gov.ms.store.api.DBStore;

/**
 * @author shaik.Nawaz
 *
 */
public class InsightsZKMetaDataStore extends BaseInsightsMetaDataStore {

    public InsightsZKMetaDataStore(DBStore dbStore, DBResultSetFormatter<?> resultSetFormatter) {
        super(dbStore, resultSetFormatter);
    }
}
