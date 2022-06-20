/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

public enum FeatureEnum {
    
    /**
     * Suggested Reference Data
     */
    REF_DATA,
    /**
     * Domain Data fingerprint
     */
    CUSTOM_CLASS(1, "Suggest Custom DataClasses"),
    /**
     * Suggest Data Rules (Automation Rules)
     */
    AUTOMATIONRULES(5, "Suggest Automation Rules"),
    /**
     * For Unit Test
     */
    UNIT_TEST
    ;
    
    private String featureLongName = name();

    /**
     * If feature is enabled at container level using INSIGHTS_FEATURES environment variable
     * which is passed to app using property insights.features 
     * (See docker-entrypoint.sh and InsightsConfiguration.enableFeatures() method)
     */
    private boolean available=false;
    
    /**
     * If feature is enabled or disabled(default) (paused=disabled) 
     */
    private boolean paused=true;
    private boolean inProgress = false;
    private boolean isAborted = false;
    private String enabledByUser = null;
    private String enabledByUserName = null;
    private String enabledByKey = null;
    private Date enabledTime = null;
    private String enabledByBasicAuthToken = null;
    private boolean useIsadminUser = false;
    
    public boolean useIsadminUser() {
        return useIsadminUser;
    }
    
    public void setUseIsadminUser() {
        useIsadminUser = true;
    }
    
    public String getIISBasicAuthToken() {
        return useIsadminUser? InsightsConfiguration.getInstance().getIISBasicAuthToken(): enabledByBasicAuthToken;
    }
    
    /**
     * Last execution time for the feature
     */
    private Date _LAST_EXECUTION_TIME = null;
    
    private Date _LAST_COMPLETION_TIME = null;
    
    private final Logger LOGGER = LoggerFactory.getLogger(FeatureEnum.class);
    
    /**
     * Execution period for the feature in hours (default 24 hours)
     */
    private final int DEFAULT_EXECUTION_PERIOD = 24; // hours
    private int _EXECUTION_PERIOD = DEFAULT_EXECUTION_PERIOD; // hours
    /**
     * Count/number of execution 
     */
    private int _EXECUTION_COUNT = 0;
    /**
     * Max Count/number of execution (default=0 for infinite)
     */
    private int _MAX_EXECUTION_COUNT = 0;
    /**
     * The time period for ManagedScheduledExecutorService to initiate execution thread for this feature.
     * Execution thread for a feature will wake up every <FEATURE_CHECK_SCHEDULE_PERIOD> minute and check
     * if feature is enabled and if last execution time has passed <EXECUTION_PERIOD> hour.
     * Note :- Every time the feature is enabled lastExecutionTime is set to null, so that a 
     */
    private int FEATURE_CHECK_SCHEDULE_PERIOD = 60; //minutes
    private Date lastLogTime = null;
    
    FeatureEnum() {
        available = InsightsConfiguration.getInstance().isFeatureAvailable(name());
        if (available) {
//            JSONObject featureJson = InsightsMetaDataStore.getInstance().get(Constants.FEATURES, name());
//            JSONObject thisFeatureJson = null;
//            LOGGER.info(featureJson.toString());
            // Get enable status from config
            paused = !InsightsConfiguration.getInstance().isFeatureEnabled(name());
            if (!paused) { // Enabled by config(/default)
                useIsadminUser = true; // use config(/default) user creds (isadmin)
            }
//            try {
//                if (featureJson.has(Constants.FEATURES) && featureJson.getJSONObject(Constants.FEATURES).has(name())) {
//                    thisFeatureJson = featureJson.getJSONObject(Constants.FEATURES).getJSONObject(name());
//                    // Override enable status from zookeeper
//                    //paused = thisFeatureJson.has(Constants.ENABLE) ? !thisFeatureJson.optBoolean(Constants.ENABLE, paused) : paused;
//                    boolean savedStatus = thisFeatureJson.has(Constants.ENABLE) ? thisFeatureJson.optBoolean(Constants.ENABLE, false) : false;
//                    if (paused) { // Disabled by config(/default)
//                        if (savedStatus) { // Saved Status is "enabled" 
//                            // State was deliberately enabled by user and set to use "Enabler" user creds
//                            // But, Server restarted/rebooted before job got "finished" and(/or) disabled" by the enabler.
//                            isAborted = true;
//                        }
//                    } else { // Enabled by config(/default)
//                        if (thisFeatureJson.has(Constants.ENABLE) && !savedStatus) { // Saved Status is "disabled"
//                            paused = true; // State was deliberately disabled by user.
//                        }
//                    }
//                    _EXECUTION_PERIOD = thisFeatureJson.has(Constants.EXECUTION_PERIOD_HOUR)
//                            ? thisFeatureJson.optInt(Constants.EXECUTION_PERIOD_HOUR, DEFAULT_EXECUTION_PERIOD)
//                            : DEFAULT_EXECUTION_PERIOD;
//                    _EXECUTION_COUNT = thisFeatureJson.has(Constants.EXECUTION_COUNT)
//                            ? thisFeatureJson.optInt(Constants.EXECUTION_COUNT, 0) : 0;
//                    _MAX_EXECUTION_COUNT = thisFeatureJson.has(Constants.MAX_EXECUTION_COUNT)
//                            ? thisFeatureJson.optInt(Constants.MAX_EXECUTION_COUNT, 0) : 0;
//                    long last_execution_time = thisFeatureJson.has(Constants.LAST_EXECUTION_TIME)
//                            ? thisFeatureJson.optLong(Constants.LAST_EXECUTION_TIME, 0) : 0;
//                    _LAST_EXECUTION_TIME = (last_execution_time > 0) ? new Date(last_execution_time) : null;
//                    long last_completion_time = thisFeatureJson.has(Constants.LAST_COMPLETION_TIME)
//                            ? thisFeatureJson.optLong(Constants.LAST_COMPLETION_TIME, 0) : 0;
//                    _LAST_COMPLETION_TIME = (last_completion_time > 0) ? new Date(last_completion_time) : null;
//                }
//            } catch (JSONException e1) {
//                LOGGER.warn("Error getting Feature state from zookeeper for " + name());
//            }
            if (isAborted) {
                disable();
            }
            if (paused) {
                LOGGER.warn("Feature " + name() + " disabled");
            } else {
                LOGGER.info("Feature " + name() + " is enabled");
            }
        }
    }
    
