/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */
package com.mit.gov.ms.common;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.mit.gov.ms.common.interfaces.ICatalogsMetaDataStore;
import com.mit.gov.ms.common.storage.CatalogsSQLQueries;
import com.mit.gov.ms.common.storage.CatalogsSQLRSFormater;
import com.mit.gov.ms.common.storage.InsightsSQLDBMetaDataStore;
import com.mit.gov.ms.store.DBStoreFactory;
import com.mit.gov.ms.store.StorageException;
import com.mit.gov.ms.store.api.DBStore;
import com.mit.gov.ms.store.queries.SQLQueries;
import com.mit.gov.ms.store.schema.StorageSchema;

public class CatalogsMetaDataStore {

    private static ICatalogsMetaDataStore DEFAULT_INSTANCE = null;

    /**
     * @param insightsMetaDataStore the DEFAULT_INSTANCE to set for Tests
     */
    public static void setInsightsMetaDataStore(ICatalogsMetaDataStore insightsMetaDataStore) {
        DEFAULT_INSTANCE = insightsMetaDataStore;
    }

    private CatalogsMetaDataStore() {

    }

    public static ICatalogsMetaDataStore getInstance() throws InsightsException {
        return getInstance(null);
    }

    public static ICatalogsMetaDataStore getInstance(DataSource dataSource) throws InsightsException {
        if (DEFAULT_INSTANCE == null) {
            synchronized (CatalogsMetaDataStore.class) {
                if (DEFAULT_INSTANCE == null) {
                    try {
                        // use XMETA DS if different from Insights DS (Same for now)
                        if (dataSource == null) {
                            dataSource = (DataSource) new InitialContext().lookup(Constants.XMETA_JNDI_DS);
                        }
                        StorageSchema storageSchema = null;
                        SQLQueries catalogsSQLQueries =
                            new CatalogsSQLQueries(InsightsConfiguration.getInstance().getDbType());
                        DBStore dbStore = DBStoreFactory
                            .getDBStore(InsightsConfiguration.getInstance().getDbType(), dataSource, storageSchema,
                                catalogsSQLQueries);
                        DEFAULT_INSTANCE = new InsightsSQLDBMetaDataStore(dbStore, new CatalogsSQLRSFormater(dbStore));
                        DEFAULT_INSTANCE.getDBStatus(null);
                    } catch (StorageException | NamingException e) {
                        throw new InsightsException(e);
                    }
                }
            }
        }
        return DEFAULT_INSTANCE;
    }

}
