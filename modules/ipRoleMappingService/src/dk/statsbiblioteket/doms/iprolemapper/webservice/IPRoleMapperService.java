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
package dk.statsbiblioteket.doms.iprolemapper.webservice;

import javax.ws.rs.Path;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.core.Response;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import dk.statsbiblioteket.doms.webservices.ConfigCollection;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpServletResponseWrapper;

/**
 *@author &lt;tsh@statsbiblioteket.dk&gt; Thomas Skou Hansen
 * 
 */
@Path("/")
public class IPRoleMapperService {

    private static final Log log = LogFactory.getLog(IPRoleMapperService.class);

    // @Context
    // private HttpServletRequest request;

    /**
     * 
     */
    public IPRoleMapperService() {

    }

    @GET
    @Path("getRoles/{ipaddress}")
    @Produces("text/xml")
    public String getRoles(@PathParam("ipaddress") String ipAddress) {
        log.trace("IPRoleMapperService.getRoles(): Called with IP adress: '"
                + ipAddress + "'");
        return "<hello>" + ipAddress + "</hello>";
    }

}
