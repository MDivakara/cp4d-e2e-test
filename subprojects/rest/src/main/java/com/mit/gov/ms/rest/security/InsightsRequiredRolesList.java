/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.rest.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.NameBinding;


/** Use to specify required roles for accessing APIs
 *Use @InsightsRequiredRolesList({@InsightsRequiredRoles({Role 1 ,Role 2})}) if user has to have role 1 or role 2
 *Use @InsightsRequiredRolesList({@InsightsRequiredRoles({Role 1}) ,@InsightsRequiredRoles({Role 2})}) if user has to have role 1 and role 2 
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.TYPE,
    ElementType.METHOD
})
public @interface InsightsRequiredRolesList {

    InsightsRequiredRoles[] value();

}
