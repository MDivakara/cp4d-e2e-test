/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.gov.ms.store.StorageConstants;

public class InsightsConfiguration {
    
    public enum InsightsMetaStoreType {
        ZOOKEEPER,
        FILE,
        CLOUDANT,
        SQLDB
    }
    
    public enum CatalogInteractorType {
    	IGC,
    	WKC
    }
    private CatalogInteractorType defaultCatalogInteractor = CatalogInteractorType.IGC;
    
    private InsightsMetaStoreType defaultInsightsMetaStoreType = InsightsMetaStoreType.SQLDB;
    
    public static final String INSIGHTS_META_STORE_TYPE = "insights.md.store.type";
    
    public static final String PROP_NOAUTH = "noauth";
    
    public static final String PROP_FEATURES = "insights.features";

    public static final String ZK_URL = "insights.zk.host";

    public static final String SOLR_URL = "solr_url";

    public static final String IIS_BASE_URL = "iis.base.url";
    
    public static final String UG_BASE_URL = "ug.base.url";
    
    public static final String UG_BASE_URL_V3 = "ug.base.url.v3";
    
    public static final String IIS_USERNAME_SECRET = "iis.username.secret";

    public static final String IIS_PASSWORD_SECRET = "iis.password.secret";
    
    public static final String DB_TYPE = "insights.db.type";
    
    public static final String DB_USER = "insights.db.user";

    public static final String DATA_COLLECTION = "data_collection";

    private static final Logger LOG = LoggerFactory.getLogger(InsightsConfiguration.class);

    private static final String CONFIGURATION_FILE_LOCATION = "insights_service.properties";

    private static final Object LOCK = new Object();

    private static final String ENV_VAR_PREFIX = "INSIGHTS_SERVICE_";
    
    public static final String CLOUDANT_ACCOUNT = "cloudant.account";
    
    public static final String CLOUDANT_USER = "cloudant.user";
    
    public static final String CLOUDANT_PASSWORD = "cloudant.password";
    
    public final String CATALOG_INTERACTOR_TYPE = "catalog.interactor.type";
    
    public static final String INSIGHTS_SQL_SCHEMA = "GIMETA";
    
    public static final String INSIGHTS_ZEN_URL = "insights.zen.url";
    
    private static volatile InsightsConfiguration INSTANCE;
    
    private String iisUser = null;
    private String iisCreds = null;
    private String iisBasicAuthToken = null;

    private Properties props;

    protected InsightsConfiguration(Properties props) {
        super();
        this.props = props;
    }

    public static InsightsConfiguration getInstance() {
        if (INSTANCE == null) {
            synchronized (LOCK) {
                if (INSTANCE != null) {
                    return INSTANCE;
                }
                Properties props = getConfigFromFile();
                props = getConfigFromEnv(props);
                INSTANCE = new InsightsConfiguration(props);
                INSTANCE.setIISBasicAuthToken();
            }
        }
        return INSTANCE;
    }

    public InsightsMetaStoreType getInsightsMetaDataStoreType() {
        if (props.getProperty(INSIGHTS_META_STORE_TYPE) != null) {
            try {
                return InsightsMetaStoreType.valueOf(props.getProperty(INSIGHTS_META_STORE_TYPE).toUpperCase());
            } catch (IllegalArgumentException e) {
                LOG.warn("Could not identify InsightsMetaStoreType, using default "
                        + defaultInsightsMetaStoreType.name(), e);
            }
        }
        return defaultInsightsMetaStoreType;
    }
    
    public CatalogInteractorType getCatalogInteractorType() {
        if (props.getProperty(CATALOG_INTERACTOR_TYPE) != null) {
            try {
                return CatalogInteractorType.valueOf(props.getProperty(CATALOG_INTERACTOR_TYPE).toUpperCase());
            } catch (IllegalArgumentException e) {
                LOG.warn("Could not identify Catalog type, using default "
                        + defaultInsightsMetaStoreType.name(), e);
            }
        }
        return defaultCatalogInteractor;
    }
    

    public boolean isNoAuthEnabled() {
        LOG.debug("isNoAuthEnabled: {}", Boolean.parseBoolean(props.getProperty(PROP_NOAUTH)));
        return Boolean.parseBoolean(props.getProperty(PROP_NOAUTH));
    }

    public String getIISBaseUrl() {
        return props.getProperty(IIS_BASE_URL);
    }
    
    public String getUGBaseUrl() {
        return props.getProperty(UG_BASE_URL);
    }

    public String getUGBaseUrlV3() {
        return props.getProperty(UG_BASE_URL_V3);
    }
    
    public String getInsightsZenUrl() {
        return props.getProperty(INSIGHTS_ZEN_URL);
    }
    
    private void setIISBasicAuthToken() {
        iisUser = props.getProperty(IIS_USERNAME_SECRET).trim();
        iisCreds = props.getProperty(IIS_PASSWORD_SECRET).trim();
        iisBasicAuthToken = new String(Base64.getEncoder().encode((iisUser + ":" + iisCreds).getBytes()), StandardCharsets.UTF_8);
    }
    
