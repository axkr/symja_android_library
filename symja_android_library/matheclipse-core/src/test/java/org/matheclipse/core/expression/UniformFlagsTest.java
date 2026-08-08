package org.matheclipse.core.expression;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;

/**
 * Tests for the uniform type flags of {@link IAST} arguments.
 *
 * @see UniformFlags
 */
class UniformFlagsTest {

  @BeforeAll
  static void initSymja() {
    F.initSymja();
  }

  @Test
  void testArrayConstructorDoubles() {
    // built with the AST(IExpr, IExpr...) constructor, which doesn't accumulate the flags
    IAST list = F.List(1.0, 2.0, 3.0, 4.0);
    assertTrue(list.isUniform());
    assertTrue(list.isUniform(UniformFlags.DOUBLE));
    assertTrue(list.isUniform(UniformFlags.REAL));
    assertTrue(list.isUniform(UniformFlags.NUMBER));
    assertFalse(list.isUniform(UniformFlags.INTEGER));
  }

  @Test
  void testArrayConstructorIntegers() {
    IAST list = F.List(1, 2, 3, 4);
    assertTrue(list.isUniform(UniformFlags.INT));
    assertTrue(list.isUniform(UniformFlags.INTEGER));
    assertFalse(list.isUniform(UniformFlags.DOUBLE));
  }

  @Test
  void testArrayConstructorMixedIntegerSize() {
    // a[0] is an IntegerSym, the last argument is a BigIntegerSym, so the list is uniform
    // INTEGER but not uniform INT
    IAST list = F.List(1L, 2L, 3L, 5_000_000_000L);
    assertTrue(list.isUniform(UniformFlags.INTEGER));
    assertFalse(list.isUniform(UniformFlags.INT));
  }

  @Test
  void testArrayConstructorStrings() {
    IAST list = F.List("a", "b", "c", "d");
    assertTrue(list.isUniform(UniformFlags.STRING));
    assertFalse(list.isUniform(UniformFlags.NUMBER));
  }

  @Test
  void testNewInstanceFromDoubleArray() {
    IAST list = AST.newInstance(S.List, new double[] {1.0, 2.0, 3.0});
    assertTrue(list.isUniform(UniformFlags.DOUBLE));
  }

  @Test
  void testInitNullAndSet() {
    // the arguments are assigned with set() only, the flags start out as UNKNOWN
    IASTAppendable list = F.ast(S.List, 3, true);
    list.set(1, F.ZZ(7));
    list.set(2, F.ZZ(8));
    list.set(3, F.ZZ(9));
    assertTrue(list.isUniform(UniformFlags.INTEGER));
    assertFalse(list.isUniform(UniformFlags.DOUBLE));
  }

  @Test
  void testInitNullAndSetNotUniform() {
    IASTAppendable list = F.ast(S.List, 3, true);
    list.set(1, F.ZZ(7));
    list.set(2, F.List(F.ZZ(8)));
    list.set(3, F.ZZ(9));
    assertFalse(list.isUniform());
  }

  @Test
  void testAppendKeepsFlagsAccurate() {
    IASTAppendable list = F.ListAlloc(4);
    list.append(F.ZZ(1));
    list.append(F.ZZ(2));
    assertTrue(list.isUniform(UniformFlags.INTEGER));
    list.append(F.num(3.0));
    assertFalse(list.isUniform(UniformFlags.INTEGER));
    assertTrue(list.isUniform(UniformFlags.NUMBER));
    list.append(F.x);
    // a symbol is still an atom, so only the NUMBER bit is lost
    assertFalse(list.isUniform(UniformFlags.NUMBER));
    assertTrue(list.isUniform(UniformFlags.ATOM));
    list.append(F.List(F.C1));
    assertFalse(list.isUniform());
  }

  @Test
  void testSetInvalidatesFlags() {
    IASTMutable list = (IASTMutable) F.List(1, 2, 3, 4);
    assertTrue(list.isUniform(UniformFlags.INTEGER));
    list.set(2, F.x);
    assertFalse(list.isUniform(UniformFlags.INTEGER));
    assertTrue(list.isUniform(UniformFlags.ATOM));
    list.set(3, F.List(F.C1));
    assertFalse(list.isUniform());
  }

  @Test
  void testNestedListIsNotUniform() {
    IAST list = F.List(F.List(1, 2), F.List(3, 4), F.List(5, 6), F.List(7, 8));
    assertFalse(list.isUniform());
  }

  @Test
  void testEmptyArgumentsAreNotUniform() {
    assertFalse(F.ListAlloc(4).isUniform());
  }

  @Test
  void testCopyKeepsFlags() {
    IAST list = F.List(1.0, 2.0, 3.0, 4.0);
    assertTrue(list.copy().isUniform(UniformFlags.DOUBLE));
    assertTrue(list.copyAppendable().isUniform(UniformFlags.DOUBLE));
  }

