/*
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
package cc.redberry.core.math.frobenius;

/**
 *
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
abstract class SolutionProviderAbstract implements SolutionProvider {
    private final SolutionProvider provider;
    final int position;
    final int[] coefficients;
    int[] currentSolution;
    int currentCounter = 0;
    int[] currentRemainder;

    SolutionProviderAbstract(SolutionProvider provider, int position, int[] coefficients) {
        this.provider = provider;
        this.position = position;
        this.coefficients = coefficients;
    }

    @Override
    public boolean tick() {
        currentSolution = provider.take();
        currentRemainder = provider.currentRemainders();
        currentCounter = 0;
        return currentSolution != null;
    }

    @Override
    public int[] currentRemainders() {
        int[] remainders = new int[coefficients.length];
        for (int i = 0; i < coefficients.length; ++i)
            remainders[i] = currentRemainder[i] - coefficients[i] * (currentCounter - 1);
        return remainders;
    }
}
