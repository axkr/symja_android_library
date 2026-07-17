package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * MoleculeValue(mol, "AtomCount") MoleculeValue(mol, List("AtomCount", "MolecularFormula"))
 */
public class MoleculeValue extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() == 2) {
      IExpr mol = ast.arg1();
      IExpr prop = ast.arg2();

      // Validate the first argument is a Molecule AST with two lists (atoms and bonds)
      if (mol.isAST(F.Molecule, 3)) {
        IAST molAst = (IAST) mol;

        if (prop.isString()) {
          return evaluateProperty(molAst, prop.toString(), engine);
        } else if (prop.isList()) {
          IAST propList = (IAST) prop;
          IASTAppendable results = F.ListAlloc();

          for (int i = 1; i <= propList.argSize(); i++) {
            IExpr res = evaluateProperty(molAst, propList.get(i).toString(), engine);
            if (res.isPresent()) {
              results.append(res);
            } else {
              results.append(F.Missing("NotAvailable"));
            }
          }
          return results;
        }
      }
    }
    return F.NIL;
  }

  private IExpr evaluateProperty(IAST mol, String propertyName, EvalEngine engine) {
    switch (propertyName) {
      case "AtomCount":
        IExpr atoms = mol.arg1();
        if (atoms.isList()) {
          return F.ZZ(((IAST) atoms).argSize());
        }
        return F.NIL;

      case "BondCount":
        IExpr bonds = mol.arg2();
        if (bonds.isList()) {
          return F.ZZ(((IAST) bonds).argSize());
        }
        return F.NIL;

      case "MolecularMass":
        // Calculate via CDK and evaluate to a double representation natively
        // IExpr exactMass = ...
        // return exactMass.evalf();
        return F.NIL;

      case "MolecularFormula":
        // Return Hill formula string generated from CDK
        // return F.stringx(formula);
        return F.NIL;

      default:
        return F.NIL;
    }
  }
}