    FeatureEnum(int featureCheckSchedulePeriod) {
        this();
        FEATURE_CHECK_SCHEDULE_PERIOD = featureCheckSchedulePeriod;
    }
    
    FeatureEnum(int featureCheckSchedulePeriod, String featureLongName) {
        this(featureCheckSchedulePeriod);
        this.featureLongName = featureLongName;
    }
    
    public String getFeatureLongName() {
        return featureLongName;
    }
    
    public String getEnabledByUser() {
        return enabledByUser;
    }

    public String getEnabledByUserName() {
        return enabledByUserName;
    }

    public String getEnabledByKey() {
        return enabledByKey;
    }

    public void setEnabledByUserCreds(String user, String name, String secret) {
        this.enabledByUser = user;
        this.enabledByUserName = name;
        this.enabledByKey = secret;
        this.enabledByBasicAuthToken = new String(Base64.getEncoder().encode((user + ":" + secret).getBytes()), StandardCharsets.UTF_8);
    }

    public int getFeatureCheckSchedulePeriod() {
        return FEATURE_CHECK_SCHEDULE_PERIOD;
    }
    
    public Date getLastExecutionTime() {
        return _LAST_EXECUTION_TIME;
    }

    public String getLastExecutionTimeString() {
        return ((_LAST_EXECUTION_TIME != null) ? _LAST_EXECUTION_TIME.toString() : null);
    }

    public Date getLastCompletionTime() {
        return _LAST_COMPLETION_TIME;
    }

    public String getLastCompletionTimeString() {
        return ((_LAST_COMPLETION_TIME != null) ? _LAST_COMPLETION_TIME.toString() : null);
    }

    public String getEnabledTimeString() {
        return ((enabledTime != null) ? enabledTime.toString() : null);
    }

    /**
     * If feature is GAed/available for use (deafult not available)
     * Use insights_service.properties features to enable a property (, separated)
     * @return true/false
     */
    public boolean isFeatureAvailable() {
        return available;
    }
    
    public void setAvailable() {
        available=true;
    }
    
    public void setUnAvailable() {
        available=false;
    }
    
    /**
     * If a feature is available but paused/disabled
     * Use endpoint to pause / play
     * @return true/false
     */
    public boolean isEnabled() {
        return !paused;
    }
    
    public JSONObject getProperties() throws JSONException {
        JSONObject retJson = new JSONObject().put(Constants.FEATURE, name().toLowerCase())
                .put(Constants.ENABLE, !paused).put(Constants.EXECUTION_PERIOD_HOUR, _EXECUTION_PERIOD)
                .put(Constants.EXECUTION_COUNT, _EXECUTION_COUNT).put(Constants.MAX_EXECUTION_COUNT, _MAX_EXECUTION_COUNT)
                .put(Constants.ENABLED_BY, getEnabledByUserName())
                .put(Constants.ENABLED_AT, getEnabledTimeString())
                .put(Constants.LAST_EXECUTION_TIME, getLastExecutionTimeString())
                .put(Constants.LAST_COMPLETION_TIME, getLastCompletionTimeString());
        if (isAborted) {
            retJson.put(Constants.MESSAGE, "Service Restarted, Please Re-Enable the feature");
        }
        return retJson;
    }
    
