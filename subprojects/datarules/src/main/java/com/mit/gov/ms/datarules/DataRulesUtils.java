/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.datarules;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.gov.ms.common.FeatureEnum;
import com.mit.gov.ms.common.HttpUtils;
import com.mit.gov.ms.common.InsightsConfiguration;
import com.mit.gov.ms.common.InsightsException;
import com.mit.gov.ms.common.apriori.NamedItem;

import de.mrapp.apriori.Output;
//import de.mrapp.apriori.metrics.Confidence;

abstract class DataRulesUtils {

	private final static Logger LOGGER = LoggerFactory.getLogger(DataRulesUtils.class);

	private JSONObject actionableRules = new JSONObject();
	private JSONArray SuggestedRules = new JSONArray();
	protected Map<String, String> termsPathMap = new HashMap<String, String>();
	
	public Map<String, String> getTermsPathMap() {
		return termsPathMap;
	}

	public void setTermsPathMap(Map<String, String> termsPathMap) {
		this.termsPathMap = termsPathMap;
	}

	protected Map<String, String> actionsMap = new HashMap<String, String>();

	String termPathurl;
	String actionableRulesurl;
	String basicAuth;
	int count = 0;

	public JSONObject getActionableRules() {
		return actionableRules;
	}

	public JSONArray getSuggestedRules() {
		return SuggestedRules;
	}

	public void addSuggestedRules(JSONArray paramSuggestedRules) {
		SuggestedRules.addAll(paramSuggestedRules);
	}

	// build the input for apriori algorithm in normalized form
	// Ex:[[a,b],[b,c],[a,c]]
	//abstract JSONArray getDataRulesNormalizedOutput(ResultSet rs);
	abstract JSONArray getDataRulesNormalizedOutput(JSONArray rsJSON);

	// generate post request JSON from Apriori algorithm suggestions
	abstract JSONObject generateJSONResponse(Output<NamedItem> inputData);

	// build a condition JSONObject required in POST request body to create a
	// automation rule.
	abstract JSONObject buildCondition(Set<String> terms);

	// check condition has right_
	abstract void hasRight(JSONObject cond, String term);

	// check condition has left_
	abstract void hasLeft(JSONObject cond, String term);

	DataRulesUtils() {

		termPathurl = InsightsConfiguration.getInstance().getIISBaseUrl() + "/igc-rest/v1/assets/";
		actionableRulesurl = InsightsConfiguration.getInstance().getIISBaseUrl()
				+ "/ia/api/dataQualityConfigurationRules";
        basicAuth = FeatureEnum.AUTOMATIONRULES.getIISBasicAuthToken();
	}

	// remove duplicate suggestions
	public void removeDuplicates() {
		JSONArray actionableRuleList = new JSONArray();
		JSONObject actionableRules = retriveActionableRules();

		try {

			if (actionableRules.has("actionableRules"))
				actionableRuleList = actionableRules.getJSONArray("actionableRules");
			else
				return;

			for (int i = 0; i < SuggestedRules.size(); i++) {
				
				boolean isDuplicate = false;
				count = 0;
				JSONObject suggestedRuleCond = SuggestedRules.getJSONObject(i);
				Condition sugConditionobject = new Condition();
				sugConditionobject.buildConditionFromJSON(suggestedRuleCond, null, 0);
				suggestedRuleCond.put("name_", "SAR_"+suggestedRuleCond.getJSONArray("actions_").getJSONObject(0).getString("value_")+"_0");

				for (int j = 0; j < actionableRuleList.size(); j++) {
					//check actions are same? 
					JSONObject automatedRule = actionableRuleList.getJSONObject(j);
					if (!suggestedRuleCond.getJSONArray("actions_").getJSONObject(0).getString("value_")
							.equals(automatedRule.getJSONArray("actions_").getJSONObject(0).getString("value_")))
					{
						continue;
					}
					Condition automatedConditionobject = new Condition();
					automatedConditionobject.buildConditionFromJSON(automatedRule, null, 0);
					if (automatedConditionobject.isSimilarTo(sugConditionobject)) {
						isDuplicate = true;
						break;
					}
					String existingName = automatedRule.getString("name_");
					if (existingName.startsWith("SAR_")) {
						String[]  tokens = existingName.split("_");
						try {
							count = count < Integer.parseInt(tokens[tokens.length-1]) ? Integer.parseInt(tokens[tokens.length-1]) : count;
						} catch (NumberFormatException e) {
							LOGGER.error("invalid number fromat ", e);
						}
						suggestedRuleCond.put("name_",
								"SAR_" + suggestedRuleCond.getJSONArray("actions_").getJSONObject(0).getString("value_")
										+ "_" + (count + 1));
					}	
					
				}

				if (isDuplicate) {
					SuggestedRules.getJSONObject(i).put("status_", "Duplicate");
				}
			}
		} catch (JSONException e) {
			LOGGER.error("Error while parsing JSON", e);
		}

	}

