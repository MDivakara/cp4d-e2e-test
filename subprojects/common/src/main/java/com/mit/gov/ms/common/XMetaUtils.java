/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class XMetaUtils {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(XMetaUtils.class);

    private XMetaUtils() {
    }
//
//    public static String workspace_rid = null;
//
//    public static void setWorkspace_rid(String workspace_rid) {
//        XMetaUtils.workspace_rid = workspace_rid;
//    }
//
//    public static String getWorkSpaceQuery() {
//
//        String workspacesQuery = "SELECT RID, NAME FROM IAVIEWS.IAPROJECT ";
//        if (workspace_rid != null) {
//            workspacesQuery += "WHERE RID = '" + workspace_rid + "'";
//        }
//        return workspacesQuery;
//    }
//
//    public static String getWorkSpaceRid(ResultSet rs) throws SQLException {
//        return rs.getString(1);
//    }
//
//    public static String getCAResultsQuery() {
//        return getCAResultsQuery(false);
//    }
//
//    public static String getCAResultsQuery(boolean forProjectRId) {
//        String CA_RESULT_TABLE = "INVESTIGATE_COLUMNANALYSISRESULTS";
//        String CA_RESULT_TS_COL = "XMETA_MODIFICATION_TIMESTAMP_XMETA";
//        String XMETA_USER = "XMETA";
//        String NAME_DATATYPE = "VARGRAPHIC";
//        String LISTAGG_STR = "LISTAGG(nvl(bgterm.NAME,'NA'), ',')";
//        String NVL_FUNC = "nvl";
//        String CONCAT_OPERATOR = "||";
//        String BG_TERM_QUERY = "    SELECT term.CLASSIFIEDOBJECTRID bg_datafield_rid, "
//                + LISTAGG_STR + " bg_terms"
//                + "    FROM IGVIEWS.IGASSIGNEDOBJECTSOFATERM term"
//                + "    INNER JOIN IGVIEWS.IGBUSINESSTERM bgterm ON bgterm.RID = term.BUSINESSTERMRID"
//                + "    GROUP BY term.CLASSIFIEDOBJECTRID";
//        if (InsightsConfiguration.getInstance().getDbType() != null
//                && InsightsConfiguration.getInstance().getDbType().toLowerCase()
//                        .startsWith("oracle")) {
//            CA_RESULT_TABLE = "INVESTGTCLMNNLYSSRSLTS";
//            CA_RESULT_TS_COL = "XMETAMODIFICATIONTIMESTAMPXMET";
//            XMETA_USER = InsightsConfiguration.getInstance().getDbUser();
//            NAME_DATATYPE = "NVARCHAR2(255)";
//            LISTAGG_STR = "LISTAGG(nvl(bgterm.NAME,'NA'), ',') WITHIN GROUP (ORDER BY term.CLASSIFIEDOBJECTRID)";
//            BG_TERM_QUERY = "    SELECT term.CLASSIFIEDOBJECTRID bg_datafield_rid, "
//                    + LISTAGG_STR + " bg_terms"
//                    + "    FROM IGVIEWS.IGASSIGNEDOBJECTSOFATERM term"
//                    + "    INNER JOIN IGVIEWS.IGBUSINESSTERM bgterm ON bgterm.RID = term.BUSINESSTERMRID"
//                    + "    GROUP BY term.CLASSIFIEDOBJECTRID";
//        } else if (InsightsConfiguration.getInstance().getDbType() != null
//                && InsightsConfiguration.getInstance().getDbType().toLowerCase()
//                        .startsWith("sqlserver")) {
//            CA_RESULT_TABLE = "investigate_ColumnAnalysisResults";
//            CA_RESULT_TS_COL = "_xmeta_modification_timestamp_xmeta";
//            XMETA_USER = InsightsConfiguration.getInstance().getDbUser();
//            NAME_DATATYPE = "NVARCHAR";
//            NVL_FUNC = "isnull";
//            CONCAT_OPERATOR = "+";
//            String INNER_QUERY = " SELECT term.CLASSIFIEDOBJECTRID bg_datafield_rid, isnull(bgterm.NAME,'NA') bg_term "
//                    + " FROM IGVIEWS.IGASSIGNEDOBJECTSOFATERM term "
//                    + "  INNER JOIN IGVIEWS.IGBUSINESSTERM bgterm ON bgterm.RID = term.BUSINESSTERMRID ";
//            BG_TERM_QUERY = " SELECT t0.bg_datafield_rid , STUFF(("
//                    + " SELECT ',' + t1.bg_term" + "   FROM (" + INNER_QUERY
//                    + ") t1 "
//                    + "  WHERE t1.bg_datafield_rid = t0.bg_datafield_rid "
//                    + "  ORDER BY t1.bg_term "
//                    + "  FOR XML PATH('')), 1, LEN(','), '') AS bg_terms "
//                    + " FROM (" + INNER_QUERY + ") t0 "
//                    + " GROUP BY t0.bg_datafield_rid";
//        }
//        return "SELECT rc.TABLERID table_rid, rc.RID col_rid, cr.advancedResults_xmeta CA_RESULT, "
//                + " " + NVL_FUNC
//                + "(col_identity.collection_name, rc.\"TABLE\") collectionName, "
//                + NVL_FUNC + "(col_identity.datafield_name, rc.NAME) dataField,"
//                + " " + NVL_FUNC
//                + "(rc.\"DATABASE\", col_identity.df_database) databaseName, "
//                + NVL_FUNC
//                + "(rc.\"SCHEMA\", col_identity.df_schema) schemaName,"
//                + " col_identity.df_host as dbhost, col_identity.datafield_desc col_desc, term_assignets.bg_terms terms,"
//                + " col_identity.isdbtable isdbtable, " + " cr."
//                + CA_RESULT_TS_COL + " modification_time, "
//                + "cqh.qualityscore qualityscore, col_identity.created_on "
//                + " FROM " + XMETA_USER + "." + CA_RESULT_TABLE + " cr"
//                + " INNER JOIN IAVIEWS.IAREGISTEREDCOLUMN rc ON cr.ColumnAnalysisMaster_xmeta=rc.RID"
//                + " INNER JOIN IAVIEWS.IACOLUMNQUALITYHISTORY cqh ON cr.ColumnAnalysisMaster_xmeta=cqh.COLUMNRID"
//                + " LEFT OUTER JOIN " + "(select * FROM (" + "    SELECT"
//                + "        host.NAME df_host , db.NAME df_database, sch.NAME df_schema, coll.NAME collection_name, field.NAME AS datafield_name,"
//                + "        cast (" + NVL_FUNC + "(field.LONGDESCRIPTION, "
//                + NVL_FUNC
//                + "(field.SHORTDESCRIPTION, 'NA')) AS VARCHAR(256)) AS datafield_desc,"
//                + "        field.RID AS datafield_rid, coll.RID AS collection_rid, coll.createdon AS created_on,'true' isdbtable"
//                + "    FROM CMVIEWS.PDRDATAFIELD field"
//                + "    INNER JOIN CMVIEWS.PDRDATACOLLECTION coll ON field.OFDATACOLLECTIONRID = coll.RID"
//                + "    INNER JOIN CMVIEWS.PDRDATABASESCHEMA sch  ON coll.OFDATASCHEMARID = sch.RID"
//                + "    INNER JOIN CMVIEWS.PDRDATABASE db ON sch.OFDATABASERID = db.RID"
//                + "    INNER JOIN CMVIEWS.PDRHOSTSYSTEM host ON db.HOSTEDBYRID = host.RID"
//                + "    UNION" + "    SELECT" + "        cast('NA' AS "
//                + NAME_DATATYPE + ") AS df_host, cast('NA' AS " + NAME_DATATYPE
//                + ") AS df_database, cast('NA' AS " + NAME_DATATYPE
//                + ") AS df_schema, " + "        pdfile.PATH " + CONCAT_OPERATOR
//                + " '/' " + CONCAT_OPERATOR
//                + " coll.NAME collection_name, field.NAME AS datafield_name,"
//                + "        cast (" + NVL_FUNC + "(field.LONGDESCRIPTION, "
//                + NVL_FUNC
//                + "(field.SHORTDESCRIPTION, 'NA')) AS VARCHAR(256)) AS datafield_desc,"
//                + "        field.RID AS datafield_rid, coll.RID AS collection_rid,coll.createdon AS created_on ,'false' isdbtable"
//                + "    FROM CMVIEWS.PDRDATAFIELD field"
//                + "    INNER JOIN CMVIEWS.PDRDATACOLLECTION coll ON field.OFDATACOLLECTIONRID = coll.RID"
//                + "    INNER JOIN CMVIEWS.PDRDATAFILE pdfile ON coll.OFDATAFILERID = pdfile.RID"
//                + " ) col_identity ) col_identity "
//                + " ON col_identity.datafield_rid = rc.DATAFIELDRID "
//                + " LEFT OUTER JOIN " + " (select * from (" + BG_TERM_QUERY
//                + " ) term_assignets ) term_assignets"
//                + " ON term_assignets.bg_datafield_rid = rc.DATAFIELDRID "
//                + " WHERE cr.advancedResults_xmeta IS NOT NULL "
//                + (forProjectRId ? " AND rc.PROJECTRID = ? " : "");
//        // return "SELECT rc.TABLERID table_rid, rc.RID col_rid,
//        // cr.ADVANCEDRESULTS_XMETA CA_RESULT, "
//        // + " nvl(col_identity.collection_name, rc.\"TABLE\") collectionName,
//        // nvl(col_identity.datafield_name, rc.NAME) dataField,"
//        // + " nvl(rc.DATABASE, col_identity.df_database) database,
//        // nvl(rc.SCHEMA, col_identity.df_schema) schema,"
//        // + " col_identity.df_host as dbhost, col_identity.datafield_desc
//        // col_desc, term_assignets.bg_terms terms,"
//        // + " col_identity.isdbtable isdbtable, " + "
//        // cr.XMETA_MODIFICATION_TIMESTAMP_XMETA modification_time "
//        // + " FROM XMETA.INVESTIGATE_COLUMNANALYSISRESULTS cr"
//        // + " INNER JOIN IAVIEWS.IAREGISTEREDCOLUMN rc ON
//        // cr.COLUMNANALYSISMASTER_XMETA=rc.RID"
//        // + " LEFT OUTER JOIN " + " (" + " SELECT"
//        // + " host.NAME host , db.NAME database, sch.NAME schema, coll.NAME
//        // collectionName, field.NAME AS datafieldName,"
//        // + " cast (nvl(field.LONGDESCRIPTION, nvl(field.SHORTDESCRIPTION,
//        // 'NA')) AS VARCHAR(256)) AS datafield_desc,"
//        // + " field.RID AS datafield_rid, coll.RID AS collection_rid, 'true'
//        // isDBTable"
//        // + " FROM CMVIEWS.PDRDATAFIELD field"
//        // + " INNER JOIN CMVIEWS.PDRDATACOLLECTION coll ON
//        // field.OFDATACOLLECTIONRID = coll.RID"
//        // + " INNER JOIN CMVIEWS.PDRDATABASESCHEMA sch ON coll.OFDATASCHEMARID
//        // = sch.RID"
//        // + " INNER JOIN CMVIEWS.PDRDATABASE db ON sch.OFDATABASERID = db.RID"
//        // + " INNER JOIN CMVIEWS.PDRHOSTSYSTEM host ON db.HOSTEDBYRID =
//        // host.RID" + " UNION" + " SELECT"
//        // + " 'NA' host, 'NA' database, 'NA' schema, pdfile.PATH || '/' ||
//        // coll.NAME collectionName, field.NAME AS datafieldName,"
//        // + " cast (nvl(field.LONGDESCRIPTION, nvl(field.SHORTDESCRIPTION,
//        // 'NA')) AS VARCHAR(256)) AS col_desc,"
//        // + " field.RID AS col_rid, coll.RID AS file_rid, 'false' isDBTable"
//        // + " FROM CMVIEWS.PDRDATAFIELD field"
//        // + " INNER JOIN CMVIEWS.PDRDATACOLLECTION coll ON
//        // field.OFDATACOLLECTIONRID = coll.RID"
//        // + " INNER JOIN CMVIEWS.PDRDATAFILE pdfile ON coll.OFDATAFILERID =
//        // pdfile.RID"
//        // + " ) col_identity(df_host, df_database, df_schema, collection_name,
//        // datafield_name, datafield_desc, datafield_rid, collection_rid,
//        // isdbtable) "
//        // + " ON col_identity.datafield_rid = rc.DATAFIELDRID " + " LEFT OUTER
//        // JOIN " + " ("
//        // + " SELECT term.CLASSIFIEDOBJECTRID datafield_rid,
//        // LISTAGG(nvl(bgterm.NAME,'NA'), ',') terms"
//        // + " FROM IGVIEWS.IGASSIGNEDOBJECTSOFATERM term"
//        // + " INNER JOIN IGVIEWS.IGBUSINESSTERM bgterm ON bgterm.RID =
//        // term.BUSINESSTERMRID"
//        // + " GROUP BY term.CLASSIFIEDOBJECTRID" + " )
//        // term_assignets(bg_datafield_rid, bg_terms)"
//        // + " ON term_assignets.bg_datafield_rid = rc.DATAFIELDRID "
//        // + " WHERE cr.ADVANCEDRESULTS_XMETA IS NOT NULL "
//        // + (forProjectRId ? " AND rc.PROJECTRID = ? " : "")
//        // ;
//
//        // return "SELECT table_rid, col_rid, ca_result, table_name, col_name,
//        // database, schema, host, col_desc, bg_terms, modification_time FROM "
//        // + "("
//        // + " SELECT rc.TABLERID table_rid, rc.RID col_rid,
//        // cr.ADVANCEDRESULTS_XMETA CA_RESULT, "
//        // + " nvl(rc.TABLE,'NA') Table, rc.NAME, nvl(rc.DATABASE,'NA')
//        // Database, nvl(rc.SCHEMA,'NA') schema, nvl(dbhost.NAME,'NA') Host, "
//        // + " cast(nvl(cast(dbcol.LONGDESCRIPTION as VARCHAR(256)),
//        // dbcol.SHORTDESCRIPTION) AS VARCHAR(256)) as col_desc, "
//        // + " cr.XMETA_MODIFICATION_TIMESTAMP_XMETA modification_time"
//        // + " FROM XMETA.INVESTIGATE_COLUMNANALYSISRESULTS cr "
//        // + " INNER JOIN IAVIEWS.IAREGISTEREDCOLUMN rc ON
//        // cr.COLUMNANALYSISMASTER_XMETA=rc.RID "
//        // + " INNER JOIN CMVIEWS.PDRDATABASECOLUMN dbcol ON dbcol.RID =
//        // rc.DATAFIELDRID "
//        // + " LEFT OUTER JOIN CMVIEWS.PDRDATABASETABLE dbtab ON dbtab.RID =
//        // dbcol.OFDATABASETABLERID "
//        // + " LEFT OUTER JOIN CMVIEWS.PDRDATABASESCHEMA dbschema ON
//        // dbschema.RID = dbtab.OFDATASCHEMARID "
//        // + " LEFT OUTER JOIN CMVIEWS.PDRDATABASE dbase ON dbase.RID =
//        // dbschema.OFDATABASERID "
//        // + " LEFT OUTER JOIN CMVIEWS.PDRHOSTSYSTEM dbhost ON dbhost.RID =
//        // dbase.HOSTEDBYRID "
//        // + " WHERE ADVANCEDRESULTS_XMETA IS NOT NULL"
//        // + ") col_identity(table_rid, col_rid, ca_result, table_name,
//        // col_name, database, schema, host, col_desc, modification_time )"
//        // + " LEFT OUTER JOIN "
//        // + "("
//        // + " SELECT rc.TABLERID table_rid, rc.RID col_rid,
//        // LISTAGG(bgterm.NAME, ',') terms "
//        // + " FROM XMETA.INVESTIGATE_COLUMNANALYSISRESULTS cr "
//        // + " INNER JOIN IAVIEWS.IAREGISTEREDCOLUMN rc ON
//        // cr.COLUMNANALYSISMASTER_XMETA=rc.RID "
//        // + " INNER JOIN IGVIEWS.IGASSIGNEDOBJECTSOFATERM term ON
//        // term.CLASSIFIEDOBJECTRID = rc.DATAFIELDRID "
//        // + " INNER JOIN IGVIEWS.IGBUSINESSTERM bgterm ON bgterm.RID =
//        // term.BUSINESSTERMRID "
//        // + " WHERE ADVANCEDRESULTS_XMETA IS NOT NULL "
//        // + " GROUP BY rc.TABLERID, rc.RID "
//        // + ") term_assignets(bg_table_rid, bg_col_rid, bg_terms)"
//        // + " ON term_assignets.bg_col_rid = col_identity.col_rid AND
//        // term_assignets.bg_table_rid = col_identity.table_rid "
//        // ; // FETCH FIRST 10 ROWS ONLY";
//    }
//
//    public static JSONObject getCAResultWithColumnIdentity(ResultSet rs)
//            throws NullPointerException, JSONException, IOException,
//            SQLException {
//        JSONObject cAResults = new JSONObject();
//        // JSONArray terms = new JSONArray();
//        cAResults.put(Constants.CA_RESULTS_JSON,
//                XMetaUtils.getJSONFromClob(rs.getClob(3)));
//        JSONObject columnIdentity = new JSONObject();
//        columnIdentity.put(Constants.TABLE_RID, rs.getString(1));
//        columnIdentity.put(Constants.COLUMN_RID, rs.getString(2));
//        columnIdentity.put(Constants.TABLE_NAME, rs.getString(4));
//        columnIdentity.put(Constants.COLUMN_NAME, rs.getString(5));
//        columnIdentity.put(Constants.DATABASE_NAME, rs.getString(6));
//        columnIdentity.put(Constants.SCHEMA_NAME, rs.getString(7));
//        columnIdentity.put(Constants.HOST_NAME, rs.getString(8));
//        columnIdentity.put(Constants.COLUMN_DESC, rs.getString(9));
//        // String termsStr = rs.getString(10);
//        // if (termsStr != null) {
//        // String[] termArr = termsStr.split(",");
//        // for (String term : termArr) {
//        // terms.add(term);
//        // }
//        // }
//        // columnIdentity.put(Constants.TERM_ASSIGNMENTS, terms);
//        columnIdentity.put(Constants.IS_DB_TABLE, rs.getBoolean(11));
//
//        columnIdentity.put(Constants.QUALITY_SCORE, rs.getString(13));
//        columnIdentity.put(Constants.LAST_IMPORT, rs.getString(14));
//
//        cAResults.put(Constants.COLUMN_IDENTITY, columnIdentity);
//        return cAResults;
//    }
//
//    public static JSONObject getPIICDEResultsWithColumnIdentity(ResultSet rs)
//            throws NullPointerException, JSONException, IOException,
//            SQLException {
//        JSONObject columnIdentity = new JSONObject();
//
//        columnIdentity.put(Constants.WORKSPACE, rs.getString(1));
//        columnIdentity.put(Constants.DATABASE_NAME, rs.getString(2));
//        columnIdentity.put(Constants.SCHEMA_NAME, rs.getString(3));
//        columnIdentity.put(Constants.TABLE_NAME, rs.getString(4));
//        columnIdentity.put(Constants.QUALITY_SCORE_BENCHMARK, rs.getString(5));
//        columnIdentity.put(Constants.QUALITY_SCORE, rs.getString(6));
//        columnIdentity.put(Constants.TYPE, rs.getString(7));
//        columnIdentity.put(Constants.DATA_ELEMENT_RID, rs.getString(8));
//        columnIdentity.put(Constants.WORKSPACE_RID, rs.getString(9));
//
//        return columnIdentity;
//    }
//
//    // public static JSONArray getDataRulesNormalizedOutput(ResultSet rs) throws
//    // NullPointerException, JSONException, IOException, SQLException {
//    // JSONArray dataRulesNormalizedResults = new JSONArray();
//    // JSONObject terms = new JSONObject();
//    // cAResults.put(Constants.CA_RESULTS_JSON,
//    // XMetaUtils.getJSONFromClob(rs.getClob(3)));
//    // JSONObject columnIdentity = new JSONObject();
//    // columnIdentity.put(Constants.TABLE_RID, rs.getString(1));
//    // columnIdentity.put(Constants.COLUMN_RID, rs.getString(2));
//    // columnIdentity.put(Constants.TABLE_NAME, rs.getString(4));
//    // columnIdentity.put(Constants.COLUMN_NAME, rs.getString(5));
//    // columnIdentity.put(Constants.DATABASE_NAME, rs.getString(6));
//    // columnIdentity.put(Constants.SCHEMA_NAME, rs.getString(7));
//    // columnIdentity.put(Constants.HOST_NAME, rs.getString(8));
//    // columnIdentity.put(Constants.COLUMN_DESC, rs.getString(9));
//    // String termsStr = rs.getString(10);
//    // if (termsStr != null) {
//    // String[] termArr = termsStr.split(",");
//    // for (String term: termArr) {
//    // terms.add(term);
//    // }
//    // }
//    // columnIdentity.put(Constants.TERM_ASSIGNMENTS, terms);
//    // columnIdentity.put(Constants.IS_DB_TABLE, rs.getBoolean(11));
//    //
//    // cAResults.put(Constants.COLUMN_IDENTITY, columnIdentity);
//    // return cAResults;
//    // }
//
    /**
     * Query for Suggested Data Rules *The asset* has the term
     * 
     * @return sql query string
     */
    public static String getDataRulesQuery() {
        return "select RB.RULERID AS RuleRID, RD.NAME AS Actions, BT.NAME AS Terms, BT.RID AS TermRID, RD.RID AS ActionsRID "
                + "FROM ((IAVIEWS.IARULEDEFINITION RD INNER JOIN IAVIEWS.IARULEBINDING RB ON RB.RULEDEFINITIONRID = RD.RID)"
                + "INNER JOIN IGVIEWS.IGASSIGNEDOBJECTSOFATERM AOT ON RB.TARGETRID = AOT.CLASSIFIEDOBJECTRID)"
                + "INNER JOIN IGVIEWS.IGBUSINESSTERM BT ON BT.RID = AOT.BUSINESSTERMRID AND RB.RULERID IS NOT NULL";
    }
//
//    /**
//     * Query for Suggested Data Rules for The name of *the asset*
//     * 
//     * @return sql query string
//     */
//
//    public static String getDataRulesForAsset() {
//        return "select RB.RULERID AS RuleRID, RD.NAME AS Actions, rc.NAME columnOrFieldName, RD.RID AS ActionsRID "
//                + "  FROM IAVIEWS.IARULEDEFINITION RD INNER JOIN IAVIEWS.IARULEBINDING RB ON RB.RULEDEFINITIONRID = RD.RID"
//                + "  INNER JOIN IAVIEWS.IAREGISTEREDCOLUMN rc ON RB.TARGETRID = rc.RID"
//                + "  WHERE RB.RULERID IS NOT NULL";
//    }

    /**
     * Query for Suggested Data Rules for *The asset* has the label
     * 
     * @return sql query string
     */

    public static String getDataRulesForLabel() {
        return "select RB.RULERID AS RuleRID, RD.NAME AS Actions, label.NAME AS Labels, RD.RID AS ActionsRID, label.RID AS labelRID "
                + "FROM IAVIEWS.IARULEDEFINITION RD INNER JOIN IAVIEWS.IARULEBINDING RB ON RB.RULEDEFINITIONRID = RD.RID "
                + "INNER JOIN IAVIEWS.IAREGISTEREDCOLUMN rc ON RB.TARGETRID = rc.RID "
                + "INNER JOIN IGVIEWS.IGLABELREFERENCESCOMMONOBJECT LRCOBJ ON rc.DATAFIELDRID = LRCOBJ.LABELEDOBJECTRID "
                + "INNER JOIN IGVIEWS.IGLABEL label ON LRCOBJ.LABELRID = label.RID "
                + "WHERE RB.RULERID IS NOT NULL";
    }

    /**
     * Query for Suggested Data Rules for *The asset* has the name
     * 
     * @return sql query string
     */

    public static String getDataRulesForName() {
        return "select RB.RULERID AS RuleRID, RD.NAME AS Actions, rc.NAME columnOrFieldName, RD.RID AS ActionsRID "
                + "FROM IAVIEWS.IARULEDEFINITION RD INNER JOIN IAVIEWS.IARULEBINDING RB ON RB.RULEDEFINITIONRID = RD.RID "
                + "INNER JOIN IAVIEWS.IAREGISTEREDCOLUMN rc ON RB.TARGETRID = rc.RID "
                + "WHERE RULERID IS NOT NULL";
    }

    /**
     * Query for Suggested Data Rules for *The asset* is classified by the data
     * class
     * 
     * @return sql query string
     */

    public static String getDataRulesForDataClass() {
        return "select RB.RULERID AS RuleRID, RD.NAME AS Actions, dataclass.NAME AS DATACLASS, dataclass.CLASSCODE, RD.RID AS ActionsRID, dataclass.RID AS dataClassRID "
                + "  FROM IAVIEWS.IARULEDEFINITION RD INNER JOIN IAVIEWS.IARULEBINDING RB ON RB.RULEDEFINITIONRID = RD.RID"
                + "  INNER JOIN IAVIEWS.IAREGISTEREDCOLUMN rc ON RB.TARGETRID = rc.RID"
                + "  INNER JOIN CMVIEWS.DQCLASSIFICATION classification ON rc.RID = classification.CLASSIFIESOBJECTRID"
                + "  INNER JOIN CMVIEWS.DQDATACLASS dataclass ON classification.OFDATACLASSRID = dataclass.RID"
                + "  WHERE RB.RULERID IS NOT NULL AND dataclass.CLASSCODE IS NOT NULL ";
    }

    /**
     * Query for Suggested Data Rules for *The asset* has a term with `the
     * attribute` which has `the value`
     * 
     * @return sql query string
     */

    public static String getDataRulesForTermHasAttribute() {
        return "select RB.RULERID AS RuleRID, RD.NAME AS Actions, BT.NAME AS Terms, BT.RID AS TermRID, cadef.NAME AS Attribute, caval.VALUE AS AttrVARGRAPHICValue, RD.RID AS ActionsRID, cadef.RID AS AttributeRID"
                + "  FROM IAVIEWS.IARULEDEFINITION RD INNER JOIN IAVIEWS.IARULEBINDING RB ON RB.RULEDEFINITIONRID = RD.RID"
                + "  INNER JOIN IGVIEWS.IGASSIGNEDOBJECTSOFATERM AOT ON RB.TARGETRID = AOT.CLASSIFIEDOBJECTRID"
                + "  INNER JOIN IGVIEWS.IGBUSINESSTERM BT ON BT.RID = AOT.BUSINESSTERMRID"
                + "  INNER JOIN IAVIEWS.IAREGISTEREDCOLUMN rc ON RB.TARGETRID = rc.RID"
                + "  INNER JOIN CMVIEWS.COMMONCUSTOMATTRIBUTESTRINGVAL caval ON caval.OBJECTRID = BT.RID AND caval.OFCUSTOMARRAYVALRID IS NULL"
                + "  INNER JOIN CMVIEWS.COMMONCUSTOMATTRIBUTE cadef ON caval.OFCUSTOMATTRIBUTERID = cadef.RID"
                + "  WHERE RB.RULERID IS NOT NULL ";
    }

    /**
     * Query for Suggested Data Rules for *The asset* has a term with `the
     * attribute` which has `the value`
     * 
     * @return sql query string
     */

    public static String getDataRulesForAssetHasAttribute() {
        return "select RB.RULERID AS RuleRID, RD.NAME AS Actions, cadef.NAME AS Attribute, caval.VALUE AS AttrVARGRAPHICValue, RD.RID AS ActionsRID, cadef.RID AS AttributeRID "
                + "  FROM IAVIEWS.IARULEDEFINITION RD INNER JOIN IAVIEWS.IARULEBINDING RB ON RB.RULEDEFINITIONRID = RD.RID"
                + "  INNER JOIN IAVIEWS.IAREGISTEREDCOLUMN rc ON RB.TARGETRID = rc.RID"
                + "  INNER JOIN CMVIEWS.COMMONCUSTOMATTRIBUTESTRINGVAL caval ON caval.OBJECTRID = rc.DATAFIELDRID AND caval.OFCUSTOMARRAYVALRID IS NULL"
                + "  INNER JOIN CMVIEWS.COMMONCUSTOMATTRIBUTE cadef ON caval.OFCUSTOMATTRIBUTERID = cadef.RID"
                + "  WHERE RB.RULERID IS NOT NULL ";
    }

//    /**
//     * Query for Suggested Stewards Rules
//     * 
//     * @return sql query string
//     */
//    public static String getStewardsQuery() {
//        return "SELECT t.NAME AS TERM_NAME, t.RID AS TERM_RID, c.NAME AS TERM_CATEGORY, c.RID AS TERM_CATEGORY_RID, u.PRINCIPALID AS TERM_STEWARD, u.RID AS TERM_STEWARD_RID "
//                + "FROM IGVIEWS.IGBUSINESSTERM t "
//                + "INNER JOIN IGVIEWS.IGBUSINESSCATEGORY c ON t.OFCATEGORY = c.RID "
//                + "INNER JOIN CMVIEWS.STEWARDSHIPASSIGNMENT a ON a.ASSIGNEDFORCOMMONOBJECTRID = t.RID "
//                + "INNER JOIN CMVIEWS.STEWARDUSER u ON a.ASSIGNSPRINCIPALRID = u.RID ";
//    }
//
//    /**
//     * Query for Custom Attribute PII/CDE
//     * 
//     * @return sql query string
//     */
//    public static String getPIICDEQuery() {
//        return "SELECT proj.NAME AS WORKSPACE, t.\"DATABASE\" DATABASE_NAME, t.\"SCHEMA\" SCHEMA_NAME, t.NAME AS TABLE_NAME, ltqh.QUALITYSCOREBENCHMARK, ltqh.QUALITYSCORE, 'TABLE' AS TYPE, t.DATACOLLECTIONRID AS DATA_ELEMENT_RID, proj.RID WORKSPACE_RID "
//                + "FROM IAVIEWS.IAREGISTEREDTABLE t " + "INNER JOIN ( "
//                + "SELECT val.OBJECTRID as RID "
//                + "FROM CMVIEWS.COMMONCUSTOMATTRIBUTESTRINGVAL val "
//                + "INNER JOIN CMVIEWS.COMMONCUSTOMATTRIBUTE attr "
//                + "ON val.OFCUSTOMATTRIBUTERID = attr.RID WHERE UPPER(attr.NAME) = ? and val.VALUE = 'True' "
//                + ") ca " + "ON t.DATACOLLECTIONRID = ca.RID "
//                + "INNER JOIN IAVIEWS.IAPROJECT proj "
//                + "ON proj.RID=t.PROJECTRID "
//                + "INNER JOIN IAVIEWS.IATABLEQUALITYHISTORY ltqh "
//                + "ON ltqh.STARTTIME = t.DATAQUALITYANALYSISDATE "
//                + "WHERE ltqh.QUALITYSCOREBENCHMARK > ltqh.QUALITYSCORE ";
//    }
//
//    // Query to get terms
//    public static String getTermsQuery() {
//        return "SELECT t.RID AS TERM_RID,t.NAME AS TERM_NAME,c.RID AS CATEGORY_RID,c.NAME AS CATEGORY_NAME "
//                + "FROM IGVIEWS.IGBUSINESSTERM t  "
//                + "INNER JOIN IGVIEWS.IGBUSINESSCATEGORY c ON t.OFCATEGORY = c.RID ";
//    }
//
//    // Query to get term details
//    public static String getTermDetailsQuery() {
//        return "SELECT t.RID AS TERM_RID, t.NAME AS TERM_NAME, t.OFCATEGORY AS CATEGORY_RID, u.RID AS TERM_STEWARD_RID "
//                + "FROM IGVIEWS.IGBUSINESSTERM t LEFT JOIN CMVIEWS.STEWARDSHIPASSIGNMENT a ON a.ASSIGNEDFORCOMMONOBJECTRID = t.RID  "
//                + "LEFT JOIN CMVIEWS.STEWARDUSER u ON a.ASSIGNSPRINCIPALRID = u.RID ";
//    }
//
//    // Query to get steward id and steward rid
//    public static JSONObject getStewards(Connection dbcon) {
//        String stewardsQuery = "SELECT u.RID AS STEWARD_RID, u.PRINCIPALID AS STEWARD_ID FROM CMVIEWS.STEWARDUSER u";
//        PreparedStatement stmt = getPreparedStatement(dbcon, stewardsQuery);
//        ResultSet rs = executeStmtWithParam(stmt, null);
//
//        JSONObject stewardsDict = new JSONObject();
//        try {
//            while (rs != null && rs.next()) {
//                stewardsDict.put(rs.getString("STEWARD_RID"),
//                        rs.getString("STEWARD_ID"));
//            }
//        } catch (SQLException | JSONException e) {
//            LOGGER.warn("Error while getStewards", e);
//        }
//        closePreparedStatement(stmt);
//
//        return stewardsDict;
//    }
//
//    public static JSONObject getCategories(Connection dbcon) {
//
//        String getCategoriesSQL = "SELECT RID, NAME, SUPERCATEGORYRID FROM IGVIEWS.IGBUSINESSCATEGORY";
//
//        PreparedStatement stmt = getPreparedStatement(dbcon, getCategoriesSQL);
//        ResultSet rs = executeStmtWithParam(stmt, null);
//
//        JSONObject categoriesDict = new JSONObject();
//        List<String> value = new ArrayList<String>();
//        JSONObject categoryHierarchyDict = new JSONObject();
//
//        try {
//            while (rs != null && rs.next()) {
//                // String rulerid = rs.getString("RID");
//                value.add(rs.getString("NAME"));
//                value.add(rs.getString("SUPERCATEGORYRID"));
//                categoriesDict.put(rs.getString("RID"), value);
//                value = new ArrayList<String>();
//            }
//            closePreparedStatement(stmt);
//            PreparedStatement stmt1 = getPreparedStatement(dbcon,
//                    getCategoriesSQL);
//            rs = executeStmtWithParam(stmt1, null);
//            while (rs != null && rs.next()) {
//                String catRid = rs.getString("RID");
//                String catName = rs.getString("NAME");
//                String parentRid = rs.getString("SUPERCATEGORYRID");
//                JSONArray contextPathList = createCategoryPath(categoriesDict,
//                        catRid, catName, parentRid);
//                String categoryPath = "";
//                for (int len = contextPathList.size() - 1; len >= 0; len--) {
//                    categoryPath += contextPathList.get(len);
//
//                }
//                categoryHierarchyDict.put(rs.getString("RID"), categoryPath);
//            }
//            closePreparedStatement(stmt1);
//
//        } catch (SQLException | JSONException e) {
//            LOGGER.warn("Error while getCategories", e);
//        }
//
//        return categoryHierarchyDict;
//    }
//
//    // Recursively build the category path
//    public static JSONArray createCategoryPath(JSONObject categoriesDict,
//            String catRid, String catName, String parentRid) {
//        JSONArray contextPathList = new JSONArray();
//
//        if (parentRid == null) {
//            contextPathList.add(catName);
//            return contextPathList;
//        }
//        contextPathList.add('/' + catName);
//        catRid = parentRid;
//        // JSONArray test = new JSONArray();
//        try {
//            catName = categoriesDict.getJSONArray(catRid).getString(0);
//            // test = categoriesDict.getJSONArray(catRid);
//            if (categoriesDict.getJSONArray(catRid).get(1) == null)
//                parentRid = null;
//            else
//                parentRid = categoriesDict.getJSONArray(catRid).getString(1);
//            // parentRid = categoriesDict.getJSONArray(catRid).getString(1);
//        } catch (JSONException e) {
//            LOGGER.warn("Error while createCategoryPath", e);
//        }
//
//        return createCategoryPath(categoriesDict, catRid, catName, parentRid);
//    }
//
//    public static Connection getDSConnection(XADataSource ds) {
//        XAConnection xaconnection = null;
//        try {
//            xaconnection = ds.getXAConnection();
//            return xaconnection.getConnection();
//        } catch (SQLException e) {
//            LOGGER.error("Got Error while getDSConnection", e);
//        }
//        return null;
//    }
//
//    public static JSONArtifact getJSONFromClob(Clob clob)
//            throws NullPointerException, IOException, SQLException,
//            JSONException {
//        if (clob != null) {
//            String str = clob.getSubString(1, (int) clob.length());
//            JSONArtifact jsonStr = JSON.parse(str);
//            if (jsonStr instanceof JSONObject) {
//                return ((JSONObject) jsonStr);
//            } else {
//                return ((JSONArray) jsonStr);
//            }
//        }
//        return null;
//    }
//
//    public static JSONObject generateJSONResponseForStewards(
//            Output<NamedItem> inputData) {
//
//        JSONObject result = new JSONObject();
//
//        JSONArray jsonSuggestedStewardsList = new JSONArray();
//        RuleSet<NamedItem> ruleSet = inputData.getRuleSet();
//        Iterator<AssociationRule<NamedItem>> ruleSetItr = ruleSet.iterator();
//
//        while (ruleSetItr.hasNext()) {
//
//            AssociationRule<NamedItem> associatedRule = ruleSetItr.next();
//            double conf = (new Confidence()).evaluate(associatedRule);
//            // double lift = (new Lift()).evaluate(associatedRule);
//            double support = associatedRule.getSupport();
//            String source = "";
//            String target = "";
//            String head = associatedRule.getHead().toString();
//            String body = associatedRule.getBody().toString();
//
//            if (head.contains("TERM_CATEGORY_RID")) {
//                continue;
//            }
//            source = body.substring(body.indexOf(":") + 1);
//            source = source.substring(0, source.indexOf("]"));
//
//            target = head.substring(head.indexOf(":") + 1);
//            target = target.substring(0, target.indexOf("]"));
//
//            source = source.trim();
//            target = target.trim();
//
//            try {
//
//                jsonSuggestedStewardsList.add(new JSONObject()
//                        .put("source", source).put("target", target)
//                        .put("confidence", conf).put("support", support));
//            } catch (JSONException e) {
//                LOGGER.error(
//                        "Got Error while generateJSONResponseForStewards", e);
//            }
//
//        }
//
//        try {
//            result.put("result", jsonSuggestedStewardsList);
//        } catch (JSONException e) {
//            LOGGER.error(
//                    "Got Error while generateJSONResponseForStewards-2", e);
//        }
//
//        return result;
//
//    }

    // Create prepared statement
    public static PreparedStatement getPreparedStatement(Connection dbcon,
            final String sql) {
        if (dbcon != null) {
            PreparedStatement stmt = null;
            try {
                stmt = dbcon.prepareStatement(sql);
                return stmt;
            } catch (SQLException e) {
                LOGGER.error("Got Error while executeQuery", e);
            }
        }
        return null;
    }

    // Execute prepared statement
    public static ResultSet executeStmtWithParam(PreparedStatement stmt,
            String param1) {
        if (stmt != null) {
            try {
                if (param1 != null) {
                    stmt.setString(1, param1.toUpperCase());
                }
                return stmt.executeQuery();
            } catch (SQLException e) {
                LOGGER.error("Got Error while executeQuery", e);
            }
        }
        return null;
    }

//    // Execute prepared statement
//    public static ResultSet executeStmtWithStringParam(PreparedStatement stmt,
//            String param1) {
//        if (stmt != null) {
//            try {
//                if (param1 != null) {
//                    stmt.setString(1, param1);
//                }
//                return stmt.executeQuery();
//            } catch (SQLException e) {
//                LOGGER.error("Got Error while executeQuery", e);
//            }
//        }
//        return null;
//    }

    // Close prepared statement
    public static void closePreparedStatement(PreparedStatement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                LOGGER.error("Got Error while executeQuery", e);
            }
        }
    }

//    public static JSONObject getWorkSpaceInfo(ResultSet rs)
//            throws JSONException, SQLException {
//
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put(Constants.WORKSPACE_RID, rs.getString(1));
//        jsonObject.put(Constants.WORKSPACE, rs.getString(2));
//        return jsonObject;
//    }
//
}
