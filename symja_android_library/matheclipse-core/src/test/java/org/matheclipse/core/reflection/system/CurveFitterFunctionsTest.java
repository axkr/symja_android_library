package org.matheclipse.core.reflection.system;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class CurveFitterFunctionsTest extends ExprEvaluatorTestCase {

  @Test
  public void testFit() {
    check("Fit({{-Pi,4}, {-Pi/2,0}, {0,1}, {Pi/2,-1}, {Pi,-4}}, {Sin(x/2), Sin(x), Sin(2*x)}, x)", //
        "-4.0*Sin(x/2)+2.32843*Sin(x)+Sin(2*x)");
    // TODO needs improvement
    check(
        "Fit({{-Pi,4}, {-Pi/2,0}, {0,1}, {Pi/2,-1}, {Pi,-4}}, {Sin(x/2), Sin(x), Sin(2*x), Sin(3*x)}, x)//Chop", //
        "83.5143*Sin(x/2)-186.4314*Sin(x)+3.57304*10^17*Sin(2*x)-83.12067*Sin(3*x)");

    check("Fit({1.2214,1.49182,1.82212,2.22554,2.71828,3.32012,4.0552},{2},x)", //
        "2.40778");
    check("Fit({1.2214,1.49182,1.82212,2.22554,2.71828,3.32012,4.0552},{2,x},x)", //
        "0.542903+0.46622*x");
    check("Fit({1.2214,1.49182,1.82212,2.22554,2.71828,3.32012,4.0552},{1,x,x^2},x)", //
        "1.09428+0.0986352*x+0.0459481*x^2");

    check("Fit({2,3,5,7,11,13},3,x)", //
        "6.83333");
    check("Fit({{1,1},{2,4},{3,9},{4,16}},2,x)", //
        "7.5");
    check("Fit({1,4,9,16},2,x)", //
        "7.5");
    check("Fit({1,4,9,16},{x^2},x)", //
        "x^2");

    check("Fit({x,-3,-1/2},2147483647,ComplexInfinity)", //
        "Fit({x,-3,-1/2},2147483647,ComplexInfinity)");
    check("Fit({1->0},1,x)", //
        "Fit({1->0},1,x)");
    check("Fit({{0, 1}, {1, 0}, {3, 2}, {5, 4}}, {1,x}, x)", //
        "0.186441+0.694915*x");
    check("Fit({{0, 1}, {1, 0}, {3, 2}, {5, 4}}, 1, x)", //
        "1.75");
    check("Fit({{0, 1}, {1, 0}, {3, 2}, {5, 4}}, {1,x,x^2}, x)", //
        "0.678392-0.266332*x+0.190955*x^2");
    check("Fit({{0, 1}, {1, 0}, {3, 2}, {5, 4}}, {1,x,x^2,x^4,x^5}, x)", //
        "1.0-1.96768*x+x^2-0.035533*x^4+0.00321489*x^5");

    check("Fit({{1,1},{2,4},{3,9},{4,16}},{1,x,x^2},x) // Chop", //
        "x^2");

  }

  @Test
  public void testFitDesignMatrix() {
    // Fit({m, v}) signature: design matrix m and response vector v.
    // Returns the best fit (coefficient) vector a minimizing Norm(m.a - v) -> {-6, 6.5}
    check("Fit({{{1, 2}, {3, 4}, {5, 6}}, {7, 8, 9}})", //
        "{-6.0,6.5}");
  }

  @Test
  public void testFitWorkingPrecisionExact() {
    // WorkingPrecision->Infinity solves the linear least squares problem exactly
    check("Fit({{1,1},{2,4},{3,9},{4,16}}, {1,x,x^2}, x, WorkingPrecision->Infinity) // Chop", //
        "x^2");
  }

  @Test
  public void testFitWorkingPrecisionApfloat() {
    // WorkingPrecision->n uses n-digit Apfloat arithmetic
    check("Fit({{1,1},{2,4},{3,9},{4,16}}, {1,x,x^2}, x, WorkingPrecision->30)", //
        "0*x+x^2");
  }

  @Test
  public void testFindFit() {
    check("FindFit({1.2214,1.49182,1.82212,2.22554,2.71828,3.32012,4.0552},a+b*x+c*x^2,{a,b,c},x)", //
        "{a->1.09428,b->0.0986352,c->0.0459481}");

    // https://stackoverflow.com/a/51696587/24819
    check(
        "FindFit({ {1.3,0.5}, {2.8,0.9}, {5.0,2.6}, {10.2,7.1}, {16.5,12.3}, {21.3,15.3},{ 31.8,20.4}, {52.2,24.4}}, " //
            + "d+((a-d)/ (1+(x/c)^ b)),  {{a, 1}, {b,2}, {c,20}, {d,20}}, x)", //
        "{a->0.174321,b->1.75938,c->19.69032,d->28.83068}");
    // initial guess [1.0, 1.0, 1.0] gives bad result:
    check("FindFit(Table({t, 3*Sin(3*t + 1)}, {t, -3, 3, 0.1}), a* Sin(w*t + f), {a,w,f}, t)", //
        "{a->0.599211,w->1.51494,f->3.80421}");
    // initial guess [2.0, 1.0, 1.0] gives better result:
    check(
        "FindFit(Table({t, 3*Sin(3*t + 1)}, {t, -3, 3, 0.1}), a* Sin(w*t + f), {{a, 2}, {w,1}, {f,1}}, t)", //
        "{a->3.0,w->3.0,f->1.0}");
    // initial guess [2.0, 1.0, 1.0] with 1.0 by default:
    check(
        "FindFit(Table({t, 3*Sin(3*t + 1)}, {t, -3, 3, 0.1}), a* Sin(w*t + f), {{a, 2}, w, f}, t)", //
        "{a->3.0,w->3.0,f->1.0}");

    check("FindFit({{1,1},{2,4},{3,9},{4,16}}, " //
        + "a+b*x+c*x^2, {a, b, c}, x)", //
        "{a->-6.10623*10^-16,b->7.21645*10^-16,c->1.0}");
    check("FindFit({{15.2,8.9},{31.1,9.9},{38.6,10.3},{52.2,10.7},{75.4,11.4}}, " //
        + "a*Log(b*x), {a, b}, x)", //
        "{a->1.54503,b->20.28258}");
    check("FindFit(Table(Prime(x), {x, 20}), a*x*Log(b + c*x), {a, b, c}, x)", //
        "{a->1.42076,b->1.65558,c->0.534644}");
    check("FindFit({{1.0, 12.}, {1.9, 10.}, {2.6, 8.2}, {3.4, 6.9}, {5.0, 5.9}}, " //
        + "a*Exp(-k*t), {a, k}, t)", //
        "{a->14.38886,k->0.198208}");

    // initial guess [0, 0, 0] doesn't work
    check("FindFit(Table(Prime(x), {x, 20}), a*x*Log(b + c*x), {{a,0},{b,0},{c,0}}, x)", //
        "FindFit({2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71},a*x*Log(b+c*x),{{a,\n"
            + "0},{b,0},{c,0}},x)");
  }

  @Test
  public void testFindFitSE89247() {
    // Gauss Fit to List of 2D Points
    // https://mathematica.stackexchange.com/a/89247/21734
    check("data = {{0.05, 15}, {0.15, 51}, {0.25, 64}, {0.35, 107}, {0.45, 113}, {0.55, 162}, " //
        + "{0.65, 163}, {0.75, 167}, {0.85, 182}, {0.95, 187}, {1.05, 165}, {1.15, 168}, " //
        + "{1.25, 151}, {1.35, 143}, {1.45, 121}, {1.55, 130}, {1.65, 109}, {1.75, 91}, " //
        + "{1.85, 91}, {1.95, 63}, {2.05, 48}, {2.15, 34}, {2.25, 29}, {2.35, 24}, {2.45, 14}, " //
        + "{2.55, 11}, {2.65, 6}, {2.75, 6}, {2.85, 9}, {2.95, 4}, {3.05, 4}, {3.15, 2}, {3.25, 0}, " //
        + "{3.35, 1}, {3.45, 5}, {3.55, 3}, {3.65, 2}, {3.75, 1}, {3.85, 2}, {3.95, 1}, {4.05, 0}, {4.15, 0}};", //
        "");
    check("model(x_) = ampl*Evaluate(PDF(NormalDistribution(x0, sigma), x));", //
        "");
    check("fit = FindFit(data, model(x), {ampl, x0, sigma}, x)", //
        "{ampl->274.7648,x0->1.02404,sigma->0.614853}");

  }
}
