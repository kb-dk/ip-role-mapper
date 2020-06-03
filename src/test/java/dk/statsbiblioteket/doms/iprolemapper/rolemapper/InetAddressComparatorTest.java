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

import org.junit.Before;
import org.junit.Test;

/**
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class InetAddressComparatorTest {

    private InetAddress lowIPv4Address;
    private InetAddress lowIPv4AddressClone;
    private InetAddress highIPv4Address;

    private InetAddress lowIPv6Address;
    private InetAddress lowIPv6AddressClone;
    private InetAddress highIPv6Address;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        lowIPv4Address = InetAddress.getByName("10.50.0.42");
        lowIPv4AddressClone = InetAddress.getByName("10.50.0.42");
        highIPv4Address = InetAddress.getByName("192.168.0.42");

        lowIPv6Address = InetAddress
                .getByName("1020:3040:5060:0:1337:b007:c4fe:f00d");
        lowIPv6AddressClone = InetAddress
                .getByName("1020:3040:5060:0:1337:b007:c4fe:f00d");
        highIPv6Address = InetAddress
                .getByName("1020:3040:5060:0:1337:beef:c0ff:ee");
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.rolemapper.InetAddressComparator#compare(java.net.InetAddress, java.net.InetAddress)}
     * .
     */
    @Test
    public void testCompareIPv4() {
        final InetAddressComparator comparator = new InetAddressComparator();
        assertEquals(
                "Failed comparing two similar IPv4 InetAddress instances.", 0,
                comparator.compare(lowIPv4Address, lowIPv4AddressClone));
        assertEquals(
                "Failed comparing an IPv4 InetAddress instances with it self.",
                0, comparator.compare(lowIPv4Address, lowIPv4Address));
        assertEquals(
                "Failed performing a \"less than\" comparison of two IPv4 "
                        + "InetAddress instances.", -1, comparator.compare(
                        lowIPv4Address, highIPv4Address));
        assertEquals(
                "Failed performing a \"greater than\" comparison of two IPv4 "
                        + "InetAddress instances.", 1, comparator.compare(
                        highIPv4Address, lowIPv4Address));
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.rolemapper.InetAddressComparator#compare(java.net.InetAddress, java.net.InetAddress)}
     * .
     */
    @Test
    public void testCompareIPv6() {
        final InetAddressComparator comparator = new InetAddressComparator();
        assertEquals(
                "Failed comparing two similar IPv6 InetAddress instances.", 0,
                comparator.compare(lowIPv6Address, lowIPv6AddressClone));
        assertEquals(
                "Failed comparing an IPv6 InetAddress instances with it self.",
                0, comparator.compare(lowIPv6Address, lowIPv6Address));
        assertEquals(
                "Failed performing a \"less than\" comparison of two IPv6 "
                        + "InetAddress instances.", -1, comparator.compare(
                        lowIPv6Address, highIPv6Address));
        assertEquals(
                "Failed performing a \"greater than\" comparison of two IPv6 "
                        + "InetAddress instances.", 1, comparator.compare(
                        highIPv6Address, lowIPv6Address));
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.rolemapper.InetAddressComparator#compare(java.net.InetAddress, java.net.InetAddress)}
     * .
     */
    @Test
    public void testCompareErrorScenarios() {
        final InetAddressComparator comparator = new InetAddressComparator();
        try {
            comparator.compare(lowIPv4Address, lowIPv6Address);
            fail("Expected a ClassCastException when comparing an IPv4 "
                    + "InetAddress with an IPv6 InetAddress.");
        } catch (ClassCastException classCastException) {
            // Good. That was the correct answer.
        }

        // Now, check the other way around.
        try {
            comparator.compare(lowIPv6Address, lowIPv4Address);
            fail("Expected a ClassCastException when comparing an IPv6 "
                    + "InetAddress with an IPv4 InetAddress.");
        } catch (ClassCastException classCastException) {
            // Good. That was the correct answer.
        }

        try {
            comparator.compare(null, lowIPv6Address);
            fail("Expected an NullPointerException when comparing null with an IPv6 InetAddress instance.");
        } catch (NullPointerException nullPointerException) {
            // Good. That was the correct answer.
        }

        // Now, check the other way around.
        try {
            comparator.compare(lowIPv6Address, null);
            fail("Expected an NullPointerException when comparing an IPv6 InetAddress instance with null.");
        } catch (NullPointerException nullPointerException) {
            // Good. That was the correct answer.
        }
    }

}
