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
import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRange;
import dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRoleMapper;
import dk.statsbiblioteket.doms.webservices.ConfigCollection;

//import dk.statsbiblioteket.doms.webservices.ConfigCollection;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpServletResponseWrapper;

/**
 *@author &lt;tsh@statsbiblioteket.dk&gt; Thomas Skou Hansen
 */
@Path("/")
public class IPRoleMapperService {

    private static final Log log = LogFactory.getLog(IPRoleMapperService.class);
    private static long latestConfigFileModificationTime = -1;
    private static String lastConfigurationFilePath = "";

    // @Context
    // private HttpServletRequest request;

    /**
     * 
     */
    public IPRoleMapperService() {

    }

    @GET
    @Path("getRoles/{ipaddress}")
    @Produces("text/plain")
    public String getRoles(@PathParam("ipaddress") String ipAddress)
            throws XPathExpressionException, ParserConfigurationException,
            SAXException, IOException, URISyntaxException {
        log.trace("IPRoleMapperService.getRoles(): Called with IP adress: '"
                + ipAddress + "'");

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

        log.trace("IPRoleMapperService.getRoles(): returning roles: '"
                + rolesString + "' for IP address: " + ipAddress);
        return rolesString;
    }

    /**
     * Check whether the IP ranges configuration has changed since last
     * initialisation, and if so, then re-initialise IPRoleMapper.
     * 
     * @throws IOException
     *             if any problems are encountered while reading the
     *             configuration file.
     * @throws SAXException
     *             if any problems are encountered while parsing the
     *             configuration file.
     * @throws ParserConfigurationException
     *             if any problems are encountered while preparing the XML
     *             parser.
     * @throws XPathExpressionException
     *             if any problems are encountered while fetching the
     *             information from the configuration file.
     * @throws URISyntaxException
     *             if the configuration file cannot be loaded.
     */
    private void verifyConfiguration() throws XPathExpressionException,
            ParserConfigurationException, SAXException, IOException,
            URISyntaxException {
        // NOTE! Make sure that web.xml has a listener entry for the
        // ConfigContextListener otherwise this will go very, very wrong.

        final Properties configuration = ConfigCollection.getProperties();

        final String rangesConfigFileName = (String) configuration
                .get("ipRangeAndRoleConfigurationFile");

        if (rangesConfigFileName == null || rangesConfigFileName.length() == 0) {
            throw new IllegalArgumentException("Bad file name for the IP "
                    + "address ranges configuration file: "
                    + rangesConfigFileName);
        }

        // FIXME! I'm not quite sure that this is the best way to locate the
        // configuration file.
        final File rangesConfigFile = new File(ConfigCollection
                .getServletContext().getRealPath(rangesConfigFileName));

        if ((rangesConfigFile.lastModified() != latestConfigFileModificationTime)
                || (!lastConfigurationFilePath.equals(rangesConfigFile
                        .getAbsolutePath()))) {

            latestConfigFileModificationTime = rangesConfigFile.lastModified();
            lastConfigurationFilePath = rangesConfigFile.getAbsolutePath();

            log.info("IP ranges configuration has changed. Re-initialising.");
            // The configuration has changed. Re-initialise.
            final IPRangesConfigReader rangesReader = new IPRangesConfigReader();
            final List<IPRange> ranges = rangesReader
                    .readFromXMLConfigFile(rangesConfigFile);
            IPRoleMapper.init(ranges);
        }
    }
}
