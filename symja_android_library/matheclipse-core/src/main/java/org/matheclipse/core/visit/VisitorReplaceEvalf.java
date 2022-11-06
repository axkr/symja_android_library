package org.matheclipse.core.visit;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Replace all non numeric expressions where the given <code>function.apply()</code> method returns
 * a non {@link F#NIL} value. The visitors <code>visit()</code> methods return {@link F#NIL} if no
 * substitution occurred.
 */
public class VisitorReplaceEvalf extends VisitorExpr {
  final Function<IExpr, IExpr> fFunction;

  public VisitorReplaceEvalf(Function<IExpr, IExpr> function) {
    super();
    this.fFunction = function;
    // this.fOffset = offset;
  }

  public VisitorReplaceEvalf(IAST ast) {
    this(ast, 0);
  }

  public VisitorReplaceEvalf(IAssociation assoc) {
    this(assoc.normal(false), 0);
  }

  public VisitorReplaceEvalf(IAST ast, int offset) {
    super();
    this.fFunction = Functors.rules(ast, EvalEngine.get());
    // this.fOffset = offset;
  }

  public VisitorReplaceEvalf(Map<? extends IExpr, ? extends IExpr> map) {
    this(map, 0);
  }

  public VisitorReplaceEvalf(Map<? extends IExpr, ? extends IExpr> map, int offset) {
    super();
    this.fFunction = x -> {
      IExpr subst = map.get(x);
      if (subst != null) {
        return subst;
      }
      return F.NIL;
    };
    // this.fOffset = offset;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (!(obj instanceof VisitorReplaceEvalf)) {
      return false;
    }
    VisitorReplaceEvalf other = (VisitorReplaceEvalf) obj;
    return Objects.equals(this.fFunction, other.fFunction);
  }

  /**
   * Get this visitors basic <code>replace</code> function. The basic <code>replace</code> function
   * returns {@link F#NIL} if no element should be replaced.
   * 
   * @return
   */
  public Function<IExpr, IExpr> getFunction() {
    return fFunction;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fFunction == null) ? 0 : fFunction.hashCode());
    // result = prime * result + fOffset;
    return result;
  }

  @Override
  public IExpr visit(IASTMutable ast) {
    return fFunction.apply(ast).orElseGet(() -> visitAST(ast));
  }

  /** @return <code>F.NIL</code>, if no evaluation is possible */
  @Override
  public IExpr visit(ISymbol element) {
    return fFunction.apply(element);
  }

  /** @return <code>F.NIL</code>, if no evaluation is possible */
  @Override
  public IExpr visit(IStringX element) {
    return fFunction.apply(element);
  }

  @Override
  public IExpr visit(IPattern element) {
    IExpr temp = fFunction.apply(element);
    if (temp.isPresent()) {
      return temp;
    }
    ISymbol symbol = element.getSymbol();
    if (symbol != null) {
      IExpr expr = fFunction.apply(symbol);
      if (expr.isPresent() && expr.isSymbol()) {
        if (element.isPatternDefault()) {
          return F.$p((ISymbol) expr, element.getHeadTest(), true);
        }
        return F.$p((ISymbol) expr, element.getHeadTest());
      }
    }
    return F.NIL;
  }

  @Override
  public IExpr visit(IPatternSequence element) {
    IExpr temp = fFunction.apply(element);
    if (temp.isPresent()) {
      return temp;
    }
    ISymbol symbol = element.getSymbol();
    if (symbol != null) {
      IExpr expr = fFunction.apply(symbol);
      if (expr.isPresent() && expr.isSymbol()) {
        return F.$ps((ISymbol) expr, element.getHeadTest(), element.isDefault(),
            element.isNullSequence());
      }
    }
    return F.NIL;
  }

  /** @return <code>F.NIL</code>, if no evaluation is possible */
  @Override
  public IExpr visit(IAssociation assoc) {
    IExpr replacement = fFunction.apply(assoc);
    if (replacement.isPresent()) {
      return replacement;
    }
    int size = assoc.size();
    for (int i = 1; i < size; i++) {
      IExpr temp = assoc.getValue(i).accept(this);
      if (temp.isPresent()) {
        // something was evaluated - return a new IAST:
        IAST iRuleOrNIL = assoc.getRule(i);
        IASTMutable result;
        if (iRuleOrNIL.isPresent()) {
          result = assoc.setAtCopy(i, iRuleOrNIL.setAtCopy(2, temp));
        } else {
          result = assoc.copy();
        }
        assoc.forEach(i + 1, size, (x, j) -> {
          IExpr t = x.accept(this);
          if (t.isPresent()) {
            result.set(j, assoc.getRule(j).setAtCopy(2, t));
          }
        });
        return result;
      }
    }
    return F.NIL;
  }

  @Override
  protected IExpr visitAST(IAST ast) {
    int size = ast.size();
    for (int i = 1; i < size; i++) {
      IExpr temp = ast.get(i).accept(this);
      if (temp.isPresent()) {
        // something was evaluated - return a new IAST:
        IASTMutable result = ast.setAtCopy(i, temp);
        ast.forEach(i + 1, size, (x, j) -> {
          IExpr t = x.accept(this);
          if (t.isPresent()) {
            result.set(j, t);
          }
        });
        return result;
      }
    }
    return F.NIL;
  }

}
