package org.matheclipse.core.reflection.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/**
 * Guards that the options of the {@code Solve} family are read by name.
 *
 * <p>
 * The option values reach an evaluator as an array indexed like the option keys that evaluator
 * declared, and the declaring functions disagree on the order: index <code>2</code> is the
 * {@link S#Modulus} of {@link S#Solve} but the {@link S#WorkingPrecision} of {@link S#NSolve}, and
 * {@link S#FindInstance} declares a single option, so the array is shorter than a reader of
 * {@link S#MaxRoots} expects. Reading by position silently mixes those up instead of failing, so
 * the mapping is asserted here.
 */
public class SolveOptionsTest extends ExprEvaluatorTestCase {

  @Test
  public void solveKeysBindByName() {
    // {Assumptions, GenerateConditions, InverseFunctions, MaxExtraConditions, MaxRoots, Modulus}
    SolveOptions options = SolveOptions.of(SolveOptions.SOLVE_KEYS,
        new IExpr[] {F.Greater(F.a, F.C0), S.False, S.False, F.C2, F.ZZ(7), F.ZZ(13)});
    assertEquals(F.Greater(F.a, F.C0), options.assumptions());
    assertSame(S.False, options.generateConditions());
    assertEquals(EvalEngine.INVERSE_FUNCTIONS_FALSE, options.inverseFunctionsMode());
    assertEquals(2, options.maxExtraConditions());
    assertEquals(F.ZZ(7), options.maxRoots());
    assertEquals(F.ZZ(13), options.modulus());
    // Solve declares no WorkingPrecision - it must not pick up any of these values
    assertSame(S.Automatic, options.workingPrecision());
  }

  @Test
  public void nsolveKeysBindByName() {
    SolveOptions options = SolveOptions.of(SolveOptions.NSOLVE_KEYS,
        new IExpr[] {S.True, F.ZZ(7), F.ZZ(30)});
    assertSame(S.True, options.generateConditions());
    assertEquals(F.ZZ(7), options.maxRoots());
    // the third value is the working precision here, not the modulus
    assertEquals(F.ZZ(30), options.workingPrecision());
    assertEquals(F.C0, options.modulus());
  }

  /**
   * The same array position means a different option depending on the caller - the key lists don't
   * even have the same length. Reading by position would mix these up instead of failing.
   */
  @Test
  public void samePositionIsADifferentOptionPerCaller() {
    IExpr[] values = new IExpr[] {S.True, F.C1000, F.ZZ(8)};
    SolveOptions solve = SolveOptions.of(SolveOptions.SOLVE_KEYS, values);
    SolveOptions nsolve = SolveOptions.of(SolveOptions.NSOLVE_KEYS, values);
    // for NSolve the third value is the working precision, for Solve it is the InverseFunctions
    assertEquals(F.ZZ(8), nsolve.workingPrecision());
    assertEquals(EvalEngine.INVERSE_FUNCTIONS_AUTOMATIC, solve.inverseFunctionsMode());
    assertSame(S.Automatic, solve.workingPrecision());
    // and the first value is the Assumptions of Solve but the GenerateConditions of NSolve
    assertSame(S.True, solve.assumptions());
    assertSame(S.True, nsolve.generateConditions());
    assertEquals(F.C0, nsolve.modulus());
  }

  /** FindInstance declares one option, so every other option has to fall back to its default. */
  @Test
  public void shorterValueArrayFallsBackToDefaults() {
    SolveOptions options =
        SolveOptions.of(SolveOptions.FIND_INSTANCE_KEYS, new IExpr[] {S.False});
    assertSame(S.False, options.generateConditions());
    assertEquals(F.C1000, options.maxRoots());
    assertEquals(F.C0, options.modulus());
    assertSame(S.Automatic, options.workingPrecision());
  }

  /** Every declared key list must line up with its default list. */
  @Test
  public void keysAndDefaultsHaveTheSameLength() {
    assertEquals(SolveOptions.SOLVE_KEYS.length, SolveOptions.SOLVE_DEFAULTS.length);
    assertEquals(SolveOptions.NSOLVE_KEYS.length, SolveOptions.NSOLVE_DEFAULTS.length);
    assertEquals(SolveOptions.SOLVE_ALWAYS_KEYS.length, SolveOptions.SOLVE_ALWAYS_DEFAULTS.length);
    assertEquals(SolveOptions.FIND_INSTANCE_KEYS.length,
        SolveOptions.FIND_INSTANCE_DEFAULTS.length);
    for (IBuiltInSymbol[] keys : new IBuiltInSymbol[][] {SolveOptions.SOLVE_KEYS,
        SolveOptions.NSOLVE_KEYS, SolveOptions.SOLVE_ALWAYS_KEYS,
        SolveOptions.FIND_INSTANCE_KEYS}) {
      for (IBuiltInSymbol key : keys) {
        assertTrue(key.isSymbol(), key + " is no option symbol");
      }
    }
  }

