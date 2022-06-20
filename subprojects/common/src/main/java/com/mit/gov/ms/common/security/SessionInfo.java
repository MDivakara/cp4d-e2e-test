/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.security;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * SessionInfo holds the IAM/JWT token user session details like the JWT attributes,
 * filter Init parameters, request header parameters the requested URL and the
 * access token.
 * 
 */
public class SessionInfo implements Serializable {

    private final static Logger LOGGER = LoggerFactory.getLogger(SessionInfo.class);
    /** The serial version UID */
    private static final long serialVersionUID = 159322859196078425L;
    /** The JWT token sub type claim */
    private static final String SUB_TYPE_CLAIM = "sub_type";
    /** The sub type claim value denoting a service */
    private static final String SERVICE_SUB_TYPE = "ServiceId";
    /** Session attribute Key */
    public static final String SESSION_ATT_KEY = "WDP_PROXY_AUTH_SESSION";
    
    private static final String DEFAULT_TENANT_ID = "9999";

    private String userId;
    private String userName;
    private String accessToken;
    private String subType;
    private String requestedUrl;
    private String traceId;
    private String httpSessionId;
    private String bssAccountId;
    private String clientIp;
    private String tenantId = DEFAULT_TENANT_ID;

    private Map<String, String> initParams;
    private Map<String, Serializable> headerParams;
    private Map<String, Serializable> sessionProps = new HashMap<String, Serializable>();

    /**
     * The default constructor
     */
    public SessionInfo() {
    }

    /**
     * Returns the access token
     * 
     * @return accessToken
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Sets the access token
     * 
     * @param accessToken
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    
    public String getTenantId() {
        return tenantId;
    }

    
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * Sets the userId/iamId
     *
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Returns the IBM ID/USER ID. Typically this is the 'ibm_id' field from the bearer
     * token
     *
     * @return userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the userName
     * 
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Returns the user name
     * 
     * @return userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the bssAccountId
     *
     * @param bssAccountId
     */
    public void setBSSAccountId(String bssAccountId) {
        this.bssAccountId = bssAccountId;
    }

    /**
     * Returns the bss account id. Typically this is the 'account.bss' field
     * from the bearer token
     *
     * @return bssAccountId
     */
    public String getBSSAccountId() {
        return bssAccountId;
    }

    /**
     * Sets the subType
     *
     * @param subType
     */
    public void setSubType(String subType) {
        this.subType = subType;
    }

    /**
     * Returns the subType
     *
     * @return subType
     */
    public String getSubType() {
        return subType;
    }

    /**
     * Sets the trace id
     *
     * @param traceId
     */
    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    /**
     * Returns the trace id
     *
     * @return trace id
     */
    public String getTraceId() {
        return traceId;
    }

    /**
     * Gets the requested sessionId
     *
     * @return requested URL
     */
    public String getHttpSessionId() {
        return httpSessionId;
    }

    /**
     * Sets the sessionId
     *
     * @param sessionId
     */
    public void setHttpSessionId(String sessionId) {
        this.httpSessionId = sessionId;
    }

    /**
     * Gets the requested IIS URL
     * 
     * @return requested URL
     */
    public String getRequestedUrl() {
        return requestedUrl;
    }

    /**
     * Sets the requested URL
     * 
     * @param requestedUrl
     */
    public void setRequestedUrl(String requestedUrl) {
        this.requestedUrl = requestedUrl;
    }

    
    /**
     * Gets the getFilterInitParams.
     * 
     * @return profile
     */
    public Map<String, String> getFilterInitParams() {
        return initParams;
    }

    /**
     * Set the FilterInitParams.
     * 
     * @param profile
     */
    public void setFilterInitParams(Map<String, String> initParms) {
        this.initParams = initParms;
    }

    /**
     * Gets the request header parameters.
     * 
     * @return headerParams
     */
    public Map<String, Serializable> getHeaderParams() {
        return headerParams;
    }

    /**
     * Sets the Request Header parameters
     * 
     * @param headerParams
     */
    public void setHeaderParams(Map<String, Serializable> headerParams) {
        this.headerParams = headerParams;
    }

    /**
     * Gets the properties.
     * 
     * @param key
     * The property key
     * 
     * @return Object
     */
    public Object getProperty(String key) {
        Object value = sessionProps.get(key);
        return value;
    }

    /**
     * Set the property.
     * 
     * @param key
     * The property key
     * @param value
     * The property Value
     * 
     */
    public void setProperty(String key, Serializable value) {
        sessionProps.put(key, value);
    }

    /**
     * Returns true if the current user is a service.
     *
     * @return true if the JWT token (getJWTAttributes) is not null and is a
     * service bearer token (a token generated using a service API key).
     */
    public boolean isService() {
        boolean isService = false;
        if (getSubType() != null && SERVICE_SUB_TYPE.equalsIgnoreCase(getSubType())) {
            isService = true;
        }
        return isService;
    }

    /**
     * Returns the Client IP
     *
     * @return clientIp
     */
    public String getClientIp() {
        return clientIp;
    }

    /**
     * Sets the Client IP
     *
     * @param clientIp
     */
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    /**
     * Gets the AuthSession stored from the HTTPServlet Request
     * 
     * @param request
     * The Servlet Request
     * @return user SessionInfo if found, otherwise returns a null.
     */
    public static SessionInfo fromHttpSession(HttpServletRequest request) {
        SessionInfo session = (SessionInfo) request.getSession(true).getAttribute(SESSION_ATT_KEY);
        return session;
    }

    /**
     * Stores the specified user AuthSession into the http session.
     * 
     * @param request
     * The Servlet Request
     * @param authSession
     * The user auth session
     */
    public static void toHttpSession(HttpServletRequest request, SessionInfo authSession) {
        request.getSession(true).setAttribute(SESSION_ATT_KEY, authSession);
    }
}
