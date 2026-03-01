package org.matheclipse.core.compile;

import org.matheclipse.core.interfaces.IExpr;

/**
 * A data holder for a compiled function argument, including its expression, type, and rank (scalar,
 */
public class CompiledFunctionArg {
  public enum Rank {
    SCALAR, VECTOR, MATRIX
  }

  IExpr argument;
  IExpr type;
  public Rank rank;

  public IExpr argument() {
    return argument;
  }

  public IExpr type() {
    return type;
  }

  public Rank rank() {
    return rank;
  }

  public CompiledFunctionArg(IExpr argument, IExpr type) {
    this.argument = argument;
    this.type = type;
    this.rank = Rank.SCALAR;
  }

  public CompiledFunctionArg(IExpr argument, IExpr type, Rank rank) {
    this.argument = argument;
    this.type = type;
    this.rank = rank;
  }

  public static Rank getRank(int rank) {
    if (rank == 0) {
      return Rank.SCALAR;
    } else if (rank == 1) {
      return Rank.VECTOR;
    } else if (rank == 2) {
      return Rank.MATRIX;
    }
    return null;
  }
}
