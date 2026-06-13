package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.CombinatoricUtil;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class PermutationProduct extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.isEmpty()) {
      return F.Cycles(F.CEmptyList);
    }
    if (ast.isAST1()) {
      return ast.arg1();
    }

    IASTAppendable resultBuilder = F.ast(S.PermutationProduct, ast.argSize());
    List<IExpr> currentBlock = new ArrayList<>();
    boolean allLists = true;
    boolean hasSymbols = false;
    boolean isEvaled = false;

    for (int i = 1; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      boolean isValidPerm = isValidPermutation(arg, engine);

      if (isValidPerm) {
        if (!arg.isList()) {
          allLists = false;
        }
        currentBlock.add(arg);
      } else {
        hasSymbols = true;
        // Process and collapse the accumulated block before adding the roadblock symbol
        if (!currentBlock.isEmpty()) {
          IExpr collapsed = collapseBlock(currentBlock, allLists, engine);
          if (!isIdentityCycles(collapsed)) {
            if (currentBlock.size() > 1) {
              isEvaled = true;
            }
            resultBuilder.append(collapsed);
          }
          currentBlock.clear();
        }
        // Reset list-only tracking for the next block, and append the un-permutable symbol
        allLists = true;
        resultBuilder.append(arg);
      }
    }

    // Process any remaining permutations at the tail end of the sequence
    if (!currentBlock.isEmpty())

    {
      IExpr collapsed = collapseBlock(currentBlock, allLists, engine);
      if (!hasSymbols) {
        return collapsed; // Complete success: return the collapsed outcome directly
      }
      if (!isIdentityCycles(collapsed)) {
        resultBuilder.append(collapsed);
      }
    }

    // If everything was stripped out because they evaluated to identity cycles, return identity
    if (resultBuilder.isEmpty()) {
      return F.Cycles(F.CEmptyList);
    }

    // If only one element remains un-evaluated in the product tree, strip the enclosing head
    if (resultBuilder.isAST1()) {
      return resultBuilder.arg1();
    }

    return isEvaled ? resultBuilder : F.NIL;
  }

  private boolean isValidPermutation(IExpr arg, EvalEngine engine) {
    if (arg.isAST(S.Cycles, 2)) {
      IAST cycles = arg.isEvalFlagOn(IAST.BUILT_IN_EVALED) ? (IAST) arg
          : CombinatoricUtil.checkCycles((IAST) arg, true, engine);
      return cycles.isPresent();
    } else if (arg.isList()) {
      if (arg.isEmptyList()) {
        return false;
      }
      return CombinatoricUtil.permutationCycles((IAST) arg).isAST(S.Cycles, 2);
    }
    return false;
  }

  private IExpr collapseBlock(List<IExpr> block, boolean allLists, EvalEngine engine) {
    if (block.size() == 1) {
      return block.get(0);
    }

    IAST[] cycleLists = new IAST[block.size()];
    int maxSupport = -1;

    for (int i = 0; i < block.size(); i++) {
      IExpr arg = block.get(i);
      IAST cycles;
      if (arg.isAST(S.Cycles, 2)) {
        cycles = arg.isEvalFlagOn(IAST.BUILT_IN_EVALED) ? (IAST) arg
            : CombinatoricUtil.checkCycles((IAST) arg, false, engine);
      } else {
        cycles = (IAST) CombinatoricUtil.permutationCycles((IAST) arg);
        if (((IAST) arg).argSize() > maxSupport) {
          maxSupport = ((IAST) arg).argSize();
        }
      }

      IAST mainList = (IAST) cycles.arg1();
      cycleLists[i] = mainList;

      int currentLen = CombinatoricUtil.determineLengthFromCycles(mainList);
      if (currentLen > maxSupport) {
        maxSupport = currentLen;
      }
    }

    if (maxSupport < 1) {
      return allLists ? F.CEmptyList : F.Cycles(F.CEmptyList);
    }

    IAST range = F.mapRange(1, maxSupport + 1, i -> F.ZZ(i));
    for (IAST mainList : cycleLists) {
      range = CombinatoricUtil.permutationReplace(range, mainList);
    }

    return allLists ? range : CombinatoricUtil.permutationCycles(range);
  }

  private boolean isIdentityCycles(IExpr expr) {
    if (expr.isAST(S.Cycles, 2)) {
      return ((IAST) expr).arg1().isEmptyList();
    }
    return false;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_0_INFINITY;
  }
}
