/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.wink.json4j.JSON;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.OrderedJSONObject;

import com.mit.gov.ms.common.security.SessionInfo;
import com.mit.gov.ms.common.security.SessionManager;

@Plugin(name = "InsightsLogLayout", category = Node.CATEGORY, elementType = Layout.ELEMENT_TYPE, printObject = true)
public class InsightsLogLayout extends AbstractStringLayout {

    private boolean prettyjson = false; 
    private static final String NONE = "NONE";

    private static final String LOGLEVEL = "loglevel";
    private static final String COMPONENT_ID = "component_ID";
    private static final String SESSION_ID = "session_ID";
    private static final String USER = "user";
    private static final String TRACE_ID = "trace_ID";
    private static final String TRANSACTION_ID = "transaction_ID";
    private static final String TIMESTAMP = "timestamp";
    private static final String METHOD = "method";
    private static final String LINE = "line";
    private static final String CLASS = "class";
    private static final String TENANT = "tenant";
    private static final String MESSAGE = "message_details";
    private static final String EXCEPTION = "exception";
    private static final String PERFORMANCE_FLAG = "perf";
    private static final String errorMessage = "Unable to serialize the objects in the logger";
    private static final String componentID = System.getenv("COMPONENT_ID") != null ? System.getenv("COMPONENT_ID") : "gov-insights";
    private static final String LOGSOURCECRN = "logSourceCRN";
    private static final String SAVESERVICECOPY = "saveServiceCopy";
    private static final String logdnaSourceCrn = System.getenv("LOGDNA_LOG_SOURCE_CRN");
    private static final String logdnaServiceCopy = System.getenv("LOGDNA_SAVE_SERVICE_COPY");
    
    protected InsightsLogLayout(boolean prettyjson, Charset charset) {
        super(charset);
        this.prettyjson = prettyjson;
    }

    @Override
    public String toSerializable(LogEvent event) {
        OrderedJSONObject jsonParam = new OrderedJSONObject();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            final SessionInfo session = SessionManager.getCurrentSession();
            final String tid = (session != null ? session.getTraceId() : NONE);
            final String sessionid = (session != null ? session.getHttpSessionId() : NONE);
            final String userId = (session != null ? session.getUserId() : NONE);
            final String tenant = NONE; //session != null ? session.getEffectiveBSSAccountId() : NONE;

            final String txid = (session != null && session.getHeaderParams() != null
                    && session.getHeaderParams().containsKey("X-Global-Transaction-Id"))
                            ? (String) session.getHeaderParams().get("X-Global-Transaction-Id") : NONE;
            jsonParam.put(CLASS, event.getSource().getClassName());
            jsonParam.put(METHOD, event.getSource().getMethodName());
            jsonParam.put(LINE, event.getSource().getLineNumber());
            jsonParam.put(COMPONENT_ID, componentID);
            jsonParam.put(USER, userId);
            jsonParam.put(TRACE_ID, tid);
            jsonParam.put(TRANSACTION_ID, txid);
            jsonParam.put(TIMESTAMP, df.format(new Date(event.getTimeMillis())));
            jsonParam.put(TENANT, tenant);
            jsonParam.put(SESSION_ID, sessionid);
            jsonParam.put(PERFORMANCE_FLAG, "false");
            // for logdna implementation
            if (logdnaSourceCrn != null) {
                jsonParam.put(LOGSOURCECRN, logdnaSourceCrn);
            }
            if (logdnaServiceCopy != null) {
                jsonParam.put(SAVESERVICECOPY, Boolean.parseBoolean(logdnaServiceCopy));
            }

            jsonParam.put(LOGLEVEL, event.getLevel().name());
            String msg = event.getMessage().getFormattedMessage();
            if (msg.startsWith("{")) {
                try {
                    jsonParam.put(MESSAGE, JSON.parse(msg));
                } catch (JSONException je) {
                    jsonParam.put(MESSAGE, msg);
                }
            } else {
                jsonParam.put(MESSAGE, msg);
            }
            if ( event.getThrown() != null) { 
                jsonParam.put(EXCEPTION, CommonUtils.getTraceAsString(event.getThrown()));
            }
        } catch (JSONException e) {
            LOGGER.error(errorMessage, e);
        }
        if (prettyjson) {
            try {
                return String.format("%s%n",jsonParam.toString(4));
            } catch (JSONException e) {
                LOGGER.error("can not format json", e);
            }
        }
        return String.format("%s%n",jsonParam.toString());
    }

    @PluginFactory
    public static InsightsLogLayout createLayout(
            @PluginAttribute(value = "prettyjson", defaultBoolean = false)  boolean prettyjson,
            @PluginAttribute(value = "charset", defaultString = "UTF-8") Charset charset) {
        return new InsightsLogLayout(prettyjson, charset);
    }

}