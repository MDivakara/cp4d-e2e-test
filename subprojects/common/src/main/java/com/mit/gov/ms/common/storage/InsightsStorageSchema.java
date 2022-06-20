/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.storage;

import java.util.ArrayList;

import com.mit.gov.ms.common.storage.versions.InsightsStorageSchemaV1;
import com.mit.gov.ms.common.storage.versions.InsightsStorageSchemaV2;
import com.mit.gov.ms.store.schema.StorageSchema;

/**
 * @author shaik.Nawaz
 *
 */
public class InsightsStorageSchema extends StorageSchema {

    public InsightsStorageSchema(String dbType, int schemaVersion) {
        super(dbType, schemaVersion);
    }

    public ArrayList<String> getInitStatementList() {
        return getSchemaVersion(this.dbType, this.getSchemaVersion()).getInitStatementList();
    }

    public ArrayList<String> getUpgradeStatementList(int currVersion, int newVersion) {
        
        ArrayList<String> upgradeStatementList = new ArrayList<String>();
        if (newVersion > currVersion) {
            for (int i = currVersion + 1; i <= newVersion; i++) {
                upgradeStatementList.addAll(getSchemaVersion(this.dbType, i).getUpgradeStatementList(i - 1, i));
            }
        }
        
        return upgradeStatementList;
    }

    private StorageSchema getSchemaVersion(String dbType, int schemaVersion) {
        switch (schemaVersion) {
        case 1:
            return new InsightsStorageSchemaV1(dbType, schemaVersion);
        case 2:
            return new InsightsStorageSchemaV2(dbType, schemaVersion);
        default:
            throw new RuntimeException("Invalid Schema Version passed");
        }
    }
    
    @Override
    public ArrayList<String> getTableList() {
        return getSchemaVersion(this.dbType, this.getSchemaVersion()).getTableList();
    }

}
