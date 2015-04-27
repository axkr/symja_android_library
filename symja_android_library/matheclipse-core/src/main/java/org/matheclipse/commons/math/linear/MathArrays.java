/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.matheclipse.commons.math.linear;

import java.lang.reflect.Array;
import java.util.Arrays;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Arrays utilities.
 *
 * @since 3.0
 */
public class MathArrays {

	/**
	 * Private constructor.
	 */
	private MathArrays() {
	}

	/**
	 * Build an array of elements.
	 * <p>
	 * Arrays are filled with field.getZero()
	 *
	 * @param <T>
	 *            the type of the field elements
	 * @param field
	 *            field to which array elements belong
	 * @param length
	 *            of the array
	 * @return a new array
	 * @since 3.2
	 */
	public static IExpr[] buildArray(final int length) {
		// OK because field must be correct class
		IExpr[] array = new IExpr[length];
		Arrays.fill(array, F.C0);
		return array;
	}

	/**
	 * Build a double dimension array of elements.
	 * <p>
	 * Arrays are filled with field.getZero()
	 *
	 * @param <T>
	 *            the type of the field elements
	 * @param field
	 *            field to which array elements belong
	 * @param rows
	 *            number of rows in the array
	 * @param columns
	 *            number of columns (may be negative to build partial arrays in the same way <code>new Field[rows][]</code> works)
	 * @return a new array
	 * @since 3.2
	 */
	public static IExpr[][] buildArray(final int rows, final int columns) {
		final IExpr[][] array;
		if (columns < 0) {
			IExpr[] dummyRow = buildArray(0);
			array = (IExpr[][]) Array.newInstance(dummyRow.getClass(), rows);
		} else {
			array = new IExpr[rows][columns];// (IExpr[][]) Array.newInstance(field.getRuntimeClass(), new int[] { rows, columns });
			for (int i = 0; i < rows; ++i) {
				Arrays.fill(array[i], F.C0);
			}
		}
		return array;
	}

}
