#!/usr/bin/env bash

cp -- /tmp/src/conf/ocp/logback.xml "$CONF_DIR/logback.xml"
cp -- /tmp/src/conf/ipRangesAndRoles.xml "$CONF_DIR/ipRangesAndRoles.xml"
 
ln -s -- "$TOMCAT_APPS/ip-rolemapper.xml" "$DEPLOYMENT_DESC_DIR/ip-rolemapper.xml"
