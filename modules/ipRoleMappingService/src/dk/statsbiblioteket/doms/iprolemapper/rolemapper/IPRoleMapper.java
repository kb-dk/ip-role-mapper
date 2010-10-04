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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *@author &lt;tsh@statsbiblioteket.dk&gt; Thomas Skou Hansen
 * 
 */
public class IPRoleMapper {

    private TreeSet<InetAddress> rangeStartAddresses;

    private static TreeMap<InetAddress, HashSet<IPRange>> startAddrRangeMapSet;

    /**
     * 
     */
    public IPRoleMapper() {
    }

    /**
     * Map a host name or IP address to one or more roles.
     * 
     * @param ipAddress
     * @return
     */
    public String mapIPHost(InetAddress ipAddress) {
        String collectedRoles = "";
        synchronized (startAddrRangeMapSet) {
            // Get all ranges starting before or at the given ipAddress.
            Collection<HashSet<IPRange>> allPotentialMatchingRangeSets = startAddrRangeMapSet
                    .headMap(ipAddress, true).values();
            
            for (HashSet<IPRange> potentialMatchingRangeSet : allPotentialMatchingRangeSets) {
                
                for (IPRange candidateRange : potentialMatchingRangeSet) {
                    InetAddressComparator ipComparator = new InetAddressComparator();
                    if (ipComparator.compare(ipAddress, candidateRange.getBeginAddress()) == 0) {
                        List<String> rangeRoles = candidateRange.getRoles();
                        for (int roleIdx = 0; roleIdx < rangeRoles.size(); roleIdx++) {
                            collectedRoles += rangeRoles.get(roleIdx);
                            if (roleIdx < rangeRoles.size() - 1) {
                                collectedRoles += ",";
                                //FIXME! Hmm, setting commas will not be that easy. It's probably better collecting roles first and then building the string afterwards.
                            }
                        }
                    }
                }
            }
        }
        return collectedRoles;
    }

    public static void init(List<IPRange> ranges) {
        synchronized (startAddrRangeMapSet) {
            startAddrRangeMapSet = new TreeMap<InetAddress, HashSet<IPRange>>(new InetAddressComparator());
            for (IPRange range : ranges) {
                HashSet<IPRange> rangeSet = startAddrRangeMapSet.get(range
                        .getBeginAddress());
                if (rangeSet == null) {
                    rangeSet = new HashSet<IPRange>();
                    startAddrRangeMapSet.put(range.getBeginAddress(), rangeSet);
                }
                rangeSet.add(range);
            }// end-for
        }
    }
}
