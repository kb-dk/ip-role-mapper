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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class SortedListTest {

    private final Integer[] testValues;
    private final Integer[] sortedTestValues;

    private SortedList<Integer> testList;

    /**
     * 
     */
    public SortedListTest() {
        testValues = new Integer[] { 10, 2, 3, 0, 2, 7, 2, 6, 1, 0, 10, 7, 8, 2 };
        Integer[] testClone = testValues.clone();
        Arrays.sort(testClone);
        sortedTestValues = testClone;
    }

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
        for (Integer integer : testValues) {
            assertTrue(testList.add(integer));
        }

        Integer[] testListArray = new Integer[11];
        assertTrue(Arrays.deepEquals(sortedTestValues, testList
                .toArray(testListArray)));
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#addAll(java.util.Collection)}
     * .
     */
    @Test
    public void testAddAll() {
        assertTrue(testList.addAll(Arrays.asList(testValues)));

        Integer[] testListArray = new Integer[11];
        assertTrue(Arrays.deepEquals(sortedTestValues, testList
                .toArray(testListArray)));
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#clear()}.
     */
    @Test
    public void testClear() {
        testList.addAll(Arrays.asList(testValues));
        assertFalse(testList.isEmpty());
        testList.clear();
        assertTrue(testList.isEmpty());
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#contains(java.lang.Object)}
     * .
     */
    @Test
    public void testContains() {
        testList.addAll(Arrays.asList(testValues));
        assertTrue(testList.contains(testValues[7]));
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#containsAll(java.util.Collection)}
     * .
     */
    @Test
    public void testContainsAll() {
        testList.addAll(Arrays.asList(testValues));
        assertTrue(testList.containsAll(Arrays.asList(sortedTestValues)));
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#get(int)}.
     */
    @Test
    public void testGet() {

        testList.addAll(Arrays.asList(testValues));

        // Expect the same element value at the same positions of the two sorted
        // lists.
        final int testProbeIndex = 7;
        assertEquals(sortedTestValues[testProbeIndex], testList
                .get(testProbeIndex));
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#indexOf(java.lang.Object)}
     * .
     */
    @Test
    public void testIndexOf() {

        testList.addAll(Arrays.asList(testValues));
        final Integer testValue = testValues[5];
        final int referenceIndex = Arrays.asList(sortedTestValues).indexOf(
                testValue);
        assertEquals("The test value '" + testValue
                + "' was not found at the expected position.", referenceIndex,
                testList.indexOf(testValue));
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#isEmpty()}.
     */
    @Test
    public void testIsEmpty() {
        assertTrue(testList.isEmpty());
        testList.addAll(Arrays.asList(testValues));
        assertFalse(testList.isEmpty());
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#iterator()}
     * .
     */
    @Test
    public void testIterator() {
        testList.addAll(Arrays.asList(testValues));
        Iterator<Integer> intIterator = testList.iterator();
        assertTrue(intIterator.hasNext());

        // Iterate through the sorted values and compare them to the reference
        // list.
        Iterator<Integer> referenceIterator = Arrays.asList(sortedTestValues)
                .iterator();
        while (intIterator.hasNext()) {
            assertEquals(referenceIterator.next(), intIterator.next());
        }
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#lastIndexOf(java.lang.Object)}
     * .
     */
    @Test
    public void testLastIndexOf() {

        testList.addAll(Arrays.asList(testValues));
        final Integer testValue = testValues[5];
        final int referenceIndex = Arrays.asList(sortedTestValues).lastIndexOf(
                testValue);
        assertEquals("The test value '" + testValue
                + "' was not found at the expected position.", referenceIndex,
                testList.lastIndexOf(testValue));
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#remove(int)}
     * .
     */
    @Test
    public void testRemoveInt() {
        testList.addAll(Arrays.asList(testValues));

        final int referenceIndex = 6;
        final Integer testValue = sortedTestValues[referenceIndex];

        assertEquals(testValue, testList.get(referenceIndex));
        assertEquals("An un-expected element value was removed.", testValue,
                testList.remove(referenceIndex));
        assertFalse(testValue.equals(testList.get(referenceIndex)));
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#remove(java.lang.Object)}
     * .
     */
    @Test
    public void testRemoveT() {
        testList.addAll(Arrays.asList(testValues));

        final int referenceIndex = 2;
        final Integer testValue = sortedTestValues[referenceIndex];

        assertEquals("The test value '" + testValue
                + "' was not found at the expected position.", referenceIndex,
                testList.indexOf(testValue));
        assertTrue("The list was expected to change.", testList
                .remove(testValue));
        assertFalse("The test value '" + testValue
                + "' does not seem to be removed from the list.",
                referenceIndex == testList.indexOf(testValue));
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#removeAll(java.util.Collection)}
     * .
     */
    @Test
    public void testRemoveAll() {

        // Build a helper list of elements to remove.
        ArrayList<Integer> elementsToRemove = new ArrayList<Integer>();
        for (int index = 0; index < sortedTestValues.length; index += 2) {
            elementsToRemove.add(sortedTestValues[index]);
        }

        // Fill the test list and remove the elements.
        testList.addAll(Arrays.asList(testValues));
        testList.removeAll(elementsToRemove);

        // Verify that the removed elements do no longer exist in the list.
        for (Integer removedElement : elementsToRemove) {
            assertFalse("The removed element '" + removedElement
                    + "' still exists in the list.", testList
                    .contains(removedElement));
        }
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#retainAll(java.util.Collection)}
     * .
     */
    @Test
    public void testRetainAll() {
        // Build a helper list of elements to keep.
        ArrayList<Integer> elementsToKeep = new ArrayList<Integer>();
        for (int index = 0; index < sortedTestValues.length; index += 2) {
            elementsToKeep.add(sortedTestValues[index]);
        }

        // Fill the test list and remove the elements not represented in the
        // elementsToKeep list.
        testList.addAll(Arrays.asList(testValues));
        testList.retainAll(elementsToKeep);

        // Verify that the elements to retain still exist in the list.
        for (Integer retainedElement : elementsToKeep) {
            assertFalse("The element '" + retainedElement
                    + "' was not retained in the list.", -1 == testList
                    .indexOf(retainedElement));
        }
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#size()}.
     */
    @Test
    public void testSize() {
        assertTrue(testList.size() == 0);
        testList.addAll(Arrays.asList(testValues));
        assertTrue(testList.size() != 0);
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#subList(int, int)}
     * .
     */
    @Test
    public void testSubList() {
        testList.addAll(Arrays.asList(testValues));

        final int fromIndex = 3;
        final int toIndex = 7;
        final List<Integer> referenceList = Arrays.asList(sortedTestValues)
                .subList(fromIndex, toIndex);

        final List<Integer> subTestList = testList.subList(fromIndex, toIndex);

        assertTrue(referenceList.equals(subTestList));

    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#toArray()}.
     */
    @Test
    public void testToArray() {
        testList.addAll(Arrays.asList(testValues));
        assertTrue(Arrays.deepEquals(sortedTestValues, testList.toArray()));
    }

    /**
     * Test method for
     * {@link dk.statsbiblioteket.doms.iprolemapper.utils.SortedList#toArray(T[])}
     * .
     */
    @Test
    public void testToArrayTArray() {
        testList.addAll(Arrays.asList(testValues));
        assertTrue(Arrays.deepEquals(sortedTestValues, testList.toArray()));
    }
}
