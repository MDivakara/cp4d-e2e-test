
/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.task.api;

import java.util.Date;
import java.util.UUID;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.mit.gov.ms.common.Constants;
import com.mit.gov.ms.common.InsightsException;
import com.mit.gov.ms.common.InsightsMetaDataStore;
import com.mit.gov.ms.common.interfaces.IInsightsMetaDataStore;
import com.mit.gov.ms.common.storage.InsightsTables;
import com.mit.gov.ms.common.task.api.InsightsTaskCallable;
import com.mit.gov.ms.store.SQLTimestampWrapper;
import com.mit.gov.ms.store.StorageConstants;
import com.mit.gov.ms.store.StorageException;

public abstract class InsightsTaskCallableService implements InsightsTaskCallable {

    private final static Logger LOGGER = LoggerFactory.getLogger(InsightsTaskCallableService.class);

    protected IInsightsMetaDataStore insightsStore = null;
    
    protected String tenant_id = null;

    public InsightsTaskCallableService(String tenant_id) throws InsightsException {
        super();
        this.tenant_id = tenant_id;
        this.insightsStore = InsightsMetaDataStore.getInstance();
    }

    /**
     * Register a new task for task scheduler to pickup - with status as NEW
     * @param task_type
     * @param source_id
     * @param created_by
     * @param request_info
     * 
     * @return task_id for the registered task
     * @throws InsightsException
     */
    public String registerTask(InsightsTaskType task_type, String source_id, String created_by, JSONObject request_info)
        throws InsightsException {
        return registerTask(task_type, source_id, created_by, request_info, InsightsTaskStatus.NEW);
    }
    
    private String registerTask(InsightsTaskType task_type, String source_id, String created_by, JSONObject request_info,
        InsightsTaskStatus status)
        throws InsightsException {
        String task_id = UUID.randomUUID().toString();
        JSONObject task = new JSONObject();
        try {
            SQLTimestampWrapper now = new SQLTimestampWrapper(StorageConstants.CURRENT_TIMESTAMP);
            task.put(Constants.TENANT_ID, tenant_id);
            task.put(Constants.TASK_ID, task_id);
            task.put(Constants.TASK_TYPE, task_type.name());
            task.put(Constants.SOURCE_ID, source_id);
            task.put(Constants.CREATED_BY, created_by);
            task.put(Constants.CREATED_AT, now.toString());
            task.put(Constants.UPDATED_AT, StorageConstants.CURRENT_TIMESTAMP);
            task.put(Constants.STATUS, status.getStatusCode());
            task.put(Constants.STATUS_MESSAGE, status.name());
            task.put(Constants.REQUEST_INFO, request_info.toString());
            LOGGER.info("Registering task - " + task.toString());            
            insightsStore.save(InsightsTables.GI_TASKS.name(), task_id, task);
            LOGGER.info("Done - Registering task - " + task.toString());
        } catch (JSONException | StorageException e) {
            LOGGER.error("Exception while create task - task_id=" + task_id, e);
            throw new InsightsException("Exception while create task - task_id=" + task_id, e);
        }
        return task_id;
    }
    
    /**
     * Register a new task <b>NOT</b> for task scheduler to pickup - with status as IN_PROGRESS
     * Caller of this task should initiate the execution as well, By calling InsightsTaskExecutor.execute
     * @param task_type
     * @param source_id
     * @param created_by
     * @param request_info
     * @param maskRequestColumns
     * 
     * @return task_id for the registered task
     * @throws InsightsException
     */
    public String registerTaskAndExecute(InsightsTaskThreadHandler executor, InsightsTaskType task_type, String source_id, String created_by,
        JSONObject request_info, String... maskRequestColumns)
        throws InsightsException {

        JSONObject masked_request_info = new JSONObject(request_info);
        if (maskRequestColumns != null && maskRequestColumns.length > 0) {
            for (int i = 0; i < maskRequestColumns.length; i++) {
                masked_request_info.remove(maskRequestColumns[i]);
            }
        }
        String task_id = registerTask(task_type, source_id, created_by, masked_request_info, InsightsTaskStatus.IN_PROGRESS);
        executor.startTaskThread(tenant_id, task_type, task_id, source_id, request_info);
        
        return task_id;
    }
    
}
