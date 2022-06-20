/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.test.standalone;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.gov.ms.common.CatalogsMetaDataStore;
import com.mit.gov.ms.common.InsightsConfiguration;
import com.mit.gov.ms.common.InsightsException;
import com.mit.gov.ms.common.InsightsMetaDataStore;
import com.mit.gov.ms.store.StorageConstants;

public class UnitTestUtils {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(UnitTestUtils.class);

    private static String INSIGHTS_XMETA_DB_TYPE = System.getProperty("dbType") != null ? System.getProperty("dbType")
            : System.getenv("INSIGHTS_DB_TYPE") != null ? System.getenv("INSIGHTS_DB_TYPE")
                    : InsightsConfiguration.getInstance().getDbType() != null
                            ? InsightsConfiguration.getInstance().getDbType() : StorageConstants.DB_TYPE_DB2;
    private static String INSIGHTS_DB_TYPE = INSIGHTS_XMETA_DB_TYPE;
                            
//    private static String INSIGHTS_DB_PREFIX = INSIGHTS_DB_TYPE == "db2OnCloud" ? "insights." + INSIGHTS_DB_TYPE + "." :
//    	"insights.db";
    
	private static String INSIGHTS_DB_PROP_PREFIX = "insights." + INSIGHTS_DB_TYPE + ".";
    private static String INSIGHTS_XMETA_PROP_PREFIX = "insights." + INSIGHTS_XMETA_DB_TYPE + ".xmeta.";
	
    private static String INSIGHTS_DB_USER = InsightsConfiguration.getInstance().getTestProperty(INSIGHTS_DB_PROP_PREFIX + "user");
	private static String INSIGHTS_DB_PASSWORD = InsightsConfiguration.getInstance().getTestProperty(INSIGHTS_DB_PROP_PREFIX + "password");
	private static String INSIGHTS_DB_HOST = InsightsConfiguration.getInstance().getTestProperty(INSIGHTS_DB_PROP_PREFIX + "host");
	private static String INSIGHTS_DB_PORT = InsightsConfiguration.getInstance().getTestProperty(INSIGHTS_DB_PROP_PREFIX + "port");
	private static String INSIGHTS_DB_NAME = InsightsConfiguration.getInstance().getTestProperty(INSIGHTS_DB_PROP_PREFIX + "name");
	private static String INSIGHTS_DB_SCHEMA = InsightsConfiguration.getInstance().getInsightsSchemaName();

    private static String INSIGHTS_XMETA_USER = InsightsConfiguration.getInstance().getTestProperty(INSIGHTS_XMETA_PROP_PREFIX + "user");
    private static String INSIGHTS_XMETA_PASSWORD = InsightsConfiguration.getInstance().getTestProperty(INSIGHTS_XMETA_PROP_PREFIX + "password");
    private static String INSIGHTS_XMETA_HOST = InsightsConfiguration.getInstance().getTestProperty(INSIGHTS_XMETA_PROP_PREFIX + "host");
    private static String INSIGHTS_XMETA_PORT = InsightsConfiguration.getInstance().getTestProperty(INSIGHTS_XMETA_PROP_PREFIX + "port");
    private static String INSIGHTS_XMETA_NAME = InsightsConfiguration.getInstance().getTestProperty(INSIGHTS_XMETA_PROP_PREFIX + "name");
    private static String INSIGHTS_XMETA_SCHEMA = InsightsConfiguration.getInstance().getTestProperty(INSIGHTS_XMETA_PROP_PREFIX + "schema");

    static {
        INSIGHTS_XMETA_USER = (INSIGHTS_XMETA_USER == null) ? INSIGHTS_DB_USER : INSIGHTS_XMETA_USER;
        INSIGHTS_XMETA_PASSWORD = (INSIGHTS_XMETA_PASSWORD == null) ? INSIGHTS_DB_PASSWORD : INSIGHTS_XMETA_PASSWORD;
        INSIGHTS_XMETA_HOST = (INSIGHTS_XMETA_HOST == null) ? INSIGHTS_DB_HOST : INSIGHTS_XMETA_HOST;
        INSIGHTS_XMETA_PORT = (INSIGHTS_XMETA_PORT == null) ? INSIGHTS_DB_PORT : INSIGHTS_XMETA_PORT;
        INSIGHTS_XMETA_NAME = (INSIGHTS_XMETA_NAME == null) ? INSIGHTS_DB_NAME : INSIGHTS_XMETA_NAME;
        INSIGHTS_XMETA_SCHEMA = (INSIGHTS_XMETA_SCHEMA == null) ? INSIGHTS_XMETA_USER : INSIGHTS_XMETA_NAME; // xmeta schema is same as xmeta user
    }
    
