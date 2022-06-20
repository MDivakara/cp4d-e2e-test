/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.modelmanagement;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.activation.DataHandler;
import javax.ws.rs.core.Response.Status;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.slf4j.LoggerFactory;

import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import com.ibm.websphere.jaxrs20.multipart.IMultipartBody;
import com.mit.gov.ms.common.Constants;
import com.mit.gov.ms.common.InsightsConfiguration;
import com.mit.gov.ms.common.InsightsException;
import com.mit.gov.ms.common.InsightsMetaDataStore;
import com.mit.gov.ms.common.interfaces.IInsightsMetaDataStore;
import com.mit.gov.ms.common.interfaces.IModelManager;
import com.mit.gov.ms.common.security.SessionManager;
import com.mit.gov.ms.common.storage.CatalogsSQLRSFormater;
import com.mit.gov.ms.common.storage.InsightsSQLQueries;
import com.mit.gov.ms.common.storage.InsightsSQLRSFormater;
import com.mit.gov.ms.common.storage.InsightsTables;
import com.mit.gov.ms.store.StorageConstants;
import com.mit.gov.ms.store.StorageException;
import com.mit.gov.ms.store.db.sql.SQLDBHelper;

public class InsightsModelManager implements IModelManager {

    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(InsightsModelManager.class);

    public enum InsightsModelTypes {
        JSON, JAVA_CLASS, PYTHON
    }

    protected IInsightsMetaDataStore insightsStore = null;

    private String schema_name = InsightsConfiguration.getInstance().getInsightsSchemaName();

    private String tenant_id = "9999";

    private InsightsModelTypes default_model_type = InsightsModelTypes.JSON;

    public InsightsModelManager() throws InsightsException {
        super();
        this.insightsStore = InsightsMetaDataStore.getInstance();
        if (SessionManager.getCurrentSession() != null && SessionManager.getCurrentSession().getTenantId() != null) {
            tenant_id = SessionManager.getCurrentSession().getTenantId();
        }
    }

    public InsightsModelManager(String tenant_id) throws InsightsException {
        super();
        this.insightsStore = InsightsMetaDataStore.getInstance();
        this.tenant_id = tenant_id;
    }

    @Override
    public JSONObject storeModel(String name, String description, InputStream modelStream) {

        JSONObject result = new JSONObject();

        // store model information in models table
        UUID model_id = UUID.randomUUID();
        UUID model_version_id = UUID.randomUUID();
        JSONObject model = new JSONObject();
        JSONObject model_store = new JSONObject();
        String user = SessionManager.getCurrentSession() != null ? SessionManager.getCurrentSession().getUserName() : Constants.SYSTEM_USER;
        try {
            model.put(Constants.TENANT_ID, tenant_id);
            model.put(Constants.MODEL_ID, model_id.toString());
            model.put(Constants.NAME, name);
            model.put(Constants.DESCRIPTION, description);
            model.put(Constants.MODEL_TYPE, default_model_type.name());
            model.put(Constants.MODEL_VERSION_ID, model_version_id.toString());
            model.put(Constants.MODEL_METADATA, model_id.toString());
            model.put(Constants.CREATED_BY, user);
            model.put(Constants.CREATED_AT, StorageConstants.CURRENT_TIMESTAMP);
            model.put(Constants.UPDATED_BY, user);
            model.put(Constants.UPDATED_AT, StorageConstants.CURRENT_TIMESTAMP);

            LOGGER.info("creating a model" + model.toString());
            insightsStore.save(InsightsTables.GI_MODELS.name(), model_id.toString(), model);
            LOGGER.info("Done - stored model - " + model.toString());
        } catch (JSONException | InsightsException e) {
            LOGGER.error("Exception while create task - model_id=" + model_id, e);
            try {
                result.put(Constants.STATUS_CODE, Status.INTERNAL_SERVER_ERROR).put(Constants.ERROR_MESSAGE, e);
            } catch (JSONException e1) {
                LOGGER.error("Exception while create task - model_id=" + model_id, e1);
            }
        }
        // store the model in MODEL_STORE table
        try {
            model_store.put(Constants.TENANT_ID, tenant_id);
            model_store.put(Constants.MODEL_ID, model_id.toString());
            model_store.put(Constants.MODEL_VERSION_ID, model_version_id.toString());
            model_store.put(Constants.MODEL, SQLDBHelper.encodedValueForBlob(modelStream));

            LOGGER.info("creating a model" + model.toString());
            insightsStore.save(InsightsTables.GI_MODEL_STORE.name(), model_id.toString(), model_store);
            LOGGER.info("Done - stored model - " + model.toString());
            result.put(Constants.STATUS_CODE, 201).put(Constants.STATUS_MESSAGE, "succesfully stored the model").put(Constants.MODEL_ID, model_id.toString())
                    .put(Constants.MODEL_VERSION_ID, model_version_id.toString());
        } catch (JSONException | InsightsException | StorageException e) {
            LOGGER.error("Exception while create task - model_id=" + model_id, e);
            try {
                result.put(Constants.STATUS_CODE, Status.INTERNAL_SERVER_ERROR).put(Constants.ERROR_MESSAGE, e);
            } catch (JSONException e1) {
                LOGGER.error("Exception while create task - model_id=" + model_id, e);
            }
        }
        return result;
    }

