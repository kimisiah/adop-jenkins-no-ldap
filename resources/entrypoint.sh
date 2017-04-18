#!/bin/bash

echo "Genarate JENKINS SSH KEY and add it to gerrit"
host=$GERRIT_HOST_NAME
port=$GERRIT_PORT
gerrit_provider_id="adop-gerrit"
gerrit_protocol="ssh"
username=$GERRIT_JENKINS_USERNAME
password=$GERRIT_JENKINS_PASSWORD
: ${JENKINS_HOME:="/var/jenkins_home"}

nohup /usr/share/jenkins/ref/adop\_scripts/generate_key.sh -c ${host} -p ${port} -u ${username} -w ${password} &

echo "Setting up your default SCM provider - Gerrit..."
mkdir -p $PLUGGABLE_SCM_PROVIDER_PROPERTIES_PATH $PLUGGABLE_SCM_PROVIDER_PATH
mkdir -p ${PLUGGABLE_SCM_PROVIDER_PROPERTIES_PATH}/CartridgeLoader ${PLUGGABLE_SCM_PROVIDER_PROPERTIES_PATH}/ScmProviders
nohup /usr/share/jenkins/ref/adop\_scripts/generate_gerrit_scm.sh -i ${gerrit_provider_id} -p ${gerrit_protocol} -h ${host} &

echo "Tokenising scriptler scripts..."
sed -i "s,###SCM_PROVIDER_PROPERTIES_PATH###,$PLUGGABLE_SCM_PROVIDER_PROPERTIES_PATH,g" /usr/share/jenkins/ref/scriptler/scripts/retrieve_scm_props.groovy

echo "skip upgrade wizard step after installation"
echo "2.7.4" > /var/jenkins_home/jenkins.install.UpgradeWizard.state

## SCB CUSTOMS
mkdir -p $JENKINS_HOME/configs
if [[ ! -L $JENKINS_HOME/config.xml ]]; then 
   if [[ ! -f  $JENKINS_HOME/config.xml ]]; then 
      mv /usr/share/jenkins/scb_customs/default_config.xml $JENKINS_HOME/configs/config.xml;
   else
      mv $JENKINS_HOME/configs/config.xml $JENKINS_HOME/configs/config.xml.`date +"%Y%m%d%H%M%S"`;
      mv $JENKINS_HOME/config.xml $JENKINS_HOME/configs/config.xml
   fi

   ln -s $JENKINS_HOME/configs/config.xml $JENKINS_HOME/config.xml
fi

rm -f /usr/share/jenkins/ref/init.groovy.d/*based_auth.groovy
rm -f $JENKINS_HOME/init.groovy.d/*based_auth.groovy

grep -q "<sid>$INITIAL_ADMIN_USER</sid>" $JENKINS_HOME/config.xml
if [[ $? != 0 ]]; then
   if $ADOP_LDAP_ENABLED ; then 
      cp /usr/share/jenkins/authStrategy/role_based_auth.groovy.ldap /usr/share/jenkins/ref/init.groovy.d/role_based_auth.groovy
   else
      cp /usr/share/jenkins/authStrategy/role_based_auth.groovy /usr/share/jenkins/ref/init.groovy.d/role_based_auth.groovy
   fi
fi

if ! grep -q "<sid>$INITIAL_ADMIN_USER</sid>" $JENKINS_HOME/configs/config.xml; then
   nohup watch -e -n 15 "/usr/share/jenkins/scb_customs/link_watcher.sh" &
fi

## SCB CUSTOMS

echo "start JENKINS"

chown -R 1000:1000 /var/jenkins_home
su jenkins -c /usr/local/bin/jenkins.sh

## SCB CUSTOMS
#if [[ ! -L $JENKINS_HOME/config.xml ]]; then
#   echo "Placing Jenkins configuration backup..."
#   mv $JENKINS_HOME/config.xml $JENKINS_HOME/configs/config.xml
#   ln -s $JENKINS_HOME/configs/config.xml $JENKINS_HOME/config.xml
#fi
## SCB CUSTOMS