	// build absolute path of the term
	protected String getTermPath(String termRid, String termName) {

		if (termsPathMap.containsKey(termRid))
			return termsPathMap.get(termRid);
		
		// end point to get term path
		String url = termPathurl + termRid;
		Map<String, String> header = new HashMap<String, String>();
		header.put("Content-Type", "application/json");
		header.put("Authorization", "Basic " + basicAuth);

		JSONObject termDeatils = new JSONObject();
		JSONArray context = null;
		String termPath = "";

		try {
			HttpResponse HTTPResp = HttpUtils.executeRestAPI(url, "GET", header, null);
			termDeatils = HttpUtils.parseResponseAsJSONObject(HTTPResp);
			context = termDeatils.getJSONArray("_context");
			for (int i = 0; i < context.size(); i++) {
				JSONObject path = context.getJSONObject(i);
				termPath += path.getString("_name") + "/";
			}
			termPath = termPath + termName;
		} catch (InsightsException | JSONException e) {
			LOGGER.error("JSON exception " + e);
		}
		termsPathMap.put(termRid, termPath);
		termsPathMap.put(termPath, termRid);
		return termPath;
	}

	private JSONObject retriveActionableRules() {

		// end point to get full list of automated rules
		Map<String, String> header = new HashMap<String, String>();
		header.put("Content-Type", "application/json");
		header.put("Authorization", "Basic " + basicAuth);

		JSONObject actionableRules = new JSONObject();

		try {
			HttpResponse HTTPResp = HttpUtils.executeRestAPI(actionableRulesurl, "GET", header, null);
			actionableRules = HttpUtils.parseResponseAsJSONObject(HTTPResp);

		} catch (InsightsException e) {
			LOGGER.error("InsightsException exception " + e);
		}
		return actionableRules;
	}

	public void mergeRulesOnAction() {
		int len = SuggestedRules.size();
		if (len < 2) {
			return;
		}
		try {

			for (int i = 0; i < SuggestedRules.size() - 1; i++) {
				
				String currentActionValue = SuggestedRules.getJSONObject(i).getJSONArray("actions_").getJSONObject(0)
						.getJSONArray("fields_details").getJSONObject(0).getString("value");
				String nextActionValue;

				for (int j = i + 1; j < SuggestedRules.size(); j++) {
					nextActionValue = SuggestedRules.getJSONObject(j).getJSONArray("actions_").getJSONObject(0)
							.getJSONArray("fields_details").getJSONObject(0).getString("value");
					if (currentActionValue.equals(nextActionValue)) {
						JSONObject currentRule = SuggestedRules.getJSONObject(i);
						JSONObject matchingRule = SuggestedRules.getJSONObject(j);
						mergeRules(currentRule, matchingRule, i, j);
					}
				}
			}
			if (SuggestedRules.size() > 2) {
				String currentActionValue = SuggestedRules.getJSONObject(SuggestedRules.size() - 2)
						.getJSONArray("actions_").getJSONObject(0).getJSONArray("fields_details").getJSONObject(0)
						.getString("value");
				String nextActionValue = SuggestedRules.getJSONObject(SuggestedRules.size() - 1)
						.getJSONArray("actions_").getJSONObject(0).getJSONArray("fields_details").getJSONObject(0)
						.getString("value");
				if (currentActionValue.equals(nextActionValue)) {
					JSONObject currentRuleCond = SuggestedRules.getJSONObject(SuggestedRules.size() - 2);
					JSONObject matchingRuleCond = SuggestedRules.getJSONObject(SuggestedRules.size() - 1);
					mergeRules(currentRuleCond, matchingRuleCond, SuggestedRules.size() - 2, SuggestedRules.size() - 1);
				}
			}
		} catch (JSONException e) {
			LOGGER.error("JSON exception " + e);
		}

	}

