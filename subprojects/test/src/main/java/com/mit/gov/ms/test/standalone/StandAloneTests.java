/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.test.standalone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.gov.ms.common.Constants;
import com.mit.gov.ms.common.FeatureEnum;
import com.mit.gov.ms.common.InsightsConfiguration;
import com.mit.gov.ms.common.InsightsException;
import com.mit.gov.ms.common.InsightsMetaDataStore;
import com.mit.gov.ms.common.interfaces.IInsightsMetaDataStore;
import com.mit.gov.ms.common.task.api.InsightsTaskStatus;
import com.mit.gov.ms.common.task.api.InsightsTaskType;
import com.mit.gov.ms.datarules.DataRulesResultCache;
import com.mit.gov.ms.datarules.DataRulesResultManager;
import com.mit.gov.ms.domaindata.SimilarColumnsUtils;

public class StandAloneTests {

    private final static Logger LOGGER = LoggerFactory.getLogger(StandAloneTests.class);
    private static final InsightsConfiguration config = InsightsConfiguration.getInstance();
    private final static String tenant_id = "999";

    public StandAloneTests() {

    }

    public static void main(String[] args) throws JSONException, SQLException, InsightsException {
        boolean runAll = false;
        boolean runDomainData = false;
        boolean runDataRules = false;
        boolean runShowZK = false;
        boolean cleanZK = false;
        boolean runZK = false;
        boolean testQuery = false;
        // String sqlQuery = XMetaUtils.getCAResultsQuery();
        boolean runTaskM = true;

        LOGGER.info("Arg Size=" + args.length);
        if (args.length > 0) {
            runAll = false;
            // gradlew run -Pargs="refdata,domaindata"
            for (int i = 0; i < args.length; i++) {
                LOGGER.info(args[i]);
                if (args[i].equals("domaindata")) {
                    // gradlew run -Pargs=domaindata
                    runDomainData = true;
                } else if (args[i].equals("datarules")) {
                    // gradlew run -Pargs=datarules -DdbType=oracle
                    runDataRules = true;
                } else if (args[i].equals("shoszk")) {
                    // gradlew run -Pargs=showzk
                    runShowZK = true;
                } else if (args[i].equals("cleanzk")) {
                    // gradlew run -Pargs=cleanzk
                    cleanZK = true;
                } else if (args[i].equals("zk")) {
                    // gradlew run -Pargs=zk
                    runZK = true;
                } else if (args[i].equals("query")) {
                    // gradlew run -Pargs=query
                    testQuery = true;
                }
            }
        }

        // Initialize DataSource and InsightsMetadataStore
        //UnitTestUtils.initMetaDataStores();

        // if (runAll || runRefData)
        // testDB2DataSourceRefData();
        if (runAll || runDomainData)
            testDB2DataSourceDomainData();
        if (runAll || runDataRules)
            testDataRules();
        if (runAll || runZK)
            testZokkeper();
        if (cleanZK) // run only if cleanzk is called not with runall
            cleanZookeeper();
        if (runAll || runShowZK)
            showZookeeper();
//        if (runAll || runTaskM)
//            taskManager();
        // if (testQuery)
        // testQuery(sqlQuery);
    }

    private static void cleanZookeeper() throws JSONException, InsightsException {
        IInsightsMetaDataStore insightsMetaDataStore = InsightsMetaDataStore.getInstance();
        LOGGER.info("cleaning zookeeper/costom_class");
        insightsMetaDataStore.delete(Constants.CUSTOM_CLASS, null, null);
        LOGGER.info(insightsMetaDataStore.get(Constants.CUSTOM_CLASS, null, null, null, null).toString(4));
        LOGGER.info("cleaning zookeeper/ref_data");
        insightsMetaDataStore.delete(Constants.REF_DATA, null, null);
        LOGGER.info(insightsMetaDataStore.get(Constants.REF_DATA, null, null, null, null).toString(4));
    }

    private static void showZookeeper() throws JSONException, InsightsException {
        IInsightsMetaDataStore insightsMetaDataStore = InsightsMetaDataStore.getInstance();
        LOGGER.info("Show zookeeper/features");
        LOGGER.info(insightsMetaDataStore.get(Constants.FEATURES, null, null, null, null).toString(4));
        LOGGER.info("Show zookeeper/costom_class");
        LOGGER.info(insightsMetaDataStore.get(Constants.CUSTOM_CLASS, null, null, null, null).toString(4));
        LOGGER.info("Show zookeeper/ref_data");
        LOGGER.info(insightsMetaDataStore.get(Constants.REF_DATA, null, null, null, null).toString(4));
    }

