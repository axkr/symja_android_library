package org.matheclipse.core.reflection.system;

import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.AlgebraicNumberUtils;

public class IrreduciblePolynomialQ extends AbstractFunctionOptionEvaluator {

  public IrreduciblePolynomialQ() {}


  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    IExpr poly = ast.arg1();

    // Find the variables of the expression
    VariablesSet variablesSet = new VariablesSet(poly);
    IAST variables = variablesSet.getVarList();

    // Constants are never irreducible polynomials (units / zero / non-unit integers all map to
    // False).
    if (variables.argSize() == 0) {
      return S.False;
    }

    // Check if the given expression is structurally a polynomial
    IExpr isPoly = engine.evaluate(F.PolynomialQ(poly, variables));
    if (!isPoly.isTrue()) {
      return S.False;
    }

    // Resolve the Extension option (None / Automatic / All / explicit list / single algebraic
    // number) into an explicit list of generators, then split off the imaginary unit (which is
    // supported by Factor via the GaussianIntegers option) from the remaining algebraic generators
    // (which Factor does not currently honor and which we therefore handle via a root-based or
    // Trager-style reducibility test below).
    IAST extensionGenerators = F.CEmptyList;
    boolean gaussianFromExtension = false;
    IExpr extensionOption = options[0];
    if (extensionOption.equals(S.Automatic)) {
      extensionGenerators = AlgebraicNumberUtils.extractAlgebraicGenerators(poly);
    } else if (extensionOption.equals(S.All)) {
      // Absolute irreducibility over the algebraic closure of Q; approximate by adjoining I
      // (sufficient for absolute irreducibility of e.g. x^2 + y^2). Full algebraic closure is not
      // implemented.
      gaussianFromExtension = true;
    } else if (extensionOption.isList()) {
      extensionGenerators = (IAST) extensionOption;
    } else if (!extensionOption.equals(S.None)) {
      // Single algebraic number, e.g. Extension -> Sqrt(3); treat as singleton list
      extensionGenerators = F.List(extensionOption);
    }
    if (extensionGenerators.argSize() > 0) {
      IASTAppendable nonI = F.ListAlloc(extensionGenerators.argSize());
      for (int i = 1; i <= extensionGenerators.argSize(); i++) {
        IExpr g = extensionGenerators.get(i);
        if (g.isImaginaryUnit()) {
          gaussianFromExtension = true;
        } else {
          nonI.append(g);
        }
      }
      extensionGenerators = nonI;
    }

    // Root-based reducibility check for non-trivial Extension generators (univariate only). If any
    // root of poly lies in Q(generators [+ I]) then poly is reducible over the extension.
    if (extensionGenerators.argSize() > 0 && variables.argSize() == 1) {
      IAST allGens;
      if (gaussianFromExtension) {
        IASTAppendable tmp = F.ListAlloc(extensionGenerators.argSize() + 1);
        tmp.appendArgs(extensionGenerators);
        tmp.append(S.I);
        allGens = tmp;
      } else {
        allGens = extensionGenerators;
      }
      IExpr variable = variables.arg1();
      IExpr solveResult = engine.evaluate(F.Solve(F.Equal(poly, F.C0), variable));
      if (solveResult.isList()) {
        IAST solutions = (IAST) solveResult;
        for (int i = 1; i <= solutions.argSize(); i++) {
          IExpr sol = solutions.get(i);
          if (sol.isList()) {
            IAST rules = (IAST) sol;
            for (int j = 1; j <= rules.argSize(); j++) {
              IExpr rule = rules.get(j);
              if (rule.isRule() && rule.first().equals(variable)) {
                IExpr root = rule.second();
                if (AlgebraicNumberUtils.isInExtension(root, allGens)) {
                  return S.False;
                }
              }
            }
          }
        }
      }
    }

    // Multivariate fallback for non-trivial Extension generators: Trager-style resultant test.
    // Substitute the single non-I generator alpha with a fresh symbol t whose minimal polynomial is
    // m(t). For increasing shifts s = 0, 1, ... compute R = Resultant_t(m(t), p[v -> v + s*t], t)
    // over the first variable v, until R is square-free. Then poly is irreducible over Q(alpha)
    // iff R has exactly one (non-constant) irreducible factor over Q.
    if (extensionGenerators.argSize() == 1 && variables.argSize() >= 2) {
      IExpr alpha = extensionGenerators.arg1();
      ISymbol t = F.Dummy("$ipqT");
      IExpr m = engine.evaluate(F.binaryAST2(S.MinimalPolynomial, alpha, t));
      if (m.isPresent() && !m.isAST(S.MinimalPolynomial)) {
        IExpr pT = engine.evaluate(F.subst(poly, alpha, t));
        IExpr v = variables.arg1();
        final int maxShift = 5;
        for (int s = 0; s <= maxShift; s++) {
          IExpr pShifted = pT;
          if (s > 0) {
            pShifted = engine.evaluate(F.subst(pT, v, F.Plus(v, F.Times(F.ZZ(s), t))));
          }
          IExpr resultant = engine.evaluate(F.Resultant(m, pShifted, t));
          if (!resultant.isPresent() || resultant.isZero()) {
            continue;
          }
          IExpr factored = engine.evaluate(F.Factor(resultant));
          int[] info = analyzeFactored(factored, variables);
          int count = info[0];
          boolean squarefree = info[1] == 1;
          if (count < 0 || !squarefree) {
            // Factor could not decide or factorization not yet squarefree -> try a larger shift
            continue;
          }
          return count >= 2 ? S.False : S.True;
        }
      }
    }

