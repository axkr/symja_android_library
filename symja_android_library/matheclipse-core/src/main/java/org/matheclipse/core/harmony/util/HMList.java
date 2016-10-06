package org.matheclipse.core.harmony.util;

import java.util.Collection;
import java.util.Iterator;

/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/**
 * A {@code List} is a collection which maintains an ordering for its elements.
 * Every element in the {@code List} has an index. Each element can thus be
 * accessed by its index, with the first index being zero. Normally,
 * {@code List}s allow duplicate elements, as compared to Sets, where elements
 * have to be unique.
 */
public interface HMList<E> extends HMCollection<E> {

	/**
	 * Compares the given object with the {@code List}, and returns true if they
	 * represent the <em>same</em> object using a class specific comparison. For
	 * {@code List}s, this means that they contain the same elements in exactly
	 * the same order.
	 * 
	 * @param object
	 *            the object to compare with this object.
	 * @return boolean {@code true} if the object is the same as this object,
	 *         and {@code false} if it is different from this object.
	 * @see #hashCode
	 */
	public boolean equals(Object object);

	/**
	 * Returns the hash code for this {@code List}. It is calculated by taking
	 * each element' hashcode and its position in the {@code List} into account.
	 * 
	 * @return the hash code of the {@code List}.
	 */
	public int hashCode();

}