    private static void testZokkeper() throws JSONException, InsightsException {
        // Initialize DataSource and InsightsMetadataStore
        // UnitTestUtils.getDataSource();
        IInsightsMetaDataStore insightsMetaDataStore = InsightsMetaDataStore.getInstance();
        insightsMetaDataStore.initSchema(config.getInsightsSchemaName(), config.getInsightsSchemaVersion());

        JSONArray testResult = new JSONArray();
        JSONArray testinnerResult = new JSONArray();
        JSONObject testdata1 = new JSONObject();
        testdata1.put("inferred_data_class", "Code");
        testdata1.put("table_rid", "ec1481df.fee6c3ac.ktsmb9tn3.cir0r1g.ttm2k1.594v0gvphkhgim016uss6");
        testdata1.put("column_rid", "ec1481df.c862f974.ktsmb9tn3.dcbcs32.fbhgrv.punao11n2dri2qaojtj5u");
        testdata1.put("suggested_classifier_type", "valid_values");
        testdata1.put("database_name", "NA");
        testdata1.put("column_name", "NBR_YEARS_CLI");
        testdata1.put("schema_name", "ACCOUNT_HOLDERS.data");
        testdata1.put("table_name", "/iaqa/BANK_DEMO/ACCOUNT_HOLDERS");

        JSONArray testdata2 = new JSONArray();
        testdata2.add(new JSONObject().put("count", 137).put("value", "26.0"));
        testdata2.add(new JSONObject().put("count", 131).put("value", "1.0"));
        testdata2.add(new JSONObject().put("count", 128).put("value", "11.0"));
        testdata2.add(new JSONObject().put("count", 124).put("value", "12.0"));
        testdata2.add(new JSONObject().put("count", 121).put("value", "13.0"));

        testdata1.put("sample_values", testdata2);
        testdata1.put("column_description", "NA");
        testdata1.put("is_db_table", false);
        testdata1.put("term_assignments", new JSONArray());
        testdata1.put("num_of_distinct_values", 46);
        testdata1.put("host_name", "NA");
        testdata1.put("total_num_of_values", 2941);

        testinnerResult.add(testdata1);
        testResult.add(testinnerResult);

        LOGGER.info("save - zookeeper/test_zookeeper/ec1481df.64b1b87d.r67m2loap.28ghsgp.9p8cd8.davd63s9l5dk8aqo3tjh9");
        insightsMetaDataStore.save("test_zookeeper", "ec1481df.64b1b87d.r67m2loap.28ghsgp.9p8cd8.davd63s9l5dk8aqo3tjh9",
                new JSONObject().put("result", testResult));
        LOGGER.info("get - zookeeper/test_zookeeper");
        LOGGER.info(insightsMetaDataStore.get("test_zookeeper", null, null, null, null).toString(4));
        LOGGER.info("get - zookeeper/test_zookeeper/ec1481df.64b1b87d.r67m2loap.28ghsgp.9p8cd8.davd63s9l5dk8aqo3tjh9");
        LOGGER.info(insightsMetaDataStore.get("test_zookeeper",
                "ec1481df.64b1b87d.r67m2loap.28ghsgp.9p8cd8.davd63s9l5dk8aqo3tjh9", null, null, null).toString(4));
        LOGGER.info(
                "delete - zookeeper/test_zookeeper/ec1481df.64b1b87d.r67m2loap.28ghsgp.9p8cd8.davd63s9l5dk8aqo3tjh9");
        insightsMetaDataStore.delete("test_zookeeper",
                "ec1481df.64b1b87d.r67m2loap.28ghsgp.9p8cd8.davd63s9l5dk8aqo3tjh9", null);
        LOGGER.info("get - zookeeper/test_zookeeper");
        LOGGER.info(insightsMetaDataStore.get("test_zookeeper", null, null, null, null).toString(4));
        LOGGER.info("delete - zookeeper/test_zookeeper");
        insightsMetaDataStore.delete("test_zookeeper", null, null);
        LOGGER.info("get - zookeeper/test_zookeeper");
        LOGGER.info(insightsMetaDataStore.get("test_zookeeper", null, null, null, null).toString(4));
        // LOGGER.info("zookeeper/costom_class");
        // LOGGER.info(insightsMetaDataStore.get("custom_class", null).toString(4));
        // LOGGER.info("zookeeper/ref_data");
        // LOGGER.info(insightsMetaDataStore.get("ref_data", null).toString(4));
        LOGGER.info(FeatureEnum.UNIT_TEST.getProperties().toString(4));
        LOGGER.info("FeatureEnum.UNIT_TEST.isFeatureAvailable()=" + FeatureEnum.UNIT_TEST.isFeatureAvailable());
        LOGGER.info("FeatureEnum.UNIT_TEST.initiatExecution()=" + FeatureEnum.UNIT_TEST.initiatExecution());
        LOGGER.info(FeatureEnum.UNIT_TEST.getProperties().toString(4));
        FeatureEnum.UNIT_TEST.enable(0, 0);
        LOGGER.info("FeatureEnum.UNIT_TEST.isEnabled()=" + FeatureEnum.UNIT_TEST.isEnabled());
        LOGGER.info("FeatureEnum.UNIT_TEST.initiatExecution()=" + FeatureEnum.UNIT_TEST.initiatExecution());
        LOGGER.info(insightsMetaDataStore.get(Constants.FEATURES, null, null, null, null).toString(4));
        LOGGER.info(FeatureEnum.UNIT_TEST.getProperties().toString(4));
        FeatureEnum.UNIT_TEST.disable();
        LOGGER.info("FeatureEnum.UNIT_TEST.isEnabled()=" + FeatureEnum.UNIT_TEST.isEnabled());
        LOGGER.info("FeatureEnum.UNIT_TEST.initiatExecution()=" + FeatureEnum.UNIT_TEST.initiatExecution());
        LOGGER.info(insightsMetaDataStore.get(Constants.FEATURES, null, null, null, null).toString(4));
        LOGGER.info(FeatureEnum.UNIT_TEST.getProperties().toString(4));
    }

