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

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import dk.statsbiblioteket.doms.iprolemapper.utils.SortedList;

/**
 * The <code>IPRoleMapper</code> is capable of associating a number of roles
 * with an IP address based on a number of IP address ranges, which again are
 * associated with a number of roles.
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class IPRoleMapper {

    private static final Log log = LogFactory.getLog(IPRoleMapper.class);

    /**
     * A map for mapping an IP range start address to a list of all IP ranges
     * starting at that particular address.
     */
    private static TreeMap<InetAddress, LinkedList<IPRangeRoles>> startAddrRangeMapList = new TreeMap<InetAddress, LinkedList<IPRangeRoles>>(
            new InetAddressComparator());

    /**
     * A map for mapping all known role names with the ranges associated with
     * them.
     */
    private static TreeMap<String, LinkedList<IPRange>> roleIPRangeMapList = new TreeMap<String, LinkedList<IPRange>>();

    /**
     * Map a host name or IP address to one or more roles all roles of the known
     * ip ranges matching <code>ipAddress</code> will be collected. The roles
     * are returned as a comma separated list in a <code>String</code>.
     * 
     * @param ipAddress
     *            the IP address or host name to get roles for.
     * @return a <code>String</code> containing a comma separated list of all
     *         matching roles.
     */
    public Set<String> mapIPHost(InetAddress ipAddress) {

        if (log.isTraceEnabled()) {
            log.trace("mapIPHost(): Called with InetAddress: " + ipAddress);
        }

        final Set<String> collectedRoles = new TreeSet<String>();

        Collection<LinkedList<IPRangeRoles>> allPotentialMatchingRangeSets;

        // Synchronise with the static init() method.
        synchronized (IPRoleMapper.class) {
            // Get all ranges starting before or at the given ipAddress.
            allPotentialMatchingRangeSets = startAddrRangeMapList.headMap(
                    ipAddress, true).values();
        }

        final InetAddressComparator ipComparator = new InetAddressComparator();
        for (List<IPRangeRoles> potentialMatchingRanges : allPotentialMatchingRangeSets) {

            for (IPRangeRoles candidateRange : potentialMatchingRanges) {

                // Test ipAddress lies within the candidate range.
                if ((ipComparator.compare(ipAddress, candidateRange
                        .getBeginAddress()) == 0 || ipComparator.compare(
                        ipAddress, candidateRange.getBeginAddress()) > 0)
                        && (ipComparator.compare(ipAddress, candidateRange
                                .getEndAddress()) == 0 || ipComparator.compare(
                                ipAddress, candidateRange.getEndAddress()) < 0)) {

                    // Range match! Collect the roles from this range into a
                    // set in order to eliminate any duplicates.
                    collectedRoles.addAll(candidateRange.getRoles());
                } // end-if
            } // end-for test of candidate range list.
        }

        if (log.isTraceEnabled()) {
            log.trace("mapIPHost(): Returning collected roles: "
                    + collectedRoles);
        }

        return collectedRoles;
    }

    /**
     * Get a set of address ranges associated with the role names specified by
     * <code>roles</code>. Any overlapping ranges will be merged.
     * 
     * @param roles
     *            a set of role names to find associated address ranges for.
     * @return all the <code>IPRange</code> instances associated with the roles.
     */
    public Set<IPRange> mapRoles(Set<String> roles) {

        if (log.isTraceEnabled()) {
            log.trace("mapRoles(): Called with roles: " + roles);
        }

        // Collect all the ranges associated with the roles specified. The
        // sorted list will keep the IPRange objects ordered in a non-descending
        // order.
        final SortedList<IPRange> associatedRanges = new SortedList<IPRange>(
                new IPRangeComparator());
        for (String role : roles) {
            final List<IPRange> associatedRoleRanges = roleIPRangeMapList
                    .get(role);
            if (associatedRoleRanges != null) {
                associatedRanges.addAll(associatedRoleRanges);
            }
        }

        // Now iterate through all the ranges, merge any overlapping ranges and
        // remove any redundant ranges.

        final Set<IPRange> mergedRanges = new HashSet<IPRange>();
        while (associatedRanges.isEmpty() == false) {
            final IPRange lowestRange = associatedRanges.get(0);

            // Remove all occurrences of the lowest range as it will be
            // collected under any circumstances.
            associatedRanges.removeAll(Arrays.asList(lowestRange));

            IPRange neighbourRange = null;
            if (associatedRanges.isEmpty() == false) {
                neighbourRange = associatedRanges.get(0);
            }

            if ((neighbourRange == null)
                    || (lowestRange.overlaps(neighbourRange) == false)) {

                // No more ranges or they do not overlap. Done...
                mergedRanges.add(lowestRange);
            } else {

                // The ranges overlap each other. Merge them and any
                // subsequently overlapping ranges.
                IPRange mergedRange = lowestRange;
                while ((associatedRanges.isEmpty() == false)
                        && mergedRange.overlaps(neighbourRange)) {

                    // Remove all occurrences of the overlapping neighbour
                    // range.
                    associatedRanges.removeAll(Arrays.asList(neighbourRange));

                    // Update the merged range.
                    mergedRange = mergedRange.merge(neighbourRange);

                    // Prepare testing the next neighbour if there is any.
                    if (associatedRanges.isEmpty() == false) {
                        neighbourRange = associatedRanges.get(0);
                    }
                }
                mergedRanges.add(mergedRange);
            }
        }

        if (log.isTraceEnabled()) {
            log.trace("mapRoles(): Returning IP address ranges: "
                    + associatedRanges);
        }

        return mergedRanges;
    }

    /**
     * Test whether this <code>IPRoleMapper</code> currently contains any
     * mapping information.
     * 
     * @return <code>true</code> if this <code>IPRoleMapper</code> does not
     *         contain any mapping information and otherwise <code>false</code>.
     */
    public boolean isEmpty() {
        return startAddrRangeMapList.isEmpty();
    }

    /**
     * (re-)initialise the internal database over IP ranges and roles. This
     * method should only be called for the initial initialisation and if the
     * configuration changes.
     * 
     * @param ranges
     *            a list of IP range and role information to initialise the
     *            database with.
     */
    public static synchronized void init(List<IPRangeRoles> ranges) {

        if (log.isTraceEnabled()) {
            log.trace("init(): Called with IPRangeRoles: " + ranges);
        }
        startAddrRangeMapList = new TreeMap<InetAddress, LinkedList<IPRangeRoles>>(
                new InetAddressComparator());

        roleIPRangeMapList = new TreeMap<String, LinkedList<IPRange>>();

        for (IPRangeRoles range : ranges) {

            // Add the range to the start IP address -> IPRangeRoles map.
            LinkedList<IPRangeRoles> rangeList = startAddrRangeMapList
                    .get(range.getBeginAddress());

            if (rangeList == null) {
                // No ranges with this begin address have been registered
                // earlier. Create a container for them.
                rangeList = new LinkedList<IPRangeRoles>();
                startAddrRangeMapList.put(range.getBeginAddress(), rangeList);
            }
            rangeList.add(range);

            // Associate the range with all its roles in the role -> IPRange
            // map.
            for (String roleName : range.getRoles()) {

                LinkedList<IPRange> roleRanges = roleIPRangeMapList
                        .get(roleName);

                if (roleRanges == null) {
                    // There has not previously been associated any ranges with
                    // this role name. Create a new list.
                    roleRanges = new LinkedList<IPRange>();
                    roleIPRangeMapList.put(roleName, roleRanges);
                }
                roleRanges.add(range);
            }
        }// end-for

        if (log.isTraceEnabled()) {
            log.trace("init(): Finished initialisation. Exiting.");
        }
    }
}
