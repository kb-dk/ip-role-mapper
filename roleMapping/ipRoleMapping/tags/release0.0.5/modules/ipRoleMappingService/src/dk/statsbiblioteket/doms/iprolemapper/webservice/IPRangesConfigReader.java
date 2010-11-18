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
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRangeRoles;
import dk.statsbiblioteket.util.Logs;
import dk.statsbiblioteket.util.xml.DOM;

/**
 * This class is a factory class meant for production of lists of
 * <code>IPRange</code> instances from various configuration sources.
 * 
 * @author &lt;tsh@statsbiblioteket.dk&gt; Thomas Skou Hansen
 */
public class IPRangesConfigReader {

    private static final Log log = LogFactory
            .getLog(IPRangesConfigReader.class);

    /**
     * Produce a <code>List</code> of <code>IPRangeRoles</code> instances
     * constructed from the information read from the XML configuration
     * specified by <code>rangesConfigFile</code>.
     * 
     * @param rangesConfigFile
     *            a <code>File</code> instance configured with the path to the
     *            XML configuration file to read.
     * @return a list of <code>IPRangeRoles</code> instances, produced from the
     *         contents of the configuration file.
     * @throws IOException
     *             if any errors are encountered while reading the configuration
     *             file.
     */
    public List<IPRangeRoles> readFromXMLConfigFile(File rangesConfigFile)
            throws IOException {

        if (log.isTraceEnabled()) {
            log.trace("readFromXMLConfigFile(): Called with file path: "
                    + rangesConfigFile);
        }

        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();

        NodeList ipRangeNodes = null;

        try {
            final DocumentBuilder documentBuilder = documentBuilderFactory
                    .newDocumentBuilder();

            final Document configuationDocument = documentBuilder
                    .parse(rangesConfigFile);

            final XPathFactory xPathFactory = XPathFactory.newInstance();
            final XPath xPath = xPathFactory.newXPath();

            ipRangeNodes = (NodeList) xPath.evaluate("/ipranges/iprange",
                    configuationDocument, XPathConstants.NODESET);
        } catch (ParserConfigurationException parserConfigException) {
            throw new IOException("Failed setting up parser",
                    parserConfigException);
        } catch (SAXException saxException) {
            throw new IOException("Failed parsing configuration file '"
                    + rangesConfigFile + "'", saxException);

        } catch (XPathExpressionException xPathExpressionException) {
            throw new IOException("Failed parsing (evaluating) configuration"
                    + " file '" + rangesConfigFile + "'",
                    xPathExpressionException);

        }
        final List<IPRangeRoles> ipRangeList = new LinkedList<IPRangeRoles>();
        for (int nodeIdx = 0; nodeIdx < ipRangeNodes.getLength(); nodeIdx++) {
            try {
                ipRangeList.add(produceIPRangeInstance(ipRangeNodes
                        .item(nodeIdx)));
            } catch (Exception cause) {
                String ipRangeNodeXMLString = "Malformed IpRange.";
                try {
                    ipRangeNodeXMLString = DOM.domToString(ipRangeNodes
                            .item(nodeIdx));
                } catch (Exception eTwo) {
                    // Exception being ignored
                }
                Logs.log(log, Logs.Level.WARN,
                        "readFromXMLConfigFile() failed to read IPRange: ",
                        ipRangeNodeXMLString, cause);
            }
        }

        if (log.isTraceEnabled()) {
            log.trace("readFromXMLConfigFile(): Returning IP address ranges: "
                    + ipRangeList);
        }
        return ipRangeList;
    }

    /**
     * This method produces an <code>IPRangeRoles</code> instance from the
     * information stored in the <code>Node</code> specified by
     * <code>ipRangeNode</code>.
     * 
     * @param ipRangeNode
     *            a <code>Document Node</code> containing information about an
     *            IP range and its associated roles.
     * @return an <code>IPRangeRoles</code> instance created from the
     *         information contained in <code>ipRangeNode</code>.
     * @throws XPathExpressionException
     *             if any errors are encountered while reading range roles from
     *             <code>ipRangeNode</code>.
     * @throws UnknownHostException
     *             if the begin or end address of <code>ipRangeNode</code> is
     *             either an unknown host name or an illegal IP address.
     * @throws IllegalArgumentException
     *             if the begin address and end address of the range is not of
     *             the same type. I.e. if they are not both IPv4 or IPv6
     *             addresses, or if <code>beginAddress</code> is higher/after
     *             <code>endAddress</code>.
     */
    private IPRangeRoles produceIPRangeInstance(Node ipRangeNode)
            throws XPathExpressionException, IllegalArgumentException,
            UnknownHostException {

        if (log.isTraceEnabled()) {

            String ipRangeNodeXMLString = "Malformed XML";
            try {
                ipRangeNodeXMLString = DOM.domToString(ipRangeNode);
            } catch (TransformerException transformerException) {
                // Just ignore for now and log. The code will break later...
            }

            log.trace("produceIPRangeInstance(): Called with XML node: "
                    + ipRangeNodeXMLString);
        }

        final NamedNodeMap attributes = ipRangeNode.getAttributes();
        final String beginAddress = attributes.getNamedItem("begin")
                .getNodeValue();
        final String endAddress = attributes.getNamedItem("end").getNodeValue();

        final XPathFactory xPathFactory = XPathFactory.newInstance();
        final XPath xPath = xPathFactory.newXPath();

        final NodeList ipRangeRoleNodes = (NodeList) xPath.evaluate("role",
                ipRangeNode, XPathConstants.NODESET);

        final List<String> ipRangeRoles = new LinkedList<String>();
        for (int nodeIdx = 0; nodeIdx < ipRangeRoleNodes.getLength(); nodeIdx++) {
            ipRangeRoles.add(ipRangeRoleNodes.item(nodeIdx).getTextContent()
                    .trim());
        }

        final IPRangeRoles rangeRoles = new IPRangeRoles(InetAddress
                .getByName(beginAddress), InetAddress.getByName(endAddress),
                ipRangeRoles);

        if (log.isTraceEnabled()) {
            log.trace("produceIPRangeInstance(): Returning IPRangeRoles "
                    + "instance: " + rangeRoles);
        }
        return rangeRoles;
    }
}
