#!/usr/bin/env bash

cd /tmp/src

cp -rp -- /tmp/src/target/ip-rolemapper-*.war "$TOMCAT_APPS/ip-rolemapper.war"
cp -- /tmp/src/conf/ocp/ip-rolemapper.xml "$TOMCAT_APPS/ip-rolemapper.xml"

export WAR_FILE=$(readlink -f "$TOMCAT_APPS/ip-rolemapper.war")
