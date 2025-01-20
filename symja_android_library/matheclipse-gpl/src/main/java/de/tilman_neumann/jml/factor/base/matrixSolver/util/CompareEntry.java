/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018 Tilman Neumann - tilman.neumann@web.de
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, see <http://www.gnu.org/licenses/>.
 */
package de.tilman_neumann.jml.factor.base.matrixSolver.util;

import java.util.Comparator;
import java.util.Map.Entry;

import de.tilman_neumann.jml.factor.base.IntHolder;

/**
 * @author David McGuigan
 */
public class CompareEntry implements Comparator<Object> {
	@Override
	public int compare(Object o1, Object o2) {
		@SuppressWarnings("unchecked")
		Entry<Integer,IntHolder> e1 = (Entry<Integer,IntHolder>)o1;
		@SuppressWarnings("unchecked")
		Entry<Integer,IntHolder> e2 = (Entry<Integer,IntHolder>)o2;
		return e2.getValue().value-e1.getValue().value;
	}
}
