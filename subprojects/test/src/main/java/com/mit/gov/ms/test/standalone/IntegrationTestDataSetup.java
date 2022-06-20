/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.test.standalone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import java.sql.Blob;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ParseException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
//import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.gov.ms.common.HttpUtils;
import com.mit.gov.ms.common.InsightsConfiguration;
import com.mit.gov.ms.common.InsightsException;

public class IntegrationTestDataSetup {

    private final Logger LOGGER = LoggerFactory.getLogger(IntegrationTestDataSetup.class);

    private String getIISBASEAPIURL = InsightsConfiguration.getInstance().getIISBaseUrl();
    private String getIISAPIURL = getIISBASEAPIURL + "/ia/api";
    private String basic_auth = InsightsConfiguration.getInstance().getIISBasicAuthToken();
    private String mn_Server = InsightsConfiguration.getInstance().getTestProperty("insights.db2oncloud.mnserver");
    private String importAreaName = "InsightsTestData";
    private String SAR_WS1 = "SAR_WS1";
    private String SAR_WS2 = "SAR_WS2";
    private String SAR_WS3 = "SAR_WS3";
    private String SCA_WS = "SSC_WS";
    private String ML_DRD_WS = "ML_DRD_WS";
    private boolean isCleanRun = false;
    private String dataConnectionName = "insights_connection";
    private String dataTableName = "SCC_CUSTOMER10";

    // test properties
    private static String INSIGHTS_DB_USER = InsightsConfiguration.getInstance()
            .getTestProperty("insights.db2oncloud.user");
    private static String INSIGHTS_DB_PASSWORD = InsightsConfiguration.getInstance()
            .getTestProperty("insights.db2oncloud.password");
    private static String INSIGHTS_DB_HOST = InsightsConfiguration.getInstance()
            .getTestProperty("insights.db2oncloud.host");
    private static String INSIGHTS_DB_PORT = InsightsConfiguration.getInstance()
            .getTestProperty("insights.db2oncloud.port");
    private static String INSIGHTS_DB_NAME = InsightsConfiguration.getInstance()
            .getTestProperty("insights.db2oncloud.name");
    private static String INSIGHTS_CLEAN_RUN = InsightsConfiguration.getInstance()
            .getTestProperty("insights.db2oncloud.clean.run");
    private static String TEST_SUFFIX = InsightsConfiguration.getInstance()
            .getTestProperty("insights.db2oncloud.test.suffix");

    // private String iisUserName =
    // InsightsConfiguration.getInstance().getIISUSer();
    // private String iisUserpassword =
    // InsightsConfiguration.getInstance().getIISCreds();

    private final String ACCOUNT_HOLDERS_TABLE_CREATE = " CREATE TABLE ACCOUNT_HOLDERS ( "
            + "ACCOUNT_HOLDER_ID  SMALLINT, " + "NAME  VARCHAR(128), " + "ADDRESS  VARCHAR(136), "
            + "ZIP  VARCHAR(128), " + "AGE  VARCHAR(117), " + "GENDER  VARCHAR(111), " + "MARITAL_STATUS  VARCHAR(12), "
            + "PROFESSION  VARCHAR(24), " + "NBR_YEARS_CLI  VARCHAR(24), " + "EMAIL  VARCHAR(32), "
            + "CCN  VARCHAR(33), " + "PHONE1  VARCHAR(19), " + "PHONE2  VARCHAR(23), " + "CC  VARCHAR(12), "
            + "CONTACT  VARCHAR(29) )";

