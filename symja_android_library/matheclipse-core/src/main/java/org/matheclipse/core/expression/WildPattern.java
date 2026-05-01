package org.matheclipse.core.expression;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * A pattern object that represents a SymPy-like wildcard with optional exclusion constraints. The
 * exclusion constraints are used in pattern matching to specify that certain expressions should not
 * match.
 */
public class WildPattern extends Pattern {

  /**
   * 
   */
  private static final long serialVersionUID = 530596875314950910L;

  private final IExpr[] excludes;

  public WildPattern(ISymbol symbol, boolean optional, IExpr... excludes) {
    // Calls the native Pattern constructor (Symbol, Default Value, Optional flag)
    super(symbol, null, optional);
    this.excludes = excludes;
  }

  /**
   * Verifies if the matched expression satisfies the exclusion constraints.
   */
  public boolean isConditionMatched(IExpr expr, EvalEngine engine) {
    if (excludes == null || excludes.length == 0) {
      return true;
    }

    // First check: the expression itself cannot be an excluded value
    // for (IExpr exclude : excludes) {
    // if (exclude.equals(expr)) {
    // return false;
    // }
    // }

    final boolean free = expr.isFree(x -> isExcluded(x), true);
    // System.out.println("Checking if " + expr + " is free(" + free + ") of excluded symbols: "
    // + java.util.Arrays.toString(excludes));
    return free;
  }

  private boolean isExcluded(IExpr x) {
    for (IExpr exclude : excludes) {
      if (exclude.equals(x)) {
        return true;
      }
    }
    return false;
  }

  public IExpr[] getExcludes() {
    return excludes;
  }
}
