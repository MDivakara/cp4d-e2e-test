/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.rest.filters;

import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;

import com.mit.gov.ms.common.HttpUtils;
import com.mit.gov.ms.common.InsightsConfiguration;
import com.mit.gov.ms.common.InsightsException;
import com.mit.gov.ms.common.security.SessionInfo;
import com.mit.gov.ms.common.security.SessionManager;

public class InsightsWKCAuthFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(InsightsWKCAuthFilter.class);

    private static final String AZN_HEADER_PATTERN = "(?i)^%s\\s+([\\w\"!#\\$%%&'\\(\\)\\*\\+\\-\\./:<=>\\?@\\[\\]\\^`\\{\\|\\}~\\\\,;]+)$";

    private static Pattern _bearerHeaderPattern = Pattern.compile(String.format(AZN_HEADER_PATTERN, "bearer"));
    // private static Pattern _basicHeaderPattern =
    // Pattern.compile(String.format(AZN_HEADER_PATTERN, "basic"));

    public static final String X_FORWARDED_FOR = "X-Forwarded-For";

    private static final String HEADER_AUTHORIZATION = "Authorization";

    private static final String CP4D_JWK_PUBLIC_KEYS_END_POINT = "/auth/jwtpublic";

    @Context
    private HttpServletRequest servletRequest;

    /**
     * Constructor
     */
    public InsightsWKCAuthFilter(HttpServletRequest request) {
        servletRequest = request;
    }

    public InsightsWKCAuthFilter() {

    }

   
    private SessionInfo getSessionInfo(ContainerRequestContext requestContext, SessionInfo authSession) {

        if (authSession == null) {
            LOGGER.info("Get basic session info.");
            authSession = getBasicSessionInfo(requestContext);
        }
        SessionInfo.toHttpSession(servletRequest, authSession);

        return authSession;
    }

    private SessionInfo getBasicSessionInfo(ContainerRequestContext requestContext) {
        SessionInfo authSession = new SessionInfo();

        final String params = servletRequest.getQueryString();
        String requestUrl = servletRequest.getRequestURL().toString()
                + (params == null || params.isEmpty() ? "" : "?" + params);
        authSession.setRequestedUrl(requestUrl);

        Map<String, Serializable> headerParams = new HashMap<String, Serializable>();
        Enumeration<String> headers = servletRequest.getHeaderNames();
        while (headers.hasMoreElements()) {
            String headerKey = headers.nextElement();
            String headerValue = servletRequest.getHeader(headerKey);
            headerParams.put(headerKey, headerValue);
        }
        authSession.setHeaderParams(headerParams);
        authSession.setClientIp(getClientIpAddress(servletRequest));
        if (servletRequest.getSession() != null) {
            authSession.setHttpSessionId(servletRequest.getSession().getId());
        }
        SessionInfo.toHttpSession(servletRequest, authSession);

        return authSession;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String clientIpAddress = null;
        String ips = request.getHeader(X_FORWARDED_FOR);
        // X-Forwarded-For: client, proxy1, proxy2 etc
        // where the value is a comma+space separated list of IP addresses
        // (https://en.wikipedia.org/wiki/X-Forwarded-For)
        if (ips != null && ips.length() > 0) {
            StringTokenizer st = new StringTokenizer(ips, ",");
            clientIpAddress = st.nextToken();
        }

        if (clientIpAddress == null || clientIpAddress.trim().length() == 0) {
            clientIpAddress = request.getRemoteAddr();
        }
        return clientIpAddress;
    }

    protected String getJwkPublicKeys() throws InsightsException {

        LOGGER.info("Retrieving JWK public keys");

        try {
            Map<String, String> str = new HashMap<>();
            HttpResponse response = HttpUtils.executeRestAPI(getJwkPublicKeysUrl(), "GET", str, null);

            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (status >= 200 && status < 300) {
                if (entity != null) {
                    LOGGER.info("Successfully retrieved the JWK public keys.");
                    return EntityUtils.toString(entity);
                } else {
                    throw new InsightsException("unable retrieve JWK public keys");
                }
            } else {
                throw new InsightsException("unable retrieve JWK public keys");
            }
        } catch (IOException e) {
            throw new InsightsException("unable retrieve JWK public keys", e);
        }
    }

    protected String getJwkPublicKeysUrl() throws InsightsException {

        // For the builds in PR, ICP4D in icp1dev environment is used.
        String baseUrl = InsightsConfiguration.getInstance().getInsightsZenUrl();

        if (baseUrl != null) {
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
        }
        return baseUrl + CP4D_JWK_PUBLIC_KEYS_END_POINT;
    }

}