    @Override
    public JSONObject getModelInfo(String modelID, String modelVersionID) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JSONObject fetchModels() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JSONObject getModelByDate(String modelID, Period range, int limit, int offset) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getModelbyVersion(String modelID, String modelVersionID) {

        InputStream modelInputStream = null;
        String queryIdentifier = InsightsSQLQueries.GET_ML_MODEL_BY_CURRENT_VERSION;
        String[] formats = { schema_name, schema_name };
        JSONArray param_array = new JSONArray();
        try {
            param_array.put(SQLDBHelper.createParamString(tenant_id));
            param_array.put(SQLDBHelper.createParamString(modelID));
            if (!modelVersionID.equalsIgnoreCase("current")) {
                // fetch the latest version of the model
                queryIdentifier = InsightsSQLQueries.GET_ML_MODEL_BY_VERSION;
                param_array.put(SQLDBHelper.createParamString(modelVersionID));
            }
            return insightsStore.getInputStream(InsightsTables.GI_MODEL_STORE.name(), queryIdentifier, formats, param_array,
                    InsightsSQLRSFormater.FORMAT_MODEL_STREAM);

        } catch (RuntimeException | JSONException | InsightsException e) {
            LOGGER.info("Exception while getModelByVersionId - model_id=" + modelID, e);
        }
        return modelInputStream;
    }

