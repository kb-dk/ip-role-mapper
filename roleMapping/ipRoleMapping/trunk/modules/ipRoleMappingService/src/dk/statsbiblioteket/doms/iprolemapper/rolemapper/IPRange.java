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

/**
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class IPRange {

    private final InetAddress beginAddress;
    private final InetAddress endAddress;

    /**
     * Create an IP range starting at <code>beginAddress</code> and ending at
     * <code>endAddress</code>. Both addresses are included in the range.
     * 
     * @param beginAddress
     *            the start address of the range.
     * @param endAddress
     *            the end address of the range.
     * @throws IllegalArgumentException
     *             if the begin address and end address is not of the same type.
     *             I.e. if they are not both IPv4 of IPv6 addresses, or if
     *             <code>beginAddress</code> is larger/higher/after
     *             <code>endAddress</code>.
     */
    public IPRange(InetAddress beginAddress, InetAddress endAddress) {
        if (beginAddress.getClass() != endAddress.getClass()) {
            throw new IllegalArgumentException("The begin and end addresses "
                    + "must be of the same type. beginAddress.getClas() = "
                    + beginAddress.getClass() + "  endAddress.getClass() = "
                    + endAddress.getClass());
        }

        final InetAddressComparator comparator = new InetAddressComparator();
        if (comparator.compare(beginAddress, endAddress) == 1) {
            throw new IllegalArgumentException("The begin addresses must be "
                    + "equal to or before the end address. beginAddress = "
                    + beginAddress + "  endAddress = " + endAddress);
        }

        this.beginAddress = beginAddress;
        this.endAddress = endAddress;
    }

    /**
     * @return the begin address of this IP range.
     */
    public InetAddress getBeginAddress() {
        return beginAddress;
    }

    /**
     * @return the end address of this IP range.
     */
    public InetAddress getEndAddress() {
        return endAddress;
    }

    /**
     * Merge this <code>IPRange</code> instance with the overlapping range
     * specified by <code>overlappingRange</code>. That is, create a new
     * <code>IPRange</code> instance with the lowest begin address and the
     * highest end address of the two ranges.
     * <p/>
     * This method will throw an exception if <code>overlappingRange</code> does
     * not overlap this range.
     * 
     * @param overlappingRange
     *            an <code>IPRange</code> that overlaps this range.
     * @return an <code>IPRange</code> containing the result of the merge
     *         operation.
     * @throws IllegalArgumentException
     *             if <code>overlappingRange</code> does not overlap this range.
     */
    public IPRange merge(IPRange overlappingRange)
            throws IllegalArgumentException {

        if (!overlaps(overlappingRange)) {
            throw new IllegalArgumentException("The IP range: "
                    + overlappingRange + " does not overlap this range: "
                    + toString());
        }

        final InetAddressComparator comparator = new InetAddressComparator();

        final InetAddress resultStartAddress = comparator.compare(
                getBeginAddress(), overlappingRange.getBeginAddress()) < 0 ? getBeginAddress()
                : overlappingRange.getBeginAddress();

        final InetAddress resultEndAddress = comparator.compare(
                getEndAddress(), overlappingRange.getEndAddress()) > 0 ? getEndAddress()
                : overlappingRange.getEndAddress();

        return new IPRange(resultStartAddress, resultEndAddress);
    }

    /**
     * Test whether this <code>IPRange</code> overlaps the IP range specified by
     * <code>ipRange</code>.
     * 
     * @param ipRange
     *            other <code>IPRange</code> instance to test for overlap.
     * @return <code>true</code> if there is an overlap between this IP range
     *         and <code>ipRange</code> and otherwise false.
     */
    public boolean overlaps(IPRange ipRange) {

        final InetAddressComparator comparator = new InetAddressComparator();

        final InetAddress rangeBegin = ipRange.getBeginAddress();
        final InetAddress rangeEnd = ipRange.getEndAddress();

        // The below comparison are based on the (safe) assumption that begin
        // addresses are lower than end addresses. The IPRange implementation
        // ensures this.

        if (comparator.compare(rangeEnd, beginAddress) < 0) {
            return false;
        }

        if (comparator.compare(rangeEnd, endAddress) <= 0) {
            return true;
        }

        if (comparator.compare(endAddress, rangeBegin) < 0) {
            return false;
        }

        if (comparator.compare(endAddress, rangeEnd) <= 0) {
            return true;
        }

        // Never reached. However, the compiler refuses to realise that.
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((beginAddress == null) ? 0 : beginAddress.hashCode());
        result = prime * result
                + ((endAddress == null) ? 0 : endAddress.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof IPRange)) {
            return false;
        }
        IPRange other = (IPRange) obj;
        if (beginAddress == null) {
            if (other.beginAddress != null) {
                return false;
            }
        } else if (!beginAddress.equals(other.beginAddress)) {
            return false;
        }
        if (endAddress == null) {
            if (other.endAddress != null) {
                return false;
            }
        } else if (!endAddress.equals(other.endAddress)) {
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "IPRange: " + beginAddress + " - " + endAddress + "\n";
    }
}