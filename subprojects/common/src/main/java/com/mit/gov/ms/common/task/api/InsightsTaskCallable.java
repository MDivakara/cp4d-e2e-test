
/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.task.api;

import org.apache.wink.json4j.JSONObject;

import com.mit.gov.ms.common.InsightsException;

public interface InsightsTaskCallable {

    /**
     * Execute requested task.
     * 
     * <pre>
     * use taskHandler.pingTask to ping the taskExecutor while analysis is still in progress to mark the task as not stale
     * use taskHandler.isCancelRequested to check if a cancel request is being raised for the task while analysis is still in progress.
     * use taskHandler.cancelTask to cancel a task if cancel is requested.
     * </pre>
     * 
     * @param taskHandler - To communicate with taskExecutor
     * @param task_id task_id of the task
     * @param source_id source_id for the service
     * @param request_info - JSON with information about the request to process.
     * @return A JSONObject with response.
     * @throws InsightsException
     */
    JSONObject execute(InsightsTaskHandler taskHandler, String task_id, String source_id, JSONObject request_info) throws InsightsException;

    /**
     * Cancel a new task before it has even started.
     * 
     * @param source_id
     * @param request_info
     */
    void cancelNewTask(String source_id, JSONObject request_info);
    
}