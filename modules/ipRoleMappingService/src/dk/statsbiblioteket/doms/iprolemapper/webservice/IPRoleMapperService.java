/*
 * $Id$
 * $Revision$
 * $Date$
 * $Author$
 *
 * The DOMS project.
 * Copyright (C) 2007-2010  The State and University Library
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package dk.statsbiblioteket.doms.iprolemapper.webservice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRange;
import dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRangeRoles;
import dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRoleMapper;
import dk.statsbiblioteket.doms.iprolemapper.rolemapper.InetAddressComparator;
import dk.statsbiblioteket.doms.webservices.ConfigCollection;

/**
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
@Path("/")
public class IPRoleMapperService {

    private static final Log log = LogFactory.getLog(IPRoleMapperService.class);

    private static final String IP_RANGE_ROLE_CONFIGURATION_PROPERTY = "dk.statsbiblioteket.iprolemapping.configurationFile";
    private static long latestConfigFileModificationTime = -1;
    private static String currentConfigurationFilePath = null;
    private static String lastConfigurationFilePath = "";
    private static String lastConfigurationReloadStatusMessage = "No configuration loaded.";
    private static Status lastConfigurationReloadStatus = Status.OK;

    private enum Status {
        OK, WARNING, ERROR
    };

    public IPRoleMapperService() {
    }

    /*
     * Expected exception: UnknownHostException.
     */
    @GET
    @Path("getRoles/{ipaddress}")
    @Produces("text/plain")
    public String getRoles(@PathParam("ipaddress") String ipAddress)
            throws Throwable {

        if (log.isTraceEnabled()) {
            log.trace("getRoles(): Called with IP adress: " + ipAddress);
        }

        try {
            // The static content of IPRolemapper will be initialised by
            // verifyConfiguration if the configuration can be successfully
            // read.
            verifyConfiguration();
            final IPRoleMapper ipRoleMapper = new IPRoleMapper();
            final Set<String> mappedRoles = ipRoleMapper.mapIPHost(InetAddress
                    .getByName(ipAddress));

            // Build the result string.
            String rolesString = "";
            final Iterator<String> roleIterator = mappedRoles.iterator();
            while (roleIterator.hasNext()) {
                rolesString += roleIterator.next();

                // Append a comma if there are more roles left.
                if (roleIterator.hasNext()) {
                    rolesString += ",";
                }
            }// end-while

            log.debug("IPRoleMapperService.getRoles(): returning roles: "
                    + rolesString);

            return rolesString;
        } catch (Throwable throwable) {
            log.warn("getRoles(): Caught un-expected exception.", throwable);
            throw throwable;
        }
    }

    @GET
    @Path("getRanges")
    @Produces("text/plain")
    public String getRanges(@QueryParam("role") List<String> roles)
            throws Throwable {

        if (log.isTraceEnabled()) {
            log.trace("getRanges(): Called with roles: " + roles);
        }

        try {
            // The static content of IPRolemapper will be initialised by
            // verifyConfiguration if the configuration can be successfully
            // read.
            verifyConfiguration();
            final IPRoleMapper ipRoleMapper = new IPRoleMapper();
            final Set<IPRange> mappedRanges = ipRoleMapper
                    .mapRoles(new TreeSet<String>(roles));

            // Build the result string.
            String rangesString = "";
            final Iterator<IPRange> rangesIterator = mappedRanges.iterator();
            while (rangesIterator.hasNext()) {

                final IPRange range = rangesIterator.next();
                final InetAddress beginAddress = range.getBeginAddress();
                final InetAddress endAddress = range.getEndAddress();

                final InetAddressComparator addressComparator = new InetAddressComparator();
                if (addressComparator.compare(beginAddress, endAddress) == 0) {
                    // It's a single host...
                    rangesString += beginAddress.getHostAddress();
                } else {
                    // It's an actual range...
                    rangesString += beginAddress.getHostAddress() + "-"
                            + endAddress.getHostAddress();
                }

                // Append a comma if there are more roles left.
                if (rangesIterator.hasNext()) {
                    rangesString += "\n";
                }
            }// end-while

            log.debug("getRanges(): returning ranges: " + rangesString);

            return rangesString;
        } catch (Throwable throwable) {
            log.error("getRoles(): Caught un-expected exception.", throwable);
            throw throwable;
        }
    }

    /**
     * Simple status service which informs about the current state of the ip
     * role mapper service. If the reported status is <code>ERROR</code> then
     * the service is not working at all. If the status is <code>WARNING</code>
     * then the service has been unable to update its configuration and is still
     * using the last known good configuration. The <code>OK</code> status
     * indicates that everything is OK.
     * 
     * @return <code>String</code> containing a description of the current state
     *         of this service.
     * @throws Throwable
     *             if anything unexpected happens.
     */
    @GET
    @Path("status")
    @Produces("text/plain")
    public String getStatus() throws Throwable {

        log.trace("getStatus(): Entered.");

        verifyConfiguration();

        final String statusMessage = "STATUS: " + lastConfigurationReloadStatus
                + "\n\nMESSAGE: " + lastConfigurationReloadStatusMessage
                + "\n\nCurrently using this configuration: "
                + currentConfigurationFilePath;

        log.debug("getStatus(): Returning: " + statusMessage);
        return statusMessage;
    }

    /**
     * Check whether the IP ranges configuration has changed since last
     * initialisation, and if so, then re-initialise IPRoleMapper.
     * <p/>
     * This method will not throw any exceptions if the initialisation fails,
     * but will just keep the previous configuration.
     */
    private void verifyConfiguration() {

        log.trace("verifyConfiguration(): Entering.");

        // NOTE! Make sure that web.xml has a listener entry for the
        // ConfigContextListener otherwise this will go very, very wrong.
        final Properties configuration = ConfigCollection.getProperties();

        final String rangesConfigLocation = configuration
                .getProperty(IP_RANGE_ROLE_CONFIGURATION_PROPERTY);

        if (log.isTraceEnabled()) {
            log.trace("IPRoleMapperService(): About to load a configuration "
                    + "from this location: " + rangesConfigLocation);
        }

        if (rangesConfigLocation == null || rangesConfigLocation.length() == 0) {
            final String errorMessage = "The location of the IP address ranges"
                    + " configuration has not been specified in the '"
                    + IP_RANGE_ROLE_CONFIGURATION_PROPERTY
                    + "' property in the service/server configuration.";

            lastConfigurationReloadStatusMessage = errorMessage;
            lastConfigurationFilePath = rangesConfigLocation;
            lastConfigurationReloadStatus = (currentConfigurationFilePath == null) ? Status.ERROR
                    : Status.WARNING;
            throw new IllegalArgumentException(errorMessage);
        }

        File rangesConfigFile = new File(rangesConfigLocation);
        try {
            if (!rangesConfigFile.exists()) {
                // The file could not be found, either because the path is not
                // an absolute path or because it does not exist. Now try
                // locating it within the WAR file before giving up.
                rangesConfigFile = new File(ConfigCollection
                        .getServletContext().getRealPath(rangesConfigLocation));

                if (!rangesConfigFile.exists()) {

                    final String errorMessage = "Could not locate the "
                            + "configuration file on the file system or within"
                            + " the service WAR file: " + rangesConfigLocation;

                    lastConfigurationReloadStatusMessage = errorMessage;
                    lastConfigurationFilePath = rangesConfigLocation;
                    lastConfigurationReloadStatus = (currentConfigurationFilePath == null) ? Status.ERROR
                            : Status.WARNING;
                    throw new FileNotFoundException(errorMessage);
                }
            }

            // Check if the file path or modification time has changed since
            // last initialisation.
            if ((rangesConfigFile.lastModified() != latestConfigFileModificationTime)
                    || (!lastConfigurationFilePath.equals(rangesConfigFile
                            .getAbsolutePath()))) {

                lastConfigurationFilePath = rangesConfigFile.getAbsolutePath();
                latestConfigFileModificationTime = rangesConfigFile
                        .lastModified();

                log.info("IP ranges configuration has changed. Re-initialising"
                        + " from file: " + lastConfigurationFilePath);

                // The configuration has changed. Re-initialise.
                final IPRangesConfigReader rangesReader = new IPRangesConfigReader();
                final List<IPRangeRoles> ranges = rangesReader
                        .readFromXMLConfigFile(rangesConfigFile);
                IPRoleMapper.init(ranges);

                lastConfigurationReloadStatus = Status.OK;
                lastConfigurationReloadStatusMessage = "Running normally.";
                currentConfigurationFilePath = rangesConfigFile
                        .getAbsolutePath();
            }
        } catch (IOException ioException) {
            // Intentionally ignoring/logging this exception. The service will
            // just continue using the last known good configuration or wait for
            // the configuration to be fixed.
            final String errorMessage = "Failed (re-)initialising "
                    + "configuration. Will proceed with the current "
                    + "configuration. The failing configuration file is: "
                    + rangesConfigFile;

            lastConfigurationReloadStatus = (currentConfigurationFilePath == null) ? Status.ERROR
                    : Status.WARNING;

            lastConfigurationReloadStatusMessage = errorMessage
                    + " Cause of the failure: " + ioException;
            log.warn("verifyConfiguration(): " + errorMessage, ioException);
        }

        log.trace("verifyConfiguration(): Exiting. Current re-load status: "
                + lastConfigurationReloadStatus);
    }
}
