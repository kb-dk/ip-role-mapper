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
package dk.statsbiblioteket.doms.iprolemapper.rolemapper;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;


/**
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class IPRoleRangeTest {

    private final List<String> testRoles;
    private final IPRangeRoles testIPRange;

    /**
     * @throws UnknownHostException
     *             if hard-coded IP addresses are illegal. This will not happen.
     */
    public IPRoleRangeTest() throws UnknownHostException {

        testRoles = Arrays.asList(new String[] { "public" });

        final InetAddress testBeginAddress = InetAddress
                .getByName("192.168.0.1");
        final InetAddress testEndAddress = InetAddress
                .getByName("192.168.0.254");

        testIPRange = new IPRangeRoles(testBeginAddress, testEndAddress,
                testRoles);
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRangeRoles#getRoles()}
     * .
     */
    @Test
    public void testGetRoles() {
        assertEquals(testRoles, testIPRange.getRoles());
    }
}
