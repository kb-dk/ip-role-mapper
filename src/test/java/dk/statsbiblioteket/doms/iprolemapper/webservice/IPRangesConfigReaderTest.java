/*
 * $Id: IPRangesConfigReader.java 878 2010-10-07 09:19:08Z thomassh $
 * $Revision: 878 $
 * $Date: 2010-10-07 11:19:08 +0200 (Thu, 07 Oct 2010) $
 * $Author: thomassh $
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


import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRangeRoles;

/**
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class IPRangesConfigReaderTest {

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.webservice.IPRangesConfigReader#readFromXMLConfigFile(java.io.File)}
     * .
     * 
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws XPathExpressionException
     */
    @Test
    public void testReadFromXMLConfigFile() throws XPathExpressionException,
            ParserConfigurationException, SAXException, IOException {

        // See if we survive reading Mads' insane configuration.
        IPRangesConfigReader configReader = new IPRangesConfigReader();

        // NOTE! This test will need access to the config directory via the
        // classpath in order to function properly.
        final String configFilePath = "config/madstest.xml";
        final URL configURL = ClassLoader.getSystemResource(configFilePath);

        assertNotNull(configURL, "Cannot locate the test configuration file. Make sure "
                + "that '" + configFilePath
                + "' is accessible via the class path.");

        System.out.println(this.getClass().getName()
                + ".testReadFromXMLConfigFile(): About to read configuration "
                + "from: " + configURL);

        List<IPRangeRoles> ipRanges = configReader
                .readFromXMLConfigFile(new File(configURL.getFile()));
        assertEquals(967, ipRanges.size(),
                "Un-expected number of IPRange instances were produced from the configuration file.");
    }
}