    private static void testDataRules() throws SQLException, JSONException, InsightsException {
        DataSource ds = UnitTestUtils.getXmetaDataSource();
        Connection connection = ds.getConnection();
        try {
            LOGGER.info("Verifying Database connection");
            verifyDbConnection(connection);
            LOGGER.info("Database connection working");
        } finally {
            connection.close();
        }
        DataRulesResultManager dRResultManager = new DataRulesResultManager((DataSource) ds);
        dRResultManager.run();
        LOGGER.info(new DataRulesResultCache().getDataRulesJson().toString(4));
    }

    private static void verifyDbConnection(Connection connection) throws SQLException {
        LOGGER.info("Database connection URL is: " + connection.getMetaData().getURL());
        String dbProductName = connection.getMetaData().getDatabaseProductName();
        LOGGER.info("dbProductName=" + dbProductName);
        String sql;
        if (dbProductName.contains("Oracle")) {
            sql = "SELECT 1 FROM DUAL";
        } else if (dbProductName.contains("DB2")) {
            sql = "SELECT 1 FROM SYSIBM.SYSDUMMY1";
        } else {
            sql = "SELECT 1";
        }
        LOGGER.info(sql);
        PreparedStatement ps = connection.prepareStatement(sql);
        try {
            ps.executeQuery();
            LOGGER.info("verified Connection");
        } catch (SQLException e) {
            LOGGER.error("Error verifying connection", e);
            throw e;
        } finally {
            ps.close();
        }
    }

