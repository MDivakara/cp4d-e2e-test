/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

import org.apache.wink.json4j.JSON;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;


public class TestConditionCompare {
	
	public static void main(String[] args) throws NullPointerException, JSONException {
		String conditionJSON1 = "{\"condition_\": {\n" + 
				"        \"right_\": {\n" + 
				"          \"testOn\": \"asset\",\n" + 
				"          \"fields_details\": [\n" + 
				"            {\n" + 
				"              \"name\": \"datasource\",\n" + 
				"              \"rid\": \"b1c497ce.6e83759b.00ambsp4h.h2fkmv3.1d7trm.3lkm5ve6om7ivti4vqhpp\",\n" + 
				"              \"value\": \"COWL1.FYRE.IBM.COM:IADB\"\n" + 
				"            }\n" + 
				"          ],\n" + 
				"          \"type_\": \"asset_from_datasource\"\n" + 
				"        },\n" + 
				"        \"left_\": {\n" + 
				"          \"testOn\": \"asset\",\n" + 
				"          \"fields_details\": [\n" + 
				"            {\n" + 
				"              \"name\": \"selectedItem-field\",\n" + 
				"              \"rid\": \"6662c0f2.e1b1ec6c.6ubkg4m3e.8636c8h.pjchq0.sqee0mrqfa83nfgjha3l8\",\n" + 
				"              \"value\": \"Business Information\\/Customer Information\\/Customer Identifier\"\n" + 
				"            }\n" + 
				"          ],\n" + 
				"          \"value_\": \"Business Information\\/Customer Information\\/Customer Identifier\",\n" + 
				"          \"type_\": \"TermAssignment\"\n" + 
				"        },\n" + 
				"        \"type_\": \"OR\"\n" + 
				"      }}";
		
		JSONObject condition1 = (JSONObject) JSON.parse(conditionJSON1);
		Condition conditionobject = new Condition();
		conditionobject.buildConditionFromJSON(condition1, null, 0);
		
		String conditionJSON2 = "{\n" + 
				"      \"condition_\": {\n" + 
				"        \"right_\": {\n" + 
				"          \"testOn\": \"asset\",\n" + 
				"          \"fields_details\": [\n" + 
				"            {\n" + 
				"              \"name\": \"selectedItem-field\",\n" + 
				"              \"rid\": \"6662c0f2.e1b1ec6c.6vd4coo1d.oh4r117.s00hl7.vdof9c8u2pfia0ocsitda\",\n" + 
				"              \"value\": \"Information Governance\\/Information Governance Classifications\\/Data Classifications\\/Data Classes\\/Address\"\n" + 
				"            }\n" + 
				"          ],\n" + 
				"          \"value_\": \"Information Governance\\/Information Governance Classifications\\/Data Classifications\\/Data Classes\\/Address\",\n" + 
				"          \"type_\": \"TermAssignment\"\n" + 
				"        },\n" + 
				"        \"left_\": {\n" + 
				"          \"testOn\": \"asset\",\n" + 
				"          \"fields_details\": [\n" + 
				"            {\n" + 
				"              \"name\": \"selectedItem-field\",\n" + 
				"              \"rid\": \"6662c0f2.e1b1ec6c.6v4k87aqa.3sphukt.nhrt00.691a1rnt05laalogsbpt3\",\n" + 
				"              \"value\": \"Business Information\\/Organizational Information\\/Address\"\n" + 
				"            }\n" + 
				"          ],\n" + 
				"          \"value_\": \"Business Information\\/Organizational Information\\/Address\",\n" + 
				"          \"type_\": \"TermAssignment\"\n" + 
				"        },\n" + 
				"        \"type_\": \"OR\"\n" + 
				"      }}";
		
		JSONObject condition2 = (JSONObject) JSON.parse(conditionJSON2);
		Condition conditionobject2 = new Condition();
		conditionobject2.buildConditionFromJSON(condition2, null, 0);
		
		String conditionJSON3 = "{\"condition_\": {\n" + 
				"        \"right_\": {\n" + 
				"          \"testOn\": \"asset\",\n" + 
				"          \"fields_details\": [\n" + 
				"            {\n" + 
				"              \"name\": \"datasource\",\n" + 
				"              \"rid\": \"b1c497ce.6e83759b.00ambsp4h.h2fkmv3.1d7trm.3lkm5ve6om7ivti4vqhpp\",\n" + 
				"              \"value\": \"COWL1.FYRE.IBM.COM:IADB\"\n" + 
				"            }\n" + 
				"          ],\n" + 
				"          \"type_\": \"asset_from_datasource\"\n" + 
				"        },\n" + 
				"        \"left_\": {\n" + 
				"          \"testOn\": \"asset\",\n" + 
				"          \"fields_details\": [\n" + 
				"            {\n" + 
				"              \"name\": \"selectedItem-field\",\n" + 
				"              \"rid\": \"6662c0f2.e1b1ec6c.6ubkg4m3e.8636c8h.pjchq0.sqee0mrqfa83nfgjha3l8\",\n" + 
				"              \"value\": \"Business Information\\/Customer Information\\/Customer Identifier\"\n" + 
				"            }\n" + 
				"          ],\n" + 
				"          \"value_\": \"Business Information\\/Customer Information\\/Customer Identifier\",\n" + 
				"          \"type_\": \"TermAssignment\"\n" + 
				"        },\n" + 
				"        \"type_\": \"OR\"\n" + 
				"      }}";
		
		JSONObject condition3 = (JSONObject) JSON.parse(conditionJSON3);
		Condition conditionobject3 = new Condition();
		conditionobject3.buildConditionFromJSON(condition3, null, 0);
		
		//System.out.println(conditionobject2.isSimilarTo(conditionobject));
		System.out.println(conditionobject.isSimilarTo(conditionobject3));
		System.out.println("Done");
	}

}
