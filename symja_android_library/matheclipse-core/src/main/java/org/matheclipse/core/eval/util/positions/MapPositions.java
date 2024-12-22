package org.matheclipse.core.eval.util.positions;

import java.util.IdentityHashMap;
import java.util.function.Function;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;

public class MapPositions {
  final IdentityHashMap<IAST, IExpr> resultASTCache = new IdentityHashMap<IAST, IExpr>();
  int index;
  IAST positions;
  final Function<IExpr, IExpr> f;

  public MapPositions(Function<IExpr, IExpr> f) {
    this.f = f;
    reset(F.CEmptyList);
  }

  public MapPositions(Function<IExpr, IExpr> f, IAST positions) {
    this.f = f;
    reset(positions);
  }

  private void reset(IAST positions) {
    this.index = 1;
    this.positions = positions;
  }

  /**
   * Map a function `f` over the elements of a list of positions
   * <code>{{p11, p12, ...},{p21, p22, ...}}</code>. The function `f` is applied to the part of the
   * expression at each position.
   * 
   * @param f function to apply
   * @param ast
   * @param listOfPositions
   */
  public static IExpr mapListOfPositions(Function<IExpr, IExpr> f, IAST ast, IAST listOfPositions) {
    MapPositions mapPositions = new MapPositions(f);
    for (int i = 1; i < listOfPositions.size(); i++) {
      mapPositions.reset(listOfPositions.getAST(i));
      IExpr temp = mapPositions.mapAtRecursive(ast);
      if (temp.isPresent()) {
        if (temp.isASTOrAssociation()) {
          ast = (IAST) temp;
        }
      }
    }
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
  public static IExpr mapPositions(Function<IExpr, IExpr> f, IAST ast, IAST listOfPositions) {
    MapPositions mapPositions = new MapPositions(f, listOfPositions);
    return mapPositions.mapAtRecursive(ast);
  }

  private IExpr mapAtRecursive(IAST ast) {
    IExpr pos = positions.get(index);
    if (pos.equals(S.All)) {
      IASTMutable subResult;
      IExpr value = resultASTCache.get(ast);
      if (value != null) {
        subResult = (IASTMutable) ast;
      } else {
        subResult = ast.copy();
        resultASTCache.put(subResult, S.Null);
      }
      if (index == positions.size() - 1) {
        for (int i = 1; i < ast.size(); i++) {
          IExpr temp = f.apply(ast.get(i));
          if (temp.isPresent()) {
            subResult.set(i, temp);
          }
        }
      } else {
        index++;
        for (int i = 1; i < ast.size(); i++) {
          IExpr temp = mapAtRecursive(subResult.getAST(i));
          if (temp.isPresent()) {
            subResult.set(i, temp);
          }
        }
        index--;
      }
      return subResult;
    }
    if (pos.isString() || pos.isKey()) {
      if (ast.isAssociation()) {
        IExpr key = pos.isString() ? pos : pos.first();
        IAST rule = ((IAssociation) ast).getRule(key);
        if (rule.isPresent()) {
          if (index == positions.size() - 1) {
            IExpr temp = f.apply(rule.second());
            if (temp.isPresent()) {
              rule = rule.setAtCopy(2, temp);
              IASTAppendable association;
              IExpr value = resultASTCache.get(ast);
              if (value != null) {
                association = (IASTAppendable) ast;
              } else {
                association = ast.copyAppendable();
                resultASTCache.put(association, S.Null);
              }
              association.appendRule(rule);
              return association;
            }

          } else {
            IExpr arg = rule.second();
            if (arg.isASTOrAssociation()) {
              index++;
              IExpr temp = mapAtRecursive(((IAST) arg));
              if (temp.isPresent()) {
                rule = rule.setAtCopy(2, temp);
                IASTAppendable association;
                IExpr value = resultASTCache.get(ast);
                if (value != null) {
                  association = (IASTAppendable) ast;
                } else {
                  association = ast.copyAppendable();
                  resultASTCache.put(association, S.Null);
                }
                association.appendRule(rule);
                index--;
                return association;
              }
              index--;
            }
          }
        }
        // Part `1` of `2` does not exist.
        throw new ArgumentTypeException("partw", F.list(F.list(pos), ast));
      }
    }

    int p = pos.toIntDefault();
    if (p == Integer.MIN_VALUE) {
      // Part `1` of `2` does not exist.
      throw new ArgumentTypeException("partw", F.list(F.list(pos), ast));
    }
    if (p < 0) {
      p = ast.size() + p;
    }

    if (p >= 0 && p < ast.size()) {
      if (index == positions.size() - 1) {
        IExpr temp = f.apply(ast.get(p));
        if (temp.isPresent()) {
          if (ast.isAssociation()) {
            IExpr rule = ((IAST) ast.getRule(p)).setAtCopy(2, temp);
            return ast.setAtCopy(p, rule);
          }
          return ast.setAtCopy(p, temp);
        }
      } else {
        IExpr arg = ast.get(p);
        if (arg.isASTOrAssociation()) {
          IASTMutable subResult;
          IExpr value = resultASTCache.get(ast);
          if (value != null) {
            subResult = (IASTMutable) ast;
          } else {
            subResult = ast.copy();
            resultASTCache.put(subResult, S.Null);
          }
          index++;
          IExpr temp = mapAtRecursive((IAST) arg);
          if (temp.isPresent()) {
            index--;
            subResult.set(p, temp);
            return subResult;
          }
          index--;
        }
      }
    }
    // Part `1` of `2` does not exist.
    throw new ArgumentTypeException("partw", F.list(F.list(pos), ast));
  }
}
