package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public interface IRewrite {
  default IExpr rewrite(IAST ast, EvalEngine engine, int functionID) {
    switch (functionID) {
      case ID.Exp:
        if (ast.isAST1()) {
          return rewriteExp(ast.arg1(), engine);
        }
        break;
      case ID.Log:
        if (ast.isAST1()) {
          return rewriteLog(ast.arg1(), engine);
        }
        if (ast.isAST2()) {
          return rewriteLog(ast.arg1(), ast.arg2(), engine);
        }
        break;
    }
    return F.NIL;
  }

  default IExpr rewriteExp(IExpr arg, EvalEngine engine) {
    return F.NIL;
  }

  default IExpr rewriteLog(IExpr arg, EvalEngine engine) {
    return F.NIL;
  }

  default IExpr rewriteLog(IExpr arg1, IExpr arg2, EvalEngine engine) {
    return F.NIL;
  }

  // default IExpr asLeadingTerm(IAST ast, ISymbol symbol, int cdir) {
  // return asLeadingTerm(ast, symbol, F.NIL, cdir);
  // }

  default IExpr evalAsLeadingTerm(IAST ast, ISymbol symbol, IExpr logx, int cdir) {
    return F.NIL;
  }

}
