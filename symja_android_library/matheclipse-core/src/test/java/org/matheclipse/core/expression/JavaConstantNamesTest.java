package org.matheclipse.core.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.hipparchus.util.ArithmeticUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.util.SourceCodeProperties;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;

/**
 * The named constants of {@link F} which {@link IExpr#internalJavaString} prints for the generated
 * rule classes, their serialization IDs and the small number caches.
 */
public class JavaConstantNamesTest {

  private static final SourceCodeProperties NO_PREFIX =
      SourceCodeProperties.of(false, false, SourceCodeProperties.Prefix.NONE, false);

  /** Number of entries of {@link S#COMMON_IDS} before the named constants were appended. */
  private static final int OLD_COMMON_IDS_LENGTH = 167;

  /** The constants appended to {@link S#COMMON_IDS}, in their order. */
  private static final List<IExpr> APPENDED_IDS = Arrays.asList(F.C12, F.C15, F.C16, F.C18, F.C20,
      F.C27, F.C35, F.C100, F.C1000, F.C3D2, F.CN3D2, F.C2D3, F.CN2D3, F.C3D4, F.CN3D4, F.C5D2,
      F.CN5D2, F.C7D2, F.C1D5, F.CN1D5, F.C1D6, F.CN1D6, F.C1D8, F.CN1D8, F.C5D8, F.CN5D4, F.C1D12,
      F.CID2, F.CNID2, F.CID4, F.C2I, F.CIInfinity, F.CNIInfinity, F.CSqrtPi, F.C1DSqrtPi,
      F.CSqrtPiHalf, F.CSqrt2DPi, F.CNPi, F.CN2Pi, F.C2Pi, F.CNPiHalf, F.CPiHalf, F.CNPiThird,
      F.CPiThird, F.CNPiQuarter, F.CPiQuarter, F.CPiFifth, F.CPiSixth, F.CPiEighth, F.CPiTwelfth,
      F.C1DPi, F.CPiSqr, F.CIPi, F.CIPiHalf, F.C1PSqrt3, F.C1MSqrt3, F.CN1PSqrt3, F.C1PSqrt5,
      F.C1MSqrt5, F.CN1PSqrt5, F.CN1MSqrt5, F.CCbrt2, F.CCbrt4, F.CLog2, F.CDerivative1);

  @BeforeEach
  public void setUp() {
    F.initSymja();
  }

  @Test
  public void testEveryNameIsAFieldOfF() throws Exception {
    Map<String, IExpr> all = JavaConstantNames.all();
    assertTrue(all.size() > 100, "table size " + all.size());
    for (Map.Entry<String, IExpr> entry : all.entrySet()) {
      String name = entry.getKey();
      IExpr field = (IExpr) F.class.getField(name).get(null);
      assertEquals(entry.getValue(), field, name);
      assertEquals(name, field.internalJavaString(NO_PREFIX, 0, x -> null).toString());
    }
  }

  @Test
  public void testEvaluatedInputPrintsName() {
    ExprEvaluator evaluator = new ExprEvaluator();
    String[][] inputs = {{"1/Pi", "C1DPi"}, {"Pi^2", "CPiSqr"}, {"I*Pi", "CIPi"},
        {"I/2*Pi", "CIPiHalf"}, {"Pi/5", "CPiFifth"}, {"Pi/6", "CPiSixth"}, {"Pi/8", "CPiEighth"},
        {"Pi/12", "CPiTwelfth"}, {"1/Sqrt(Pi)", "C1DSqrtPi"}, {"Sqrt(Pi/2)", "CSqrtPiHalf"},
        {"Sqrt(2/Pi)", "CSqrt2DPi"}, {"1+Sqrt(3)", "C1PSqrt3"}, {"1-Sqrt(3)", "C1MSqrt3"},
        {"-1+Sqrt(3)", "CN1PSqrt3"}, {"1+Sqrt(5)", "C1PSqrt5"}, {"1-Sqrt(5)", "C1MSqrt5"},
        {"-1+Sqrt(5)", "CN1PSqrt5"}, {"-1-Sqrt(5)", "CN1MSqrt5"}, {"2^(1/3)", "CCbrt2"},
        {"2^(2/3)", "CCbrt4"}, {"Log(2)", "CLog2"}, {"Derivative(1)", "CDerivative1"},
        {"I*Infinity", "CIInfinity"}, {"-I*Infinity", "CNIInfinity"}, {"I/2", "CID2"},
        {"-I/2", "CNID2"}, {"I/4", "CID4"}, {"2*I", "C2I"}, {"-3/2", "CN3D2"}, {"5/8", "C5D8"},
        {"1/12", "C1D12"}, {"27", "C27"}, {"1000", "C1000"}, {"Sqrt(3)", "CSqrt3"},
        {"-Pi/4", "CNPiQuarter"},
        // numbers and structures without a name are printed as before
        {"7/3", "QQ(7L,3L)"}, {"11", "ZZ(11L)"}, {"Pi/7", "Times(QQ(1L,7L),Pi)"},
        {"1+Sqrt(7)", "Plus(C1,CSqrt7)"}, {"3*I", "CC(0L,1L,3L,1L)"}};
    for (String[] input : inputs) {
      IExpr result = evaluator.eval(input[0]);
      assertEquals(input[1], result.internalJavaString(NO_PREFIX, 0, x -> null).toString(),
          input[0]);
    }
    assertEquals("F.Plus(F.C3D2,F.Power(F.x,F.C2D3),F.Times(F.CSqrtPiHalf,F.y))",
        evaluator.eval("JavaForm(3/2+x^(2/3)+Sqrt(Pi/2)*y, Prefix->True)").toString());
  }

