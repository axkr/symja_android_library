package org.matheclipse.io.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

/** Tests for NumericArray functions */
public class NumericArrayTest extends AbstractTestCase {

  public NumericArrayTest(String name) {
    super(name);
  }

  public void testArrayDepth() {
    check(
        "ArrayDepth({{1, 2}, SparseArray({3, 4}), NumericArray({5, 6}, \"Integer32\")})", //
        "1");
    check(
        "ArrayDepth({{1, 2}, SparseArray({3, 4})  })", //
        "2");
  }

  public void testNumericArrayQ() {
    check(
        "NumericArrayQ(NumericArray(SparseArray({{1, 1} -> 1, {2, 2} -> 2, {3, 3} -> 3, {4, 4} -> 4}), \"Integer32\") )", //
        "True");
    check(
        "NumericArrayQ(NumericArray({{7,2,0,1,10},{3,6,4,5,10},{7,9,2,9,3},{1,2,5,9,8},{4,6,8,3,0}}, \"Integer32\"))", //
        "True");
  }

  public void testNumericArrayType() {
    check(
        "NumericArrayType(NumericArray(SparseArray({{1, 1} -> 1, {2, 2} -> 2, {3, 3} -> 3, {4, 4} -> 4}), \"Integer32\") )", //
        "Integer32");
  }

  public void testUnsignedInteger8() {
    check(
        "NumericArray({{0, 2}, {3, 2^8-1}}, \"UnsignedInteger8\")// Normal", //
        "{{0,2},{3,255}}");
  }

  public void testByteArrayInteger8() {
    check(
        "NumericArray(ByteArray({1, 2, 3, 4}), \"UnsignedInteger8\") // Normal", //
        "{1,2,3,4}");
    check(
        "NumericArray(ByteArray({1, 2, 3, 255}), \"Integer8\") // Normal", //
        "NumericArray({1,2,3,255},Integer8)");
    check(
        "NumericArray(ByteArray({1, 2, 3, 255}), \"Integer16\")", //
        "NumericArray(Type: Integer16 Dimensions: {4})");
  }

  public void testUnsignedInteger16() {
    check(
        "NumericArray({{0, 2}, {3, 2^16-1}}, \"UnsignedInteger16\")// Normal", //
        "{{0,2},{3,65535}}");
  }

  public void testUnsignedInteger32() {
    check(
        "NumericArray({{0, 2}, {3, 2^32-1}}, \"UnsignedInteger32\")// Normal", //
        "{{0,2},{3,4294967295}}");
  }

  public void testUnsignedInteger64() {
    check(
        "NumericArray({{0, 2}, {3, 2^64-1}}, \"UnsignedInteger64\")// Normal", //
        "{{0,2},{3,18446744073709551615}}");
  }

  public void testInteger16() {
    check(
        "NumericArray( { } , \"Integer16\") ", //
        "{}");
  }

  public void testInteger32() {
    check(
        "NumericArray(SparseArray({{1, 1} -> 1, {2, 2} -> 2, {3, 3} -> 3, {4, 4} -> 4}), \"Integer32\") // Normal", //
        "{{1,0,0,0},{0,2,0,0},{0,0,3,0},{0,0,0,4}}");
    check(
        "NumericArray({{7,2,0,1,10},{3,6,4,5,10},{7,9,2,9,3},{1,2,5,9,8},{4,6,8,3,0}}, \"Integer32\") // Normal", //
        "{{7,2,0,1,10},{3,6,4,5,10},{7,9,2,9,3},{1,2,5,9,8},{4,6,8,3,0}}");
  }

  public void testInteger64() {
    check(
        "NumericArray({{7,2,0,1,10},{3,6,4,5,10},{7,9,2,9,3},{1,2^40-1,5,9,8},{4,6,8,3,0}}, \"Integer64\") // Normal", //
        "{{7,2,0,1,10},{3,6,4,5,10},{7,9,2,9,3},{1,1099511627775,5,9,8},{4,6,8,3,0}}");
  }

  public void testReal32() {
    check(
        "NumericArray("
            + "{{0.71816,0.985858,0.462604,0.873427,0.837309},"
            + "{0.248652,0.308832,0.159069,0.89764,0.879546},"
            + "{0.285534,0.0789913,0.582489,0.635672,0.260148},"
            + "{0.405098,0.627613,0.685662,0.767928,0.941469},"
            + "{0.0446911,0.990178,0.765964,0.893501,0.35334}}, \"Real32\") // Normal", //
        "{{0.71816,0.985858,0.462604,0.873427,0.837309},"
            + "{0.248652,0.308832,0.159069,0.89764,0.879546},"
            + "{0.285534,0.0789913,0.582489,0.635672,0.260148},"
            + "{0.405098,0.627613,0.685662,0.767928,0.941469},"
            + "{0.0446911,0.990178,0.765964,0.893501,0.35334}}");
    check(
        "NumericArray({1, 2, 3, 4}, \"Real32\") // Normal", //
        "{1.0,2.0,3.0,4.0}");
    check(
        "NumericArray({1/3, 2/3}, \"Real32\") // Normal", //
        "{0.333333,0.666667}");

    check(
        "na=NumericArray( {-100, 0, 5, 120}, \"Integer8\")  ", //
        "NumericArray(Type: Integer8 Dimensions: {4})");
    check(
        " NumericArray( na, \"Real32\")  ", //
        "NumericArray(Type: Real32 Dimensions: {4})");
    check(
        " NumericArray( na, \"Real64\") // Normal ", //
        "{-100.0,0.0,5.0,120.0}");
    check(
        "Dimensions(na)", //
        "{4}");
    check(
        "ArrayDepth(na)", //
        "1");
  }

  public void testReal64() {
    check(
        "NumericArray("
            + "{{0.71816,0.985858,0.462604,0.873427,0.837309},"
            + "{0.248652,0.308832,0.159069,0.89764,0.879546},"
            + "{0.285534,0.0789913,0.582489,0.635672,0.260148},"
            + "{0.405098,0.627613,0.685662,0.767928,0.941469},"
            + "{0.0446911,0.990178,0.765964,0.893501,0.35334}}, \"Real64\") // Normal", //
        "{{0.71816,0.985858,0.462604,0.873427,0.837309},"
            + "{0.248652,0.308832,0.159069,0.89764,0.879546},"
            + "{0.285534,0.0789913,0.582489,0.635672,0.260148},"
            + "{0.405098,0.627613,0.685662,0.767928,0.941469},"
            + "{0.0446911,0.990178,0.765964,0.893501,0.35334}}");
    check(
        "NumericArray({1/3, 2/3}, \"Real64\") ", //
        "NumericArray(Type: Real64 Dimensions: {2})");
  }

  public void testComplexReal32() {
    check(
        "NumericArray({3/7 + 0.6666*I}, \"ComplexReal32\") // Normal", //
        "{0.428571+I*0.6666}");
    check(
        "NumericArray({1/3 + 2/3*I}, \"ComplexReal32\")  ", //
        "NumericArray(Type: ComplexReal32 Dimensions: {1})");
  }

  public void testComplexReal64() {
    check(
        "NumericArray({3/7 + 0.6666*I}, \"ComplexReal64\") // Normal", //
        "{0.428571+I*0.6666}");
    check(
        "NumericArray({1/3 + 2/3*I}, \"ComplexReal64\")  ", //
        "NumericArray(Type: ComplexReal64 Dimensions: {1})");
  }

  /** The JUnit setup method */
  @Override
  protected void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
