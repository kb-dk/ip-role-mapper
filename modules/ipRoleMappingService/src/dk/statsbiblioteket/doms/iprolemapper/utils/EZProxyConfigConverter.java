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
package dk.statsbiblioteket.doms.iprolemapper.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringWriter;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import dk.statsbiblioteket.doms.iprolemapper.rolemapper.IPRange;
import dk.statsbiblioteket.util.xml.DOM;

/**
 *@author &lt;tsh@statsbiblioteket.dk&gt; Thomas Skou Hansen
 * 
 */
public class EZProxyConfigConverter {

    /**
     * Convert an EZproxy configuration file to an IPRoleMapper configuration
     * file.
     * 
     * @param args
     *            argument list containing the input and output file names.
     * @throws IOException
     *             if any errors are encountered while reading the EZProxy
     *             configuration.
     * @throws ParseException
     *             if any errors are encountered while parsing the EZProxy
     *             configuration.
     * @throws ParserConfigurationException if creation of the document builder for building the <code>IPRangeMapperService</code> configuration failed. 
     * @throws TransformerException if transformation to a garbage truck failed.
     */
    public static void main(String[] args) throws IOException, ParseException, ParserConfigurationException, TransformerException {
        if (args.length != 2) {
            System.out.println("Usage: "
                    + EZProxyConfigConverter.class.getSimpleName()
                    + " <EZProxy configuration file name> <IP role mapper "
                    + "configuration file name>");
            System.exit(1);
        }

        final Reader ezReader = new FileReader(args[0]);
        final StreamTokenizer ezTokenizer = new StreamTokenizer(ezReader);
        ezTokenizer.resetSyntax();
        ezTokenizer.whitespaceChars(' ', ' ');
        ezTokenizer.whitespaceChars('*', '*');
        ezTokenizer.whitespaceChars(':', ':');
        ezTokenizer.whitespaceChars('=', '=');

        ezTokenizer.wordChars('a', 'å');
        ezTokenizer.wordChars('.', '.');
        ezTokenizer.wordChars('0', '9');
        ezTokenizer.wordChars(',', ',');

        int ttype = ezTokenizer.nextToken();
        List<IPRange> ipRanges = new LinkedList<IPRange>();
        List<String> roles = new LinkedList<String>();
        while (ttype != StreamTokenizer.TT_EOF) {
            switch (ttype) {
            case StreamTokenizer.TT_EOL:
                // Just ignore.
                break;
            case StreamTokenizer.TT_NUMBER:
                // There should not be any numbers and if there is, then ignore
                // them.
                break;
            case StreamTokenizer.TT_WORD:
                if ("ip".equals(ezTokenizer.sval.toLowerCase())) {
                    // Expect an IP address range.
                    ipRanges.add(readIPRange(ezTokenizer, roles));
                } else {
                    throw new ParseException(
                            "Un-expected token in the EZProxy configuration: "
                                    + ezTokenizer.sval, ezTokenizer.lineno());
                }
                break;
            case '#':
                // Trash the current role list and collect the new roles.
                roles = readRoles(ezTokenizer);
                break;
            default:
                System.out.println("Woot? ttype = " + ttype + " ( = '"
                        + new String(Character.toChars(ttype)) + "')");
                break;
            }

            ttype = ezTokenizer.nextToken();
        }
        
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        final Document ipRangeMapperConfig = documentBuilder.newDocument();
        
        final Element ipRangesElement = ipRangeMapperConfig.createElement("ipranges");
        ipRangeMapperConfig.appendChild(ipRangesElement);
        
        // Create an <iprange> element for each IPRange object. 
        for (IPRange ipRange : ipRanges) {
            final Element ipRangeElement = ipRangeMapperConfig.createElement("iprange");
            ipRangesElement.appendChild(ipRangeElement);
            ipRangeElement.setAttribute("begin", ipRange.getBeginAddress().getHostAddress());
            ipRangeElement.setAttribute("end", ipRange.getEndAddress().getHostAddress());
            
            // Add role child nodes.
            for (String roleName : ipRange.getRoles()) {
                final Element roleElement = ipRangeMapperConfig.createElement("role");
                ipRangeElement.appendChild(roleElement);
                roleElement.setTextContent(roleName);
            }
        }
        
        final DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(args[1]));
        dataOutputStream.write(DOM.domToString(ipRangeMapperConfig,true).getBytes());
    }

    /**
     * Expect that <code>ezTokenizer</code> is ready to read a word/string and
     * expect that it contains a valid IP address or host name. If the next
     * token after the address is a minus (-) then the method expects to find
     * yet another address, the range end address. If no end address is found,
     * then the returned <code>IPRange</code> instancs
     * 
     * @param ezTokenizer
     * @param roles
     * @return
     * @throws IOException
     *             if any errors are encountered while reading the next token
     *             from the tokenizer.
     * @throws ParseException
     *             if an un-expected token is encountered.
     */
    private static IPRange readIPRange(StreamTokenizer ezTokenizer,
            List<String> roles) throws IOException, ParseException {

        final String beginIP = readWord(ezTokenizer);

        // The end is the same as the beginning until we are told something
        // else.
        String endIP = beginIP;
        final int ttype = ezTokenizer.nextToken();

        if (ttype != '-' && ttype != StreamTokenizer.TT_EOL) {
            throw new ParseException(
                    "Failed reading IP address range. Un-expected token in the EZProxy configuration: "
                            + new String(Character.toChars(ttype)), ezTokenizer
                            .lineno());
        } else if (ttype != StreamTokenizer.TT_EOL) {
            // It's an IP range. Get the end address.
            endIP = readWord(ezTokenizer);
        } // else: it is not an actual range, thus the end address will be the
        // same as the begin address.

        return new IPRange(InetAddress.getByName(beginIP), InetAddress
                .getByName(endIP), roles);
    }

    /**
     * Expect that <code>ezTokenizer</code> is ready to read a word/string and
     * expect that it is not empty. If the string contains a comma separated
     * list then a role name is created for each of the elements. Consequently,
     * role names cannot contain commas otherwise this method will fail.
     * 
     * @param ezTokenizer
     *            a <code>Tokenizer</code> expected to be ready to read a
     *            string.
     * @return a list containing all the role names found.
     * @throws IOException
     *             if any errors are encountered while reading the next token
     *             from the tokenizer.
     * @throws ParseException
     *             if the next token is not a word/string.
     */
    private static List<String> readRoles(StreamTokenizer ezTokenizer)
            throws IOException, ParseException {

        // Build one long string of the rest of the line contents.
        String roleString = "";
        try {
            while (true) {
                // The space is a bit of a hack. If there is a space in a role
                // name, then the tokenizer will see it as two separate names
                // and strip the space. Thus, it is added again. Surplus white
                // spaces will be stripped later on.
                roleString += readWord(ezTokenizer) + " ";
            }
        } catch (ParseException parseException) {
            // No big deal. We just finished building the string.
        }

        final List<String> roleList = new LinkedList<String>();

        // If the role string contains a comma separated list of role names then
        // create an role name for each of them.
        for (int roleStringIndex = 0; roleStringIndex < roleString.length();) {
            int roleEndIndex = roleString.indexOf(',', roleStringIndex);
            roleEndIndex = (roleEndIndex == -1) ? roleString.length()
                    : roleEndIndex;
            roleList.add(roleString.substring(roleStringIndex, roleEndIndex)
                    .trim());
            roleStringIndex = roleEndIndex + 1;
        }
        return roleList;
    }

    /**
     * Read the next token from <code>tokenizer</code> and expect it to be a
     * word (i.e. a string). That is, the method fails if the next token is not
     * a word.
     * 
     * @param tokenizer
     *            a <code>Tokenizer</code> expected to be ready to read a word
     *            (i.e. string).
     * @return the <code>String</code> read from the tokenizer.
     * @throws IOException
     *             if any errors are encountered while reading the next token
     *             from the tokenizer.
     * @throws ParseException
     *             if the next token is not a word/string.
     */
    private static String readWord(StreamTokenizer tokenizer)
            throws IOException, ParseException {
        int ttype = tokenizer.nextToken();
        if (ttype != StreamTokenizer.TT_WORD) {
            throw new ParseException(
                    "Failed reading a string/word from the EZProxy configuration.",
                    tokenizer.lineno());
        }

        return tokenizer.sval;
    }
}
