/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.datarules;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.mit.gov.ms.common.CommonUtils;
import com.mit.gov.ms.common.FeatureEnum;
import com.mit.gov.ms.common.XMetaUtils;
import com.mit.gov.ms.common.apriori.NamedItem;

import de.mrapp.apriori.Output;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.sql.DataSource;

public class DataRulesResultManager implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataRulesResultManager.class);
	private String dataRulesQuery = XMetaUtils.getDataRulesQuery();

	private DataSource xmetaDS = null;

	public DataRulesResultManager(DataSource xmetaDatasource) {
		this.xmetaDS = xmetaDatasource;
		if (this.xmetaDS == null) {
		    LOGGER.warn("xmetaDS is null !!");
		}
	}

	public String getDataRulesQuery() {
		return this.dataRulesQuery;
	}

	public void setDataRulesQuery(String dataRulesModQuery) {
		this.dataRulesQuery = dataRulesModQuery;
	}

	@Override
    public void run() {
        try {
            if (FeatureEnum.AUTOMATIONRULES.initiatExecution()) {
                try {
                    doRun();
                } catch (Throwable e) {
                    LOGGER.error("Error while analysis for automation rules suggestions", e);
                }
                try {
                    FeatureEnum.AUTOMATIONRULES.finalizeExecution();
                } catch (Throwable e) {
                    LOGGER.error("Error while finalizing analysis for automation rules suggestions", e);
                }
            }
        } catch (Throwable e) {
            LOGGER.error("Error while initializing analysis for automation rules suggestions", e);
        }
    }
	
	public void doRun() {
//		// Get List of ADVANCEDRESULTS_XMETA from INVESTIGATE_COLUMNANALYSISRESULTS and
//		// Column Identities from IAVIEWS.IAREGISTEREDCOLUMN
//		Connection dbcon = null;
//		try {
//			dbcon = xmetaDS.getConnection();
//		} catch (SQLException e1) {
//			LOGGER.error("Got Error while getConnection", e1);
//		}
//
//		JSONArray suggestedAutomationRules = new JSONArray();
//		String CAResultsQuery = XMetaUtils.getDataRulesQuery();
//		PreparedStatement stmt = XMetaUtils.getPreparedStatement(dbcon, CAResultsQuery);
//		ResultSet rs = XMetaUtils.executeStmtWithParam(stmt, null);
//		DataRulesUtils dataRulesUtilObj = null;
//		JSONArray aprioriInputResult = new JSONArray();
//
//		// Data Rule suggestion for *The asset* has the term assigned to it.
//		if (rs != null) {
//			dataRulesUtilObj = new DataRulesUtilAssetTerm();
//			JSONArray rsJSON = ((DataRulesUtilAssetTerm) dataRulesUtilObj).getJSONrsAssetTerm(rs);
//			System.out.println(rsJSON);
//			if (rsJSON.size() > 2) {
//				aprioriInputResult = dataRulesUtilObj.getDataRulesNormalizedOutput(rsJSON);
//				System.out.println(aprioriInputResult);
//				if (aprioriInputResult != null) {
//					Output<NamedItem> result = CommonUtils.AprioriResult(aprioriInputResult);
//					if (result != null && !result.getRuleSet().isEmpty() ) {
//						JSONObject jsonResponse = dataRulesUtilObj.generateJSONResponse(result);
//						try {
//							suggestedAutomationRules.addAll(jsonResponse.getJSONArray("result"));
//						} catch (JSONException e) {
//							LOGGER.error("Got Error while adding rules", e);
//						}
//					}
//				}
//			} else {
//				LOGGER.warn("Insuffient Data to apriori to generate suggestion.");
//			}
//		} else {
//			LOGGER.error("ResultSet is null");
//		}
//		XMetaUtils.closePreparedStatement(stmt);
//
//		// data rules label query
//		CAResultsQuery = XMetaUtils.getDataRulesForLabel();
//        PreparedStatement stmt2 = XMetaUtils.getPreparedStatement(dbcon, CAResultsQuery);
//        rs = XMetaUtils.executeStmtWithParam(stmt2, null);
//		if (rs != null) {
//			dataRulesUtilObj = new DataRulesUtilAssetLabel();
//			JSONArray rsJSON = ((DataRulesUtilAssetLabel) dataRulesUtilObj).getJSONrsAssetLabel(rs);
//			if (rsJSON.size() > 2) {
//				aprioriInputResult = dataRulesUtilObj.getDataRulesNormalizedOutput(rsJSON);
//				if (aprioriInputResult != null) {
//					Output<NamedItem> result = CommonUtils.AprioriResult(aprioriInputResult);
//					if (result != null && !result.getRuleSet().isEmpty()) {
//						JSONObject jsonResponse = dataRulesUtilObj.generateJSONResponse(result);
//						try {
//							suggestedAutomationRules.addAll(jsonResponse.getJSONArray("result"));
//						} catch (JSONException e) {
//							LOGGER.error("Got Error while adding rules", e);
//						}
//					}
//				} else {
//					LOGGER.warn("Insuffient Data to apriori to generate suggestion.");
//				}
//			}
//		} else {
//			LOGGER.error("ResultSet is null");
//		}
//		XMetaUtils.closePreparedStatement(stmt2);
//
//		// Data Rule suggestion for *The asset* is classified by the dataclass.
//		CAResultsQuery = XMetaUtils.getDataRulesForDataClass();
//        PreparedStatement stmt3 = XMetaUtils.getPreparedStatement(dbcon, CAResultsQuery);
//        rs = XMetaUtils.executeStmtWithParam(stmt3, null);
//		if (rs != null) {
//			dataRulesUtilObj = new DataRulesUtilAssetDataclass();
//			JSONArray rsJSON = ((DataRulesUtilAssetDataclass) dataRulesUtilObj).getJSONrsAssetDataClass(rs);
//			if (rsJSON.size() > 2) {
//				aprioriInputResult = dataRulesUtilObj.getDataRulesNormalizedOutput(rsJSON);
//				if (aprioriInputResult != null) {
//					Output<NamedItem> result = CommonUtils.AprioriResult(aprioriInputResult);
//					if (result != null && !result.getRuleSet().isEmpty()) {
//						JSONObject jsonResponse = dataRulesUtilObj.generateJSONResponse(result);
//						try {
//							suggestedAutomationRules.addAll(jsonResponse.getJSONArray("result"));
//						} catch (JSONException e) {
//							LOGGER.error("Got Error while adding rules", e);
//						}
//					}
//				}
//			} else {
//				LOGGER.warn("Insufficient Data to apriori to generate suggestion.");
//			}
//		} else {
//			LOGGER.error("ResultSet is null");
//		}
//        XMetaUtils.closePreparedStatement(stmt3);
//
//		// Data Rule suggestion for *The asset* has the name which has the value.
//		CAResultsQuery = XMetaUtils.getDataRulesForName();
//        PreparedStatement stmt4 = XMetaUtils.getPreparedStatement(dbcon, CAResultsQuery);
//        rs = XMetaUtils.executeStmtWithParam(stmt4, null);
//		if (rs != null) {
//			dataRulesUtilObj = new DataRulesUtilAssetName();
//			JSONArray rsJSON = ((DataRulesUtilAssetName) dataRulesUtilObj).getJSONrsAssetName(rs);
//			if (rsJSON.size() > 2) {
//				aprioriInputResult = dataRulesUtilObj.getDataRulesNormalizedOutput(rsJSON);
//				if (aprioriInputResult != null) {
//					Output<NamedItem> result = CommonUtils.AprioriResult(aprioriInputResult);
//					if (result != null && !result.getRuleSet().isEmpty()) {
//						JSONObject jsonResponse = dataRulesUtilObj.generateJSONResponse(result);
//						try {
//							suggestedAutomationRules.addAll(jsonResponse.getJSONArray("result"));
//						} catch (JSONException e) {
//							LOGGER.error("Got Error while adding rules", e);
//						}
//					}
//				}
//			} else {
//				LOGGER.warn("Insufficient Data to apriori to generate suggestion.");
//			}
//		} else {
//			LOGGER.error("ResultSet is null");
//		}
//        XMetaUtils.closePreparedStatement(stmt4);
//
//		// Data Rule suggestion for *The asset* has a term with the attribute which has
//		// the value.
//		CAResultsQuery = XMetaUtils.getDataRulesForTermHasAttribute();
//        PreparedStatement stmt5 = XMetaUtils.getPreparedStatement(dbcon, CAResultsQuery);
//        rs = XMetaUtils.executeStmtWithParam(stmt5, null);
//		if (rs != null) {
//			dataRulesUtilObj = new DataRulesUtilTermHasAttribute();
//			JSONArray rsJSON = ((DataRulesUtilTermHasAttribute) dataRulesUtilObj).getJSONrsAssetTermAttribute(rs);
//			if (rsJSON.size() > 2) {
//				aprioriInputResult = dataRulesUtilObj.getDataRulesNormalizedOutput(rsJSON);
//				if (aprioriInputResult != null) {
//					Output<NamedItem> result = CommonUtils.AprioriResult(aprioriInputResult);
//					if (result != null && !result.getRuleSet().isEmpty()) {
//						JSONObject jsonResponse = dataRulesUtilObj.generateJSONResponse(result);
//						try {
//							suggestedAutomationRules.addAll(jsonResponse.getJSONArray("result"));
//						} catch (JSONException e) {
//							LOGGER.error("Got Error while adding rules", e);
//						}
//					}
//				}
//			} else {
//				LOGGER.warn("Insufficient Data to apriori to generate suggestion.");
//			}
//		} else {
//			LOGGER.error("ResultSet is null");
//		}
//        XMetaUtils.closePreparedStatement(stmt5);
//
//		// Data Rule suggestion for *The asset* has the attribute which has the value.
//		CAResultsQuery = XMetaUtils.getDataRulesForAssetHasAttribute();
//        PreparedStatement stmt6 = XMetaUtils.getPreparedStatement(dbcon, CAResultsQuery);
//        rs = XMetaUtils.executeStmtWithParam(stmt6, null);
//		if (rs != null) {
//			dataRulesUtilObj = new DataRulesUtilAssetAttribute();
//			JSONArray rsJSON = ((DataRulesUtilAssetAttribute) dataRulesUtilObj).getJSONrsAssetAttribute(rs);
//			if (rsJSON.size() > 2) {
//				aprioriInputResult = dataRulesUtilObj.getDataRulesNormalizedOutput(rsJSON);
//				if (aprioriInputResult != null) {
//					Output<NamedItem> result = CommonUtils.AprioriResult(aprioriInputResult);
//					if (result != null && !result.getRuleSet().isEmpty()) {
//						JSONObject jsonResponse = dataRulesUtilObj.generateJSONResponse(result);
//						try {
//							suggestedAutomationRules.addAll(jsonResponse.getJSONArray("result"));
//						} catch (JSONException e) {
//							LOGGER.error("Got Error while adding rules", e);
//						}
//					}
//				}
//			} else {
//				LOGGER.warn("Insufficient Data to apriori to generate suggestion.");
//			}
//		} else {
//			LOGGER.error("ResultSet is null");
//		}
//        XMetaUtils.closePreparedStatement(stmt6);
//
//        dataRulesUtilObj.addSuggestedRules(suggestedAutomationRules);
//		// merge based on action
//		dataRulesUtilObj.mergeRulesOnAction();
//		
//		dataRulesUtilObj.removeDuplicates();
//		JSONArray result = dataRulesUtilObj.getSuggestedRules();
//		//POST suggested automation rules
//		dataRulesUtilObj.createSuggestedAutoamationRules(result);
//
//		// No need to cache as its stored in IIS/Xmeta Store use only when testing/debugging
//		/*
//		try {
//		    new DataRulesResultCache().setDataRulesJson(new JSONObject().put(Constants.DATA_RULES, result));
//		} catch (JSONException e1) {
//			LOGGER.error("Got Error while DataRules Result Cache", e1);
//		}
//		*/
//		try {
//			dbcon.close();
//		} catch (SQLException e) {
//			LOGGER.error("Got Error while closeConnection", e);
//		}
//	
	}
}
