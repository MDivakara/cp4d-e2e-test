/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.domaindata.functional.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.gov.ms.common.Constants;
import com.mit.gov.ms.common.InsightsConfiguration;
import com.mit.gov.ms.common.InsightsException;
import com.mit.gov.ms.common.task.api.InsightsTaskStatus;
import com.mit.gov.ms.common.task.api.InsightsTaskType;
import com.mit.gov.ms.domaindata.SimilarColumnsUtils;
import com.mit.gov.ms.test.standalone.UnitTestUtils;

public class SCAFunctionalTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(SCAFunctionalTest.class);

    private static SimilarColumnsUtils similarColumnsUtils = null;
    private static String TEST_SUFFIX = InsightsConfiguration.getInstance()
            .getTestProperty("insights.db2oncloud.test.suffix");
    private static String workspace_name = "SSC_WS_" + TEST_SUFFIX;
    private static String workspace_id = null;
    private static int limit = 20;
    private static int offset = 0;
    private final static String tenant_id = "9999";

    @BeforeClass
    public static void testSetup() {
       //TODO

    }

    @AfterClass
    public static void cleanup() {
        // TODO
    }
    
    

    /**
     * fetch sca groups while checking sorting (ASC/DESC)
     */
    @Test
    public void test_getSCASuggestedGroups1() {

        try {
            // fetching group in descending order by num_cols
            JSONArray jsonArray = similarColumnsUtils.getSCAGroups(tenant_id, workspace_id, null, null, "DESC", "", "", limit, offset);

            assertTrue(!jsonArray.isEmpty());
            assertEquals(jsonArray.size(), 3);

            // fetch first group
            JSONObject group1 = jsonArray.getJSONObject(0);
            assertEquals(group1.size(), 12);

            assertEquals(group1.getInt(Constants.NUM_DATASETS), 6);
            assertEquals(group1.getInt(Constants.NUM_COLS), 8);
            assertEquals(group1.getInt(Constants.MAX_SIM_SCORE), 100);
            assertEquals(group1.getInt(Constants.MIN_SIM_SCORE), 86);
            assertEquals(group1.getString(Constants.REFERENCE_COLUMN_NAME), "COL1");

            // fetching group in asc order by num_cols
            JSONArray jsonArray1 = similarColumnsUtils.getSCAGroups(tenant_id, workspace_id, null, null, "ASC", "", "", limit,
                    offset);

            assertTrue(!jsonArray1.isEmpty());
            assertEquals(jsonArray1.size(), 3);

            // fetch first group
            JSONObject group2 = jsonArray1.getJSONObject(0);
            assertEquals(group2.size(), 12);

            assertEquals(group2.getInt(Constants.NUM_DATASETS), 4);
            assertEquals(group2.getInt(Constants.NUM_COLS), 4);
            assertEquals(group2.getInt(Constants.MAX_SIM_SCORE), 100);
            assertEquals(group2.getInt(Constants.MIN_SIM_SCORE), 100);
            assertTrue(group2.has(Constants.REFERENCE_COLUMN_NAME));

        } catch (InsightsException | JSONException e) {
            assertTrue("Error while test_getSCASuggestedGroups1", false);
        }

    }

    /**
     * fetch sca group filter by filter_state
     */
    @Test
    public void test_getSCASuggestedGroups2() {

        try {
            // fetching group in descending order with filter_state 'no_action'
            JSONArray jsonArray = similarColumnsUtils.getSCAGroups(tenant_id, workspace_id, "no_action", null, "DESC", "", "", limit,
                    offset);

            assertTrue(!jsonArray.isEmpty());
            assertEquals(jsonArray.size(), 3);

            // fetch first group
            JSONObject group1 = jsonArray.getJSONObject(0);
            assertEquals(group1.size(), 12);

            assertEquals(group1.getInt(Constants.NUM_DATASETS), 6);
            assertEquals(group1.getInt(Constants.NUM_COLS), 8);
            assertEquals(group1.getInt(Constants.MAX_SIM_SCORE), 100);
            assertEquals(group1.getInt(Constants.MIN_SIM_SCORE), 86);
            assertEquals(group1.getString(Constants.REFERENCE_COLUMN_NAME), "COL1");

            // negative cases
            // passing wrong filter_state
            JSONArray jsonArray1 = similarColumnsUtils.getSCAGroups(tenant_id, workspace_id, "no", null, "", "", "", limit,
                    offset);
            assertTrue(jsonArray1.isEmpty());

        } catch (InsightsException | JSONException e) {
            assertTrue("Error while test_getSCASuggestedGroups2", false);
        }
    }

    /**
     * fetch sca group filter by filter_name
     */
    @Test
    public void test_getSCASuggestedGroups3() {

        try {
            // fetching group in descending order with filter_state 'no_action'
            JSONArray jsonArray = similarColumnsUtils.getSCAGroups(tenant_id, workspace_id, null, "COL1", "DESC", "", "", limit,
                    offset);

            assertTrue(!jsonArray.isEmpty());
            assertEquals(jsonArray.size(), 2);

            // fetch first group
            JSONObject group1 = jsonArray.getJSONObject(0);
            assertEquals(group1.size(), 12);

            assertEquals(group1.getInt(Constants.NUM_DATASETS), 6);
            assertEquals(group1.getInt(Constants.NUM_COLS), 8);
            assertEquals(group1.getInt(Constants.MAX_SIM_SCORE), 100);
            assertEquals(group1.getInt(Constants.MIN_SIM_SCORE), 86);
            assertEquals(group1.getInt(Constants.TOTAL_COUNT), 2);
            assertEquals(group1.getString(Constants.REFERENCE_COLUMN_NAME), "COL1");

            // negative cases
            // passing wrong filter_name
            JSONArray jsonArray1 = similarColumnsUtils.getSCAGroups(tenant_id, workspace_id, null, "testcol", "", "", "", limit,
                    offset);
            assertTrue(jsonArray1.isEmpty());
        } catch (InsightsException | JSONException e) {
            assertTrue("Error while test_getSCASuggestedGroups3", false);
        }
    }

    /**
     * negative scenario for fetch sca group
     */
    @Test
    public void test_getSCASuggestedGroups4() {

        try {
            // passing wrong workspace_id
            JSONArray jsonArray1 = similarColumnsUtils.getSCAGroups(tenant_id, "workspace_id", "no_action", null, "", "", "",
                    limit, offset);
            assertTrue(jsonArray1.isEmpty());

            JSONArray jsonArray2 = similarColumnsUtils.getSCAGroups(tenant_id, null, null, null, "", "", "", limit, offset);
            assertTrue(jsonArray2.isEmpty());

            // limit 0 is not allowed (will be ignored) - Can return value if any old run was done
            //JSONArray jsonArray3 = similarColumnsUtils.getSCAGroups(tenant_id, workspace_id, null, null, "", "", "", 0, 0);
            //LOGGER.info(jsonArray3.toString());
            //assertTrue(jsonArray3.isEmpty());
        } catch (InsightsException e) {
            assertTrue("Error while test_getSCASuggestedGroups4", false);
        }
    }

    /**
     * fetch sca group filter by filter_name and state
     */
    @Test
    public void test_getSCASuggestedGroups5() {

        try {
            // fetching group in descending order with filter_state 'no_action' and
            // filter_name 'col1'
            JSONArray jsonArray = similarColumnsUtils.getSCAGroups(tenant_id, workspace_id, "no_action", "COL1", "DESC", "",
                    "", limit, offset);

            assertTrue(!jsonArray.isEmpty());
            assertEquals(jsonArray.size(), 2);

            // fetch first group
            JSONObject group1 = jsonArray.getJSONObject(0);
            assertEquals(group1.size(), 12);

            assertEquals(group1.getInt(Constants.NUM_DATASETS), 6);
            assertEquals(group1.getInt(Constants.NUM_COLS), 8);
            assertEquals(group1.getInt(Constants.MAX_SIM_SCORE), 100);
            assertEquals(group1.getInt(Constants.MIN_SIM_SCORE), 86);
            assertEquals(group1.getInt(Constants.TOTAL_COUNT), 2);
            assertEquals(group1.getString(Constants.REFERENCE_COLUMN_NAME), "COL1");

            String ref_col_id = group1.getString(Constants.REFERENCE_COLUMN_ID);

            JSONObject jsonObject = similarColumnsUtils.getRefColumnForAGroup(tenant_id, ref_col_id);

            JSONObject refColumn = new JSONObject(jsonObject.getString(Constants.COL_INFO));
            assertEquals(refColumn.size(), 21);

            assertEquals(refColumn.getString(Constants.INFERRED_DATA_CLASS), "NoClassDetected");
            assertEquals(refColumn.getInt(Constants.SIMILARITY), 1);
            assertEquals(refColumn.getString(Constants.COLUMN_NAME), "COL1");

            assertTrue(refColumn.has(Constants.TABLE_RID));
            assertTrue(refColumn.has(Constants.COLUMN_RID));
            assertTrue(refColumn.has(Constants.DATABASE_NAME));
            assertTrue(refColumn.has(Constants.SAMPLE_VALUES));
            assertTrue(refColumn.has(Constants.TABLE_NAME));
            assertTrue(refColumn.has(Constants.COL_LOCATION));
            assertTrue(refColumn.has(Constants.MAX_VALUE));
            assertTrue(refColumn.has(Constants.MIN_VALUE));
            assertTrue(refColumn.has(Constants.MIN_VALUE));

        } catch (InsightsException | JSONException e) {
            assertTrue("Error while test_getSCASuggestedGroups3", false);
        }
    }


    @Test
    public void test_getSimilarColumnsForGroup1() {

        try {
            JSONArray jsonArray = similarColumnsUtils.getSCAGroups(tenant_id, workspace_id, null, null, "DESC", "", "", limit, offset);

            assertTrue(!jsonArray.isEmpty());
            assertEquals(jsonArray.size(), 3);

            // fetch first group
            JSONObject group1 = jsonArray.getJSONObject(0);
            assertEquals(group1.size(), 12);

            assertEquals(group1.getInt(Constants.NUM_DATASETS), 6);
            assertEquals(group1.getInt(Constants.NUM_COLS), 8);
            assertEquals(group1.getInt(Constants.MAX_SIM_SCORE), 100);
            assertEquals(group1.getInt(Constants.MIN_SIM_SCORE), 86);
            assertEquals(group1.getString(Constants.REFERENCE_COLUMN_NAME), "COL1");

            // fetching group in asc order by similarity_score
            String group_id = group1.getString(Constants.GROUP_ID);
            JSONObject result = similarColumnsUtils.getAllSimilarColumnForGroup(tenant_id, workspace_id, group_id, 0, 100, null,
                    "DESC", "", limit, offset);

            assertTrue(!result.isEmpty());
            JSONArray columns = result.getJSONArray(Constants.COLUMNS);

            assertTrue(!columns.isEmpty());
            assertEquals(columns.size(), 8);

            // fetch first group
            JSONObject jsonObject = columns.getJSONObject(0);
            assertEquals(jsonObject.size(), 21);

            assertEquals(jsonObject.getString(Constants.INFERRED_DATA_CLASS), "NoClassDetected");
            assertEquals(jsonObject.getInt(Constants.SIMILARITY), 100);
            assertEquals(jsonObject.getString(Constants.COLUMN_NAME), "COL1");

            // fetching group in desc order by similarity_score
            JSONObject result1 = similarColumnsUtils.getAllSimilarColumnForGroup(tenant_id, workspace_id, group_id, 0, 100, null,
                    "ASC", "", limit, offset);

            assertTrue(!result1.isEmpty());
            JSONArray columns1 = result1.getJSONArray(Constants.COLUMNS);

            assertTrue(!columns1.isEmpty());
            assertEquals(columns1.size(), 8);

            // fetch first group
            JSONObject jsonObject21 = columns1.getJSONObject(0);
            assertEquals(jsonObject21.size(), 21);

            assertEquals(jsonObject21.getString(Constants.INFERRED_DATA_CLASS), "NoClassDetected");
            assertEquals(jsonObject21.getInt(Constants.SIMILARITY), 86);
            assertEquals(jsonObject21.getString(Constants.COLUMN_NAME), "COL4");

        } catch (InsightsException | JSONException e) {
            assertTrue("Error while test_getSimilarColumnsForGroup1", false);
        }
    }

    @Test
    public void test_getSimilarColumnsForGroup2() {

        try {
            JSONArray jsonArray = similarColumnsUtils.getSCAGroups(tenant_id, workspace_id, null, null, "DESC", "", "", limit, offset);

            assertTrue(!jsonArray.isEmpty());
            assertEquals(jsonArray.size(), 3);

            // fetch first group
            JSONObject group1 = jsonArray.getJSONObject(0);
            assertEquals(group1.size(), 12);

            assertEquals(group1.getInt(Constants.NUM_DATASETS), 6);
            assertEquals(group1.getInt(Constants.NUM_COLS), 8);
            assertEquals(group1.getInt(Constants.MAX_SIM_SCORE), 100);
            assertEquals(group1.getInt(Constants.MIN_SIM_SCORE), 86);
            assertEquals(group1.getString(Constants.REFERENCE_COLUMN_NAME), "COL1");

            // fetching columns in descending order with filter_name 'COL1'
            String group_id = group1.getString(Constants.GROUP_ID);
            JSONObject result = similarColumnsUtils.getAllSimilarColumnForGroup(tenant_id, workspace_id, group_id, 0, 100, "COL1",
                    "DESC", "", limit, offset);

            assertTrue(!result.isEmpty());
            assertEquals(result.getInt(Constants.TOTAL_COUNT), 6);
            JSONArray columns = result.getJSONArray(Constants.COLUMNS);

            assertTrue(!columns.isEmpty());
            assertEquals(columns.size(), 6);

            // fetch first group
            JSONObject jsonObject = columns.getJSONObject(0);
            assertEquals(jsonObject.size(), 21);

            assertEquals(jsonObject.getString(Constants.INFERRED_DATA_CLASS), "NoClassDetected");
            assertEquals(jsonObject.getInt(Constants.SIMILARITY), 100);
            assertEquals(jsonObject.getString(Constants.COLUMN_NAME), "COL1");

            assertTrue(jsonObject.has(Constants.TABLE_RID));
            assertTrue(jsonObject.has(Constants.COLUMN_RID));
            assertTrue(jsonObject.has(Constants.DATABASE_NAME));
            assertTrue(jsonObject.has(Constants.SAMPLE_VALUES));
            assertTrue(jsonObject.has(Constants.TABLE_NAME));
            assertTrue(jsonObject.has(Constants.COL_LOCATION));
            assertTrue(jsonObject.has(Constants.MAX_VALUE));
            assertTrue(jsonObject.has(Constants.MIN_VALUE));
            assertTrue(jsonObject.has(Constants.MIN_VALUE));

            // fetching columns in descending order with wrong filter_name
            JSONObject result1 = similarColumnsUtils.getAllSimilarColumnForGroup(tenant_id, workspace_id, group_id, 0, 100,
                    "COL123", "", "", limit, offset);
            assertTrue(result1.getJSONArray(Constants.COLUMNS).isEmpty());

        } catch (InsightsException | JSONException e) {
            assertTrue("Error while test_getSimilarColumnsForGroup2", false);
        }
    }

    /**
     * negative scenario to fetch similar columns for a group
     */
    @Test
    public void test_getSimilarColumnsForGroup3() {

        try {
            JSONArray jsonArray = similarColumnsUtils.getSCAGroups(tenant_id, workspace_id, null, null, "ASC", "", "", limit, offset);
            assertTrue(!jsonArray.isEmpty());

            // fetch first group
            JSONObject group1 = jsonArray.getJSONObject(0);
            String group_id = group1.getString(Constants.GROUP_ID);
            // passing group_id as null
            JSONObject result = similarColumnsUtils.getAllSimilarColumnForGroup(tenant_id, workspace_id, null, 0, 100, null, "",
                    "", limit, offset);
            assertTrue(result.getJSONArray(Constants.COLUMNS).isEmpty());

            // passing wrong workspace_id and group_id
            JSONObject result1 = similarColumnsUtils.getAllSimilarColumnForGroup(tenant_id, "workspace_id", "group_id", 0, 100,
                    null, "", "", limit, offset);
            assertTrue(result1.getJSONArray(Constants.COLUMNS).isEmpty());

            // passing limit and offset both 0 - not allowed (will be ignored)
            JSONObject result2 = similarColumnsUtils.getAllSimilarColumnForGroup(tenant_id, workspace_id, group_id, 0, 100, null,
                    "", "", 0, 0);
            assertTrue(!result2.getJSONArray(Constants.COLUMNS).isEmpty());

            // passing more offset value than number of records
            JSONObject result3 = similarColumnsUtils.getAllSimilarColumnForGroup(tenant_id, workspace_id, group_id, 0, 100, null,
                    "", "", 1, 100);
            assertTrue(result3.getJSONArray(Constants.COLUMNS).isEmpty());

        } catch (InsightsException | JSONException e) {
            assertTrue("Error while test_getSimilarColumnsForGroup3", false);
        }
    }

    /**
     * 
     */
    @Test
    public void test_SCAResultsStatus() {

        String is_being_analysed = null;
        try {
            JSONObject status = similarColumnsUtils.getSCAResultsStatus(tenant_id, workspace_id);
            is_being_analysed = status.getString(Constants.ISBEINGANALYSED);
            int rev_no = status.getInt(Constants.REV_NO);

            if (is_being_analysed.equalsIgnoreCase("n")) {
                assertEquals(is_being_analysed, "n");

                // update status
                similarColumnsUtils.updateSCAResults(tenant_id, workspace_id, "y", -1, rev_no);
                JSONObject status1 = similarColumnsUtils.getSCAResultsStatus(tenant_id, workspace_id);

                String is_being_analysed1 = status1.getString(Constants.ISBEINGANALYSED);

                assertEquals(is_being_analysed1, "y");
            }
            // status revert back
            similarColumnsUtils.updateSCAResults(tenant_id, workspace_id, "n", -1, ++rev_no);
        } catch (InsightsException | JSONException e) {
            assertTrue("Error while test_SCAResultsStatus", false);
        }
    }

    /**
     * test to accept/reject group
     */
    @Test
    public void test_SCAResultsState() {
        try {
            JSONArray jsonArray = similarColumnsUtils.getSCAGroups(tenant_id, workspace_id, null, null, "", "", "", limit, offset);
            assertTrue(!jsonArray.isEmpty());

            // fetch first group
            JSONObject group1 = jsonArray.getJSONObject(0);
            String group_id = group1.getString(Constants.GROUP_ID);

            JSONObject stateJson = similarColumnsUtils.getSCAGroupState(tenant_id, workspace_id, group_id);
            String state = stateJson.getString(Constants.STATE);
            int rev_no = stateJson.getInt(Constants.REV_NO);

            if (state.equals(Constants.NO_ACTION)) {
                similarColumnsUtils.updateSCAGroupState(tenant_id, workspace_id, group_id, Constants.ACCEPTED, rev_no);
                String new_state = similarColumnsUtils.getSCAGroupState(tenant_id, workspace_id, group_id).getString(Constants.STATE);
                assertEquals(new_state, Constants.ACCEPTED);
            } else if (state.equals(Constants.REJECTED)) {
                similarColumnsUtils.updateSCAGroupState(tenant_id, workspace_id, group_id, Constants.NO_ACTION, rev_no);
                String new_state = similarColumnsUtils.getSCAGroupState(tenant_id, workspace_id, group_id).getString(Constants.STATE);
                assertEquals(new_state, Constants.NO_ACTION);
            }

            // state revert back
            similarColumnsUtils.updateSCAGroupState(tenant_id, workspace_id, group_id, Constants.NO_ACTION, ++rev_no);
        } catch (InsightsException | JSONException e) {
            assertTrue("Error while test_SCAResultsStatus", false);
        }
    }

}