	void mergeRules(JSONObject currentRule, JSONObject matchingRule, int ipos, int jpos) throws JSONException {
		if (currentRule.getJSONObject("condition_").has("right_")) {
			hasRightInCond(currentRule.getJSONObject("condition_"), matchingRule.getJSONObject("condition_"));
			SuggestedRules.remove(jpos);
		} else if (matchingRule.getJSONObject("condition_").has("right_")) {
			hasRightInCond(matchingRule.getJSONObject("condition_"), currentRule.getJSONObject("condition_"));
			JSONObject mergedRule = new JSONObject (SuggestedRules.get(jpos));
			SuggestedRules.remove(ipos);
			SuggestedRules.add(ipos, mergedRule);
			SuggestedRules.remove(jpos);
		} else {

			JSONObject rightCond = new JSONObject(currentRule.getJSONObject("condition_"));
			JSONObject leftCond = new JSONObject(matchingRule.getJSONObject("condition_"));
			currentRule.put("condition_", new JSONObject());
			currentRule.getJSONObject("condition_").put("right_", rightCond);
			currentRule.getJSONObject("condition_").put("left_", leftCond);
			currentRule.getJSONObject("condition_").put("type_", "OR");
			currentRule.remove("type_");
			currentRule.remove("testOn");
			currentRule.remove("fields_details");
			SuggestedRules.remove(jpos);
		}
	}

	void hasRightInCond(JSONObject currentRuleCond, JSONObject matchingRuleCond) throws JSONException {

		JSONObject right = currentRuleCond;

		if (currentRuleCond.has("right_")) {
			right = currentRuleCond.getJSONObject("right_");
			hasRightInCond(right, matchingRuleCond);
		} else {
			right.put("right_", new JSONObject(right));
			right.put("left_", matchingRuleCond);
			right.put("type_", "OR");
			right.remove("testOn");
			right.remove("fields_details");
			right.remove("value_");
		}

	}

	public JSONArray createSuggestedAutoamationRules(JSONArray jsonSuggestedRulsList) {

		Map<String, String> header = new HashMap<String, String>();
		header.put("Content-Type", "application/json");
		header.put("Authorization", "Basic " + basicAuth);
		JSONArray respRids = new JSONArray(); 

		try {
			for (int i = 0; i < jsonSuggestedRulsList.size(); i++) {
				JSONObject postBody = jsonSuggestedRulsList.getJSONObject(i);
				if (postBody.has("status_") && postBody.getString("status_").equals("Duplicate"))
					continue;
				
				String action = postBody.getJSONArray("actions_").getJSONObject(0).getString("value_");
				String[]  tokens = action.split("_");
				try {
					count = count < Integer.parseInt(tokens[tokens.length-1]) ? Integer.parseInt(tokens[tokens.length-1]) + 1 : count;
				} catch (NumberFormatException e) {
					count = 0;
					LOGGER.error("invalid number fromat ", e);
				}

				for(; true; count++) {
					HttpEntity bodyEntity = HttpUtils.getHTTPEntity(postBody.toString());
					HttpResponse HTTPResp = HttpUtils.executeRestAPI(actionableRulesurl, "POST", header, bodyEntity);
					JSONObject resp = HttpUtils.parseResponseAsJSONObject(HTTPResp);
					if(resp != null &&  HTTPResp.getStatusLine().getStatusCode() == 200 && resp.has("rid") ) {
						respRids.add(resp);
						break;
					} else if(resp != null && HTTPResp.getStatusLine().getStatusCode() == 409 && resp.has("errorType") 
							&& resp.getString("errorType").equals("CONFLICT")) {
						postBody.put("name_", "SAR_"+ action + "_" + (count));
						continue;
					} else {
						String errorType = resp.getString("errorType");
						if (!errorType.isEmpty()) {
							// Log error message and return false
							LOGGER.error("Unable to create automation rule ");
							LOGGER.error(resp.getString("errorType"));
							LOGGER.error(resp.getString("message"));
						}
						break;
					}
				}
			}
		} catch (InsightsException | JSONException e) {
			LOGGER.error("InsightsException exception " + e);
		}
		return respRids;
	}

	protected String getRuleStatus() {
		return "SUGGESTED";
	}
	
	protected String getDescription(String action) {
		return "Action, "+ action + ", is applied on multiple columns across workspaces when one "
				+ "of the derived conditions in this SUGGESTED rule is satisfied. Elevating the rule to "
				+ "ACCEPTED status will ensure auto enforcement of the rule.";
	}

}
