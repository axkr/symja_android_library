package org.matheclipse.core.visit;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.data.DispatchExpr;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
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
 * Replace all occurrences of expressions where the given <code>function.apply()</code> method
 * returns a non <code>F.NIL</code> value. The visitors <code>visit()</code> methods return <code>
 * F.NIL</code> if no substitution occurred.
 */
public class VisitorReplaceAll extends VisitorExpr {
  final Function<IExpr, IExpr> fFunction;

  private Function<IASTMutable, IExpr> fPostProcessing;

  final int fOffset;

  public VisitorReplaceAll(Function<IExpr, IExpr> function) {
    this(function, 0);
  }

  public VisitorReplaceAll(Function<IExpr, IExpr> function, int offset) {
    super();
    this.fFunction = function;
    this.fOffset = offset;
  }

  public VisitorReplaceAll(IAST ast) {
    this(ast, 0);
  }

  public VisitorReplaceAll(IAssociation assoc) {
    this(assoc.normal(false), 0);
  }

  public VisitorReplaceAll(IAST ast, int offset) {
    super();
    this.fFunction = Functors.rules(ast, EvalEngine.get());
    this.fOffset = offset;
  }

  public VisitorReplaceAll(Map<? extends IExpr, ? extends IExpr> map) {
    this(map, 0);
  }

  public VisitorReplaceAll(Map<? extends IExpr, ? extends IExpr> map, int offset) {
    super();
    this.fFunction = x -> {
      IExpr subst = map.get(x);
      if (subst != null) {
        return subst;
      }
      return F.NIL;
    };
    this.fOffset = offset;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (!(obj instanceof VisitorReplaceAll)) {
      return false;
    }
    VisitorReplaceAll other = (VisitorReplaceAll) obj;
    return Objects.equals(this.fFunction, other.fFunction) //
        && fOffset == other.fOffset //
        && Objects.equals(this.fPostProcessing, other.fPostProcessing);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fFunction == null) ? 0 : fFunction.hashCode());
    result = prime * result + fOffset;
    result = prime * result + ((fPostProcessing == null) ? 0 : fPostProcessing.hashCode());
    return result;
  }

  private IExpr postProcessing(IASTMutable result) {
    if (fPostProcessing != null) {
      return fPostProcessing.apply(result);
    }
    return result;
  }

  /**
   * Define an optional post-processing function for the result of a substitution.
   *
   * @param postProcessing
   */
  public void setPostProcessing(Function<IASTMutable, IExpr> postProcessing) {
    this.fPostProcessing = postProcessing;
  }

  @Override
  public IExpr visit(IASTMutable ast) {
    return fFunction.apply(ast).orElseGet(() -> visitAST(ast));
  }

  /** @return <code>F.NIL</code>, if no evaluation is possible */
  @Override
  public IExpr visit(IComplex element) {
    return fFunction.apply(element);
  }

  /** @return <code>F.NIL</code>, if no evaluation is possible */
  @Override
  public IExpr visit(IComplexNum element) {
    return fFunction.apply(element);
  }

  /** @return <code>F.NIL</code>, if no evaluation is possible */
  @Override
  public IExpr visit(IDataExpr<?> element) {
    return fFunction.apply(element);
  }

  /** @return <code>F.NIL</code>, if no evaluation is possible */
  @Override
  public IExpr visit(IFraction element) {
    return fFunction.apply(element);
  }

  @Override
  public IExpr visit(IInteger element) {
    return fFunction.apply(element);
  }

  /** @return <code>F.NIL</code>, if no evaluation is possible */
  @Override
  public IExpr visit(INum element) {
    return fFunction.apply(element);
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
    for (int i = fOffset; i < size; i++) {
      IExpr temp = assoc.getValue(i).accept(this);
      if (temp.isPresent()) {
        // something was evaluated - return a new IAST:
        IAST iRuleOrNIL = assoc.getRule(i);
        IASTMutable result;
        if (iRuleOrNIL.isPresent()) {
          result = assoc.setAtCopy(i, iRuleOrNIL.setAtCopy(2, temp));
        } else {
          result = assoc.copy(); // setAtCopy(i, iRuleOrNIL.setAtCopy(2, temp));
        }
        i++;
        assoc.forEach(i, size, (x, j) -> {
          IExpr t = x.accept(this);
          if (t.isPresent()) {
            result.set(j, assoc.getRule(j).setAtCopy(2, t));
          }
        });
        return postProcessing(result);
      }
    }
    return F.NIL;
  }

  @Override
  protected IExpr visitAST(IAST ast) {
    int size = ast.size();
    for (int i = fOffset; i < size; i++) {
      IExpr temp = ast.get(i).accept(this);
      if (temp.isPresent()) {
        // something was evaluated - return a new IAST:
        IASTMutable result = ast.setAtCopy(i++, temp);
        ast.forEach(i, size, (x, j) -> {
          IExpr t = x.accept(this);
          if (t.isPresent()) {
            result.set(j, t);
          }
        });
        return postProcessing(result);
      }
    }
    return F.NIL;
  }

  public static VisitorReplaceAll createVisitor(IExpr arg) {
    if (arg instanceof DispatchExpr) {
      return ((DispatchExpr) arg).getVisitor();
    } else if (arg.isListOfRules(false) || arg.isRuleAST()) {
      return new VisitorReplaceAll((IAST) arg);
    } else if (arg.isAssociation()) {
      return new VisitorReplaceAll((IAST) arg.normal(false));
    } else {
      // (`1`) is neither a list of replacement nor a valid dispatch table and cannot be used for
      // replacing.
      throw new ArgumentTypeException("reps", F.List(arg));
    }
  }
}