    public String getIISUSer() {
        return iisUser;
    }
    
    public String getIISCreds() {
        return iisCreds;
    }
    
    public String getIISBasicAuthToken() {
        return iisBasicAuthToken;
    }
    
    public String getDecoded(String encoded) {
        return new String(Base64.getDecoder().decode(encoded.getBytes()), StandardCharsets.UTF_8).trim();
    }
    
    public boolean isValidIISBasicAuthToken(String authString) {
        if (authString == null || !authString.toUpperCase().startsWith("BASIC ") || authString.length() < 8) {
            return false;
        }
        String authDecodedString = getDecoded(authString.substring(6));
        String[] authCreds = authDecodedString.split(":");
        if (authCreds.length != 2) {
            return false;
        }
        if (authCreds[0].trim().equals(iisUser) && authCreds[1].trim().equals(iisCreds)) {
            return true;
        }
        return false;
    }

    public String getZKUrl() {
        return props.getProperty(ZK_URL);
    }

    public String getZKRootNodeName() {
        return "govinsights";
    }

    public String getInsightsSchemaName() {
        if (getDbType().toLowerCase().startsWith(StorageConstants.DB_TYPE_ORCL)) {
            return getDbUser();
        } else {
            return INSIGHTS_SQL_SCHEMA;
        }
    }

    public int getInsightsSchemaVersion() {
        return 2;
    }

    public String getSolrUrl() {
        return props.getProperty(SOLR_URL);
    }

    public String getDataCollection() {
        return props.getProperty(DATA_COLLECTION);
    }
    
    public String getDbType() {
        return props.getProperty(DB_TYPE);
    }

    public String getDbUser() {
        return props.getProperty(DB_USER);
    }
    
    public String getCloudantAccount() {
		return props.getProperty(CLOUDANT_ACCOUNT);
	}

	public String getCloudantUser() {
		return props.getProperty(CLOUDANT_USER);
	}

	public String getCloudantPassword() {
		return props.getProperty(CLOUDANT_PASSWORD);
	}

    public String getTestProperty(String propertyName) {
        if (props.getProperty(propertyName) != null && !props.getProperty(propertyName).isEmpty()) {
            return props.getProperty(propertyName);
        }
        String envVarName = ENV_VAR_PREFIX + propertyName.toUpperCase().replaceAll("\\.", "_");
        if (System.getenv(envVarName) != null) {
            return System.getenv(envVarName);
        }
        if (System.getenv(propertyName) != null) {
            return System.getenv(propertyName);
        }
        return null;
    }

    private static Properties getConfigFromFile() {
        Properties props = new Properties();
        InputStream is = InsightsConfiguration.class.getClassLoader().getResourceAsStream(CONFIGURATION_FILE_LOCATION);
        if (is != null) {
            try {
                try {
                    props.load(is);
                } finally {
                    is.close();
                }
            } catch (Exception e) {
                LOG.warn(String.format("Failed to load configuration file %s from the application classpath: %s",
                    CONFIGURATION_FILE_LOCATION, e.getMessage()), e);
            }
        } else {
            LOG.debug("Configuration file {} not found on the application classpath", CONFIGURATION_FILE_LOCATION);
        }
        return props;
    }

    private static Properties getConfigFromEnv(Properties defaults) {
        
        Map<String, String> env = System.getenv();
        String[] propertyNames = defaults.stringPropertyNames().toArray(new String[0]);
        for (int i=0; i< propertyNames.length; i++) {
            safeSetConfigValueFromEnv(defaults, env, propertyNames[i]);
        }
        //safeSetConfigValueFromEnv(props, env, PROP_NOAUTH);
        //safeSetConfigValueFromEnv(props, env, PROP_FEATURES);
        return defaults;
    }

    public boolean isFeatureAvailable(String featureEnumName) {
        String enabledFeatures = props.getProperty(PROP_FEATURES);
        if (enabledFeatures != null) {
            for (String feature : enabledFeatures.trim().split(",")) {
                String[] featureWithMode = feature.trim().split(":");
                if (featureWithMode[0].equalsIgnoreCase(featureEnumName)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isFeatureEnabled(String featureEnumName) {
        String enabledFeatures = props.getProperty(PROP_FEATURES);
        if (enabledFeatures != null) {
            for (String feature : enabledFeatures.trim().split(",")) {
                String[] featureWithMode = feature.trim().split(":");
                if (featureWithMode[0].equalsIgnoreCase(featureEnumName)) {
                    if (featureWithMode.length > 1 && featureWithMode[1].equalsIgnoreCase("t")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }
    
    private static void safeSetConfigValueFromEnv(Properties props, Map<String, String> env, String propertyName) {
        String envVarName = ENV_VAR_PREFIX + propertyName.toUpperCase().replaceAll("\\.", "_");
        String value = env.get(envVarName);
        if (value != null) {
            LOG.debug("Found environment variable {} with value: {}", envVarName, value);
            props.put(propertyName, value);
        }
    }

}
