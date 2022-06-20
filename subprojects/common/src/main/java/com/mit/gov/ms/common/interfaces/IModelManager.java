/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.interfaces;

import java.io.InputStream;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONObject;

import com.mit.gov.ms.common.modelmanagement.Period;

/**
 * This class performs CRUD (create, read, update, delete) operation of models.
 */
public interface IModelManager {
	
	/**
     * This methods takes model name,  model description, model file and returns the connection details corresponding
     * to that model as JSON.
     * 
     * @param name
     *            This is the name of the model
     * @param description
     *            This is the description of the model
     * @param modelStream
     *            This is the file or input stream of the model
     * @return JSONObject which contains model details(model id, model version, etc)
     */
	public JSONObject storeModel(String name, String description, InputStream modelStream);
	
	/**
     * This methods takes model modelID,  model modelVersionIDand returns the details corresponding
     * model as JSON.
     * 
     * @param modelID
     *            This is the name of the model
     * @param modelVersionID
     *            This is the description of the model
     * @return JSONObject which contains model details (model name, model description, etc)
     */
	public JSONObject getModelInfo(String modelID, String modelVersionID);
	
	/**
     * This methods returns the list of models and their details present in storage layer
     * as a JSON.
     * @return JSONObject which contains model details(model id, model version, etc)
     */
	public JSONObject fetchModels();
	
	/**
     * This methods takes model id,  range, limit, offset and returns the connection details corresponding
     * to that model as JSON.
     * 
     * @param getModelByDate
     *            This is the name of the model
     * @param description
     *            This is the description of the model
     * @param modelStream
     *            This is the file or input stream of the model
     * @return JSONObject which contains model details(model id, model version, etc)
     */
	public JSONObject getModelByDate(String modelID, Period range, int limit, int offset); //add period class to get the range or asof param
	
	/**
     * This methods takes model model id,  model Version ID and returns the model as a input stream.
     * 
     * @param modelID
     *            This is the model id of the model
     * @param modelVersionID
     *            This is the version of the model
     * @return JSONObject which contains model details(model id, model version, etc)
     */
	public InputStream getModelbyVersion(String modelID, String modelVersionID);
	
	/**
     * This methods takes model name,  model description, model file and returns the connection details corresponding
     * to that model as JSON.
     * 
     * @param name
     *            This is the name of the model
     * @param description
     *            This is the description of the model
     * @param modelStream
     *            This is the file or input stream of the model
     * @return JSONObject which contains model details(model id, model version, etc)
     */
	public JSONObject updateModel(String modelID, JSONArray updateInfo, InputStream modelStream);
	
	/**
     * This methods takes model id and deletes the corresponding model stored in storage layer,
     * and return details of deleted model as JSONObect.
     * to that model as JSON.
     * 
     * @param modelID
     *            This is the model id of the model
     */
	public JSONObject delete(String modelID);

}
