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
 * the context-parameter key which has been assiged with the file path to an XML
 * log4j configuration (that is <b>not</b> a <code>.properties</code> file), and
 * initialise log4j with that.
 * <p/>
 * Add a section like the following to your <code>web.xml</code> file in order
 * to use this servlet to initialise log4j for your web application:
 * 
 * <pre>
 *     &lt;servlet&gt;
 *         &lt;servlet-name&gt;Log4jInitialisation&lt;/servlet-name&gt;
 *         &lt;servlet-class&gt;full.package.name.to.Log4JInitServlet&lt;/servlet-class&gt;
 * 
 *         &lt;init-param&gt;
 *             &lt;param-name&gt;
 *                 must.be.full.package.name.of.the.package.containing.Log4JInitServlet.appended.with.log4jConfigurationPropertyKey&lt;/param-name&gt;
 *             &lt;param-value&gt;full.package.name.to.your.web-app.plus.a.name.of.your.choice.eg.log4jConfigurationFilePath&lt;/param-value&gt;
 *         &lt;/init-param&gt;
 *         &lt;load-on-startup&gt;1&lt;/load-on-startup&gt;
 *     &lt;/servlet&gt;
 * </pre>
 * <p/>
 * You must declare the above context-param
 * <code>full.package.name.to.your.web-app.plus.a.name.of.your.choice.eg.log4jConfigurationFilePath</code>
 * in the tomcat configuration (eg. in the <code>context.xml</code> file) and
 * assign that with the file path to the log4j configuration, in order to make
 * it all work. This servlet will attempt to log any initialisation issues to
 * the default log provided by the servlet context.
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class Log4JInitServlet extends HttpServlet {

    private static final long serialVersionUID = 4530169251282101347L;

    public void init() {

        final String className = getClass().getName();

        String log4jConfigurationPropertyKey = null;
        String log4jConfigurationPathKey = null;
        String log4jconfigPath = null;

        try {
            log4jConfigurationPropertyKey = className
                    + ".log4jConfigurationPropertyKey";

            log4jConfigurationPathKey = getInitParameter(log4jConfigurationPropertyKey);

            log4jconfigPath = getServletContext().getInitParameter(
                    log4jConfigurationPathKey);

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
                            + "using the configuration file: '"
                            + configFile.getAbsolutePath()
                            + "' specified by the context-param: "
                            + log4jConfigurationPathKey);
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
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) {
    }
}
