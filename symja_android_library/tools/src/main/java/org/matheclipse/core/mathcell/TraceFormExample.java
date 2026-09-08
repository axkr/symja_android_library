package org.matheclipse.core.mathcell;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;

/**
 * Print the derivation of an evaluation as the TeX array which
 * {@link org.matheclipse.core.expression.S#TraceForm} produces.
 *
 * <p>
 * The web front ends lay the same steps out as a tree of collapsible sections; this prints the
 * one-formula form, which is what a notebook saved as <code>*.ipynb</code> keeps.
 */
public class TraceFormExample {

  public static void main(String[] args) {
    F.initSymbols();
    ExprEvaluator util = new ExprEvaluator();
    for (String input : new String[] { //
        "TeXForm(TraceForm(D(Sin(x^2)*x, x)))", //
        "TeXForm(TraceForm(Integrate(Sin(x)^3, x), 2))", //
        "TraceForm(QuarticSolve(1, -4, -3), Infinity, \"Arithmetic\")"}) {
      System.out.println("\n" + input);
      System.out.println(util.eval(input).toString());
    }
  }
}
