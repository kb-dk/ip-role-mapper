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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRange;

/**
 * This class is a factory class meant for production of lists of
 * <code>IPRange</code> instances from various configuration sources.
 * 
 * @author &lt;tsh@statsbiblioteket.dk&gt; Thomas Skou Hansen
 */
public class IPRangesConfigReader {

    /**
     * Produce a <code>List</code> of <code>IPRange</code> instances constructed
     * from the information read from the XML configuration specified by
     * <code>rangesConfigFile</code>.
     * 
     * @param rangesConfigFile
     *            a <code>File</code> instance configured with the path to the
     *            XML configuration file to read.
     * @return a list of <code>IPRange</code> instances, produced from the
     *         contents of the configuration file.
     * @throws ParserConfigurationException
     *             if any errors were encountered while instantiating a
     *             <code>DocumentBuilder</code> for parsing the configuration.
     * @throws IOException
     *             if any errors are encountered while reading the configuration
     *             file.
     * @throws SAXException
     *             if any errors are encountered while parsing the configuration
     *             file.
     * @throws XPathExpressionException
     *             if any errors are encountered while parsing the configuration
     *             file.
     */
    public List<IPRange> readFromXMLConfigFile(File rangesConfigFile)
            throws ParserConfigurationException, SAXException, IOException,
            XPathExpressionException {

        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();

        final DocumentBuilder documentBuilder = documentBuilderFactory
                .newDocumentBuilder();

        final Document configuationDocument = documentBuilder
                .parse(rangesConfigFile);

        final XPathFactory xPathFactory = XPathFactory.newInstance();
        final XPath xPath = xPathFactory.newXPath();

        final NodeList ipRangeNodes = (NodeList) xPath.evaluate(
                "/ipranges/iprange", configuationDocument,
                XPathConstants.NODESET);

        final List<IPRange> ipRangeList = new LinkedList<IPRange>();
        for (int nodeIdx = 0; nodeIdx < ipRangeNodes.getLength(); nodeIdx++) {
            ipRangeList.add(produceIPRangeInstance(ipRangeNodes.item(nodeIdx)));
        }

        return ipRangeList;
    }

    /**
     * @param ipRangeNode
     *            a <code>Document Node</code> containing information about an
     *            IP range.
     * @return an <code>IPRange</code> instance created from the information
     *         contained in <code>ipRangeNode</code>.
     * @throws XPathExpressionException
     *             if any errors are encountered while reading range roles from
     *             <code>ipRangeNode</code>.
     * @throws UnknownHostException
     *             if the begin or end address of <code>ipRangeNode</code> is
     *             either an unknown host name or illegal IP address.
     * @throws IllegalArgumentException
     */
    private IPRange produceIPRangeInstance(Node ipRangeNode)
            throws XPathExpressionException, IllegalArgumentException,
            UnknownHostException {

        NamedNodeMap attributes = ipRangeNode.getAttributes();
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

        return new IPRange(InetAddress.getByName(beginAddress), InetAddress
                .getByName(endAddress), ipRangeRoles);
    }
}
