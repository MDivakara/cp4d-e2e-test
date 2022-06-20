/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.security;

public enum IISRole {

    SUITE_USER("SuiteUser"),
    SUITE_ADMIN("SuiteAdmin"),
    GLOSSARY_USER("GlossaryUser"),
    GLOSSARY_AUTHOR("GlossaryAuthor"),
    GLOSSARY_ADMIN("GlossaryAdmin"),
    GLOSSARY_BASIC_USER("GlossaryBasicUser"),
    GLOSSARY_ASSIGNER("GlossaryAssigner"),
    GLOSSARY_REVIEWER("GlossaryReviewer"),
    MDW_USER("MDWUser"),
    MDW_ADMINISTRATOR("MDWAdministrator"),
    CM_IMPORTER("CMImporter"),
    CM_ADMIN("CMAdmin"),
    DATA_PREVIEW_USER("DataPreviewUser"),
    IA_DATA_ADMINISTRATOR("SorcererDataAdmin"),
    IA_PROJECT_ADMINISTRATOR("SorcererAdmin"),
    IA_USER("SorcererUser"),
    IA_RULES_ADMINISTRATOR("RulesAdministrator"),
    IA_RULES_AUTHOR("RulesAuthor"),
    IA_RULES_MANAGER("RulesManager"),
    IA_RULES_USER("RulesUser"),
    BG_ADMINISTRATOR("GlossaryAdmin"),
    BG_AUTHOR("GlossaryAuthor"),
    BG_BASIC_USER("GlossaryBasicUser"),
    BG_USER("GlossaryUser");


    private String roleName;

    IISRole(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

}