    private final String ACCOUNT_HOLDERS_TABLE_INSERT = "INSERT INTO ACCOUNT_HOLDERS ( "
            + "ACCOUNT_HOLDER_ID, NAME,ADDRESS, ZIP, AGE, GENDER, MARITAL_STATUS, PROFESSION, "
            + "NBR_YEARS_CLI, EMAIL, CCN, PHONE1, PHONE2, CC, CONTACT ) "
            + " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    private final String BANK_ACCOUNTS_TABLE_CREATE = " CREATE TABLE BANK_ACCOUNTS ( "
            + "ACCOUNT_ID INTEGER, CUSTOMER_ID INTEGER, ACCOUNT_TYPE VARCHAR(3), "
            + "ACCOUNT_BALANCE DECIMAL(10), JOINT_ACCOUNT_HOLDER  VARCHAR(3), BANKCARD VARCHAR(3), "
            + "ONLINE_ACCESS VARCHAR(3), CARDNB VARCHAR(19), RTN INTEGER )";

    private final String BANK_ACCOUNTS_TABLE_INSERT = "INSERT INTO BANK_ACCOUNTS ( "
            + "ACCOUNT_ID, CUSTOMER_ID, ACCOUNT_TYPE, ACCOUNT_BALANCE, JOINT_ACCOUNT_HOLDER, BANKCARD, "
            + "ONLINE_ACCESS, CARDNB, RTN )  VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    private final String BANK_CLIENTS_TABLE_CREATE = " CREATE TABLE BANK_CLIENTS ( "
            + "CLIENT_ID  SMALLINT, NAME  VARCHAR(128),  ADDRESS  VARCHAR(136), ZIP  VARCHAR(123), "
            + "AGE  VARCHAR(116), GENDER  VARCHAR(8), MARITAL_STATUS  VARCHAR(12), PROFESSION  VARCHAR(98), "
            + "NBR_YEARS_CLI  VARCHAR(24), SAVINGS_ACCOUNT  VARCHAR(24), ONLINE_ACCESS  VARCHAR(8), JOINED_ACCOUNTS  VARCHAR(12), "
            + "BANKCARD  VARCHAR(24),AVERAGE_BALANCE  VARCHAR(10), ACCOUNT_ID  VARCHAR(10), ACCOUNT_TYPE  VARCHAR(10),  "
            + "EMAIL  VARCHAR(37), CCN  VARCHAR(29), PHONE1  VARCHAR(19), PHONE2  VARCHAR(12), CC  VARCHAR(12), CONTACT  VARCHAR(32), "
            + " RTN  VARCHAR(28))";

    private final String BANK_CLIENTS_TABLE_INSERT = "INSERT INTO BANK_CLIENTS ( "
            + "CLIENT_ID, NAME, ADDRESS, ZIP, AGE, GENDER, MARITAL_STATUS, PROFESSION, NBR_YEARS_CLI, "
            + "SAVINGS_ACCOUNT, ONLINE_ACCESS, JOINED_ACCOUNTS, BANKCARD, AVERAGE_BALANCE, ACCOUNT_ID, "
            + "ACCOUNT_TYPE, EMAIL, CCN, PHONE1, PHONE2, CC, CONTACT, RTN ) "
            + " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    private final String BANK_CUSTOMERS_TABLE_CREATE = " CREATE TABLE BANK_CUSTOMERS ( "
            + "CUSTOMER_ID  INTEGER, NAME  VARCHAR(128), ADDRESS  VARCHAR(136), ZIP  VARCHAR(121), "
            + "CREDIT_RATING  VARCHAR(111), AGE  DECIMAL(6), GENDER  VARCHAR(8), MARITAL_STATUS  VARCHAR(12), "
            + "PROFESSION  VARCHAR(24), NBR_YEARS_CLI  VARCHAR(24), EMAIL  VARCHAR(32), CCN  VARCHAR(30), "
            + "PHONE1  VARCHAR(23), PHONE2  VARCHAR(12), CC  VARCHAR(12), CONTACT  VARCHAR(29) )";

    private final String BANK_CUSTOMERS_TABLE_INSERT = "INSERT INTO BANK_CUSTOMERS ( "
            + "CUSTOMER_ID, NAME, ADDRESS, ZIP, CREDIT_RATING, AGE, GENDER, MARITAL_STATUS, "
            + "PROFESSION, NBR_YEARS_CLI, EMAIL, CCN, PHONE1, PHONE2, CC, CONTACT ) "
            + "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    private final String CHECKING_ACCOUNTS_TABLE_CREATE = " CREATE TABLE CHECKING_ACCOUNTS ( "
            + "ACCOUNT_ID  INTEGER, ACCOUNT_HOLDER_ID  SMALLINT, ACCOUNT_BALANCE  DECIMAL(10), "
            + "JOINT_ACCOUNT_HOLDER  VARCHAR(3), BANKCARD  VARCHAR(3), ONLINE_ACCESS  VARCHAR(3), "
            + "CARDNB  VARCHAR(19), RTN INTEGER )";

    private final String CHECKING_ACCOUNTS_TABLE_INSERT = "INSERT INTO CHECKING_ACCOUNTS ( "
            + "ACCOUNT_ID, ACCOUNT_HOLDER_ID, ACCOUNT_BALANCE, JOINT_ACCOUNT_HOLDER, BANKCARD, "
            + "ONLINE_ACCESS, CARDNB, RTN )  " + " VALUES ( ?, ?, ?, ?, ?, ?, ?, ? ) ";

    private final String SAVINGS_ACCOUNTS_TABLE_CREATE = " CREATE TABLE SAVINGS_ACCOUNTS ( "
            + "ACCOUNT_ID  INTEGER, ACCOUNT_HOLDER_ID  SMALLINT, ACCOUNT_BALANCE  DECIMAL(10), "
            + "JOINT_ACCOUNT_HOLDER  VARCHAR(3), BANKCARD  VARCHAR(3), ONLINE_ACCESS  VARCHAR(3), RTN  INTEGER )";

    private final String SAVINGS_ACCOUNTS_TABLE_INSERT = "INSERT INTO SAVINGS_ACCOUNTS ( "
            + "ACCOUNT_ID, ACCOUNT_HOLDER_ID, ACCOUNT_BALANCE, JOINT_ACCOUNT_HOLDER, BANKCARD, "
            + "ONLINE_ACCESS, RTN ) VALUES ( ?, ?, ?, ?, ?, ?, ? ) ";

    private final String SCC_CUSTOMER1_TABLE_CREATE = " CREATE TABLE SCC_CUSTOMER1 ( "
            + "col1  VARCHAR(75), col2  VARCHAR(75), col3  VARCHAR(75) ) ";

    private final String SCC_CUSTOMER1_TABLE_INSERT = "INSERT INTO SCC_CUSTOMER1 ( "
            + "col1, col2, col3 ) VALUES ( ?, ?, ? ) ";

    private final String SCC_CUSTOMER2_TABLE_CREATE = " CREATE TABLE SCC_CUSTOMER2 ( "
            + "col1  VARCHAR(75), col2  VARCHAR(75), col3  VARCHAR(75) ) ";

    private final String SCC_CUSTOMER2_TABLE_INSERT = "INSERT INTO SCC_CUSTOMER2 ( "
            + "col1, col2, col3 ) VALUES ( ?, ?, ? ) ";

    private final String SCC_CUSTOMER3_TABLE_CREATE = " CREATE TABLE SCC_CUSTOMER3 ( "
            + "col1  VARCHAR(75), col2  VARCHAR(75), col3  VARCHAR(75) ) ";

    private final String SCC_CUSTOMER3_TABLE_INSERT = "INSERT INTO SCC_CUSTOMER3 ( "
            + "col1, col2, col3 ) VALUES ( ?, ?, ? ) ";

    private final String SCC_CUSTOMER4_TABLE_CREATE = " CREATE TABLE SCC_CUSTOMER4 ( "
            + "col1  VARCHAR(75), col2  VARCHAR(75), col3  VARCHAR(75) ) ";

    private final String SCC_CUSTOMER4_TABLE_INSERT = "INSERT INTO SCC_CUSTOMER4 ( "
            + "col1, col2, col3 ) VALUES ( ?, ?, ? ) ";

    private final String SCC_CUSTOMER5_TABLE_CREATE = " CREATE TABLE SCC_CUSTOMER5 ( "
            + "col1  VARCHAR(75), col2  VARCHAR(75), col3  VARCHAR(75) ) ";

    private final String SCC_CUSTOMER5_TABLE_INSERT = "INSERT INTO SCC_CUSTOMER5 ( "
            + "col1, col2, col3 ) VALUES ( ?, ?, ? ) ";

    private final String SCC_CUSTOMER6_TABLE_CREATE = " CREATE TABLE SCC_CUSTOMER6 ( "
            + "col1  VARCHAR(75), col2  VARCHAR(75), col3  VARCHAR(75) ) ";

    private final String SCC_CUSTOMER6_TABLE_INSERT = "INSERT INTO SCC_CUSTOMER6 ( "
            + "col1, col2, col3 ) VALUES ( ?, ?, ? ) ";

    private final String SCC_CUSTOMER7_TABLE_CREATE = " CREATE TABLE SCC_CUSTOMER7 ( "
            + "col1  VARCHAR(75), col2  VARCHAR(75), col3  VARCHAR(75) ) ";

    private final String SCC_CUSTOMER7_TABLE_INSERT = "INSERT INTO SCC_CUSTOMER7 ( "
            + "col1, col2, col3 ) VALUES ( ?, ?, ? ) ";

    private final String SCC_CUSTOMER8_TABLE_CREATE = " CREATE TABLE SCC_CUSTOMER8 ( "
            + "col1  VARCHAR(75), col2  VARCHAR(75), col3  VARCHAR(75) ) ";

    private final String SCC_CUSTOMER8_TABLE_INSERT = "INSERT INTO SCC_CUSTOMER8 ( "
            + "col1, col2, col3 ) VALUES ( ?, ?, ? ) ";

    private final String SCC_CUSTOMER9_TABLE_CREATE = " CREATE TABLE SCC_CUSTOMER9 ( "
            + "col1  VARCHAR(75), col2  VARCHAR(75), col3  VARCHAR(75), col4  VARCHAR(75) ) ";

    private final String SCC_CUSTOMER9_TABLE_INSERT = "INSERT INTO SCC_CUSTOMER9 ( "
            + "col1, col2, col3, col4 ) VALUES ( ?, ?, ?, ? ) ";

    private final String SCC_CUSTOMER10_TABLE_CREATE = " CREATE TABLE SCC_CUSTOMER10 ( "
            + "col1  VARCHAR(75), col2  VARCHAR(75), col3  VARCHAR(75), col4  VARCHAR(75) ) ";

    private final String SCC_CUSTOMER10_TABLE_INSERT = "INSERT INTO SCC_CUSTOMER10 ( "
            + "col1, col2, col3, col4 ) VALUES ( ?, ?, ?, ? ) ";

    private final String BANK_MARKET_TABLE_CREATE = " CREATE TABLE BANK_MARKET ( "
            + "CUST_NUM REAL, AGE INTEGER, JOB CHAR(100), MARITAL BLOB, "
            + "EDUCATION VARCHAR(100), DEFAULT VARCHAR(100), BALANCE VARCHAR(100), HOUSING VARCHAR(100), "
            + "LOAN VARCHAR(100), CONTACT VARCHAR(100), DAY BIGINT, MONTH VARCHAR(100), DURATION DECIMAL, "
            + "CAMPAIGN DECFLOAT, PDAYS DOUBLE, PREVIOUS SMALLINT, POUTCOME VARCHAR(100), Y VARCHAR(100) )";

    private final String BANK_MARKET_TABLE_INSERT = "INSERT INTO BANK_MARKET ( "
            + "CUST_NUM, AGE, JOB, MARITAL, EDUCATION, DEFAULT, BALANCE, HOUSING, LOAN, CONTACT, "
            + "DAY, MONTH, DURATION, CAMPAIGN, PDAYS, PREVIOUS, POUTCOME, Y) VALUES "
            + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    private final String BANK_MARKET_ANO_TABLE_CREATE = " CREATE TABLE BANK_MARKET_ANO ( "
            + "CUST_NUM REAL, AGE INTEGER, JOB CHAR(100), MARITAL BLOB, "
            + "EDUCATION VARCHAR(100), DEFAULT VARCHAR(100), BALANCE VARCHAR(100), HOUSING VARCHAR(100), "
            + "LOAN VARCHAR(100), CONTACT VARCHAR(100), DAY BIGINT, MONTH VARCHAR(100), DURATION DECIMAL, "
            + "CAMPAIGN DECFLOAT, PDAYS DOUBLE, PREVIOUS SMALLINT, POUTCOME VARCHAR(100), Y VARCHAR(100) )";

    private final String BANK_MARKET_ANO_TABLE_INSERT = "INSERT INTO BANK_MARKET_ANO ( "
            + "CUST_NUM, AGE, JOB, MARITAL, EDUCATION, DEFAULT, BALANCE, HOUSING, LOAN, CONTACT, "
            + "DAY, MONTH, DURATION, CAMPAIGN, PDAYS, PREVIOUS, POUTCOME, Y) VALUES "
            + "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
    // private final String IS_TEST_TABLE_CREATE = " CREATE TABLE BANK_MARKET2 ( "
    // + "IS_TEST_CONFIGURED VARCHAR(100) )";

    private String schemaName = null;

    private static boolean isTestDataConfigured = false;
    private DataSource ds = null;
    private Connection connection = null;

    public boolean setUpTestData() {
        
        if (INSIGHTS_CLEAN_RUN.equalsIgnoreCase("true")) {
            isCleanRun = true;
        } else {
            isCleanRun = false;
        }
        try {
            ds = getDataSource();
            connection = ds.getConnection();
            schemaName = connection.getSchema();
            verifyDbConnection(connection);

            // Load test data for SAR
            boolean status = loadSARTestData();
            if (status == false) {
                LOGGER.error("failed to load the SAR data");
                return status;
            }
            // Load test data for SCC
            status = loadSCCTestData();
            if (status == false) {
                LOGGER.error("failed to load the SCC data");
                return status;
            }

            // Load test data for Anomaly Detection
            status = loadSAnomalyDetectionTestData();
            if (status == false) {
                LOGGER.error("failed to load the Anomaly Detection data");
                return status;
            }

            // create a import are which import data to SAR_bankdemo_import
            if (isCleanRun) {
                importAreaName += TEST_SUFFIX;
                dataConnectionName += "_" +TEST_SUFFIX;
            }
            
            
            boolean importAreaCreationStatus = createImportArea(importAreaName, "parameters.xml");
            if ( !importAreaCreationStatus ) {
                LOGGER.error("unable to create/verify import area");
                return importAreaCreationStatus;
            }

            // create work space for SAR and SCC
            try {
                if (isCleanRun) {
                    SAR_WS1 += "_"+TEST_SUFFIX;
                    SAR_WS2 += "_"+TEST_SUFFIX;
                    SAR_WS3 += "_"+TEST_SUFFIX;
                    SCA_WS += "_"+TEST_SUFFIX;
                    ML_DRD_WS += "_"+TEST_SUFFIX;
                }
                
                createWorkSpaceAndRunCA("BANKDEMO_WS1.xml", SAR_WS1, "columnAnalysis_ws1.xml");
                createWorkSpaceAndRunCA("BANKDEMO_WS2.xml", SAR_WS2, "columnAnalysis_ws2.xml");
                createWorkSpaceAndRunCA("BANKDEMO_WS3.xml", SAR_WS3, "columnAnalysis_ws3.xml");
                createWorkSpaceAndRunCA("CustomClass.xml", SCA_WS, "columnAnalysis_scc.xml");
                createWorkSpaceAndRunCA("ML_DRD_WS.xml", ML_DRD_WS, null);
                
            } catch (InsightsException e) {
                LOGGER.error("Error while creating Work space or running column analysis", e);
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            LOGGER.info("Error while creating db2 connection", e);
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LOGGER.info("unable to close the db2 connection", e);
                    return false;
                }
            }
        }
        isTestDataConfigured = true;
        return isTestDataConfigured;
    }

    private void verifyDbConnection(Connection connection) throws SQLException {
        LOGGER.info("Database connection URL is: " + connection.getMetaData().getURL());
        String dbProductName = connection.getMetaData().getDatabaseProductName();
        LOGGER.info("dbProductName=" + dbProductName);
        String sql;
        if (dbProductName.contains("Oracle")) {
            sql = "SELECT 1 FROM DUAL";
        } else if (dbProductName.contains("DB2")) {
            sql = "SELECT 1 FROM SYSIBM.SYSDUMMY1";
        } else {
            sql = "SELECT 1";
        }
        LOGGER.info(sql);
        PreparedStatement ps = connection.prepareStatement(sql);
        try {
            ps.executeQuery();
            LOGGER.info("verified Connection");
        } catch (SQLException e) {
            LOGGER.error("Error verifying connection", e);
            throw e;
        } finally {
            ps.close();
        }
    }

    private boolean loadSARTestData() {

        boolean status = true;

        try {

            //create ACCOUNT_HOLDERS table
            boolean tableAvailable = doesTableExists("ACCOUNT_HOLDERS");
            
            if (isCleanRun && tableAvailable) {
                PreparedStatement stmt = connection.prepareStatement("DROP TABLE ACCOUNT_HOLDERS");
                stmt.execute();
                connection.commit();
                stmt.close();
                tableAvailable = false;
            }
            
            if (!tableAvailable) {
                PreparedStatement stmt = connection.prepareStatement(ACCOUNT_HOLDERS_TABLE_CREATE);
                stmt.execute();
                InputStream is = InsightsConfiguration.class.getClassLoader().getResourceAsStream("ACCOUNT_HOLDERS.csv");
                if (is != null) {
                    stmt = connection.prepareStatement(ACCOUNT_HOLDERS_TABLE_INSERT);
                    BufferedReader csvReader = new BufferedReader(new InputStreamReader(is));
                    String row = null;
                    while ((row = csvReader.readLine()) != null) {

                        String[] values = row.split(",");
                        int count = 1;
                        for (String value : values) {
                            String data = (value == null || value.isEmpty()) ? " " : value.trim();
                            stmt.setString(count, data);
                            count++;
                        }
                        stmt.addBatch();
                    }
                    // execute the batch
                    int[] updateCounts = stmt.executeBatch();
                    status = checkUpdateCounts(updateCounts);
                    // since there were no errors, commit
                    if (!status) {
                        return status;
                    }
                    connection.commit();

                } else {
                    LOGGER.error("Unable to find the ACCOUNT_HOLDERS file to load test data ");
                    return false;
                }
                stmt.close();
            }
            
            //create BANK_ACCOUNTS table
            tableAvailable = doesTableExists("BANK_ACCOUNTS");
            
            if (isCleanRun && tableAvailable) {
                PreparedStatement stmt = connection.prepareStatement("DROP TABLE BANK_ACCOUNTS");
                stmt.execute();
                connection.commit();
                stmt.close();
                tableAvailable = false;
            }

            if (!tableAvailable) {
                PreparedStatement stmt = connection.prepareStatement(BANK_ACCOUNTS_TABLE_CREATE);
                status = stmt.execute();
                InputStream is = InsightsConfiguration.class.getClassLoader().getResourceAsStream("BANK_ACCOUNTS.csv");

                if (is != null) {
                    stmt = connection.prepareStatement(BANK_ACCOUNTS_TABLE_INSERT);
                    BufferedReader csvReader = new BufferedReader(new InputStreamReader(is));
                    String row = null;
                    while ((row = csvReader.readLine()) != null) {

                        String[] values = row.split(",");
                        int count = 1;
                        for (String value : values) {
                            String data = (value == null || value.isEmpty()) ? " " : value.trim();
                            stmt.setString(count, data);
                            count++;
                        }
                        stmt.addBatch();
                    }
                    // execute the batch
                    int[] updateCounts = stmt.executeBatch();
                    status = checkUpdateCounts(updateCounts);
                    if (!status) {
                        return status;
                    }
                    // since there were no errors, commit
                    connection.commit();

                } else {
                    LOGGER.error("Unable to find the BANK_ACCOUNTS file to load test data ");
                    return false;
                }
                stmt.close();

            }
            
            //create BANK_CLIENTS table
            tableAvailable = doesTableExists("BANK_CLIENTS");
            
            if (isCleanRun && tableAvailable) {
                PreparedStatement stmt = connection.prepareStatement("DROP TABLE BANK_CLIENTS");
                stmt.execute();
                tableAvailable = false;
                connection.commit();
                stmt.close();
            }

            if (!tableAvailable) {
                PreparedStatement stmt = connection.prepareStatement(BANK_CLIENTS_TABLE_CREATE);
                stmt.execute();
                InputStream is = InsightsConfiguration.class.getClassLoader().getResourceAsStream("BANK_CLIENTS.csv");

                if (is != null) {
                    stmt = connection.prepareStatement(BANK_CLIENTS_TABLE_INSERT);
                    BufferedReader csvReader = new BufferedReader(new InputStreamReader(is));
                    String row = null;
                    while ((row = csvReader.readLine()) != null) {

                        String[] values = row.split(",");
                        int count = 1;
                        for (String value : values) {
                            String data = (value == null || value.isEmpty()) ? " " : value.trim();
                            stmt.setString(count, data);
                            count++;
                        }

                        stmt.addBatch();
                    }
                    // execute the batch
                    int[] updateCounts = stmt.executeBatch();
                    status = checkUpdateCounts(updateCounts);
                    if (!status) {
                        return status;
                    }
                    // since there were no errors, commit
                    connection.commit();

                } else {
                    LOGGER.error("Unable to find the BANK_CLIENTS file to load test data ");
                    return false;
                }
                stmt.close();

            }
            
            //create BANK_CUSTOMERS table
            tableAvailable = doesTableExists("BANK_CUSTOMERS");
            
            if (isCleanRun && tableAvailable) {
                PreparedStatement stmt = connection.prepareStatement("DROP TABLE BANK_CUSTOMERS");
                stmt.execute();
                tableAvailable = false;
                connection.commit();
                stmt.close();
            }

            if (!tableAvailable) {
                PreparedStatement stmt = connection.prepareStatement(BANK_CUSTOMERS_TABLE_CREATE);
                stmt.execute();
                InputStream is = InsightsConfiguration.class.getClassLoader().getResourceAsStream("BANK_CUSTOMERS.csv");

                if (is != null) {
                    stmt = connection.prepareStatement(BANK_CUSTOMERS_TABLE_INSERT);
                    BufferedReader csvReader = new BufferedReader(new InputStreamReader(is));
                    String row = null;
                    while ((row = csvReader.readLine()) != null) {

                        String[] values = row.split(",");
                        int count = 1;
                        for (String value : values) {
                            String data = (value == null || value.isEmpty()) ? " " : value.trim();
                            stmt.setString(count, data);
                            count++;
                        }

                        stmt.addBatch();
                    }
                    // execute the batch
                    int[] updateCounts = stmt.executeBatch();
                    status = checkUpdateCounts(updateCounts);
                    if (!status) {
                        return false;
                    }
                    // since there were no errors, commit
                    connection.commit();

                } else {
                    LOGGER.error("Unable to find the BANK_CUSTOMERS file to load test data ");
                    return false;
                }
                stmt.close();

            }

            //create CHECKING_ACCOUNTS table
            tableAvailable = doesTableExists("CHECKING_ACCOUNTS");
            
            if (isCleanRun && tableAvailable) {
                PreparedStatement stmt = connection.prepareStatement("DROP TABLE CHECKING_ACCOUNTS");
                stmt.execute();
                tableAvailable = false;
                connection.commit();
                stmt.close();
            }

            if (!tableAvailable) {
                PreparedStatement stmt = connection.prepareStatement(CHECKING_ACCOUNTS_TABLE_CREATE);
                stmt.execute();
                InputStream is = InsightsConfiguration.class.getClassLoader().getResourceAsStream("CHECKING_ACCOUNTS.csv");

                if (is != null) {
                    stmt = connection.prepareStatement(CHECKING_ACCOUNTS_TABLE_INSERT);
                    BufferedReader csvReader = new BufferedReader(new InputStreamReader(is));
                    String row = null;
                    while ((row = csvReader.readLine()) != null) {

                        String[] values = row.split(",");
                        int count = 1;
                        for (String value : values) {
                            String data = (value == null || value.isEmpty()) ? " " : value.trim();
                            stmt.setString(count, data);
                            count++;
                        }

                        stmt.addBatch();
                    }
                    // execute the batch
                    int[] updateCounts = stmt.executeBatch();
                    status = checkUpdateCounts(updateCounts);
                    if (!status) {
                        return status;
                    }
                    // since there were no errors, commit
                    connection.commit();

                } else {
                    LOGGER.error("Unable to find the CHECKING_ACCOUNTS file to load test data ");
                    return false;
                }
                stmt.close();
            }
            
            //create SAVINGS_ACCOUNTS table
            tableAvailable = doesTableExists("SAVINGS_ACCOUNTS");
            
            if (isCleanRun && tableAvailable) {
                PreparedStatement stmt = connection.prepareStatement("DROP TABLE SAVINGS_ACCOUNTS");
                stmt.execute();
                tableAvailable = false;
                connection.commit();
                stmt.close();
            }

            if (!tableAvailable) {
                PreparedStatement stmt = connection.prepareStatement(SAVINGS_ACCOUNTS_TABLE_CREATE);
                stmt.execute();
                InputStream is = InsightsConfiguration.class.getClassLoader().getResourceAsStream("SAVINGS_ACCOUNTS.csv");

                if (is != null) {
                    stmt = connection.prepareStatement(SAVINGS_ACCOUNTS_TABLE_INSERT);
                    BufferedReader csvReader = new BufferedReader(new InputStreamReader(is));
                    String row = null;
                    while ((row = csvReader.readLine()) != null) {

                        String[] values = row.split(",");
                        int count = 1;
                        for (String value : values) {
                            String data = (value == null || value.isEmpty()) ? " " : value.trim();
                            stmt.setString(count, data);
                            count++;
                        }
                        stmt.addBatch();
                    }
                    // execute the batch
                    int[] updateCounts = stmt.executeBatch();
                    status = checkUpdateCounts(updateCounts);
                    if (!status) {
                        return status;
                    }
                    // since there were no errors, commit
                    connection.commit();

                } else {
                    LOGGER.error("Unable to find the SAVINGS_ACCOUNTS file to load test data ");
                    return false;
                }
                stmt.close();
            }

        } catch (SQLException | IOException e) {
            LOGGER.error("Unable to load the test data for SAR", e);
            status = false;
            try {
                connection.close();
            } catch (SQLException e1) {
                LOGGER.error("Unable to close connection", e1);
                return false;
            }
        } finally {

        }

        return status;
    }

    private boolean loadSCCTestData() {
        boolean status = true;

        try {
          //create ACCOUNT_HOLDERS table
            boolean tableAvailable = doesTableExists("SCC_CUSTOMER1");
            
            if (isCleanRun && tableAvailable) {
                PreparedStatement stmt = connection.prepareStatement("DROP TABLE SCC_CUSTOMER1");
                stmt.execute();
                connection.commit();
                stmt.close();
                tableAvailable = false;
            }
            
            if (!tableAvailable) {
                PreparedStatement stmt = connection.prepareStatement(SCC_CUSTOMER1_TABLE_CREATE);
                stmt.execute();

                tableAvailable = doesTableExists("SCC_CUSTOMER2");
                if (isCleanRun && tableAvailable) {
                    stmt = connection.prepareStatement("DROP TABLE SCC_CUSTOMER2");
                    stmt.execute();
                    connection.commit();
                    tableAvailable = false;
                }
                
                if (!tableAvailable) {
                    stmt = connection.prepareStatement(SCC_CUSTOMER2_TABLE_CREATE);
                    stmt.execute();
                }
                
                tableAvailable = doesTableExists("SCC_CUSTOMER3");
                if (isCleanRun && tableAvailable) {
                    stmt = connection.prepareStatement("DROP TABLE SCC_CUSTOMER3");
                    stmt.execute();
                    connection.commit();
                    tableAvailable = false;
                }
                
                if (!tableAvailable) {
                    stmt = connection.prepareStatement(SCC_CUSTOMER3_TABLE_CREATE);
                    stmt.execute();
                }
                
                tableAvailable = doesTableExists("SCC_CUSTOMER4");
                if (isCleanRun && tableAvailable) {
                    stmt = connection.prepareStatement("DROP TABLE SCC_CUSTOMER4");
                    stmt.execute();
                    connection.commit();
                    tableAvailable = false;
                }
                
                if (!tableAvailable) {
                    stmt = connection.prepareStatement(SCC_CUSTOMER4_TABLE_CREATE);
                    stmt.execute();
                }

                InputStream is = InsightsConfiguration.class.getClassLoader().getResourceAsStream("scc_demo.csv");

                if (is != null) {
                    stmt = connection.prepareStatement(SCC_CUSTOMER1_TABLE_INSERT);
                    PreparedStatement stmt1 = connection.prepareStatement(SCC_CUSTOMER2_TABLE_INSERT);
                    PreparedStatement stmt2 = connection.prepareStatement(SCC_CUSTOMER3_TABLE_INSERT);
                    PreparedStatement stmt3 = connection.prepareStatement(SCC_CUSTOMER4_TABLE_INSERT);

                    BufferedReader csvReader = new BufferedReader(new InputStreamReader(is));
                    String row = null;
                    while ((row = csvReader.readLine()) != null) {

                        String[] values = row.split(",");
                        int count = 1;
                        for (String value : values) {
                            String data = (value == null || value.isEmpty()) ? " " : value.trim();
                            stmt.setString(count, data);
                            stmt1.setString(count, data);
                            stmt2.setString(count, data);
                            stmt3.setString(count, data);
                            count++;
                        }
                        stmt.addBatch();
                        stmt1.addBatch();
                        stmt2.addBatch();
                        stmt3.addBatch();
                    }
                    // execute the batch
                    int[] updateCounts = stmt.executeBatch();
                    status = checkUpdateCounts(updateCounts);
                    if (!status) {
                        return status;
                    }
                    // since there were no errors, commit
                    connection.commit();
                    updateCounts = stmt1.executeBatch();
                    status = checkUpdateCounts(updateCounts);
                    if (!status) {
                        return status;
                    }
                    // since there were no errors, commit
                    connection.commit();
                    updateCounts = stmt2.executeBatch();
                    status = checkUpdateCounts(updateCounts);
                    if (!status) {
                        return status;
                    }
                    // since there were no errors, commit
                    connection.commit();
                    updateCounts = stmt3.executeBatch();
                    status = checkUpdateCounts(updateCounts);
                    if (!status) {
                        return status;
                    }
                    // since there were no errors, commit
                    connection.commit();
                    stmt1.close();
                    stmt2.close();
                    stmt3.close();
                } else {
                    LOGGER.error("Unable to find the ACCOUNT_HOLDERS file to load test data ");
                    return false;
                }
                stmt.close();
            }
            
            tableAvailable = doesTableExists("SCC_CUSTOMER5");
            if (isCleanRun && tableAvailable) {
                PreparedStatement stmt = connection.prepareStatement("DROP TABLE SCC_CUSTOMER5");
                stmt.execute();
                connection.commit();
                stmt.close();
                tableAvailable = false;
            }

            if (!tableAvailable) {
                PreparedStatement stmt = connection.prepareStatement(SCC_CUSTOMER5_TABLE_CREATE);
                stmt.execute();
                
                tableAvailable = doesTableExists("SCC_CUSTOMER6");
                if (isCleanRun && tableAvailable) {
                    stmt = connection.prepareStatement("DROP TABLE SCC_CUSTOMER6");
                    stmt.execute();
                    connection.commit();
                    tableAvailable = false;
                }
                
                if (!tableAvailable) {
                    stmt = connection.prepareStatement(SCC_CUSTOMER6_TABLE_CREATE);
                    stmt.execute();
                }
                
                tableAvailable = doesTableExists("SCC_CUSTOMER7");
                if (isCleanRun && tableAvailable) {
                    stmt = connection.prepareStatement("DROP TABLE SCC_CUSTOMER7");
                    stmt.execute();
                    connection.commit();
                    tableAvailable = false;
                }
                if (!tableAvailable) {
                    stmt = connection.prepareStatement(SCC_CUSTOMER7_TABLE_CREATE);
                    stmt.execute();
                }
                
                tableAvailable = doesTableExists("SCC_CUSTOMER8");
                if (isCleanRun && tableAvailable) {
                    stmt = connection.prepareStatement("DROP TABLE SCC_CUSTOMER8");
                    stmt.execute();
                    connection.commit();
                    tableAvailable = false;
                }
                if (!doesTableExists("SCC_CUSTOMER8")) {
                    stmt = connection.prepareStatement(SCC_CUSTOMER8_TABLE_CREATE);
                    stmt.execute();
                }

                InputStream is = InsightsConfiguration.class.getClassLoader().getResourceAsStream("scc_demo1.csv");

                if (is != null) {
                    stmt = connection.prepareStatement(SCC_CUSTOMER5_TABLE_INSERT);
                    PreparedStatement stmt1 = connection.prepareStatement(SCC_CUSTOMER6_TABLE_INSERT);
                    PreparedStatement stmt2 = connection.prepareStatement(SCC_CUSTOMER7_TABLE_INSERT);
                    PreparedStatement stmt3 = connection.prepareStatement(SCC_CUSTOMER8_TABLE_INSERT);

                    BufferedReader csvReader = new BufferedReader(new InputStreamReader(is));
                    String row = null;
                    while ((row = csvReader.readLine()) != null) {

                        String[] values = row.split(",");
                        int count = 1;
                        for (String value : values) {
                            String data = (value == null || value.isEmpty()) ? " " : value.trim();
                            stmt.setString(count, data);
                            stmt1.setString(count, data);
                            stmt2.setString(count, data);
                            stmt3.setString(count, data);
                            count++;
                        }
                        stmt.addBatch();
                        stmt1.addBatch();
                        stmt2.addBatch();
                        stmt3.addBatch();
                    }
                    // execute the batch
                    int[] updateCounts = stmt.executeBatch();
                    status = checkUpdateCounts(updateCounts);
                    if (!status) {
                        return status;
                    }
                    // since there were no errors, commit
                    connection.commit();
                    updateCounts = stmt1.executeBatch();
                    status = checkUpdateCounts(updateCounts);
                    if (!status) {
                        return status;
                    }
                    // since there were no errors, commit
                    connection.commit();
                    updateCounts = stmt2.executeBatch();
                    status = checkUpdateCounts(updateCounts);
                    if (!status) {
                        return status;
                    }
                    // since there were no errors, commit
                    connection.commit();
                    updateCounts = stmt3.executeBatch();
                    status = checkUpdateCounts(updateCounts);
                    if (!status) {
                        return status;
                    }
                    // since there were no errors, commit
                    connection.commit();

                    stmt1.close();
                    stmt2.close();
                    stmt3.close();

                } else {
                    LOGGER.error("Unable to find the ACCOUNT_HOLDERS file to load test data ");
                    return false;
                }
                stmt.close();
            }

            tableAvailable = doesTableExists("SCC_CUSTOMER9");
            if (isCleanRun && tableAvailable) {
                PreparedStatement stmt = connection.prepareStatement("DROP TABLE SCC_CUSTOMER9");
                stmt.execute();
                connection.commit();
                stmt.close();
                tableAvailable = false;
            }
            
            if (!tableAvailable) {
                PreparedStatement stmt = connection.prepareStatement(SCC_CUSTOMER9_TABLE_CREATE);
                stmt.execute();
                
                tableAvailable = doesTableExists("SCC_CUSTOMER10");
                if (isCleanRun && tableAvailable) {
                    stmt = connection.prepareStatement("DROP TABLE SCC_CUSTOMER10");
                    stmt.execute();
                    connection.commit();
                    tableAvailable = false;
                }
                
                if (!tableAvailable) {
                    stmt = connection.prepareStatement(SCC_CUSTOMER10_TABLE_CREATE);
                    stmt.execute();
                }

                InputStream is = InsightsConfiguration.class.getClassLoader().getResourceAsStream("scc_demo2.csv");

                if (is != null) {
                    stmt = connection.prepareStatement(SCC_CUSTOMER9_TABLE_INSERT);
                    PreparedStatement stmt1 = connection.prepareStatement(SCC_CUSTOMER10_TABLE_INSERT);

                    BufferedReader csvReader = new BufferedReader(new InputStreamReader(is));
                    String row = null;
                    while ((row = csvReader.readLine()) != null) {

                        String[] values = row.split(",");
                        int count = 1;
                        for (String value : values) {
                            String data = (value == null || value.isEmpty()) ? " " : value.trim();
                            stmt.setString(count, data);
                            stmt1.setString(count, data);
                            count++;
                        }
                        stmt.addBatch();
                        stmt1.addBatch();
                    }
                    // execute the batch
                    int[] updateCounts = stmt.executeBatch();
                    status = checkUpdateCounts(updateCounts);
                    if (!status) {
                        return status;
                    }
                    // since there were no errors, commit
                    connection.commit();
                    updateCounts = stmt1.executeBatch();
                    status = checkUpdateCounts(updateCounts);
                    if (!status) {
                        return status;
                    }
                    // since there were no errors, commit
                    connection.commit();
                    stmt1.close();

                } else {
                    LOGGER.error("Unable to find the ACCOUNT_HOLDERS file to load test data ");
                    return false;
                }
                stmt.close();
            }

        } catch (SQLException | IOException e) {
            LOGGER.error("Unable to load the test data for SCC", e);
            status = false;
        }

        return status;
    }

    private boolean loadSAnomalyDetectionTestData() {
        boolean status = true;

        try {
            if (!doesTableExists("BANK_MARKET")) {
                PreparedStatement stmt = connection.prepareStatement(BANK_MARKET_TABLE_CREATE);
                stmt.execute();
                if (!doesTableExists("BANK_MARKET_ANO")) {
                    stmt = connection.prepareStatement(BANK_MARKET_ANO_TABLE_CREATE);
                    stmt.execute();
                }

                InputStream is = IntegrationTestDataSetup.class.getClassLoader()
                        .getResourceAsStream("bank_market_train.csv");
                
                InputStream isAnomalous = IntegrationTestDataSetup.class.getClassLoader().getResourceAsStream("bank_market_test.csv");

                if (is != null && isAnomalous != null) {
                    stmt = connection.prepareStatement(BANK_MARKET_TABLE_INSERT);
                    addMlDrdDataToStatement(stmt, is);
                    // execute the batch
                    int[] updateCounts = stmt.executeBatch();
                    status = checkUpdateCounts(updateCounts);
                    if (!status) {
                        return status;
                    }
					// since there were no errors, commit
					connection.commit();

                    stmt = connection.prepareStatement(BANK_MARKET_ANO_TABLE_INSERT);
                    addMlDrdDataToStatement(stmt, isAnomalous);
                    updateCounts = stmt.executeBatch();
                    status = checkUpdateCounts(updateCounts);
                    if (!status) {
                        return status;
                    }
                    // since there were no errors, commit
                    connection.commit();
                } else {
                    LOGGER.error("Unable to find the ACCOUNT_HOLDERS file to load test data ");
                    status = false;
                }
                stmt.close();
            }

        } catch (SQLException | IOException e) {
            LOGGER.error("Unable to load the test data for Anomaly detection", e);
            return false;
        }

        return status;
    }

    private boolean doesTableExists(String tableName) {
        // XMetaUtils.executeStmtWithStringParam(stmt, null);
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM SYSCAT.TABLES WHERE TYPE = 'T' "
                    + "AND TABSCHEMA = '" + schemaName + "' AND TABNAME = '" + tableName + "'");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            LOGGER.error("Failure; whiledoesTableExists", e);
            return false;
        }

    }

    public boolean checkUpdateCounts(int[] updateCounts) {
        for (int i = 0; i < updateCounts.length; i++) {
            if (updateCounts[i] >= 0) {
                // System.out.println("OK; updateCount=" + updateCounts[i]);
            } else if (updateCounts[i] == Statement.SUCCESS_NO_INFO) {
                // System.out.println("OK; updateCount=Statement.SUCCESS_NO_INFO");
            } else if (updateCounts[i] == Statement.EXECUTE_FAILED) {
                LOGGER.info("Failure; updateCount=Statement.EXECUTE_FAILED");
                return false;
            }
        }
        return true;
    }

    private void runColumnAnalysis(String filenameCA) throws InsightsException {

        // create run CA URL
        String getCustomClassURL = getIISAPIURL + "/executeTasks";

        // URL for check status of executeTasks (e.g. runCA)
        String getStatusCA = getIISAPIURL + "/analysisStatus?scheduleID=";
        HttpResponse HTTPResp;
        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "text/xml");
        header.put("Authorization", "Basic " + basic_auth);

        try {
            InputStreamEntity requestEntity = new InputStreamEntity(
                    IntegrationTestDataSetup.class.getClassLoader().getResourceAsStream(filenameCA));
            HTTPResp = HttpUtils.executeRestAPI(getCustomClassURL, "POST", header, requestEntity);
            int status2 = HTTPResp.getStatusLine().getStatusCode();
            if (!(status2 == 200 || status2 == 201)) {
                LOGGER.info("unable to initiate column analysis for : " + filenameCA);
                try {
                    if (HTTPResp.getStatusLine().getReasonPhrase() != null) {
                        LOGGER.info(HTTPResp.getStatusLine().getReasonPhrase());
                    }
                    if (HTTPResp.getEntity() != null) {
                        LOGGER.info(EntityUtils.toString(HTTPResp.getEntity(), StandardCharsets.UTF_8));
                    }
                    LOGGER.info("unable to initiate column analysis for : " + EntityUtils.toString(HTTPResp.getEntity(), StandardCharsets.UTF_8));
                } catch (ParseException | IOException e) {
                    LOGGER.info("unable to initiate column analysis for : " + HTTPResp.getEntity());
                }
                throw new InsightsException("Erorr while running column analysis for : " + filenameCA);
            }

            String scheduleId = HttpUtils.parseResponseAsXMLDocument(HTTPResp, "ScheduledTask", "scheduleId");
            String url = getStatusCA + scheduleId;
            LOGGER.info(url);
            long count = 0;
            while (count < 10) {

                LOGGER.info("Running Column Analysis..");
                HTTPResp = HttpUtils.executeRestAPI(url, "GET", header, null);

                String resp1 = HttpUtils.parseResponseAsXMLDocument(HTTPResp, "TaskExecution", "status");
                int status1 = HTTPResp.getStatusLine().getStatusCode();
                if (!(status1 == 200 || status1 == 201)) {
                    LOGGER.info("unable to Complete 'Run Column Analysis' ");
                    if (HTTPResp.getStatusLine().getReasonPhrase() != null) {
                        LOGGER.info(HTTPResp.getStatusLine().getReasonPhrase());
                    }
                    if (HTTPResp.getEntity() != null) {
                        LOGGER.info(EntityUtils.toString(HTTPResp.getEntity(), StandardCharsets.UTF_8));
                    }
                }

                if (resp1.equals("successful")) {
                    LOGGER.info("Successfully Completed 'Run Column Analysis' ");
                    break;
                } else if (resp1.equals("failed")) {
                    LOGGER.info("failed to 'Run Column Analysis' ");
                    break;
                }

                Thread.sleep(60 * 1000);
                count ++;
            }

        } catch (InterruptedException | ParseException | IOException e) {
            LOGGER.error("unable to run the CA", e);
            throw new InsightsException("unable to run the CA", e);
        }

    }

