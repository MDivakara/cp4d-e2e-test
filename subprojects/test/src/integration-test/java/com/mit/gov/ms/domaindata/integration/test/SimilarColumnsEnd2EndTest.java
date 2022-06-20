/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.domaindata.integration.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.gov.ms.common.Constants;
import com.mit.gov.ms.common.HttpUtils;
import com.mit.gov.ms.common.InsightsConfiguration;
import com.mit.gov.ms.common.InsightsException;
import com.mit.gov.ms.common.task.api.InsightsTaskStatus;

public class SimilarColumnsEnd2EndTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(SimilarColumnsEnd2EndTest.class);

    // Actual UG url from IIS Setup
    private static String getUGURL = InsightsConfiguration.getInstance().getUGBaseUrl();
    private static String getUGURLV3 = InsightsConfiguration.getInstance().getUGBaseUrlV3();
    // Insights base URL V3 for testing - local / same as UG url on IIS
    private static String getInsightsURLV3 = InsightsConfiguration.getInstance().getTestProperty("insights.base.url.v3");
    private static String getCustomClassURL = getInsightsURLV3 + "/insights/similar_columns/";
    //private static String v3_insights_sca_path = "/v3/insights/similar_columns/";
    private static String run_only_on_non_classifeid = "?run_on_non_classified_only=true";
    private static String workspace_overview = "/dq/da/rest/v1/workspaces";
    
    private static String getRidOfAssetURL = InsightsConfiguration.getInstance().getIISBaseUrl() + "/igc-rest/v1";

    private static String basic_auth = InsightsConfiguration.getInstance().getIISBasicAuthToken();
    private static String isadmin_user = InsightsConfiguration.getInstance().getIISUSer();

    private static String TEST_SUFFIX = InsightsConfiguration.getInstance()
            .getTestProperty("insights.db2oncloud.test.suffix");
    private static String workspace_name = "SSC_WS_" + TEST_SUFFIX;
    private static String workspace_id = null;
    
    private static String data_class_rid = null;
    
    private static String getDataClassRidURL = getRidOfAssetURL + "/search?types=data_class&pageSize=200&begin=0";
    private static String dataClassByRidURL = getRidOfAssetURL + "/assets";
    
    private static Map<String, String> headerBasicAuth = new HashMap<String, String>();
    private static Map<String, String> headerBearerAuth = new HashMap<String, String>();
    private static String sca_wksp_url = null;
    
    //wkc token
    private static String zen_url = InsightsConfiguration.getInstance().getInsightsZenUrl();
    private static String artifact_id = null;
    private static String version_id = null;
    private static String access_token = null;
    

    @BeforeClass
    public static void test_setup() throws NullPointerException, FileNotFoundException {
        try {
            
            LOGGER.info("UGURL=" + getUGURL);
            LOGGER.info("UGURLV3=" + getUGURLV3);
            LOGGER.info("getInsightsURLV3=" + getInsightsURLV3);
            LOGGER.info("CustomClassURL=" + getCustomClassURL);
            
            // fetch CustomClass Workspace_id
            // creating basic auth header
            headerBasicAuth.put("Content-Type", "application/json");
            headerBasicAuth.put("Authorization", "Basic " + basic_auth);
            
            HttpResponse HTTPResp;
            String fetch_workspace_url = getUGURL + workspace_overview + "/overview";
            LOGGER.info("GET " + fetch_workspace_url);
            HTTPResp = HttpUtils.executeRestAPI(fetch_workspace_url, "GET", headerBasicAuth, null);
            int status2 = HTTPResp.getStatusLine().getStatusCode();
            assertTrue("failed to get workspace_overview, status=" + status2, status2 == 200 );
            JSONObject fetch_workspace_response = HttpUtils.parseResponseAsJSONObject(HTTPResp);
            JSONArray rows = fetch_workspace_response.getJSONArray("rows");
            
            for(int i = 0 ; i < rows.size() ; i++) {
                if(rows.getJSONObject(i).getString("WORKSPACE").trim().equalsIgnoreCase(workspace_name)) {
                    workspace_id = rows.getJSONObject(i).getString("WORKSPACERID");
                }
            }
            
            if(workspace_id == null) {
                LOGGER.error("Data setup is incomplete : unable to fetch CustomClass workspace");
                assertTrue("Data setup is incomplete : unable to fetch CustomClass workspace", false);
            }
            
            // add isadmin user, if user is different
            if (isadmin_user != null && !isadmin_user.isEmpty() && !isadmin_user.equals("isadmin")) {
                String update_workspace_user_url = getUGURL + workspace_overview + "/" + workspace_id + "/user/update";
                LOGGER.info("PUT " + fetch_workspace_url);

                String update_user_body = "{\"users\":[{\"id\":\"admin\",\"role\":\"SorcererBusinessAnalyst,SorcererOperator\"},{\"id\":\"isadmin\",\"role\":\"SorcererBusinessAnalyst\"}]}";
                StringEntity update_user_entity = new StringEntity(update_user_body);
                HTTPResp = HttpUtils.executeRestAPI(update_workspace_user_url, "PUT", headerBasicAuth,
                        update_user_entity);

                int status3 = HTTPResp.getStatusLine().getStatusCode();
                assertTrue("failed to get workspace_overview, status=" + status3, status3 == 200);
                JSONObject update_workspace_user_response = HttpUtils.parseResponseAsJSONObject(HTTPResp);

                assertTrue("failed to update workspace users, status=" + update_workspace_user_response,
                        update_workspace_user_response.getString("result").isEmpty());
                LOGGER.info("Update workspace user response " + update_workspace_user_response.toString(4));
            }
            
            //running Similar Column Analysis
            sca_wksp_url = getCustomClassURL + workspace_id;
            LOGGER.info("POST " + sca_wksp_url);
            HTTPResp = HttpUtils.executeRestAPI(sca_wksp_url + run_only_on_non_classifeid, "POST", headerBasicAuth,
                    null);
            int status_resp = HTTPResp.getStatusLine().getStatusCode();
            if (status_resp != 200 && status_resp != 201) {
                LOGGER.info("Status Code for " + sca_wksp_url + " = " + status_resp);
                LOGGER.info("Response -" + HttpUtils.parseResponseAsString(HTTPResp));
                assertTrue("Unexpected Response Code " + status_resp, false);
            }

            JSONObject sca_post_response = HttpUtils.parseResponseAsJSONObject(HTTPResp);
            assertNotNull("POST response is NULL " , sca_post_response);
            LOGGER.info(sca_post_response.toString());
            assertTrue("POST response does not contain task_id", sca_post_response.containsKey(Constants.TASK_ID));

            JSONObject responseSCAStatus = new JSONObject();
            // loop for 10 mins to check similar column analysis has finished or not
            long counter = 0;
            while (counter < 600000) {

                // create status url
                String status_url = getCustomClassURL + "status/" + workspace_id;
                LOGGER.info("GET " + status_url);
                HTTPResp = HttpUtils.executeRestAPI(status_url, "GET", headerBasicAuth, null);

                int status1 = HTTPResp.getStatusLine().getStatusCode();
                assertTrue("GET response is " + status1, status1 == 200 || status1 == 201);

                responseSCAStatus = HttpUtils.parseResponseAsJSONObject(HTTPResp);

                assertTrue("GET response does not contain " + Constants.METADATA, responseSCAStatus.containsKey(Constants.METADATA));
                assertTrue("GET response does not contain " + Constants.ENTITY, responseSCAStatus.containsKey(Constants.ENTITY));

                JSONObject entity_resp = responseSCAStatus.getJSONObject(Constants.ENTITY);

                if (entity_resp.getString(Constants.STATUS).equalsIgnoreCase(InsightsTaskStatus.SUCCESS.name())) {
                    LOGGER.info("Successfully fetch all Custom_Class Groups");
                    LOGGER.info(entity_resp.toString());
                    break;
                }
                if (entity_resp.getString(Constants.STATUS).equalsIgnoreCase(InsightsTaskStatus.FAILED.name())) {
                    LOGGER.info("Failed while fetching all Custom_Class Groups");
                    LOGGER.info(entity_resp.toString());
                    break;
                }
                LOGGER.info("No Custom_Class Groups fetched, retry after 20 sec");
                Thread.sleep(20 * 1000);
                counter += 20 * 1000;
                LOGGER.info("No Custom_Class Groups fetched, retrying");
            }
            
            //generate wkc token
            String wkc_token_url = zen_url + "/v1/preauth/validateAuth";
            LOGGER.info("GET " + wkc_token_url);
            HTTPResp = HttpUtils.executeRestAPI(wkc_token_url, "GET", headerBasicAuth, null);
            int status = HTTPResp.getStatusLine().getStatusCode();
            
            if (status != 200 && status != 201) {
                LOGGER.info("Status Code for " + wkc_token_url + " = " + status);
                LOGGER.info("Response -" + HttpUtils.parseResponseAsString(HTTPResp));
                assertTrue("Unexpected Response Code " + status, false);
            }

            JSONObject token_resp = HttpUtils.parseResponseAsJSONObject(HTTPResp);
            assertNotNull("POST response is NULL " , token_resp);
            
            access_token = token_resp.getString("accessToken");
            headerBearerAuth.put("Content-Type", "application/json");
            headerBearerAuth.put("Authorization", "Bearer " + access_token);
            
            
            LOGGER.info("Data setup is complete : CustomClass workspace_id=" + workspace_id);
        } catch (InsightsException | JSONException | UnsupportedEncodingException | InterruptedException e) {
            LOGGER.error("CustomClass workspace is not present" + workspace_name);
            assertTrue("Data setup is incomplete : unable to fetch CustomClass workspace-" + e.getMessage(), false);
        }
    }
    
    @AfterClass
    public static void test_cleanup() {
        HttpResponse HTTPResp = null;

        LOGGER.info("Clean up is in Progress..");
        try {
            // delete igc side data class
            if (data_class_rid != null && !data_class_rid.isEmpty()) {
                String url = dataClassByRidURL + "/" + data_class_rid;

                // delete custom_class by rid
                LOGGER.info("GET " + url);
                HTTPResp = HttpUtils.executeRestAPI(url, "DELETE", headerBasicAuth, null);
                if (HTTPResp.getStatusLine().getStatusCode() == 200) {
                    LOGGER.info("Succesfully deleted the custom data class");
                }
            }

            // delete column similarity type data class from WKC
            if (artifact_id != null && !artifact_id.isEmpty() && version_id != null && !version_id.isEmpty()) {
                String url = zen_url + "/v3/data_classes/" + artifact_id + "/versions/" + version_id;

                LOGGER.info("GET " + url);
                HTTPResp = HttpUtils.executeRestAPI(url, "DELETE", headerBearerAuth, null);
                if (HTTPResp.getStatusLine().getStatusCode() == 200) {
                    LOGGER.info("Succesfully deleted the custom data class");
                }
            }
            LOGGER.info("Clean up activity finished..");
        } catch (InsightsException e) {
            LOGGER.error("Erorr while cleaning SimilarColumnsEnd2EndTest:test_cleanup", e);
            assertTrue("Erorr while cleaning SimilarColumnsEnd2EndTest:test_cleanup" + e.getMessage(), false);
        }
    }

    /**
     * end to end test for similar column using old IGC endpoint to create data class
     *  - run similar columns analysis
     *  - verified all groups
     *  - create a data class from one group
     *  - verified the column similarity type data class in IGC
     */
    @Test
    public void test_similarColumnsE2E() {

        HttpResponse HTTPResp;

        try {
            
            // fetch SCA groups
            JSONObject sca_group_resp = new JSONObject();
            LOGGER.info("GET "  +sca_wksp_url + "?limit=20&filter[state]=no_action&sort[num_cols]=DESC");
            HTTPResp = HttpUtils.executeRestAPI(sca_wksp_url + "?limit=20&filter[state]=no_action&sort[num_cols]=DESC", "GET", headerBasicAuth, null);

            int status1 = HTTPResp.getStatusLine().getStatusCode();
            assertTrue("GET response is " + status1, status1 == 200 || status1 == 201);

            sca_group_resp = HttpUtils.parseResponseAsJSONObject(HTTPResp);
            assertNotNull("GET response is NULL " , sca_group_resp);
            LOGGER.info(sca_group_resp.toString());

            assertTrue("GET response does not contain " + Constants.METADATA, sca_group_resp.containsKey(Constants.METADATA));
            assertTrue("GET response does not contain " + Constants.ENTITY, sca_group_resp.containsKey(Constants.ENTITY));

            JSONObject group_entity = sca_group_resp.getJSONObject(Constants.ENTITY);
            assertTrue(Constants.ENTITY + " has no " + Constants.GROUPS , group_entity.containsKey(Constants.GROUPS));

            // fetch groups array
            JSONArray jsonArray = group_entity.getJSONArray(Constants.GROUPS);

            assertTrue("Empty group ", !jsonArray.isEmpty());
            assertTrue("Group size expected>=2, actual=" +jsonArray.size(), jsonArray.size()>=2);

            // fetch first group
            JSONObject group1 = new JSONObject();
            for(int i=0; i<jsonArray.size();i++) {
                if(jsonArray.getJSONObject(i).getInt(Constants.NUM_COLS) == 8) {
                    group1 = jsonArray.getJSONObject(0);
                }
            }
            assertTrue("Unable to found group with num_cols = 8", !group1.isEmpty());
            assertEquals("group1 size is not correct", group1.size(), 13);

            assertEquals("group1 " + Constants.NUM_DATASETS + " is not correct", group1.getInt(Constants.NUM_DATASETS), 6);
            assertEquals("group1 " + Constants.NUM_COLS + " is not correct", group1.getInt(Constants.NUM_COLS), 8);
            assertTrue("group1 " + Constants.MAX_SIM_SCORE + " is not correct : " + group1.getInt(Constants.MAX_SIM_SCORE), group1.getInt(Constants.MAX_SIM_SCORE) >= 99);
            assertEquals("group1 " + Constants.MIN_SIM_SCORE + " is not correct : " + group1.getInt(Constants.MIN_SIM_SCORE), group1.getInt(Constants.MIN_SIM_SCORE), 86);
            assertTrue("group1 doesn't not contains reference column", group1.has(Constants.REFERENCE_COLUMN_NAME));

            JSONObject sca_group_cols_resp = new JSONObject();
            // fetch similar columns for a group
            String group_id = group1.getString(Constants.GROUP_ID);
            String sca_wksp_group_url = sca_wksp_url + "/" + group_id;
            LOGGER.info("GET " + sca_wksp_group_url);
            HTTPResp = HttpUtils.executeRestAPI(sca_wksp_group_url + "?max_threshold=100&sort[similarity_score]=DESC", "GET", headerBasicAuth,
                    null);

            int status2 = HTTPResp.getStatusLine().getStatusCode();
            assertTrue("GET response is " + status2, status2 == 200 || status2 == 201);
            sca_group_cols_resp = HttpUtils.parseResponseAsJSONObject(HTTPResp);
            assertNotNull("GET response is NULL " , sca_group_cols_resp);
            LOGGER.info(sca_group_cols_resp.toString());

            assertTrue("Empty group ", !sca_group_cols_resp.isEmpty());
            assertTrue("GET response does not contain " + Constants.METADATA, sca_group_cols_resp.containsKey(Constants.METADATA));
            JSONObject columns_metadata = sca_group_cols_resp.getJSONObject(Constants.METADATA);
            assertTrue("columns_metadata does not contain " + Constants.GROUP_ID, columns_metadata.containsKey(Constants.GROUP_ID));
            assertTrue("columns_metadata does not contain " + Constants.STATE, columns_metadata.containsKey(Constants.STATE));
            assertEquals("columns_metadata state is " + columns_metadata.getString(Constants.STATE).trim(),
                columns_metadata.getString(Constants.STATE).trim(), Constants.NO_ACTION);

            assertTrue("GET response does not contain " + Constants.ENTITY, sca_group_cols_resp.containsKey(Constants.ENTITY));
            JSONObject columns_entity = sca_group_cols_resp.getJSONObject(Constants.ENTITY);
            assertTrue("columns_entity does not contain " + Constants.COLUMNS, columns_entity.containsKey(Constants.COLUMNS));

            JSONArray columns = columns_entity.getJSONArray(Constants.COLUMNS);

            assertTrue("Empty columns ", !columns.isEmpty());
            assertEquals("incorrect columns size=" + columns.size(), columns.size(), 7);

            // fetch first group
            JSONObject jsonObject = columns.getJSONObject(0);
            assertTrue("First group has incorrect size=" + jsonObject.size(), jsonObject.size() >= 20);

            assertEquals(
                "First group has incorrect " + Constants.INFERRED_DATA_CLASS + "="
                    + jsonObject.getString(Constants.INFERRED_DATA_CLASS),
                jsonObject.getString(Constants.INFERRED_DATA_CLASS), "NoClassDetected");
            assertEquals(
                "First group has incorrect " + Constants.SIMILARITY + "=" + jsonObject.getInt(Constants.SIMILARITY),
                jsonObject.getInt(Constants.SIMILARITY), 100);
            assertTrue("First group doesn't not contains column name", jsonObject.has(Constants.COLUMN_NAME));
            
            // reject group
            String action_body = "{\"action\" : \"reject\"}";
            StringEntity action_entity = new StringEntity(action_body);
            LOGGER.info("POST " + sca_wksp_group_url + "/action");
            HTTPResp = HttpUtils.executeRestAPI(sca_wksp_group_url + "/action", "POST", headerBasicAuth, action_entity);
            int status3 = HTTPResp.getStatusLine().getStatusCode();
            assertTrue("POST response is " + status3, status3 == 200 || status3 == 201);

            // fetch again same group and check state
            LOGGER.info("GET " + sca_wksp_group_url + "?max_threshold=100");
            HTTPResp = HttpUtils.executeRestAPI(sca_wksp_group_url + "?max_threshold=100", "GET", headerBasicAuth,
                    null);
            int status4 = HTTPResp.getStatusLine().getStatusCode();
            assertTrue("GET response is " + status4, status4 == 200 || status4 == 201);

            JSONObject sca_group_cols_new_resp = HttpUtils.parseResponseAsJSONObject(HTTPResp);
            assertNotNull("GET response is NULL " , sca_group_cols_new_resp);
            LOGGER.info(sca_group_cols_new_resp.toString());
            assertTrue("Empty sca_group_cols_new_resp", !sca_group_cols_new_resp.isEmpty());
            assertTrue(sca_group_cols_new_resp.containsKey(Constants.METADATA));
            JSONObject columns_metadata1 = sca_group_cols_new_resp.getJSONObject(Constants.METADATA);
            assertTrue(columns_metadata1.containsKey(Constants.GROUP_ID));
            assertTrue(columns_metadata1.containsKey(Constants.STATE));
            assertEquals(columns_metadata1.getString(Constants.STATE).trim(), Constants.REJECTED);

            // state revert back
            // restore group
            String action_body1 = "{\"action\" : \"restore\"}";
            LOGGER.info("POST " + sca_wksp_group_url + "/action");
            StringEntity action_entity1 = new StringEntity(action_body1);
            HTTPResp = HttpUtils.executeRestAPI(sca_wksp_group_url + "/action", "POST", headerBasicAuth,
                    action_entity1);
            int status5 = HTTPResp.getStatusLine().getStatusCode();
            assertTrue("POST response is " + status5, status5 == 200 || status5 == 201);
            
            // fetch again same group and check state
            LOGGER.info("GET " + sca_wksp_group_url + "?max_threshold=100");
            HTTPResp = HttpUtils.executeRestAPI(sca_wksp_group_url + "?max_threshold=100", "GET", headerBasicAuth,
                    null);
            int status6 = HTTPResp.getStatusLine().getStatusCode();
            assertTrue("GET response is " + status6, status6 == 200 || status6 == 201);

            JSONObject sca_group_cols_new_resp1 = HttpUtils.parseResponseAsJSONObject(HTTPResp);
            assertNotNull("GET response is NULL " , sca_group_cols_new_resp1);
            LOGGER.info(sca_group_cols_new_resp1.toString());
            assertTrue(!sca_group_cols_new_resp1.isEmpty());
            assertTrue(sca_group_cols_new_resp1.containsKey(Constants.METADATA));
            JSONObject columns_metadata2 = sca_group_cols_new_resp1.getJSONObject(Constants.METADATA);
            assertTrue(columns_metadata2.containsKey(Constants.GROUP_ID));
            assertTrue(columns_metadata2.containsKey(Constants.STATE));
            assertEquals(columns_metadata2.getString(Constants.STATE).trim(), Constants.NO_ACTION);
            
            
            // accept group
            String uuid = UUID.randomUUID().toString();
            String className = "test" + uuid + "Name";
            String action = "accept";
            String classDescription = "A new class generated from SimilarColumnsEndToEndTest";
            double confidence_threshold = 0.75;
            String accept_action_body = "{\"confidence_threshold\" : \"" + confidence_threshold
                    + "\", \"class_name\" : \"" + className + "\", \"action\" : \"" + action
                    + "\", \"description\" : \"" + classDescription + "\"}";
            StringEntity accept_action_entity = new StringEntity(accept_action_body);
            LOGGER.info("POST " + sca_wksp_group_url + "/action");
            HTTPResp = HttpUtils.executeRestAPI(sca_wksp_group_url + "/action", "POST", headerBasicAuth, accept_action_entity);
            int status7 = HTTPResp.getStatusLine().getStatusCode();
            assertTrue("POST response is " + status7, status7 == 200 || status7 == 201);
            
            //fetch data_class from IGC and validates
            LOGGER.info("GET " + getDataClassRidURL);
            HTTPResp = HttpUtils.executeRestAPI(getDataClassRidURL, "GET", headerBasicAuth, null);
            JSONObject resp1 = HttpUtils.parseResponseAsJSONObject(HTTPResp);
            assertNotNull("GET response is NULL " , resp1);
            LOGGER.info(resp1.toString());
            JSONArray data_class_json_array = resp1.getJSONArray("items");
            
            boolean found = false;
            JSONObject data_class_jsonObject = new JSONObject();
            // looping over JSONArray to fetch Rid of created custom_class
            for (int i = 0; i < data_class_json_array.length(); i++) {
                data_class_jsonObject = data_class_json_array.getJSONObject(i);
                if (data_class_jsonObject.getString("_name").equals(className)) {
                    found = true;
                    break;
                }
            }
            
            assertTrue("Found dataclass",found);
            
            data_class_rid = data_class_jsonObject.getString("_id");
            String url = dataClassByRidURL + "/" + data_class_rid;
            LOGGER.info("GET " + url);
            HTTPResp = HttpUtils.executeRestAPI(url, "GET", headerBasicAuth, null);
            int status8 = HTTPResp.getStatusLine().getStatusCode();
            assertTrue("GET response is " + status8, status8 == 200 || status8 == 201);
            
            JSONObject data_class_resp = HttpUtils.parseResponseAsJSONObject(HTTPResp);
            assertNotNull("GET response is NULL " , data_class_resp);
            LOGGER.info(data_class_resp.toString());
            
            assertEquals(className, data_class_resp.getString("_name"));
            assertEquals(confidence_threshold*100, data_class_resp.getDouble("default_threshold"), 0.01);
            
        } catch (InsightsException | JSONException | UnsupportedEncodingException e) {
            LOGGER.error("Erorr while test_similarColumnsE2E", e);
            assertTrue("Erorr while test_similarColumnsE2E-" + e.getMessage(), false);
        }

    }
    
    /**
     * create column similarity data class using BG endpoint
     * - fetch not accepted groups for a workspace
     * - create data class
     * - verify column similarity type data class in WKC side
     */
    @Test
    public void test_createColumnSimilarityDataClassFromBG() {
        
        HttpResponse HTTPResp;
        try {
            //fetch only "no_action" group
            String sca_wksp_url = getCustomClassURL + workspace_id;
            JSONObject sca_group_resp = new JSONObject();
            LOGGER.info("GET "  +sca_wksp_url + "?limit=20&filter[state]=no_action&sort[num_cols]=DESC");
            HTTPResp = HttpUtils.executeRestAPI(sca_wksp_url + "?limit=20&filter[state]=no_action&sort[num_cols]=DESC", "GET", headerBasicAuth, null);

            int status = HTTPResp.getStatusLine().getStatusCode();
            assertTrue("GET response is " + status, status == 200 || status == 201);

            sca_group_resp = HttpUtils.parseResponseAsJSONObject(HTTPResp);
            assertNotNull("GET response is NULL " , sca_group_resp);
            LOGGER.info(sca_group_resp.toString());

            assertTrue("GET response does not contain " + Constants.METADATA, sca_group_resp.containsKey(Constants.METADATA));
            assertTrue("GET response does not contain " + Constants.ENTITY, sca_group_resp.containsKey(Constants.ENTITY));

            JSONObject group_entity = sca_group_resp.getJSONObject(Constants.ENTITY);
            assertTrue(Constants.ENTITY + " has no " + Constants.GROUPS , group_entity.containsKey(Constants.GROUPS));
            
            
            JSONArray jsonArray = group_entity.getJSONArray(Constants.GROUPS);

            assertTrue("Empty group ", !jsonArray.isEmpty());
            assertTrue("Group size expected>=2, actual=" +jsonArray.size(), jsonArray.size()>=2);

            // fetch first group
            JSONObject group = new JSONObject();
            for(int i=0; i<jsonArray.size();i++) {
                if(jsonArray.getJSONObject(i).getInt(Constants.NUM_COLS) == 4) {
                    group = jsonArray.getJSONObject(0);
                }
            }
            assertTrue("Unable to found group with num_cols = 4", !group.isEmpty());
            assertEquals("group size is not correct", group.size(), 13);

            
            String group_id = group.getString(Constants.GROUP_ID);
            String create_dataclass_url = sca_wksp_url + "/" + group_id + "/action?is_wkc=true";
            LOGGER.info("POST " + create_dataclass_url);
            
            //check negative scenarios while creating action body
            String uuid = UUID.randomUUID().toString();
            String className = "test" + uuid + "Name";
            String action = "accept";
            String classDescription = "A new Column Similarity type data class generated from InsighstE2E";
            double confidence_threshold = 1.0;
            
            //-ve case 1 : not passing classname , it should return bad request
            String accept_action_body = "{\"confidence_threshold\" : \"" + confidence_threshold
                    + "\", \"action\" : \"" + action
                    + "\", \"description\" : \"" + classDescription + "\"}";
            StringEntity accept_action_entity = new StringEntity(accept_action_body);
            HTTPResp = HttpUtils.executeRestAPI(create_dataclass_url, "POST", headerBearerAuth, accept_action_entity);
            status = HTTPResp.getStatusLine().getStatusCode();
            assertTrue("POST response is " + status, status == 400);
            JSONObject create_dc_resp = HttpUtils.parseResponseAsJSONObject(HTTPResp);
            assertEquals("incorrect response - ","Invalid payload to accept group", create_dc_resp.getString(Constants.MESSAGE));
            
            //-ve case 2 : not passing confidence , it should return bad request
            accept_action_body = "{ \"class_name\" : \"" + className + "\", \"action\" : \"" + action
                    + "\", \"description\" : \"" + classDescription + "\"}";
            accept_action_entity = new StringEntity(accept_action_body);
            HTTPResp = HttpUtils.executeRestAPI(create_dataclass_url, "POST", headerBearerAuth, accept_action_entity);
            status = HTTPResp.getStatusLine().getStatusCode();
            assertTrue("POST response is " + status, status == 400);
            create_dc_resp = HttpUtils.parseResponseAsJSONObject(HTTPResp);
            assertEquals("incorrect response - ","Invalid payload to accept group", create_dc_resp.getString(Constants.MESSAGE));
            
            //passing correct body should return OK
            accept_action_body = "{\"confidence_threshold\" : \"" + confidence_threshold
                    + "\", \"class_name\" : \"" + className + "\", \"action\" : \"" + action
                    + "\", \"description\" : \"" + classDescription + "\"}";
            accept_action_entity = new StringEntity(accept_action_body);
            
            // accept group using /v3/data_class
            HTTPResp = HttpUtils.executeRestAPI(create_dataclass_url, "POST", headerBearerAuth, accept_action_entity);
            status = HTTPResp.getStatusLine().getStatusCode();
            assertTrue("POST response is " + status, status == 200 || status == 201);
            create_dc_resp = HttpUtils.parseResponseAsJSONObject(HTTPResp);
            
            assertTrue("Empty Response while creating data class", create_dc_resp.has(Constants.MESSAGE));
            create_dc_resp = create_dc_resp.getJSONObject(Constants.MESSAGE);
            assertTrue("Empty Response while creating data class", create_dc_resp.has("resources"));
            
            artifact_id = create_dc_resp.getJSONArray("resources").getJSONObject(0).getString("artifact_id");
            version_id = create_dc_resp.getJSONArray("resources").getJSONObject(0).getString("version_id");
            
            assertTrue("artifact_id is NULL or empty ", artifact_id!= null && !artifact_id.isEmpty());
            assertTrue("version_id_id is NULL or empty ", version_id!= null && !version_id.isEmpty());
            
            //v3/data_classes/dcc4ae0e-0298-4566-9a75-fb86b610b675/versions/0f61003c-c71e-489c-82d8-01fc60d89aae
            
            //fetch group based on artifact and version_id
            String fetch_artifact_url = zen_url + "/v3/data_classes/" + artifact_id + "/versions/" + version_id;
            HTTPResp = HttpUtils.executeRestAPI(fetch_artifact_url, "GET", headerBearerAuth, null);
            status = HTTPResp.getStatusLine().getStatusCode();
            assertTrue("GET response is " + status, status == 200 || status == 201);
            
            JSONObject fetch_data_class = HttpUtils.parseResponseAsJSONObject(HTTPResp);
            assertTrue("GET response does not contain " + Constants.METADATA, fetch_data_class.containsKey(Constants.METADATA));
            assertTrue("GET response does not contain " + Constants.ENTITY, fetch_data_class.containsKey(Constants.ENTITY));
            
            assertEquals(className, fetch_data_class.getJSONObject(Constants.METADATA).getString(Constants.NAME));
            assertEquals("DRAFT", fetch_data_class.getJSONObject(Constants.METADATA).getString("state"));
            
            JSONObject entity = fetch_data_class.getJSONObject(Constants.ENTITY);
            assertEquals("Data class type is not columnSimilarity","ColumnSimilarity", entity.getString("data_class_type"));
            assertEquals("Threshold should be = 95",95, entity.getInt("threshold"));
            assertEquals("Data class descrition not matched", classDescription, entity.getString("long_description"));
            
        } catch (InsightsException | JSONException | UnsupportedEncodingException e) {
            LOGGER.error("Erorr while test_createColumnSimilarityDataClassFromBG", e);
            assertTrue("Erorr while test_createColumnSimilarityDataClassFromBG-" + e.getMessage(), false);
        }
    }

    /**
     * -ve test to verify authentication when is_wkc is true/false
     */
    @Test
    public void test_createColumnSimilarityDataClassNegativeTests() {
        
        HttpResponse HTTPResp;
        try {
            String sca_wksp_url = getCustomClassURL + workspace_id;
            String group_id = UUID.randomUUID().toString();
            String create_dataclass_url = sca_wksp_url + "/" + "group_id" + "/action";
            
            String uuid = UUID.randomUUID().toString();
            String className = "test" + uuid + "Name";
            String action = "accept";
            String classDescription = "A new Column Similarity type data class generated from InsighstE2E";
            double confidence_threshold = 1.0;
            String accept_action_body = "{\"confidence_threshold\" : \"" + confidence_threshold
                    + "\", \"class_name\" : \"" + className + "\", \"action\" : \"" + action
                    + "\", \"description\" : \"" + classDescription + "\"}";
            StringEntity accept_action_entity = new StringEntity(accept_action_body);
            LOGGER.info("POST " + create_dataclass_url);
            
            // case 1: if is_wkc true and passing basic auth, authentication should fails
            HTTPResp = HttpUtils.executeRestAPI(create_dataclass_url+"?is_wkc=true", "POST", headerBasicAuth, accept_action_entity);
            int status = HTTPResp.getStatusLine().getStatusCode();
            assertTrue("POST response is " + status, status == 401);
            
            // case 2: if is_wkc false and passing wkc token, authentication should fails
            HTTPResp = HttpUtils.executeRestAPI(create_dataclass_url, "POST", headerBearerAuth, accept_action_entity);
            status = HTTPResp.getStatusLine().getStatusCode();
            assertTrue("POST response is " + status, status == 401);
            
            // case 3: a) passing wrong group_id (any string)
            HTTPResp = HttpUtils.executeRestAPI(create_dataclass_url+"?is_wkc=true", "POST", headerBearerAuth, accept_action_entity);
            status = HTTPResp.getStatusLine().getStatusCode();
            assertTrue("POST response is " + status, status == 404);
            
            //case 3: b) passing wrong group_id (valid uuid)
            create_dataclass_url = sca_wksp_url + "/" + group_id + "/action";
            HTTPResp = HttpUtils.executeRestAPI(create_dataclass_url+"?is_wkc=true", "POST", headerBearerAuth, accept_action_entity);
            status = HTTPResp.getStatusLine().getStatusCode();
            assertTrue("POST response is " + status, status == 404);
            
            //case 4: passing no action, it should return bad request
            accept_action_body = "{\"confidence_threshold\" : \"" + confidence_threshold
                    + "\", \"class_name\" : \"" + className + "\",  \"description\" : \"" + classDescription + "\"}";
            accept_action_entity = new StringEntity(accept_action_body);
            HTTPResp = HttpUtils.executeRestAPI(create_dataclass_url+"?is_wkc=true", "POST", headerBearerAuth, accept_action_entity);
            status = HTTPResp.getStatusLine().getStatusCode();
            assertTrue("POST response is " + status, status == 400);
            
            JSONObject response = HttpUtils.parseResponseAsJSONObject(HTTPResp);
            assertEquals("incorrect response - ","action is missing", response.getString(Constants.MESSAGE));
            
        } catch (UnsupportedEncodingException | InsightsException | JSONException e) {
            LOGGER.error("Erorr while test_createColumnSimilarityDataClassNegativeTests", e);
            assertTrue("Erorr while test_createColumnSimilarityDataClassNegativeTests-" + e.getMessage(), false);
        } 
        
    }
    
}
