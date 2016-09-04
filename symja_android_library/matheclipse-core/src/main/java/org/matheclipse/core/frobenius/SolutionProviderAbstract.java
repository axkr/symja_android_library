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

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IInteger;

/**
 *
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
abstract class SolutionProviderAbstract implements SolutionProvider {
	private final SolutionProvider provider;
	final int position;
	final IInteger[] coefficients;
	IInteger[] currentSolution;
	IInteger currentCounter;
	IInteger[] currentRemainder;

	SolutionProviderAbstract(SolutionProvider provider, int position, IInteger[] coefficients) {
		this.provider = provider;
		this.position = position;
		this.coefficients = coefficients;
		this.currentCounter = F.C0;
	}

	@Override
	public boolean tick() {
		currentSolution = provider.take();
		currentRemainder = provider.currentRemainders();
		currentCounter = F.C0;
		return currentSolution != null;
	}

	@Override
	public IInteger[] currentRemainders() {
		IInteger[] remainders = new IInteger[coefficients.length];
		IInteger factor = currentCounter.subtract(F.C1);
		for (int i = 0; i < coefficients.length; ++i) {
			remainders[i] = currentRemainder[i].subtract(coefficients[i].multiply(factor));
		}
		return remainders;
	}
}
