package org.matheclipse.core.eval.util;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Utility functions for molecular operations.
 */
public class ChemistryUtils {

  /**
   * Executes logic for built-in chemistry symbols based on their ordinal value.
   */
  public static IExpr dispatchChemistryAction(IAST ast) {
    IExpr head = ast.head();

    if (head.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) head).ordinal()) {
        case ID.MoleculeQ:
          return evaluateMoleculeQ(ast);
        case ID.AtomList:
          return extractList(ast, 1);
        case ID.BondList:
          return extractList(ast, 2);
        default:
          return F.NIL;
      }
    }
    return F.NIL;
  }

  private static IExpr extractList(IAST ast, int argPosition) {
    if (ast.argSize() == 1) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST(S.Molecule, 3)) {
        IAST molAst = (IAST) arg1;
        return molAst.get(argPosition);
      }
    }
    return F.NIL;
  }

  private static IExpr evaluateMoleculeQ(IAST ast) {
    if (ast.argSize() == 1 && ast.arg1().isAST(S.Molecule, 3)) {
      return F.True;
    }
    return F.False;
  }
}