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
import java.util.Comparator;

/**
 *@author &lt;tsh@statsbiblioteket.dk&gt; Thomas Skou Hansen
 */
public class InetAddressComparator implements Comparator<InetAddress> {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(InetAddress inetAddress1, InetAddress inetAddress2) {
        if (inetAddress1.getClass() != inetAddress2.getClass()) {
            throw new ClassCastException("The InetAddress instances to "
                    + "compare are not of the same sub-type. "
                    + "inetAddress1.getClass() = " + inetAddress1.getClass()
                    + "  inetAddress2.getClass() = " + inetAddress2.getClass());
        }

        int returnVal = 0;
        final byte inetAddress2bytes[] = inetAddress2.getAddress();
        final byte inetAddress1bytes[] = inetAddress1.getAddress();

        // This should already be ensured by the verification of the class
        // types above.
        assert (inetAddress1bytes.length == inetAddress2bytes.length);

        for (int index = 0; index < inetAddress1bytes.length; index++) {
            if (inetAddress1bytes[index] == inetAddress2bytes[index]) {
                // It's all good, just keep the 0 return value.
                continue;
            }

            // Convert to integer to avoid getting fooled by signed bytes.
            final int addr1byte = inetAddress1bytes[index] & 0xff;
            final int addr2byte = inetAddress2bytes[index] & 0xff;
            
            if (addr1byte < addr2byte) {
                returnVal = -1;
            } else {
                returnVal = 1;
            }
            break;
        }
        return returnVal;
    }
}
