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

import dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRange;
import dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRangeRoles;
import dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRoleMapper;
import dk.statsbiblioteket.doms.iprolemapper.rolemapper.InetAddressComparator;
import dk.statsbiblioteket.doms.webservices.ConfigCollection;
import dk.statsbiblioteket.util.Logs;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

//import dk.statsbiblioteket.doms.webservices.ConfigCollection;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author &lt;tsh@statsbiblioteket.dk&gt; Thomas Skou Hansen
 */
@Path("/")
public class IPRoleMapperService {

    private static final Log log = LogFactory.getLog(IPRoleMapperService.class);
    private static long latestConfigFileModificationTime = -1;
    private static String lastConfigurationFilePath = "";

    // @Context
    // private HttpServletRequest request;

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

        final String rangesConfigLocation = configuration.getProperty
                ("dk.statsbiblioteket.iprolemapping.configurationFile");

        if (log.isTraceEnabled()) {
            log.trace("IPRoleMapperService(): About to load a configuration "
                      + "from this location: " + rangesConfigLocation);
        }

        if (rangesConfigLocation == null || rangesConfigLocation.length() == 0) {
            throw new IllegalArgumentException("The location of the IP address"
                                               + " ranges configuration has not been specified.");
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
                    throw new FileNotFoundException("Could not locate the "
                                                    + "configuration file on the file system or within"
                                                    + " this WAR: " + rangesConfigLocation);
                }
            }

            if ((rangesConfigFile.lastModified() != latestConfigFileModificationTime)
                || (!lastConfigurationFilePath.equals(rangesConfigFile
                    .getAbsolutePath()))) {

                latestConfigFileModificationTime = rangesConfigFile
                        .lastModified();
                lastConfigurationFilePath = rangesConfigFile.getAbsolutePath();

                Logs.log(log, Logs.Level.INFO, "IP ranges configuration has "
                                               + "changed. Re-initialising from file: ",
                         lastConfigurationFilePath);
                // The configuration has changed. Re-initialise.
                final IPRangesConfigReader rangesReader = new IPRangesConfigReader();
                final List<IPRangeRoles> ranges = rangesReader
                        .readFromXMLConfigFile(rangesConfigFile);
                IPRoleMapper.init(ranges);
            }
        } catch (IOException ioException) {
            // intentionally ignoring this exception.
            log.warn("verifyConfiguration(): Failed (re-)initialising "
                     + "configuration. Will proceed with the current "
                     + "configuration. The failing configuration file is: "
                     + rangesConfigFile, ioException);
        }
        log.trace("verifyConfiguration(): Exiting.");
    }
}
