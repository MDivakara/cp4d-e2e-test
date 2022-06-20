#!/usr/bin/env bash
set -e

if [ -z "$INSIGHTS_ZOOKEEPER_HOST" ]; then
  export INSIGHTS_ZOOKEEPER_HOST="zookeeper:2181"
fi
if [ -z "$INSIGHTS_DB_TYPE" ]; then
  export INSIGHTS_DB_TYPE="db2"
fi
if [ -z "$INSIGHTS_DB_HOST" ]; then
  export INSIGHTS_DB_HOST="localhost"
fi
if [ -z "$INSIGHTS_DB_PORT" ]; then
  if [ "$INSIGHTS_DB_TYPE" = "db2" ]; then
    export INSIGHTS_DB_PORT="50000"
  elif [ "$INSIGHTS_DB_TYPE" = "oracle" ]; then
    export INSIGHTS_DB_PORT="1521"
  elif [ "$INSIGHTS_DB_TYPE" = "sqlserver" ]; then
    export INSIGHTS_DB_PORT="1433"
  else
    export INSIGHTS_DB_PORT="1521"
  fi
fi
if [ -z "$INSIGHTS_DB_USER" ]; then
  if [ "$INSIGHTS_DB_TYPE" = "db2" ]; then
    export INSIGHTS_DB_USER="db2inst1"
  elif [ "$INSIGHTS_DB_TYPE" = "oracle" ]; then
    export INSIGHTS_DB_USER="xmeta1"
  elif [ "$INSIGHTS_DB_TYPE" = "sqlserver" ]; then
    export INSIGHTS_DB_USER="xmeta1"
  else
    export INSIGHTS_DB_USER="xmeta1"
  fi
fi
if [ -z "$INSIGHTS_DB_PASSWORD" ]; then
    export INSIGHTS_DB_PASSWORD="{xor}BT4ubm1sOjw="
  else
    export INSIGHTS_DB_PASSWORD="$(securityUtility encode "$INSIGHTS_DB_PASSWORD")"
fi

if [ -z "$INSIGHTS_DB_NAME" ]; then
  if [ "$INSIGHTS_DB_TYPE" = "db2" ]; then
    export INSIGHTS_DB_NAME="db2inst1"
  elif [ "$INSIGHTS_DB_TYPE" = "oracle" ]; then
    export INSIGHTS_DB_NAME="orcl"
  elif [ "$INSIGHTS_DB_TYPE" = "sqlserver" ]; then
    export INSIGHTS_DB_NAME="xmeta1"
  else
    export INSIGHTS_DB_NAME="xmeta1"
  fi
fi

cp /config/configDropins/available/ds_${INSIGHTS_DB_TYPE}.xml /config/configDropins/defaults

if [ "$INSIGHTS_DB_TYPE" = "oracle" ]; then
  if [ "$INSIGHTS_DB_ORACLE_TYPE" = "SID" ]; then
    sed -i 's/serviceName/databaseName/g' /config/configDropins/defaults/ds_oracle.xml
  fi
fi

export INSIGHTS_FEATURES="custom_class:f,automationrules:f"

xmlescape() {
  echo "$1" | sed 's/&/\&amp;/g;s/</\&lt;/g;s/>/\&gt;/g;s/"/\&quot;/g;s/'"'"'/\&#39;/g'
}

cat << EOF > /config/configDropins/defaults/vars.xml
<server>
  <variable name="insights.db.host" value="$(xmlescape "$INSIGHTS_DB_HOST")" />
  <variable name="insights.db.port" value="$(xmlescape "$INSIGHTS_DB_PORT")" />
  <variable name="insights.db.user" value="$(xmlescape "$INSIGHTS_DB_USER")" />
  <variable name="insights.db.password" value="$(xmlescape "$INSIGHTS_DB_PASSWORD")" />
  <variable name="insights.db.name" value="$(xmlescape "$INSIGHTS_DB_NAME")" />
  <variable name="insights.xmeta.host" value="$(xmlescape "$INSIGHTS_DB_HOST")" />
  <variable name="insights.xmeta.port" value="$(xmlescape "$INSIGHTS_DB_PORT")" />
  <variable name="insights.xmeta.user" value="$(xmlescape "$INSIGHTS_DB_USER")" />
  <variable name="insights.xmeta.password" value="$(xmlescape "$INSIGHTS_DB_PASSWORD")" />
  <variable name="insights.xmeta.name" value="$(xmlescape "$INSIGHTS_DB_NAME")" />
</server>
EOF


KEYSTORE_FILE='/config/config/tokenservice.keystore'
if [ ! -f $KEYSTORE_FILE ]; then
  JWT_KEY_FILE='/config/secrets/jwtkey.cer'
  if [ -f $JWT_KEY_FILE ]; then
    echo 'Adding JWT certificate to the keystore...'
    keytool -importcert -file $JWT_KEY_FILE -alias jwtkey -keystore $KEYSTORE_FILE -storepass passw0rd -keypass passw0rd -no-prompt
  fi
fi

echo "insights.features=$INSIGHTS_FEATURES" >> /config/config/insights_service.properties
echo "iis.base.url=$IIS_BASE_URL" >> /config/config/insights_service.properties
echo "iis.username.secret=$IIS_USERNAME_SECRET" >> /config/config/insights_service.properties
echo "iis.password.secret=$IIS_PASSWORD_SECRET" >> /config/config/insights_service.properties
echo "insights.zk.host=$INSIGHTS_ZOOKEEPER_HOST" >> /config/config/insights_service.properties
echo "insights.db.type=$INSIGHTS_DB_TYPE" >> /config/config/insights_service.properties
echo "insights.db.user=$INSIGHTS_DB_USER" >> /config/config/insights_service.properties
echo "insights.zen.url=$INSIGHTS_ZEN_URL" >> /config/config/insights_service.properties

if [ "$USER_PREFS_NOAUTH" = "true" ]; then
  echo "noauth=true" >> /config/config/insights_service.properties
fi

exec "$@"
