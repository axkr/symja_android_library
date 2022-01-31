package org.matheclipse.core.eval.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.eval.MathUtils;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.Symbol;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class MagicProcessor {
  private static final Logger LOGGER = LogManager.getLogger();

  String outPut = null;
  String err = null;

  public String magicSolve(String q, final String function) {
    outPut = q;
    LOGGER.debug("Input '{}'", outPut);
    // If user gave single "=" in solve, replace it with "=="
    boolean isSysOfEq = outPut.contains("Solve");
    if (isSysOfEq) {
      String pattern = "([^=])(=)([^=])";
      outPut = outPut.replaceAll(pattern, "$1==$3");
      LOGGER.debug("Input has Solve so after replacing all = with == we have '{}'", outPut);
    }

    String processedQ = preProcessQues();
    String ans = MathUtils.evaluate(processedQ, function);
    if (processedQ.contains("Solve")) {
      ans = ans.replace("->", "=");
      // Just Remove outermost braces for single equation. Issues for
      // multiple eqn
      /*
       * if(processedQ.split(",").length == 2) { if (ans.charAt(0) == '{' && ans.charAt(ans.length()
       * - 1) == '}') ans = ans.substring(1, ans.length() - 1); }
       */
    }

    return ans;
  }

  String preProcessQues() {
    /*
     * For q = Solve({x^2==4,y^2+x==6},{x,y}) ques = AST = Solve[{x^2==4,y^2+x==6},{x,y}] |
     * |----array |0 = Symbol = solve |1 = AST = {x^2==4,y^2+x==6} |2 = AST = {x,y} |3 = null |4 =
     * null
     */

    // Convert expression like "2*x+7=10" to Solve(2*x+7 - 10 == 0, x)
    if (outPut.contains("=")) {
      String processEq = inputQuestionIsPossiblyASystemOfEquation(outPut);
      if (processEq != null) {
        // Args will be handled in wrtArgumentMising
        outPut = ((ISymbol) S.Solve).toString() + "(" + processEq + ")";
      }

      /*
       * String [] list = outPut.split("="); if(list.length == 2) { String eq = list[1] + "- (" +
       * list[0] + ")"; IExpr eqn = MathUtils.parse(eq, null); if(eqn.isAST() && isPolynomial((IAST)
       * eqn)) { String vars = solve_get_arg_if_missing(eqn); isSysOfEq = true; outPut = ((Symbol)
       * F.Solve).toString() + "(" + eq + " == 0" + "," + vars + ")"; } }
       */
    }

    IExpr ques = MathUtils.parse(outPut, null);
    if (ques == null)
      return outPut;
    LOGGER.debug("ques = {}", ques);

    if (wrtArgumentMising(ques, S.Solve)) {
      IExpr equations = getArg1(ques);
      String vars = solve_get_arg_if_missing(equations);
      if (vars != null && err == null) {
        outPut = ((Symbol) S.Solve).toString() + "(" + equations.toString() + "," + vars + ")";
        LOGGER.debug(" Result after eq processing {}", outPut);
      }
    }

    if (wrtArgumentMising(ques, S.D)) {
      IExpr fn = getArg1(ques);
      // Extract variables from equations
      org.matheclipse.core.convert.VariablesSet eVar =
          new org.matheclipse.core.convert.VariablesSet(fn);
      String var = null;
      if (eVar.isSize(1))
        var = getVarString(eVar, false);
      else
        var = getVarString(eVar, true);
      outPut = ((Symbol) S.D).toString() + "(" + fn.toString() + "," + var + ")";
    }
    if (wrtArgumentMising(ques, S.Integrate)) {
      IExpr fn = getArg1(ques);
      // Extract variables from equations
      org.matheclipse.core.convert.VariablesSet eVar =
          new org.matheclipse.core.convert.VariablesSet(fn);
      String var = null;
      if (eVar.isSize(1))
        var = getVarString(eVar, false);
      else
        var = getVarString(eVar, true);
      outPut = ((Symbol) S.Integrate).toString() + "(" + fn.toString() + "," + var + ")";
    }

    LOGGER.debug("Processed q = {}", outPut);

    return (err == null) ? outPut : err;
  }

  private IExpr getArg1(IExpr ques) {
    if (ques.isAST()) {
      return ques.first();
    }
    return null;
  }

  private int getSize(IExpr ques) {
    if (ques.isAST())
      return ((IAST) ques).size();
    return 0;
  }

  public String solve_get_arg_if_missing(IExpr equations) {
    // Extract variables from equations
    org.matheclipse.core.convert.VariablesSet eVar =
        new org.matheclipse.core.convert.VariablesSet(equations);
    if (equations.isAST()) {
      // equation is list for more than 1 equations
      int num_equations = equations.isList() ? getSize(equations) - 1 : 1;
      // If equations is AST and num_equations = num variables
      if (equations.isAST() && eVar.isSize(num_equations)) {
        String vars = getVarString(eVar, false);
        LOGGER.debug("\t list of var = {}", vars);
        return vars;
      } else {
        // Number of equations and variables are different
        if (num_equations == 1) {
          String vars = getVarString(eVar, true);
          if (vars != null)
            return vars;
          else
            err = "Please specify variable also i.e Solve( f(x), x)";
        } else
          err = "Please specify variable also i.e Solve( equatons, {x, y, ...} )";
      }
    }
    return null;
  }

  // w.r.t arg missing for D(f(x)) , Solve({eq1, eq2}), Integrate(f(x))
  private boolean wrtArgumentMising(IExpr expr, ISymbol fun) {

    ISymbol s = isSymbol(expr.head());
    if (s != null && s == fun) {
      LOGGER.debug("{} is instanceof {}", expr, fun);
      if (expr.isAST()) {
        IAST ast = (IAST) expr;
        if (ast.isAST1()) {
          return true;
        }
      }
    }

    return false;
  }

  public String getVarString(org.matheclipse.core.convert.VariablesSet eVar, boolean onlyXorY) {
    if (onlyXorY) {
      if (eVar.contains(S.x))
        return S.x.toString();
      else if (eVar.contains(S.y))
        return S.y.toString();
      else
        return null;
    }
    IAST varList = eVar.getVarList();
    // [list, x, null, null, null, null]
    return (varList.isAST1()) ? varList.arg1().toString() : varList.toString();
  }

  boolean isEquation(IExpr exp) {
    if (exp.isAST() && isPolynomial((IAST) exp)) {
    }

    return false;
  }

  public static ISymbol isSymbol(IExpr exp) {
    ISymbol s = null;
    if (exp.isSymbol()) {
      s = (ISymbol) exp;
    }
    return s;
  }

  public static boolean isPolynomial(IAST exp) {
    org.matheclipse.core.convert.VariablesSet eVar =
        new org.matheclipse.core.convert.VariablesSet(exp);
    return exp.isPolynomial(eVar.getVarList());
  }

  /**
   * For input <code>F() = G()</code> return <code>F() - G() == 0</code> if its a equation of return
   * null (i.e for <code>x+y=7</code> return <code>x+y - 7 == 0</code> )
   *
   * @param possibleEq
   * @return
   */
  public String getFixedEquation(String possibleEq) {
    String[] list = possibleEq.split("=");
    if (list.length == 2) {
      String eq = list[1] + "- (" + list[0] + ")";
      IExpr eqn = MathUtils.parse(eq, null);
      if (eqn.isAST() && isPolynomial((IAST) eqn)) {
        return list[0] + " == " + list[1];
      }
    }
    return null;
  }

  String inputQuestionIsPossiblyASystemOfEquation(String s) {
    int numQuals = s.split("=").length - 1;
    int numComma = s.split(",").length - 1;
    if ((numQuals > 0) && (numComma + 1 != numQuals))
      return null;

    String[] listEqn = s.split(",");
    String processedEqn = "";

    for (int i = 0; i < numQuals; i++) {
      String fixedEq = getFixedEquation(listEqn[i]);
      if (fixedEq == null)
        return null;
      processedEqn += fixedEq;
      if (i != numQuals - 1)
        processedEqn += ",";
    }

    if (numQuals >= 1)
      processedEqn = "{" + processedEqn + "}";

    return processedEqn;
  }
}
