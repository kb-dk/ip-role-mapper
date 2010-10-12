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
import java.util.List;

/**
 *@author &lt;tsh@statsbiblioteket.dk&gt; Thomas Skou Hansen
 */
public class IPRangeRoles extends IPRange {

    private final List<String> roles;

    /**
     * Create an IP range starting at <code>beginAddress</code> and ending at
     * <code>endAddress</code>. Both addresses are included in the range. <\p>
     * The IP range is associated with the roles specified by <code>roles</code>
     * 
     * @param beginAddress
     *            the start address of the range.
     * @param endAddress
     *            the end address of the range.
     * @param roles
     *            the roles associated with the range.
     * @throws IllegalArgumentException
     *             if the begin address and end address is not of the same type.
     *             I.e. if they are not both IPv4 of IPv6 addresses, or if
     *             <code>beginAddress</code> is larger/higher/after
     *             <code>endAddress</code>.
     */
    public IPRangeRoles(InetAddress beginAddress, InetAddress endAddress,
            List<String> roles) throws IllegalArgumentException {
        super(beginAddress, endAddress);
        this.roles = roles;
    }

    /**
     * @return the roles associated with this IP range.
     */
    public List<String> getRoles() {
        return roles;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((roles == null) ? 0 : roles.hashCode());
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
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof IPRangeRoles)) {
            return false;
        }
        IPRangeRoles other = (IPRangeRoles) obj;
        if (roles == null) {
            if (other.roles != null) {
                return false;
            }
        } else if (!roles.equals(other.roles)) {
            return false;
        }
        return true;
    }
}
