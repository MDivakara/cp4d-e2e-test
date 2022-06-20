/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.task.api;

public enum InsightsTaskType {
    SCA("Similar Column Analusis"), ANA("ML DRD"), DMY("Dummy");

    private String description = name();

    InsightsTaskType(String desc) {
        this.description = desc;
    }

    public String getFulName() {
        return this.description;
    }
}
