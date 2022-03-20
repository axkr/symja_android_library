package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.function.Function;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.visit.VisitorReplaceAll;

/** Maintain <code>Dispatch()</code> rules. */
public class DispatchExpr extends DataExpr<VisitorReplaceAll>
    implements Externalizable, Function<IExpr, IExpr> {
  public static DispatchExpr newInstance(final IAST listOfRules) {
    return new DispatchExpr(listOfRules);
  }

  public static DispatchExpr newInstance(final IAssociation assoc) {
    return new DispatchExpr(assoc);
  }

  IAST listOfRules;

  /** Needed for serialization. */
  public DispatchExpr() {
    super(S.Dispatch, null);
    listOfRules = F.NIL;
  }

  protected DispatchExpr(final IAST listOfRules) {
    super(S.Dispatch, new VisitorReplaceAll(listOfRules));
    this.listOfRules = listOfRules;
  }

  protected DispatchExpr(final IAssociation assoc) {
    super(S.Dispatch, new VisitorReplaceAll(assoc));
    this.listOfRules = assoc.normal(false);
  }

  /**
   * Copy constructor
   *
   * @param visitor
   */
  protected DispatchExpr(final VisitorReplaceAll visitor, IAST listOfRules) {
    super(S.Dispatch, visitor);
    this.listOfRules = listOfRules;
  }

  /**
   * Apply the dispatcher functions on an expression and return the right-hand-side if found or
   * {@link F#NIL}.
   * 
   * @param expr
   * @return {@link F#NIL} if no matching left-hand-side was found in this dispatcher
   */
  @Override
  public IExpr apply(IExpr expr) {
    return fData.getFunction().apply(expr);
  }

  @Override
  public IExpr copy() {
    return new DispatchExpr(fData, listOfRules);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof DispatchExpr) {
      return fData.equals(((DispatchExpr) obj).fData);
    }
    return false;
  }

  public VisitorReplaceAll getVisitor() {
    return fData;
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 973 : 973 + fData.hashCode();
  }

  @Override
  public int hierarchy() {
    return DISPATCHID;
  }

  @Override
  public IAST normal(boolean nilIfUnevaluated) {
    return listOfRules;
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    final IAST listOfRules = (IAST) in.readObject();
    this.fData = new VisitorReplaceAll(listOfRules);
    this.listOfRules = listOfRules;
  }

  @Override
  public String toString() {
    return "Dispatch(" + listOfRules.toString() + ")";
  }

  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeObject(listOfRules);
  }
}
