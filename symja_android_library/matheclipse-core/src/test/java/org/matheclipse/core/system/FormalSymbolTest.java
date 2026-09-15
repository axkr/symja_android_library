package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.RuleCreationError;
import org.matheclipse.core.eval.util.WriterOutputStream;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.FormalSymbol;
import org.matheclipse.core.expression.KryoUtil;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Formal symbols like <code>\[FormalK]</code>: Protected <code>System`</code> symbols which never
 * hold a value. The expected results are the ones of Mathematica.
 */
public class FormalSymbolTest extends ExprEvaluatorTestCase {

  @Test
  public void testLocalizingConstructs() {
    check("Sum(\\[FormalK],{\\[FormalK],1,10})", //
        "55");
    check("Table(\\[FormalK]^2,{\\[FormalK],3})", //
        "{1,4,9}");
    check("Product(\\[FormalK],{\\[FormalK],5})", //
        "120");
    check("Sum(\\[FormalK]*\\[FormalJ],{\\[FormalK],1,3},{\\[FormalJ],1,\\[FormalK]})", //
        "25");
    check("Block({\\[FormalK]=3},\\[FormalK])", //
        "3");
    check("Module({\\[FormalK]=3},\\[FormalK])", //
        "3");
    check("With({\\[FormalK]=3},\\[FormalK])", //
        "3");
    check("Function(\\[FormalK],\\[FormalK]^2)[5]", //
        "25");
    // the initializer is evaluated outside of the block
    check("Block({\\[FormalK]=\\[FormalK]+1},\\[FormalK])", //
        "1+k");
    // the formal k is not the Global` k
    check("k=5;Sum(k,{\\[FormalK],1,3})", //
        "15");
    check("Sum(\\[FormalK],{\\[FormalK],1,n})", //
        "1/2*n*(1+n)");
    check("Sum(f(\\[FormalK]),{\\[FormalK],1,n})", //
        "Sum(f(k),{k,1,n})");
    assertNull(S.k.assignedValue());
    assertNull(S.j.assignedValue());
  }

  @Test
  public void testGlobalDefinitionsAreRefused() {
    assertEquals("Set: Symbol k is Protected.\n", //
        messages("\\[FormalK]=10"));
    check("\\[FormalK]", //
        "k");
    assertEquals("ClearAll: Symbol k is Protected.\n", //
        messages("ClearAll(\\[FormalK])"));
    assertEquals("TagSet: Tag k in f(k) is Protected.\n", //
        messages("\\[FormalK]/:f(\\[FormalK])=1"));
    check("Attributes(\\[FormalK])", //
        "{Protected}");
    check("Attributes(\\[FormalCapitalK])", //
        "{Protected}");
    assertFalse(S.k.hasAssignedSymbolValue());

    assertThrows(RuleCreationError.class, () -> S.k.assignValue(F.C1));
    assertThrows(RuleCreationError.class, () -> S.k.addAttributes(ISymbol.FLAT));
    assertThrows(RuleCreationError.class, () -> S.f1.assignValue(F.C1));
    assertEquals(ISymbol.PROTECTED, S.k.getAttributes());
  }

  @Test
  public void testParser() {
    IExpr held = evaluator.eval("Hold(\\[FormalK], \\[FormalCapitalC], k, C)");
    assertSame(S.k, held.first());
    assertSame(S.CSymbol, held.second());
    assertTrue(held.getAt(3) != S.k);
    assertSame(S.C, held.getAt(4));
    check("Context(\\[FormalK])", //
        "System`");
    check("\\[FormalK]===k", //
        "False");
  }

  @Test
  public void testPrinting() {
    check("InputForm(\\[FormalK]+\\[FormalCapitalE])", //
        "\\[FormalCapitalE] + \\[FormalK]");
    check("FullForm(\\[FormalK]^2)", //
        "Power(\\[FormalK], 2)");
    check("ToExpression(ToString(\\[FormalK],InputForm))===\\[FormalK]", //
        "True");
    check("InputForm(HoldForm(\\[FormalZ]_Integer:>\\[FormalZ]))", //
        "\\[FormalZ]_Integer:>\\[FormalZ]");
    check("TeXForm(\\[FormalCapitalE]+\\[FormalK])", //
        "E + k");

    // the built-in rules use the formal symbols, which print as their plain letters
    check("Sum(f(\\[FormalK]),{\\[FormalK],1,n}) /. \\[FormalK]->m", //
        "Sum(f(m),{m,1,n})");
  }

  @Test
  public void testBuiltinRules() {
    check("D(Tan(x),{x,n})", //
        "KroneckerDelta(-1+n)*Sec(x)^2+n*Sum(((-1)^k*Binomial(-1+n,k)*Binomial(2*k,j)*Sin(\n"
            + "1/2*n*Pi+2*(-j+k)*x))/((1+k)*Cos(x)^(2+2*k)*2^(2*k-n)*(-j+k)^(1-n)),{k,0,-1+n},{j,\n"
            + "0,-1+k})+KroneckerDelta(n)*Tan(x)");
    check("Simplify(D(Tan(x),{x,n}) /. n->4)", //
        "16*Sec(x)^4*Tan(x)+8*Sec(x)^2*Tan(x)^3");
    check("LerchPhi(z,s,5)", //
        "(-z-z^2/2^s-z^3/3^s-z^4/4^s+PolyLog(s,z))/z^5");
    check("FunctionExpand(FactorialPower(x,4))", //
        "(-3+x)*(-2+x)*(-1+x)*x");
    assertNull(S.k.assignedValue());
    assertNull(S.j.assignedValue());
  }

  @Test
  public void testJavaSerialization() throws Exception {
    IAST expr = F.Sum(F.Power(S.k, S.CSymbol), F.list(S.k, F.C1, S.n));
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
      oos.writeObject(expr);
    }
    try (ObjectInputStream ois =
        new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
      IAST copy = (IAST) ois.readObject();
      assertEquals(expr, copy);
      assertSame(S.k, copy.arg2().first());
    }
    assertSame(S.k, Context.SYSTEM.get("\uF80A"));
    assertNull(Context.SYSTEM.get("k"));
  }

  @Test
  public void testKryoSerialization() throws Exception {
    Kryo kryo = KryoUtil.initKryo();
    IAST expr = F.Sum(F.Power(S.k, S.CSymbol), F.list(S.k, F.C1, S.n));
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try (Output output = new Output(outputStream)) {
      kryo.writeClassAndObject(output, expr);
    }
    try (Input input = new Input(new ByteArrayInputStream(outputStream.toByteArray()))) {
      IAST copy = (IAST) kryo.readClassAndObject(input);
      assertEquals(expr, copy);
      assertSame(S.k, copy.arg2().first());
      assertTrue(copy.arg2().first() instanceof FormalSymbol);
    }
    // deserializing must not create a second System`k
    assertNull(Context.SYSTEM.get("k"));
  }

  private String messages(String input) {
    EvalEngine engine = evaluator.getEvalEngine();
    PrintStream previous = engine.getErrorPrintStream();
    StringWriter errorWriter = new StringWriter();
    PrintStream errors = new PrintStream(new WriterOutputStream(errorWriter));
    engine.setErrorPrintStream(errors);
    try {
      evaluator.eval(input);
    } finally {
      errors.flush();
      engine.setErrorPrintStream(previous);
    }
    return errorWriter.toString();
  }
}
