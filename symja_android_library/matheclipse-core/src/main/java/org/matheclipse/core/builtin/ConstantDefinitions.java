package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Times;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Locale;
import org.apfloat.Apfloat;
import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.EvalHistory;
import org.matheclipse.core.eval.interfaces.AbstractSymbolEvaluator;
import org.matheclipse.core.eval.interfaces.IRealConstant;
import org.matheclipse.core.eval.interfaces.ISetValueEvaluator;
import org.matheclipse.core.expression.ContextPath;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.DateObjectExpr;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IExpr.COMPARE_TERNARY;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.ParserConfig;

public class ConstantDefinitions {

  // load version string from MAVEN
  public static String VERSION = "?";
  public static String TIMESTAMP = "";
  private static int YEAR = Calendar.getInstance().get(Calendar.YEAR);
  private static int MONTH = Calendar.getInstance().get(Calendar.MONTH);
  private static int DAY = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
  private static int HOUR = Calendar.getInstance().get(Calendar.HOUR);
  private static int MINUTE = Calendar.getInstance().get(Calendar.MINUTE);
  private static int SECOND = Calendar.getInstance().get(Calendar.SECOND);
  public static final double EULER_GAMMA = 0.57721566490153286060651209008240243104215933593992;

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {

      String versionString = Config.getVersion();
      if (versionString != null) {
        VERSION = versionString;
      }
      // Properties properties = ResourceData.properties("/version.txt");
      // String timestamp = properties.getProperty("timestamp");
      // if (timestamp != null && timestamp.charAt(0) != '$') {
      // TIMESTAMP = timestamp;
      // try {
      // YEAR = Integer.parseInt(TIMESTAMP.substring(0, 4));
      // MONTH = Integer.parseInt(TIMESTAMP.substring(4, 6));
      // DAY = Integer.parseInt(TIMESTAMP.substring(6, 8));
      // HOUR = Integer.parseInt(TIMESTAMP.substring(8, 10));
      // MINUTE = Integer.parseInt(TIMESTAMP.substring(10, 12));
      // SECOND = Integer.parseInt(TIMESTAMP.substring(12, 14));
      // } catch (NumberFormatException nfe) {
      // }
      // }

      S.$Assumptions.setEvaluator(new $Assumptions());
      S.$BaseDirectory.setEvaluator(new $BaseDirectory());
      S.$Context.setEvaluator(new $Context());
      S.$ContextPath.setEvaluator(new $ContextPath());
      S.$CreationDate.setEvaluator(new $CreationDate());
      S.$HistoryLength.setEvaluator(new $HistoryLength());
      S.$HomeDirectory.setEvaluator(new $HomeDirectory());
      S.$Input.setEvaluator(new $Input());
      S.$InputFileName.setEvaluator(new $InputFileName());
      S.$IterationLimit.setEvaluator(new $IterationLimit());
      S.$Line.setEvaluator(new $Line());
      S.$MachineEpsilon.setEvaluator(new $MachineEpsilon());
      S.$MachinePrecision.setEvaluator(new $MachinePrecision());
      S.$MaxMachineNumber.setEvaluator(new $MaxMachineNumber());
      S.$MinMachineNumber.setEvaluator(new $MinMachineNumber());
      S.$Notebooks.setEvaluator(new $Notebooks());
      S.$OperatingSystem.setEvaluator(new $OperatingSystem());
      S.$Packages.setEvaluator(new $Packages());
      S.$Path.setEvaluator(new $Path());
      S.$PathnameSeparator.setEvaluator(new $PathnameSeparator());

      S.$UserName.setEvaluator(new $UserName());
      S.$RecursionLimit.setEvaluator(new $RecursionLimit());
      S.$RootDirectory.setEvaluator(new $RootDirectory());
      S.$ScriptCommandLine.setEvaluator(new $ScriptCommandLine());
      S.$SystemCharacterEncoding.setEvaluator(new $SystemCharacterEncoding());
      S.$CharacterEncoding.setEvaluator(new $CharacterEncoding());
      S.$SystemMemory.setEvaluator(new $SystemMemory());
      S.$TemporaryDirectory.setEvaluator(new $TemporaryDirectory());
      S.$UserBaseDirectory.setEvaluator(new $UserBaseDirectory());
      S.$Version.setEvaluator(new $Version());

      S.RecordSeparators.setEvaluator(new RecordSeparators());
      S.WordSeparators.setEvaluator(new WordSeparators());

      S.Catalan.setEvaluator(new Catalan());
      S.ComplexInfinity.setEvaluator(new ComplexInfinity());
      S.Degree.setEvaluator(new Degree());
      S.E.setEvaluator(new E());
      S.EulerGamma.setEvaluator(new EulerGamma());
      S.Glaisher.setEvaluator(new Glaisher());
      S.GoldenAngle.setEvaluator(new GoldenAngle());
      S.GoldenRatio.setEvaluator(new GoldenRatio());
      S.I.setEvaluator(new I());
      S.Infinity.setEvaluator(new Infinity());
      S.Khinchin.setEvaluator(new Khinchin());
      S.Pi.setEvaluator(new Pi());

      S.Now.setEvaluator(new Now());
      S.Today.setEvaluator(new Today());

      S.False.setEvaluator(NILEvaluator.CONST);
      S.True.setEvaluator(NILEvaluator.CONST);
      S.Null.setEvaluator(NILEvaluator.CONST);
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private ConstantDefinitions() {}

  private static class NILEvaluator extends AbstractSymbolEvaluator {
    static final NILEvaluator CONST = new NILEvaluator();

    @Override
    public IExpr numericEval(final ISymbol symbol, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public IExpr apfloatEval(ISymbol symbol, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      // don't set CONSTANT attribute !
    }
  }

  private static class $Assumptions extends AbstractSymbolEvaluator implements ISetValueEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      // IAssumptions assumptions = engine.getAssumptions();
      // if (assumptions == null) {
      // return F.True;
      // }
      IExpr assume = S.$Assumptions.assignedValue();
      if (assume != null) {
        return assume;
      }
      return F.True;
    }

    @Override
    public IExpr evaluateSet(IExpr rightHandSide, boolean setDelayed, final EvalEngine engine) {
      S.$Assumptions.assignValue(rightHandSide, setDelayed);
      return rightHandSide;
    }
  }