  @Test
  void testIsUniformUsesAndSemantics() {
    // NUMBER and STRING are mutually exclusive, so the "and" test can never succeed
    IAST strings = F.List("a", "b", "c", "d");
    assertFalse(strings.isUniform(UniformFlags.NUMBER | UniformFlags.STRING));
    assertTrue(strings.isUniformAny(UniformFlags.NUMBER | UniformFlags.STRING));

    IAST numbers = F.List(1, 2, 3, 4);
    assertFalse(numbers.isUniform(UniformFlags.NUMBER | UniformFlags.STRING));
    assertTrue(numbers.isUniformAny(UniformFlags.NUMBER | UniformFlags.STRING));

    IAST symbols = F.List(F.a, F.b, F.c, F.d);
    assertTrue(symbols.isUniform(UniformFlags.SYMBOL));
    assertFalse(symbols.isUniformAny(UniformFlags.NUMBER | UniformFlags.STRING));
  }

  @Test
  void testIsFreeOnUniformArguments() {
    IAST numbers = F.List(1, 2, 3, 4);
    assertTrue(numbers.isFree(F.x, true));
    assertTrue(numbers.isFree(F.stringx("a"), true));
    assertFalse(numbers.isFree(F.C2, true));
    // the head is still tested
    assertFalse(numbers.isFree(S.List, true));
    assertTrue(numbers.isFree(S.List, false));

    IAST strings = F.List("a", "b", "c", "d");
    assertTrue(strings.isFree(F.x, true));
    assertTrue(strings.isFree(F.C2, true));
    assertFalse(strings.isFree(F.stringx("c"), true));

    IAST symbols = F.List(F.a, F.b, F.c, F.d);
    assertFalse(symbols.isFree(F.c, true));
    assertTrue(symbols.isFree(F.x, true));
    assertTrue(symbols.isFree(F.C2, true));
    assertTrue(symbols.isFree(F.stringx("a"), true));
  }

  @Test
  void testIsFreeOnMixedAtomArguments() {
    // uniform in ATOM only, so the specialization must not fire
    IAST mixed = F.List(F.C1, F.C2, F.x, F.C3);
    assertTrue(mixed.isUniform(UniformFlags.ATOM));
    assertFalse(mixed.isFree(F.x, true));
    assertFalse(mixed.isFree(F.C2, true));
    assertTrue(mixed.isFree(F.y, true));
  }

  @Test
  void testIsFreeWithNestedArgumentAndPattern() {
    IAST nested = F.List(F.C1, F.C2, F.Sin(F.x), F.C3);
    assertFalse(nested.isFree(F.x, true));
    assertFalse(nested.isFree(S.Sin, true));

    // a pattern can match any type, so the specialization must not fire
    IAST numbers = F.List(1, 2, 3, 4);
    assertFalse(numbers.isFree(F.$p("p"), true));
    assertFalse(numbers.isFree(F.$b(), true));
  }

  @Test
  void testHasOnUniformArguments() {
    IAST numbers = F.List(1, 2, 3, 4);
    assertFalse(numbers.has(F.x, true));
    assertFalse(numbers.has(F.stringx("a"), true));
    // the pattern type is not excluded, so the specialization must not fire
    assertTrue(numbers.has(F.C2, true));
    // the head is still tested
    assertTrue(numbers.has(S.List, true));
    assertFalse(numbers.has(S.List, false));

    IAST strings = F.List("a", "b", "c", "d");
    assertFalse(strings.has(F.x, true));
    assertFalse(strings.has(F.C1, true));
    assertTrue(strings.has(F.stringx("d"), true));

    IAST symbols = F.List(F.a, F.b, F.c, F.d);
    assertFalse(symbols.has(F.C1, true));
    assertFalse(symbols.has(F.stringx("a"), true));
    assertTrue(symbols.has(F.d, true));

    // a nested argument must still be searched
    IAST nested = F.List(F.C1, F.C2, F.Sin(F.x), F.C3);
    assertTrue(nested.has(F.x, true));
  }

  @Test
  void testIsMemberOnUniformArguments() {
    IAST numbers = F.List(1, 2, 3, 4);
    assertFalse(numbers.isMember(F.x, false, null));
    assertFalse(numbers.isMember(F.stringx("a"), false, null));
    assertTrue(numbers.isMember(F.C3, false, null));
    // the header element is only tested with heads == true
    assertTrue(numbers.isMember(S.List, true, null));
    assertFalse(numbers.isMember(S.List, false, null));

    IAST strings = F.List("a", "b", "c", "d");
    assertFalse(strings.isMember(F.C1, false, null));
    assertTrue(strings.isMember(F.stringx("b"), false, null));

    IAST symbols = F.List(F.a, F.b, F.c, F.d);
    assertFalse(symbols.isMember(F.C1, false, null));
    assertTrue(symbols.isMember(F.c, false, null));

    // isMember() only operates at level 1, a nested match must not be reported
    IAST nested = F.List(F.C1, F.C2, F.Sin(F.x), F.C3);
    assertFalse(nested.isMember(F.x, false, null));
  }

  @Test
  void testAppendArgsFromUniformList() {
    IASTAppendable result = F.ListAlloc(8);
    result.appendArgs(F.List(1, 2, 3, 4));
    assertTrue(result.isUniform(UniformFlags.INTEGER));
    result.appendArgs(F.List(5.0, 6.0, 7.0, 8.0));
    assertFalse(result.isUniform(UniformFlags.INTEGER));
    assertTrue(result.isUniform(UniformFlags.NUMBER));
  }
}
