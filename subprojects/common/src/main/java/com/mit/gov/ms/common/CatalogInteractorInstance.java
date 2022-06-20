/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common;

import com.mit.gov.ms.common.impl.IGCInteractor;
import com.mit.gov.ms.common.impl.WKCInteractor;
import com.mit.gov.ms.common.interfaces.CatalogInteractor;

/**
 * This class will give a Singleton Catalog Interactor Instance.
 *
 */
public class CatalogInteractorInstance {
    private static CatalogInteractor defaultCatalogInteractorInstance = null;

    /**
     * This method gives a singleton instance of CatalogInteractor depending on
     * environment.
     * 
     * @return WKCInteractor instance when in WKC environment, IGCInteractor
     *         instance when in IGC environment or as a default
     */
    public static CatalogInteractor getCatalogInteractorInstance() {

        if (defaultCatalogInteractorInstance == null) {
            switch (InsightsConfiguration.getInstance().getCatalogInteractorType()) {
            case WKC:
                defaultCatalogInteractorInstance = new WKCInteractor();
                break;
            default:
                defaultCatalogInteractorInstance = new IGCInteractor();
            }
        }

        return defaultCatalogInteractorInstance;
    }
}
