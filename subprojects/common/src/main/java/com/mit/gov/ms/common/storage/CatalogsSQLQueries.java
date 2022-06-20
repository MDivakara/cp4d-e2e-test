/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.storage;

import java.util.HashMap;

import com.mit.gov.ms.common.InsightsConfiguration;
import com.mit.gov.ms.store.StorageConstants;
import com.mit.gov.ms.store.queries.SQLQueries;

/**
 * @author Shaik.Nawaz
 *
 */
public class CatalogsSQLQueries extends SQLQueries {
    
    public static final String FETCH_CA_RESULTS_BY_COLUMN_ID = "select ADVANCEDRESULTS_XMETA from XMETA.INVESTIGATE_COLUMNANALYSISRESULTS WHERE CONTAINER_RID = ?;";

    public CatalogsSQLQueries(String dbType) {
        super(dbType);
    }

    protected HashMap<String, String> getQueries(String dbType) {
        HashMap<String, String> db2Queries = new HashMap<>();
        HashMap<String, String> oracleOveridesQueries = new HashMap<>();
        HashMap<String, String> sqlServerQueridess = new HashMap<>();

        db2Queries.put("DB_VERIFY_QUERY", "SELECT 1 FROM SYSIBM.SYSDUMMY1");
        oracleOveridesQueries.put("DB_VERIFY_QUERY", "SELECT 1 FROM DUAL");
        sqlServerQueridess.put("DB_VERIFY_QUERY", "SELECT 1");

        //db2Queries.put("SINGLE_TABLE_QUERY", "SELECT %s FROM %s.%s WHERE %s = ? ");

        db2Queries.put("XMETA_COLUMN_ANALYSIS_RESULTS_QUERY", getCAResultsQuery());
        
        db2Queries.put("XMETA_WORKSPACE_QUERY", "SELECT RID, NAME FROM IAVIEWS.IAPROJECT WHERE RID = ?");
        db2Queries.put("XMETA_GET_WORKSPACE_ID_QUERY", "SELECT RID FROM IAVIEWS.IAPROJECT WHERE NAME = ?");
        db2Queries.put("XMETA_COLUMNS_QUERY", "SELECT RID, NAME, TABLERID FROM IAVIEWS.IAREGISTEREDCOLUMN WHERE RID = ?");
        
        //fetch caresults for column_rid
        db2Queries.put("FETCH_CA_RESULTS_BY_COLUMN_ID", FETCH_CA_RESULTS_BY_COLUMN_ID);
        
        if (dbType.startsWith(StorageConstants.DB_TYPE_ORCL)) {
            db2Queries.putAll(oracleOveridesQueries);
        } else if (dbType.startsWith(StorageConstants.DB_TYPE_MSSQL)) {
            db2Queries.putAll(sqlServerQueridess);
        }
        return db2Queries;
    }

