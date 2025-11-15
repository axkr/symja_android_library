package org.matheclipse.core.reflection.system;

import java.math.BigInteger;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.FileExpr;
import org.matheclipse.core.expression.data.InputStreamExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;

public class FileHash extends AbstractFunctionEvaluator {

  public FileHash() {}

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (!Config.FILESYSTEM_ENABLED) {
      // The operation `1` is not allowed in sandbox mode.
      return Errors.printMessage(S.FileHash, "sandbox", F.List(S.FileHash));
    }
    IExpr arg1 = ast.arg1();
    String algorithm = "MD5";
    if (ast.isAST2()) {
      IExpr arg2 = ast.arg2();
      if (arg2 instanceof IStringX) {
        algorithm = arg2.toString();
      } else {
        return F.NIL;
      }
    }
    if (arg1 instanceof FileExpr) {
      try {
        BigInteger hashValue = Hash.hash((FileExpr) arg1, algorithm);
        if (hashValue != null) {
          return F.ZZ(hashValue);
        }
      } catch (Exception e) {
        e.printStackTrace();
        return F.NIL;
      }
    }
    if (arg1 instanceof InputStreamExpr) {
      try {
        BigInteger hashValue = Hash.hash((InputStreamExpr) arg1, algorithm);
        if (hashValue != null) {
          return F.ZZ(hashValue);
        }
      } catch (Exception e) {
        e.printStackTrace();
        return F.NIL;
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }

}
