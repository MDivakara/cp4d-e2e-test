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

import com.mit.gov.ms.common.interfaces.IInsightsMetaDataStore;
import com.mit.gov.ms.common.storage.InsightsCloudantMetaDataStore;
import com.mit.gov.ms.common.storage.InsightsFSMetaDataStore;
import com.mit.gov.ms.common.storage.InsightsJSONRSFormater;
import com.mit.gov.ms.common.storage.InsightsSQLDBMetaDataStore;
import com.mit.gov.ms.common.storage.InsightsSQLQueries;
import com.mit.gov.ms.common.storage.InsightsSQLRSFormater;
import com.mit.gov.ms.common.storage.InsightsStorageSchema;
import com.mit.gov.ms.common.storage.InsightsZKMetaDataStore;
import com.mit.gov.ms.store.DBStoreFactory;
import com.mit.gov.ms.store.StorageException;
import com.mit.gov.ms.store.api.DBStore;
import com.mit.gov.ms.store.queries.SQLQueries;
import com.mit.gov.ms.store.schema.StorageSchema;

public class InsightsMetaDataStore {

    private static IInsightsMetaDataStore DEFAULT_INSTANCE = null;

    /**
     * @param insightsMetaDataStore the DEFAULT_INSTANCE to set for Tests
     */
    public static void setInsightsMetaDataStore(IInsightsMetaDataStore insightsMetaDataStore) {
        DEFAULT_INSTANCE = insightsMetaDataStore;
    }

    private InsightsMetaDataStore() {

    }

    public static IInsightsMetaDataStore getInstance() throws InsightsException {
        return getInstance(null);
    }

    public static IInsightsMetaDataStore getInstance(DataSource dataSource) throws InsightsException {
        if (DEFAULT_INSTANCE == null) {
            synchronized (InsightsMetaDataStore.class) {
                if (DEFAULT_INSTANCE == null) {
                    try {
                        DBStore dbStore = null;
                        switch (InsightsConfiguration.getInstance().getInsightsMetaDataStoreType()) {
                        case ZOOKEEPER:
                            dbStore = DBStoreFactory
                                .getDBStore(InsightsConfiguration.getInstance().getZKUrl(),
                                    InsightsConfiguration.getInstance().getZKRootNodeName());
                            DEFAULT_INSTANCE =
                                new InsightsZKMetaDataStore(dbStore, new InsightsJSONRSFormater(dbStore));
                            break;
                        case FILE:
                            dbStore =
                                DBStoreFactory.getDBStore(InsightsConfiguration.getInstance().getZKRootNodeName());
                            DEFAULT_INSTANCE =
                                new InsightsFSMetaDataStore(dbStore, new InsightsJSONRSFormater(dbStore));
                            break;
                        case CLOUDANT:
                            dbStore = DBStoreFactory
                                .getDBStore(InsightsConfiguration.getInstance().getCloudantAccount(),
                                    InsightsConfiguration.getInstance().getCloudantUser(),
                                    InsightsConfiguration.getInstance().getCloudantPassword());
                            DEFAULT_INSTANCE =
                                new InsightsCloudantMetaDataStore(dbStore, new InsightsJSONRSFormater(dbStore));
                            break;
                        case SQLDB:
                            // use Insights DS, if different from XMETA DS (Same for now)
                            if (dataSource == null) {
                                dataSource = (DataSource) new InitialContext().lookup(Constants.INSIGHTS_JNDI_DS);
                            }
                            StorageSchema insightsStorageSchema =
                                new InsightsStorageSchema(InsightsConfiguration.getInstance().getDbType(),
                                    InsightsConfiguration.getInstance().getInsightsSchemaVersion());
                            SQLQueries insightsSQLQueries =
                                new InsightsSQLQueries(InsightsConfiguration.getInstance().getDbType());
                            dbStore = DBStoreFactory
                                .getDBStore(InsightsConfiguration.getInstance().getDbType(), dataSource,
                                    insightsStorageSchema, insightsSQLQueries);
                            DEFAULT_INSTANCE =
                                new InsightsSQLDBMetaDataStore(dbStore, new InsightsSQLRSFormater(dbStore));
                            try {
                                DEFAULT_INSTANCE
                                .initSchema(InsightsConfiguration.getInstance().getInsightsSchemaName(),
                                    InsightsConfiguration.getInstance().getInsightsSchemaVersion());
                            } catch (InsightsException e) {
                                DEFAULT_INSTANCE = null;
                                throw e;
                            }
                            break;
                        }
                    } catch (StorageException | NamingException | RuntimeException e) {
                        throw new InsightsException(e);
                    }
                }
            }
        }
        return DEFAULT_INSTANCE;
    }

}
