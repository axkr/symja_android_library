package org.matheclipse.core.system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.S;

/**
 * Inputs at the edge of what can be computed: deeply nested chains, sizes which no memory can
 * hold, special functions at huge parameters, duplicate local variables and control flow in the
 * bounds of an iterator.
 */
public class ExtremeInputTest extends ExprEvaluatorTestCase {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    try {
      // the integration rules are installed in the background; the epoch move at the end of
      // their installation makes every stored expression evaluate once more
      S.Integrate.getEvaluator().await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  @Test
  public void testBlockDuplicateLocal() {
    // the second local used to be saved twice and the outer value of y lost
    check("y=7;{Block({y=1,y=2},y),y}", //
        "{Block({y=1,y=2},y),7}");
    check("Block({y,z=3,y},y)", //
        "Block({y,z=3,y},y)");
  }

  @Test
  public void testThrowInIteratorBound() {
    check("Catch(Do(k,{k,Throw(21)}))", //
        "21");
    check("Catch(Table(k,{k,2,Throw(22)}))", //
        "22");
    check("Catch(Table(k,{k,{1,Throw(23)}}))", //
        "23");
    check("Catch(Do(k,{k,1,5,Throw(24)}))", //
        "24");
  }

  @Test
  public void testTuplesOfLongLength() {
    check("Length(Tuples({u},20000))", //
        "1");
    check("Tuples({p,q},3)", //
        "{{p,p,p},{p,p,q},{p,q,p},{p,q,q},{q,p,p},{q,p,q},{q,q,p},{q,q,q}}");
    check("Tuples(f(x,y),2)", //
        "{f(x,x),f(x,y),f(y,x),f(y,y)}");
    check("Tuples({},2)", //
        "{}");
    check("Tuples({1,2},0)", //
        "{{}}");
  }

  @Test
  public void testAllocationBeyondMemory() {
    check("ConstantArray(0,{20000,20000,20000})", //
        "$Aborted");
    check("CheckAbort(Array(g,{20000,20000,20000}),aborted)", //
        "aborted");
    // a negative index into the diagonal used to throw an ArrayIndexOutOfBoundsException; here the
    // dimension is beyond the size limit of one expression
    check("DiagonalMatrix(Range(3), 0, 10^9)", //
        "DiagonalMatrix(Range(3),0,10^9)");
    // a length beyond a machine integer used to leave these unevaluated without a message
    check("Table(0, 10^12)", //
        "$Aborted");
    check("Range(10^12)", //
        "$Aborted");
    check("PadLeft({1}, 10^12)", //
        "$Aborted");
    check("Tuples({p, q}, 1000)", //
        "Tuples({p,q},1000)");
    check("Take(Range(5), {1, 2^62})", //
        "Take({1,2,3,4,5},{1,4611686018427387904})");
    check("Take(Range(5), {2, 3})", //
        "{2,3}");
  }

  @Test
  public void testExpIntegralELargeOrder() {
    check("ExpIntegralE(5*10^11, 3.0)", //
        "9.95741*10^-14");
    check("N(ExpIntegralE(4*10^8, 1/4), 25)", //
        "0.000000001947001961329140845062874");
    // the continued fraction agrees with the series of the general routine
    check("N(ExpIntegralE(2500, 3), 30)", //
        "0.0000198988986805579871158989292557");
  }

  @Test
  public void testBetaRegularizedHugeParameter() {
    check("BetaRegularized(0.25, 4, 3.0*10^9)", //
        "1.0");
    check("BetaRegularized(0.75, 3.0*10^9, 4)", //
        "0.0");
    // I_(1/2)(a, a) is 1/2 for every a
    check("InputForm(BetaRegularized(0.5, 7.0*10^8, 7.0*10^8))", //
        "0.5`");
    // the quotient of the incomplete and the complete beta function gave -1.16*10^254
    check("N(BetaRegularized(3/1000, 4, 3000), 30)", //
        "0.979042272608131990571275802978");
    check("N(BetaRegularized(1/2, 7*10^5+1/2, 7*10^5+1/2), 30)", //
        "0.5");
    // the upper limit next to 1 and next to 0: 1-x and x have no double value
    check("InputForm(N(BetaRegularized(1-10^-30, 2, 7/2), 50))", //
        "1`50");
    check("N(BetaRegularized(10^-400, 5/2, 7/2), 50)", //
        "1.0864977448406721588489131579563647114885778482548*10^-999");
  }

  @Test
  public void testQPochhammerBeyondExpansion() {
    // the product is too long to expand: (a;q)_n = (a;q)_Infinity/(a*q^n;q)_Infinity
    check("QPochhammer(0.25, 0.5, 3*10^5)", //
        "0.577576");
    check("QPochhammer(0.25, 0.5)", //
        "0.577576");
    // (a;q)_(-m) == (-q/a)^m*q^(m*(m-1)/2) / (q/a;q)_m; the second has the factor 1-0.25*2^2
    check("{QPochhammer(0.3, 0.5, -3*10^5), QPochhammer(0.25, 0.5, -3*10^5)}", //
        "{0.0,ComplexInfinity}");
  }

  @Test
  public void testDeepChains() {
    // each new element is compared with the last one: the hash codes differ at once
    check("Length(FixedPointList(w, v, 3000))", //
        "3001");
    // the numeric argument test walked all nested lists at every level
    check("Depth(Nest(List, 1, 30000))", //
        "30001");
    check("Nest(w, 0.1+0.2, 30000) === Nest(w, 0.3, 30000)", //
        "True");
    check("Nest(w, 0.1+0.2, 30000) === Nest(w, 0.4, 30000)", //
        "False");
    // the leaves of a chain are counted in a loop
    check("LeafCount(Nest(w, v, 100000))", //
        "100001");
    check("LeafCount(Nest(w, v, 100000), Heads -> False)", //
        "1");
    check("LeafCount(Nest(w, v_, 3000))", //
        "3003");
  }

  @Test
  public void testOptionsAndDefaultOfProtectedSymbol() {
    check("Options(Plus) = {foo -> 1}", //
        "{foo->1}");
    check("Options(Plus) := {foo -> 1}", //
        "$Failed");
    check("Default(Times) = 2", //
        "2");
    check("Default(Times)", //
        "1");
  }

  @Test
  public void testChangeOfProtectedVariable() {
    // the new value is returned, the protected variable keeps the old one
    check("pk={1};Protect(pk);{AppendTo(pk,2),PrependTo(pk,0),AddTo(pk,1),pk++,++pk,pk[[1]]=7,pk}", //
        "{{1,2},{0,1},{2},{1},{2},7,{1}}");
    check("pa=<|x->1|>;Protect(pa);{AssociateTo(pa,y->2),KeyDropFrom(pa,x),pa}", //
        "{<|x->1,y->2|>,<||>,<|x->1|>}");
    // the names, not the values, of the symbols which were protected
    check("pv=3;InputForm({Protect(pv),Protect(pv),Unprotect(pv)})", //
        "{{\"pv\"},{},{\"pv\"}}");
    check("pv++;pv", //
        "4");
  }
}
