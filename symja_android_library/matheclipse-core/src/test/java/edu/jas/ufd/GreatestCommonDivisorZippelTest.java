package edu.jas.ufd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Random;
import org.junit.jupiter.api.Test;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrderByName;

/**
 * Tests {@link GreatestCommonDivisorZippel}, the sparse interpolation gcd.
 *
 * <p>
 * In the package of the class under test on purpose: {@link GreatestCommonDivisorZippel#gcd} falls
 * back to a dense algorithm whenever the sparse one gives up, so a test which only went through
 * <code>gcd()</code> would still pass if the sparse path never ran at all. The tests below call the
 * protected {@link GreatestCommonDivisorZippel#sparseGcd} directly and assert that it produced an
 * answer itself.
 */
public class GreatestCommonDivisorZippelTest {

  private static final ModLongRing MOD = new ModLongRing(32003, true);

  private final GreatestCommonDivisorZippel<ModLong> zippel =
      new GreatestCommonDivisorZippel<ModLong>();

  /**
   * The reference. Brown's algorithm is modular as well but interpolates densely, so it is
   * independent of the code under test and, unlike a polynomial remainder sequence, still finishes
   * on products in five or six variables.
   */
  private final GreatestCommonDivisorModEval<ModLong> dense =
      new GreatestCommonDivisorModEval<ModLong>();

  private static GenPolynomialRing<ModLong> ring(int nvar) {
    String[] names = new String[nvar];
    for (int i = 0; i < nvar; i++) {
      names[i] = "x" + i;
    }
    return new GenPolynomialRing<ModLong>(MOD, nvar, TermOrderByName.INVLEX, names);
  }

  private static GenPolynomial<ModLong> random(GenPolynomialRing<ModLong> fac, int terms, int deg,
      Random rnd) {
    GenPolynomial<ModLong> p = fac.getZERO().copy();
    for (int t = 0; t < terms; t++) {
      long[] e = new long[fac.nvar];
      for (int i = 0; i < fac.nvar; i++) {
        e[i] = rnd.nextInt(deg + 1);
      }
      p.doAddTo(MOD.fromInteger(1 + rnd.nextInt(32002)), ExpVector.create(e));
    }
    return p;
  }

  private static String normal(GenPolynomial<ModLong> p) {
    return p == null ? "null" : (p.isZERO() ? "0" : p.monic().toString());
  }

  /** The sparse path itself agrees with the dense one, and is really the path taken. */
  private void checkSparse(int nvar, int rounds, int terms, int deg, long seed) {
    GenPolynomialRing<ModLong> fac = ring(nvar);
    Random rnd = new Random(seed);
    int used = 0;
    for (int round = 0; round < rounds; round++) {
      GenPolynomial<ModLong> g = random(fac, terms, deg, rnd);
      GenPolynomial<ModLong> p = random(fac, terms, deg, rnd);
      GenPolynomial<ModLong> q = random(fac, terms, deg, rnd);
      if (g.isZERO() || p.isZERO() || q.isZERO()) {
        continue;
      }
      GenPolynomial<ModLong> a = g.multiply(p).abs();
      GenPolynomial<ModLong> b = g.multiply(q).abs();
      GenPolynomial<ModLong> got = zippel.sparseGcd(a, b);
      if (got == null) {
        continue; // gave up, gcd() would fall back - not a wrong answer
      }
      used++;
      assertEquals(normal(dense.gcd(a, b)), normal(got),
          "gcd of " + a + " and " + b + " in " + nvar + " variables");
    }
    assertTrue(used > rounds / 2,
        "the sparse path gave up too often to be tested: " + used + " of " + rounds);
  }

  @Test
  public void testTwoVariables() {
    checkSparse(2, 40, 3, 3, 11L);
  }

  @Test
  public void testThreeVariables() {
    checkSparse(3, 40, 3, 3, 22L);
  }

