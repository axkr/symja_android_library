package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.frobenius.FrobeniusSolver;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.FEConfig;

/**
 *
 *
 * <pre>
 * FrobeniusSolve({a1, ... ,aN}, M)
 * </pre>
 *
 * <blockquote>
 *
 * <p>get a list of solutions for the Frobenius equation given by the list of integers <code>
 * {a1, ... ,aN}</code> and the non-negative integer <code>M</code>.
 *
 * </blockquote>
 *
 * <p>The Frobenius problem, also known as the postage-stamp problem or the money-changing problem,
 * is an integer programming problem that seeks nonnegative integer solutions to <code>
 * x1*a1 + ... + xN*aN = M</code> where <code>ai</code> and <code>M</code> are positive integers. In
 * particular, the Frobenius number <code>FrobeniusNumber({a1, ... ,aN})</code>, is the largest
 * <code>M</code> so that this equation fails to have a solution.
 *
 * <p>See:
 *
 * <ul>
 *   <li><a href="https://en.wikipedia.org/wiki/Coin_problem">Wikipedia - Coin problem</a>
 * </ul>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; FrobeniusSolve({2, 3, 4}, 29)
 * {{0,3,5},{0,7,2},{1,1,6},{1,5,3},{1,9,0},{2,3,4},{2,7,1},{3,1,5},{3,5,2},{4,3,3},{
 * 4,7,0},{5,1,4},{5,5,1},{6,3,2},{7,1,3},{7,5,0},{8,3,1},{9,1,2},{10,3,0},{11,1,1},{
 * 13,1,0}}
 * </pre>
 */
public class FrobeniusSolve extends AbstractEvaluator {

  public FrobeniusSolve() {
    // default ctor
  }

  /** {@inheritDoc} */
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.arg1().isList() && ast.arg2().isInteger()) {
      IAST list = ast.getAST(1);
      try {
        int[] listInt = Validate.checkListOfInts(ast, list, true, false, engine);
        if (listInt != null) {
          for (int i = 0; i < listInt.length; i++) {
            if (listInt[i] < 0 && ast.size() < 4) {}
          }
          IInteger[] solution;
          IASTAppendable result = F.ListAlloc(8);
          FrobeniusSolver solver = getSolver(listInt, (IInteger) ast.arg2());
          int numberOfSolutions = -1; // all solutions
          if (ast.size() == 4) {
            numberOfSolutions = ast.arg3().toIntDefault(-1);
          }
          while ((solution = solver.take()) != null) {
            if (result.size() >= Config.MAX_AST_SIZE) {
              throw new ASTElementLimitExceeded(result.size());
            }
            result.append(F.List(solution));
          }

          return result;
        }
      } catch (LimitException le) {
        throw le;
      } catch (RuntimeException rex) {
        if (FEConfig.SHOW_STACKTRACE) {
          rex.printStackTrace();
        }
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_3;
  }

  public static FrobeniusSolver getSolver(int[] listInt, IInteger number) {
    IInteger[][] equations = new IInteger[1][listInt.length + 1];
    // format looks like: { { 12, 16, 20, 27, 123 } };
    for (int j = 0; j < listInt.length; j++) {
      equations[0][j] = F.ZZ(listInt[j]);
    }
    // list.forEach((x, i) -> equations[0][i - 1] = (IInteger) x);
    equations[0][listInt.length] = number;

    return new FrobeniusSolver(equations);
  }

  /** {@inheritDoc} */
  @Override
  public void setUp(final ISymbol newSymbol) {}
}