    @Override
    public JSONObject updateModel(String model_id, JSONArray updateInfos, InputStream modelStream) {

        JSONObject result = new JSONObject();
        int revNo = getRevisionNobyModleID(model_id, tenant_id);
        if (revNo == -1) {
            try {
                return new JSONObject().put(Constants.ERROR_MESSAGE, "Invalid model_id or update Information").put(Constants.STATUS_CODE, 400);
            } catch (JSONException e) {
                LOGGER.error("JSON Exception", e);
            }
        }

        try {
            for (int i = 0; updateInfos != null && i < updateInfos.size(); i++) {
                JSONObject updateInfo = updateInfos.getJSONObject(i);
                // update record
                JSONObject condition = new JSONObject();
                JSONObject updateValues = new JSONObject();

                if (!updateInfo.isEmpty()) {
                    String op = "", path = "", value = "";
                    if (updateInfo.containsKey("operation")) {
                        op = updateInfo.getString("operation");
                    }
                    if (updateInfo.containsKey("path")) {
                        path = updateInfo.getString("path");
                    }
                    if (updateInfo.containsKey("value")) {
                        value = updateInfo.getString("value");
                    }
                    if (op.equalsIgnoreCase("replace") || op.equalsIgnoreCase("remove")) {
                        if (path.equalsIgnoreCase(Constants.NAME)) {
                            // update name
                            value = updateInfo.getString("value");
                            updateValues.put(Constants.NAME, value);

                        } else if (path.equalsIgnoreCase(Constants.DESCRIPTION)) {
                            // update description
                            value = updateInfo.getString("value");
                            updateValues.put(Constants.DESCRIPTION, value);

                        } else if (path.equalsIgnoreCase(Constants.MODEL_TYPE)) {
                            // update model_type
                            value = updateInfo.getString("value");
                            updateValues.put(Constants.MODEL_TYPE, value);

                        } else {
                            // update unsupported path value
                            LOGGER.error("unsupported path value to update" + path);
                            return new JSONObject().put(Constants.ERROR_MESSAGE, "unsupported path value to update" + path).put(Constants.STATUS_CODE, 400);

                        }
                        condition.put(Constants.MODEL_ID, model_id);
                        condition.put(Constants.TENANT_ID, tenant_id);
                        condition.put(Constants.REV_NO, revNo);
                        try {

                            LOGGER.info("updating a model" + updateValues.toString());
                            JSONObject res = insightsStore.update(InsightsTables.GI_MODELS.name(), model_id, updateValues, condition);
                            if (res == null || res.isEmpty() || res.isNull(StorageConstants.SQL_RESULT) || res.getInt(StorageConstants.SQL_RESULT) <= 0) {
                            	LOGGER.error("Error while updating model with model id: " + model_id );
                                return new JSONObject().put(Constants.ERROR_MESSAGE, "Error while updating model with model id: " + model_id).put(Constants.STATUS_CODE, 500);
                            }
                            LOGGER.info("Done - stored model - " + updateValues.toString());
                            result.put(Constants.STATUS_CODE, 200).put(Constants.STATUS_MESSAGE, "succesfully updated the model").put(Constants.MODEL_ID,
                                    model_id.toString());
                        } catch (JSONException | InsightsException e) {
                            LOGGER.error("Exception while create task - model_id=" + model_id, e);
                            try {
                                result.put(Constants.STATUS_CODE, Status.INTERNAL_SERVER_ERROR).put(Constants.ERROR_MESSAGE, e);
                            } catch (JSONException e1) {
                                LOGGER.error("JSONException while update" + e1);
                                return new JSONObject().put(Constants.ERROR_MESSAGE, "JSONException while update" + e1).put(Constants.STATUS_CODE, 500);
                            }
                        }

                    } else {
                        // return invalid operation
                        LOGGER.error("unsupported operation to update." + op);
                        return new JSONObject().put(Constants.ERROR_MESSAGE, "unsupported operation to update." + op).put(Constants.STATUS_CODE, 400);
                    }

                }
            }
        } catch (JSONException e) {
            LOGGER.error("JSONException while update" + e);
            try {
                return new JSONObject().put(Constants.ERROR_MESSAGE, "JSONException while update" + e).put(Constants.STATUS_CODE, 500);
            } catch (JSONException e1) {
                LOGGER.error("JSONException while update" + e1);
            }
        }
        try {

            if (modelStream != null) {
                JSONObject updateInputStream = new JSONObject();
                updateInputStream.put(Constants.MODEL, SQLDBHelper.encodedValueForBlob(modelStream));
                JSONObject res = insightsStore.update(InsightsTables.GI_MODEL_STORE.name(), model_id, updateInputStream,
                        new JSONObject().put(Constants.MODEL_ID, model_id).put(Constants.REV_NO, revNo).put(Constants.TENANT_ID, tenant_id));
                if (res == null || res.isEmpty() || res.isNull(StorageConstants.SQL_RESULT) || res.getInt(StorageConstants.SQL_RESULT) <= 0) {
                	LOGGER.error("Error while updating model with model id: " + model_id );
                    return new JSONObject().put(Constants.ERROR_MESSAGE, "Error while updating model with model id: " + model_id).put(Constants.STATUS_CODE, 500);
                }
                LOGGER.info("Done - updated model - " + updateInputStream.toString());
                result.put(Constants.STATUS_CODE, 200).put(Constants.STATUS_MESSAGE, "succesfully updated the model").put(Constants.MODEL_ID,
                        model_id.toString());

            }
        } catch (JSONException | StorageException | InsightsException e) {
            LOGGER.error("JSONException while update" + e);
            try {
                return new JSONObject().put(Constants.ERROR_MESSAGE, "JSONException while update" + e).put(Constants.STATUS_CODE, 500);
            } catch (JSONException e1) {
                LOGGER.error("JSONException while update" + e1);
            }
        }

        return result;
    }

