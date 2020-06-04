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

import static org.junit.jupiter.api.Assertions.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.jupiter.api.Test;


/**
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class IPRangeTest {

    private final InetAddress testBeginAddress;
    private final InetAddress testEndAddress;

    private final IPRange testIPRange;

    /**
     * @throws UnknownHostException
     *             if hard-coded IP addresses are illegal. This will not happen.
     */
    public IPRangeTest() throws UnknownHostException {

        testBeginAddress = InetAddress.getByName("192.168.0.100");
        testEndAddress = InetAddress.getByName("192.168.0.200");
        testIPRange = new IPRange(testBeginAddress, testEndAddress);
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

            // Verify that the constructor coughs up blood....
            new IPRange(beginAddress, endAddress);

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

        // Test handling of mixing IPv4 and IPv6 begin and end addresses.
        try {

            // Verify that the constructor coughs up blood....
            new IPRange(ipV4Address, ipV6Address);

            fail("Expected an IllegalArgumentException to be thrown.");
        } catch (IllegalArgumentException illegalArgumentException) {
            // That was the correct answer.
        }

        // Now check the other way around...
        try {
            // Verify that the constructor coughs up blood....
            new IPRange(ipV6Address, ipV4Address);

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
     * {@link dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRange#overlaps(IPRange)}
     * .
     * 
     * @throws UnknownHostException
     *             if hard-coded IP addresses are illegal. This will not happen.
     */
    @Test
    public void testOverlaps() throws UnknownHostException {

        // This test assumes that the testRange attribute has been assigned the
        // range: 192.168.0.100 - 192.168.0.200

        // First, test overlap scenarios
        final InetAddress beforeStartRangeAddress1 = InetAddress
                .getByName("192.168.0.1");

        final InetAddress inRangeAddress1 = InetAddress
                .getByName("192.168.0.123");

        final InetAddress afterEndRangeAddress1 = InetAddress
                .getByName("192.168.0.213");

        final InetAddress inRangeAddress2 = InetAddress
                .getByName("192.168.0.189");

        IPRange overlappingRange = new IPRange(beforeStartRangeAddress1,
                inRangeAddress1);
        assertTrue(testIPRange.overlaps(overlappingRange));

        overlappingRange = new IPRange(inRangeAddress1, afterEndRangeAddress1);
        assertTrue(testIPRange.overlaps(overlappingRange));

        overlappingRange = new IPRange(beforeStartRangeAddress1,
                afterEndRangeAddress1);
        assertTrue(testIPRange.overlaps(overlappingRange));

        overlappingRange = new IPRange(inRangeAddress1, inRangeAddress2);
        assertTrue(testIPRange.overlaps(overlappingRange));

        // Test non-overlap scenarios.
        final InetAddress beforeStartRangeAddress2 = InetAddress
                .getByName("192.168.0.12");

        final InetAddress afterEndRangeAddress2 = InetAddress
                .getByName("192.168.0.253");

        IPRange nonOverlappingRange = new IPRange(beforeStartRangeAddress1,
                beforeStartRangeAddress2);
        assertFalse(testIPRange.overlaps(nonOverlappingRange));

        nonOverlappingRange = new IPRange(afterEndRangeAddress1,
                afterEndRangeAddress2);
        assertFalse(testIPRange.overlaps(nonOverlappingRange));
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRange#merge(IPRange)}
     * .
     * 
     * @throws UnknownHostException
     *             if hard-coded IP addresses are illegal. This will not happen.
     */
    @Test
    public void testMerge() throws UnknownHostException {

        // This test assumes that the testRange attribute has been assigned the
        // range: 192.168.0.100 - 192.168.0.200

        // First, test overlap scenarios
        final InetAddress address1 = InetAddress.getByName("192.168.0.1");
        final InetAddress address2 = InetAddress.getByName("192.168.0.12");
        final InetAddress address3 = InetAddress.getByName("192.168.0.123");
        final InetAddress address4 = InetAddress.getByName("192.168.0.189");
        final InetAddress address5 = InetAddress.getByName("192.168.0.213");
        final InetAddress address6 = InetAddress.getByName("192.168.0.253");

        // Test partial overlap of the beginning of the range.
        IPRange overlappingRange = new IPRange(address1, address3);
        IPRange expectedMergeResult = new IPRange(overlappingRange
                .getBeginAddress(), testIPRange.getEndAddress());
        assertEquals(expectedMergeResult, testIPRange.merge(overlappingRange));

        // Test merging with a range which overlaps completely.
        overlappingRange = new IPRange(address1, address5);
        assertEquals(overlappingRange, testIPRange.merge(overlappingRange));

        // Test partial overlap of the end of the range.
        overlappingRange = new IPRange(address4, address5);
        expectedMergeResult = new IPRange(testIPRange.getBeginAddress(),
                overlappingRange.getEndAddress());
        assertEquals(expectedMergeResult, testIPRange.merge(overlappingRange));

        // Test merging with a range which lies within the test range.
        overlappingRange = new IPRange(address3, address4);
        assertEquals(testIPRange, testIPRange.merge(overlappingRange));

        // Test the error handling when attempting merging non-overlapping
        // ranges.
        IPRange nonOverlappingRange = new IPRange(address1, address2);
        try {
            testIPRange.merge(nonOverlappingRange);
            fail("Expected exception when merging non-overlapping IP ranges: "
                    + nonOverlappingRange + ", " + testIPRange);
        } catch (IllegalArgumentException illegalArgumentException) {
            // That was expected. Continue....
        }

        nonOverlappingRange = new IPRange(address5, address6);
        try {
            testIPRange.merge(nonOverlappingRange);
            fail("Expected exception when merging non-overlapping IP ranges: "
                    + nonOverlappingRange + ", " + testIPRange);
        } catch (IllegalArgumentException illegalArgumentException) {
            // That was expected. Continue....
        }
        assertTrue(true);
    }
}
