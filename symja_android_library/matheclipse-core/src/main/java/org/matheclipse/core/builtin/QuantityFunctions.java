package org.matheclipse.core.builtin;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.DateObjectExpr;
import org.matheclipse.core.expression.data.TimeObjectExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.tensor.qty.IQuantity;
import org.matheclipse.core.tensor.qty.IUnit;
import org.matheclipse.core.tensor.qty.UnitSystem;

public class QuantityFunctions {
  private static final Logger LOGGER = LogManager.getLogger();

  static final HashMap<String, Function<LocalDateTime, IExpr>> DATEVALUE_MAP =
      new HashMap<String, Function<LocalDateTime, IExpr>>();

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.DateObject.setEvaluator(new DateObject());
      S.DateString.setEvaluator(new DateString());
      S.DateValue.setEvaluator(new DateValue());

      S.TimeObject.setEvaluator(new TimeObject());
      S.Quantity.setEvaluator(new Quantity());
      S.QuantityMagnitude.setEvaluator(new QuantityMagnitude());
      S.QuantityUnit.setEvaluator(new QuantityUnit());
      S.UnitConvert.setEvaluator(new UnitConvert());

      // integers
      DATEVALUE_MAP.put("Year", x -> F.ZZ(x.getYear()));
      DATEVALUE_MAP.put("ISOYearDay", x -> F.ZZ(x.getDayOfYear()));
      DATEVALUE_MAP.put("ISOYearDayShort", x -> F.ZZ(x.getDayOfYear()));
      DATEVALUE_MAP.put("Month", x -> F.ZZ(x.getMonthValue()));
      DATEVALUE_MAP.put("MonthShort", x -> F.ZZ(x.getMonthValue()));
      DATEVALUE_MAP.put("Day", x -> F.ZZ(x.getDayOfMonth()));

      DATEVALUE_MAP.put("Hour", x -> F.ZZ(x.getHour()));
      DATEVALUE_MAP.put("HourShort", x -> F.ZZ(x.getHour()));
      DATEVALUE_MAP.put("Minute", x -> F.ZZ(x.getMinute()));
      DATEVALUE_MAP.put("MinuteShort", x -> F.ZZ(x.getMinute()));
      DATEVALUE_MAP.put("Second", x -> F.ZZ(x.getSecond()));
      DATEVALUE_MAP.put("SecondShort", x -> F.ZZ(x.getSecond()));

      // strings
      DATEVALUE_MAP.put("MonthName",
          x -> F.stringx(x.getMonth().getDisplayName(TextStyle.FULL, Locale.US)));
      DATEVALUE_MAP.put("MonthNameShort",
          x -> F.stringx(x.getMonth().getDisplayName(TextStyle.SHORT, Locale.US)));
      DATEVALUE_MAP.put("MonthNameInitial",
          x -> F.stringx(x.getMonth().getDisplayName(TextStyle.NARROW, Locale.US)));

