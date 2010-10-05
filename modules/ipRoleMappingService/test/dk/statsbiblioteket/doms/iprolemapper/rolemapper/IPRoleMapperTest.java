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

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *@author &lt;tsh@statsbiblioteket.dk&gt; Thomas Skou Hansen
 */
public class IPRoleMapperTest {

    private final List<IPRange> ipRanges;
    private IPRoleMapper ipRoleMapper;
    
    /**
     * @throws UnknownHostException 
     * 
     */
    public IPRoleMapperTest() throws UnknownHostException {
       
       final List<String> roles = Arrays.asList("public", "secret", "topsecret");
       final InetAddress beginAddress = InetAddress.getByName("192.168.0.1");
       final InetAddress endAddress = InetAddress.getByName("192.168.0.254");
       final IPRange testRange = new IPRange(beginAddress, endAddress, roles);
       
       ipRanges = new LinkedList<IPRange>();
       ipRanges.add(testRange);
    }
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        IPRoleMapper.init(ipRanges);
        ipRoleMapper = new IPRoleMapper();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRoleMapper#mapIPHost(java.net.InetAddress)}.
     * @throws UnknownHostException 
     */
    @Test
    public void testMapIPHost() throws UnknownHostException {
        final InetAddress addressToMap = InetAddress.getByName("192.168.0.34");
        final String roles = ipRoleMapper.mapIPHost(addressToMap);
        assertEquals("public,secret,topsecret", roles);
    }
}
