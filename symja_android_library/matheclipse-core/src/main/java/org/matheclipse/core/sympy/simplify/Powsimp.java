package org.matheclipse.core.sympy.simplify;

import java.util.function.Function;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Powsimp {
  public static IExpr powsimp(IExpr expr) {
    return powsimp(expr, false, "all", false, null);
  }

  public static IExpr powsimp(IExpr expr, boolean deep, String combine) {
    return powsimp(expr, deep, combine, false, null);
  }

  public static IExpr powsimp(IExpr expr, boolean deep, String combine, boolean force,
      Function<IExpr, IExpr> measure) {
    // reduces expression by combining powers with similar bases and exponents.
    //
    // Explanation
    // ===========
    //
    // If ``deep`` is ``True`` then powsimp() will also simplify arguments of
    // functions. By default ``deep`` is set to ``False``.
    //
    // If ``force`` is ``True`` then bases will be combined without checking for
    // assumptions, e.g. sqrt(x)*sqrt(y) -> sqrt(x*y) which is not true
    // if x and y are both negative.
    //
    // You can make powsimp() only combine bases or only combine exponents by
    // changing combine='base' or combine='exp'. By default, combine='all',
    // which does both. combine='base' will only combine::
    //
    // a a a 2x x
    // x * y => (x*y) as well as things like 2 => 4
    //
    // and combine='exp' will only combine
    // ::
    //
    // a b (a + b)
    // x * x => x
    //
    // combine='exp' will strictly only combine exponents in the way that used
    // to be automatic. Also use deep=True if you need the old behavior.
    //
    // When combine='all', 'exp' is evaluated first. Consider the first
    // example below for when there could be an ambiguity relating to this.
    // This is done so things like the second example can be completely
    // combined. If you want 'base' combined first, do something like
    // powsimp(powsimp(expr, combine='base'), combine='exp').
    //
    // Examples
    // ========
    //
    // >>> from sympy import powsimp, exp, log, symbols
    // >>> from sympy.abc import x, y, z, n
    // >>> powsimp(x**y*x**z*y**z, combine='all')
    // x**(y + z)*y**z
    // >>> powsimp(x**y*x**z*y**z, combine='exp')
    // x**(y + z)*y**z
    // >>> powsimp(x**y*x**z*y**z, combine='base', force=True)
    // x**y*(x*y)**z
    //
    // >>> powsimp(x**z*x**y*n**z*n**y, combine='all', force=True)
    // (n*x)**(y + z)
    // >>> powsimp(x**z*x**y*n**z*n**y, combine='exp')
    // n**(y + z)*x**(y + z)
    // >>> powsimp(x**z*x**y*n**z*n**y, combine='base', force=True)
    // (n*x)**y*(n*x)**z
    //
    // >>> x, y = symbols('x y', positive=True)
    // >>> powsimp(log(exp(x)*exp(y)))
    // log(exp(x)*exp(y))
    // >>> powsimp(log(exp(x)*exp(y)), deep=True)
    // x + y
    //
    // Radicals with Mul bases will be combined if combine='exp'
    //
    // >>> from sympy import sqrt
    // >>> x, y = symbols('x y')
    //
    // Two radicals are automatically joined through Mul:
    //
    // >>> a=sqrt(x*sqrt(y))
    // >>> a*a**3 == a**4
    // True
    //
    // But if an integer power of that radical has been
    // autoexpanded then Mul does not join the resulting factors:
    //
    // >>> a**4 # auto expands to a Mul, no longer a Pow
    // x**2*y
    // >>> _*a # so Mul doesn't combine them
    // x**2*y*sqrt(x*sqrt(y))
    // >>> powsimp(_) # but powsimp will
    // (x*sqrt(y))**(5/2)
    // >>> powsimp(x*y*a) # but won't when doing so would violate assumptions
    // x*y*sqrt(x*sqrt(y))
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      if (deep == false && !expr.isPlusTimesPower()) {
        return expr;
      }
      // IASTAppendable result = F.mapFunction(ast.head(), ast, a -> powsimp(a));
      return F.eval(expr);
    }
    return expr;
  }
}
