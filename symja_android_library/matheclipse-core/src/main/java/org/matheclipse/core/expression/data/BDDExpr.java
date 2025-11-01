package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import org.logicng.formulas.Variable;
import org.logicng.knowledgecompilation.bdds.BDD;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A wrapper expression for a LogicNG {@link BDD} used in the function {@link S#BooleanFunction}.
 * <p>
 * This class extends {@link DataExpr} parameterized with {@link BDD} to store a compiled boolean
 * function (BDD). It implements {@link Externalizable} to allow custom serialization of the
 * underlying BDD object.
 * </p>
 * Instances may represent either a "pure" boolean function or a generic boolean function indicated
 * by {@link #isPureBooleanFunction()}.
 */
public class BDDExpr extends DataExpr<BDD> implements Externalizable {
  /**
   * True when this BDD expression represents a pure boolean function.
   */
  boolean isPureFunction;

  /**
   * Create a new {@code BDDExpr} instance wrapping the given {@link BDD}. This is used in the
   * function {@link S#BooleanFunction}.
   *
   * @param bdd the LogicNG BDD to wrap
   * @param isPureFunction if true, the wrapped boolean function is considered pure
   * @return a new {@code BDDExpr} wrapping {@link BDD}}
   */
  public static BDDExpr newInstance(final BDD bdd, boolean isPureFunction) {
    return new BDDExpr(bdd, isPureFunction);
  }

  /**
   * No-arg constructor required for {@link Externalizable} deserialization. Initializes the object
   * with the standard boolean function head and a null data reference.
   */
  public BDDExpr() {
    super(S.BooleanFunction, null);
  }

  /**
   * Protected constructor used by factory method to create a BDD expression with the specified data
   * and purity flag.
   *
   * @param bdd the BDD to store in this expression
   * @param isPureFunction whether the BDD represents a pure boolean function
   */
  protected BDDExpr(final BDD bdd, boolean isPureFunction) {
    super(S.BooleanFunction, bdd);
    this.isPureFunction = isPureFunction;
  }

  /**
   * Equality is based on the wrapped BDD data. Two {@link BDDExpr} instances are equal if their
   * underlying {@link BDD} objects are equal.
   *
   * @param obj the other object to compare
   * @return true if equal, false otherwise
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof BDDExpr) {
      return fData.equals(((BDDExpr) obj).fData);
    }
    return false;
  }

  /**
   * Evaluate this boolean function expression against an AST of boolean arguments.
   * <p>
   * The incoming {@link IAST} is expected to have boolean arguments
   * ({@link S#True}/{@link S#False}). Each argument is evaluated by the provided
   * {@link EvalEngine}. If any argument is not a boolean constant after evaluation, this method
   * returns {@link F#NIL} to indicate an invalid call. Otherwise the boolean argument list is
   * applied to this BDD expression and the result is returned.
   * </p>
   *
   * @param ast the argument AST (first element usually head)
   * @param engine the evaluation engine used to evaluate arguments
   * @return the result of applying the boolean arguments to this function, or {@link F#NIL} if
   *         argument evaluation fails
   */
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    IASTAppendable variablesList = F.ListAlloc(ast.argSize());
    for (int i = 1; i < ast.size(); i++) {
      IExpr expr = engine.evaluate(ast.get(i));
      if (expr.isTrue()) {
        variablesList.append(S.True);
      } else if (expr.isFalse()) {
        variablesList.append(S.False);
      } else {
        return F.NIL;
      }
    }
    return variablesList.apply(this);
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 353 : 353 + fData.hashCode();
  }

  /**
   * Return the internal hierarchy id for this expression type.
   *
   * @return the hierarchy id (BDDEXPRID)
   */
  @Override
  public int hierarchy() {
    return BDDEXPRID;
  }

  /**
   * Create a shallow copy of this {@link BDDExpr}. The underlying {@link BDD} reference is shared
   * (no deep clone).
   *
   * @return a new {@link BDDExpr} with the same BDD and purity flag
   */
  @Override
  public IExpr copy() {
    return new BDDExpr(fData, isPureFunction);
  }

  /**
   * Query whether this boolean function is marked as pure.
   *
   * @return true if pure, false otherwise
   */
  public boolean isPureBooleanFunction() {
    return isPureFunction;
  }

  /**
   * Provide a readable string representation for debugging. If the underlying data is a
   * {@link BDD}, the representation includes the BDD index and number of variables according to its
   * variable order.
   *
   * @return a string describing this boolean function expression
   */
  @Override
  public String toString() {
    if (fData instanceof BDD) {
      List<Variable> variableOrder = fData.getVariableOrder();
      return "BooleanFunction(Index: " + fData.index() + " Number of variables: "
          + variableOrder.size() + ")";
    }

    return fHead + "[" + fData.toString() + "]";
  }

  /**
   * Custom deserialization hook for {@link Externalizable}. Reads the wrapped {@link BDD} object
   * from the provided stream.
   *
   * @param in the input stream to read from
   * @throws IOException if an I/O error occurs
   * @throws ClassNotFoundException if the BDD class cannot be found during deserialization
   */
  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    fData = (BDD) in.readObject();
  }

  /**
   * Custom serialization hook for {@link Externalizable}. Writes the wrapped {@link BDD} object to
   * the provided stream.
   *
   * @param output the output stream to write to
   * @throws IOException if an I/O error occurs
   */
  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeObject(fData);
  }

}
