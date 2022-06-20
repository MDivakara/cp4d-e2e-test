FROM hyc-ug-releases-docker-local.artifactory.swg-devops.com/websphere-liberty-ubi8-x86_64:latest
MAINTAINER IBM

# install as root	
USER root

# create server and install necessary liberty extensions
RUN set -eux \
	&& server create gov_insights_service \
    && ln -sfn $WLP_USER_DIR/../output/gov_insights_service /output \
    && ln -sfn $WLP_USER_DIR/servers/gov_insights_service /config

# copy server configuration
COPY build/docker/server.xml \
     build/docker/jvm.options \
     build/docker/log4j2.xml \
     /config/
COPY build/docker/configDropins /config/configDropins

# copy application files
COPY build/docker/lib/ /config/lib/insights
COPY build/docker/apps/*.war /config/apps/

#copy scripts
COPY scripts/*.sh /

# set ownership for non-root user
RUN set -eux \
	&& mkdir -p /config/configDropins/defaults \
	&& mkdir -p /config/config \
	&& find -L /config -type d -exec chmod o+rx {} \; \
	&& find -L /config -type f -exec chmod o+r {} \; \
	&& chown -RL liberty:liberty /config 

#install utilities 
RUN /opt/ibm/wlp/bin/installUtility install --acceptLicense gov_insights_service

# run as non-root
USER liberty
	
# expose ports
EXPOSE 9080

# set up entry point and default command
ENTRYPOINT [ "/docker-entrypoint.sh" ]
CMD [ "/start_service.sh" ]
