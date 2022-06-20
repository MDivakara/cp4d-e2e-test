/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import javax.ws.rs.core.Response.Status;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IISRestClient {
    private final static Logger LOGGER = LoggerFactory.getLogger(IISRestClient.class);

    private String iisuser = null;
    private String iisuserName = null;
    private String iissecret = null;
    private String iisbaseurl = null;
    private int authStatus = Status.INTERNAL_SERVER_ERROR.getStatusCode();
    private String authStatusMessage = "Internal Server Error";
    private String JWTtoken = null;
    //private DecodedJWT decodedJWTtoken = null;

    public IISRestClient(String iisuser, String iissecret) {
        this.iisuser = iisuser;
        this.iisuserName = this.iisuser;
        this.iissecret = iissecret;
        this.iisbaseurl = InsightsConfiguration.getInstance().getIISBaseUrl();
    }

    public IISRestClient(String authString) {
        if (authString == null || !authString.toUpperCase().startsWith("BASIC ") || authString.length() < 8) {
            return;
        }
        String authDecodedString = InsightsConfiguration.getInstance().getDecoded(authString.substring(6));
        String[] authCreds = authDecodedString.split(":");
        if (authCreds.length != 2) {
            return;
        }
        this.iisuser = authCreds[0].trim();
        this.iisuserName = this.iisuser;
        this.iissecret = authCreds[1].trim();
        this.iisbaseurl = InsightsConfiguration.getInstance().getIISBaseUrl();
    }

    private void addCommonJSONHeader(HashMap<String, String> header, String method) {
        if (header == null) {
            header = new HashMap<String, String>();
        }
        header.put("accept", "application/json");
        header.put("charset", "utf-8");
        if (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT") || method.equalsIgnoreCase("PATCH")) {
            header.put("Content-Type", "application/json");
        } else if (method.equals("PATCHJSON")) {
            header.put("Content-Type", "application/json-patch+json");
        }
    }

    private String getIISBasicAuthToken(String user, String key) {
        return new String(Base64.getEncoder().encode((user + ":" + key).getBytes()), StandardCharsets.UTF_8);
    }

    private void addBasicTokenHeader(HashMap<String, String> header) {
        if (header == null) {
            header = new HashMap<String, String>();
        }
        header.put("Authorization", "Basic " + getIISBasicAuthToken(iisuser, iissecret));
    }

    private HttpResponse executeRestAPI(String url, String method, HashMap<String, String> header,
            HttpEntity bodyEntity) throws InsightsException {

        addCommonJSONHeader(header, method);
        addBasicTokenHeader(header);

        return HttpUtils.executeRestAPI(url, method, header, bodyEntity);

    }

    private void getJWTtokenFromIIS() {
        String url = this.iisbaseurl + Constants.AUTH_TOKEN_PATH;
        JSONObject payload = new JSONObject();
        try {
            payload.put("username", this.iisuser);
            payload.put("password", this.iissecret);
            HttpResponse response = executeRestAPI(url, "POST", null, HttpUtils.getHTTPEntity(payload.toString()));
            this.authStatus = response.getStatusLine().getStatusCode();
            JSONObject jsonResponse = HttpUtils.parseResponseAsJSONObject(response);
            if (this.authStatus == 200 && jsonResponse != null && jsonResponse.has("access_token")) {
                //LOGGER.info(jsonResponse.toString());
                this.JWTtoken = jsonResponse.getString("access_token");
            } else if (jsonResponse != null && jsonResponse.has("message")) {
                this.authStatusMessage = jsonResponse.getString("message");
            } else {
                LOGGER.error("Got error while executing POST " + url + ", error_response_code="
                        + response.getStatusLine().getStatusCode() + ", error_response_reason="
                        + ((jsonResponse != null) ? jsonResponse : response.getStatusLine().getReasonPhrase()));
                this.authStatusMessage = "Internal Server Error";
            }
        } catch (Exception e) {
            LOGGER.error("Got error while executing POST " + url, e);
            this.authStatusMessage = "Internal Server Error";
        }
    }

    public boolean authenticateUser() {
        if (this.iisuser == null || this.iisuser.isEmpty() || this.iissecret == null || this.iissecret.isEmpty()) {
            this.authStatus = Status.UNAUTHORIZED.getStatusCode();
            this.authStatusMessage = "You must authorize yourself to access this resource";
            this.JWTtoken = null;
            return false;
        }
        getJWTtokenFromIIS();
        if (this.authStatus == Status.OK.getStatusCode()) {
            return true;
        }
        return false;
    }


    /**
     * @return the Auth Status Code
     */
    public Status getAuthStatusCode() {
        return Status.fromStatusCode(this.authStatus);
    }

    /**
     * @return the Auth Status Message
     */
    public JSONObject getAuthStatusMessage() {
        try {
            return new JSONObject().put("message", this.authStatusMessage).put("code", getAuthStatusCode().name());
        } catch (JSONException e) {
            LOGGER.error("Got error while getStatusMessage" + this.authStatusMessage, e);
            return new JSONObject();
        }
    }
    
    /**
     * @return the iisuser
     */
    public String getIISUser() {
        return iisuser;
    }

    /**
     * @return the iisUsername
     */
    public String getIISUserName() {
        return iisuserName;
    }

    /**
     * @return the iissecret
     */
    public String getIISSecret() {
        return iissecret;
    }

    /**
     * @return the jWTtoken
     */
    public String getJWTtoken() {
        return JWTtoken;
    }

}
