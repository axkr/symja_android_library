/*
 * 2016-09-04: Copied and modified under Lesser GPL license from <a
 * href="http://redberry.cc/">Redberry: symbolic tensor computations</a> with permission from the
 * original authors Stanislav Poslavsky and Dmitry Bolotin.
 *
 * Following is the original header:
 *
 * Redberry: symbolic tensor computations.
 *
 * Copyright (c) 2010-2012: Stanislav Poslavsky <stvlpos@mail.ru> Bolotin Dmitriy
 * <bolotin.dmitriy@gmail.com>
 *
 * This file is part of Redberry.
 *
 * Redberry is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Redberry is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Redberry. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package org.matheclipse.core.frobenius;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IInteger;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
final class TotalSolutionProvider implements OutputPortUnsafe<IInteger[]> {
  private final SolutionProvider[] providers;
  private boolean inited = false;

  public TotalSolutionProvider(SolutionProvider[] providers) {
    this.providers = providers;
  }

  @Override
  public IInteger[] take() {
    if (!inited) {
      for (SolutionProvider provider : providers) {
        provider.tick();
      }
      inited = true;
    }
    int iterationLimit = EvalEngine.get().getIterationLimit() * 100;
    int counter = 0;
    int i = providers.length - 1;
    IInteger[] solution = providers[i].take();
    if (solution != null) {
      return solution;
    }
    OUTER: while (true) {
      boolean r;
      if (counter++ > iterationLimit && iterationLimit > 0) {
        IterationLimitExceeded.throwIt(counter, S.FrobeniusSolve);
      }
      while ((r = !(providers[i--].tick())) && i >= 0) {
        //
      }
      if (i == -1 && r) {
        return null;
      }
      i += 2;
      for (; i < providers.length; ++i)
        if (!providers[i].tick()) {
          i--;
          continue OUTER;
        }
      assert i == providers.length;
      i--;
      solution = providers[i].take();
      if (solution != null) {
        return solution;
      }
    }
  }
}