    public static String getCAResultsQuery() {
        String CA_RESULT_TABLE = "INVESTIGATE_COLUMNANALYSISRESULTS";
        String CA_RESULT_TS_COL = "XMETA_MODIFICATION_TIMESTAMP_XMETA";
        String XMETA_USER = "XMETA";
        String NAME_DATATYPE = "VARGRAPHIC";
        String LISTAGG_STR = "LISTAGG(nvl(bgterm.NAME,'NA'), ',')";
        String NVL_FUNC = "nvl";
        String CONCAT_OPERATOR = "||";
        String BG_TERM_QUERY = "    SELECT term.CLASSIFIEDOBJECTRID bg_datafield_rid, " + LISTAGG_STR + " bg_terms"
                + "    FROM IGVIEWS.IGASSIGNEDOBJECTSOFATERM term"
                + "    INNER JOIN IGVIEWS.IGBUSINESSTERM bgterm ON bgterm.RID = term.BUSINESSTERMRID"
                + "    GROUP BY term.CLASSIFIEDOBJECTRID";
        if (InsightsConfiguration.getInstance().getDbType() != null
                && InsightsConfiguration.getInstance().getDbType().toLowerCase().startsWith(StorageConstants.DB_TYPE_ORCL)) {
            CA_RESULT_TABLE = "INVESTGTCLMNNLYSSRSLTS";
            CA_RESULT_TS_COL = "XMETAMODIFICATIONTIMESTAMPXMET";
            XMETA_USER = InsightsConfiguration.getInstance().getDbUser();
            NAME_DATATYPE = "NVARCHAR2(255)";
            LISTAGG_STR = "LISTAGG(nvl(bgterm.NAME,'NA'), ',') WITHIN GROUP (ORDER BY term.CLASSIFIEDOBJECTRID)";
            BG_TERM_QUERY = "    SELECT term.CLASSIFIEDOBJECTRID bg_datafield_rid, " + LISTAGG_STR + " bg_terms"
                    + "    FROM IGVIEWS.IGASSIGNEDOBJECTSOFATERM term"
                    + "    INNER JOIN IGVIEWS.IGBUSINESSTERM bgterm ON bgterm.RID = term.BUSINESSTERMRID"
                    + "    GROUP BY term.CLASSIFIEDOBJECTRID";
        } else if (InsightsConfiguration.getInstance().getDbType() != null
                && InsightsConfiguration.getInstance().getDbType().toLowerCase().startsWith(StorageConstants.DB_TYPE_MSSQL)) {
            CA_RESULT_TABLE = "investigate_ColumnAnalysisResults";
            CA_RESULT_TS_COL = "_xmeta_modification_timestamp_xmeta";
            XMETA_USER = InsightsConfiguration.getInstance().getDbUser();
            NAME_DATATYPE = "NVARCHAR";
            NVL_FUNC = "isnull";
            CONCAT_OPERATOR = "+";
            String INNER_QUERY = " SELECT term.CLASSIFIEDOBJECTRID bg_datafield_rid, isnull(bgterm.NAME,'NA') bg_term "
                    + " FROM IGVIEWS.IGASSIGNEDOBJECTSOFATERM term "
                    + "  INNER JOIN IGVIEWS.IGBUSINESSTERM bgterm ON bgterm.RID = term.BUSINESSTERMRID ";
            BG_TERM_QUERY = " SELECT t0.bg_datafield_rid , STUFF((" + " SELECT ',' + t1.bg_term" + "   FROM ("
                    + INNER_QUERY + ") t1 " + "  WHERE t1.bg_datafield_rid = t0.bg_datafield_rid "
                    + "  ORDER BY t1.bg_term " + "  FOR XML PATH('')), 1, LEN(','), '') AS bg_terms " + " FROM ("
                    + INNER_QUERY + ") t0 " + " GROUP BY t0.bg_datafield_rid";
        }
        return "SELECT rc.TABLERID table_rid, rc.RID col_rid, cr.advancedResults_xmeta CA_RESULT, " + " " + NVL_FUNC
                + "(col_identity.collection_name, rc.\"TABLE\") collectionName, " + NVL_FUNC
                + "(col_identity.datafield_name, rc.NAME) dataField," + " " + NVL_FUNC
                + "(rc.\"DATABASE\", col_identity.df_database) databaseName, " + NVL_FUNC
                + "(rc.\"SCHEMA\", col_identity.df_schema) schemaName,"
                + " col_identity.df_host as dbhost, col_identity.datafield_desc col_desc, term_assignets.bg_terms terms,"
                + " col_identity.isdbtable isdbtable, " + " cr." + CA_RESULT_TS_COL + " modification_time, "
                + "cqh.qualityscore qualityscore, col_identity.created_on " + " FROM " + XMETA_USER + "."
                + CA_RESULT_TABLE + " cr"
                + " INNER JOIN IAVIEWS.IAREGISTEREDCOLUMN rc ON cr.ColumnAnalysisMaster_xmeta=rc.RID"
                + " LEFT JOIN IAVIEWS.IACOLUMNQUALITYHISTORY cqh ON cr.ColumnAnalysisMaster_xmeta=cqh.COLUMNRID"
                + " LEFT OUTER JOIN " + "(select * FROM (" + "    SELECT"
                + "        host.NAME df_host , db.NAME df_database, sch.NAME df_schema, coll.NAME collection_name, field.NAME AS datafield_name,"
                + "        cast (" + NVL_FUNC + "(field.LONGDESCRIPTION, " + NVL_FUNC
                + "(field.SHORTDESCRIPTION, 'NA')) AS VARCHAR(256)) AS datafield_desc,"
                + "        field.RID AS datafield_rid, coll.RID AS collection_rid, coll.createdon AS created_on,'true' isdbtable"
                + "    FROM CMVIEWS.PDRDATAFIELD field"
                + "    INNER JOIN CMVIEWS.PDRDATACOLLECTION coll ON field.OFDATACOLLECTIONRID = coll.RID"
                + "    INNER JOIN CMVIEWS.PDRDATABASESCHEMA sch  ON coll.OFDATASCHEMARID = sch.RID"
                + "    INNER JOIN CMVIEWS.PDRDATABASE db ON sch.OFDATABASERID = db.RID"
                + "    INNER JOIN CMVIEWS.PDRHOSTSYSTEM host ON db.HOSTEDBYRID = host.RID" + "    UNION" + "    SELECT"
                + "        cast('NA' AS " + NAME_DATATYPE + ") AS df_host, cast('NA' AS " + NAME_DATATYPE
                + ") AS df_database, cast('NA' AS " + NAME_DATATYPE + ") AS df_schema, " + "        pdfile.PATH "
                + CONCAT_OPERATOR + " '/' " + CONCAT_OPERATOR
                + " coll.NAME collection_name, field.NAME AS datafield_name," + "        cast (" + NVL_FUNC
                + "(field.LONGDESCRIPTION, " + NVL_FUNC
                + "(field.SHORTDESCRIPTION, 'NA')) AS VARCHAR(256)) AS datafield_desc,"
                + "        field.RID AS datafield_rid, coll.RID AS collection_rid,coll.createdon AS created_on ,'false' isdbtable"
                + "    FROM CMVIEWS.PDRDATAFIELD field"
                + "    INNER JOIN CMVIEWS.PDRDATACOLLECTION coll ON field.OFDATACOLLECTIONRID = coll.RID"
                + "    INNER JOIN CMVIEWS.PDRDATAFILE pdfile ON coll.OFDATAFILERID = pdfile.RID"
                + " ) col_identity ) col_identity " + " ON col_identity.datafield_rid = rc.DATAFIELDRID "
                + " LEFT OUTER JOIN " + " (select * from (" + BG_TERM_QUERY + " ) term_assignets ) term_assignets"
                + " ON term_assignets.bg_datafield_rid = rc.DATAFIELDRID "
                + " WHERE cr.advancedResults_xmeta IS NOT NULL AND rc.PROJECTRID = ? ";
    }

}
