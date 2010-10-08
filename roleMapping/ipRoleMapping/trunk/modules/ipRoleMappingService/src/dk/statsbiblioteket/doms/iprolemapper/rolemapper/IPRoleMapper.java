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
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * The <code>IPRoleMapper</code> is capable of associating a number of roles
 * with an IP address based on a number of IP address ranges, which again are
 * associated with a number of roles.
 * 
 * @author &lt;tsh@statsbiblioteket.dk&gt; Thomas Skou Hansen
 */
public class IPRoleMapper {

    /**
     * A map for mapping an IP range start address to a list of all IP ranges
     * starting at that particular address.
     */
    private static TreeMap<InetAddress, LinkedList<IPRangeRoles>> startAddrRangeMapList;

    /**
     * A map for mapping all known role names with the ranges associated with
     * them.
     */
    private static TreeMap<String, LinkedList<IPRange>> roleIPRangeMapList;

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

        return collectedRoles;
    }

    
    /**
     * Get a set of address ranges associated with the role names specified by
     * <code>roles</code>.
     * 
     * @param roles a set of role names to find associated address ranges for.
     * @return all the <code>IPRange</code> instances associated with the roles.
     */
    public Set<IPRange> mapRoles(Set<String> roles) {

        // Collect all the ranges associated with the roles specified. Use a set
        // to eliminate duplicate IPRange objects.
        final Set<IPRange> associatedRanges = new HashSet<IPRange>();
        for (String role : roles) {
            associatedRanges.addAll(roleIPRangeMapList.get(role));
        }
        return associatedRanges;
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
    }
}
