/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.gov.ms.common.apriori.DataIterator;
import com.mit.gov.ms.common.apriori.NamedItem;

import de.mrapp.apriori.Apriori;
import de.mrapp.apriori.Output;
import de.mrapp.apriori.Transaction;
import de.mrapp.apriori.Apriori.Configuration;

public class CommonUtils {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);
    
    public enum AuthenticationScheme {
        AUTH_SCHEME_BASIC,
        AUTH_SCHEME_JWT
    }
    
    public static Response secureResponse(JSONObject result, Status status) {
        return secureResponse(AuthenticationScheme.AUTH_SCHEME_BASIC, result, status);
    }
    
    public static Response secureResponse(AuthenticationScheme authScheme, JSONObject result, Status status) {
        ResponseBuilder rsb = Response.status(status)
                .header("X-Content-Type-Options", "nosniff")
                .header("X-XSS-Protection", "1; mode=block")
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .header("Pragma", "no-cache");
        if (result!=null && !result.isEmpty()) {
            rsb = rsb.entity(result);
        }
        if (status.equals(Status.UNAUTHORIZED)) {
            if (authScheme.equals(AuthenticationScheme.AUTH_SCHEME_BASIC)) {
                rsb.header("WWW-Authenticate", "Basic realm=\"IBM Information Server\"");
            } else {
                rsb.header("WWW-Authenticate", "Bearer realm=\"IBM Information Server\"");
            }
        }
        return rsb.build();
    
    }
    
    public static JSONObject getJsonNotAvailable(FeatureEnum feature) throws JSONException {
        return new JSONObject().put(Constants.STATUS, Constants.NOT_AVAILABLE).put(Constants.FEATURE, feature.name().toLowerCase());
    }
    
    public static Response enableDisableFeature(FeatureEnum feature, JSONObject payload) {
        try {
            if (payload == null || !payload.has(Constants.ENABLE)) {
                LOGGER.error("Invalid payload for enableDisableFeature");
                return secureResponse((payload == null ? new JSONObject() : payload).put(Constants.STATUS, "BAD_REQUEST"), Status.BAD_REQUEST);
            }

            if (payload.optBoolean(Constants.ENABLE)) {
                int execution_period_hours = 0;
                int max_execution_count = 0;
                if (payload.has(Constants.EXECUTION_INTERVAL_HOURS)) {
                    execution_period_hours = payload.optInt(Constants.EXECUTION_INTERVAL_HOURS, 0);
                }
                if (payload.has(Constants.ONE_TIME_EXECUTION)) {
                    if (payload.optBoolean(Constants.ONE_TIME_EXECUTION, false)) {
                        max_execution_count = 1;
                    }
                }
                if (!feature.isEnabled()) {
                    feature.enable(execution_period_hours, max_execution_count);
                    return secureResponse(feature.getProperties(), Status.OK);
                } else {
                    return secureResponse(
                            new JSONObject().put(Constants.ERROR, "Run already initiated by " + feature.getEnabledByUserName() + " and is in progress")
                                    .put(Constants.STATUS, feature.getProperties()),
                            Status.CONFLICT);
                }
            } else if (payload.getString(Constants.ENABLE).equalsIgnoreCase("false")) {
                feature.disable();
                return secureResponse(feature.getProperties(), Status.OK);
            } else {
                LOGGER.error("Invalid payload for enableDisableFeature");
                return secureResponse(payload.put(Constants.STATUS, "BAD_REQUEST"), Status.BAD_REQUEST);                
            }
        } catch (JSONException e) {
            LOGGER.error("Error while enableDisableFeature", e);
            return secureResponse(new JSONObject(), Status.INTERNAL_SERVER_ERROR );
        }
    }
    
    public static Response enableForOneRun(FeatureEnum feature, String projectRid) {
        try {
            if (!feature.isEnabled()) {
                int execution_period_hours = 0;
                int max_execution_count = 1;
//                if(projectRid != null) {
//                		XMetaUtils.setWorkspace_rid(projectRid);
//                }
                feature.enable(execution_period_hours, max_execution_count);
                return secureResponse(feature.getRunStatus(), Status.OK);
            } else {
                return secureResponse(
                        new JSONObject().put(Constants.ERROR, "Run already initiated by " + feature.getEnabledByUserName() + " and is in progress")
                                .put(Constants.STATUS, feature.getRunStatus()),
                        Status.CONFLICT);
            }
        } catch (JSONException e) {
            LOGGER.error("Error while enableForOnce", e);
            return secureResponse(new JSONObject(), Status.INTERNAL_SERVER_ERROR);
        }
    }
        
	public static Output<NamedItem> AprioriResult(JSONArray transactions) {
		return AprioriResult(transactions, Constants.MINIMUM_CONFIDENCE);
	}
    
    public static Output<NamedItem> AprioriResult(JSONArray transactions, double min_confidance) {
		Output<NamedItem> output = null;
		int tranLength = transactions.length();
		double minSupport = 1;
		if (tranLength >= 3) {
			minSupport = (double) 3.0 / transactions.length();
		} else {
			LOGGER.info("insufficient data to generate suggestion.");
			return null;
		}

		Apriori<NamedItem> apriori = new Apriori.Builder<NamedItem>(minSupport).create();
		Configuration configuration = apriori.getConfiguration();
		configuration.setMinConfidence(min_confidance);
		configuration.setGenerateRules(true);

		// configuration.
		Iterable<Transaction<NamedItem>> iterable = () -> new DataIterator(transactions);
		output = apriori.execute(iterable);
		return output;
	}
    
    public static String getTraceAsString(Throwable throwable)
    {
        final StringWriter sWriter = new StringWriter();
        final PrintWriter pWriter = new PrintWriter(sWriter);
        try {
            throwable.printStackTrace(pWriter);
            return sWriter.toString();
        }
        catch (final Exception e) {
            // Don't fail if we're unable to get a stack trace
            return "[<Failed to get Throwable stack trace: " + e.getMessage() + ">]";
        }
        finally {
            try {
                pWriter.close();
                sWriter.close();
            }
            catch (final Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }
    
}