    public JSONObject getRunStatus() throws JSONException {
        if (paused) {
            if (!isAborted) {
                return new JSONObject().put(Constants.MESSAGE, Constants.ANALYSIS_NOT_STARTED)
                    .put(Constants.FEATURE, getFeatureLongName()).put(Constants.ANALYSING, false);
            } else {
                return new JSONObject().put(Constants.MESSAGE, Constants.ANALYSIS_ABORTED)
                        .put(Constants.FEATURE, getFeatureLongName()).put(Constants.ANALYSING, false);
            }
        } else if (inProgress) {
        		if (this.name().equals("CUSTOM_CLASS")) {
        			return new JSONObject().put(Constants.MESSAGE, Constants.ANALYSIS_INITIATED_SCC).put(Constants.FEATURE, getFeatureLongName())
                            .put(Constants.ANALYSING, true).put(Constants.STARTED_BY, getEnabledByUserName())
                            .put(Constants.STARTED_AT, getEnabledTimeString());
        		} else if (this.name().equals(Constants.AUTOMATIONRULES)) {
        			return new JSONObject().put(Constants.MESSAGE, Constants.ANALYSIS_INITIATED_SAR).put(Constants.FEATURE, getFeatureLongName())
                            .put(Constants.ANALYSING, true).put(Constants.STARTED_BY, getEnabledByUserName())
                            .put(Constants.STARTED_AT, getEnabledTimeString());
        		} else 
        			return new JSONObject().put(Constants.MESSAGE, Constants.ANALYSIS_INITIATED).put(Constants.FEATURE, getFeatureLongName())
                            .put(Constants.ANALYSING, true).put(Constants.STARTED_BY, getEnabledByUserName())
                            .put(Constants.STARTED_AT, getEnabledTimeString());
            
        } else {
	        	if (this.name().equals("CUSTOM_CLASS")) {
	        		return new JSONObject().put(Constants.MESSAGE, Constants.ANALYSIS_SCHEDULED_SCC).put(Constants.FEATURE, getFeatureLongName())
	                        .put(Constants.ANALYSING, true).put(Constants.STARTED_BY, getEnabledByUserName())
	                        .put(Constants.STARTED_AT, getEnabledTimeString());
	        	} else if (this.name().equals(Constants.AUTOMATIONRULES)) {
	        		return new JSONObject().put(Constants.MESSAGE, Constants.ANALYSIS_SCHEDULED_SAR).put(Constants.FEATURE, getFeatureLongName())
	                        .put(Constants.ANALYSING, true).put(Constants.STARTED_BY, getEnabledByUserName())
	                        .put(Constants.STARTED_AT, getEnabledTimeString());
	        	} else {
	        		return new JSONObject().put(Constants.MESSAGE, Constants.ANALYSIS_SCHEDULED).put(Constants.FEATURE, getFeatureLongName())
	                        .put(Constants.ANALYSING, true).put(Constants.STARTED_BY, getEnabledByUserName())
	                        .put(Constants.STARTED_AT, getEnabledTimeString());
	        	}
        }
    }
    
    public void disable() {
        paused=true;
        this.enabledByUser = null;
        this.enabledByUserName = null;
        this.enabledByKey = null;
        this.enabledByBasicAuthToken = null;
//        try {
//            InsightsMetaDataStore.getInstance().save(Constants.FEATURES, name(),
//                    new JSONObject().put(Constants.ENABLE, false).put(Constants.EXECUTION_PERIOD_HOUR, _EXECUTION_PERIOD)
//                            .put(Constants.EXECUTION_COUNT, _EXECUTION_COUNT)
//                            .put(Constants.MAX_EXECUTION_COUNT, _MAX_EXECUTION_COUNT)
//                            .put(Constants.LAST_EXECUTION_TIME, ((_LAST_EXECUTION_TIME != null) ? _LAST_EXECUTION_TIME.getTime() : _LAST_EXECUTION_TIME))
//                            .put(Constants.LAST_COMPLETION_TIME, ((_LAST_COMPLETION_TIME != null) ? _LAST_COMPLETION_TIME.getTime() : _LAST_COMPLETION_TIME))
//                            );
//            LOGGER.warn("Feature " + name() + " disabled");
//        } catch (JSONException e) {
//            LOGGER.warn("Feature " + name() + "disabled but state could not be persisted");
//        }
    }
    
