/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.rest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.gov.ms.common.CatalogsMetaDataStore;
import com.mit.gov.ms.common.Constants;
import com.mit.gov.ms.common.InsightsConfiguration;
import com.mit.gov.ms.common.InsightsException;
import com.mit.gov.ms.datarules.DataRulesResultManager;
import com.mit.gov.ms.model.SwaggerConstants;
import com.mit.gov.ms.rest.providers.InsightsDefaultExceptionMapper;
import com.mit.gov.ms.rest.resources.VersionResourceV1;

import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

@SwaggerDefinition(info = @Info(title = "Insights API v3", version = "0.0.3", description = "Provides insights on data quality"), tags = {
    @Tag(name = SwaggerConstants.INSIGHTS_API_VERSION, description = "APIs for Insights version v1."),
    @Tag(name = SwaggerConstants.INSIGHTS_HEALTH, description = "APIs for Insights Service Health Status v1."),
    @Tag(name = SwaggerConstants.INSIGHTS_SAR, description = "APIs for Suggested Automation Rules.")
})
@ApplicationPath("/ibm/iis/api/insights/v1")
public class InsightsJAXRSApplicationSAR extends Application {

    private final static Logger LOGGER = LoggerFactory.getLogger(InsightsJAXRSApplicationSAR.class);


    private transient ManagedScheduledExecutorService executor = null;

    @SuppressWarnings("unused")
    private ScheduledFuture<?> SARtask = null;
    
    

    @Provider
    public static class AuthenticationFeature implements Feature {

        @Override
        public boolean configure(FeatureContext context) {
            InsightsConfiguration configuration = InsightsConfiguration.getInstance();
//            JWTAuthFilter filter = new InsightsAuthFilter();
//            filter.setNoauthEnabled(configuration.isNoAuthEnabled());
//            List<String> noauthPaths = filter.getNoauthPaths();
//            noauthPaths.add("/");
//            noauthPaths.add("health");
//            context.register(filter);
            return true;
        }

    }

//    @Provider
//    public static class AuthorizationFeature implements Feature {
//
//        @Override
//        public boolean configure(FeatureContext context) {
//            InsightsAuthorizationFilter filter = new InsightsAuthorizationFilter();
//            context.register(filter);
//            return true;
//        }
//
//    }

    @Provider
    public static class ExceptionHandlersFeature implements Feature {

        public ExceptionHandlersFeature() {
            super();
        }

        @Override
        public boolean configure(FeatureContext context) {
            //context.register(InsightsRESTServiceExceptionMapper.class, 1);
            context.register(InsightsDefaultExceptionMapper.class, 2);
            return true;
        }

    }

    @Provider
    public static class SerializationFeature implements Feature {

        public SerializationFeature() {
            super();
        }

        @Override
        public boolean configure(FeatureContext context) {
//            GsonSerializationFactory gsonFactory = new GsonSerializationFactory();
//            GsonSerializationProvider provider = new GsonSerializationProvider(gsonFactory.getGson());
//            context.register(provider);
            return true;
        }

    }

    @Override
    public Set<Class<?>> getClasses() {
        return getResourceClasses();
    }
    
    public static Set<Class<?>> getResourceClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(VersionResourceV1.class);
        
        resources.add(AuthenticationFeature.class);
        //resources.add(AuthorizationFeature.class);
        resources.add(ExceptionHandlersFeature.class);
        resources.add(SerializationFeature.class);
        return resources;
    }

}
