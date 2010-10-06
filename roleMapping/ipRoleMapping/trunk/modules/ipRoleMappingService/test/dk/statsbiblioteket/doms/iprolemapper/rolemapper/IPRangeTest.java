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
import java.util.List;

import org.junit.Test;

/**
 *@author &lt;tsh@statsbiblioteket.dk&gt; Thomas Skou Hansen
 */
public class IPRangeTest {

    private final InetAddress testBeginAddress;
    private final InetAddress testEndAddress;
    private final List<String> testRoles;

    private final IPRange testIPRange;

    /**
     * @throws UnknownHostException
     *             if hard-coded IP addresses are illegal. This will not happen.
     */
    public IPRangeTest() throws UnknownHostException {

        testBeginAddress = InetAddress.getByName("192.168.0.1");
        testEndAddress = InetAddress.getByName("192.168.0.254");
        testRoles = Arrays.asList(new String[] { "public" });
        testIPRange = new IPRange(testBeginAddress, testEndAddress, testRoles);
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRange#IPRange(java.net.InetAddress, java.net.InetAddress, java.util.List)}
     * .
     * 
     * @throws UnknownHostException
     *             if hard-coded IP addresses are illegal. This will not happen.
     */
    @Test
    public void testIPRangeConstructorAddressRangeHandling()
            throws UnknownHostException {

        // Test handling of a begin address which is larger than/after the end
        // address.
        try {
            final InetAddress beginAddress = InetAddress
                    .getByName("192.168.0.254");
            final InetAddress endAddress = InetAddress.getByName("192.168.0.1");
            final List<String> roles = Arrays.asList(new String[] { "public" });

            // Verify that the constructor coughs up blood....
            new IPRange(beginAddress, endAddress, roles);

            fail("Expected an IllegalArgumentException to be thrown.");
        } catch (IllegalArgumentException illegalArgumentException) {
            // That was the correct answer.
        }
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRange#IPRange(java.net.InetAddress, java.net.InetAddress, java.util.List)}
     * .
     * 
     * @throws UnknownHostException
     *             if hard-coded IP addresses are illegal. This will not happen.
     */
    @Test
    public void testIPRangeConstructorIPv4And6MixupHandling()
            throws UnknownHostException {

        final InetAddress ipV4Address = InetAddress.getByName("192.168.0.254");

        final InetAddress ipV6Address = InetAddress
                .getByName("1020:3040:5060:0:1337:b007:c4fe:f00d");

        final List<String> roles = Arrays.asList(new String[] { "public" });

        // Test handling of mixing IPv4 and IPv6 begin and end addresses.
        try {

            // Verify that the constructor coughs up blood....
            new IPRange(ipV4Address, ipV6Address, roles);

            fail("Expected an IllegalArgumentException to be thrown.");
        } catch (IllegalArgumentException illegalArgumentException) {
            // That was the correct answer.
        }

        // Now check the other way around...
        try {
            // Verify that the constructor coughs up blood....
            new IPRange(ipV6Address, ipV4Address, roles);

            fail("Expected an IllegalArgumentException to be thrown.");
        } catch (IllegalArgumentException illegalArgumentException) {
            // That was the correct answer.
        }
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRange#getBeginAddress()}
     * .
     * 
     * @throws UnknownHostException
     */
    @Test
    public void testGetBeginAddress() {
        assertEquals(testBeginAddress, testIPRange.getBeginAddress());
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRange#getEndAddress()}
     * .
     */
    @Test
    public void testGetEndAddress() {
        assertEquals(testEndAddress, testIPRange.getEndAddress());
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRange#getRoles()}
     * .
     */
    @Test
    public void testGetRoles() {
        assertEquals(testRoles, testIPRange.getRoles());
    }

}