  /** The defaults a function declares are the ones its evaluation actually uses. */
  @Test
  public void declaredDefaultsDriveEvaluation() {
    // Solve: Modulus -> 0 means "no modulus"
    check("Solve(x^2 == 1, x)", //
        "{{x->-1},{x->1}}");
    check("Solve(x^2 == 1, x, Modulus -> 8)", //
        "{{x->1},{x->3},{x->5},{x->7}}");
    // NSolve: WorkingPrecision -> Automatic means machine precision
    check("NSolve(x^2 == 2, x)", //
        "{{x->-1.41421},{x->1.41421}}");
    check("NSolve(x^2 == 2, x, WorkingPrecision -> 30)", //
        "{{x->-1.4142135623730950488016887242},{x->1.4142135623730950488016887242}}");
    // MaxRoots applies to Solve and SolveValues alike
    check("Solve(x^5 == 1, x, MaxRoots -> 2)", //
        "{{x->1},{x->-(-1)^(1/5)}}");
    check("SolveValues(x^5 == 1, x, MaxRoots -> 2)", //
        "{1,-(-1)^(1/5)}");
    // an option may follow the domain argument
    check("Solve(x^5 == 1, x, Reals, MaxRoots -> 2)", //
        "{{x->1}}");
  }

  /**
   * {@link S#MaxExtraConditions} decides how many equations on the parameters of a system a
   * solution may require. The default <code>0</code> returns only the generic solutions.
   */
  @Test
  public void maxExtraConditionsAddsTheDegenerateCases() {
    // generically the equation is quadratic, so only its two roots are generic solutions
    check("Solve(a*x^2 + b*x + c == 0, x)", //
        "{{x->-b/(2*a)-Sqrt(b^2-4*a*c)/(2*a)},{x->-b/(2*a)+Sqrt(b^2-4*a*c)/(2*a)}}");
    // allowing one equation on a parameter adds the case in which it is linear
    check("Solve(a*x^2 + b*x + c == 0, x, MaxExtraConditions -> 1)", //
        "{{x->-b/(2*a)-Sqrt(b^2-4*a*c)/(2*a)},{x->-b/(2*a)+Sqrt(b^2-4*a*c)/(2*a)},{x->ConditionalExpression(-c/b,a==\n"
            + "0&&b!=0)}}");
    check("Solve(a*x^2 + b*x + c == 0, x, MaxExtraConditions -> All)", //
        "{{x->-b/(2*a)-Sqrt(b^2-4*a*c)/(2*a)},{x->-b/(2*a)+Sqrt(b^2-4*a*c)/(2*a)},{x->ConditionalExpression(-c/b,a==\n"
            + "0&&b!=0)}}");
    check("SolveValues(a*x^2 + b*x + c == 0, x, MaxExtraConditions -> 1)", //
        "{-b/(2*a)-Sqrt(b^2-4*a*c)/(2*a),-b/(2*a)+Sqrt(b^2-4*a*c)/(2*a),ConditionalExpression(-c/b,a==\n"
            + "0&&b!=0)}");
    // a system without parameters has no degenerate case to add
    check("Solve(x^2 == 4, x, MaxExtraConditions -> 2)", //
        "{{x->-2},{x->2}}");
    // TODO a branch in which the variable is unconstrained - `a*x==b` with `a==0&&b==0` - has no
    // value to report and is skipped
    check("Solve(a*x == b, x, MaxExtraConditions -> All)", //
        "{{x->b/a}}");
  }