    private void createWorkSpaceAndRunCA(String filenameWorkSpace, String workSpaceName, String columnAnalysisFileName)
            throws InsightsException {
        // get work space details URL
        String getWorkspaceURL = getIISAPIURL + "/project?projectName=" + workSpaceName;
        HttpResponse HTTPResp;
        // create work space URL
        String createWorkspaceURL = getIISAPIURL + "/create";

        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "application/xml");
        header.put("Authorization", "Basic " + basic_auth);

        try {
            // check whether workspace exist
            HTTPResp = HttpUtils.executeRestAPI(getWorkspaceURL, "GET", header, null);
            if (HTTPResp.getStatusLine().getStatusCode() == 200) {
                LOGGER.info("Work space already exist : " + workSpaceName);
                // return;
            } else if (HTTPResp.getStatusLine().getStatusCode() == 404) {
                // create workspace with given name
                LOGGER.info("Work space : " + workSpaceName + "creation request Strted. url=" + createWorkspaceURL );
                InputStreamEntity requestEntity = new InputStreamEntity(
                        IntegrationTestDataSetup.class.getClassLoader().getResourceAsStream(filenameWorkSpace));
                HTTPResp = HttpUtils.executeRestAPI(createWorkspaceURL, "POST", header, requestEntity);
                int status = HTTPResp.getStatusLine().getStatusCode();
                if (!(status == 200 || status == 201)) {
                    LOGGER.info("unable to create a work space: " + filenameWorkSpace);
                    if (HTTPResp.getStatusLine().getReasonPhrase() != null) {
                        LOGGER.info(HTTPResp.getStatusLine().getReasonPhrase());
                    }
                    if (HTTPResp.getEntity() != null) {
                        LOGGER.info(EntityUtils.toString(HTTPResp.getEntity(), StandardCharsets.UTF_8));
                    }
                    throw new InsightsException("Erorr while creating work space: " + workSpaceName);
                }
                LOGGER.info("Work space : " + workSpaceName + "creation request completed Successfully." );
            }

        } catch (InsightsException | UnsupportedOperationException | IOException e) {
            Throwable cause = e.getCause();
            boolean created = false;
            while (cause != null) {
                if ( cause instanceof NoHttpResponseException) {
                    LOGGER.warn("Got exception while create workspace - " + cause.getMessage());
                    LOGGER.warn("Sleep for 60 sec, and check if workspace was created");
                    try {
                        Thread.sleep(60 * 1000);
                    } catch (InterruptedException e1) { }
                    HTTPResp = HttpUtils.executeRestAPI(getWorkspaceURL, "GET", header, null);
                    if (HTTPResp.getStatusLine().getStatusCode() == 200) {
                        LOGGER.info("Work space : " + workSpaceName + "creation request seems completed Successfully." );
                        created = true;
                    }
                    break;
                } else {
                    cause = cause.getCause();
                }
            }
            if (!created) {
                LOGGER.error("Erorr Creating Workspace : " + workSpaceName, e);
                throw new InsightsException("Erorr Creating Workspace : " + workSpaceName, e);
            }
        }
        // run column analysis for the given work spaces
        if (columnAnalysisFileName != null) {
            runColumnAnalysis(columnAnalysisFileName);
        }                
    }

    // private void deleteWorkSpace(String workSpaceName) {
    // // create a work space with give xml file.
    //
    // // create work space URL
    // String deleteWorkspaceURL = getIISAPIURL + "/project?projectName=" +
    // workSpaceName;
    //
    // HttpResponse HTTPResp;
    //
    // Map<String, String> header = new HashMap<String, String>();
    // header.put("Content-Type", "text/xml");
    // header.put("Authorization", "Basic " + basic_auth);
    //
    // try {
    // HTTPResp = HttpUtils.executeRestAPI(deleteWorkspaceURL, "GET", header, null);
    // if (HTTPResp.getStatusLine().getStatusCode() != 200) {
    // System.out.println("there is no work space name : " + workSpaceName +
    // ",Nothing to delete");
    // return;
    // }
    // HTTPResp = HttpUtils.executeRestAPI(deleteWorkspaceURL, "DELETE", header,
    // null);
    // int status = HTTPResp.getStatusLine().getStatusCode();
    // if ( !(status == 200 || status == 201) ) {
    // System.out.println("unable to delete a work space: " + workSpaceName);
    // }
    //
    // } catch (InsightsException e) {
    // LOGGER.error("Erorr while cleaning CustomClassEndToEndTest:runCA", e);
    // }
    // }

    private boolean createImportArea(String importAreaName, String importAreaParametersFile) {

        String getImportAreas = getIISBASEAPIURL + "/imam/dcm/getAreasByCurator?curator="
                + InsightsConfiguration.getInstance().getIISUSer() + "&size=100";
        String createImportAreaUrl = getIISBASEAPIURL + "/imam/cli/importareas/" + importAreaName;

        HttpResponse HTTPResp;

        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "multipart/form-data");
        header.put("Authorization", "Basic " + basic_auth);

        try {

            // check for the import area name
            HTTPResp = HttpUtils.executeRestAPI(getImportAreas, "GET", header, null);
            int status = HTTPResp.getStatusLine().getStatusCode();
            
            if (!(status == 200 || status == 201)) {
                LOGGER.info("unable to get a import area: " + importAreaName);
                if (HTTPResp.getStatusLine().getReasonPhrase() != null) {
                    LOGGER.info(HTTPResp.getStatusLine().getReasonPhrase());
                }
                if (HTTPResp.getEntity() != null) {
                    LOGGER.info(EntityUtils.toString(HTTPResp.getEntity(), StandardCharsets.UTF_8));
                }
            }

            String importAreaNamesList = EntityUtils.toString(HTTPResp.getEntity(), StandardCharsets.UTF_8);
            LOGGER.info("Lsit of existing import ares: " + importAreaNamesList);
            JSONArray importAreasArray = new JSONArray (importAreaNamesList);
            boolean importAreaExists = false;
            for (int i =0; i< importAreasArray.length();i++) {
            	String name = importAreasArray.getString(i);
            	if (name.equalsIgnoreCase(importAreaName) ){
            		importAreaExists = true;
            	}
            }

            if (importAreaExists) {
                LOGGER.info("import area already exist: " + importAreaName);
                return importAreaExists;
            }

//            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//            // builder.addTextBody("l", "sample import are created from rest end point");
//            // //Local file to upload
//            builder.addTextBody("ad", "import are created using REST to gov-insights sanity test"); // Import area description
//            builder.addTextBody("mn", mn_Server); // Metadata interchange server name
//            builder.addTextBody("id", "importing test data to run gov-insights sanity test"); // Import event description
//            builder.addBinaryBody("pf",
//                    IntegrationTestDataSetup.class.getClassLoader().getResourceAsStream(importAreaParametersFile),
//                    ContentType.TEXT_XML, importAreaParametersFile); // Parameters file
//            builder.addTextBody("f", "true"); // Force to skip version check
//            // builder.addTextBody("dcu", iisUserName); // DC user
//            // builder.addTextBody("dcw", iisUserpassword); // DC password
//            builder.addTextBody("sdc", "true"); // Save DC password.
//
//            HttpEntity multipart = builder.build();
//            // HttpEntity requestEntity = new InputStreamEntity(new
//            // FileInputStream(importAreaParametersFile));
//            HTTPResp = HttpUtils.executeRestAPI(createImportAreaUrl, "POST", header, multipart);
//            status = HTTPResp.getStatusLine().getStatusCode();
//            if (!(status == 200 || status == 201)) {
//            	LOGGER.info("import area create url :  " + createImportAreaUrl);
//                LOGGER.info("unable to create a import area: " + importAreaName);
//                if (HTTPResp.getStatusLine().getReasonPhrase() != null) {
//                    LOGGER.info(HTTPResp.getStatusLine().getReasonPhrase());
//                }
//                if (HTTPResp.getEntity() != null) {
//                    LOGGER.info(EntityUtils.toString(HTTPResp.getEntity(), StandardCharsets.UTF_8));
//                }
//                return false;
//            } else {
//            	LOGGER.info("import area create url :  " + createImportAreaUrl);
//            	LOGGER.info("import area created successfully. import area name :  " + importAreaName);
//            }
        } catch (InsightsException | ParseException | IOException e) {
            LOGGER.error("Erorr while creating import area : " + importAreaName, e);
            return false;
        } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        boolean verifyImporArea = verifyImportArea(importAreaName);
        if (!verifyImporArea) {
            LOGGER.error("Failed to verify import area : " + importAreaName);
            return verifyImporArea;
        }
        return true;
    }
    
    private boolean verifyImportArea(String importAreaName) {

        String getImportAreas = getIISBASEAPIURL + "/imam/dcm/getAreasByCurator?curator=" + InsightsConfiguration.getInstance().getIISUSer() + "&size=100";
        HttpResponse HTTPResp;
        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "multipart/form-data");
        header.put("Authorization", "Basic " + basic_auth);

        try {
            //check for the import area name
            HTTPResp = HttpUtils.executeRestAPI(getImportAreas, "GET", header, null);
            String importAreaNamesList = EntityUtils.toString(HTTPResp.getEntity(), StandardCharsets.UTF_8);
            JSONArray importAreasArray = new JSONArray (importAreaNamesList);
            boolean importAreaExists = false;
            for (int i =0; i< importAreasArray.length();i++) {
            	String name = importAreasArray.getString(i);
            	if (name.equalsIgnoreCase(importAreaName) ){
            		importAreaExists = true;
            	}
            }
            if (importAreaExists) {
                LOGGER.info("found import area : " + importAreaName);
            }
            //check data connection created
            String geteDataConnectionUrl = getIISBASEAPIURL + "/igc-rest/v1/search/?types=data_connection&pageSize=500&begin=0";
            HTTPResp = HttpUtils.executeRestAPI(geteDataConnectionUrl, "GET", header, null);
            String res = EntityUtils.toString(HTTPResp.getEntity(), StandardCharsets.UTF_8);
            JSONArray result = new JSONArray();
            if(res != null && !res.isEmpty()) {
                result = new JSONArray(new JSONObject(res).getJSONArray("items"));
            }
            
            boolean assetFound = false;
            for (int i=0; i<result.size(); i++) {
                JSONObject item = result.getJSONObject(i);
                if (item.containsKey("_name")) {
                    String connectionName = item.getString("_name");
                    if (connectionName.equalsIgnoreCase(dataConnectionName)) {
                        assetFound = true;
                        break;
                    }
                }
            }
            if (assetFound) {
                LOGGER.info("found data connection : " + dataConnectionName);
            } else {
                LOGGER.info("unable to get data connection : " + dataConnectionName);
                //assertTrue("unable to get data connection", false);
                return false;
            }
            //check assets import is successful
            String geteDataAssetUrl = getIISBASEAPIURL + "/igc-rest/v1/search/?types=database_table&pageSize=500&begin=0";
            HTTPResp = HttpUtils.executeRestAPI(geteDataAssetUrl, "GET", header, null);
            res = EntityUtils.toString(HTTPResp.getEntity(), StandardCharsets.UTF_8);
            result = new JSONObject(res).getJSONArray("items");
            assetFound = false;
            for (int i=0; i<result.size(); i++) {
                JSONObject item = result.getJSONObject(i);
                if (item.containsKey("_name")) {
                    String connectionName = item.getString("_name");
                    if (connectionName.equalsIgnoreCase(dataTableName)) {
                        assetFound = true;
                        break;
                    }
                }
            }
            if (assetFound) {
                LOGGER.info("found database table : " + dataTableName);
            } else {
                LOGGER.info("unable to get database table : " + dataTableName);
                //assertTrue("unable to get database table", false);
                return false;
            }
            
        } catch (ParseException | IOException | InsightsException | JSONException e) {
            LOGGER.error("Erorr while verifying import area : " + importAreaName, e);
        }
        return true;
    }

    private DataSource getDataSource() {
        return null;
    }
    
    public boolean cleanupData() {

        // if (INSIGHTS_CLEAN_RUN.equalsIgnoreCase("true")) {
        // isCleanRun = true;
        // } else {
        // isCleanRun = false;
        // }
        // boolean status = false;
        //
        // if (isCleanRun) {
        // SAR_WS1 += "_"+TEST_SUFFIX;
        // SAR_WS2 += "_"+TEST_SUFFIX;
        // SAR_WS3 += "_"+TEST_SUFFIX;
        // SSC_WS += "_"+TEST_SUFFIX;
        // ML_DRD_WS += "_"+TEST_SUFFIX;
        // importAreaName += TEST_SUFFIX;
        // }
        boolean status = true;
        SAR_WS1 += "_" + TEST_SUFFIX;
        SAR_WS2 += "_" + TEST_SUFFIX;
        SAR_WS3 += "_" + TEST_SUFFIX;
        SCA_WS += "_" + TEST_SUFFIX;
        ML_DRD_WS += "_" + TEST_SUFFIX;
        importAreaName += TEST_SUFFIX;

        String deleteImportAreaUrl = getIISBASEAPIURL + "/imam/cli/importareas/" + importAreaName;
        HttpResponse HTTPResp;
        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "multipart/form-data");
        header.put("Authorization", "Basic " + basic_auth);

        try {
            // delete import area
            HTTPResp = HttpUtils.executeRestAPI(deleteImportAreaUrl, "DELETE", header, null);
            int respStatus = HTTPResp.getStatusLine().getStatusCode();
            if (!(respStatus == 200 || respStatus == 201 || respStatus == 202 || respStatus == 204)) {
                LOGGER.info("unable to delete a import area: " + importAreaName);
                if (HTTPResp.getStatusLine().getReasonPhrase() != null) {
                    LOGGER.info(HTTPResp.getStatusLine().getReasonPhrase());
                }
                if (HTTPResp.getEntity() != null) {
                    LOGGER.info(EntityUtils.toString(HTTPResp.getEntity(), StandardCharsets.UTF_8));
                }
            }
        } catch (InsightsException | ParseException | IOException e) {
            LOGGER.error("Erorr while deleting import area : " + importAreaName, e);
            status = false;
        }

        String deleteWorkSpaceaUrl = getIISBASEAPIURL + "/ia/api/project?projectName=";
        try {
            // delete workspace SAR_WS1
            HTTPResp = HttpUtils.executeRestAPI(deleteWorkSpaceaUrl + SAR_WS1, "DELETE", header, null);
            int respStatus = HTTPResp.getStatusLine().getStatusCode();
            if (!(respStatus == 200 || respStatus == 201 || respStatus == 202 || respStatus == 204)) {
                LOGGER.info("unable to deleet Work space : " + SAR_WS1);
                status = false;
            } else {
                LOGGER.info("successfully deleted the Work space : " + SAR_WS1);
            }

            // delete workspace SAR_WS2
            HTTPResp = HttpUtils.executeRestAPI(deleteWorkSpaceaUrl + SAR_WS2, "DELETE", header, null);
            respStatus = HTTPResp.getStatusLine().getStatusCode();
            if (!(respStatus == 200 || respStatus == 201 || respStatus == 202 || respStatus == 204)) {
                LOGGER.info("unable to deleet Work space : " + SAR_WS2);
                status = false;
            } else {
                LOGGER.info("successfully deleted the Work space : " + SAR_WS2);
            }

            // delete workspace SAR_WS3
            HTTPResp = HttpUtils.executeRestAPI(deleteWorkSpaceaUrl + SAR_WS3, "DELETE", header, null);
            respStatus = HTTPResp.getStatusLine().getStatusCode();
            if (!(respStatus == 200 || respStatus == 201 || respStatus == 202 || respStatus == 204)) {
                LOGGER.info("unable to deleet Work space : " + SAR_WS3);
                status = false;
            } else {
                LOGGER.info("successfully deleted the Work space : " + SAR_WS3);
            }

            // delete workspace SSC_WS
            HTTPResp = HttpUtils.executeRestAPI(deleteWorkSpaceaUrl + SCA_WS, "DELETE", header, null);
            respStatus = HTTPResp.getStatusLine().getStatusCode();
            if (!(respStatus == 200 || respStatus == 201 || respStatus == 202 || respStatus == 204)) {
                LOGGER.info("unable to deleet Work space : " + SCA_WS);
                status = false;
            } else {
                LOGGER.info("successfully deleted the Work space : " + SCA_WS);
            }

            // delete workspace ML_DRD_WS
            HTTPResp = HttpUtils.executeRestAPI(deleteWorkSpaceaUrl + ML_DRD_WS, "DELETE", header, null);
            respStatus = HTTPResp.getStatusLine().getStatusCode();
            if (!(respStatus == 200 || respStatus == 201 || respStatus == 202 || respStatus == 204)) {
                LOGGER.info("unable to deleet Work space : " + ML_DRD_WS);
                status = false;
            }

        } catch (InsightsException e) {
            LOGGER.error("Erorr while deleting import area : " + importAreaName, e);
            status = false;
        }

        return status;
    }
    
	/**
	 * This will add ml_drd csv data to the prepared statement.
	 * 
	 * @param stmt: Prepared statemnet in which the csv file data has to be loaded.
	 * @param csvFile: InputStream for of the csv file
	 * @throws IOException
	 * @throws SQLException
	 */
	private void addMlDrdDataToStatement(PreparedStatement stmt, InputStream csvFile) throws IOException, SQLException {
		BufferedReader csvReader = new BufferedReader(new InputStreamReader(csvFile));
		String row = null;
		while ((row = csvReader.readLine()) != null) {

			String[] values = row.split(",");
			for (int i = 0; i < values.length; i++) {
				String data = (values[i] == null || values[i].isEmpty()) ? " " : values[i].trim();
				switch (i) {
				case 0:
					Float f = Float.parseFloat(data);
					stmt.setFloat(i + 1, f);
					break;
				case 1:
					stmt.setInt(i + 1, Integer.parseInt(data));
					break;
				case 3:
					Blob blob = connection.createBlob();
					blob.setBytes(1, data.getBytes());
					stmt.setBlob(i + 1, blob);
					break;
				case 10:
					stmt.setLong(i + 1, Long.parseLong(data));
					break;
				case 12:
				case 13:
					stmt.setBigDecimal(i + 1, new BigDecimal(data));
					break;
				case 14:
					stmt.setDouble(i + 1, Double.parseDouble(data));
					break;
				case 15:
					stmt.setShort(i + 1, Short.parseShort(data));
					break;
				default:
					stmt.setString(i + 1, data);
				}
			}
			stmt.addBatch();
		}

		return;
	}

}
