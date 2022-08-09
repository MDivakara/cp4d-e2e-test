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
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.gov.ms.common.InsightsConfiguration;
import com.mit.gov.ms.model.SwaggerConstants;
import com.mit.gov.ms.rest.providers.InsightsDefaultExceptionMapper;
import com.mit.gov.ms.rest.resources.InsightsBuildVersion;
//import com.mit.gov.ms.rest.resources.VersionResourceV3;

//import io.swagger.annotations.Info;
//import io.swagger.annotations.SwaggerDefinition;
//import io.swagger.annotations.Tag;
//
//@SwaggerDefinition(info = @Info(title = "Insights API v3", version = "0.0.3", description = "Provides insights on data quality"), tags = {
//    @Tag(name = SwaggerConstants.INSIGHTS_API_VERSION_V3, description = "APIs for Insights version."),
//    @Tag(name = SwaggerConstants.INSIGHTS_HEALTH_V3, description = "APIs for Insights Service Health Status."),
//    @Tag(name = SwaggerConstants.INSIGHTS_BUILD_VERSION, description = "APIs for Insights Build Number."),
//    @Tag(name = SwaggerConstants.INSIGHTS_SCA, description = "APIs for Similar Columns Analysis."),
//    @Tag(name = SwaggerConstants.INSIGHTS_MODEL, description = "APIs for Model Management."),
//    @Tag(name = SwaggerConstants.INSIGHTS_ANOMALY_DETECTION, description = "APIs for Anomaly Detection."),
//    @Tag(name = SwaggerConstants.INSIGHTS_DRD, description = "APIs for Data Rule Definitions."),
//    @Tag(name = SwaggerConstants.INSIGHTS_TASK, description = "APIs for Task Management.")
//})
@ApplicationPath("/v3/test-suit")
public class InsightsJAXRSApplication extends Application {

    private final static Logger LOGGER = LoggerFactory.getLogger(InsightsJAXRSApplication.class);

    
    private transient ManagedScheduledExecutorService executor = null;

    @SuppressWarnings("unused")
    private ScheduledFuture<?> insightStask = null;
    
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
//            noauthPaths.add("build");
//            noauthPaths.add("models/*");
            //context.register(filter);
            return true;
        }

    }

   
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
        //resources.add(VersionResourceV3.class);
        
        resources.add(InsightsBuildVersion.class);
        
        resources.add(AuthenticationFeature.class);
        resources.add(ExceptionHandlersFeature.class);
        resources.add(SerializationFeature.class);
        return resources;
    }

}
