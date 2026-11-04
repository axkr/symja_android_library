package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The trigonometric functions with an argument in degrees (<code>SinDegrees</code> ...
 * <code>CscDegrees</code>) and the inverse functions with a result in degrees
 * (<code>ArcSinDegrees</code> ... <code>ArcCscDegrees</code>).
 *
 * <p>
 * Each function is evaluated through its radian counterpart. A closed form which is free of
 * <code>Pi</code> is returned; otherwise the trigonometric functions of the radian result are
 * written in degrees again, so <code>SinDegrees(-x)</code> gives <code>-SinDegrees(x)</code>
 * while <code>SinDegrees(15/2)</code> stays unevaluated.
 */
public class DegreeTrigFunctions {

  /** The trigonometric functions with an argument in degrees. */
  private static final IBuiltInSymbol[] FORWARD_DEGREES = {S.SinDegrees, S.CosDegrees,
      S.TanDegrees, S.CotDegrees, S.SecDegrees, S.CscDegrees};

  /** The radian counterparts of {@link #FORWARD_DEGREES}. */
  private static final IBuiltInSymbol[] FORWARD_RADIANS =
      {S.Sin, S.Cos, S.Tan, S.Cot, S.Sec, S.Csc};

  /** The inverse trigonometric functions with a result in degrees. */
  private static final IBuiltInSymbol[] INVERSE_DEGREES = {S.ArcSinDegrees, S.ArcCosDegrees,
      S.ArcTanDegrees, S.ArcCotDegrees, S.ArcSecDegrees, S.ArcCscDegrees};

  /** The radian counterparts of {@link #INVERSE_DEGREES}. */
  private static final IBuiltInSymbol[] INVERSE_RADIANS =
      {S.ArcSin, S.ArcCos, S.ArcTan, S.ArcCot, S.ArcSec, S.ArcCsc};

  private static class Initializer {

    private static void init() {
      for (int i = 0; i < FORWARD_DEGREES.length; i++) {
        FORWARD_DEGREES[i].setEvaluator(new DegreeTrig(FORWARD_RADIANS[i]));
        INVERSE_DEGREES[i].setEvaluator(new InverseDegreeTrig(INVERSE_RADIANS[i]));
      }
    }
  }

  /** <code>SinDegrees(x)</code> ... <code>CscDegrees(x)</code> */
  private static final class DegreeTrig extends AbstractFunctionEvaluator {
    private final IBuiltInSymbol radianHead;

    private DegreeTrig(IBuiltInSymbol radianHead) {
      this.radianHead = radianHead;
    }

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IAST radianAST = toRadian(ast);
      if (ast.arg1().isPlus()) {
        // SinDegrees(90+x) is Sin(Pi/2+Pi*x/180)
        radianAST = F.unaryAST1(radianAST.head(), F.Expand(radianAST.arg1()));
      }
      IExpr radian = engine.evaluate(radianAST);
      IExpr result = cofunction(fromRadian(radian, engine));
      if (result.equals(ast) || !result.isFree(S.Pi)) {
        return F.NIL;
      }
      return result;
    }

