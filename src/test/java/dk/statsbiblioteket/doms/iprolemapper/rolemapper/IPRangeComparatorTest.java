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
public class IPRangeComparatorTest {

    private InetAddress lowIPv4Address1;
    private InetAddress lowIPv4Address2;

    private InetAddress highIPv4Address1;
    private InetAddress highIPv4Address2;

    private InetAddress lowIPv6Address1;
    private InetAddress lowIPv6Address2;
    private InetAddress highIPv6Address1;
    private InetAddress highIPv6Address2;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        lowIPv4Address1 = InetAddress.getByName("10.50.0.1");
        lowIPv4Address2 = InetAddress.getByName("10.50.0.42");

        highIPv4Address1 = InetAddress.getByName("10.50.0.34");
        highIPv4Address2 = InetAddress.getByName("10.50.2.34");

        lowIPv6Address1 = InetAddress
                .getByName("1020:3040:5060:0:1337:b007:c4fe:000d");
        lowIPv6Address2 = InetAddress
                .getByName("1020:3040:5060:0:1337:b007:c4fe:f00d");
        highIPv6Address1 = InetAddress
                .getByName("1020:3040:5060:0:1337:beef:c0ff:0001");
        highIPv6Address2 = InetAddress
                .getByName("1020:3040:5060:0:1337:beef:c0ff:ee");
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRangeComparator#compare(dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRange, dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRange)}
     * .
     */
    @Test
    public void testCompareIPv4() {
        final IPRangeComparator comparator = new IPRangeComparator();

        final IPRange testIPRange1 = new IPRange(lowIPv4Address2,
                highIPv4Address2);
        final IPRange testIPRange2 = new IPRange(lowIPv4Address2,
                highIPv4Address2);
        assertEquals("Failed comparing two similar IPRange instances.", 0,
                comparator.compare(testIPRange1, testIPRange2));

        assertEquals(
                "Failed comparing an IPv4 InetAddress instances with it self.",
                0, comparator.compare(testIPRange1, testIPRange1));

        // Test comparison with a lower, however, overlapping IP range.
        final IPRange smallerRange1 = new IPRange(lowIPv4Address1,
                highIPv4Address1);
        assertEquals(
                "Failed performing a \"less than\" comparison of two IPv4 "
                        + "IPRange instances.", -1, comparator.compare(
                        smallerRange1, testIPRange1));

        // Test comparison with a higher, however, overlapping IP range. That
        // is, "the other way around".
        assertEquals(
                "Failed performing a \"greater than\" comparison of two IPv4 "
                        + "IPRange instances.", 1, comparator.compare(
                        testIPRange1, smallerRange1));

        // Test comparison with lower/higher ranges, having the same start
        // address.
        final IPRange smallerRange2 = new IPRange(lowIPv4Address1,
                lowIPv4Address2);
        final IPRange largerRange2 = new IPRange(lowIPv4Address1,
                highIPv4Address2);
        assertEquals(
                "Failed performing a \"less than\" comparison of two IPv4 "
                        + "IPRange instances.", -1, comparator.compare(
                        smallerRange2, largerRange2));

        assertEquals(
                "Failed performing a \"greater than\" comparison of two IPv4 "
                        + "IPRange instances.", 1, comparator.compare(
                        largerRange2, smallerRange2));

        // Test comparison with lower/higher ranges, having the same end
        // address.
        final IPRange smallerRange3 = new IPRange(lowIPv4Address1,
                highIPv4Address2);
        final IPRange largerRange3 = new IPRange(lowIPv4Address2,
                highIPv4Address2);
        assertEquals(
                "Failed performing a \"less than\" comparison of two IPv4 "
                        + "IPRange instances.", -1, comparator.compare(
                        smallerRange3, largerRange3));

        assertEquals(
                "Failed performing a \"greater than\" comparison of two IPv4 "
                        + "IPRange instances.", 1, comparator.compare(
                        largerRange3, smallerRange3));

    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRangeComparator#compare(dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRange, dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRange)}
     * .
     */
    @Test
    public void testCompareIPv6() {

        final IPRangeComparator comparator = new IPRangeComparator();

        final IPRange testIPRange1 = new IPRange(lowIPv6Address2,
                highIPv6Address2);
        final IPRange testIPRange2 = new IPRange(lowIPv6Address2,
                highIPv6Address2);
        assertEquals("Failed comparing two similar IPRange instances.", 0,
                comparator.compare(testIPRange1, testIPRange2));

        assertEquals(
                "Failed comparing an IPv6 InetAddress instances with it self.",
                0, comparator.compare(testIPRange1, testIPRange1));

        // Test comparison with a lower, however, overlapping IP range.
        final IPRange smallerRange1 = new IPRange(lowIPv6Address1,
                highIPv6Address1);
        assertEquals(
                "Failed performing a \"less than\" comparison of two IPv6 "
                        + "IPRange instances.", -1, comparator.compare(
                        smallerRange1, testIPRange1));

        // Test comparison with a higher, however, overlapping IP range. That
        // is, "the other way around".
        assertEquals(
                "Failed performing a \"greater than\" comparison of two IPv6 "
                        + "IPRange instances.", 1, comparator.compare(
                        testIPRange1, smallerRange1));

        // Test comparison with lower/higher ranges, having the same start
        // address.
        final IPRange smallerRange2 = new IPRange(lowIPv6Address1,
                lowIPv6Address2);
        final IPRange largerRange2 = new IPRange(lowIPv6Address1,
                highIPv6Address2);
        assertEquals(
                "Failed performing a \"less than\" comparison of two IPv6 "
                        + "IPRange instances.", -1, comparator.compare(
                        smallerRange2, largerRange2));

        assertEquals(
                "Failed performing a \"greater than\" comparison of two IPv6 "
                        + "IPRange instances.", 1, comparator.compare(
                        largerRange2, smallerRange2));

        // Test comparison with lower/higher ranges, having the same end
        // address.
        final IPRange smallerRange3 = new IPRange(lowIPv6Address1,
                highIPv6Address2);
        final IPRange largerRange3 = new IPRange(lowIPv6Address2,
                highIPv6Address2);
        assertEquals(
                "Failed performing a \"less than\" comparison of two IPv6 "
                        + "IPRange instances.", -1, comparator.compare(
                        smallerRange3, largerRange3));

        assertEquals(
                "Failed performing a \"greater than\" comparison of two IPv6 "
                        + "IPRange instances.", 1, comparator.compare(
                        largerRange3, smallerRange3));

    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRangeComparator#compare(dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRange, dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRange)}
     * .
     */
    @Test
    public void testCompareErrorScenarios() {

        final IPRangeComparator comparator = new IPRangeComparator();

        final IPRange testIPv6Range = new IPRange(lowIPv6Address2,
                highIPv6Address2);

        final IPRange testIPv4Range = new IPRange(lowIPv4Address2,
                highIPv4Address2);

        try {
            comparator.compare(testIPv4Range, testIPv6Range);
            fail("Expected a ClassCastException when comparing an IPv4 "
                    + " IP address range with an IPv6 range.");
        } catch (ClassCastException classCastException) {
            // Good. That was the correct answer.
        }

        // Now, check the other way around.
        try {
            comparator.compare(testIPv6Range, testIPv4Range);
            fail("Expected a ClassCastException when comparing an IPv6 "
                    + " IP address range with an IPv4 range.");
        } catch (ClassCastException classCastException) {
            // Good. That was the correct answer.
        }

        try {
            comparator.compare(null, testIPv4Range);
            fail("Expected an NullPointerException when comparing null with an IPv4 IPRange instance.");
        } catch (NullPointerException nullPointerException) {
            // Good. That was the correct answer.
        }

        // Now, check the other way around.
        try {
            comparator.compare(testIPv4Range, null);
            fail("Expected an NullPointerException when comparing an IPv4 IPRange instance with null.");
        } catch (NullPointerException nullPointerException) {
            // Good. That was the correct answer.
        }
    }

}
