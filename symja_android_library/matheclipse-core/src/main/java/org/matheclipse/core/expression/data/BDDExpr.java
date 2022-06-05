package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.logicng.knowledgecompilation.bdds.BDD;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class BDDExpr extends DataExpr<BDD> implements Externalizable {
  boolean isPureFunction;

  public static BDDExpr newInstance(final BDD bdd, boolean isPureFunction) {
    return new BDDExpr(bdd, isPureFunction);
  }

  public BDDExpr() {
    super(S.BooleanFunction, null);
  }

  protected BDDExpr(final BDD bdd, boolean isPureFunction) {
    super(S.BooleanFunction, bdd);
    this.isPureFunction = isPureFunction;
  }

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

  @Override
  public int hierarchy() {
    return BDDEXPRID;
  }

  @Override
  public IExpr copy() {
    return new BDDExpr(fData, isPureFunction);
  }

  public boolean isPureBooleanFunction() {
    return isPureFunction;
  }

  @Override
  public String toString() {
    return fData.toString();
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    fData = (BDD) in.readObject();
  }

  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeObject(fData);
  }

  // public static IExpr evaluateBooleanFunction(BDDExpr booleanFunction, IAST ast,
  // EvalEngine engine) {
  // IASTAppendable variablesList = F.ListAlloc(ast.argSize());
  // for (int i = 1; i < ast.size(); i++) {
  // IExpr expr = engine.evaluate(ast.get(i));
  // if (expr.isTrue()) {
  // variablesList.append(S.True);
  // } else if (expr.isFalse()) {
  // variablesList.append(S.False);
  // } else {
  // return F.NIL;
  // }
  // }
  // return variablesList.apply(booleanFunction);
  // }
}