    // Build the delegated Factor[...] call. Rewrite Extension -> Automatic / {..., I, ...} so that
    // the I component becomes GaussianIntegers -> True (which Factor supports) and pass through all
    // other options unchanged.
    IASTAppendable factorAST = F.ast(S.Factor);
    factorAST.append(poly);
    boolean gaussianOptionAlreadyPresent = false;
    for (int i = 2; i <= originalAST.argSize(); i++) {
      IExpr arg = originalAST.get(i);
      if (arg.isRule()) {
        IExpr lhs = arg.first();
        if (lhs.equals(S.Extension)) {
          // Drop the Extension rule; if we still need to pass I to Factor we add
          // GaussianIntegers -> True below.
          continue;
        }
        if (lhs.equals(S.GaussianIntegers)) {
          gaussianOptionAlreadyPresent = true;
          if (gaussianFromExtension) {
            factorAST.append(F.Rule(S.GaussianIntegers, S.True));
          } else {
            factorAST.append(arg);
          }
          continue;
        }
      }
      factorAST.append(arg);
    }
    if (gaussianFromExtension && !gaussianOptionAlreadyPresent) {
      factorAST.append(F.Rule(S.GaussianIntegers, S.True));
    }

    IExpr factored = engine.evaluate(factorAST);

    int nonConstantFactorsCount = 0;

    if (factored.isAST()) {
      IAST astFactored = (IAST) factored;
      IExpr head = astFactored.head();

      if (head.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.Factor:
            // Factor could not evaluate the polynomial (e.g., unsupported extension)
            return F.NIL;
          case ID.Times:
            for (int i = 1; i <= astFactored.argSize(); i++) {
              IExpr arg = astFactored.get(i);
              if (hasVariables(arg, variables)) {
                if (arg.isPower()) {
                  IExpr exp = arg.getAt(2);
                  if (exp.isInteger() && !exp.isOne()) {
                    // A power factor with an integer exponent > 1 means the polynomial is reducible
                    return S.False;
                  }
                }
                nonConstantFactorsCount++;
              }
            }
            break;
          case ID.Power:
            IExpr base = astFactored.getAt(1);
            IExpr exp = astFactored.getAt(2);
            if (hasVariables(base, variables)) {
              if (exp.isInteger() && !exp.isOne()) {
                return S.False;
              }
              nonConstantFactorsCount++;
            }
            break;
          default:
            // Expressions like Plus[...] fall through here
            if (hasVariables(astFactored, variables)) {
              nonConstantFactorsCount++;
            }
            break;
        }
      } else {
        if (hasVariables(astFactored, variables)) {
          nonConstantFactorsCount++;
        }
      }
    } else {
      // Non-AST expressions (e.g. a single Symbol)
      if (hasVariables(factored, variables)) {
        nonConstantFactorsCount++;
      }
    }

    // An irreducible polynomial should only have 1 non-constant factor evaluated
    return nonConstantFactorsCount == 1 ? S.True : S.False;
  }

  /**
   * Checks if the given factor expression contains any of the polynomial variables.
   *
   * @param expr the factor expression to check
   * @param variables the list of variables present in the original polynomial
   * @return true if the expression depends on any variable, false otherwise
   */
  private boolean hasVariables(IExpr expr, IAST variables) {
    for (int i = 1; i <= variables.argSize(); i++) {
      if (!expr.isFree(variables.get(i))) {
        return true;
      }
    }
    return false;
  }

  /**
   * Inspect a {@code Factor}-result expression and return a two-element {@code int[]}:
   * <ul>
   * <li>index {@code 0}: the number of distinct non-constant irreducible factors, or {@code -1}
   * when {@code Factor} returned its input unchanged;</li>
   * <li>index {@code 1}: {@code 1} when the factorization is square-free (no factor with integer
   * exponent &ge; 2), {@code 0} otherwise.</li>
   * </ul>
   */
  private int[] analyzeFactored(IExpr factored, IAST variables) {
    int count = 0;
    boolean squarefree = true;
    if (factored.isAST()) {
      IAST astFactored = (IAST) factored;
      IExpr head = astFactored.head();
      if (head.isBuiltInSymbol()) {
        int id = ((IBuiltInSymbol) head).ordinal();
        if (id == ID.Factor) {
          return new int[] {-1, 1};
        }
        if (id == ID.Times) {
          for (int i = 1; i <= astFactored.argSize(); i++) {
            IExpr arg = astFactored.get(i);
            if (hasVariables(arg, variables)) {
              if (arg.isPower()) {
                IExpr exp = arg.getAt(2);
                if (exp.isInteger() && !exp.isOne()) {
                  squarefree = false;
                }
              }
              count++;
            }
          }
          return new int[] {count, squarefree ? 1 : 0};
        }
        if (id == ID.Power) {
          IExpr base = astFactored.getAt(1);
          IExpr exp = astFactored.getAt(2);
          if (hasVariables(base, variables)) {
            if (exp.isInteger() && !exp.isOne()) {
              squarefree = false;
            }
            count = 1;
          }
          return new int[] {count, squarefree ? 1 : 0};
        }
      }
    }
    if (hasVariables(factored, variables)) {
      count = 1;
    }
    return new int[] {count, 1};
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.LISTABLE);
    // Set default options: Extension -> None, GaussianIntegers -> False, Modulus -> 0
    setOptions(newSymbol, new IBuiltInSymbol[] {S.Extension, S.GaussianIntegers, S.Modulus},
        new IExpr[] {S.None, S.False, F.C0});
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

}