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
package dk.statsbiblioteket.doms.iprolemapper.servlets;

import java.io.File;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.xml.DOMConfigurator;

/**
 * This servlet will look up the servlet init-param key called <code>
 * &quot;&lt;this package name&gt;.&lt;this class name&gt;.loglog4jConfigurationPropertyKey&quot;
 * </code> to obtain
 * the context-parameter key which has been assiged with the file path to a XML
 * log4j configuration, and initialise log4j with that.
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class Log4JInitServlet extends HttpServlet {

    private static final long serialVersionUID = 4530169251282101347L;

    public void init() {

        final String className = getClass().getName();

        final String log4jConfigurationPropertyKey = className
                + ".log4jConfigurationPropertyKey";

        getServletContext().log(
                className + ".init(): Fetching the log4j configuration file "
                        + "path parameter key assigned to the parameter key:"
                        + log4jConfigurationPropertyKey);

        final String log4jConfigurationPathKey = getInitParameter(log4jConfigurationPropertyKey);

        getServletContext().log(
                className + ".init(): Looking up the log4j configuration file"
                        + " path assigned to the context parameter key '"
                        + log4jConfigurationPathKey + "'.");

        final String log4jconfigPath = getServletContext().getInitParameter(
                log4jConfigurationPathKey);

        getServletContext().log(
                className + ".init(): About to load the log4j configuration "
                        + "file: " + log4jconfigPath);

        try {
            // Attempt reading from the file system.
            File configFile = new File(log4jconfigPath);
            if (!configFile.exists()) {
                // The file could not be found, either because the path is not
                // an absolute path or because it does not exist. Now try
                // locating it within the WAR file before giving up.
                configFile = new File(getServletContext().getRealPath(
                        log4jconfigPath));
            }
            DOMConfigurator.configure(configFile.getAbsolutePath());

            getServletContext().log(
                    className + ".init(): Successfully initialised log4j, "
                            + "using the configuration file: "
                            + log4jconfigPath);
        } catch (RuntimeException runtimeException) {
            // The above code throws no checked exceptions, however, make sure
            // that no runtime exceptions goes by unnoticed.
            getServletContext().log(
                    className + ".init(): Failed configuring log4j. The "
                            + "configuration file path context parameter key "
                            + "specified by the '"
                            + log4jConfigurationPropertyKey
                            + "' init-parameter key was: '"
                            + log4jConfigurationPathKey
                            + "' and the configuration file path specified by "
                            + "that was: '" + log4jconfigPath + "'.",
                    runtimeException);
            throw runtimeException;
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) {
    }
}
