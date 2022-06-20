/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.store;

/**
 * @author Shaik.Nawaz
 *
 */
public class StorageException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param message - Custom Error Message
     */
    public StorageException(String message) {
        super(message);
    }

    /**
     * @param cause - Actual Exception/Error
     */
    public StorageException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message - Custom Error Message
     * @param cause - Actual Exception/Error
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
