#!/bin/bash

GIT_PATH=`pwd`
echo "Current directory - $GIT_PATH"
ls -alh

if [ -z "$IIS_IMAM_MN_SERVER" ]; then
   export IIS_IMAM_MN_SERVER=$IIS_HOSTNAME
fi

export TEST_SUFFIX=$(date +%s)

#echo $APT_PM_CONDUCTOR_HOSTNAME
#if [ "$MY_POD_NAME" == "is-en-conductor-0" ]; then
# export IIS_HOSTNAME=${IS_SERVICESDOCKER_SERVICE_HOST}
# export IIS_PORT=${IS_SERVICESDOCKER_SERVICE_PORT}
# export IIS_USER="isadmin"
# export IIS_PASS=${ISADMIN_PASSWORD}
# export IIS_IMAM_MN_SERVER="is-en-conductor-0.en-cond"
#fi

echo "### IIS/UG Details ###"
echo "IIS_HOSTNAME=${IIS_HOSTNAME}"
echo "IIS_PORT=${IIS_PORT}"
echo "IIS_USERNAME=${IIS_USERNAME}"
echo "IIS_PASSWORD=${IIS_PASSWORD}"
echo "IIS_HOME=${IIS_HOME}"
echo "IIS_IMAM_MN_SERVER=${IIS_IMAM_MN_SERVER}"
echo "UG_HOSTNAME=${UG_HOSTNAME}"
echo "UG_PORT=${UG_PORT}"

echo "### IIS/UG DB Details ###"
echo "INSIGHTS_DB_TYPE=${INSIGHTS_DB_TYPE}"
echo "INSIGHTS_DB_HOST=${INSIGHTS_DB_HOST}"
echo "INSIGHTS_DB_PORT=${INSIGHTS_DB_PORT}"
echo "INSIGHTS_DB_USER=${INSIGHTS_DB_USER}"
echo "INSIGHTS_DB_PASSWORD=${INSIGHTS_DB_PASSWORD}"
echo "INSIGHTS_DB_NAME=${INSIGHTS_DB_NAME}"

echo "### Test Data DB Details ###"
echo "CLEAN_RUN=${CLEAN_RUN}"
echo "TEST_SUFFIX=${TEST_SUFFIX}"
echo "TEST_DATABASE_HOST=${TEST_DATABASE_HOST}"
echo "TEST_DATABASE_PORT=${TEST_DATABASE_PORT}"
echo "TEST_DATABASE_NAME=${TEST_DATABASE_NAME}"
echo "TEST_DATABASE_USER_NAME=${TEST_DATABASE_USER_NAME}"
echo "TEST_DATABASE_SCHEMA=${TEST_DATABASE_SCHEMA}"
echo "TEST_DATABASE_PASSWORD=${TEST_DATABASE_PASSWORD}"

xmlescape() {
  echo "$1" | sed 's/&/\&amp;/g;s/</\&lt;/g;s/>/\&gt;/g;s/"/\&quot;/g;s/'"'"'/\&#39;/g'
}

# test data resources/files
cd $GIT_PATH/subprojects/test/src/main/resources
pwd
sed -i "s|DATABASE_USER_NAME|$TEST_DATABASE_USER_NAME|g" *.xml
sed -i "s|DATABASE_PASSWORD|$TEST_DATABASE_PASSWORD|g" *.xml
sed -i "s|DATABASE_NAME|$TEST_DATABASE_NAME|g" *.xml
sed -i "s|DATABASE_SCHEMA|$TEST_DATABASE_SCHEMA|g" *.xml
sed -i "s|DATABASE_HOST|$TEST_DATABASE_HOST|g" *.xml
sed -i "s|DATABASE_PORT|$TEST_DATABASE_PORT|g" *.xml
sed -i "s|DATABASE_MNSERVER|$IIS_IMAM_MN_SERVER|g" *.xml
sed -i "s|TEST_SUFFIX|$TEST_SUFFIX|g" *.xml

#replace work space names
TEST_PREFIX="SAR_WS1_${TEST_SUFFIX}"
sed -i "s|SAR_WS1|${TEST_PREFIX}|g" *.xml
TEST_PREFIX="SAR_WS2_${TEST_SUFFIX}"
sed -i "s|SAR_WS2|${TEST_PREFIX}|g" *.xml
TEST_PREFIX="SAR_WS3_${TEST_SUFFIX}"
sed -i "s|SAR_WS3|${TEST_PREFIX}|g" *.xml
TEST_PREFIX="SSC_WS_${TEST_SUFFIX}"
sed -i "s|SSC_WS|${TEST_PREFIX}|g" *.xml
TEST_PREFIX="ML_DRD_WS_${TEST_SUFFIX}"
sed -i "s|ML_DRD_WS|${TEST_PREFIX}|g" *.xml

echo "### test data - BANKDEMO_WS*.xml files ###"
cat BANKDEMO_WS*.xml
echo "### test data - CustomClass.xml file ###"
cat CustomClass.xml
echo "### test data - columnAnalysis*.xml files ###"
cat columnAnalysis*.xml
echo "### test data - ML_DRD_WS.xml files ###"
cat ML_DRD_WS.xml
echo "#############################"