    public void enable(int execution_period_hour, int max_execution_count) {
        _EXECUTION_PERIOD = execution_period_hour > 0 ? execution_period_hour : DEFAULT_EXECUTION_PERIOD;
        _MAX_EXECUTION_COUNT = max_execution_count > 0 ? max_execution_count : 0;
        if (_MAX_EXECUTION_COUNT > 0 ) {
            _EXECUTION_COUNT = 0;
        }
        enable();
    }
    
    private void enable() {
        _LAST_EXECUTION_TIME = null;
        _LAST_COMPLETION_TIME = null;
        enabledTime = new Date();
        paused=false;
        isAborted=false;
//        try {
//            InsightsMetaDataStore.getInstance().save(Constants.FEATURES, name(),
//                    new JSONObject().put(Constants.ENABLE, true).put(Constants.EXECUTION_PERIOD_HOUR, _EXECUTION_PERIOD)
//                            .put(Constants.EXECUTION_COUNT, _EXECUTION_COUNT)
//                            .put(Constants.MAX_EXECUTION_COUNT, _MAX_EXECUTION_COUNT)
//                            .put(Constants.LAST_EXECUTION_TIME, _LAST_EXECUTION_TIME)
//                            .put(Constants.LAST_COMPLETION_TIME, _LAST_COMPLETION_TIME));
//            LOGGER.info("Feature " + name() + " enabled");
//        } catch (JSONException e) {
//            LOGGER.warn("Feature " + name() + "enabled but state could not be persisted");
//        }
    }
    
    public void finalizeExecution() {
        if (!isFeatureAvailable()) {
            return;
        }
        inProgress = false;
        _LAST_COMPLETION_TIME = new Date();
        if (_EXECUTION_COUNT < Integer.MAX_VALUE) {
            _EXECUTION_COUNT++;
        }
        // If already run max number of times disable and return false
        if (_MAX_EXECUTION_COUNT > 0 && _EXECUTION_COUNT >= _MAX_EXECUTION_COUNT) {
            disable();
        }        
    }
    
    public boolean initiatExecution() {
        if (!isFeatureAvailable()) {
            return false;
        }
        if (inProgress) {
            return false;
        }
        Date now = new Date();
        // If disabled , no need to execute this feature
        if (paused) {
            if (lastLogTime == null || (now.getTime() - lastLogTime.getTime() >= TimeUnit.HOURS.toMillis(1))) {
                lastLogTime = now;
                // log once every hour only, if disabled
                LOGGER.warn("Feature " + name() + " is disabled");
            }
            return false;
        }
        if ((_LAST_EXECUTION_TIME == null
                || (now.getTime() - _LAST_EXECUTION_TIME.getTime() >= TimeUnit.HOURS.toMillis(_EXECUTION_PERIOD)))
                && (_MAX_EXECUTION_COUNT == 0 || _MAX_EXECUTION_COUNT > _EXECUTION_COUNT)) {
            _LAST_EXECUTION_TIME = now;
            _LAST_COMPLETION_TIME = null;
            // Execute every EXECUTION_PERIOD hours
//            try {
//                InsightsMetaDataStore.getInstance().save(Constants.FEATURES, name(),
//                        new JSONObject().put(Constants.ENABLE, true).put(Constants.EXECUTION_PERIOD_HOUR, _EXECUTION_PERIOD)
//                                .put(Constants.EXECUTION_COUNT, _EXECUTION_COUNT)
//                                .put(Constants.MAX_EXECUTION_COUNT, _MAX_EXECUTION_COUNT)
//                                .put(Constants.LAST_EXECUTION_TIME, _LAST_EXECUTION_TIME.getTime())
//                                .put(Constants.LAST_COMPLETION_TIME, _LAST_COMPLETION_TIME));
//                LOGGER.info("Feature " + name() + " is starting execution now");
//            } catch (JSONException e) {
//                LOGGER.warn("Feature " + name() + " is starting execution now but ExecutionTime could not be persisted");
//            }
            inProgress = true;
            return true;
        }
        if ((lastLogTime == null || (now.getTime() - lastLogTime.getTime() >= TimeUnit.HOURS.toMillis(1)))
                && (_MAX_EXECUTION_COUNT == 0 || _MAX_EXECUTION_COUNT > _EXECUTION_COUNT)) {
            lastLogTime = now;
            // log once every hour only if enabled
            LOGGER.warn(
                    "Feature " + name() + " will start next execution in "
                            + ((_EXECUTION_PERIOD * 60)
                                    - TimeUnit.MILLISECONDS.toMinutes(now.getTime() - _LAST_EXECUTION_TIME.getTime()))
                            + " minutes");
        }
        return false;
    }
}
