/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class IISRoleHelper {

    private IISRoleHelper() {
        super();
    }



    public static boolean hasAnyRole(List<String> userRoles, List<IISRole> requiredRoles) {
        List<String> roleNames = new ArrayList<String>();
        for (IISRole role : requiredRoles) {
            roleNames.add(role.getRoleName());
        }
        return !Collections.disjoint(userRoles, roleNames);
    }
    
    public static boolean hasRole(List<String> userRoles, IISRole requiredRole) {
        return userRoles.contains(requiredRole.getRoleName());
    }

}