  /**
   * {@link S#InverseFunctions} decides whether a symbolic inverse function may be applied.
   * {@link S#Automatic} applies it and warns that a multivalued inverse may lose branches,
   * {@link S#True} applies it silently, {@link S#False} never applies it.
   */
  @Test
  public void inverseFunctionsGatesTheSymbolicInverses() {
    // an equation which needs a lossy inverse is only solved when one may be applied
    check("Solve(Sqrt(x^2) == a, x)", //
        "{{x->Sqrt(a^2)},{x->-Sqrt(a^2)}}");
    check("Solve(Sqrt(x^2) == a, x, InverseFunctions -> True)", //
        "{{x->Sqrt(a^2)},{x->-Sqrt(a^2)}}");
    check("Solve(Sqrt(x^2) == a, x, InverseFunctions -> False)", //
        "Solve(Sqrt(x^2)==a,x,InverseFunctions->False)");
    // a purely algebraic equation needs no inverse function at all
    check("Solve(x^2 + 1 == 0, x, InverseFunctions -> False)", //
        "{{x->-I},{x->I}}");
    check("Solve(x^2 == 4, x, InverseFunctions -> False)", //
        "{{x->-2},{x->2}}");
    // the periodic expansion gives a complete solution set rather than one branch of an inverse,
    // so it isn't gated
    check("Solve(Sin(x) == 0, x, InverseFunctions -> False)", //
        "{{x->ConditionalExpression(2*Pi*C(1),C(1)∈Integers)},{x->ConditionalExpression(Pi+\n"
            + "2*Pi*C(1),C(1)∈Integers)}}");
    check("Solve(Log(x) == 2, x, InverseFunctions -> False)", //
        "{{x->E^2}}");
  }

  /**
   * {@link S#Assumptions} constrains the parameters of the system. Over the reals
   * <code>x^2 == a</code> is unsolvable unless <code>a</code> is known to be non negative.
   */
  @Test
  public void assumptionsConstrainTheParameters() {
    check("Solve(x^2 == a, x, Reals)", //
        "{}");
    check("Solve(x^2 == a, x, Reals, Assumptions -> a > 0)", //
        "{{x->-Sqrt(a)},{x->Sqrt(a)}}");
    // the option adds to the assumptions the engine already carries
    check("Assuming(a > 0, Solve(x^2 == a, x))", //
        "{{x->-Sqrt(a)},{x->Sqrt(a)}}");
    // TODO an explicit `Reals` domain replaces the surrounding `Assuming(...)` instead of adding to
    // it, so the assumption is lost here - the `Assumptions` option is the way to combine both
    check("Assuming(a > 0, Solve(x^2 == a, x, Reals))", //
        "{}");
    // Automatic and True are the "no extra assumption" values
    check("Solve(x^2 == a, x, Reals, Assumptions -> Automatic)", //
        "{}");
    check("Solve(x^2 == a, x, Reals, Assumptions -> True)", //
        "{}");
    // it combines with the other options rather than displacing them
    check("Solve(x^5 == 1, x, MaxRoots -> 2, Assumptions -> a > 0)", //
        "{{x->1},{x->-(-1)^(1/5)}}");
    check("SolveValues(x^2 == a, x, Reals, Assumptions -> a > 0)", //
        "{-Sqrt(a),Sqrt(a)}");
  }

  @Test
  public void reduceKeysBindByName() {
    // {Backsubstitution, Cubics, GeneratedParameters, Method, Modulus, Quartics,
    // WorkingPrecision}
    SolveOptions options = SolveOptions.of(SolveOptions.REDUCE_KEYS,
        new IExpr[] {S.True, S.True, F.$s("K"), S.Automatic, F.ZZ(8), S.False, F.ZZ(30)});
    assertTrue(options.isBacksubstitution());
    assertSame(S.True, options.cubics());
    assertSame(F.$s("K"), options.generatedParameters());
    assertSame(S.Automatic, options.method());
    assertEquals(F.ZZ(8), options.modulus());
    assertSame(S.False, options.quartics());
    assertEquals(F.ZZ(30), options.workingPrecision());
    // Reduce declares neither MaxRoots nor GenerateConditions
    assertEquals(F.C1000, options.maxRoots());
    assertSame(S.True, options.generateConditions());
  }

