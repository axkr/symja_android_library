package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 *
 *
 * <pre>
 * Extract(expr, list)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * extracts parts of <code>expr</code> specified by <code>list</code>.
 *
 * </blockquote>
 *
 * <pre>
* Extract(expr, {list1, list2, ...})'
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * extracts a list of parts.
 *
 * </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <p>
 * <code>Extract(expr, i, j, ...)</code> is equivalent to <code>Part(expr, {i, j, ...})</code>.
 *
 * <pre>
* &gt;&gt; Extract(a + b + c, {2})
* b
*
* &gt;&gt; Extract({{a, b}, {c, d}}, {{1}, {2, 2}})
* {{a,b},d}
 * </pre>
 */
public final class Extract extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (ast.arg1().isASTOrAssociation()) {
      IAST list = (IAST) ast.arg1();
      IExpr arg3 = F.NIL;
      if (ast.isAST3()) {
        arg3 = ast.arg3();
      }
      if (ast.arg2().isInteger()) {
        int indx = ast.arg2().toIntDefault();
        if (F.isNotPresent(indx)) {
          return F.NIL;
        }
        if (indx < 0) {
          // negative n counts from the end
          indx = list.size() + indx;
          if (indx <= 0) {
            // == 0 - for MMA behaviour
            return F.NIL;
          }
        }
        if (indx < list.size()) {
          if (arg3.isPresent()) {
            return F.unaryAST1(arg3, list.get(indx));
          }
          return list.get(indx);
        }
      } else if (ast.arg2().isList()) {
        IAST arg2 = (IAST) ast.arg2();
        if (arg2.isListOfLists()) {
          final int arg2Size = arg2.size();
          IASTAppendable result = F.ListAlloc(arg2Size);
          for (int i = 1; i < arg2Size; i++) {
            IAST positions = arg2.getAST(i);
            if (!checkPositions(ast, positions, engine)) {
              return F.NIL;
            }
            if (arg3.isPresent()) {
              // Direct structural extraction — no evaluation of result elements
              IASTAppendable partAST = F.Part(positions.argSize(), list);
              partAST.appendAll(positions, 1, positions.size());
              IExpr rawResult = Part.part(list, partAST, 2, engine);
              if (rawResult.isNIL()) {
                return F.NIL;
              }
              result.append(F.unaryAST1(arg3, rawResult));
            } else {
              IExpr temp = extract(list, positions, engine);
              if (temp.isNIL()) {
                return F.NIL;
              }
              result.append(temp);
            }
          }
          return result;
        }
        if (arg2.isEmptyList()) {
          return F.CEmptyList;
        }
        if (!checkPositions(ast, arg2, engine)) {
          return F.NIL;
        }

        if (arg3.isPresent()) {
          IASTAppendable partAST = F.Part(arg2.argSize(), list);
          partAST.appendAll(arg2, 1, arg2.size());
          // Direct structural extraction via Part.part() — no evaluation of result elements
          IExpr rawResult = Part.part(list, partAST, 2, engine);
          if (rawResult.isNIL()) {
            return F.NIL;
          }
          return F.unaryAST1(arg3, rawResult);
        }
        return extract(list, arg2, engine);
      }
    }
    return F.NIL;
  }

  private boolean checkPositions(IAST ast, IAST positions, EvalEngine engine) {
    for (int j = 1; j < positions.size(); j++) {
      IExpr arg = positions.get(j);
      if (arg.isAST(S.Key)) {
        continue;
      }
      // Allow All, Span (;;), or list of indices
      if (arg == S.All || arg.isAST(S.Span) || arg.isList()) {
        continue;
      }
      int intValue = arg.toIntDefault();
      if (F.isNotPresent(intValue)) {
        // Position specification `1` in `2` is not applicable.
        Errors.printMessage(ast.topHead(), "psl1", F.list(ast.arg2(), ast), engine);
        return false;
      }
    }
    return true;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_3_1;
  }

  private static IExpr extract(final IAST list, final IAST position, EvalEngine engine) {
    IASTAppendable part = F.Part(position.argSize(), list);
    part.appendAll(position, 1, position.size());
    return engine.evaluate(part);
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.NHOLDREST);
  }
}