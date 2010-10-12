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

import java.io.IOException;
import java.net.UnknownHostException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.sun.jersey.api.client.ClientResponse;

/**
 *@author &lt;tsh@statsbiblioteket.dk&gt; Thomas Skou Hansen
 */
@Provider
public class ServiceExceptionMapper implements ExceptionMapper<Exception> {

    /* (non-Javadoc)
     * @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable)
     */
    @Override
    public Response toResponse(Exception exception) {

        if (exception instanceof UnknownHostException) {
            
            // Complain about an invalid host address.
            return Response.status(ClientResponse.Status.BAD_REQUEST).entity(
                    "Could not lookup roles for the requested host. Reason: "
                            + exception.toString()).build();
        } else if (exception instanceof XPathExpressionException
                || exception instanceof IOException
                || exception instanceof SAXException) {
            
            // Complain over a broken service configuration.
            return Response.status(ClientResponse.Status.INTERNAL_SERVER_ERROR)
                    .entity(
                            "Service configuration error: "
                                    + exception.toString()).build();
        } else {
            
            // Any un-expected exception also leads to a server error.
            return Response.serverError().entity(
                    "The service could not process the request due to an"
                            + " error. Please consult the server log. The "
                            + "cause to the failure was: "
                            + exception.toString()).build();
        }
    }
}
