/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.task.api;

import com.mit.gov.ms.common.InsightsException;

public enum InsightsTaskStatus {
    NEW(1), IN_PROGRESS(2), CANCEL_REQUESTED(3), CANCELLED(4), SUCCESS(5), FAILED(6);

    private int statusCode = 0;

    public int getStatusCode() {
        return statusCode;
    }

    private InsightsTaskStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public static InsightsTaskStatus valueOf(int statusCode) throws InsightsException {
        for (InsightsTaskStatus value : InsightsTaskStatus.values()) {
            if (value.getStatusCode() == statusCode) {
                return value;
            }
        }
        throw new InsightsException("Invalued Code for " + InsightsTaskStatus.class.getSimpleName() + " - " + statusCode);

    }
}
