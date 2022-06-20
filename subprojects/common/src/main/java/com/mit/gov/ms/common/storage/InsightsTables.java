/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.storage;

import com.mit.gov.ms.common.Constants;
import com.mit.gov.ms.store.StorageConstants;
import com.mit.gov.ms.store.schema.StorageTables;
import com.mit.gov.ms.store.schema.StorageTablesOps;

/**
 * @author shaik.Nawaz
 *
 */
public enum InsightsTables implements StorageTables {
    GI_TASKS(new String[][] {
        { Constants.TENANT_ID, StorageConstants.STRING },
        { Constants.TASK_ID, StorageConstants.STRING },
        { Constants.REV_NO, StorageConstants.INT },
        { Constants.TASK_TYPE, StorageConstants.STRING },
        { Constants.SOURCE_ID, StorageConstants.STRING },
        { Constants.CREATED_BY, StorageConstants.STRING },
        { Constants.CREATED_AT, StorageConstants.TIMESTAMP },
        { Constants.UPDATED_AT, StorageConstants.TIMESTAMP },
        { Constants.STARTED_AT, StorageConstants.TIMESTAMP },
        { Constants.STATUS, StorageConstants.INT },
        { Constants.STATUS_MESSAGE, StorageConstants.STRING },
        { Constants.REQUEST_INFO, StorageConstants.STRING },
        { Constants.OUTPUT_INFO, StorageConstants.STRING }
        }),
    GI_SCA_RESULTS(new String[][] {
        { Constants.TENANT_ID, StorageConstants.STRING },
        { Constants.WKSP_ID, StorageConstants.STRING },
        { Constants.REV_NO, StorageConstants.INT },
        { Constants.UPDATED_AT, StorageConstants.TIMESTAMP },
        { Constants.ISBEINGANALYSED, StorageConstants.STRING },
        { Constants.NUM_GROUPS, StorageConstants.INT }
        }),
    GI_SCA_GROUPS(new String[][] {
        { Constants.TENANT_ID, StorageConstants.STRING },
        { Constants.WKSP_ID, StorageConstants.STRING },
        { Constants.GROUP_ID, StorageConstants.STRING },
        { Constants.REV_NO, StorageConstants.INT },
        { Constants.REF_COL_NAME, StorageConstants.STRING },
        { Constants.REF_COL_ID, StorageConstants.STRING },
        { Constants.REF_ASSET_ID, StorageConstants.STRING },
        { Constants.NUM_DATASETS, StorageConstants.INT },
        { Constants.NUM_COLS, StorageConstants.INT },
        { Constants.MIN_SIM_SCORE, StorageConstants.INT },
        { Constants.MAX_SIM_SCORE, StorageConstants.INT },
        { Constants.STATE, StorageConstants.STRING },
        { Constants.IS_EXPLICIT_RUN, StorageConstants.STRING },
        { Constants.LAST_RUN_AT, StorageConstants.TIMESTAMP },
        { Constants.LAST_RUN_BY, StorageConstants.STRING },
        }),
    GI_SCA_GROUP_COLUMNS(new String[][] {
        { Constants.TENANT_ID, StorageConstants.STRING },
        { Constants.WKSP_ID, StorageConstants.STRING },
        { Constants.GROUP_ID, StorageConstants.STRING },
        { Constants.COL_ID, StorageConstants.STRING },
        { Constants.REV_NO, StorageConstants.INT },
        { Constants.COL_NAME, StorageConstants.STRING },
        { Constants.SIM_SCORE, StorageConstants.INT },
        { Constants.COL_LOCATION, StorageConstants.STRING },
        { Constants.COL_INFO, StorageConstants.CLOB }
        }),
    GI_MODELS(new String[][] {
        { Constants.TENANT_ID, StorageConstants.STRING },
        { Constants.MODEL_ID, StorageConstants.STRING },
        { Constants.NAME, StorageConstants.STRING },
        { Constants.DESCRIPTION, StorageConstants.STRING },
        { Constants.MODEL_TYPE, StorageConstants.STRING },
        { Constants.MODEL_VERSION_ID, StorageConstants.STRING },
        { Constants.REV_NO, StorageConstants.INT },
        { Constants.MODEL_METADATA, StorageConstants.STRING },
        { Constants.CREATED_BY, StorageConstants.STRING },
        { Constants.CREATED_AT, StorageConstants.TIMESTAMP },
        { Constants.UPDATED_BY, StorageConstants.STRING },
        { Constants.UPDATED_AT, StorageConstants.TIMESTAMP }
        }),
    GI_MODEL_STORE(new String[][] {
        { Constants.TENANT_ID, StorageConstants.STRING },
        { Constants.MODEL_ID, StorageConstants.STRING },
        { Constants.MODEL_VERSION_ID, StorageConstants.STRING },
        { Constants.REV_NO, StorageConstants.INT },
        { Constants.MODEL, StorageConstants.BLOB }
        }),
    GI_DATA_RULE_DEFINITIONS(new String[][] {
        { Constants.TENANT_ID, StorageConstants.STRING },
        { Constants.DRD_ID, StorageConstants.STRING },
        { Constants.REV_NO, StorageConstants.INT },
        { Constants.DRD_NAME, StorageConstants.STRING },
        { Constants.ASSET_ID, StorageConstants.STRING },
        { Constants.UPDATED_AT, StorageConstants.TIMESTAMP },
        { Constants.UPDATED_BY, StorageConstants.STRING },
        { Constants.CREATED_AT, StorageConstants.TIMESTAMP },
        { Constants.CREATED_BY, StorageConstants.STRING },
        { Constants.MODEL_ID, StorageConstants.STRING },
        { Constants.USER_TUNED, StorageConstants.STRING },
        { Constants.IS_BEING_ANALYSED, StorageConstants.STRING },
        { Constants.STATE, StorageConstants.STRING },
        { Constants.DRD_DESCRIPTION, StorageConstants.STRING},
        { Constants.REQUEST_INFO, StorageConstants.STRING}
        }),
    ;

    private String[][] columns = null;

    private StorageTablesOps ops = null;

    private InsightsTables() {

    }

    private InsightsTables(String[][] columns) {
        this.columns = columns;
        this.ops = new StorageTablesOps(name().toLowerCase(), columns);
    }

    @Override
    public StorageTablesOps getOps() {
        return this.ops;
    }

    @Override
    public String toString() {
        return name();
    }

    @Override
    public String[] getColumnsArray() {
        String[] result = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            result[i] = columns[i][0];
        }
        return result;
    }
}