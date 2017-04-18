#!/bin/bash

create_symlink(){

if [[ ! -L $JENKINS_HOME/config.xml ]]; then
   if grep -q "<sid>$INITIAL_ADMIN_USER</sid>" $JENKINS_HOME/config.xml; then
      mv $JENKINS_HOME/config.xml $JENKINS_HOME/configs/config.xml
      ln -s $JENKINS_HOME/configs/config.xml $JENKINS_HOME/config.xml
   else
      mv $JENKINS_HOME/config.xml $JENKINS_HOME/configs/config.xml.`date +"%Y%m%d%H%M%S"`;
      ln -s $JENKINS_HOME/configs/config.xml $JENKINS_HOME/config.xml
   fi
fi

}

TIMER=0;
while [[ -L $JENKINS_HOME/config.xml ]]; do
  sleep 2; TIMER=$((TIMER+1));
  if [[ "$TIMER" -ge 150 ]]; then exit 3; fi
done

#sleep 300; create_symlink
create_symlink;