    public static void initMetaDataStores() throws InsightsException {
        InsightsMetaDataStore.getInstance(getOpsDataSource());
        CatalogsMetaDataStore.getInstance(getXmetaDataSource());        
    }
    
    // Get Insights DataSource
	private static DataSource getOpsDataSource() throws InsightsException {
	    DataSource dataSource = null;
	    LOGGER.info("INSIGHTS_DB_TYPE(SystemProperty)=" + System.getProperty("dbType"));
        LOGGER.info("INSIGHTS_DB_TYPE(eEnv)=" + System.getenv("INSIGHTS_DB_TYPE"));
        LOGGER.info("INSIGHTS_DB_TYPE(insightsProperty)=" + InsightsConfiguration.getInstance().getDbType());
        LOGGER.info("INSIGHTS_DB_TYPE=" + INSIGHTS_DB_TYPE);
	    LOGGER.info("DB Connection Param="+INSIGHTS_DB_HOST+":"+INSIGHTS_DB_PORT+":"+INSIGHTS_DB_USER+":"+INSIGHTS_DB_PASSWORD+":"+INSIGHTS_DB_NAME);
//	    
//		if (INSIGHTS_DB_TYPE != null && INSIGHTS_DB_TYPE.equals(StorageConstants.DB_TYPE_ORCL)) {
//			OracleDataSource ds = new OracleDataSource();
//			ds.setUser(INSIGHTS_DB_USER);
//			ds.setPassword(INSIGHTS_DB_PASSWORD);
//			ds.setServerName(INSIGHTS_DB_HOST);
//			ds.setPortNumber(Integer.valueOf(INSIGHTS_DB_PORT));
//			ds.setDatabaseName(INSIGHTS_DB_NAME);
//			dataSource = ds;
//		} else if (INSIGHTS_DB_TYPE != null && INSIGHTS_DB_TYPE.equals(StorageConstants.DB_TYPE_MSSQL)) {
//			SQLServerDataSource ds = new SQLServerDataSource();
//			ds.setUser(INSIGHTS_DB_USER);
//			ds.setPassword(INSIGHTS_DB_PASSWORD);
//			ds.setServerName(INSIGHTS_DB_HOST);
//			ds.setPortNumber(Integer.valueOf(INSIGHTS_DB_PORT));
//			ds.setDatabaseName(INSIGHTS_DB_NAME);
//			dataSource = ds;
//		} else if (INSIGHTS_DB_TYPE != null && INSIGHTS_DB_TYPE.equals("db2OnCloud")) {
//			DB2SimpleDataSource ds = new DB2SimpleDataSource();
//			ds.setUser(INSIGHTS_DB_USER);
//			ds.setPassword(INSIGHTS_DB_PASSWORD);
//			ds.setServerName(INSIGHTS_DB_HOST);
//			ds.setPortNumber(Integer.valueOf(INSIGHTS_DB_PORT));
//			ds.setDatabaseName(INSIGHTS_DB_NAME);
//			ds.setCurrentSchema(INSIGHTS_DB_SCHEMA);
//			ds.setDriverType(4);
//			ds.setSslConnection(true);
//			//return new StorageDB2ConnectionPoolDataSource(ds, POOL_INITIAL_CONNECTION, POOL_MAX_CONNECTION, true);
//			dataSource = ds;
//		} else {
//			DB2SimpleDataSource ds = new DB2SimpleDataSource();
//			// DB2XADataSource ds = new DB2XADataSource();
//			ds.setUser(INSIGHTS_DB_USER);
//			ds.setPassword(INSIGHTS_DB_PASSWORD);
//			ds.setServerName(INSIGHTS_DB_HOST);
//			ds.setPortNumber(Integer.valueOf(INSIGHTS_DB_PORT));
//			ds.setDatabaseName(INSIGHTS_DB_NAME);
//			ds.setDriverType(4);
//			dataSource = (DataSource) ds;
//		}
		
		return dataSource;
	}

