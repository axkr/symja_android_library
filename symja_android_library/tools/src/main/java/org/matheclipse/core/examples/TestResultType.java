package org.matheclipse.core.examples;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

public class TestResultType {

  public static void main(String[] args) {
    ExprEvaluator util = new ExprEvaluator();
    ISymbol f1Variable = util.defineVariable("F1", 10.0);
    ISymbol f2Variable = util.defineVariable("F2", 20.0);
    ISymbol f3Variable = util.defineVariable("F3");
    
    IExpr exp = util.eval("If(ValueQ(F3), F1, F2)");
    System.out.println(exp);
    if (f1Variable.hasAssignedSymbolValue()) {
      System.out.println("f1 value: " + f1Variable.assignedValue());
    } else {
      System.out.println("f1 has no value assigned: " + f1Variable.toString());
    }
    if (f2Variable.hasAssignedSymbolValue()) {
      System.out.println("f2 value: " + f2Variable.assignedValue());
    } else {
      System.out.println("f2 has no value assigned: " + f2Variable.toString());
    }
    if (f3Variable.hasAssignedSymbolValue()) {
      System.out.println("f3 value: " + f3Variable.assignedValue());
    } else {
      System.out.println("f3 has no value assigned: " + f3Variable.toString());
    }
    printResult(util, "F1");
    printResult(util, "F2");
    printResult(util, "F3");
    printResult(util, "Sin(F2)");
    printResult(util, "D(Sin(F3),F3)");
  }

  private static void printResult(ExprEvaluator util, String inputExpression) {
    IExpr result = util.eval(inputExpression);
    if (result.isSymbol()) {
      ISymbol sym = (ISymbol) result;
      System.out.println("Symbol result: " + sym.toString());
    } else if (result.isReal()) {
      IReal sn = (IReal) result;
      System.out.println("Real number result: " + result.toString());
    } else if (result.isAST()) {
      IAST ast = (IAST) result;
      System.out.println("Symbolic function result: " + ast.toString());
      // ... see type hierarchy for IExpr interface
    }
  }
}
