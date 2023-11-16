package org.matheclipse.core.fuzz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.FlowControlException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.form.tex.TeXFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.math.MathException;
import org.matheclipse.parser.client.operator.ASTNodeFactory;
import org.matheclipse.parser.client.operator.Precedence;
import junit.framework.TestCase;

import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class TeXTests {

  private static List<ASTNode> parseFileToList() {
    try {
      File file = new File("./data/harvest.sym");
      final BufferedReader f = new BufferedReader(new FileReader(file));
      final StringBuffer buff = new StringBuffer(1024);
      String line;
      while ((line = f.readLine()) != null) {
        buff.append(line);
        buff.append('\n');
        // Insert newlines to let the parser see that a new rule starts
        buff.append('\n');
        buff.append('\n');
      }
      f.close();
      String inputString = buff.toString();
      Parser p = new Parser(ASTNodeFactory.RELAXED_STYLE_FACTORY, true, true);
      return p.parsePackage(inputString);
      // return p.parsePackage(inputString);

      // assertEquals(obj.toString(),
      // "Plus[Plus[Times[-1, a], Times[-1, Times[b, Factorial2[c]]]], d]");
    } catch (Exception e) {
      e.printStackTrace();
      // assertEquals("", e.getMessage());
    }
    return null;
  }

  @Test
  public void testSmartFuzz() {
    boolean quietMode = true;
    EvalEngine engine = EvalEngine.get();
    List<ASTNode> node = parseFileToList();
    IExpr temp;
    TeXFormFactory fTeXFactory = new TeXFormFactory();
    OutputFormFactory fInputFactory = OutputFormFactory.get(true, false, 5, 7);
    fInputFactory.setInputForm(true);
    AST2Expr ast2Expr = new AST2Expr(engine.isRelaxedSyntax(), engine);
    IAST seedList = F.List( //
        F.complex(-0.5, 0.5), //
        F.complex(0.0, 0.5), //
        F.complex(0.0, -1.0), //
        F.complex(0.0, 1.0), //
        F.num(-0.5), //
        F.num(0.5), //
        F.num(Math.PI * (-0.5)), //
        F.num(Math.PI * 0.5), //
        F.num(-Math.PI), //
        F.num(Math.PI), //
        F.num(-Math.E), //
        F.num(Math.E), //
        S.True, //
        S.False, //
        F.assoc(F.CEmptyList), //
        F.assoc(F.List(F.Rule(F.x, F.y))), //
        F.CEmptyList, //
        F.List(F.Rule(F.C1, F.C0)), //
        F.List(F.Rule(F.x, F.CN1)), //
        F.C0, //
        F.C1, //
        F.CN1, //
        F.C2, //
        F.CN2, //
        F.CN10, //
        F.CN1D2, //
        F.C1D2, //
        F.CNI, //
        F.CI, //
        // F.ZZ(Integer.MIN_VALUE), //
        F.CInfinity, //
        F.CNInfinity, //
        F.Null, //
        F.Power(F.x, F.C2), //
        // F.Indeterminate, //
        F.ComplexInfinity, //
        F.x_, //
        F.y_, //
        F.C1DSqrt5, //
        F.Slot1, //
        F.stringx(""), //
        F.stringx("\uffff"), //
        F.Subtract(F.C1, F.C1));
    int counter = 0;
    ThreadLocalRandom random = ThreadLocalRandom.current();
    for (int j = 1; j < 10000; j++) {
      int i = 0;
      while (i < node.size()) {
        temp = ast2Expr.convert(node.get(i++));
        if (temp.isAST() && temp.size() > 1) {
          final StringBuilder buf = new StringBuilder();
          int seedIndex = random.nextInt(1, seedList.size());
          IExpr seed = seedList.get(seedIndex);

          IASTMutable mutant = ((IAST) temp).copy();
          int randomIndex = random.nextInt(1, mutant.size());
          mutant.set(randomIndex, seed);

          for (int k = 0; k < 1; k++) {
            seedIndex = random.nextInt(1, seedList.size());
            seed = seedList.get(seedIndex);
            randomIndex = random.nextInt(1, mutant.size());
            mutant.set(randomIndex, seed);
          }

          engine.init();
          engine.setQuietMode(quietMode);
          engine.setRecursionLimit(256);
          engine.setIterationLimit(1000);
          final String mutantStr = fInputFactory.toString(mutant);
          try {
            // System.out.println(">> " + mutantStr);
            // System.out.print(".");
            if (counter++ > 80) {
              // System.out.println("");
              counter = 0;
              System.out.flush();
              System.err.flush();
            }
            // eval.eval(mutantStr);
            fTeXFactory.convert(buf, mutant, Precedence.NO_PRECEDENCE);
            System.out.println(buf.toString());
          } catch (FlowControlException mex) {
            if (!quietMode) {
              System.err.println(mutantStr);
              mex.printStackTrace();
              System.err.println();
            }
          } catch (SyntaxError se) {
            if (!quietMode) {
              System.err.println(mutantStr);
              se.printStackTrace();
              System.err.println();
            }
            // fail();
          } catch (MathException mex) {
            System.err.println(mutantStr);
            mex.printStackTrace();
            System.err.println();
            fail();
          } catch (RuntimeException rex) {
            System.err.println(mutantStr);
            rex.printStackTrace();
            fail();
          } catch (Error rex) {
            System.err.println(mutantStr);
            if (rex instanceof StackOverflowError) {
              System.err.println("java.lang.StackOverflowError");
              rex.printStackTrace();
            } else {
              rex.printStackTrace();
              fail();
            }
          }
        }
      }
    }
    // return result;
  }

  @Before
  public void setUp() throws Exception {
    // Config.FUZZ_TESTING = true;
    Config.UNPROTECT_ALLOWED = false;
    // wait for initializing of Integrate() rules:
    F.await();
  }
}
