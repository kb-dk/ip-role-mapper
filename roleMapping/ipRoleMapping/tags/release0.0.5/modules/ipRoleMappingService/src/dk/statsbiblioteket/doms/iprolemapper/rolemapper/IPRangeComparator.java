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

import java.util.Comparator;

/**
 * This comparator compares the begin address of two <code>IPRange</code>
 * instances and returns -1, 0 or 1 in accordance with the
 * <code>Comparator</code> interface. However, if the begin address of the two
 * ranges are equal to each other then their end addresses will determine
 * whether they are smaller than, equal or greater than each other.
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class IPRangeComparator implements Comparator<IPRange> {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(IPRange ipRange1, IPRange ipRange2) {

        final InetAddressComparator inetAddressComparator = new InetAddressComparator();
        final int startAddressComparison = inetAddressComparator.compare(
                ipRange1.getBeginAddress(), ipRange2.getBeginAddress());

        if (startAddressComparison == 0) {
            // The begin addresses are the same. Let the end addresses determine
            // the result.
            return inetAddressComparator.compare(ipRange1.getEndAddress(),
                    ipRange2.getEndAddress());
        }

        return startAddressComparison;
    }
}
