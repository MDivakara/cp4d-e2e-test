/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.security;

import java.util.EnumMap;

public class IISRoleDescriprtion {

    private static EnumMap<IISRole, String> roleDescription = new EnumMap<IISRole, String>(IISRole.class);

    static {
        IISRoleDescriprtion.roleDescription.put(IISRole.SUITE_USER, "Suite User");
        IISRoleDescriprtion.roleDescription.put(IISRole.SUITE_ADMIN, "Suite Administrator");
        IISRoleDescriprtion.roleDescription.put(IISRole.GLOSSARY_USER, "Information Governance Catalog User"); //??
        IISRoleDescriprtion.roleDescription.put(IISRole.GLOSSARY_AUTHOR, "Information Governance Catalog Glossary Author");
        IISRoleDescriprtion.roleDescription.put(IISRole.GLOSSARY_ADMIN, "Information Governance Catalog Glossary Administrator");
        IISRoleDescriprtion.roleDescription.put(IISRole.GLOSSARY_BASIC_USER, "Information Governance Catalog Glossary Basic User");
        IISRoleDescriprtion.roleDescription.put(IISRole.GLOSSARY_ASSIGNER, "Information Governance Catalog Information Asset Assigner"); //??
        IISRoleDescriprtion.roleDescription.put(IISRole.GLOSSARY_REVIEWER, "Information Governance Catalog Glossary Reviewer");
        IISRoleDescriprtion.roleDescription.put(IISRole.MDW_USER, "Metadata Woekbench User");
        IISRoleDescriprtion.roleDescription.put(IISRole.MDW_ADMINISTRATOR, "Metadata Woekbench Administrator");
        IISRoleDescriprtion.roleDescription.put(IISRole.CM_IMPORTER, "Common Metadata Importer");
        IISRoleDescriprtion.roleDescription.put(IISRole.CM_ADMIN, "Common Metadata Administrator");
        IISRoleDescriprtion.roleDescription.put(IISRole.DATA_PREVIEW_USER, "Data Preview Service User");
        IISRoleDescriprtion.roleDescription.put(IISRole.IA_DATA_ADMINISTRATOR, "Information Analyzer Data Administrator");
        IISRoleDescriprtion.roleDescription.put(IISRole.IA_PROJECT_ADMINISTRATOR, "Information Analyzer Project Administrator");
        IISRoleDescriprtion.roleDescription.put(IISRole.IA_USER, "Information Analyzer User");
        IISRoleDescriprtion.roleDescription.put(IISRole.IA_RULES_ADMINISTRATOR, "Rules Administrator");
        IISRoleDescriprtion.roleDescription.put(IISRole.IA_RULES_AUTHOR, "Rules Author");
        IISRoleDescriprtion.roleDescription.put(IISRole.IA_RULES_MANAGER, "Rules Manager");
        IISRoleDescriprtion.roleDescription.put(IISRole.IA_RULES_USER, "Rules User");
        IISRoleDescriprtion.roleDescription.put(IISRole.BG_ADMINISTRATOR, "Information Governance Catalog Glossary Administrator");
        IISRoleDescriprtion.roleDescription.put(IISRole.BG_AUTHOR, "Information Governance Catalog Glossary Author");
        IISRoleDescriprtion.roleDescription.put(IISRole.BG_BASIC_USER, "Information Governance Catalog Glossary Basic User");
        IISRoleDescriprtion.roleDescription.put(IISRole.BG_USER, "Information Governance Catalog User");
    }
    
    public static String getIISRoleDescription(IISRole role) {
        return roleDescription.get(role);
    }
}
