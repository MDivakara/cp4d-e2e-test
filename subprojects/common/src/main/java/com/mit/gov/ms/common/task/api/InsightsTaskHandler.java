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

public interface InsightsTaskHandler {

    /**
     * Ping task scheduler to update that task is still in progress and not stale
     * @param tenant_id TODO
     * @param task_id
     * @param status_message - custom status message
     * 
     * @throws InsightsException
     */
    void pingTask(String tenant_id, String task_id, String status_message) throws InsightsException;

    /**
     * Check if a cancel request is initiated for the task specified by task_id
     * @param tenant_id TODO
     * @param task_id
     * 
     * @return
     * @throws InsightsException
     */
    boolean isCancelRequested(String tenant_id, String task_id) throws InsightsException;

    /**
     * Cancel a registered task if user has already requested to cancel
     * @param tenant_id TODO
     * @param task_id
     * @param output_info
     * 
     * @throws InsightsException
     */
    void cancelTask(String tenant_id, String task_id, JSONObject output_info) throws InsightsException;

}
