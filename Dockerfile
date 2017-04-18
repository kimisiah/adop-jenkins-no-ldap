FROM jenkins:2.7.4

MAINTAINER Nick Griffin, <nicholas.griffin>

ENV GERRIT_HOST_NAME gerrit
ENV GERRIT_PORT 8080
ENV GERRIT_SSH_PORT 29418
ENV GERRIT_PROFILE="ADOP Gerrit" GERRIT_JENKINS_USERNAME="" GERRIT_JENKINS_PASSWORD=""


# Copy in configuration files
COPY resources/plugins.txt /usr/share/jenkins/ref/
COPY resources/init.groovy.d/ /usr/share/jenkins/ref/init.groovy.d/
COPY resources/authStrategy/ /usr/share/jenkins/authStrategy/
COPY resources/scripts/ /usr/share/jenkins/ref/adop_scripts/
COPY resources/jobs/ /usr/share/jenkins/ref/jobs/
COPY resources/scriptler/ /usr/share/jenkins/ref/scriptler/scripts/
COPY resources/views/ /usr/share/jenkins/ref/init.groovy.d/
COPY resources/m2/ /usr/share/jenkins/ref/.m2
COPY resources/entrypoint.sh /entrypoint.sh
COPY resources/scriptApproval.xml /usr/share/jenkins/ref/
COPY resources/scb_customs/ /usr/share/jenkins/scb_customs/

# Reprotect
USER root
RUN chmod +x -R /usr/share/jenkins/ref/adop_scripts/ && chmod +x -R /usr/share/jenkins/scb_customs/ && chmod +x /entrypoint.sh
# USER jenkins

# Environment variables
ENV ADOP_LDAP_ENABLED=true ADOP_ACL_ENABLED=true ADOP_SONAR_ENABLED=true ADOP_ANT_ENABLED=true ADOP_MAVEN_ENABLED=true ADOP_NODEJS_ENABLED=true ADOP_GERRIT_ENABLED=true
ENV LDAP_GROUP_NAME_ADMIN=""
#ENV JENKINS_OPTS="--prefix=/jenkins -Djenkins.install.runSetupWizard=false"
ENV JENKINS_OPTS="--prefix=/jenkins -Djenkins.install.runSetupWizard=false"
ENV PLUGGABLE_SCM_PROVIDER_PROPERTIES_PATH="/var/jenkins_home/userContent/datastore/pluggable/scm"
ENV PLUGGABLE_SCM_PROVIDER_PATH="/var/jenkins_home/userContent/job_dsl_additional_classpath/"

#RUN sed -i -E '/if.*\n\W+eval.*jenkins\.war.*\nfi/r /usr/share/jenkins/scb_customs/config_relocate.sh' /usr/local/bin/jenkins.sh
#RUN sed -i -E 'N; /if.*\n\W+eval.*jenkins\.war.*/r /usr/share/jenkins/scb_customs/link_watcher.sh' /usr/local/bin/jenkins.sh
RUN sed -i -E '/^find.*\/usr\/share\/jenkins\/ref\/.*/r /usr/share/jenkins/scb_customs/link_watcher.sh' /usr/local/bin/jenkins.sh

#RUN /usr/local/bin/plugins.sh /usr/share/jenkins/ref/plugins.txt

ENTRYPOINT ["/entrypoint.sh"]