  @Test
  public void testCommonIdsAreAppended() {
    assertEquals(OLD_COMMON_IDS_LENGTH + APPENDED_IDS.size(), S.COMMON_IDS.length);
    // the old entries keep their index
    assertSame(F.CN1, S.COMMON_IDS[0]);
    assertSame(F.Slot2, S.COMMON_IDS[47]);
    assertSame(F.G_DEFAULT, S.COMMON_IDS[OLD_COMMON_IDS_LENGTH - 1]);
    for (int i = 0; i < APPENDED_IDS.size(); i++) {
      IExpr constant = APPENDED_IDS.get(i);
      assertSame(constant, S.COMMON_IDS[OLD_COMMON_IDS_LENGTH + i]);
      Short id = S.GLOBAL_IDS_MAP.get(constant);
      assertNotNull(id, constant.toString());
      assertEquals(S.EXPRID_MAX_BUILTIN_LENGTH + OLD_COMMON_IDS_LENGTH + i, id.intValue());
      assertSame(constant, ExprID.resolve(id));
    }
    assertTrue(S.EXPRID_MAX_BUILTIN_LENGTH + S.COMMON_IDS.length < Short.MAX_VALUE);
  }

  @Test
  public void testAppendedIdsSerialize() throws Exception {
    for (IExpr constant : APPENDED_IDS) {
      // WXF internal form writes the ID
      assertSame(constant, WL.deserializeInternal(WL.serializeInternal(constant)),
          constant.toString());
      // Java serialization writes the ID of an argument in the compact form of AST and AST0
      IASTAppendable list = F.ast(S.List, 2);
      list.append(constant);
      list.append(F.x);
      IAST copy = (IAST) javaCopy(list);
      assertEquals(list, copy);
      assertSame(constant, copy.arg1(), constant.toString());
    }
  }

  @Test
  public void testFractionCache() {
    for (int d = AbstractFractionSym.LOW_DENOM; d <= AbstractFractionSym.HIGH_DENOM; d++) {
      for (int n = AbstractFractionSym.LOW_NUMER; n <= AbstractFractionSym.HIGH_NUMER; n++) {
        if (n == 0 || n == d) {
          continue;
        }
        IFraction fraction = AbstractFractionSym.valueOf(n, d);
        int gcd = ArithmeticUtils.gcd(n, d);
        assertEquals(n / gcd, fraction.toBigNumerator().intValueExact(), n + "/" + d);
        assertEquals(d / gcd, fraction.toBigDenominator().intValueExact(), n + "/" + d);
        if (d / gcd >= AbstractFractionSym.LOW_DENOM) {
          assertSame(AbstractFractionSym.valueOf(n / gcd, d / gcd), fraction, n + "/" + d);
        }
      }
    }
    assertSame(F.C1D2, AbstractFractionSym.valueOf(16, 32));
    assertSame(F.CN3D4, AbstractFractionSym.valueOf(-24, 32));
  }

  @Test
  public void testIntegerCache() {
    for (int i = IntegerSym.LOW; i <= IntegerSym.HIGH; i++) {
      assertEquals(i, AbstractIntegerSym.valueOf(i).toIntDefault());
      assertSame(AbstractIntegerSym.valueOf(i), AbstractIntegerSym.valueOf(i));
    }
    assertSame(F.C27, AbstractIntegerSym.valueOf(27));
  }

  private static Object javaCopy(Object object) throws IOException, ClassNotFoundException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
      out.writeObject(object);
    }
    try (ObjectInputStream in =
        new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
      return in.readObject();
    }
  }
}