cat << EOS > insights_service.properties
noauth=true
insights.db.type=${INSIGHTS_DB_TYPE}
insights.features=ref_data:t,custom_class:t,automationrules:t,unit_test:f
iis.base.url=https://${IIS_HOSTNAME}:${IIS_PORT}/ibm/iis
ug.base.url=https://${UG_HOSTNAME}:${UG_PORT}/ibm/iis
ug.base.url.v3=https://${UG_HOSTNAME}:${UG_PORT}/v3
insights.base.url=http://localhost:9080/ibm/iis
insights.base.url.v3=http://localhost:9080/v3
insights.zen.url=https://${IIS_HOSTNAME}
iis.username.secret=${IIS_USERNAME}
iis.password.secret=${IIS_PASSWORD}
insights.md.store.type=SQLDB
insights.zk.host=${IIS_HOSTNAME}:2181
insights.db.type=${INSIGHTS_DB_TYPE}
insights.db.user=${INSIGHTS_DB_USER}
insights.${INSIGHTS_DB_TYPE}.host=$(xmlescape "$INSIGHTS_DB_HOST")
insights.${INSIGHTS_DB_TYPE}.port=$(xmlescape "$INSIGHTS_DB_PORT")
insights.${INSIGHTS_DB_TYPE}.user=$(xmlescape "$INSIGHTS_DB_USER")
insights.${INSIGHTS_DB_TYPE}.password=$(xmlescape "$INSIGHTS_DB_PASSWORD")
insights.${INSIGHTS_DB_TYPE}.name=$(xmlescape "$INSIGHTS_DB_NAME")
insights.${INSIGHTS_DB_TYPE}.xmeta.host=$(xmlescape "$INSIGHTS_DB_HOST")
insights.${INSIGHTS_DB_TYPE}.xmeta.port=$(xmlescape "$INSIGHTS_DB_PORT")
insights.${INSIGHTS_DB_TYPE}.xmeta.user=$(xmlescape "$INSIGHTS_DB_USER")
insights.${INSIGHTS_DB_TYPE}.xmeta.password=$(xmlescape "$INSIGHTS_DB_PASSWORD")
insights.${INSIGHTS_DB_TYPE}.xmeta.name=$(xmlescape "$INSIGHTS_DB_NAME")
insights.db2oncloud.host=$TEST_DATABASE_HOST
insights.db2oncloud.port=$TEST_DATABASE_PORT
insights.db2oncloud.user=$TEST_DATABASE_USER_NAME
insights.db2oncloud.password=$TEST_DATABASE_PASSWORD
insights.db2oncloud.name=$TEST_DATABASE_NAME
insights.db2oncloud.schema=$TEST_DATABASE_SCHEMA
insights.db2oncloud.mnserver=$IIS_IMAM_MN_SERVER
insights.db2oncloud.clean.run=$CLEAN_RUN
insights.db2oncloud.test.suffix=$TEST_SUFFIX

EOS
echo "### test - insights_service.properties ###"
cat insights_service.properties

cd $GIT_PATH
pwd
# liberty insights_service.properties
cat << EOS > $GIT_PATH/config/liberty/config/insights_service.properties
noauth=false
insights.features=custom_class:f,automationrules:f
iis.base.url=https://${IIS_HOSTNAME}:${IIS_PORT}/ibm/iis
ug.base.url=https://${UG_HOSTNAME}:${UG_PORT}/ibm/iis
ug.base.url.v3=https://${UG_HOSTNAME}:${UG_PORT}/v3
insights.zen.url=https://${IIS_HOSTNAME}
iis.username.secret=${IIS_USERNAME}
iis.password.secret=${IIS_PASSWORD}
insights.md.store.type=SQLDB
insights.zk.host=${IIS_HOSTNAME}:2181
insights.db.type=${INSIGHTS_DB_TYPE}
insights.db.user=${INSIGHTS_DB_USER}
catalog.interactor.type=IGC
EOS

echo "### liberty - insights_service.properties ###"
cat $GIT_PATH/config/liberty/config/insights_service.properties

# liberty bootstrap_db2.properties
cat << EOS > $GIT_PATH/config/liberty/bootstrap_${INSIGHTS_DB_TYPE}.properties
insights.db.host=$(xmlescape "$INSIGHTS_DB_HOST")
insights.db.port=$(xmlescape "$INSIGHTS_DB_PORT")
insights.db.user=$(xmlescape "$INSIGHTS_DB_USER")
insights.db.password=$(xmlescape "$INSIGHTS_DB_PASSWORD")
insights.db.name=$(xmlescape "$INSIGHTS_DB_NAME")
insights.xmeta.host=$(xmlescape "$INSIGHTS_DB_HOST")
insights.xmeta.port=$(xmlescape "$INSIGHTS_DB_PORT")
insights.xmeta.user=$(xmlescape "$INSIGHTS_DB_USER")
insights.xmeta.password=$(xmlescape "$INSIGHTS_DB_PASSWORD")
insights.xmeta.name=$(xmlescape "$INSIGHTS_DB_NAME")
EOS

echo "### liberty - bootstrap_${INSIGHTS_DB_TYPE}.properties ###"
cat $GIT_PATH/config/liberty/bootstrap_${INSIGHTS_DB_TYPE}.properties
