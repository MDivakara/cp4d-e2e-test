/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.security;

import java.math.BigInteger;
import java.util.UUID;

/** SessionManger is used to get/set the current session related to IAM token */
public class SessionManager {

    private static ThreadLocal<SessionInfo> currentSessionTL = new ThreadLocal<SessionInfo>();

    /**
     * Get the current Session Information
     * 
     * @return the current Session.
     */
    public static SessionInfo getCurrentSession() {
        SessionInfo authSession = currentSessionTL.get();
        return authSession;
    }

    /**
     * Sets current OAuth session
     * 
     * @param authSession
     */
    public static void setCurrentSession(SessionInfo authSession) {
        currentSessionTL.set(authSession);
        if (authSession != null && authSession.getTraceId() == null) {
            authSession.setTraceId(generateTraceId());
        }
    }

    /**
     * Removes the current session.
     */
    public static void clearCurrentSession() {
        currentSessionTL.remove();
    }

    /**
     * Generates a new unique trace id for logging/messaging
     *
     * @return lowercase base-36 unique id
     */
    public static String generateTraceId() {
        final BigInteger bigInt = new BigInteger(UUID.randomUUID().toString().replaceAll("-", ""), 16);
        return bigInt.toString(36);
    }
}
