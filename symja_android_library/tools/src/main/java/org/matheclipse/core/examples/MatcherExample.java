package org.matheclipse.core.examples;

import static org.matheclipse.core.expression.F.ArcCos;
import static org.matheclipse.core.expression.F.ArcSin;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CNI;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.Coth;
import static org.matheclipse.core.expression.F.Csch;
import static org.matheclipse.core.expression.F.E;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Pi;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sech;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.Tanh;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.x_;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.Matcher;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

/**
 * Transform trigonometric functions into exponential definitions. See
 *
 * <ul>
 * <li><a href=
 * "http://en.wikipedia.org/wiki/List_of_trigonometric_identities#Exponential_definitions"> List of
 * trigonometric identities - Exponential definitions</a>
 * <li><a href="http://en.wikipedia.org/wiki/Hyperbolic_function">Hyperbolic functions</a>
 * </ul>
 */
public class MatcherExample {

  public static void main(String[] args) {
    final Matcher matcher = new Matcher();

    // I/(2*E^(I*x))-1/2*I*E^(I*x)
    matcher.caseOf(Sin(x_), //
        x -> Subtract(Times(C1D2, CI, Power(E, Times(CNI, x))),
            Times(C1D2, CI, Power(E, Times(CI, x)))));
    // 1/(2*E^(I*x))+E^(I*x)/2
    matcher.caseOf(Cos(x_), //
        x -> Plus(Times(C1D2, Power(E, Times(CNI, x))), Times(C1D2, Power(E, Times(CI, x)))));
    // (I*(E^(-I*x)-E^(I*x)))/(E^(-I*x)+E^(I*x))
    matcher.caseOf(Tan(x_), //
        x -> Times(CI, Subtract(Power(E, Times(CNI, x)), Power(E, Times(CI, x))),
            Power(Plus(Power(E, Times(CNI, x)), Power(E, Times(CI, x))), CN1)));
    // -I*Log(I*x+Sqrt(1-x^2))
    matcher.caseOf(ArcSin(x_), //
        x -> Times(CNI, Log(Plus(Sqrt(Subtract(C1, Sqr(x))), Times(CI, x)))));
    // Pi/2+I*Log(I*x+Sqrt(1-x^2))
    matcher.caseOf(ArcCos(x_), //
        x -> Plus(Times(C1D2, Pi), Times(CI, Log(Plus(Sqrt(Subtract(C1, Sqr(x))), Times(CI, x))))));
    // 1/2*I*Log(1-I*x)-1/2*I*Log(1+I*x)
    matcher.caseOf(ArcTan(x_), //
        x -> Subtract(Times(C1D2, CI, Log(Plus(C1, Times(CNI, x)))),
            Times(C1D2, CI, Log(Plus(C1, Times(CI, x))))));
    // (E^x+E^(-x))/2
    matcher.caseOf(Cosh(x_), //
        x -> Times(C1D2, Plus(Power(E, x), Power(E, Times(CN1, x)))));
    // 2/(E^x-E^(-x))
    matcher.caseOf(Csch(x_), //
        x -> Times(C2, Power(Plus(Power(E, x), Times(CN1, Power(E, Times(CN1, x)))), CN1)));
    // ((E^(-x))+E^x)/((-E^(-x))+E^x)
    matcher.caseOf(Coth(x_), //
        x -> Times(Plus(Power(E, x), Power(E, Times(CN1, x))),
            Power(Plus(Power(E, x), Times(CN1, Power(E, Times(CN1, x)))), CN1)));
    // 2/(E^x+E^(-x))
    matcher.caseOf(Sech(x_), //
        x -> Times(C2, Power(Plus(Power(E, x), Power(E, Times(CN1, x))), CN1)));
    // (E^x-E^(-x))/2
    matcher.caseOf(Sinh(x_), //
        x -> Times(C1D2, Plus(Power(E, x), Times(CN1, Power(E, Times(CN1, x))))));
    // ((-E^(-x))+E^x)/((E^(-x))+E^x)
    matcher.caseOf(Tanh(x_), //
        x -> Times(Plus(Times(CN1, Power(E, Times(CN1, x))), Power(E, x)),
            Power(Plus(Power(E, Times(CN1, x)), Power(E, x)), CN1)));

    try {
      ExprEvaluator util = new ExprEvaluator();
      IExpr input = util.eval("Sin(a)");

      IExpr result = matcher.apply(input);
      if (result.isPresent()) {
        // print: I/(2*E^(I*a))-1/2*I*E^(I*a)
        System.out.println(result.toString());
      }

      input = util.eval("Cos(x)^2+Sinh(x)^3");

      result = matcher.replaceAll(input);
      if (result.isPresent()) {
        // print: I/(2*E^(I*a))-1/2*I*E^(I*a)
        System.out.println(result.toString());
      }

    } catch (SyntaxError e) {
      // catch Symja parser errors here
      System.out.println(e.getMessage());
    } catch (MathException me) {
      // catch Symja math errors here
      System.out.println(me.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
    } catch (final StackOverflowError soe) {
      System.out.println(soe.getMessage());
    } catch (final OutOfMemoryError oome) {
      System.out.println(oome.getMessage());
    }
  }
}
