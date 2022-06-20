package com.mit.gov.ms.refdata;
/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */
//
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.UUID;
//import org.slf4j.LoggerFactory;
//import org.slf4j.Logger;
//
//import javax.sql.DataSource;
//import org.apache.wink.json4j.JSONArray;
//import org.apache.wink.json4j.JSONException;
//import org.apache.wink.json4j.JSONObject;
//
//
//public class CAResultManager implements Runnable {
//    private static final Logger LOGGER = LoggerFactory.getLogger(CAResultManager.class);
//    private String workSpaceQuery = XMetaUtils.getWorkSpaceQuery();
//    //private String columnAnalysisResultsQuery = XMetaUtils.getCAResultsQuery();
//    
//    public String getWorkSpaceQuery() {
//        return workSpaceQuery;
//    }
//
//    public void setWorkSpaceQuery(String workSpaceQuery) {
//        this.workSpaceQuery = workSpaceQuery;
//    }
//
//    private DataSource xmetaDS = null;
//   
//    public CAResultManager(DataSource xmetaDatasource) throws SQLException {
//        this.xmetaDS = xmetaDatasource;
//    }
//    
////    public String getCAResultsQuery() {
////        return this.columnAnalysisResultsQuery;
////    }
////    
////    public void setCAResultsQuery(String cAResultsModQuery) {
////        this.columnAnalysisResultsQuery = cAResultsModQuery;
////    }
//    
//    @Override
//    public void run() {
//        try {
//            if (FeatureEnum.REF_DATA.initiatExecution()) {
//                try {
//                    doRun();
//                } catch (Throwable e) {
//                    LOGGER.error("Error while analysis for Reference data suggestions", e);
//                }
//                try {
//                    FeatureEnum.REF_DATA.finalizeExecution();
//                } catch (Throwable e) {
//                    LOGGER.error("Error while finalizing analysis for Reference data suggestions", e);
//                }
//            }
//        } catch (Throwable e) {
//            LOGGER.error("Error while initializing analysis for Reference data suggestions", e);
//        }
//    }
//
//    public void doRun() {
//        // Get List of ADVANCEDRESULTS_XMETA from INVESTIGATE_COLUMNANALYSISRESULTS and Column Identities from IAVIEWS.IAREGISTEREDCOLUMN
//        Connection dbcon = null;;
//        try {
//            dbcon = xmetaDS.getConnection();
//        } catch (SQLException e1) {
//            LOGGER.error("Got Error while getConnection", e1);
//        }
//        
//        PreparedStatement stmt = XMetaUtils.getPreparedStatement(dbcon, workSpaceQuery);
//        ResultSet rs = XMetaUtils.executeStmtWithParam(stmt, null);
//        if (rs != null) {
//            JSONArray workspaceInfo = new JSONArray();
//            try {
//                while (rs.next()) {
//                		workspaceInfo.add(XMetaUtils.getWorkSpaceInfo(rs));
//                }
//            } catch (SQLException e) {
//                LOGGER.error("Got Error while CAResultManager -> Workspace -> rs.next()", e);
//            } catch (JSONException e) {
//        		LOGGER.error("Got Error while CAResultManager -> Workspace -> rs.next()", e);
//            }
//            for (int i=0; i< workspaceInfo.length(); i++) {
//                try {
//                    execute(dbcon, XMetaUtils.getCAResultsQuery(true), workspaceInfo.getJSONObject(i));
//                } catch (JSONException e) {
//                    LOGGER.error("Got Error while DDResultManager -> Workspace -> execute", e);
//                }
//            }
//            try {
//                InsightsMetaDataStore.getInstance().deleteNotInList(Constants.CUSTOM_CLASS, workspaceInfo);
//            } catch (InsightsException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        } else {
//            LOGGER.error("ResultSet is null while executing workspace query");
//        }
//        
//        try {
//            dbcon.close();
//        } catch (SQLException e) {
//            LOGGER.error("Got Error while closeConnection", e);
//        }
//    }
//    
//    public void execute(Connection dbcon, final String caResultsQuery, JSONObject projectRid) {
//        PreparedStatement stmt = XMetaUtils.getPreparedStatement(dbcon, caResultsQuery);
//        ResultSet rs = null;
//		try {
//			rs = XMetaUtils.executeStmtWithStringParam(stmt, projectRid.getString(Constants.WORKSPACE_RID));
//		} catch (JSONException e1) {
//			LOGGER.error("Got Error while DDResultManager -> Workspace -> execute", e1);
//		}
//        if (rs != null) {
//            try {
//                int count=0;
//                JSONArray cAResultsArray = new JSONArray();
//                ReferenceDataDetector referenceDataDetector = new ReferenceDataDetector();
//                while (rs.next()) {
//                    try {
//                        // Get ColumnAnalysis Result with the Column Identity
//                        JSONObject caResultsJson = XMetaUtils.getCAResultWithColumnIdentity(rs);
//                        //if (caResultsJson.getJSONObject(Constants.COLUMN_IDENTITY).getBoolean(Constants.IS_DB_TABLE)) {
//                            cAResultsArray.add(caResultsJson);
//                            count++;
//                        //}
//                    } catch (NullPointerException | IOException | SQLException | JSONException e) {
//                        LOGGER.error("Got Error while handling resultset", e);
//                    }
//                    if (count == Constants.MAX_RESULTS_ARRAY_SIZE) {
//                        referenceDataDetector.analyze(cAResultsArray, projectRid.getString(Constants.WORKSPACE));
//                        count = 0;
//                        cAResultsArray = new JSONArray();
//                    }
//                }
//                if (count > 0) {
//                    referenceDataDetector.analyze(cAResultsArray, projectRid.getString(Constants.WORKSPACE));
//                }
//                processResults(Constants.REF_DATA, projectRid.getString(Constants.WORKSPACE_RID), referenceDataDetector);
//            } catch (SQLException | JSONException e) {
//                LOGGER.error("Got Error while CAResultManager -> rs.next()", e);
//            }
//        } else {
//            LOGGER.error("ResultSet is null");
//        }
//        XMetaUtils.closePreparedStatement(stmt);
//    }
//    
//    private void processResults(String nodeType, String projectRid, ReferenceDataDetector referenceDataDetector) throws JSONException {
//        JSONArray result = referenceDataDetector.fetchResult();
//        
//        JSONArray resultantJsonArray = new JSONArray();
//        for (int i = 0 ; i < result.length() ; i++ ) {
//	        	String group_id = UUID.randomUUID().toString(); 
//	    		JSONObject jsonObject2 = new JSONObject();
//	    		jsonObject2.put(Constants.GROUP_ID, group_id);
//	    		jsonObject2.put(Constants.COLUMNS, result.getJSONArray(i));
//	    		jsonObject2.put(Constants.SUGGESTED_STATE, Constants.SUGGESTED);
//	    		jsonObject2.put(Constants.SUGGESTED_CLASSIFIER, Constants.VALUE_LIST_CLASSIFIER);
//	    		resultantJsonArray.add(jsonObject2);
//        }
//        
//        if (resultantJsonArray.length() > 0) {
//            try {
//                InsightsMetaDataStore.getInstance().save(nodeType, projectRid, new JSONObject().put("groups",resultantJsonArray));
//            } catch (InsightsException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//    }
//    
//    public static JSONObject getRefDataJson() {
//        return getRefDataJson(null);
//    }
//    
//    public static JSONObject getRefDataJson(String projectRid) {
//        try {
//            return InsightsMetaDataStore.getInstance().get(Constants.REF_DATA, projectRid, null, null, null);
//        } catch (InsightsException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return new JSONObject();
//    }
//
//}
