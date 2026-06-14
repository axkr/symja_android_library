package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.matheclipse.core.builtin.BooleanFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * LogicalExpand(expr)
 * </pre>
 *
 * <blockquote>
 * <p>
 * expands out complicated logical expressions.
 * </p>
 * </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; LogicalExpand(p &amp;&amp; (q || r))
 * (p &amp;&amp; q) || (p &amp;&amp; r)
 *
 * &gt;&gt; LogicalExpand(Implies(p, q))
 * !p || q
 *
 * &gt;&gt; LogicalExpand(Equivalent(p, q))
 * (p &amp;&amp; q) || (!p &amp;&amp; !q)
 * </pre>
 */
public class LogicalExpand extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (arg1.isAST()) {
      IExpr result = F.subst(arg1, logicalExpand(engine));
      if (result.isPresent() && !result.equals(arg1)) {
        return result;
      }
    }
    return arg1;
  }

  private static Function<IExpr, IExpr> logicalExpand(EvalEngine engine) {
    return x -> {
      if (x.isAST()) {
        IAST formula = (IAST) x;
        IExpr head = formula.head();

        if (formula.isValidBuiltInFunction()) {
          IExpr converted = formula;
          int ordinal = ((IBuiltInSymbol) head).ordinal();
          switch (ordinal) {
            case ID.Equal:
            case ID.Unequal:
              if (formula.isAST2()) {
                if (formula.arg1().isList() && formula.arg2().isList()) {
                  IAST list1 = (IAST) formula.arg1();
                  IAST list2 = (IAST) formula.arg2();
                  if (list1.size() == list2.size()) {
                    IASTAppendable logicalResult =
                        ordinal == ID.Equal ? F.AndAlloc(list1.size())
                            : F.OrAlloc(list1.size());
                    for (int i = 1; i < list1.size(); i++) {
                      logicalResult.append(F.binaryAST2(head, list1.get(i), list2.get(i)));
                    }
                    converted = engine.evaluate(logicalResult);
                  }
                } else {
                  IExpr diff = engine.evaluate(F.Subtract(formula.arg1(), formula.arg2()));
                  if (diff.isAST(S.SeriesData, 7)) {
                    IAST seriesData = (IAST) diff;
                    IExpr coeffs = seriesData.arg3();
                    if (coeffs.isList()) {
                      IAST list = (IAST) coeffs;
                      IASTAppendable logicalResult =
                          ordinal == ID.Equal ? F.AndAlloc(list.size())
                              : F.OrAlloc(list.size());
                      for (int i = 1; i < list.size(); i++) {
                        logicalResult.append(F.binaryAST2(head, list.get(i), F.C0));
                      }
                      converted = engine.evaluate(logicalResult);
                    }
                  }
                }
              }
              break;

            case ID.Not:
              if (formula.argSize() == 1 && formula.arg1().isOr()) {
                IAST result = ((IAST) formula.arg1()).apply(S.And);
                converted = result.map(arg -> F.Not(arg)).eval(engine);
              } else {
                converted = applyBooleanConvert(formula, engine);
              }
              break;

            case ID.Xor:
              if (formula.argSize() >= 3) {
                converted = engine.evaluate(BooleanFunctions.xorToDNF(formula));
              } else {
                converted = applyBooleanConvert(formula, engine);
              }
              break;

            case ID.And:
            case ID.Or:
            case ID.Implies:
            case ID.Equivalent:
            case ID.Nand:
            case ID.Nor:
            case ID.Xnor:
            case ID.BooleanFunction:
              converted = applyBooleanConvert(formula, engine);
              break;

            default:
              break;
          }

          if (converted.isPresent()) {
            IExpr absorbed = logicalAbsorb(converted, engine);
            if (!absorbed.equals(formula)) {
              return absorbed;
            }
          }
        }
      }
      return F.NIL;
    };
  }

  /**
   * Absorb and reduce redundant elements in a DNF Or-expression. Includes strict subset
   * subsumption, consensus reduction, and canonical sorting to mimic minimal prime implicants
   * generation.
   */
  private static IExpr logicalAbsorb(IExpr expr, EvalEngine engine) {
    if (expr.isOr()) {
      IAST orAST = (IAST) expr;
      boolean changed = false;

      List<IExpr> terms = new ArrayList<>(orAST.size() - 1);
      for (int i = 1; i < orAST.size(); i++) {
        terms.add(orAST.get(i));
      }

      boolean loop = true;
      while (loop) {
        loop = false;

        // 1. Subsumption: Removes supersets (e.g., r || (q && r && s) -> r)
        for (int i = 0; i < terms.size(); i++) {
          IExpr argI = terms.get(i);
          boolean subsumed = false;
          for (int j = 0; j < terms.size(); j++) {
            if (i == j) {
              continue;
            }
            IExpr argJ = terms.get(j);
            if (isSubsumed(argI, argJ)) {
              subsumed = true;
              break;
            }
          }

          if (subsumed) {
            terms.remove(i);
            changed = true;
            loop = true;
            break;
          }
        }

        if (loop) {
          continue;
        }

        // 2. Reduction / Consensus: (a && b && !c) || (a && c) -> (a && b) || (a && c)
        for (int i = 0; i < terms.size(); i++) {
          IExpr argI = terms.get(i);
          boolean reduced = false;
          for (int j = 0; j < terms.size(); j++) {
            if (i == j) {
              continue;
            }
            IExpr argJ = terms.get(j);
            IExpr reducedI = reduceTerm(argI, argJ);
            if (reducedI.isPresent()) {
              terms.set(i, reducedI);
              changed = true;
              loop = true;
              reduced = true;
              break;
            }
          }

          if (reduced) {
            break;
          }
        }
      }

      if (terms.isEmpty()) {
        return S.False;
      }
      if (terms.size() == 1) {
        IExpr single = terms.get(0);
        if (single.isAnd()) {
          IASTMutable singleAnd = ((IAST) single).copy();
          singleAnd.sortInplace();
          return engine.evaluate(singleAnd);
        }
        return engine.evaluate(single);
      }

      IASTAppendable result = F.OrAlloc(terms.size());
      for (IExpr t : terms) {
        if (t.isAnd()) {
          IASTMutable tMutable = ((IAST) t).copy();
          tMutable.sortInplace();
          result.append(tMutable);
        } else {
          result.append(t);
        }
      }

      // Enforce standardized canonical order
      result.sortInplace();

      if (changed || !result.equals(orAST)) {
        return engine.evaluate(result);
      }
    } else if (expr.isAnd()) {
      IASTMutable andAST = ((IAST) expr).copy();
      andAST.sortInplace();
        return engine.evaluate(andAST);
    }
    return expr;
  }

  /**
   * Applies the consensus rule. If argJ has exactly one opposite literal to argI, and all other
   * literals in argJ are present in argI, the opposing literal in argI is redundant and can be
   * reduced.
   */
  private static IExpr reduceTerm(IExpr argI, IExpr argJ) {
    IAST andI = argI.isAnd() ? (IAST) argI : F.And(argI);
    IAST andJ = argJ.isAnd() ? (IAST) argJ : F.And(argJ);

    IExpr oppositeI = null;
    IExpr oppositeJ = null;

    // Find the single opposite literal
    for (int j = 1; j < andJ.size(); j++) {
      IExpr litJ = andJ.get(j);
      IExpr notLitJ = litJ.isNot() ? litJ.first() : F.Not(litJ);
      if (andI.contains(notLitJ)) {
        if (oppositeJ != null) {
          return F.NIL; // Cannot reduce if more than one literal is opposite
        }
        oppositeJ = litJ;
        oppositeI = notLitJ;
      }
    }

    if (oppositeJ == null) {
      return F.NIL;
    }

    // Verify all other literals of argJ exist in argI
    for (int j = 1; j < andJ.size(); j++) {
      IExpr litJ = andJ.get(j);
      if (litJ.equals(oppositeJ)) {
        continue;
      }
      if (!andI.contains(litJ)) {
        return F.NIL;
      }
    }

    // Safely remove the opposite literal from argI
    IASTAppendable newAndI = F.AndAlloc(andI.argSize());
    for (int i = 1; i < andI.size(); i++) {
      if (!andI.get(i).equals(oppositeI)) {
        newAndI.append(andI.get(i));
      }
    }

    if (newAndI.size() == 1) {
      return S.True; // Completely reduced
    }
    return newAndI.isAST1() ? newAndI.arg1() : newAndI;
  }

  /**
   * Returns true if argJ subsumes argI (i.e. argJ is a subset of argI's literals).
   */
  private static boolean isSubsumed(IExpr argI, IExpr argJ) {
    if (argI.equals(argJ)) {
      return false;
    }
    IAST andI = argI.isAnd() ? (IAST) argI : F.And(argI);
    if (argJ.isAnd()) {
      IAST andJ = (IAST) argJ;
      for (int j = 1; j < andJ.size(); j++) {
        if (!andI.contains(andJ.get(j))) {
          return false;
        }
      }
      return true;
    } else {
      return andI.contains(argJ);
    }
  }

  private static IExpr applyBooleanConvert(IAST formula, EvalEngine engine) {
    try {
      IExpr result =
          BooleanFunctions.booleanConvert(F.BooleanConvert(formula, F.stringx("DNF")), engine);
      if (result.isPresent()) {
        return result;
      }
    } catch (final ValidateException ve) {
      // necessary here because of direct calls
    }
    return formula;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}