  /**
   * {@link S#Backsubstitution} decides whether a solved variable is given explicitly even where
   * that pulls an inert {@link S#Root} object into it. A {@code Root} object has no closed form to
   * collapse into, so substituting only duplicates it.
   */
  @Test
  public void backsubstitutionKeepsTheDependentFormOfRootObjects() {
    check("Reduce({x == y + 1, y^5 - y - 1 == 0}, {x, y})", //
        "(x==1+y&&y==Root(-1-#1+#1^5&,1,0))||(x==1+y&&y==Root(-1-#1+#1^5&,2,0))||(x==1+y&&y==Root(-\n"
            + "1-#1+#1^5&,3,0))||(x==1+y&&y==Root(-1-#1+#1^5&,4,0))||(x==1+y&&y==Root(-1-#1+#1^\n"
            + "5&,5,0))");
    check("Reduce({x == y + 1, y^5 - y - 1 == 0}, {x, y}, Backsubstitution -> True)", //
        "(x==1+Root(-1-#1+#1^5&,1,0)&&y==Root(-1-#1+#1^5&,1,0))||(x==1+Root(-1-#1+#1^5&,2,\n"
            + "0)&&y==Root(-1-#1+#1^5&,2,0))||(x==1+Root(-1-#1+#1^5&,3,0)&&y==Root(-1-#1+#1^5&,\n"
            + "3,0))||(x==1+Root(-1-#1+#1^5&,4,0)&&y==Root(-1-#1+#1^5&,4,0))||(x==1+Root(-1-#1+#1^\n"
            + "5&,5,0)&&y==Root(-1-#1+#1^5&,5,0))");
    // a value which collapses to a closed form is always given explicitly
    check("Reduce({x + y == 1, x - y == 3}, {x, y})", //
        "x==2&&y==-1");
    check("Reduce({x*y == 1, y^3 == 2}, {x, y})", //
        "(x==1/2^(1/3)&&y==2^(1/3))||(x==(-1)^(2/3)/2^(1/3)&&y==-(-2)^(1/3))||(x==-(-1)^(\n"
            + "1/3)/2^(1/3)&&y==(-1)^(2/3)*2^(1/3))");
    check("Reduce({x, y} == {1, 2}, {x, y})", //
        "x==1&&y==2");
  }

  /**
   * A function which doesn't declare {@link S#Cubics} / {@link S#Quartics} keeps the radical
   * solvers - only {@link S#Reduce} opts out of them.
   */
  @Test
  public void undeclaredCubicsKeepTheRadicalSolvers() {
    SolveOptions solve = SolveOptions.of(SolveOptions.SOLVE_KEYS, SolveOptions.SOLVE_DEFAULTS);
    assertSame(S.True, solve.cubics());
    assertSame(S.True, solve.quartics());
    SolveOptions reduce = SolveOptions.of(SolveOptions.REDUCE_KEYS, SolveOptions.REDUCE_DEFAULTS);
    assertSame(S.False, reduce.cubics());
    assertSame(S.False, reduce.quartics());
  }

  /**
   * {@link S#Cubics} and {@link S#Quartics} decide whether a general cubic or quartic is given by
   * the explicit radicals of Cardano and Ferrari or by inert {@link S#Root} objects. The "very
   * simple forms" - the binomials and the reducible polynomials - keep their radicals either way.
   */
  @Test
  public void cubicsAndQuarticsChooseTheRootForm() {
    check("Reduce(x^3 + 2*x^2 + 3*x + 4 == 0, x)", //
        "x==Root(4+3*#1+2*#1^2+#1^3&,1,0)||x==Root(4+3*#1+2*#1^2+#1^3&,2,0)||x==Root(4+3*#1+\n"
            + "2*#1^2+#1^3&,3,0)");
    check("Reduce(x^3 - 3*x + 1 == 0, x)", //
        "x==Root(1-3*#1+#1^3&,1,0)||x==Root(1-3*#1+#1^3&,2,0)||x==Root(1-3*#1+#1^3&,3,0)");
    check("Reduce(x^4 + 2*x^3 + 3*x^2 + 4*x + 5 == 0, x)", //
        "x==Root(5+4*#1+3*#1^2+2*#1^3+#1^4&,1,0)||x==Root(5+4*#1+3*#1^2+2*#1^3+#1^4&,2,0)||x==Root(\n"
            + "5+4*#1+3*#1^2+2*#1^3+#1^4&,3,0)||x==Root(5+4*#1+3*#1^2+2*#1^3+#1^4&,4,0)");
    // a binomial is a "very simple form" and keeps its radicals
    check("Reduce(x^3 - 5 == 0, x)", //
        "x==-(-5)^(1/3)||x==5^(1/3)||x==(-1)^(2/3)*5^(1/3)");
    check("Reduce(x^4 - 5 == 0, x)", //
        "x==-5^(1/4)||x==-I*5^(1/4)||x==I*5^(1/4)||x==5^(1/4)");
    // a reducible polynomial is solved factor by factor
    check("Reduce(x^6 - 1 == 0, x)", //
        "x==-1||x==1||x==-(-1)^(1/3)||x==(-1)^(1/3)||x==-(-1)^(2/3)||x==(-1)^(2/3)");
    // quadratics are unaffected
    check("Reduce(x^2 == 4, x)", //
        "x==-2||x==2");
    // the radicals are still available on request
    check("Reduce(x^3 - 3*x + 1 == 0, x, Cubics -> True)", //
        "x==(-27/2+I*27/2*Sqrt(3))^(1/3)/3+(3*2^(1/3))/(-27+I*27*Sqrt(3))^(1/3)||x==(-3*(\n"
            + "1-I*Sqrt(3)))/(2^(2/3)*(-27+I*27*Sqrt(3))^(1/3))+((-1-I*Sqrt(3))*(-27+I*27*Sqrt(\n"
            + "3))^(1/3))/(6*2^(1/3))||x==(-3*(1+I*Sqrt(3)))/(2^(2/3)*(-27+I*27*Sqrt(3))^(1/3))+((-\n"
            + "1+I*Sqrt(3))*(-27+I*27*Sqrt(3))^(1/3))/(6*2^(1/3))");
    // Solve keeps the radicals: it doesn't declare the options
    check("Solve(x^3 - 3*x + 1 == 0, x) // Length", //
        "3");
    check("FreeQ(Solve(x^3 - 3*x + 1 == 0, x), Root)", //
        "True");
    // the inert roots flow through the system solver and the sign analysis alike
    check("Reduce({x^3 + 2*x^2 + 3*x + 4 == 0, y == 2}, {x, y})", //
        "(x==Root(4+3*#1+2*#1^2+#1^3&,1,0)&&y==2)||(x==Root(4+3*#1+2*#1^2+#1^3&,2,0)&&y==\n"
            + "2)||(x==Root(4+3*#1+2*#1^2+#1^3&,3,0)&&y==2)");
    check("Reduce(x^3 - 3*x + 1 > 0, x)", //
        "(x>Root(1-3*#1+#1^3&,1,0)&&x<Root(1-3*#1+#1^3&,2,0))||x>Root(1-3*#1+#1^3&,3,0)");
  }

