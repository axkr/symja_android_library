package org.matheclipse.core.eval.util.positions;

import java.util.function.Function;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.EvalFlags.Flag;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;

public class MapPositions extends PositionsVisitor {
  final Function<IExpr, IExpr> f;

  public MapPositions(Function<IExpr, IExpr> f, IAST ast) {
    this(f, ast, F.CEmptyList);
  }

  public MapPositions(Function<IExpr, IExpr> f, IAST ast, IAST positions) {
    super(ast, positions);
    this.f = f;
  }

  /**
   * Map a function `f` over the elements of a list of positions
   * <code>{{p11, p12, ...},{p21, p22, ...}}</code>. The function `f` is applied to the part of the
   * expression at each position.
   * 
   * @param f function to apply
   * @param ast
   * @param listOfListsOfPositions
   */
  public static IExpr mapListOfPositions(Function<IExpr, IExpr> f, IAST ast,
      IAST listOfListsOfPositions) {
    MapPositions mapPositions = new MapPositions(f, ast);
    for (int i = 1; i < listOfListsOfPositions.size(); i++) {
      IAST subList = (IAST) listOfListsOfPositions.get(i);
      if (subList.isEmpty()) {
        IExpr temp = f.apply(ast);
        if (temp.isPresent()) {
          if (temp.isASTOrAssociation()) {
            ast = (IAST) temp;
          }
        }
        continue;
      }

      mapPositions.reset(subList);
      IExpr temp = mapPositions.mapAtRecursive(ast);
      if (temp.isPresent()) {
        if (temp.isASTOrAssociation()) {
          ast = (IAST) temp;
        }
      }
    }
    removeIsCopiedRecursive(ast);
    return ast;
  }

  /**
   * Map a function `f` over the elements of a list of positions <code>{p1, p1, ...}</code>. The
   * function `f` is applied to the part of the expression at each position.
   * 
   * @param f function to apply
   * @param ast
   * @param listOfPositions list of positions
   * @return
   */
  public static IAST mapPositions(Function<IExpr, IExpr> f, IAST ast, IAST listOfPositions) {
    if (listOfPositions.isEmpty()) {
      return ast;
    }
    MapPositions mapPositions = new MapPositions(f, ast, listOfPositions);
    IAST result = mapPositions.mapAtRecursive(ast);
    removeIsCopiedRecursive(result);
    return result;
  }

  @Override
  protected IAST visitOther(IAST ast, IExpr position) {
    if (position == S.All) {
      return mapAtRecursiveSpan(ast, 1, ast.size(), 1);
    }
    if (position.isAST(S.Span, 3, 4)) {
      return mapAtRecursiveSpan(ast, (IAST) position);
    }
    return null;
  }

  @Override
  protected IAST keyLeaf(IAST ast, int index, IAST rule) {
    IExpr temp = f.apply(rule.second());
    if (temp.isPresent()) {
      IASTAppendable association = getAppendableAST(ast);
      association.set(index, rule.setAtCopy(2, temp));
      return association;
    }
    return null;
  }

  @Override
  protected IAST indexLeaf(IAST ast, int p) {
    IExpr temp = f.apply(ast.get(p));
    if (temp.isPresent()) {
      if (ast.isAssociation()) {
        IExpr rule = ((IAST) ast.getRule(p)).setAtCopy(2, temp);
        return ast.setAtCopy(p, rule);
      }
      return ast.setAtCopy(p, temp);
    }
    return null;
  }

  protected IAST mapAtRecursiveSpan(IAST ast, IAST span) {
    int startPosition = Validate.checkIntType(span, 1, Integer.MIN_VALUE);
    if (startPosition < 0) {
      startPosition = ast.size() + startPosition;
      if (startPosition < 0) {
        // Cannot take positions `1` through `2` in `3`.
        throw new ArgumentTypeException("take", F.list(span.arg1(), span.arg2(), ast));
      }
    } else if (startPosition > ast.size()) {
      // Cannot take positions `1` through `2` in `3`.
      throw new ArgumentTypeException("take", F.list(span.arg1(), span.arg2(), ast));
    }
    int endPosition = Validate.checkIntType(span, 2, Integer.MIN_VALUE) + 1;
    if (endPosition < 0) {
      endPosition = ast.size() + endPosition;
      if (endPosition < 0) {
        // Cannot take positions `1` through `2` in `3`.
        throw new ArgumentTypeException("take", F.list(span.arg1(), span.arg2(), ast));
      }
    } else if (endPosition > ast.size()) {
      // Cannot take positions `1` through `2` in `3`.
      throw new ArgumentTypeException("take", F.list(span.arg1(), span.arg2(), ast));
    }
    int step = 1;
    if (span.isAST3()) {
      step = Validate.checkIntType(span, 3, 0);
    }

    return mapAtRecursiveSpan(ast, startPosition, endPosition, step);
  }


  protected IAST mapAtRecursiveSpan(IAST ast, int startPosition, int endPosition, int step) {
    IASTMutable result = getAppendableAST(ast);
    if (level == positions.argSize()) {
      for (int i = startPosition; i < endPosition; i += step) {
        IExpr temp = f.apply(ast.get(i));
        if (temp.isPresent()) {
          result.set(i, temp);
        }
      }
    } else {
      level++;
      for (int i = startPosition; i < endPosition; i += step) {
        IExpr temp = mapAtRecursive(result.getAST(i));
        if (temp.isPresent()) {
          result.set(i, temp);
        }
      }
      level--;
    }
    return result;
  }

  private static IASTMutable getMutableAST(IAST ast) {
    if (ast.hasFlag(Flag.IS_COPIED)) {
      return (IASTMutable) ast;
    }
    IASTMutable mutable = ast.copy();
    mutable.addFlag(Flag.IS_COPIED);
    return mutable;
  }

  protected static void removeIsCopiedRecursive(IAST list) {
    if (list.hasFlag(Flag.IS_COPIED)) {
      list.clearFlag(Flag.IS_COPIED);
      for (int i = 0; i < list.size(); i++) {
        IExpr element = list.getRule(i);
        if (element.hasFlag(Flag.IS_COPIED)) {
          removeIsCopiedRecursive((IAST) element);
        }
      }
    }
  }
}