    /**
     * An angle between 45 and 90 degrees is written as the complement of the cofunction:
     * <code>SinDegrees(80)</code> is <code>CosDegrees(10)</code>.
     */
    private static IExpr cofunction(IExpr expr) {
      return expr.replaceAll(x -> {
        if (x.isAST1() && x.first().isRational()) {
          for (int i = 0; i < FORWARD_DEGREES.length; i++) {
            if (x.head() == FORWARD_DEGREES[i]) {
              IRational degrees = (IRational) x.first();
              if (degrees.isGT(F.ZZ(45)) && degrees.isLT(F.ZZ(90))) {
                // Sin <-> Cos, Tan <-> Cot, Sec <-> Csc
                return F.unaryAST1(FORWARD_DEGREES[i ^ 1], F.ZZ(90).subtract(degrees));
              }
              break;
            }
          }
        }
        return F.NIL;
      }).orElse(expr);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IFunctionEvaluator.ARGS_1_1;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /** <code>ArcSinDegrees(x)</code> ... <code>ArcCscDegrees(x)</code> */
  private static final class InverseDegreeTrig extends AbstractFunctionEvaluator {
    private final IBuiltInSymbol radianHead;

    private InverseDegreeTrig(IBuiltInSymbol radianHead) {
      this.radianHead = radianHead;
    }

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IASTMutable radianAST = ast.setAtCopy(0, radianHead);
      IExpr radian = engine.evaluate(radianAST);
      if (radian.equals(radianAST) && ast.isAST1() && ast.arg1().isInterval1()) {
        // ArcSec(x) == ArcCos(1/x) and ArcCsc(x) == ArcSin(1/x) for an interval outside (-1,1)
        IExpr reciprocal = reciprocalArcInterval(ast.arg1(), engine);
        if (reciprocal.isPresent()) {
          radian = engine.evaluate(reciprocal);
        }
      }
      IExpr result;
      if (radian.isInterval()) {
        // the bounds one by one, an interval with a symbolic bound can't be scaled
        result = radian.replaceAll(x -> x.isList() ? ((IAST) x).map(r -> toDegrees(r, engine))
            : F.NIL).orElse(radian);
      } else {
        result = toDegrees(radian, engine);
      }
      if (result.equals(ast) || (!result.isFree(S.Pi) && !ast.arg1().isNumber())) {
        // a number gives a closed form also with Pi: ArcSinDegrees(I) is 180*I*ArcSinh(1)/Pi
        return F.NIL;
      }
      return result;
    }

    /**
     * <code>180*radian/Pi</code>, with the inverse functions in <code>radian</code> written in
     * degrees.
     */
    private static IExpr toDegrees(IExpr radian, EvalEngine engine) {
      // the inverse functions which stay unevaluated are replaced by placeholders, so that the
      // degree forms aren't evaluated again inside this evaluation
      final IASTAppendable rules = F.ListAlloc();
      IExpr degrees = radian.replaceAll(x -> {
        if (x.isAST()) {
          IBuiltInSymbol degreeHead = degreeHead(x.head(), INVERSE_RADIANS, INVERSE_DEGREES);
          if (degreeHead != null) {
            ISymbol placeholder = F.Dummy("d" + rules.size());
            rules.append(F.Rule(placeholder, ((IAST) x).setAtCopy(0, degreeHead)));
            return F.Times(F.QQ(1, 180), S.Pi, placeholder);
          }
        }
        return F.NIL;
      }).orElse(radian);
      IExpr result = engine.evaluate(F.Times(F.ZZ(180), F.Power(S.Pi, F.CN1), degrees));
      if (rules.argSize() > 0) {
        result = F.subst(result, rules);
      }
      return result;
    }

    /**
     * <code>ArcCos(1/x)</code> for <code>ArcSec(x)</code> and <code>ArcSin(1/x)</code> for
     * <code>ArcCsc(x)</code>, if the interval <code>x</code> lies in <code>x&lt;=-1</code> or in
     * <code>x&gt;=1</code>.
     */
    private IExpr reciprocalArcInterval(IExpr interval, EvalEngine engine) {
      if (radianHead != S.ArcSec && radianHead != S.ArcCsc) {
        return F.NIL;
      }
      IExpr span = interval.first();
      if (!span.isList2()) {
        return F.NIL;
      }
      IExpr min = span.first();
      IExpr max = span.second();
      if (!min.isRealResult() || !max.isRealResult()) {
        return F.NIL;
      }
      if (engine.evalTrue(F.GreaterEqual(min, F.C1))
          || engine.evalTrue(F.LessEqual(max, F.CN1))) {
        IBuiltInSymbol head = radianHead == S.ArcSec ? S.ArcCos : S.ArcSin;
        return F.unaryAST1(head, F.Power(interval, F.CN1));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return radianHead == S.ArcTan ? IFunctionEvaluator.ARGS_1_2 : IFunctionEvaluator.ARGS_1_1;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static IBuiltInSymbol degreeHead(IExpr head, IBuiltInSymbol[] radians,
      IBuiltInSymbol[] degrees) {
    for (int i = 0; i < radians.length; i++) {
      if (head == radians[i]) {
        return degrees[i];
      }
    }
    return null;
  }

  /**
   * Test if <code>expr</code> is one of the trigonometric functions with an argument in degrees
   * <code>SinDegrees(x)</code> ... <code>CscDegrees(x)</code>.
   */
  public static boolean isForwardDegree(IExpr expr) {
    return expr.isAST1() && degreeHead(expr.head(), FORWARD_DEGREES, FORWARD_RADIANS) != null;
  }

  /**
   * The radian form <code>Sin(x*Pi/180)</code> of <code>SinDegrees(x)</code> (unevaluated).
   *
   * @param degreeAST one of <code>SinDegrees(x)</code> ... <code>CscDegrees(x)</code>
   */
  public static IAST toRadian(IAST degreeAST) {
    IBuiltInSymbol radianHead = degreeHead(degreeAST.head(), FORWARD_DEGREES, FORWARD_RADIANS);
    return F.unaryAST1(radianHead, F.Times(F.QQ(1, 180), S.Pi, degreeAST.arg1()));
  }

  /**
   * Write the trigonometric functions <code>Sin(y)</code> ... <code>Csc(y)</code> in
   * <code>expr</code> as <code>SinDegrees(180*y/Pi)</code> ... <code>CscDegrees(180*y/Pi)</code>,
   * if <code>180*y/Pi</code> is free of <code>Pi</code>.
   *
   * @return <code>expr</code> if no function could be written in degrees
   */
  public static IExpr fromRadian(IExpr expr, EvalEngine engine) {
    return expr.replaceAll(x -> {
      if (x.isAST1()) {
        IBuiltInSymbol degreeHead = degreeHead(x.head(), FORWARD_RADIANS, FORWARD_DEGREES);
        if (degreeHead != null) {
          IExpr degrees =
              engine.evaluate(F.Expand(F.Times(F.ZZ(180), F.Power(S.Pi, F.CN1), x.first())));
          if (degrees.isFree(S.Pi)) {
            return F.unaryAST1(degreeHead, degrees);
          }
        }
      }
      return F.NIL;
    }).orElse(expr);
  }

  public static void initialize() {
    Initializer.init();
  }

  private DegreeTrigFunctions() {}
}
