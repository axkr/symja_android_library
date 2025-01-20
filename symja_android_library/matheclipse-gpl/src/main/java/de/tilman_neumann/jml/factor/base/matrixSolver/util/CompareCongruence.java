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

import de.tilman_neumann.jml.factor.base.congruence.Smooth;

/**
 * @author David McGuigan
 */
public class CompareCongruence implements Comparator<Object> {
	@Override
	public int compare(Object o1, Object o2) {
		Smooth s1 = (Smooth)o1;
		Smooth s2 = (Smooth)o2;
		return s1.getMatrixElements().length-s2.getMatrixElements().length;
	}
}
