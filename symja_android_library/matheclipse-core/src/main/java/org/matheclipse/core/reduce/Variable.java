package org.matheclipse.core.reduce;

import org.matheclipse.core.interfaces.ISymbol;

/**
 * A variable of the linear formula IR.
 *
 * <p>
 * A variable bound by a quantifier carries a binder id which is unique inside one lowering, so that
 * <code>Exists(x, ... Exists(x, ...))</code> cannot capture the outer <code>x</code>. Free variables
 * (targets and parameters) carry {@link #FREE} and are the only ones the emitter ever writes back
 * into an expression.
 */
public final class Variable implements Comparable<Variable> {

  /** Binder id of a variable which is not bound by a quantifier. */
  public static final int FREE = -1;

  private final ISymbol symbol;

  private final int binder;

  private Variable(ISymbol symbol, int binder) {
    this.symbol = symbol;
    this.binder = binder;
  }

  /** A free variable: a target of the reduction or a parameter of the problem. */
  public static Variable free(ISymbol symbol) {
    return new Variable(symbol, FREE);
  }

  /** A variable bound by the quantifier with the given binder id. */
  public static Variable bound(ISymbol symbol, int binder) {
    return new Variable(symbol, binder);
  }

  public ISymbol symbol() {
    return symbol;
  }

  public int binder() {
    return binder;
  }

  public boolean isFree() {
    return binder == FREE;
  }

  @Override
  public int compareTo(Variable that) {
    if (this.binder == FREE && that.binder != FREE) {
      return -1;
    }
    if (this.binder != FREE && that.binder == FREE) {
      return 1;
    }
    int byName = this.symbol.getSymbolName().compareTo(that.symbol.getSymbolName());
    if (byName != 0) {
      return byName;
    }
    return Integer.compare(this.binder, that.binder);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof Variable)) {
      return false;
    }
    Variable that = (Variable) object;
    return this.binder == that.binder && this.symbol.equals(that.symbol);
  }

  @Override
  public int hashCode() {
    return 31 * symbol.hashCode() + binder;
  }

  @Override
  public String toString() {
    return isFree() ? symbol.getSymbolName() : symbol.getSymbolName() + "$" + binder;
  }
}
