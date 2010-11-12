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

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This is an implementation of a sorted list which maintains a non-descending
 * order of the elements by applying insertion sorting, when adding elements to
 * a <code>LinkedList</code>. The class implements most of the methods of the
 * <code>List</code> interface, however, only the methods which are meaningful
 * to a sorted list are implemented.
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class SortedList<T> {

    private final LinkedList<T> elementList;
    private final Comparator<T> elementComparator;

    /**
     * Create a <code>SortedList</code> instance which maintains a
     * non-descending order between the inserted elements, using the
     * <code>Comparator</code> specified by <code>elementComparator</code>.
     */
    public SortedList(Comparator<T> elementComparator) {
        elementList = new LinkedList<T>();
        this.elementComparator = elementComparator;
    }

    /**
     * FIXME! UPDATE DOC! Appends the specified element to the end of this list
     * (optional operation).
     * 
     * <p>
     * Lists that support this operation may place limitations on what elements
     * may be added to this list. In particular, some lists will refuse to add
     * null elements, and others will impose restrictions on the type of
     * elements that may be added. List classes should clearly specify in their
     * documentation any restrictions on what elements may be added.
     * 
     * @param element
     *            element to be appended to this list
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     * @throws UnsupportedOperationException
     *             if the <tt>add</tt> operation is not supported by this list
     * @throws ClassCastException
     *             if the class of the specified element prevents it from being
     *             added to this list
     * @throws NullPointerException
     *             if the specified element is null and this list does not
     *             permit null elements
     * @throws IllegalArgumentException
     *             if some property of this element prevents it from being added
     *             to this list
     */
    public void add(T element) {
        final int insertPos = findInsertionPosition(elementList,
                elementComparator, element);

        if (insertPos < elementList.size()) {
            elementList.add(insertPos, element);
        } else {
            // Handle insertion into an empty list or at the end of the list.
            elementList.add(element);
        }
    }

    /**
     * FIXME! UPDATE DOC! Appends all of the elements in the specified
     * collection to the end of this list, in the order that they are returned
     * by the specified collection's iterator (optional operation). The behavior
     * of this operation is undefined if the specified collection is modified
     * while the operation is in progress. (Note that this will occur if the
     * specified collection is this list, and it's nonempty.)
     * 
     * @param elements
     *            collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws UnsupportedOperationException
     *             if the <tt>addAll</tt> operation is not supported by this
     *             list
     * @throws ClassCastException
     *             if the class of an element of the specified collection
     *             prevents it from being added to this list
     * @throws NullPointerException
     *             if the specified collection contains one or more null
     *             elements and this list does not permit null elements, or if
     *             the specified collection is null
     * @throws IllegalArgumentException
     *             if some property of an element of the specified collection
     *             prevents it from being added to this list
     * @see #add(Object)
     */
    public void addAll(Collection<? extends T> elements) {
        for (T element : elements) {
            add(element);
        }
    }

    /**
     * Removes all of the elements from this list (optional operation). The list
     * will be empty after this call returns.
     * 
     * @throws UnsupportedOperationException
     *             if the <tt>clear</tt> operation is not supported by this list
     */
    public void clear() {
        elementList.clear();
    }

    /**
     * Returns <tt>true</tt> if this list contains the specified element. More
     * formally, returns <tt>true</tt> if and only if this list contains at
     * least one element <tt>e</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
     * 
     * @param element
     *            element whose presence in this list is to be tested
     * @return <tt>true</tt> if this list contains the specified element
     * @throws ClassCastException
     *             if the type of the specified element is incompatible with
     *             this list (optional)
     * @throws NullPointerException
     *             if the specified element is null and this list does not
     *             permit null elements (optional)
     */
    public boolean contains(T element) {
        return elementList.contains(element);
    }

    /**
     * FIXME! UPDATE DOC! Returns <tt>true</tt> if this list contains all of the
     * elements of the specified collection.
     * 
     * @param elements
     *            collection to be checked for containment in this list
     * @return <tt>true</tt> if this list contains all of the elements of the
     *         specified collection
     * @throws ClassCastException
     *             if the types of one or more elements in the specified
     *             collection are incompatible with this list (optional)
     * @throws NullPointerException
     *             if the specified collection contains one or more null
     *             elements and this list does not permit null elements
     *             (optional), or if the specified collection is null
     * @see #contains(Object)
     */
    public boolean containsAll(Collection<T> elements) {
        boolean allContained = true;
        for (T element : elements) {
            allContained &= elementList.contains(element);
            if (allContained == false) {
                break;
            }
        }

        return allContained;
    }

    /**
     * Returns the element at the specified position in this list.
     * 
     * @param index
     *            index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException
     *             if the index is out of range (
     *             <tt>index &lt; 0 || index &gt;= size()</tt>)
     */
    public T get(int index) {
        return elementList.get(index);
    }

    /**
     * Returns the index of the first occurrence of the specified element in
     * this list, or -1 if this list does not contain the element. More
     * formally, returns the lowest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     * 
     * @param element
     *            element to search for
     * @return the index of the first occurrence of the specified element in
     *         this list, or -1 if this list does not contain the element
     * @throws ClassCastException
     *             if the type of the specified element is incompatible with
     *             this list (optional)
     * @throws NullPointerException
     *             if the specified element is null and this list does not
     *             permit null elements (optional)
     */
    public int indexOf(T element) {
        return elementList.indexOf(element);
    }

    /**
     * Returns <tt>true</tt> if this list contains no elements.
     * 
     * @return <tt>true</tt> if this list contains no elements
     */
    public boolean isEmpty() {
        return elementList.isEmpty();
    }

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     * 
     * @return an iterator over the elements in this list in proper sequence
     */
    public Iterator<T> iterator() {
        return elementList.iterator();
    }

    /**
     * Returns the index of the last occurrence of the specified element in this
     * list, or -1 if this list does not contain the element. More formally,
     * returns the highest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     * 
     * @param element
     *            element to search for
     * @return the index of the last occurrence of the specified element in this
     *         list, or -1 if this list does not contain the element
     * @throws ClassCastException
     *             if the type of the specified element is incompatible with
     *             this list (optional)
     * @throws NullPointerException
     *             if the specified element is null and this list does not
     *             permit null elements (optional)
     */
    public int lastIndexOf(T element) {
        return elementList.lastIndexOf(element);
    }

    /**
     * Removes the element at the specified position in this list (optional
     * operation). Shifts any subsequent elements to the left (subtracts one
     * from their indices). Returns the element that was removed from the list.
     * 
     * @param index
     *            the index of the element to be removed
     * @return the element previously at the specified position
     * @throws UnsupportedOperationException
     *             if the <tt>remove</tt> operation is not supported by this
     *             list
     * @throws IndexOutOfBoundsException
     *             if the index is out of range (
     *             <tt>index &lt; 0 || index &gt;= size()</tt>)
     */
    public T remove(int index) {
        return elementList.remove(index);
    }

    /**
     * Removes the first occurrence of the specified element from this list, if
     * it is present (optional operation). If this list does not contain the
     * element, it is unchanged. More formally, removes the element with the
     * lowest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>
     * (if such an element exists). Returns <tt>true</tt> if this list contained
     * the specified element (or equivalently, if this list changed as a result
     * of the call).
     * 
     * @param element
     *            element to be removed from this list, if present
     * @return <tt>true</tt> if this list contained the specified element
     * @throws ClassCastException
     *             if the type of the specified element is incompatible with
     *             this list (optional)
     * @throws NullPointerException
     *             if the specified element is null and this list does not
     *             permit null elements (optional)
     * @throws UnsupportedOperationException
     *             if the <tt>remove</tt> operation is not supported by this
     *             list
     */
    public boolean remove(T element) {
        return elementList.remove(element);
    }

    /**
     * Removes from this list all of its elements that are contained in the
     * specified collection (optional operation).
     * 
     * @param elements
     *            collection containing elements to be removed from this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws UnsupportedOperationException
     *             if the <tt>removeAll</tt> operation is not supported by this
     *             list
     * @throws ClassCastException
     *             if the class of an element of this list is incompatible with
     *             the specified collection (optional)
     * @throws NullPointerException
     *             if this list contains a null element and the specified
     *             collection does not permit null elements (optional), or if
     *             the specified collection is null
     * @see #remove(Object)
     * @see #contains(Object)
     */
    public boolean removeAll(Collection<T> elements) {
        return elementList.removeAll(elements);
    }

    /**
     * Retains only the elements in this list that are contained in the
     * specified collection (optional operation). In other words, removes from
     * this list all the elements that are not contained in the specified
     * collection.
     * 
     * @param elements
     *            collection containing elements to be retained in this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws UnsupportedOperationException
     *             if the <tt>retainAll</tt> operation is not supported by this
     *             list
     * @throws ClassCastException
     *             if the class of an element of this list is incompatible with
     *             the specified collection (optional)
     * @throws NullPointerException
     *             if this list contains a null element and the specified
     *             collection does not permit null elements (optional), or if
     *             the specified collection is null
     * @see #remove(Object)
     * @see #contains(Object)
     */
    public boolean retainAll(Collection<T> elements) {
        return elementList.retainAll(elements);
    }

    /**
     * Returns the number of elements in this list. If this list contains more
     * than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     * 
     * @return the number of elements in this list
     */
    public int size() {
        return elementList.size();
    }

    /**
     * Returns a view of the portion of this list between the specified
     * <tt>fromIndex</tt>, inclusive, and <tt>toIndex</tt>, exclusive. (If
     * <tt>fromIndex</tt> and <tt>toIndex</tt> are equal, the returned list is
     * empty.) The returned list is backed by this list, so non-structural
     * changes in the returned list are reflected in this list, and vice-versa.
     * The returned list supports all of the optional list operations supported
     * by this list.
     * <p>
     * 
     * This method eliminates the need for explicit range operations (of the
     * sort that commonly exist for arrays). Any operation that expects a list
     * can be used as a range operation by passing a subList view instead of a
     * whole list. For example, the following idiom removes a range of elements
     * from a list:
     * 
     * <pre>
     * list.subList(from, to).clear();
     * </pre>
     * 
     * Similar idioms may be constructed for <tt>indexOf</tt> and
     * <tt>lastIndexOf</tt>, and all of the algorithms in the
     * <tt>Collections</tt> class can be applied to a subList.
     * <p>
     * 
     * The semantics of the list returned by this method become undefined if the
     * backing list (i.e., this list) is <i>structurally modified</i> in any way
     * other than via the returned list. (Structural modifications are those
     * that change the size of this list, or otherwise perturb it in such a
     * fashion that iterations in progress may yield incorrect results.)
     * 
     * @param fromIndex
     *            low endpoint (inclusive) of the subList
     * @param toIndex
     *            high endpoint (exclusive) of the subList
     * @return a view of the specified range within this list
     * @throws IndexOutOfBoundsException
     *             for an illegal endpoint index value (
     *             <tt>fromIndex &lt; 0 || toIndex &gt; size ||
     *         fromIndex &gt; toIndex</tt>)
     */
    public List<T> subList(int fromIndex, int toIndex) {
        return elementList.subList(fromIndex, toIndex);
    }

    /**
     * Returns an array containing all of the elements in this list in proper
     * sequence (from first to last element).
     * 
     * <p>
     * The returned array will be "safe" in that no references to it are
     * maintained by this list. (In other words, this method must allocate a new
     * array even if this list is backed by an array). The caller is thus free
     * to modify the returned array.
     * 
     * <p>
     * This method acts as bridge between array-based and collection-based APIs.
     * 
     * @return an array containing all of the elements in this list in proper
     *         sequence
     * @see Arrays#asList(Object[])
     */
    public Object[] toArray() {
        return elementList.toArray();
    }

    /**
     * Returns an array containing all of the elements in this list in proper
     * sequence (from first to last element); the runtime type of the returned
     * array is that of the specified array. If the list fits in the specified
     * array, it is returned therein. Otherwise, a new array is allocated with
     * the runtime type of the specified array and the size of this list.
     * 
     * <p>
     * If the list fits in the specified array with room to spare (i.e., the
     * array has more elements than the list), the element in the array
     * immediately following the end of the list is set to <tt>null</tt>. (This
     * is useful in determining the length of the list <i>only</i> if the caller
     * knows that the list does not contain any null elements.)
     * 
     * <p>
     * Like the {@link #toArray()} method, this method acts as bridge between
     * array-based and collection-based APIs. Further, this method allows
     * precise control over the runtime type of the output array, and may, under
     * certain circumstances, be used to save allocation costs.
     * 
     * <p>
     * Suppose <tt>x</tt> is a list known to contain only strings. The following
     * code can be used to dump the list into a newly allocated array of
     * <tt>String</tt>:
     * 
     * <pre>
     * String[] y = x.toArray(new String[0]);
     * </pre>
     * 
     * Note that <tt>toArray(new Object[0])</tt> is identical in function to
     * <tt>toArray()</tt>.
     * 
     * @param resultArray
     *            the array into which the elements of this list are to be
     *            stored, if it is big enough; otherwise, a new array of the
     *            same runtime type is allocated for this purpose.
     * @return an array containing the elements of this list
     * @throws ArrayStoreException
     *             if the runtime type of the specified array is not a supertype
     *             of the runtime type of every element in this list
     * @throws NullPointerException
     *             if the specified array is null
     */
    public T[] toArray(T[] resultArray) {
        return elementList.toArray(resultArray);
    }

    /**
     * 
     * @param targetList
     * @param comparator
     * @param element
     * @return
     */
    private int findInsertionPosition(LinkedList<T> targetList,
            Comparator<T> comparator, T element) {

        return findPosition(targetList, 0, targetList.size(), comparator,
                element);
    }

    /**
     * @param targetList
     * @param startIndex
     * @param endIndex
     * @param comparator
     * @param element
     * @return
     */
    private int findPosition(LinkedList<T> targetList, int startIndex,
            int endIndex, Comparator<T> comparator, T element) {

        if (startIndex == endIndex) {
            return startIndex;
        }

        final int middleIndex = startIndex + (endIndex - startIndex) / 2;
        final T middleElement = targetList.get(middleIndex);
        final int compareResult = comparator.compare(middleElement, element);

        if (compareResult == 0) {
            return middleIndex;
        } else if (compareResult < 0) {
            return findPosition(targetList, middleIndex + 1, endIndex,
                    comparator, element);
        }
        return findPosition(targetList, startIndex, middleIndex, comparator,
                element);
    }
}
