/*
 * 03/26/2004
 *
 * DynamicLongArray.java - Similar to an ArrayList, but holds longs instead
 * of Objects.
 * Copyright (C) 2004 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * www.website.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.fife.mario.editor;

/**
 * Similar to a <code>java.util.ArrayList</code>, but specifically for
 * <code>long</code>s.  This is basically an array of longs that resizes
 * itself (if necessary) when adding new elements.
 *
 * @author Robert Futrell
 * @version 0.8
 */
public class DynamicLongArray {

	/**
	 * The actual data.
	 */
	private long[] data;

	/**
	 * The number of values in the array.  Note that this is NOT the
	 * capacity of the array; rather, <code>size &lt;= capacity</code>.
	 */
	private int size;

	/**
	 * Constructs a new array object with an initial capacity of 10.
	 */
	public DynamicLongArray() {
		this(10);
	}

	/**
	 * Constructs a new array object with a given initial capacity.
	 *
	 * @param initialCapacity The initial capacity.
	 * @throws IllegalArgumentException If <code>initialCapacity</code> is
	 *         negative.
	 */
	public DynamicLongArray(int initialCapacity) {
		if (initialCapacity<0) {
			throw new IllegalArgumentException("Illegal initialCapacity: " + initialCapacity);
		}
		data = new long[initialCapacity];
		size = 0;
	}

	/**
	 * Constructs a new array object from the given long array.  The resulting
	 * <code>DynamicLongArray</code> will have an initial capacity of 110%
	 * the size of the array.
	 *
	 * @param longArray Initial data for the array object.
	 * @throws NullPointerException If <code>longArray</code> is
	 *         <code>null</code>.
	 */
	public DynamicLongArray(long[] longArray) {
		size = longArray.length;
		int capacity = (int)Math.min(size*110L/100, Integer.MAX_VALUE);
		data = new long[capacity];
		System.arraycopy(longArray,0, data,0, size); // source, dest, length.
	}

	/**
	 * Appends the specified <code>long</code> to the end of this array.
	 *
	 * @param value The <code>long</code> to be appended to this array.
	 */
	public void add(long value) {
		ensureCapacity(size + 1);
		data[size++] = value;
	}

	/**
	 * Inserts all <code>long</code>s in the specified array into this array
	 * object at the specified location.  Shifts the <code>long</code>
	 * currently at that position (if any) and any subsequent
	 * <code>long</code>s to the right (adds one to their indices).
	 *
	 * @param index The index at which the specified long is to be
	 *        inserted.
	 * @param longArray The array of <code>long</code>s to insert.
	 * @throws IndexOutOfBoundsException If <code>index</code> is less than
	 *         zero or greater than <code>getSize()</code>.
	 * @throws NullPointerException If <code>longArray</code> is
	 *         <code>null</code>.
	 */
	public void add(int index, long[] longArray) {
		if (index>size) {
			throwException2(index);
		}
		int addCount = longArray.length;
		ensureCapacity(size+addCount);
		int moveCount = size - index;
		if (moveCount>0) {
            System.arraycopy(data, index, data, index + addCount, moveCount);
        }
		System.arraycopy(data,index, longArray,0, moveCount);
		size += addCount;
	}

	/**
	 * Inserts the specified <code>long</code> at the specified position in
	 * this array. Shifts the <code>long</code> currently at that position (if
	 * any) and any subsequent <code>long</code>s to the right (adds one to
	 * their indices).
	 *
	 * @param index The index at which the specified long is to be
	 *        inserted.
	 * @param value The <code>long</code> to be inserted.
	 * @throws IndexOutOfBoundsException If <code>long</code> is less than
	 *         zero or greater than <code>getSize()</code>.
	 */
	public void add(int index, long value) {
		if (index>size) {
			throwException2(index);
		}
		ensureCapacity(size+1);
		System.arraycopy(data,index, data,index+1, size-index);
		data[index] = value;
		size++;
	}

	/**
	 * Removes all values from this array object.  Capacity will remain the
	 * same.
	 */
	public void clear() {
		size = 0;
	}

	/**
	 * Returns whether this array contains a given long.  This method
	 * performs a linear search, so it is not optimized for performance.
	 *
	 * @param l The <code>long</code> for which to search.
	 * @return Whether the given long is contained in this array.
	 */
	public boolean contains(long l) {
		for (int i=0; i<size; i++) {
			if (data[i]==l) {
                return true;
            }
		}
		return false;
	}

	/**
	 * Makes sure that this <code>DynamicLongArray</code> instance can hold
	 * at least the number of elements specified.  If it can't, then the
	 * capacity is increased.
	 *
	 * @param minCapacity The desired minimum capacity.
	 */
	private void ensureCapacity(int minCapacity) {
		int oldCapacity = data.length;
		if (minCapacity > oldCapacity) {
			long[] oldData = data;
			// Ensures we don't just keep increasing capacity by some small
			// number like 1...
			int newCapacity = (oldCapacity * 3)/2 + 1;
			if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
			data = new long[newCapacity];
			System.arraycopy(oldData,0, data,0, size);
		}
	}

	/**
	 * Returns the <code>long</code> at the specified position in this array
	 * object.
	 *
	 * @param index The index of the <code>long</code> to return.
	 * @return The <code>long</code> at the specified position in this array.
	 * @throws IndexOutOfBoundsException If <code>index</code> is less than
	 *         zero or greater than or equal to <code>getSize()</code>.
	 */
	public long get(int index) {
		// Small enough to be inlined, and throwException() is rarely called.
		if (index>=size) {
			throwException(index);
		}
		return data[index];
	}

