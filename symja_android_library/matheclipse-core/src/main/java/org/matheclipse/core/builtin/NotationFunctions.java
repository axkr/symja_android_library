package org.matheclipse.core.builtin;

import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractSymbolEvaluator;
import org.matheclipse.core.eval.interfaces.ISetValueEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Heads which only change how an expression is shown. They have no evaluation of their own; the
 * output forms, <code>ToBoxes</code>, <code>TeXForm</code> and the graphics labels draw them:
 *
 * <ul>
 * <li>the marks <code>OverBar</code>, <code>OverHat</code>, <code>OverTilde</code>,
 * <code>OverDot</code>, <code>OverVector</code>, <code>UnderBar</code>, <code>SubPlus</code>,
 * <code>SubMinus</code>, <code>SubStar</code>, <code>SuperPlus</code> and
 * <code>SuperMinus</code>,
 * <li>the brackets <code>AngleBracket</code>,
 * <li>the layouts <code>ColumnForm</code>, <code>PercentForm</code> and <code>Shallow</code>,
 * <li>the boxes <code>FrameBox</code> and <code>AdjustmentBox</code>.
 * </ul>
 *
 * <p>
 * <code>Bra</code>, <code>Ket</code>, <code>TextGrid</code>, <code>Hyperlink</code> and the
 * operators <code>GreaterSlantEqual</code> and <code>LessSlantEqual</code> are inert as well;
 * OutputForm prints them in function form or as infix operators.
 *
 * <p>
 * <code>$DefaultFont</code> is the font of text without a font of its own.
 */
public final class NotationFunctions {

  /** The heads without an evaluation of their own. */
  private static final IBuiltInSymbol[] INERT_HEADS = {S.OverBar, S.OverHat, S.OverTilde,
      S.OverDot, S.OverVector, S.UnderBar, S.SubPlus, S.SubMinus, S.SubStar, S.SuperPlus,
      S.SuperMinus, S.AngleBracket, S.Bra, S.Ket, S.ColumnForm, S.TextGrid, S.PercentForm,
      S.Shallow, S.Hyperlink, S.FrameBox, S.AdjustmentBox, S.GreaterSlantEqual, S.LessSlantEqual};

  private static class Initializer {

    private static void init() {
      Inert inert = new Inert();
      for (IBuiltInSymbol head : INERT_HEADS) {
        head.setEvaluator(inert);
      }
      // N doesn't touch the digit specification of PercentForm(x, n)
      S.PercentForm.addAttributes(ISymbol.NHOLDREST);
      S.TextGrid.addAttributes(ISymbol.READPROTECTED);
      S.Hyperlink.addAttributes(ISymbol.READPROTECTED);
      S.$DefaultFont.setEvaluator(new $DefaultFont());
    }
  }

  /** A head without evaluation of its own. */
  private static final class Inert extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /** <code>$DefaultFont</code> - <code>{"Courier", 10.0}</code> unless something else was assigned. */
  private static final class $DefaultFont extends AbstractSymbolEvaluator
      implements ISetValueEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      IExpr value = engine.getDollarValue(S.$DefaultFont);
      return value.isPresent() ? value : F.list(F.stringx("Courier"), F.num(10.0));
    }

    @Override
    public IExpr evaluateSet(IExpr rightHandSide, boolean setDelayed, final EvalEngine engine) {
      EvalEngine.incEpoch();
      engine.setDollarValue(S.$DefaultFont, rightHandSide);
      return rightHandSide;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private NotationFunctions() {}
}