  private static class $BaseDirectory extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      String userHome = System.getProperty("user.home");
      if (userHome == null) {
        return F.CEmptyString;
      }
      Path path = Paths.get(userHome, "Symja");
      return F.stringx(path.toString());
    }
  }

  private static class $CharacterEncoding extends AbstractSymbolEvaluator
      implements ISetValueEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      AbstractSymbolEvaluator evaluator =
          (AbstractSymbolEvaluator) S.$SystemCharacterEncoding.getEvaluator();
      IExpr systemCharacterEncoding = evaluator.evaluate(symbol, engine);
      final String characterEncoding;
      if (systemCharacterEncoding.isString()) {
        characterEncoding = systemCharacterEncoding.toString();
      } else {
        characterEncoding = Config.SYSTEM_CHARACTER_ENCODING;
      }
      return F.stringx(characterEncoding);
    }

    @Override
    public IExpr evaluateSet(IExpr rightHandSide, boolean setDelayed, final EvalEngine engine) {
      if (rightHandSide.isString()) {
        S.$CharacterEncoding.assignValue(rightHandSide, setDelayed);
      }
      return rightHandSide;
    }
  }

  private static class $Context extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return EvalEngine.get().getContextPath().currentCompleteContextName();
    }
  }

  private static class $ContextPath extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return EvalEngine.get().getContextPath().pathAsStrings();
    }
  }

  private static class $CreationDate extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return F.listOfObjects(F.ZZ(YEAR), F.ZZ(MONTH), F.ZZ(DAY), F.ZZ(HOUR), F.ZZ(MINUTE),
          F.ZZ(SECOND));
    }
  }

  private static class $HistoryLength extends AbstractSymbolEvaluator
      implements ISetValueEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      EvalHistory history = engine.getEvalHistory();
      if (history == null) {
        return F.C0;
      }
      short historyLength = history.getHistoryLength();
      if (historyLength == Short.MAX_VALUE) {
        return F.CInfinity;
      }
      return F.ZZ(historyLength);
    }

    @Override
    public IExpr evaluateSet(IExpr rightHandSide, boolean setDelayed, final EvalEngine engine) {
      int iValue = rightHandSide.toIntDefault();
      short historyLength;
      if (iValue < 0) {
        if (rightHandSide.isInfinity()) {
          historyLength = Short.MAX_VALUE;
        } else {
          // Positive machine-sized integer expected at position `2` in `1`.
          return Errors.printMessage(S.$HistoryLength, "intpm",
              F.list(F.C2, F.Set(S.$HistoryLength, rightHandSide)), engine);
        }
      } else if (iValue < Short.MAX_VALUE) {
        historyLength = (short) iValue;
      } else {
        historyLength = Short.MAX_VALUE;
      }
      IInteger value = F.ZZ(historyLength);
      S.$HistoryLength.assignValue(value, setDelayed);
      EvalHistory history = engine.getEvalHistory();
      if (history == null) {
        history = new EvalHistory(historyLength);
        engine.setOutListDisabled(history);
      } else {
        history.setHistoryLength(historyLength);
      }
      return value;
    }
  }

  private static class $HomeDirectory extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      String userHome = System.getProperty("user.home");
      if (userHome == null) {
        return F.CEmptyString;
      }
      Path path = Paths.get(userHome);
      return F.stringx(path.toString());
    }
  }

  private static class $Input extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return F.stringx(EvalEngine.get().get$Input());
    }
  }

  private static class $InputFileName extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return F.stringx(EvalEngine.get().get$InputFileName());
    }
  }

  private static class $IterationLimit extends AbstractSymbolEvaluator
      implements ISetValueEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      int iterationLimit = engine.getIterationLimit();
      if (symbol.hasAssignedSymbolValue()) {
        IExpr value = symbol.assignedValue();
        iterationLimit = value.toIntDefault();
        engine.setIterationLimit(iterationLimit);
      }

      return F.ZZ(iterationLimit);
    }

    @Override
    public IExpr evaluateSet(IExpr rightHandSide, boolean setDelayed, final EvalEngine engine) {
      if (rightHandSide.isInfinity()) {
        S.$IterationLimit.assignValue(F.CN1, false);
        engine.setIterationLimit(-1);
        return rightHandSide;
      }
      int iterationLimit = rightHandSide.toIntDefault();
      if (iterationLimit < 20) {
        // Cannot set $IterationLimit to `1`; value must be Infinity or an integer at least 20.
        return Errors.printMessage(S.$IterationLimit, "limset", F.list(rightHandSide), engine);
      }
      S.$IterationLimit.assignValue(F.ZZ(iterationLimit), setDelayed);
      engine.setIterationLimit(iterationLimit);
      return F.ZZ(iterationLimit);
    }
  }

  private static class $Line extends AbstractSymbolEvaluator implements ISetValueEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      EvalHistory history = engine.getEvalHistory();
      if (history == null) {
        return F.C0;
      }
      int line = history.getLineCounter();
      return F.ZZ(line);
    }

    @Override
    public IExpr evaluateSet(IExpr rightHandSide, boolean setDelayed, final EvalEngine engine) {
      int line = rightHandSide.toIntDefault();
      IInteger value = F.ZZ(line);
      S.$Line.assignValue(value, setDelayed);
      EvalHistory history = engine.getEvalHistory();
      if (history == null) {
        history = new EvalHistory((short) 100);
        engine.setOutListDisabled(history);
      }
      history.resetLineCounter(line);
      return value;
    }
  }

  private static class $MachineEpsilon extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return F.num(Config.MACHINE_EPSILON);
    }
  }

  private static class $MachinePrecision extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return F.ZZ(ParserConfig.MACHINE_PRECISION);
    }
  }

  private static class $MaxMachineNumber extends AbstractSymbolEvaluator {
    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return F.num(Double.MAX_VALUE);
    }
  }

  private static class $MinMachineNumber extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return F.num(Double.MIN_NORMAL);
    }
  }

  private static class $Notebooks extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return F.False;
    }
  }

  private static class $OperatingSystem extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      String operatingSystem = System.getProperty("os.name", "Unknown").toLowerCase(Locale.ENGLISH);
      if (operatingSystem == null) {
        return F.stringx("Unknown");
      }
      operatingSystem = operatingSystem.toLowerCase(Locale.US);
      if (operatingSystem.contains("mac") //
          || operatingSystem.contains("os2") //
          || operatingSystem.contains("darwin")) {
        return F.stringx("MaxOSX");
      }
      if (operatingSystem.contains("win")) {
        return F.stringx("Windows");
      }
      if (operatingSystem.contains("six") || operatingSystem.contains("nix")
          || operatingSystem.contains("nux") || operatingSystem.contains("aix")) {
        return F.stringx("Unix");
      }
      return F.stringx("Unknown");
    }
  }

  private static class $Packages extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      IASTAppendable result = F.ListAlloc(ContextPath.PACKAGES.size());
      for (String str : ContextPath.PACKAGES) {
        result.append(F.$str(str));
      }
      return result;
    }
  }

  private static class $Path extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      String path = System.getenv("PATH");
      if (path == null) {
        return F.CEmptyString;
      }
      return F.stringx(path);
    }
  }

  private static class $PathnameSeparator extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return F.stringx(File.separator);
    }
  }

  private static class $RecursionLimit extends AbstractSymbolEvaluator
      implements ISetValueEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      int recursionLimit = engine.getRecursionLimit();
      if (symbol.hasAssignedSymbolValue()) {
        IExpr value = symbol.assignedValue();
        recursionLimit = value.toIntDefault();
        engine.setRecursionLimit(recursionLimit);
      }

      return F.ZZ(recursionLimit);
    }

    @Override
    public IExpr evaluateSet(IExpr rightHandSide, boolean setDelayed, final EvalEngine engine) {
      if (rightHandSide.isInfinity()) {
        S.$RecursionLimit.assignValue(F.CN1, false);
        engine.setRecursionLimit(-1);
        return rightHandSide;
      }
      int recursionLimit = rightHandSide.toIntDefault();
      if (recursionLimit < 20) {
        // Cannot set $RecursionLimit to `1`; value must be Infinity or an integer at least 20.
        return Errors.printMessage(S.$RecursionLimit, "limset", F.list(rightHandSide), engine);
      }
      S.$RecursionLimit.assignValue(F.ZZ(recursionLimit), setDelayed);
      engine.setRecursionLimit(recursionLimit);
      return F.ZZ(recursionLimit);
    }
  }

  private static class $RootDirectory extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      Path root = Paths.get(System.getProperty("user.dir")).getFileSystem().getRootDirectories()
          .iterator().next();
      if (root == null) {
        return F.stringx("/");
      }
      return F.stringx(root.toString());
    }
  }

  private static class $ScriptCommandLine extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return Config.SCRIPT_COMMAND_LINE == null ? F.CEmptyList : Config.SCRIPT_COMMAND_LINE;
    }
  }

  private static class $SystemCharacterEncoding extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      String characterEncoding = Config.SYSTEM_CHARACTER_ENCODING;
      return F.stringx(characterEncoding);
    }
  }

  private static class $SystemMemory extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      long totalMemory = Runtime.getRuntime().maxMemory();
      return F.ZZ(totalMemory);
    }
  }

  private static class $TemporaryDirectory extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      String tempDirectory = System.getProperty("java.io.tmpdir");
      if (tempDirectory == null) {
        return F.CEmptyString;
      }
      return F.stringx(tempDirectory);
    }
  }

  private static class $UserBaseDirectory extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      String userHome = System.getProperty("user.home");
      if (userHome == null) {
        return F.CEmptyString;
      }
      Path path = Paths.get(userHome, "Symja");
      return F.stringx(path.toString());
    }
  }

  private static class $UserName extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      String userName = System.getProperty("user.name");
      if (userName == null) {
        return F.stringx("");
      }
      return F.stringx(userName);
    }
  }

  private static class $Version extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return F.stringx(VERSION);
    }
  }

  private static class RecordSeparators extends AbstractSymbolEvaluator
      implements ISetValueEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      if (symbol.hasAssignedSymbolValue()) {
        return symbol.assignedValue();
      }

      return F.NIL;
    }

    @Override
    public IExpr evaluateSet(IExpr rightHandSide, boolean setDelayed, final EvalEngine engine) {
      S.RecordSeparators.assignValue(rightHandSide, setDelayed);
      return rightHandSide;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      super.setUp(newSymbol);
      S.RecordSeparators.assignValue(F.list(F.stringx("\n"), F.stringx("\r\n"), F.stringx("\r")),
          false);
    }
  }

  private static class WordSeparators extends AbstractSymbolEvaluator
      implements ISetValueEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      if (symbol.hasAssignedSymbolValue()) {
        return symbol.assignedValue();
      }

      return F.NIL;
    }

    @Override
    public IExpr evaluateSet(IExpr rightHandSide, boolean setDelayed, final EvalEngine engine) {
      S.WordSeparators.assignValue(rightHandSide, setDelayed);
      return rightHandSide;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      super.setUp(newSymbol);
      S.WordSeparators.assignValue(F.list(F.stringx(" "), F.stringx("\t")), false);
    }
  }

  /**
   *
   *
   * <blockquote>
   *
   * <p>
   * Catalan's constant
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Catalan%27s_constant">Wikipedia - Catalan's
   * constant</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; N(Catalan)
   * 0.915965594177219
   *
   * &gt;&gt; PolyGamma(1,1/4)
   * 8*Catalan+Pi^2
   * </pre>
   */
  private static class Catalan extends AbstractSymbolEvaluator implements IRealConstant {
    public static final double CATALAN = 0.91596559417721901505460351493238411077414937428167;

    @Override
    public COMPARE_TERNARY isIrrational() {
      return COMPARE_TERNARY.UNDECIDABLE;
    }

    @Override
    public COMPARE_TERNARY isAlgebraic() {
      return COMPARE_TERNARY.UNDECIDABLE;
    }

    @Override
    public COMPARE_TERNARY isTranscendental() {
      return COMPARE_TERNARY.UNDECIDABLE;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.CONSTANT);
    }

    @Override
    public IExpr numericEval(final ISymbol symbol, EvalEngine engine) {
      return F.num(CATALAN);
    }

    @Override
    public IExpr apfloatEval(ISymbol symbol, EvalEngine engine) {
      return F.num(engine.apfloatHelper().catalan());
    }

    @Override
    public double evalReal() {
      return CATALAN;
    }
  }

  /**
   *
   *
   * <pre>
   * ComplexInfinity
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * represents an infinite complex quantity of undetermined direction.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; 1 / ComplexInfinity
   * 0
   *
   * &gt;&gt; ComplexInfinity + ComplexInfinity
   * ComplexInfinity
   *
   * &gt;&gt; ComplexInfinity * Infinity
   * ComplexInfinity
   *
   * &gt;&gt; FullForm(ComplexInfinity)
   * DirectedInfinity()
   * </pre>
   */
  private static class ComplexInfinity extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return F.CComplexInfinity;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      // don't set CONSTANT attribute !
    }
  }

  /**
   *
   *
   * <pre>
   * Degree
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * the constant <code>Degree</code> converts angles from degree to <code>Pi/180</code> radians.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Degree_(angle)">Wikipedia - Degree (angle)</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Sin(30*Degree)
   * 1/2
   * </pre>
   *
   * <p>
   * Degree has the value of Pi / 180
   *
   * <pre>
   * &gt;&gt; Degree == Pi / 180
   * True
   *
   * &gt;&gt; Cos(Degree(x))
   * Cos(Degree(x))
   *
   * &gt;&gt; N(Degree)
   * 0.017453292519943295
   * </pre>
   */
  private static class Degree extends AbstractSymbolEvaluator implements IRealConstant {
    public static final double DEGREE = 0.017453292519943295769236907684886127134428718885417;

    @Override
    public COMPARE_TERNARY isIrrational() {
      return COMPARE_TERNARY.FALSE;
    }

    @Override
    public COMPARE_TERNARY isAlgebraic() {
      return COMPARE_TERNARY.FALSE;
    }

    @Override
    public COMPARE_TERNARY isTranscendental() {
      return COMPARE_TERNARY.FALSE;
    }

    /** Constant Degree converted to Pi/180 */
    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return Times(S.Pi, Power(F.ZZ(180), F.CN1));
    }

    @Override
    public IExpr apfloatEval(ISymbol symbol, EvalEngine engine) {
      // Pi / 180
      Apfloat pi = engine.apfloatHelper().pi();
      return F.num(pi.divide(new Apint(180)));
    }

    @Override
    public IExpr numericEval(final ISymbol symbol, EvalEngine engine) {
      return F.num(DEGREE);
    }

    @Override
    public double evalReal() {
      return DEGREE;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.CONSTANT);
    }
  }

  /**
   *
   *
   * <blockquote>
   *
   * <p>
   * Euler's constant E
   *
   * </blockquote>
   *
   * <p>
   * <strong>Note</strong>: the upper case identifier <code>E</code> is different from the lower
   * case identifier <code>e</code>.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Exp(1)
   * E
   *
   * &gt;&gt; N(E)
   * 2.718281828459045
   * </pre>
   */
  private static class E extends AbstractSymbolEvaluator implements IRealConstant {
    @Override
    public COMPARE_TERNARY isIrrational() {
      return COMPARE_TERNARY.TRUE;
    }

    @Override
    public COMPARE_TERNARY isAlgebraic() {
      return COMPARE_TERNARY.FALSE;
    }

    @Override
    public COMPARE_TERNARY isTranscendental() {
      return COMPARE_TERNARY.TRUE;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.CONSTANT);
    }

    @Override
    public IExpr numericEval(final ISymbol symbol, EvalEngine engine) {
      return F.num(Math.E);
    }

    @Override
    public IExpr apfloatEval(ISymbol symbol, EvalEngine engine) {
      return F.num(EvalEngine.getApfloat(engine).exp(Apfloat.ONE));
    }

    @Override
    public double evalReal() {
      return Math.E;
    }
  }

  /**
   * Euler gamma constant
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Euler–Mascheroni_constant">Euler– Mascheroni
   * constant</a>
   */
  private static class EulerGamma extends AbstractSymbolEvaluator implements IRealConstant {
    @Override
    public COMPARE_TERNARY isIrrational() {
      return COMPARE_TERNARY.UNDECIDABLE;
    }

    @Override
    public COMPARE_TERNARY isAlgebraic() {
      return COMPARE_TERNARY.UNDECIDABLE;
    }

    @Override
    public COMPARE_TERNARY isTranscendental() {
      return COMPARE_TERNARY.UNDECIDABLE;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.CONSTANT);
    }

    @Override
    public IExpr numericEval(final ISymbol symbol, EvalEngine engine) {
      return F.num(EULER_GAMMA);
    }

    @Override
    public IExpr apfloatEval(ISymbol symbol, EvalEngine engine) {
      return F.num(engine.apfloatHelper().euler());
    }

    @Override
    public double evalReal() {
      return EULER_GAMMA;
    }
  }

  /**
   *
   *
   * <blockquote>
   *
   * <p>
   * Glaisher constant.
   *
   * </blockquote>
   *
   * <p>
   * The <code>Glaisher</code> constant is named after mathematicians James Whitbread Lee Glaisher
   * and Hermann Kinkelin. Its approximate value is: <code>1.2824271291...</code>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Glaisher-Kinkelin_constant">Wikipedia -
   * Glaisher-Kinkelin constant</a>
   * </ul>
   */
  private static class Glaisher extends AbstractSymbolEvaluator implements IRealConstant {
    public static final double GLAISHER = 1.2824271291006226368753425688697917277676889273250;

    @Override
    public COMPARE_TERNARY isIrrational() {
      return COMPARE_TERNARY.UNDECIDABLE;
    }

    @Override
    public COMPARE_TERNARY isAlgebraic() {
      return COMPARE_TERNARY.UNDECIDABLE;
    }

    @Override
    public COMPARE_TERNARY isTranscendental() {
      return COMPARE_TERNARY.UNDECIDABLE;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.CONSTANT);
    }

    @Override
    public IExpr numericEval(final ISymbol symbol, EvalEngine engine) {
      return F.num(GLAISHER);
    }

    @Override
    public IExpr apfloatEval(ISymbol symbol, EvalEngine engine) {
      return F.num(engine.apfloatHelper().glaisher());
    }

    @Override
    public double evalReal() {
      return GLAISHER;
    }
  }

  private static class GoldenAngle extends AbstractSymbolEvaluator
      implements IRealConstant {
    public static final double GOLDEN_ANGLE = 2.3999632297286533222315555066336138531249990110581;

    @Override
    public COMPARE_TERNARY isIrrational() {
      return COMPARE_TERNARY.TRUE;
    }

    @Override
    public COMPARE_TERNARY isAlgebraic() {
      return COMPARE_TERNARY.FALSE;
    }

    @Override
    public COMPARE_TERNARY isTranscendental() {
      return COMPARE_TERNARY.TRUE;
    }

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      // (3-Sqrt(5))*Pi
      // return F.Times(F.Subtract(F.C3, F.Sqrt(F.C5)), F.Pi);
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.CONSTANT);
    }

    @Override
    public IExpr apfloatEval(ISymbol symbol, EvalEngine engine) {
      // (3-Sqrt(5)) * Pi
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloat(engine);
      return F.num(h.multiply(h.subtract(new Apfloat(3), h.sqrt(new Apfloat(5))), h.pi()));
    }

    @Override
    public IExpr numericEval(final ISymbol symbol, EvalEngine engine) {
      return F.num(GOLDEN_ANGLE);
    }

    @Override
    public double evalReal() {
      return GOLDEN_ANGLE;
    }
  }

  /**
   *
   *
   * <pre>
   * GoldenRatio
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * is the golden ratio.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Golden_ratio">Wikipedia: Golden ratio</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; N(GoldenRatio)
   * 1.618033988749895
   * </pre>
   */
  private static class GoldenRatio extends AbstractSymbolEvaluator
      implements IRealConstant {
    public static final double GOLDEN_RATIO = 1.6180339887498948482045868343656381177203091798058;

    @Override
    public COMPARE_TERNARY isIrrational() {
      return COMPARE_TERNARY.TRUE;
    }

    @Override
    public COMPARE_TERNARY isAlgebraic() {
      return COMPARE_TERNARY.TRUE;
    }

    @Override
    public COMPARE_TERNARY isTranscendental() {
      return COMPARE_TERNARY.FALSE;
    }

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      // 1/2*(1+5^(1/2))
      // return F.Times(F.C1D2, F.Plus(F.C1, F.Power(F.integer(5), F.C1D2)));
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.CONSTANT);
    }

    @Override
    public IExpr apfloatEval(ISymbol symbol, EvalEngine engine) {
      // (1/2)*(1+Sqrt(5))
      FixedPrecisionApfloatHelper h = EvalEngine.getApfloat(engine);
      return F.num(h.multiply(h.add(h.sqrt(new Apfloat(5)), Apfloat.ONE), new Aprational("1/2")));
    }

    @Override
    public IExpr numericEval(final ISymbol symbol, EvalEngine engine) {
      return F.num(GOLDEN_RATIO);
    }

    @Override
    public double evalReal() {
      return GOLDEN_RATIO;
    }
  }

  /**
   *
   *
   * <pre>
   * I
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * Imaginary unit - internally converted to the complex number <code>0+1*i</code>. <code>I
   * </code> represents the imaginary number <code>Sqrt(-1)</code>. <code>I^2</code> will be
   * evaluated to <code>-1</code>.
   *
   * </blockquote>
   *
   * <p>
   * <strong>Note</strong>: the upper case identifier <code>I</code> is different from the lower
   * case identifier <code>i</code>.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; I^2
   * -1
   *
   * &gt;&gt; (3+I)*(3-I)
   * 10
   * </pre>
   */
  private static class I extends AbstractSymbolEvaluator {

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.CONSTANT);
    }

    @Override
    public IExpr numericEval(final ISymbol symbol, EvalEngine engine) {
      return F.complexNum(0.0, 1.0);
    }

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return F.CI;
    }
  }

  /**
   *
   *
   * <pre>
   * Infinity
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * represents an infinite real quantity.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; 1 / Infinity
   * 0
   *
   * &gt;&gt; Infinity + 100
   * Infinity
   * </pre>
   *
   * <p>
   * Use <code>Infinity</code> in sum and limit calculations:
   *
   * <pre>
   * &gt;&gt; Sum(1/x^2, {x, 1, Infinity})
   * Pi ^ 2 / 6
   *
   * &gt;&gt; FullForm(Infinity)
   * DirectedInfinity(1)
   *
   * &gt;&gt; (2 + 3.5*I) / Infinity
   * 0.0"
   *
   * &gt;&gt; Infinity + Infinity
   * Infinity
   * </pre>
   *
   * <p>
   * Indeterminate expression <code>0</code> Infinity encountered.
   *
   * <pre>
   * &gt;&gt; Infinity / Infinity
   * Indeterminate
   * </pre>
   */
  private static class Infinity extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return F.CInfinity; // unaryAST1(F.DirectedInfinity, F.C1);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      // don't set CONSTANT attribute !
    }
  }

  private static class Now extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return DateObjectExpr.newInstance(LocalDateTime.now());
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      // don't set CONSTANT attribute !
    }
  }

  private static class Today extends AbstractSymbolEvaluator {

    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      LocalDateTime now = LocalDateTime.now();
      return DateObjectExpr
          .newInstance(LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0));
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      // don't set CONSTANT attribute !
    }
  }

  /**
   *
   *
   * <pre>
   * Khinchin
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * Khinchin's constant
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Khinchin%27s_constant">Wikipedia:Khinchin's
   * constant</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; N(Khinchin)
   * 2.6854520010653062
   * </pre>
   */
  private static class Khinchin extends AbstractSymbolEvaluator implements IRealConstant {
    public static final double KHINCHIN = 2.6854520010653064453097148354817956938203822939945;

    @Override
    public COMPARE_TERNARY isIrrational() {
      return COMPARE_TERNARY.UNDECIDABLE;
    }

    @Override
    public COMPARE_TERNARY isAlgebraic() {
      return COMPARE_TERNARY.UNDECIDABLE;
    }

    @Override
    public COMPARE_TERNARY isTranscendental() {
      return COMPARE_TERNARY.UNDECIDABLE;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.CONSTANT);
    }

    @Override
    public IExpr numericEval(final ISymbol symbol, EvalEngine engine) {
      return F.num(KHINCHIN);
    }

    @Override
    public double evalReal() {
      return KHINCHIN;
    }

    @Override
    public IExpr apfloatEval(ISymbol symbol, EvalEngine engine) {
      return F.num(engine.apfloatHelper().khinchin());
    }
  }

  /**
   *
   *
   * <pre>
   * Pi
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * is the constant <code>Pi</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; N(Pi)
   * 3.141592653589793
   * </pre>
   */
  private static class Pi extends AbstractSymbolEvaluator implements IRealConstant {
    @Override
    public COMPARE_TERNARY isIrrational() {
      return COMPARE_TERNARY.TRUE;
    }

    @Override
    public COMPARE_TERNARY isAlgebraic() {
      return COMPARE_TERNARY.FALSE;
    }

    @Override
    public COMPARE_TERNARY isTranscendental() {
      return COMPARE_TERNARY.TRUE;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.CONSTANT);
    }

    @Override
    public IExpr numericEval(final ISymbol symbol, EvalEngine engine) {
      return F.num(Math.PI);
    }

    @Override
    public IExpr apfloatEval(ISymbol symbol, EvalEngine engine) {
      return F.num(engine.apfloatHelper().pi());
    }

    @Override
    public double evalReal() {
      return Math.PI;
    }
  }
}