	/**
	 * Returns the <code>long</code> at the specified position in this array
	 * object, without doing any bounds checking.  You really should use
	 * {@link #get(int)} instead of this method.
	 *
	 * @param index The index of the <code>long</code> to return.
	 * @return The <code>long</code> at the specified position in this array.
	 */
	public long getUnsafe(int index) {
		// Small enough to be inlined.
		return data[index];
	}

	/**
	 * Returns the number of <code>long</code>s in this array object.
	 *
	 * @return The number of <code>long</code>s in this array object.
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Returns whether or not this array object is empty.
	 *
	 * @return Whether or not this array object contains no elements.
	 */
	public boolean isEmpty() {
		return size==0;
	}

	/**
	 * Removes the <code>long</code> at the specified location from this array
	 * object.
	 *
	 * @param index The index of the <code>long</code> to remove.
	 * @throws IndexOutOfBoundsException If <code>index</code> is less than
	 *         zero or greater than or equal to <code>getSize()</code>.
	 */
	public void remove(int index) {
		if (index>=size) {
			throwException(index);
		}
		int toMove = size - index - 1;
		if (toMove>0) {
			System.arraycopy(data,index+1, data,index, toMove);
		}
		--size;
	}

	/**
	 * Removes the last element in this array and returns it.
	 *
	 * @return The element removed from the end of this array.
	 * @throws IndexOutOfBoundsException If this array is empty.
	 */
	public long removeLast() {
		if (size==0) {
			throwException(size);
		}
		return data[--size];
	}

	/**
	 * Removes the last element in this array and returns it.
	 *
	 * @return The element removed from the end of this array.
	 * @throws IndexOutOfBoundsException If this array is empty.
	 */
	public long removeLastUnsafe() {
		return data[--size];
	}

	/**
	 * Removes the <code>long</code>s in the specified range from this array
	 * object.
	 *
	 * @param fromIndex The index of the first <code>long</code> to remove.
	 * @param toIndex The index AFTER the last <code>long</code> to remove.
	 * @throws IndexOutOfBoundsException If either of <code>fromIndex</code>
	 *         or <code>toIndex</code> is less than zero or greater than or
	 *         equal to <code>getSize()</code>.
	 */
	public void removeRange(int fromIndex, int toIndex) {
		if (fromIndex>=size || toIndex>size) {
			throwException3(fromIndex, toIndex);
		}
		int moveCount = size - toIndex;
		System.arraycopy(data,toIndex, data,fromIndex, moveCount);
		size -= (toIndex - fromIndex);
	}

	/**
	 * Sets the <code>long</code> value at the specified position in this
	 * array object.
	 *
	 * @param index The index of the <code>long</code> to set
	 * @param value The value to set it to.
	 * @throws IndexOutOfBoundsException If <code>index</code> is less than
	 *         zero or greater than or equal to <code>getSize()</code>.
	 */
	public void set(int index, long value) {
		// Small enough to be inlined, and throwException() is rarely called.
		if (index>=size) {
			throwException(index);
		}
		data[index] = value;
	}

	/**
	 * Sets the <code>long</code> value at the specified position in this
	 * array object, without doing any bounds checking.  You should use
	 * {@link #set(int, long)} instead of this method.
	 *
	 * @param index The index of the <code>long</code> to set
	 * @param value The value to set it to.
	 */
	public void setUnsafe(int index, long value) {
		// Small enough to be inlined.
		data[index] = value;
	}

	/**
	 * Throws an exception.  This method isolates error-handling code from
	 * the error-checking code, so that callers (e.g. {@link #get} and
	 * {@link #set}) can be both small enough to be inlined, as well as
	 * not usually make any expensive method calls (since their callers will
	 * usually not pass illegal arguments to them).
	 *
	 * See <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5103956">
	 * this Sun bug report</a> for more information.
	 *
	 * @param index The invalid index.
	 * @throws IndexOutOfBoundsException Always.
	 */
	private void throwException(int index)
								throws IndexOutOfBoundsException {
		throw new IndexOutOfBoundsException("Index " + index +
						" not in valid range [0-" + (size-1) + "]");
	}

	/**
	 * Throws an exception.  This method isolates error-handling code from
	 * the error-checking code, so that callers can be both small enough to be
	 * inlined, as well as not usually make any expensive method calls (since
	 * their callers will usually not pass illegal arguments to them).
	 *
	 * See <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5103956">
	 * this Sun bug report</a> for more information.
	 *
	 * @param index The invalid index.
	 * @throws IndexOutOfBoundsException Always.
	 */
	private void throwException2(int index) throws IndexOutOfBoundsException {
		throw new IndexOutOfBoundsException("Index " + index +
								", not in range [0-" + size + "]");
	}

	/**
	 * Throws an exception.  This method isolates error-handling code from
	 * the error-checking code, so that callers can be both small enough to be
	 * inlined, as well as not usually make any expensive method calls (since
	 * their callers will usually not pass illegal arguments to them).
	 *
	 * See <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5103956">
	 * this Sun bug report</a> for more information.
	 *
	 * @param fromIndex The starting index of the invalid range.
     * @param toIndex The end index of the invalid range
	 * @throws IndexOutOfBoundsException Always.
	 */
	private void throwException3(int fromIndex, int toIndex) throws IndexOutOfBoundsException {
		throw new IndexOutOfBoundsException("Index range [" +
						fromIndex + ", " + toIndex +
						"] not in valid range [0-" + (size-1) + "]");
	}

}