  /** The four options which {@link S#Reduce} declares all change its result. */
  @Test
  public void reduceOptionsChangeTheResult() {
    // Modulus reduces in the residue class ring instead of the requested domain
    check("Reduce(x^2 == 1, x, Modulus -> 8)", //
        "x==1||x==3||x==5||x==7");
    check("Reduce(3*x == 1, x, Modulus -> 7)", //
        "x==5");
    check("Reduce({x + y == 1, x - y == 1}, {x, y}, Modulus -> 5)", //
        "x==1&&y==0");
    check("Reduce(x^2 == 2, x, Modulus -> 3)", //
        "False");
    // Modulus -> 0 is "no modulus"
    check("Reduce(x^2 == 1, x, Modulus -> 0)", //
        "x==-1||x==1");
    // GeneratedParameters names the parameters which a solution generates
    check("Reduce(2*x + 3*y == 1, {x, y}, Integers, GeneratedParameters -> K)", //
        "K(1)∈Integers&&x==-1+3*K(1)&&y==1-2*K(1)");
    // WorkingPrecision applies the requested precision to the exact result
    check("Reduce(x^2 == 2, x, WorkingPrecision -> 30)", //
        "x==-1.4142135623730950488016887242||x==1.4142135623730950488016887242");
    // Method -> Automatic is the only implemented method
    check("Reduce(x^2 == 4, x, Method -> Automatic)", //
        "x==-2||x==2");
  }

  /** {@link S#Resolve} accepts its options. */
  @Test
  public void resolveAcceptsItsOptions() {
    check("Resolve(Exists(x, x^2 == 4), Method -> Automatic)", //
        "True");
    check("Resolve(Exists(x, x^2 == 4), WorkingPrecision -> 30)", //
        "True");
    check("Resolve(ForAll(x, x^2 + 1 > 0), Reals, Method -> Automatic)", //
        "True");
  }

  /**
   * An invalid option value leaves the call unevaluated. The reported message names the calling
   * function and the option, i.e. "Solve: The value 0 of the MaxRoots options is not a positive
   * integer, Infinity or Automatic" - it used to name {@code NSolve} whichever function was called
   * and to leave the option slot of the message template empty.
   */
  @Test
  public void invalidMaxRootsLeavesTheCallUnevaluated() {
    check("Solve(x^5 == 1, x, MaxRoots -> 0)", //
        "Solve(x^5==1,x,MaxRoots->0)");
    check("Solve(x^5 == 1, x, MaxRoots -> banana)", //
        "Solve(x^5==1,x,MaxRoots->banana)");
    check("SolveValues(x^5 == 1, x, MaxRoots -> 0)", //
        "SolveValues(x^5==1,x,MaxRoots->0)");
  }
}
