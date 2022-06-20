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

public interface InsightsTaskThreadHandler {

    /**
     * Start task thread to execute specified task.
     * @param tenant_id TODO
     * @param task_type
     * @param task_id
     * @param source_id
     * @param request_info
     * @throws InsightsException
     */
    void startTaskThread(String tenant_id, InsightsTaskType task_type, String task_id, String source_id, JSONObject request_info)
        throws InsightsException;

}
