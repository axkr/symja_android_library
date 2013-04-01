package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.AbstractVisitorBoolean;
import org.matheclipse.parser.client.SyntaxError;

import com.google.common.base.Predicate;

/**
 * Returns <code>True</code>, if the given expression is a numeric function or
 * value.
 * 
 */
public class NumericQ extends AbstractFunctionEvaluator implements
    Predicate<IExpr> {
  
  public static class NumericQVisitor extends AbstractVisitorBoolean {
    public NumericQVisitor() {
      super();
    }

    @Override
    public boolean visit(IAST list) {
      ISymbol symbol = list.topHead();
      if ((symbol.getAttributes() & ISymbol.NUMERICFUNCTION) == ISymbol.NUMERICFUNCTION) {
        // check if all arguments are &quot;numeric&quot;
        for (int i = 1; i < list.size(); i++) {
          if (!list.get(i).accept(this)) {
            return false;
          }
        }
        return true;
      }
      return false;
    }

    public boolean visit(IComplex element) {
      return true;
    }

    public boolean visit(IComplexNum element) {
      return true;
    }

    public boolean visit(IFraction element) {
      return true;
    }

    public boolean visit(IInteger element) {
      return true;
    }

    public boolean visit(INum element) {
      return true;
    }

    public boolean visit(ISymbol symbol) {
      if ((symbol.getAttributes() & ISymbol.CONSTANT) == ISymbol.CONSTANT) {
        return true;
      }
      return false;
    }
  }

  /**
   * Constructor for the unary predicate
   */
  public final static NumericQ CONST = new NumericQ();

  public NumericQ() {
    // System.out.println(getClass().getCanonicalName());
  }

  @Override
  public boolean apply(IExpr arg0) {
    return arg0.accept(new NumericQVisitor());
  }

  /**
   * Returns <code>True</code> if the first argument is a numeric object;
   * <code>False</code> otherwise
   */
  @Override
  public IExpr evaluate(final IAST ast) {
  	Validate.checkSize(ast, 2);
    return F.bool(apply(ast.get(1)));
  }

  @Override
  public void setUp(ISymbol symbol) throws SyntaxError {
    symbol.setAttributes(ISymbol.HOLDALL);
  }
}
