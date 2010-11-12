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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Comparator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 * 
 */
public class SortedListTest {

    private SortedList<Integer> testList;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        testList = new SortedList<Integer>(new Comparator<Integer>() {

            /* (non-Javadoc)
             * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
             */
            public int compare(Integer integer1, Integer integer2) {
                return integer1.compareTo(integer2);
            }
        });
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#add(java.lang.Object)}
     * .
     */
    @Test
    public void testAdd() {
        Integer[] testValues = { 10, 2, 3, 0, 2, 7, 2, 6, 1, 0, 10, 7, 8, 2 };
        for (Integer integer : testValues) {
            testList.add(integer);
        }

        Arrays.sort(testValues);
        Integer[] testListArray = new Integer[11];
        assertTrue(Arrays.deepEquals(testValues, testList
                .toArray(testListArray)));
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#addAll(java.util.Collection)}
     * .
     */
    @Test
    public void testAddAll() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#clear()}.
     */
    @Test
    public void testClear() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#contains(java.lang.Object)}
     * .
     */
    @Test
    public void testContains() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#containsAll(java.util.Collection)}
     * .
     */
    @Test
    public void testContainsAll() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#get(int)}.
     */
    @Test
    public void testGet() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#indexOf(java.lang.Object)}
     * .
     */
    @Test
    public void testIndexOf() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#isEmpty()}.
     */
    @Test
    public void testIsEmpty() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#iterator()}
     * .
     */
    @Test
    public void testIterator() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#lastIndexOf(java.lang.Object)}
     * .
     */
    @Test
    public void testLastIndexOf() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#remove(int)}
     * .
     */
    @Test
    public void testRemoveInt() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#remove(java.lang.Object)}
     * .
     */
    @Test
    public void testRemoveT() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#removeAll(java.util.Collection)}
     * .
     */
    @Test
    public void testRemoveAll() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#retainAll(java.util.Collection)}
     * .
     */
    @Test
    public void testRetainAll() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#size()}.
     */
    @Test
    public void testSize() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#subList(int, int)}
     * .
     */
    @Test
    public void testSubList() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#toArray()}.
     */
    @Test
    public void testToArray() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#toArray(T[])}
     * .
     */
    @Test
    public void testToArrayTArray() {
        fail("Not yet implemented");
    }

}
