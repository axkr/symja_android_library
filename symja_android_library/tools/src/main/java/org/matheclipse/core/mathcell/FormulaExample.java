package org.matheclipse.core.mathcell;

public class FormulaExample extends BasePlotExample {

  @Override
  public String exampleFunction() {
    // return "Manipulate(-0.07 *x^5-0.42*x^4+0.94*x^3-4.25*x^2+86.5, {x,4.873,4.874})";
    return "Manipulate(Factor(x^n + 1), {n, 1, 5, 1})";
  }

  public static void main(String[] args) {
    FormulaExample p = new FormulaExample();
    p.generateHTML();
  }
}