  @Test
  public void testFourVariables() {
    checkSparse(4, 30, 4, 3, 33L);
  }

  @Test
  public void testSixVariables() {
    checkSparse(6, 15, 3, 2, 44L);
  }

  @Test
  public void testStructured() {
    GenPolynomialRing<ModLong> fac = ring(3);
    String[][] cases = { //
        {"(x0 - x1) * (x0 + x2)", "(x0 - x1) * (x1 + x2)"}, //
        {"(x0^2 + x1*x2 + 1) * (x0 + 1)", "(x0^2 + x1*x2 + 1) * (x2 + 5)"}, //
        {"x0^3 * x1^2 * x2", "x0^2 * x1^3 * x2^2"}, //
        {"(x0 + x1 + x2)^3", "(x0 + x1 + x2)^2 * (x0 - x2)"}, //
        {"(x1*x2*x0^4 + x0 + x1^5)*(x0+x1)", "(x1*x2*x0^4 + x0 + x1^5)*(x0-x2)"}, //
        // the gcd is not monic in the main variable, which is what the gamma scaling is for
        {"(x1*x0^2 + x2)*(x0 + x1)", "(x1*x0^2 + x2)*(x0 + x2)"}, //
    };
    for (String[] c : cases) {
      GenPolynomial<ModLong> a = fac.parse(c[0]);
      GenPolynomial<ModLong> b = fac.parse(c[1]);
      // the sparse path directly: with three variables gcd() would hand these to the dense
      // algorithm, see MIN_VARIABLES, and the test would no longer be about this class
      GenPolynomial<ModLong> got = zippel.sparseGcd(a.abs(), b.abs());
      assertNotNull(got, "the sparse algorithm gave up on " + c[0] + " , " + c[1]);
      assertEquals(normal(dense.gcd(a, b)), normal(got), c[0] + " , " + c[1]);
    }
  }

  /**
   * The engine picks the dense algorithm for few variables itself, because
   * {@link GCDFactory} cannot see how many variables the arguments will have.
   */
  @Test
  public void testDispatch() {
    for (int nvar = 1; nvar <= 6; nvar++) {
      GenPolynomialRing<ModLong> fac = ring(nvar);
      GenPolynomial<ModLong> a = fac.getONE().multiply(MOD.fromInteger(2));
      assertEquals(nvar >= 4, zippel.isWorthIt(a, a), "dispatch for " + nvar + " variables");
    }
  }

  /** Cases which have to be recognized before any interpolation starts. */
  @Test
  public void testDegenerate() {
    GenPolynomialRing<ModLong> fac = ring(3);
    GenPolynomial<ModLong> a = fac.parse("x0*x1 + x2");
    assertEquals(normal(a), normal(zippel.gcd(a, fac.getZERO())));
    assertEquals(normal(a), normal(zippel.gcd(fac.getZERO(), a)));
    assertEquals(normal(a), normal(zippel.gcd(a, a)));
    assertTrue(zippel.gcd(a, fac.getONE()).isONE());
    // coprime
    assertTrue(zippel.gcd(fac.parse("x0*x1*x2 + 1"), fac.parse("x0*x1*x2 - 1")).isONE());
    // a gcd which is a pure content, no dependence on the main variable
    GenPolynomial<ModLong> content = fac.parse("x1 + x2");
    assertEquals(normal(content),
        normal(zippel.gcd(content.multiply(fac.parse("x0 + 1")),
            content.multiply(fac.parse("x0 + 2")))));
  }

  /**
   * Resultants are not computed by sparse interpolation and would throw
   * <code>UnsupportedOperationException</code> from the base class without the delegation.
   */
  @Test
  public void testResultantStillWorks() {
    GenPolynomialRing<ModLong> fac = ring(3);
    GenPolynomial<ModLong> a = fac.parse("x0^2 + x1");
    GenPolynomial<ModLong> b = fac.parse("x0 + x2");
    assertEquals(dense.resultant(a, b).toString(), zippel.resultant(a, b).toString());
  }

