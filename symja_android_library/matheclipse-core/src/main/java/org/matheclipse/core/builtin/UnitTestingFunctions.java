package org.matheclipse.core.builtin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.util.List;
import java.util.function.Consumer;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.TestReportObjectExpr;
import org.matheclipse.core.expression.data.TestResultObjectExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.FEConfig;
import org.matheclipse.parser.client.ast.ASTNode;

import com.google.common.io.Files;
import com.google.common.io.Resources;

public class UnitTestingFunctions {
  static final boolean DEBUG = false;

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.TestReport.setEvaluator(new TestReport());
      S.VerificationTest.setEvaluator(new VerificationTest());
    }
  }

  private static class TestReport extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.isFileSystemEnabled(engine)) {
        if (ast.arg1().isList()) {
          IAST listÜfVerificationTest = (IAST) ast.arg1();
          if (listÜfVerificationTest.forAll(x -> x.isAST(S.VerificationTest))) {
            IAssociation testResults = F.assoc(listÜfVerificationTest.size());
            int testCounter = 1;
            for (int j = 1; j < listÜfVerificationTest.size(); j++) {
              IAST verificationTest = (IAST) listÜfVerificationTest.get(j);
              IExpr result = engine.evaluate(verificationTest);
              if (result instanceof TestResultObjectExpr) {
                testResults.appendRule(F.Rule(F.ZZ(testCounter++), result));
              }
            }
            IAssociation testReportObject = F.assoc(10);
            testReportObject.appendRule(F.Rule("TestResults", testResults));
            return TestReportObjectExpr.newInstance(testReportObject);
          }
          return F.NIL;
        }
        if (!(ast.arg1() instanceof IStringX)) {
          return IOFunctions.printMessage(ast.topHead(), "string", F.List(), engine);
        }
        String arg1 = ast.arg1().toString();
        if (arg1.startsWith("https://")
            || //
            arg1.startsWith("http://")) {
          URL url;
          try {
            url = new URL(arg1);
            return getURL(url, ast, engine);
          } catch (MalformedURLException mue) {
            if (FEConfig.SHOW_STACKTRACE) {
              mue.printStackTrace();
            }
            // Cannot open `1`.
            return IOFunctions.printMessage(ast.topHead(), "noopen", F.List(ast.arg1()), engine);
          }
        }
        File file = new File(arg1);

        if (file.exists()) {
          return getFile(file, ast, engine);
        } else {
          file = FileSystems.getDefault().getPath(arg1.toString()).toAbsolutePath().toFile();
          if (file.exists()) {
            return getFile(file, ast, engine);
          }
        }
        // Cannot open `1`.
        return IOFunctions.printMessage(ast.topHead(), "noopen", F.List(ast.arg1()), engine);
      }
      return F.NIL;
    }

    private static IExpr getFile(File file, IAST ast, EvalEngine engine) {
      // boolean packageMode = engine.isPackageMode();
      try {
        // engine.setPackageMode(true);
        String str = Files.asCharSource(file, Charset.defaultCharset()).read();
        return runTests(engine, str);
      } catch (IOException e) {
        if (FEConfig.SHOW_STACKTRACE) {
          e.printStackTrace();
        }
        // Cannot open `1`.
        return IOFunctions.printMessage(ast.topHead(), "noopen", F.List(ast.arg1()), engine);
      } finally {
        // engine.setPackageMode(packageMode);
      }
    }

    private static IExpr getURL(URL url, IAST ast, EvalEngine engine) {
      // boolean packageMode = engine.isPackageMode();
      try {
        // engine.setPackageMode(true);
        String str = Resources.toString(url, StandardCharsets.UTF_8);
        return runTests(engine, str);
      } catch (IOException e) {
        if (FEConfig.SHOW_STACKTRACE) {
          e.printStackTrace();
        }
        // Cannot open `1`.
        return IOFunctions.printMessage(ast.topHead(), "noopen", F.List(ast.arg1()), engine);
      } finally {
        // engine.setPackageMode(packageMode);
      }
    }

    private static IExpr runTests(EvalEngine engine, String str) throws IOException {
      final List<ASTNode> node = FileFunctions.parseReader(str, engine);
      IAssociation testResults = evaluatePackage(node, engine);
      IAssociation testResultObject = F.assoc(node.size());
      testResultObject.appendRule(F.Rule("TestResults", testResults));
      return TestReportObjectExpr.newInstance(testResultObject);
    }

    public static IAssociation evaluatePackage(final List<ASTNode> node, final EvalEngine engine) {
      IExpr temp;
      int i = 0;
      AST2Expr ast2Expr = new AST2Expr(engine.isRelaxedSyntax(), engine);
      IExpr result = S.Null;
      IAssociation assoc = F.assoc(node.size());
      int testCounter = 1;
      while (i < node.size()) {
        temp = ast2Expr.convert(node.get(i++));
        if (temp.isAST(S.CompoundExpression)) {
          IAST compoundExpression = (IAST) temp;
          for (int j = 1; j < compoundExpression.size(); j++) {
            temp = compoundExpression.get(j);
            result = engine.evaluate(temp);
            if (result instanceof TestResultObjectExpr) {
              assoc.appendRule(F.Rule(F.ZZ(testCounter++), result));
            }
          }
        } else {
          result = engine.evaluate(temp);
          if (result instanceof TestResultObjectExpr) {
            assoc.appendRule(F.Rule(F.ZZ(testCounter++), result));
          }
        }
      }
      return assoc;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDFIRST);
    }
  }

  private static class VerificationTest extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr sameTest = S.SameQ;
      IExpr testID = S.None;
      IExpr actualOutput = S.None;
      IExpr expectedOutput = S.True;
      int size = ast.size();
      final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
      if (options.isInvalidPosition()) {
        size = options.getInvalidPosition() + 1;
      }
      IExpr option = options.getOption(S.SameTest);
      if (option.isPresent()) {
        sameTest = option;
      }
      option = options.getOption(S.TestID);
      if (option.isPresent()) {
        testID = engine.evaluate(option);
        if (DEBUG) {
          System.out.print("\n\n>>>" + testID.toString());
        }
      }

      IExpr input = ast.arg1();
      Consumer<IExpr> out = Config.PRINT_OUT;
      try {
        out.accept(input);
        actualOutput = engine.evaluate(input);

      } catch (Exception ex) {
        if (DEBUG) {
          ex.printStackTrace();
        }
        actualOutput = S.None;
      }

      if (size > 2) {
        expectedOutput = ast.arg2();
      }

      try {
        IExpr tempActualOutput = engine.evaluate(actualOutput);
        IExpr tempExpectedOutput = engine.evaluate(expectedOutput);
        IExpr result =
            engine.evaluate(F.binaryAST2(sameTest, tempActualOutput, tempExpectedOutput));

        IAssociation assoc = F.assoc(12);
        if (result.isTrue()) {
          success(assoc);
        } else {
          if (sameTest.equals(S.SameQ)) {
            String actualOutputFullForm = tempActualOutput.fullFormString();
            String expectedOutputFullForm = expectedOutput.fullFormString();
            if (actualOutputFullForm.equals(expectedOutputFullForm)) {
              success(assoc);
            } else {
              boolean test = tempActualOutput.equals(tempExpectedOutput);
              if (!test) {
                failure(assoc);
              } else {
                success(assoc);
              }
            }
          } else {
            failure(assoc);
          }
        }
        assoc.appendRule(F.Rule("Input", F.HoldForm(input)));
        assoc.appendRule(F.Rule("ExpectedOutput", F.HoldForm(expectedOutput)));
        assoc.appendRule(F.Rule("ActualOutput", F.HoldForm(actualOutput)));
        assoc.appendRule(F.Rule("TestID", testID));
        return TestResultObjectExpr.newInstance(assoc);
      } catch (Exception ex) {
        if (FEConfig.SHOW_STACKTRACE) {
          ex.printStackTrace();
        }
      }
      return F.NIL;
    }

    private static void failure(IAssociation assoc) {
      assoc.appendRule(F.Rule("Outcome", "Failure"));
      if (DEBUG) {
        System.out.print(" - Failure");
      }
    }

    private static void success(IAssociation assoc) {
      assoc.appendRule(F.Rule("Outcome", "Success"));
      if (DEBUG) {
        System.out.print(" - Success");
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALLCOMPLETE);
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private UnitTestingFunctions() {}
}
