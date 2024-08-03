package org.matheclipse.core.sympy.concrete;

import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.sympy.exception.NotImplementedError;
import org.matheclipse.core.sympy.series.Limitseq;

public class Summations {
  public static boolean isConvergent(IAST sumAST) {
    IExpr sequence_term = sumAST.arg1();
    IAST limits = (IAST) sumAST.arg2();
    IExpr sym = limits.arg1();
    IExpr lower_limit = limits.arg2();
    IExpr upper_limit = limits.arg3();
    VariablesSet variables = new VariablesSet(sequence_term);
    if (variables.size() > 1) {
      throw new NotImplementedError(
          "Summations#isConvergent() not implemented for more than 1 variable.");
    }
    if (lower_limit.isNumericFunction(true) && upper_limit.isNumericFunction(true)) {
      return true;
    }

    EvalEngine engine = new EvalEngine();
    // TODO remove if-condition if Limit is improved for complex numbers
    if (!sequence_term.hasComplexNumber()) {
      // ### -------- Divergence test ----------- ###
      IExpr lim_val = Limitseq.limit_seq(sequence_term, sym);
      if (lim_val.isPresent() && !lim_val.isPossibleZero(true)) {
        return false;
      }

      IExpr lim_val_abs = Limitseq.limit_seq(F.Abs(sequence_term), sym);
      if (lim_val_abs.isPresent() && !lim_val_abs.isPossibleZero(true)) {
        return false;
      }
    }
    // TODO other tests https://en.wikipedia.org/wiki/Convergence_tests

    // ### ------------- Limit comparison test -----------###
    // # (1/n) comparison
    IExpr lim_comp = Limitseq.limit_seq(F.Times(sym, sequence_term), sym);
    if (lim_comp.isPresent() && lim_comp.isNumber() && lim_comp.greaterThan(F.C0).isTrue()) {
      return false;
    }


    return true;
  }

  public static boolean isAbsolutelyConvergent(IAST sumAST) {
    return isConvergent(F.Sum(F.Abs(sumAST.arg1()), sumAST.arg2()));
  }
}