  /** The Chinese remainder lift to Z[x_1, ..., x_n] with the sparse algorithm for each prime. */
  @Test
  public void testOverTheIntegers() {
    GreatestCommonDivisorAbstract<BigInteger> sparse =
        new GreatestCommonDivisorModular<ModLong>(new GreatestCommonDivisorZippel<ModLong>());
    GreatestCommonDivisorAbstract<BigInteger> reference = new GreatestCommonDivisorSubres<BigInteger>();
    GenPolynomialRing<BigInteger> fac =
        new GenPolynomialRing<BigInteger>(new BigInteger(), 3, TermOrderByName.INVLEX,
            new String[] {"x0", "x1", "x2"});
    String[][] cases = { //
        {"(x0 - x1) * (x0 + x2)", "(x0 - x1) * (x1 + x2)"}, //
        {"(3*x0^2 + x1*x2 + 7) * (x0 + 11)", "(3*x0^2 + x1*x2 + 7) * (x2 + 5)"}, //
        {"(x0 + x1 + x2)^3", "(x0 + x1 + x2)^2 * (x0 - x2)"}, //
        {"(x1*x0^2 + 13*x2)*(x0 + x1)", "(x1*x0^2 + 13*x2)*(x0 + x2)"}, //
    };
    for (String[] c : cases) {
      GenPolynomial<BigInteger> a = fac.parse(c[0]);
      GenPolynomial<BigInteger> b = fac.parse(c[1]);
      assertEquals(reference.gcd(a, b).abs().toString(), sparse.gcd(a, b).abs().toString(),
          c[0] + " , " + c[1]);
    }
  }

  /** The transposed Vandermonde solver on its own. */
  @Test
  public void testTransposedVandermonde() {
    // sum_i node_i^k c_i = rhs_k for k = 1..t, with the c_i chosen in advance
    java.util.List<ModLong> nodes = new java.util.ArrayList<ModLong>();
    java.util.List<ModLong> want = new java.util.ArrayList<ModLong>();
    for (long v : new long[] {2, 3, 5, 7}) {
      nodes.add(MOD.fromInteger(v));
    }
    for (long v : new long[] {11, 13, 17, 19}) {
      want.add(MOD.fromInteger(v));
    }
    java.util.List<ModLong> rhs = new java.util.ArrayList<ModLong>();
    for (int k = 1; k <= nodes.size(); k++) {
      ModLong sum = MOD.getZERO();
      for (int i = 0; i < nodes.size(); i++) {
        sum = sum.sum(edu.jas.structure.Power.<ModLong>positivePower(nodes.get(i), k)
            .multiply(want.get(i)));
      }
      rhs.add(sum);
    }
    java.util.List<ModLong> got = zippel.solveTransposedVandermonde(nodes, rhs, MOD);
    assertNotNull(got);
    assertEquals(want.toString(), got.toString());
  }

  /** {@link JASConfig#USE_SPARSE_GCD} really selects the sparse engine. */
  @Test
  public void testFactorySelection() {
    boolean saved = edu.jas.kern.JASConfig.USE_SPARSE_GCD;
    try {
      edu.jas.kern.JASConfig.USE_SPARSE_GCD = false;
      assertFalse(GCDFactory.getImplementation(MOD) instanceof GreatestCommonDivisorZippel);
      edu.jas.kern.JASConfig.USE_SPARSE_GCD = true;
      assertTrue(GCDFactory.getImplementation(MOD) instanceof GreatestCommonDivisorZippel);
      // over Q the dense algorithm stays selected, see the note in GCDFactory
      assertFalse(GCDFactory.getImplementation(BigRational.ZERO) instanceof GreatestCommonDivisorZippel);
    } finally {
      edu.jas.kern.JASConfig.USE_SPARSE_GCD = saved;
    }
  }
}