    private static void testQuery(String sql) throws SQLException, InsightsException {
        DataSource ds = UnitTestUtils.getXmetaDataSource();
        Connection connection = ds.getConnection();
        verifyDbConnection(connection);
        LOGGER.info(sql);
        PreparedStatement ps = connection.prepareStatement(sql);
        try {
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                LOGGER.info(rsmd.getColumnName(i));
            }
        } finally {
            ps.close();
        }
    }

    // private static void testDB2DataSourceRefData() throws SQLException,
    // JSONException, InsightsException {
    // DataSource ds = UnitTestUtils.getXmetaDataSource();
    // CAResultManager cAResultManager = new CAResultManager((DataSource)ds);
    //// XMetaUtils.setWorkspace_rid("ec1481df.64b1b87d.bdemnph0b.1g43f9c.0cin9v.abvijdujodj86tiv1duu1");
    // cAResultManager.run();
    // LOGGER.info(CAResultManager.getRefDataJson().toString(4));
    // }

    private static void testDB2DataSourceDomainData() throws SQLException, JSONException, InsightsException {
        // TODO: will need to convert this into test cases

        // DataSource ds = UnitTestUtils.getDataSource();
        // SCCResultManager cAResultManager = new SCCResultManager((DataSource)ds, null,
        // true);
        // XMetaUtils.setWorkspace_rid("ec1481df.64b1b87d.4vn6tgk0b.r06phtq.7015rm.3djdhp1uu28tssikk60l5");
        // cAResultManager.run();
        // LOGGER.info(new CustomClassUtils().getCustomClassJson().toString(4));
        // SCAServiceImpl myService = new SCAServiceImpl();
        // InsightsTaskExecutor te = new InsightsTaskExecutor();
        // JSONObject request_info = new
        // JSONObject().put(Constants.RUN_ON_NONCLASSIFIED_ONLY, true);
        // request_info.put(Constants.LAST_RUN_BY, "Shaik");
        // String task_id = myService.registerTask(tenant_id, InsightsTaskType.SCA,
        // "ec1481df.64b1b87d.4vn6tgk0b.r06phtq.7015rm.3djdhp1uu28tssikk60l5",
        // "shaik", request_info);
        // LOGGER.info("task_id=" + task_id);
        // JSONObject task = te.getTaskById(task_id);
        // LOGGER.info(task.toString(4));
        //
        // JSONObject tasksInQueue = te.getTaskByStatus(null, null,
        // Arrays.asList(InsightsTaskStatus.NEW));
        // LOGGER.info(tasksInQueue.toString(4));
        //
        // LOGGER.info("Execute New Tasks");
        // te.run();
        // while (InsightsTaskThreadPool.getInstance().hasActiveTaskThread(task_id)) {
        // try {
        // Thread.sleep(1000);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // }
        // LOGGER.info("Get Task In Queue");
        // tasksInQueue = te.getTaskByStatus(null, null,
        // Arrays.asList(InsightsTaskStatus.NEW));
        // LOGGER.info(tasksInQueue.toString(4));
        // task = te.getTaskById(task_id);
        // LOGGER.info(task.toString(4));
        // InsightsTaskThreadPool.getInstance().shutdown();
        // try {
        // InsightsTaskThreadPool.getInstance().awaitTermination(1l, TimeUnit.MINUTES);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // te.deleteTaskById(task_id);
        // LOGGER.info("Deleted");
        // task = te.getTaskById(task_id);
        // LOGGER.info(task.toString(4));

//        SimilarColumnsUtils similarColumnsUtils = new SimilarColumnsUtils();
//        String workspace_rid = "ec1481df.64b1b87d.4vn6tgk0b.r06phtq.7015rm.3djdhp1uu28tssikk60l5";
        // boolean result = similarColumnsUtils.isValidProjectRid(workspace_rid);
        // System.out.println(result);
        // System.out.println(similarColumnsUtils.registerSCATask(workspace_rid));
        // similarColumnsUtils.updateSCAStatus("ec1481df.64b1b87d.4vn6tgk0b.r06phtq.7015rm.3djdhp1uu28tssikk60l5",
        // "no");
        // System.out.println(similarColumnsUtils.getSCAGroupState(workspace_rid,
        // "b0f7265c-84ec-4a7e-acea-54817747e118abc"));
        // System.out.println(similarColumnsUtils.getSCAResultsStatus(workspace_rid));
//        jsonArray = similarColumnsUtils.getSCAGroups(workspace_rid, filter_state, filter_name, sort_numcols,
//                sort_numassets, sort_status, limit, offset);
//        JSONArray jsonObject = similarColumnsUtils.getSCAGroups(workspace_rid, "no_action", null, "ASC","", "", 20, 0);
//        System.out.println(jsonObject);
//         JSONObject jsonObject2 =
//         similarColumnsUtils.getRefColumnForAGroup(jsonObject.getJSONObject(0).getString(Constants.REFERENCE_COLUMN_ID));
//         JSONObject jsonObject3 = new JSONObject(jsonObject2.getString("col_info"));
//         System.out.println(jsonObject3.getJSONArray(Constants.SAMPLE_VALUES));

        // JSONArray jsonArray = similarColumnsUtils.getSCAGroups(workspace_rid,
        // "accept" , null,"NUM_DATASETS","", 20, 0);
        // for(int i=0;i<jsonArray.size();i++) {
        // JSONObject refJsonObject = jsonArray.getJSONObject(i);
        // JSONObject refCol = similarColumnsUtils
        // .getRefColumnForAGroup(refJsonObject.getString(Constants.REFERENCE_COLUMN_ID));
        // JSONObject refColSampleValues = new JSONObject(refCol.getString("col_info"));
        //
        // JSONObject refColJson = new JSONObject();
        // refColJson.put(Constants.COLUMN_NAME,
        // refJsonObject.getString(Constants.REFERENCE_COLUMN_NAME));
        // refColJson.put(Constants.COLUMN_RID,
        // refJsonObject.getString(Constants.REFERENCE_COLUMN_ID));
        // refColJson.put(Constants.SAMPLE_VALUES,
        // refColSampleValues.getJSONArray(Constants.SAMPLE_VALUES) );
        //
        // refJsonObject.put(Constants.REFERENCE_COLUMN, refColJson);
        // }
        //
        //
        //
        // JSONObject result = new JSONObject();
        // JSONObject metadata = new JSONObject();
        // metadata.put(Constants.WORKSPACE_RID, workspace_rid);
        //
        // JSONObject entity = new JSONObject();
        // entity.put(Constants.GROUPS, jsonArray);
        //
        // result.put(Constants.METADATA, metadata);
        // result.put(Constants.ENTITY, entity);

        // JSONArray jsonArray = similarColumnsUtils.getSCAGroups(workspace_rid,
        // "accept" , null,"NUM_DATASETS","", 20, 0);
        // JSONArray jsonArray =
        // similarColumnsUtils.getSCAGroupsBasedOnSearch(workspace_rid, "col1", null,
        // "NUM_DATASETS",
        // "", 20, 0);
        // for (int i = 0; i < jsonArray.size(); i++) {
        // JSONObject refJsonObject = jsonArray.getJSONObject(i);
        // JSONObject refCol = similarColumnsUtils
        // .getRefColumnForAGroup(refJsonObject.getString(Constants.REFERENCE_COLUMN_ID));
        // JSONObject refColSampleValues = new JSONObject(refCol.getString("col_info"));
        //
        // JSONObject refColJson = new JSONObject();
        // refColJson.put(Constants.COLUMN_NAME,
        // refJsonObject.getString(Constants.REFERENCE_COLUMN_NAME));
        // refColJson.put(Constants.COLUMN_RID,
        // refJsonObject.getString(Constants.REFERENCE_COLUMN_ID));
        // refColJson.put(Constants.SAMPLE_VALUES,
        // refColSampleValues.getJSONArray(Constants.SAMPLE_VALUES));
        //
        // refJsonObject.put(Constants.REFERENCE_COLUMN, refColJson);
        // }
        //
        // JSONObject result = new JSONObject();
        // JSONObject metadata = new JSONObject();
        // metadata.put(Constants.WORKSPACE_RID, workspace_rid);
        //
        // JSONObject entity = new JSONObject();
        // entity.put(Constants.GROUPS, jsonArray);
        //
        // result.put(Constants.METADATA, metadata);
        // result.put(Constants.ENTITY, entity);

//        getAllSimilarColumnForGroup(String workspace_rid, String group_id, int min_threshold,
//                int max_threshold, String filter_colname, String sort_similarityscore, String sort_location, int limit,
//                int offset)
//        JSONObject result = similarColumnsUtils.getAllSimilarColumnForGroup(workspace_rid,
//                "b0f7265c-84ec-4a7e-acea-54817747e1181", 0, 100, "col1","", "DESC", 2, 0);
        //
        // //find refcolumn
        // String refcolumnId = result.getString(Constants.REFERENCE_COLUMN_ID);
        // JSONArray columns = result.getJSONArray(Constants.COLUMNS);
        // JSONObject refColumnJson = new JSONObject();
        // int indextoremove = 0;
        // for(int i=0;i<columns.size();i++) {
        // JSONObject column_identity = columns.getJSONObject(i) ;
        // if(refcolumnId.equals(column_identity.getString(Constants.COLUMN_RID)) ) {
        // refColumnJson = column_identity;
        // refColumnJson.remove(Constants.SIMILARITY);
        // indextoremove = i;
        // break;
        // }
        // }
        // columns.remove(indextoremove);
        //
        // result.put(Constants.REFERENCE_COLUMN, refColumnJson);
        // result.remove(Constants.REFERENCE_COLUMN_ID);
//         System.out.println(result);
        // System.out.println(refColumnJson);
        
//        SimilarColumnsUtils similarColumnsUtils = new SimilarColumnsUtils();
//        String col_id = "ec1481df.c862f974.07jn8rdjp.9gm4viq.bj7cdj.og6ugpsottcp74vmio6in";
//        JSONObject jsonObject = similarColumnsUtils.getCAResultsByColId(col_id);
//        System.out.println(jsonObject.toString(4));
    }

}