    /**
     *  Get Xmeta DataSource (TO BE USED ONLY for xmetaUtils)
     * @return
     * @throws InsightsException
     */
    public static DataSource getXmetaDataSource() throws InsightsException {
        DataSource dataSource = null;
        LOGGER.info("INSIGHTS_XMETA_TYPE(SystemProperty)=" + System.getProperty("dbType"));
        LOGGER.info("INSIGHTS_XMETA_TYPE(eEnv)=" + System.getenv("INSIGHTS_DB_TYPE"));
        LOGGER.info("INSIGHTS_XMETA_TYPE(insightsProperty)=" + InsightsConfiguration.getInstance().getDbType());
        LOGGER.info("INSIGHTS_XMETA_TYPE=" + INSIGHTS_XMETA_DB_TYPE);
        LOGGER
            .info("DB Connection Param=" + INSIGHTS_XMETA_HOST + ":" + INSIGHTS_XMETA_PORT + ":" + INSIGHTS_XMETA_USER
                + ":" + INSIGHTS_XMETA_PASSWORD + ":" + INSIGHTS_XMETA_NAME);
//
//        if (INSIGHTS_XMETA_DB_TYPE != null && INSIGHTS_XMETA_DB_TYPE.equals(StorageConstants.DB_TYPE_ORCL)) {
//            OracleDataSource ds = new OracleDataSource();
//            ds.setUser(INSIGHTS_XMETA_USER);
//            ds.setPassword(INSIGHTS_XMETA_PASSWORD);
//            ds.setServerName(INSIGHTS_XMETA_HOST);
//            ds.setPortNumber(Integer.valueOf(INSIGHTS_XMETA_PORT));
//            ds.setDatabaseName(INSIGHTS_XMETA_NAME);
//            dataSource = ds;
//        } else if (INSIGHTS_XMETA_DB_TYPE != null && INSIGHTS_XMETA_DB_TYPE.equals(StorageConstants.DB_TYPE_MSSQL)) {
//            SQLServerDataSource ds = new SQLServerDataSource();
//            ds.setUser(INSIGHTS_XMETA_USER);
//            ds.setPassword(INSIGHTS_XMETA_PASSWORD);
//            ds.setServerName(INSIGHTS_XMETA_HOST);
//            ds.setPortNumber(Integer.valueOf(INSIGHTS_XMETA_PORT));
//            ds.setDatabaseName(INSIGHTS_XMETA_NAME);
//            dataSource = ds;
//        } else if (INSIGHTS_XMETA_DB_TYPE != null && INSIGHTS_XMETA_DB_TYPE.equals("db2OnCloud")) {
//            DB2SimpleDataSource ds = new DB2SimpleDataSource();
//            ds.setUser(INSIGHTS_XMETA_USER);
//            ds.setPassword(INSIGHTS_XMETA_PASSWORD);
//            ds.setServerName(INSIGHTS_XMETA_HOST);
//            ds.setPortNumber(Integer.valueOf(INSIGHTS_XMETA_PORT));
//            ds.setDatabaseName(INSIGHTS_XMETA_NAME);
//            ds.setCurrentSchema(INSIGHTS_XMETA_SCHEMA);
//            ds.setDriverType(4);
//            ds.setSslConnection(true);
//            // return new StorageDB2ConnectionPoolDataSource(ds, POOL_INITIAL_CONNECTION, POOL_MAX_CONNECTION, true);
//            dataSource = ds;
//        } else {
//            DB2SimpleDataSource ds = new DB2SimpleDataSource();
//            // DB2XADataSource ds = new DB2XADataSource();
//            ds.setUser(INSIGHTS_XMETA_USER);
//            ds.setPassword(INSIGHTS_XMETA_PASSWORD);
//            ds.setServerName(INSIGHTS_XMETA_HOST);
//            ds.setPortNumber(Integer.valueOf(INSIGHTS_XMETA_PORT));
//            ds.setDatabaseName(INSIGHTS_XMETA_NAME);
//            ds.setDriverType(4);
//            dataSource = (DataSource) ds;
//        }

        return dataSource;
    }
}
