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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class IPRoleMapperTest {

    private final IPRoleMapper ipRoleMapper;

    /**
     * @throws UnknownHostException
     *             if any of the hard-coded IP addresses are illegal. This will
     *             not happen.
     */
    public IPRoleMapperTest() throws UnknownHostException {

        ipRoleMapper = new IPRoleMapper();
        IPRoleMapper.init(createIPv4TestRanges());
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRoleMapper#mapIPHost(java.net.InetAddress)}
     * .
     * 
     * @throws UnknownHostException
     *             if any of the hard-coded IP addresses are illegal. This will
     *             not happen.
     */
    @Test
    public void testMapIPHost() throws UnknownHostException {

        // Check a "public" address.
        InetAddress addressToMap = InetAddress.getByName("192.168.0.34");
        Set<String> associatedRoles = ipRoleMapper.mapIPHost(addressToMap);
        Set<String> expectedRoles = new HashSet<String>();
        expectedRoles.add("public");
        assertEquals(expectedRoles, associatedRoles);

        // Check a "student" address.
        addressToMap = InetAddress.getByName("192.168.0.128");
        associatedRoles = ipRoleMapper.mapIPHost(addressToMap);

        // Expect "public" and "student".
        expectedRoles.add("student");
        assertEquals(expectedRoles, associatedRoles);

        // Check a "professor-student" address.
        addressToMap = InetAddress.getByName("192.168.0.145");
        associatedRoles = ipRoleMapper.mapIPHost(addressToMap);

        // Expect "public", "student" and "professor".
        expectedRoles.add("professor");
        assertEquals(expectedRoles, associatedRoles);

        // Check a "professor" address.
        addressToMap = InetAddress.getByName("192.168.0.160");
        associatedRoles = ipRoleMapper.mapIPHost(addressToMap);

        // Expect "public" and "professor".
        expectedRoles.remove("student");
        assertEquals(expectedRoles, associatedRoles);
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRoleMapper#mapRoles(Set<String>)}
     * .
     * 
     * @throws UnknownHostException
     *             if any of the hard-coded IP addresses are illegal. This will
     *             not happen.
     */
    @Test
    public void testMapRoles() throws UnknownHostException {
        fail("Implement this test.");
    }
    
    private List<IPRangeRoles> createIPv4TestRanges() throws UnknownHostException {

        // Note: It is important that the sub-arrays in rangeSetups contain a
        // begin address, an end address and at least one role!
        final String[][] rangeSetups = {
                { "192.168.0.1", "192.168.0.254", "public" },
                { "192.168.0.123", "192.168.0.151", "student" },
                { "192.168.0.142", "192.168.0.162", "professor" } };

        final List<IPRangeRoles> ipRanges = new LinkedList<IPRangeRoles>();
        for (String[] rangeSetup : rangeSetups) {

            final InetAddress beginAddress = InetAddress
                    .getByName(rangeSetup[0]);
            final InetAddress endAddress = InetAddress.getByName(rangeSetup[1]);

            final List<String> roles = Arrays.asList(Arrays.copyOfRange(
                    rangeSetup, 2, rangeSetup.length));

            final IPRangeRoles testRange = new IPRangeRoles(beginAddress, endAddress,
                    roles);

            ipRanges.add(testRange);
        }

        return ipRanges;
    }
}
