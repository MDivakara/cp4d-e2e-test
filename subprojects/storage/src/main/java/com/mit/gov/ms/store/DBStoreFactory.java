/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.store;

import javax.sql.DataSource;

import com.mit.gov.ms.store.api.DBStore;
import com.mit.gov.ms.store.db.cloudant.CloudantDBStore;
import com.mit.gov.ms.store.db.fs.FSDBStore;
import com.mit.gov.ms.store.db.sql.SQLDBStore;
import com.mit.gov.ms.store.db.zk.ZKDBStore;
import com.mit.gov.ms.store.queries.SQLQueries;
import com.mit.gov.ms.store.schema.StorageSchema;

/**
 * @author Shaik.Nawaz
 *
 */
public class DBStoreFactory {

    public static DBStore getDBStore(String cloudantURL, String cloudantUser, String cloudantPassword)
        throws StorageException {
        return new CloudantDBStore(cloudantURL, cloudantUser, cloudantPassword);
    }

    public static DBStore getDBStore(String zooKeeperHost, String rootNodeName) throws StorageException {
        return new ZKDBStore(zooKeeperHost, rootNodeName);
    }

    public static DBStore getDBStore(String rootNodeName) throws StorageException {
        return new FSDBStore(rootNodeName);
    }

    public static DBStore getDBStore(String dbType, DataSource dataSource, StorageSchema storageSchema, SQLQueries storageQueries) throws StorageException {
        return new SQLDBStore(dbType, dataSource, storageSchema, storageQueries);
    }
}
