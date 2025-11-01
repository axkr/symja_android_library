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


/**
 * Data wrapper that stores a {@link VisitorReplaceAll} used as a fast dispatch table.
 * <p>
 * Instances of this class wrap a {@link VisitorReplaceAll} visitor and the original list of rules
 * (as an {@link IAST}) or the normalized list from an {@link IAssociation}. The primary use is to
 * provide a data-backed representation for {@link S#Dispatch} so that lookups (pattern-based or
 * key-based) can be applied quickly through the visitor.
 * </p>
 *
 * <p>
 * Implements {@link Externalizable} for custom serialization of the underlying rule list and
 * {@link Function}{@code <IExpr,IExpr>} so the dispatch can be applied to an expression returning
 * the matched right-hand-side or {@link F#NIL}.
 * </p>
 */
public class DispatchExpr extends DataExpr<VisitorReplaceAll>
    implements Externalizable, Function<IExpr, IExpr> {

  /**
   * Factory method to create a new {@link DispatchExpr} containing the supplied list of rules.
   *
   * @param listOfRules the AST containing rules used to build the internal
   *        {@link VisitorReplaceAll}
   * @return new {@link DispatchExpr} instance containing the list of rules
   */
  public static DispatchExpr newInstance(final IAST listOfRules) {
    return new DispatchExpr(listOfRules);
  }

  /**
   * Factory method to create a new {@link DispatchExpr} containing the supplied association.
   *
   * @param assoc the {@link IAssociation} value to wrap the list of rules
   * @return a new {@link DispatchExpr} instance containing the list of rules derived from
   *         {@code assoc}
   */
  public static DispatchExpr newInstance(final IAssociation assoc) {
    return new DispatchExpr(assoc);
  }

  /**
   * The original list of rules (or the normalized rules derived from an association).
   * <p>
   * This field is serialized directly and is used to recreate the {@link VisitorReplaceAll} when
   * deserializing.
   * </p>
   */
  IAST listOfRules;

  /** Needed for serialization. */
  public DispatchExpr() {
    super(S.Dispatch, null);
    listOfRules = F.NIL;
  }


  /**
   * Construct a dispatch from an AST containing rules.
   *
   * @param listOfRules the AST of rules to build the visitor from
   */
  protected DispatchExpr(final IAST listOfRules) {
    super(S.Dispatch, new VisitorReplaceAll(listOfRules));
    this.listOfRules = listOfRules;
  }

  /**
   * Construct a dispatch from an association.
   *
   * @param assoc the association whose normalized rules will be used to build the visitor
   */
  protected DispatchExpr(final IAssociation assoc) {
    super(S.Dispatch, new VisitorReplaceAll(assoc));
    this.listOfRules = assoc.normal(false);
  }

  /**
   * Copy constructor used to duplicate the dispatch while preserving the encapsulated visitor and
   * the original list of rules.
   *
   * @param visitor the {@link VisitorReplaceAll} instance to reuse
   * @param listOfRules the corresponding list of rules
   */
  protected DispatchExpr(final VisitorReplaceAll visitor, IAST listOfRules) {
    super(S.Dispatch, visitor);
    this.listOfRules = listOfRules;
  }

  /**
   * Apply the dispatcher to an expression.
   *
   * <p>
   * The wrapped {@link VisitorReplaceAll} is invoked to find a matching left-hand-side. If a match
   * is found the corresponding right-hand-side is returned; otherwise {@link F#NIL} is returned.
   * </p>
   *
   * @param expr the expression to dispatch on
   * @return the matched right-hand-side or {@link F#NIL} if no match is found
   */
  @Override
  public IExpr apply(IExpr expr) {
    return fData.getFunction().apply(expr);
  }

  /**
   * Create a shallow copy of this dispatch expression.
   *
   * @return a new {@link DispatchExpr} with the same visitor and rule list reference
   */
  @Override
  public IExpr copy() {
    return new DispatchExpr(fData, listOfRules);
  }

  /**
   * Equality only depends on the encapsulated visitor (i.e. the dispatch table).
   *
   * @param obj other object to compare
   * @return true if the other object is a {@link DispatchExpr} with an equal visitor
   */
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

  /**
   * Get the underlying {@link VisitorReplaceAll} used by this dispatch.
   *
   * @return the visitor performing the dispatch lookups
   */
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

  /**
   * Return the original list of rules as the normal form of this expression.
   *
   * @param nilIfUnevaluated ignored for this data wrapper; always returns the stored rule list
   * @return the stored {@link IAST} list of rules
   */
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

  /**
   * Return a debug-friendly string representation.
   *
   * @return a string like "Dispatch(<rules>)"
   */
  @Override
  public String toString() {
    return "Dispatch(" + listOfRules.toString() + ")";
  }

  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeObject(listOfRules);
  }
}
