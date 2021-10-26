package org.matheclipse.core.visit;

import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IDataExpr;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * A visitor which could be used in the <code>org.matheclipse.core.interfaces.IExpr#accept()</code>
 * method.
 */
public interface IVisitor {

  public abstract IExpr visit(IDataExpr data);

  public abstract IExpr visit(IInteger element);

  public abstract IExpr visit(IFraction element);

  public abstract IExpr visit(IComplex element);

  public abstract IExpr visit(INum element);

  public abstract IExpr visit(IComplexNum element);

  public abstract IExpr visit(ISymbol element);

  public abstract IExpr visit(IPattern element);

  public abstract IExpr visit(IPatternSequence element);

  public abstract IExpr visit(IStringX element);

  public abstract IExpr visit(IASTMutable ast);

  default IExpr visit(IAssociation assoc) {
    return visit((IASTMutable) assoc);
  }
}