      DATEVALUE_MAP.put("DayName",
          x -> F.stringx(x.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.US)));
      DATEVALUE_MAP.put("DayNameShort",
          x -> F.stringx(x.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US)));
    }
  }

  private static final class DateObject extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.size() == 1) {
          return DateObjectExpr.newInstance(LocalDateTime.now());
        }
        if (ast.size() == 2) {
          IExpr arg1 = ast.arg1();
          if (arg1.isList()) {
            IAST list = (IAST) arg1;
            if (list.size() == 4) {
              int year = list.arg1().toIntDefault();
              int month = list.arg2().toIntDefault();
              int day = list.arg3().toIntDefault();
              if (year != Integer.MIN_VALUE && //
                  month != Integer.MIN_VALUE && //
                  day != Integer.MIN_VALUE) {
                return DateObjectExpr.newInstance(LocalDateTime.of(year, month, day, 0, 0));
              }
            }
          }
          return F.NIL;
        }
        if (ast.size() == 3) {
          if (ast.arg1() instanceof DateObjectExpr && //
              ast.arg2() instanceof TimeObjectExpr) {
            LocalDate ld = ((DateObjectExpr) ast.arg1()).toData().toLocalDate();
            LocalTime lt = ((TimeObjectExpr) ast.arg2()).toData();
            return DateObjectExpr.newInstance(LocalDateTime.of(ld, lt));
          }
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }
  }

  private static final class DateString extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST0()) {
          LocalDateTime now = LocalDateTime.now();
          String dateString = now.format(DateTimeFormatter.ofPattern("EEE dd MMM yyyy HH:mm:ss"));
          return F.stringx(dateString);
        }
        if (ast.isAST1()) {
          long secondsSince1900 = ast.arg1().toLongDefault();
          if (secondsSince1900 >= 0) {
            Instant base =
                LocalDate.of(1900, Month.JANUARY, 1).atStartOfDay(ZoneOffset.UTC).toInstant();
            LocalDateTime date =
                LocalDateTime.ofInstant(base.plusSeconds(secondsSince1900), ZoneOffset.UTC);
            String dateString =
                date.format(DateTimeFormatter.ofPattern("EEE dd MMM yyyy HH:mm:ss"));
            return F.stringx(dateString);
          }
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }
  }

  private static final class DateValue extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST1()) {
          IExpr arg1 = ast.arg1();
          if (ast.arg1().isList()) {
            return ast.arg1().mapThread(ast, 1);
          }
          if (arg1.isString()) {
            String str = arg1.toString();
            Function<LocalDateTime, IExpr> function = DATEVALUE_MAP.get(str);
            if (function != null) {
              return function.apply(LocalDateTime.now());
            }
          }
          return F.NIL;
        }
        if (ast.isAST2()) {
          IExpr arg1 = ast.arg1();
          IExpr arg2 = ast.arg2();
          if (arg2.isList()) {
            return ast.arg2().mapThread(ast, 2);
          }
          if (arg1 instanceof DateObjectExpr && arg2.isString()) {
            LocalDateTime ldt = ((DateObjectExpr) arg1).toData();
            String str = arg2.toString();
            Function<LocalDateTime, IExpr> function = DATEVALUE_MAP.get(str);
            if (function != null) {
              return function.apply(ldt);
            }
          }
          return F.NIL;
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  private static final class TimeObject extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.size() == 1) {
          LocalTime now = LocalTime.now();
          return TimeObjectExpr
              .newInstance(LocalTime.of(now.getHour(), now.getMinute(), now.getSecond()));
        }
        if (ast.size() == 2) {
          IExpr arg1 = ast.arg1();
          if (arg1.isList()) {
            IAST list = (IAST) arg1;
            if (list.size() >= 2 && list.size() <= 4) {
              int hour = list.arg1().toIntDefault();
              int minute = 0;
              if (list.size() >= 3) {
                minute = list.arg2().toIntDefault();
              }
              int second = 0;
              if (list.size() == 4) {
                second = list.arg3().toIntDefault();
              }
              if (hour >= 0 && //
                  minute >= 0 && //
                  second >= 0) {
                return TimeObjectExpr.newInstance(LocalTime.of(hour, minute, second));
              }
            }
          }
          return F.NIL;
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }
  }

  /**
   *
   *
   * <pre>
   * Quantity(value, unit)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the quantity for <code>value</code> and <code>unit</code>
   *
   * </blockquote>
   */
  private static final class Quantity extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        IExpr arg1 = ast.arg1();
        if (ast.isAST1()) {
          if (!arg1.isString()) {
            return Errors.printMessage(S.Quantity, "unkunit", F.List(arg1));
          }
          IUnit unit = IUnit.of(arg1.toString());
          if (unit == null) {
            return Errors.printMessage(S.Quantity, "unkunit", F.List(arg1));
          }
          return IQuantity.of(F.C1, unit);
        }
        if (ast.isAST2()) {
          if (arg1.isList()) {
            return arg1.mapThread(F.Quantity(F.Slot1, ast.arg2()), 1);
          }
          IExpr arg2 = engine.evaluate(ast.arg2());
          if (!arg2.isString()) {
            return Errors.printMessage(S.Quantity, "unkunit", F.List(arg2));
          }
          IUnit unit = IUnit.of(arg2.toString());
          if (unit == null) {
            return Errors.printMessage(S.Quantity, "unkunit", F.List(arg2));
          }
          return IQuantity.of(arg1, unit);

        }
      } catch (RuntimeException e) {
        Errors.rethrowsInterruptException(e);
        LOGGER.log(engine.getLogLevel(), "Quantity", e);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDREST | ISymbol.NHOLDREST);
    }
  }

  /**
   *
   *
   * <pre>
   * QuantityMagnitude(quantity)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the value of the <code>quantity</code>
   *
   * </blockquote>
   *
   * <pre>
   * QuantityMagnitude(quantity, unit)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the value of the <code>quantity</code> for the given <code>unit</code>
   *
   * </blockquote>
   */
  private static final class QuantityMagnitude extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.size() == 2) {
          IExpr arg1 = engine.evaluate(ast.arg1());
          if (arg1.isQuantity()) {
            return ((IQuantity) arg1).value();
          }
        } else if (ast.size() == 3) {
          IExpr arg1 = engine.evaluate(ast.arg1());
          if (arg1.isQuantity()) {
            IExpr arg2 = engine.evaluate(ast.arg2());
            org.matheclipse.core.tensor.qty.QuantityMagnitude quantityMagnitude =
                org.matheclipse.core.tensor.qty.QuantityMagnitude.SI();
            IUnit unit = IUnit.of(arg2.toString());
            if (unit == null) {
              return F.NIL;
            }
            UnaryOperator<IExpr> suo = quantityMagnitude.in(unit);
            return suo.apply(arg1);
          }
        }
      } catch (RuntimeException e) {
        Errors.rethrowsInterruptException(e);
        LOGGER.log(engine.getLogLevel(), "QuantityMagnitude", e);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static final class QuantityUnit extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1() instanceof IQuantity) {
        IQuantity quantity = (IQuantity) ast.arg1();
        IUnit unit = org.matheclipse.core.tensor.qty.QuantityUnit.of(quantity);
        return F.stringx(unit.toString());
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   *
   *
   * <pre>
   * UnitConvert(quantity)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * convert the <code>quantity</code> to the base unit
   *
   * </blockquote>
   *
   * <pre>
   * UnitConvert(quantity, unit)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * convert the <code>quantity</code> to the given <code>unit</code>
   *
   * </blockquote>
   */
  private static final class UnitConvert extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        IExpr arg1 = ast.arg1().eval(engine);
        if (ast.size() == 2) {
          if (arg1.isQuantity()) {
            return UnitSystem.SI().apply(arg1);
          }
        } else if (ast.size() == 3) {
          IExpr arg2 = ast.arg2().eval(engine);
          if (arg1.isQuantity()) {
            IUnit unit = IUnit.of(arg2.toString());
            if (unit == null) {
              return F.NIL;
            }
            return unitConvert((IQuantity) arg1, unit);
          }
        }
      } catch (RuntimeException e) {
        Errors.rethrowsInterruptException(e);
        LOGGER.log(engine.getLogLevel(), "UnitConvert", e);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private QuantityFunctions() {}

  public static IExpr unitConvert(IQuantity arg1, IUnit unit) {
    org.matheclipse.core.tensor.qty.UnitConvert unitConvert =
        org.matheclipse.core.tensor.qty.UnitConvert.SI();
    return unitConvert.to(unit).apply(arg1);
  }
}
