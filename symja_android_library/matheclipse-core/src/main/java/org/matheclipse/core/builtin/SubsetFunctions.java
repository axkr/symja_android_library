package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.patternmatching.IPatternMatcher;

public class SubsetFunctions {

  private static class Initializer {

    private static void init() {
      S.SubsetCases.setEvaluator(new SubsetCases());
      S.SubsetReplace.setEvaluator(new SubsetReplace());
    }
  }

  /**
   * <pre>
   * <code>SubsetCases(list, sublist -&gt; rhs)
   * </code>
   * </pre>
   * <p>
   * or
   * </p>
   * 
   * <pre>
   * <code>SubsetCases(list, sublist :&gt; rhs)
   * </code>
   * </pre>
   * 
   * <p>
   * returns a list of the right-hand-side <code>rhs</code> for the matching sublist pattern
   * expression <code>sublist</code> in <code>list</code>.
   * </p>
   * 
   * <pre>
   * <code>SubsetCases(list, sublist)
   * </code>
   * </pre>
   * 
   * <p>
   * returns a list of the matching sublist pattern expressions <code>sublist</code> in
   * <code>list</code>.
   * </p>
   * 
   * <p>
   * <strong>Note:</strong> this function doesn't support pattern sequences at the moment.
   * </p>
   * <h3>Examples</h3>
   * 
   * <pre>
   * <code>&gt;&gt; SubsetCases({a,b,c,d},{x_,y_} :&gt; f(x,y))
   * {f(a,b),f(c,d)}
   *         
   * &gt;&gt; SubsetCases({a,b,c,d},{x_,y_}) 
   * {{a,b},{c,d}}
   * </code>
   * </pre>
   * 
   * <h3>Related terms</h3>
   * <p>
   * <a href="SubsetReplace.md">SubsetReplace</a>, <a href="SubsetQ.md">SubsetQ</a>
   * </p>
   */
  private static final class SubsetCases extends AbstractEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.argSize() >= 2 && ast.argSize() <= 3 && ast.arg1().isAST()) {
        try {
          int maxNumberOfResults = Integer.MAX_VALUE;
          IAST arg1 = (IAST) ast.arg1();
          if (arg1.argSize() == 0) {
            return F.NIL;
          }
          IExpr rules = ast.arg2();
          if (ast.isAST3()) {
            IExpr arg3 = engine.evaluate(ast.arg3());
            if (arg3.isReal()) {
              maxNumberOfResults = ((IReal) arg3).toInt();
            }
          }
          IASTAppendable resultList = F.ListAlloc(8);
          SubsetMatchManager subsetCounter = new SubsetMatchManager(maxNumberOfResults, resultList);
          subsetCounter.subsetCases(arg1, rules, engine);
          return resultList;
        } catch (ArithmeticException ae) {
          return Errors.printMessage(S.SubsetReplace, ae, engine);
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

  }

  private static final class SubsetMatchManager {
    int maxNumberOfResult;
    IASTAppendable casesList;

    public SubsetMatchManager(int maxNumberOfResult, IASTAppendable casesList) {
      if (maxNumberOfResult > 0) {
        this.maxNumberOfResult = maxNumberOfResult;
      } else {
        this.maxNumberOfResult = Integer.MAX_VALUE;
      }
      this.casesList = casesList;
    }

    private IExpr subsetCases(IAST arg1, IExpr rules, final EvalEngine engine) {
      if (rules.forAll(x -> x.isRuleAST() || x.isList())) {
        IAST rulesList = (IAST) rules;
        IAST result = arg1;
        for (IExpr rule : rulesList) {
          if (rule.isRuleAST()) {
            IExpr temp = applyRule((IAST) rule, result, engine);
            if (temp.isAST()) {
              result = (IAST) temp;
            }
            if (maxNumberOfResult == 0) {
              return result;
            }
          } else if (rule.isList() && rule.size() > 1) {
            IExpr temp =
                applyRule(F.Rule(rule, IPatternMatcher.DUMMY_SUBSET_CASES), result, engine);
            if (temp.isAST()) {
              result = (IAST) temp;
            }
            if (maxNumberOfResult == 0) {
              return result;
            }
          } else {
            throw new ArgumentTypeException(
                "rule expressions (x->y) expected instead of " + rule.toString());
          }
        }
        return result;
      }

      if (rules.isRuleAST()) {
        return applyRule((IAST) rules, arg1, engine);
      }
      if (rules.isList()) {
        return applyRule(F.Rule(rules, IPatternMatcher.DUMMY_SUBSET_CASES), arg1, engine);
      }
      throw new ArgumentTypeException(
          "rule expressions (x->y) expected instead of " + rules.toString());
    }

    private IExpr subsetReplace(IAST arg1, IExpr rules, final EvalEngine engine) {
      if (rules.isList()) {
        IAST rulesList = (IAST) rules;
        IAST result = arg1;
        for (IExpr rule : rulesList) {
          if (rule.isRuleAST()) {
            IExpr temp = applyRule((IAST) rule, result, engine);
            if (temp.isAST()) {
              result = (IAST) temp;
            }
            if (maxNumberOfResult == 0) {
              return result;
            }
          } else {
            throw new ArgumentTypeException(
                "rule expressions (x->y) expected instead of " + rule.toString());
          }
        }
        return result;
      }

      if (rules.isRuleAST()) {
        return applyRule((IAST) rules, arg1, engine);
      }
      throw new ArgumentTypeException(
          "rule expressions (x->y) expected instead of " + rules.toString());
    }

    private IExpr applyRule(IAST rule, IAST result, final EvalEngine engine) {
      Functors.SubsetPatternFunctor function = Functors.subsetRules(rule, engine);
      IExpr temp = function.apply(result);
      if (temp.isPresent()) {
        result = (IAST) temp;

        if (casesList.isPresent()) {
          // for SubsetCases
          IExpr[] substitutedMatches = function.getSubstitutedMatches();
          if (substitutedMatches != null) {
            for (int i = 0; i < substitutedMatches.length; i++) {
              if (substitutedMatches[i] != null) {
                casesList.append(substitutedMatches[i]);
              }
            }
          }
        }

        if (--maxNumberOfResult == 0) {
          return result;
        }
      }
      return engine.evaluate(result);
    }

  }


  /**
   * <pre>
   * <code>SubsetReplace(list, sublist -&gt; rhs)
   * </code>
   * </pre>
   * <p>
   * or
   * </p>
   * 
   * <pre>
   * <code>SubsetReplace(list, sublist :&gt; rhs)
   * </code>
   * </pre>
   * 
   * <p>
   * replaces the sublist pattern expression <code>sublist</code> in <code>list</code> with the
   * right-hand-side <code>rhs</code>.
   * </p>
   * 
   * <p>
   * <strong>Note:</strong> this function doesn't support pattern sequences at the moment.
   * </p>
   * <h3>Examples</h3>
   * 
   * <pre>
   * <code>&gt;&gt; SubsetReplace({a,b,c,d},{x_,y_} :&gt; f(x,y))
   * {f(a,b),f(c,d)} 
   * </code>
   * </pre>
   * 
   * <h3>Related terms</h3>
   * <p>
   * <a href="SubsetCases.md">SubsetCases</a>, <a href="SubsetQ.md">SubsetQ</a>
   * </p>
   */
  private static final class SubsetReplace extends AbstractEvaluator {


    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.argSize() >= 2 && ast.argSize() <= 3 && ast.arg1().isAST()) {
        try {
          int maxNumberOfResults = Integer.MAX_VALUE;
          IAST arg1 = (IAST) ast.arg1();
          IExpr rules = ast.arg2();
          if (ast.isAST3()) {
            IExpr arg3 = engine.evaluate(ast.arg3());
            if (arg3.isReal()) {
              maxNumberOfResults = ((IReal) arg3).toInt();
            }
          }
          SubsetMatchManager subsetCounter = new SubsetMatchManager(maxNumberOfResults, F.NIL);
          return subsetCounter.subsetReplace(arg1, rules, engine);
        } catch (ArithmeticException ae) {
          return Errors.printMessage(S.SubsetReplace, ae, engine);
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3_1;
    }

  }

  public static void initialize() {
    Initializer.init();
  }

  private SubsetFunctions() {}
}
