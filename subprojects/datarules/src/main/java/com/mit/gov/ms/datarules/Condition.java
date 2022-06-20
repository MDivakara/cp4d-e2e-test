/*
/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.datarules;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

public class Condition {

	private Condition parentNode;
	private Condition leftNode;
	private Condition rightNode;
	
	private ConditionType conditionType;
	private String leafType;
	
	private String leafValue;
	
	private int currentLevel;
	private int maxDepth;
	
	public Condition() {
		parentNode = null;
		leftNode = null;
		rightNode = null;
		
		conditionType = null;
		leafType = null;
		
		leafValue = "";
		
		currentLevel = 0;
		maxDepth = 0;
	}
	
	/*
	 * Condition will be built as a graph where the node can be either AND, OR, LEAF
	 * LEAF will be the expression and the other two represent their respective logical meaning
	 * a non LEAF node will have 2 child nodes left and right that represent 
	 * the further logic that spins out of the node which can be a LEAF or further logic
	 */
	public void buildConditionFromJSON(JSONObject condition, Condition parent, int currentLevel) {
		try {
			parentNode = parent;
			if(condition.containsKey("condition_") && (condition.getJSONObject("condition_").getString("type_").equals("OR")
					|| condition.getJSONObject("condition_").getString("type_").equals("AND"))) {
				
				JSONObject conditionDefinition = condition.getJSONObject("condition_");
				
				conditionType = ConditionType.valueOf(conditionDefinition.getString("type_"));
				leftNode = new Condition();
				leftNode.buildConditionFromJSON(new JSONObject().put("condition_",conditionDefinition.getJSONObject("left_")), this,currentLevel + 1);
				rightNode = new Condition(); 
				rightNode.buildConditionFromJSON(new JSONObject().put("condition_",conditionDefinition.getJSONObject("right_")), this,currentLevel + 1);
			} else {
				JSONObject conditionDefinition = condition.getJSONObject("condition_");
				leafType = conditionDefinition.getString("type_");
				conditionType = ConditionType.LEAF;
				if(conditionDefinition.containsKey("fields_details")) {
					if(conditionDefinition.getJSONArray("fields_details").getJSONObject(0).containsKey("rid") && conditionDefinition.getJSONArray("fields_details").getJSONObject(0).get("rid") != null) {
	                    leafValue = conditionDefinition.getJSONArray("fields_details").getJSONObject(0).getString("rid");
	                } else {
	                    leafValue = conditionDefinition.getJSONArray("fields_details").getJSONObject(0).getString("value");
	                }
				}
				
				if(parent != null) {
					parent.leafReached(currentLevel);
				}
			}
		} catch (IllegalArgumentException e) {
			System.out.println("condition type or attribute type not recognized " + e.getMessage());
		} catch (JSONException e) {
			System.out.println("JSON parsing exception " + e.getMessage());
		}
	}
	
	public void leafReached(int level) {
		int depth = level - currentLevel;
		
		if(depth > maxDepth) {
			maxDepth = depth;
		}
		
		if(parentNode != null) {
			parentNode.leafReached(level);
		}
	}
	
	public boolean isSimilarTo(Condition otherCondition) {
		if(conditionType == ConditionType.LEAF) {
			if(!leafType.equals(otherCondition.leafType)) {
				return false;
			}
			
			if(!leafValue.equals(otherCondition.leafValue)) {
				return false;
			}
			return true;
		} else if(conditionType != null) {
			if(conditionType != otherCondition.conditionType) {
				return false;
			}
			
			//If maxDepth of both the conditions are not same then they cannot be similar conditions
			if(maxDepth != otherCondition.maxDepth) {
				return false;
			}
			
			//Check similarity of left nodes
			boolean leftIsSameAsLeft = leftNode.isSimilarTo(otherCondition.leftNode);
			
			boolean leftIsSameAsRight = false;
			
			//If left nodes are not same check similarity of left node with right
			if(!leftIsSameAsLeft) {
				leftIsSameAsRight = leftNode.isSimilarTo(otherCondition.rightNode);
			}
			
			//If left node is not same as either left or right of the other condition then 
			// the conditions are not same
			if(!leftIsSameAsLeft && !leftIsSameAsRight) {
				return false;
			}
			
			//If left tree is same as left tree then check if right branches are similar
			if(leftIsSameAsLeft) {
				return rightNode.isSimilarTo(otherCondition.rightNode);
			}
			
			//If left tree is same as right tree then check if right similar to left
			if(leftIsSameAsRight) {
				return rightNode.isSimilarTo(otherCondition.leftNode);
			}
			return false;
		} else {
			return false;
		}
	}
}
