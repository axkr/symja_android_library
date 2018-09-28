/*
 * 2016-09-04: Copied and modified under Lesser GPL license from
 * <a href="http://redberry.cc/">Redberry: symbolic tensor computations</a> with
 * permission from the original authors Stanislav Poslavsky and Dmitry Bolotin.
 * 
 * Following is the original header:
 * 
 * Redberry: symbolic tensor computations.
 *
 * Copyright (c) 2010-2012:
 *   Stanislav Poslavsky   <stvlpos@mail.ru>
 *   Bolotin Dmitriy       <bolotin.dmitriy@gmail.com>
 *
 * This file is part of Redberry.
 *
 * Redberry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Redberry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Redberry. If not, see <http://www.gnu.org/licenses/>.
 */
package org.matheclipse.core.frobenius;

import org.matheclipse.core.interfaces.IInteger;

/**
 *
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
final class FinalSolutionProvider extends SolutionProviderAbstract {
	public FinalSolutionProvider(SolutionProvider provider, int position, IInteger coefficient[]) {
		super(provider, position, coefficient);
	}

	@Override
	public IInteger[] take() {
		if (currentSolution == null) {
			return null;
		}

		int i = 0;
		// non zero coefficient
		while (i < coefficients.length && coefficients[i++].isZero()) {
			//
		}
		--i;

		assert i == 0 || i != coefficients.length;

		if (!currentRemainder[i].mod(coefficients[i]).isZero()) {
			currentSolution = null;
			return null;
		}

		currentCounter = currentRemainder[i].div(coefficients[i]);
		for (i = 0; i < coefficients.length; ++i) {
			if (coefficients[i].isZero()) {
				if (!currentRemainder[i].isZero()) {
					currentSolution = null;
					return null;
				}
			} else if (!currentRemainder[i].mod(coefficients[i]).isZero()) {
				currentSolution = null;
				return null;
			} else if (!currentRemainder[i].div(coefficients[i]).equals(currentCounter)) {
				currentSolution = null;
				return null;
			}
		}
		IInteger[] solution = currentSolution.clone();
		solution[position] = solution[position].add(currentCounter);
		currentSolution = null;
		return solution;
	}
}
