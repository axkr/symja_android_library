package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
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
            // Part `1` of `2` does not exist.
            return Errors.printMessage(S.Extract, "partw", F.list(ast.arg2(), list), engine);
          }
        }
        if (indx < list.size()) {
          if (arg3.isPresent()) {
            return F.unaryAST1(arg3, list.get(indx));
          }
          return list.get(indx);
        }
        // Part `1` of `2` does not exist.
        return Errors.printMessage(S.Extract, "partw", F.list(ast.arg2(), list), engine);
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
            IExpr part = extract(list, positions, engine);
            if (part.isNIL()) {
              // an invalid position leaves the whole Extract unevaluated
              return F.NIL;
            }
            result.append(arg3.isPresent() ? F.unaryAST1(arg3, part) : part);
          }
          return result;
        }
        if (arg2.isEmptyList()) {
          return F.CEmptyList;
        }
        if (!checkPositions(ast, arg2, engine)) {
          return F.NIL;
        }
        IExpr part = extract(list, arg2, engine);
        if (part.isNIL()) {
          return F.NIL;
        }
        return arg3.isPresent() ? F.unaryAST1(arg3, part) : part;
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
      int intValue = arg.toMachineInt();
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

  /**
   * The part of <code>list</code> at <code>position</code>, without evaluating it. An invalid
   * position is reported under <code>Extract</code>.
   *
   * @return {@link F#NIL} if the position doesn't exist
   */
  private static IExpr extract(final IAST list, final IAST position, EvalEngine engine) {
    if (position.argSize() == 0) {
      // the empty position names the whole expression, as Part() with no index does
      return list;
    }
    IASTAppendable part = F.Part(position.argSize(), list);
    part.appendAll(position, 1, position.size());
    return Part.part(list, part, 2, S.Extract, engine);
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.NHOLDREST);
  }
}