    @Override
    public JSONObject delete(String modelID) {
        // deleting model by model_id
        JSONObject result = new JSONObject();
        try {

            JSONObject deleteCondition = new JSONObject().put(Constants.MODEL_ID, modelID).put(Constants.TENANT_ID, tenant_id);
            insightsStore.delete(InsightsTables.GI_MODELS.name(), null, deleteCondition);
            insightsStore.delete(InsightsTables.GI_MODEL_STORE.name(), null, deleteCondition);
            result.put(Constants.MODEL_ID, modelID);
        } catch (RuntimeException | JSONException | InsightsException e) {
            LOGGER.info("Exception while getModelByVersionId - model_id=" + modelID, e);
            return null;
        }
        return result;
    }

    public int getRevisionNobyModleID(String modelID, String tenant_id) {
        // getting model revision by model_id
        int revNo = 0;
        String queryIdentifier = InsightsSQLQueries.GET_ML_REV_NO_BY_MODEL_ID;
        String[] formats = { schema_name, schema_name };
        JSONArray param_array = new JSONArray();

        try {
            param_array.put(SQLDBHelper.createParamString(tenant_id));
            param_array.put(SQLDBHelper.createParamString(modelID));
            JSONObject res = insightsStore.get(InsightsTables.GI_MODEL_STORE.name(), queryIdentifier, formats, param_array,
                    CatalogsSQLRSFormater.FORMAT_DEFAULT_JSON);
            JSONArray resultantArray = res.getJSONObject(StorageConstants.SQL_RESULT).getJSONArray(Constants.ROWS);
            res = resultantArray.getJSONObject(0);
            if (!res.isEmpty() && res.containsKey(Constants.REV_NO)) {
                revNo = res.getInt(Constants.REV_NO);
            } else {
                return -1;
            }

        } catch (RuntimeException | JSONException | InsightsException e) {
            LOGGER.info("Exception while getModel Revision By MId - model_id=" + modelID, e);
        }
        return revNo;
    }

    /**
     * Get InputStream from MultiPartBody attachment
     * 
     * @param multipartBody
     * @return InputStream for attachment
     * @throws GlossaryException
     */
    public static InputStream getInputStreamFromMultipartBody(IMultipartBody multipartBody) {
        List<IAttachment> attachments = multipartBody.getAllAttachments();
        if (attachments == null || attachments.size() > 1) {
            LOGGER.error("must have only one attachment");
            // TODO - cleanup this code
            // throw new GlossaryException(HttpStatus.BAD_REQUEST, "The request
            // upload only one CSV file");
        }
        try {
            for (Iterator<IAttachment> it = attachments.iterator(); it.hasNext();) {
                IAttachment attachment = it.next();
                if (attachment == null) {
                    continue;
                }
                DataHandler dataHandler = attachment.getDataHandler();
                return dataHandler.getInputStream();
            }
        } catch (IOException e) {
            LOGGER.error("Error while reading csv file attachment.", e);

        }
        return null;
    }

}
