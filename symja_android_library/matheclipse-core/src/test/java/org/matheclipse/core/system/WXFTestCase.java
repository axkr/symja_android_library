package org.matheclipse.core.system;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.WL;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Unit tests for handling the WXF serialization format.
 *
 * <p>
 * See: <a href="https://reference.wolfram.com/language/tutorial/WXFFormatDescription.html">WXF
 * Format Description</a>
 */
public class WXFTestCase extends ExprEvaluatorTestCase {

  private String toString(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < bytes.length; i++) {
      sb.append(bytes[i] & 0x00ff);
      sb.append(' ');
    }
    return sb.toString();
  }

   @Test
   public void testVarint() {
    byte[] bytes = WL.varintBytes(500);
    assertEquals("244 3 ", toString(bytes));
    assertEquals(500, WL.parseVarint(bytes, 0)[0]);

    bytes = WL.varintBytes(64);
    assertEquals("64 ", toString(bytes));
    assertEquals(Byte.MAX_VALUE / 2 + 1, WL.parseVarint(bytes, 0)[0]);

    bytes = WL.varintBytes(Byte.MAX_VALUE);
    assertEquals("127 ", toString(bytes));
    assertEquals(Byte.MAX_VALUE, WL.parseVarint(bytes, 0)[0]);

    bytes = WL.varintBytes(Byte.MAX_VALUE + 1);
    assertEquals("128 1 ", toString(bytes));
    assertEquals(Byte.MAX_VALUE + 1, WL.parseVarint(bytes, 0)[0]);

    bytes = WL.varintBytes(Integer.MAX_VALUE);
    assertEquals("255 255 255 255 7 ", toString(bytes));
    assertEquals(Integer.MAX_VALUE, WL.parseVarint(bytes, 0)[0]);

    bytes = WL.varintBytes(0);
    assertEquals("0 ", toString(bytes));
    assertEquals(0, WL.parseVarint(bytes, 0)[0]);
  }

   @Test
   public void testBinarySerialize() {
    // decimal 4611686018427387893
    check("BinarySerialize(2^62 -11) // Normal", //
        "{56,58,76,245,255,255,255,255,255,255,63}");
    check("BinarySerialize(<|a->b,1:>2|>) // Normal", //
        "{56,58,65,2,45,115,8,71,108,111,98,97,108,96,97,115,8,71,108,111,98,97,108,96,98,\n"
            + "58,67,1,67,2}");

    check("BinarySerialize(Quantity(12, \"Hours\")) // Normal", //
        "{56,58,102,2,115,8,81,117,97,110,116,105,116,121,67,12,83,5,72,111,117,114,115}");

    // check("BinarySerialize(N(Pi,30))", //
    // "");

    check("BinarySerialize(SeriesData(x, 0, {1, 1, 1/2, 1/6, 1/24, 1/120}, 0, 6, 1)) // Normal", //
        "{56,58,102,6,115,10,83,101,114,105,101,115,68,97,116,97,115,8,71,108,111,98,97,\n"
            + "108,96,120,67,0,102,6,115,4,76,105,115,116,67,1,67,1,102,2,115,8,82,97,116,105,\n"
            + "111,110,97,108,67,1,67,2,102,2,115,8,82,97,116,105,111,110,97,108,67,1,67,6,102,\n"
            + "2,115,8,82,97,116,105,111,110,97,108,67,1,67,24,102,2,115,8,82,97,116,105,111,\n"
            + "110,97,108,67,1,67,120,67,0,67,6,67,1}");

    check("BinarySerialize(Quantity(12, \"Hours\")) // Normal", //
        "{56,58,102,2,115,8,81,117,97,110,116,105,116,121,67,12,83,5,72,111,117,114,115}");

    double[] d0 = new double[] {1.1, 2.34, 4.11};
    double[] d1 = new double[] {3.1415, 100000.1234567, -100000.4711};
    ASTRealMatrix m = new ASTRealMatrix(new double[][] {d0, d1}, false);
    IExpr expr = F.Normal.of(F.BinarySerialize.of(m));
    check(expr.toString(), //
        "{56,58,193,35,2,2,3,154,153,153,153,153,153,241,63,184,30,133,235,81,184,2,64,\n"
            + "113,61,10,215,163,112,16,64,111,18,131,192,202,33,9,64,144,187,173,249,1,106,248,\n"
            + "64,82,39,160,137,7,106,248,192}");

    ASTRealVector v = new ASTRealVector(new double[] {1.1, 2.34, 4.11}, false);
    expr = F.Normal.of(F.BinarySerialize.of(v));
    check(expr.toString(), //
        "{56,58,193,35,1,3,154,153,153,153,153,153,241,63,184,30,133,235,81,184,2,64,113,\n"
            + "61,10,215,163,112,16,64}");

    check("BinarySerialize(x->y) // Normal", //
        "{56,58,102,2,115,4,82,117,108,101,115,8,71,108,111,98,97,108,96,120,115,8,71,108,\n"
            + "111,98,97,108,96,121}");

    check("BinarySerialize(x___) // Normal", //
        "{56,58,102,2,115,7,80,97,116,116,101,114,110,115,8,71,108,111,98,97,108,96,120,\n"
            + "102,0,115,17,66,108,97,110,107,78,117,108,108,83,101,113,117,101,110,99,101}");
    check("BinarySerialize(x__) // Normal", //
        "{56,58,102,2,115,7,80,97,116,116,101,114,110,115,8,71,108,111,98,97,108,96,120,\n"
            + "102,0,115,13,66,108,97,110,107,83,101,113,117,101,110,99,101}");
    check("BinarySerialize(___) // Normal", //
        "{56,58,102,0,115,17,66,108,97,110,107,78,117,108,108,83,101,113,117,101,110,99,\n"
            + "101}");
    check("BinarySerialize(__) // Normal", //
        "{56,58,102,0,115,13,66,108,97,110,107,83,101,113,117,101,110,99,101}");
    check("BinarySerialize(-25!) // Normal", //
        "{56,58,73,27,45,49,53,53,49,49,50,49,48,48,52,51,51,51,48,57,56,53,57,56,52,48,\n"
            + "48,48,48,48,48}");
    check("BinarySerialize(25!) // Normal", //
        "{56,58,73,26,49,53,53,49,49,50,49,48,48,52,51,51,51,48,57,56,53,57,56,52,48,48,\n"
            + "48,48,48,48}");
    check("BinarySerialize(500000) // Normal", //
        "{56,58,105,32,161,7,0}");
    check("BinarySerialize(-500000) // Normal", //
        "{56,58,105,224,94,248,255}");
    check("BinarySerialize(-1111) // Normal", //
        "{56,58,106,169,251}");
    check("BinarySerialize(1111) // Normal", //
        "{56,58,106,87,4}");
    check("BinarySerialize(42) // Normal", //
        "{56,58,67,42}");
    check("BinarySerialize(-42) // Normal", //
        "{56,58,67,214}");

    check("BinarySerialize(x_.) // Normal", //
        "{56,58,102,1,115,8,79,112,116,105,111,110,97,108,102,2,115,7,80,97,116,116,101,\n"
            + "114,110,115,8,71,108,111,98,97,108,96,120,102,0,115,5,66,108,97,110,107}");
    check("BinarySerialize(x_Integer) // Normal", //
        "{56,58,102,2,115,7,80,97,116,116,101,114,110,115,8,71,108,111,98,97,108,96,120,\n"
            + "102,1,115,5,66,108,97,110,107,115,7,73,110,116,101,103,101,114}");
    check("BinarySerialize(x_) // Normal", //
        "{56,58,102,2,115,7,80,97,116,116,101,114,110,115,8,71,108,111,98,97,108,96,120,\n"
            + "102,0,115,5,66,108,97,110,107}");
    check("BinarySerialize(_) // Normal", //
        "{56,58,102,0,115,5,66,108,97,110,107}");
    check("BinarySerialize(_Integer) // Normal", //
        "{56,58,102,1,115,5,66,108,97,110,107,115,7,73,110,116,101,103,101,114}");
    check("BinarySerialize(-1000.14157+I*42.1) // Normal", //
        "{56,58,102,2,115,7,67,111,109,112,108,101,120,114,194,192,115,239,33,65,143,192,\n"
            + "114,205,204,204,204,204,12,69,64}");
    check("BinarySerialize(-3.14157) // Normal", //
        "{56,58,114,253,193,192,115,239,33,9,192}");
    check("BinarySerialize(0.75) // Normal", //
        "{56,58,114,0,0,0,0,0,0,232,63}");
    check("BinarySerialize(2/3+7/4*I) // Normal", //
        "{56,58,102,2,115,7,67,111,109,112,108,101,120,102,2,115,8,82,97,116,105,111,110,\n"
            + "97,108,67,2,67,3,102,2,115,8,82,97,116,105,111,110,97,108,67,7,67,4}");
    check("BinarySerialize(2/3) // Normal", //
        "{56,58,102,2,115,8,82,97,116,105,111,110,97,108,67,2,67,3}");

    check("BinarySerialize(Plot) // Normal", //
        "{56,58,115,4,80,108,111,116}");
    check("BinarySerialize(\"hello!\") // Normal", //
        "{56,58,83,6,104,101,108,108,111,33}");
    check("BinarySerialize({}) // Normal", //
        "{56,58,102,0,115,4,76,105,115,116}");
    check("BinarySerialize(f( )) // Normal", //
        "{56,58,102,0,115,8,71,108,111,98,97,108,96,102}");
    check("BinarySerialize(f(g)) // Normal", //
        "{56,58,102,1,115,8,71,108,111,98,97,108,96,102,115,8,71,108,111,98,97,108,96,103}");
    check("BinarySerialize(f(g(x,y))) // Normal", //
        "{56,58,102,1,115,8,71,108,111,98,97,108,96,102,102,2,115,8,71,108,111,98,97,108,\n"
            + "96,103,115,8,71,108,111,98,97,108,96,120,115,8,71,108,111,98,97,108,96,121}");
    check("BinarySerialize(f(g,2)) // Normal", //
        "{56,58,102,2,115,8,71,108,111,98,97,108,96,102,115,8,71,108,111,98,97,108,96,103,\n"
            + "67,2}");
  }

   @Test
   public void testBinarySerializeRubi() {
    // IExpr rubi = UtilityFunctionCtors.FunctionOfExponentialQ(F.C1, F.C1);

    check("ba=BinarySerialize( ( Rubi`FunctionOfExponentialQ(1,1) ) ) // Normal ", //
        "{56,58,102,2,115,27,82,117,98,105,96,102,117,110,99,116,105,111,110,111,102,101,\n"
            + "120,112,111,110,101,110,116,105,97,108,113,67,1,67,1}");
  }


   @Test
   public void testBinarySerializeGraph() {

    check("BinarySerialize( EdgeWeight->{0.0,1.0,1.0} ) // Normal ", //
        "{56,58,102,2,115,4,82,117,108,101,115,10,69,100,103,101,87,101,105,103,104,116,\n" //
            + "102,3,115,4,76,105,115,116,114,0,0,0,0,0,0,0,0,114,0,0,0,0,0,0,240,63,114,0,0,0,\n" //
            + "0,0,0,240,63}");
    check("BinarySerialize( {1->2,2->3,3->1} ) // Normal ", //
        "{56,58,102,3,115,4,76,105,115,116,102,2,115,4,82,117,108,101,67,1,67,2,102,2,115,\n"
            + "4,82,117,108,101,67,2,67,3,102,2,115,4,82,117,108,101,67,3,67,1}");
    check("BinarySerialize(Graph({1,2,3},{1->2,2->3,3->1},{EdgeWeight->{0.0,1.0,1.0}})) // Normal ", //
        "{56,58,102,3,115,5,71,114,97,112,104,102,3,115,4,76,105,115,116,67,1,67,2,67,3,\n" //
            + "102,3,115,4,76,105,115,116,102,2,115,12,68,105,114,101,99,116,101,100,69,100,103,\n" //
            + "101,67,1,67,2,102,2,115,12,68,105,114,101,99,116,101,100,69,100,103,101,67,2,67,\n" //
            + "3,102,2,115,12,68,105,114,101,99,116,101,100,69,100,103,101,67,3,67,1,102,1,115,\n" //
            + "4,76,105,115,116,102,2,115,4,82,117,108,101,115,10,69,100,103,101,87,101,105,103,\n" //
            + "104,116,102,3,115,4,76,105,115,116,114,0,0,0,0,0,0,0,0,114,0,0,0,0,0,0,240,63,\n" //
            + "114,0,0,0,0,0,0,240,63}");
  }

   @Test
   public void testBinaryDeserializeRubi() {
    // IExpr rubi = UtilityFunctionCtors.FunctionOfExponentialQ(F.C1, F.C1);
    check(
        "BinaryDeserialize( ByteArray({56,58,102,2,115,27,82,117,98,105,96,102,117,110,99,116,105,111,110,111,102,101,120,112,111,110,101,110,116,105,97,108,113,67,1,67,1} ))  ", //
        "Rubi`functionofexponentialq(1,1)");
  }

   @Test
   public void testBinarySerializeNumericArray() {
    check(
        "BinarySerialize( NumericArray({3.145+I*2.9,-3.145 - I*2.9}, \"ComplexReal32\") ) // Normal", //
        "{56,58,194,51,1,2,174,71,73,64,154,153,57,64,174,71,73,192,154,153,57,192}");
    check(
        "BinarySerialize( NumericArray({3.145+I*2.9,-3.145 - I*2.9}, \"ComplexReal64\") ) // Normal", //
        "{56,58,194,52,1,2,41,92,143,194,245,40,9,64,51,51,51,51,51,51,7,64,41,92,143,194,\n"
            + "245,40,9,192,51,51,51,51,51,51,7,192}");
    check("BinarySerialize( NumericArray({-2.0,-1.0,0.0,1.0,2.0}, \"Real64\") ) // Normal", //
        "{56,58,194,35,1,5,0,0,0,0,0,0,0,192,0,0,0,0,0,0,240,191,0,0,0,0,0,0,0,0,0,0,0,0,\n"
            + "0,0,240,63,0,0,0,0,0,0,0,64}");
    check("BinarySerialize( NumericArray({-2.0,-1.0,0.0,1.0,2.0}, \"Real32\") ) // Normal", //
        "{56,58,194,34,1,5,0,0,0,192,0,0,128,191,0,0,0,0,0,0,128,63,0,0,0,64}");
    check("BinarySerialize( NumericArray({-2,-1,0,1,2}, \"Integer8\") ) // Normal", //
        "{56,58,194,0,1,5,254,255,0,1,2}");
    check("BinarySerialize( NumericArray({-2,-1,0,1,2}, \"Integer16\") ) // Normal", //
        "{56,58,194,1,1,5,254,255,255,255,0,0,1,0,2,0}");
    check("BinarySerialize( NumericArray({-2,-1,0,1,2}, \"Integer32\") ) // Normal", //
        "{56,58,194,2,1,5,254,255,255,255,255,255,255,255,0,0,0,0,1,0,0,0,2,0,0,0}");
    check("BinarySerialize( NumericArray({-2,-1,0,1,2}, \"Integer64\") ) // Normal", //
        "{56,58,194,3,1,5,254,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,\n"
            + "0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0}");

    check("BinarySerialize( NumericArray({0,1,128,2^8-1}, \"UnsignedInteger8\") ) // Normal", //
        "{56,58,194,16,1,4,0,1,128,255}");
    check("BinarySerialize( NumericArray({0,1,128,2^16-1}, \"UnsignedInteger16\") ) // Normal", //
        "{56,58,194,17,1,4,0,0,1,0,128,0,255,255}");
    check("BinarySerialize( NumericArray({0,1,128,2^32-1}, \"UnsignedInteger32\") ) // Normal", //
        "{56,58,194,18,1,4,0,0,0,0,1,0,0,0,128,0,0,0,255,255,255,255}");
    check("BinarySerialize( NumericArray({0,1,128,2^64-1}, \"UnsignedInteger64\") ) // Normal", //
        "{56,58,194,19,1,4,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,128,0,0,0,0,0,0,0,255,255,255,\n"
            + "255,255,255,255,255}");
  }

   @Test
   public void testBinarySerializeSparseArray() {
    check(
        "BinarySerialize( SparseArray({{1, 1} -> a, {2, 2} -> b, {3, 3} -> 3, {1, 3} -> c})  ) // Normal", //
        "{56,58,102,4,115,11,83,112,97,114,115,101,65,114,114,97,121,115,9,65,117,116,111,\n"
            + "109,97,116,105,99,193,0,1,2,3,3,67,0,102,3,115,4,76,105,115,116,67,1,102,2,115,4,\n"
            + "76,105,115,116,193,0,1,4,0,2,3,4,193,0,2,4,1,1,3,2,3,102,4,115,4,76,105,115,116,\n"
            + "115,8,71,108,111,98,97,108,96,97,115,8,71,108,111,98,97,108,96,99,115,8,71,108,\n"
            + "111,98,97,108,96,98,67,3}");

    // SparseArray(Automatic,{3,3},0,{1,{{0,2,3,4},{{1},{3},{2},{3}}},{1,4,2,3}})
    check(
        "BinarySerialize( SparseArray({{1, 1} -> 1, {2, 2} -> 2, {3, 3} -> 3, {1, 3} -> 4})  ) // Normal", //
        "{56,58,102,4,115,11,83,112,97,114,115,101,65,114,114,97,121,115,9,65,117,116,111,\n"
            + "109,97,116,105,99,193,0,1,2,3,3,67,0,102,3,115,4,76,105,115,116,67,1,102,2,115,4,\n"
            + "76,105,115,116,193,0,1,4,0,2,3,4,193,0,2,4,1,1,3,2,3,193,0,1,4,1,4,2,3}");
  }

   @Test
   public void testBinaryDeserialize() {
    // decimal 4611686018427387893 == 2^62 -11
    check("BinaryDeserialize(ByteArray({56,58,76,245,255,255,255,255,255,255,63})) ", //
        "4611686018427387893");

    // 3.145+I*2.9
    check(
        "BinaryDeserialize(ByteArray( {56, 58, 102, 2, 115, 7, 67, 111, 109, 112, 108, 101, 120, 114, 41, 92, 143, \n"
            + "		  194, 245, 40, 9, 64, 114, 51, 51, 51, 51, 51, 51, 7, 64}))", //
        //
        "3.145+I*2.9");
    check(
        "BinaryDeserialize(ByteArray({56,58,65,2,45,115,8,71,108,111,98,97,108,96,97,115,8,71,108,111,98,97,108,96,98,58,67,1,67,2}))", //
        "<|a->b,1:>2|>");

    // N[Pi, 30]
    // 3.14159265358979323846264338328
    // character representation: 3.1415926535897932384626433832795028841971693993751058151208`30.
    check(
        "BinaryDeserialize(ByteArray({56, 58, 82, 64, 51, 46, 49, 52, 49, 53, 57, 50, 54, 53, 51, 53, 56, 57, 55, \r\n"
            + "			  57, 51, 50, 51, 56, 52, 54, 50, 54, 52, 51, 51, 56, 51, 50, 55, 57, 53, 48, \r\n"
            + "			  50, 56, 56, 52, 49, 57, 55, 49, 54, 57, 51, 57, 57, 51, 55, 53, 49, 48, 53, \r\n"
            + "			  56, 49, 53, 49, 50, 48, 56, 96, 51, 48, 46}))", //
        //
        "3.14159265358979323846264338327");

    // SeriesData(x, 0, {1, 1, 1/2, 1/6, 1/24, 1/120}, 0, 6, 1)
    check(
        "BinaryDeserialize(ByteArray({56, 58, 102, 6, 115, 10, 83, 101, 114, 105, 101, 115, 68, 97, 116, 97, 115, \n"
            + "			  8, 71, 108, 111, 98, 97, 108, 96, 120, 67, 0, 102, 6, 115, 4, 76, 105, 115, \n"
            + "			  116, 67, 1, 67, 1, 102, 2, 115, 8, 82, 97, 116, 105, 111, 110, 97, 108, 67, \n"
            + "			  1, 67, 2, 102, 2, 115, 8, 82, 97, 116, 105, 111, 110, 97, 108, 67, 1, 67, \n"
            + "			  6, 102, 2, 115, 8, 82, 97, 116, 105, 111, 110, 97, 108, 67, 1, 67, 24, 102, \n"
            + "			  2, 115, 8, 82, 97, 116, 105, 111, 110, 97, 108, 67, 1, 67, 120, 67, 0, 67, \n"
            + "			  6, 67, 1}))", //
        //
        "1+x+x^2/2+x^3/6+x^4/24+x^5/120+O(x)^6");

    // Quantity(12, "Hours")
    check(
        "BinaryDeserialize(ByteArray({56,58,102,2,115,8,81,117,97,110,116,105,116,121,67,12,83,5,72,111,117,114,115}))", //
        //
        "12[Hours]");

    checkNumeric(
        "BinaryDeserialize(ByteArray({56,58,193,35,2,2,3,154,153,153,153,153,153,241,63,184,30,133,235,81,184,2,64,"
            + "113,61,10,215,163,112,16,64,111,18,131,192,202,33,9,64,144,187,173,249,1,106,248,"
            + "64,82,39,160,137,7,106,248,192}))", //
        //
        "{{1.1,2.34,4.11},\n" + //
            " {3.1415,100000.1234567,-100000.4711}}");

    check(
        "BinaryDeserialize(ByteArray({56, 58, 193, 35, 1, 3, 154, 153, 153, 153, 153, 153, 241, 63, 184, 30, 133, 235, 81, 184, 2, 64, 113, 61, 10, 215, 163, 112, 16, 64}))", //
        "{1.1,2.34,4.11}");

    check(
        "BinaryDeserialize(ByteArray({56,58,102,2,115,4,82,117,108,101,115,8,71,108,111,98,97,108,96,120,115,8,71,108,111,98,97,108,96,121}))", //
        "x->y");

    // BlankNullSequence(x);
    check(
        "BinaryDeserialize(ByteArray({56, 58, 102, 2, 115, 7, 80, 97, 116, 116, 101, 114, 110, 115, 8, 71, 108, 111, 98, 97, 108, 96, 120, 102, 0, 115, 17, 66, 108, 97, 110, 107, 78, 117, 108, 108, 83, 101, 113, 117, 101, 110, 99, 101}))", //
        "x___");
    // BlankSequence(x);
    check(
        "BinaryDeserialize(ByteArray({56, 58, 102, 2, 115, 7, 80, 97, 116, 116, 101, 114, 110, 115, 8, 71, 108, 111, 98, 97, 108, 96, 120, 102, 0, 115, 13, 66, 108, 97, 110, 107, 83, 101, 113, 117, 101, 110, 99, 101}))", //
        "x__");
    // BlankNullSequence();
    check(
        "BinaryDeserialize(ByteArray({56, 58, 102, 0, 115, 17, 66, 108, 97, 110, 107, 78, 117, 108, 108, 83, 101, 113, 117, 101, 110, 99, 101}))", //
        "___");
    // BlankSequence();
    check(
        "BinaryDeserialize(ByteArray({56, 58, 102, 0, 115, 13, 66, 108, 97, 110, 107, 83, 101, 113, 117, 101, 110, 99, 101}))", //
        "__");
    // -25!
    check(
        "BinaryDeserialize(ByteArray({56,58,73,27,45,49,53,53,49,49,50,49,48,48,52,51,51,51,48,57,56,53,57,56,52,48,48,48,48,48,48}))", //
        "-15511210043330985984000000");
    // 25!
    check(
        "BinaryDeserialize(ByteArray({56,58,73,26,49,53,53,49,49,50,49,48,48,52,51,51,51,48,57,56,53,57,56,52,48,48,48,48,48,48}))", //
        "15511210043330985984000000");

    check("BinaryDeserialize(ByteArray({56,58,105,32,161,7,0}))", //
        "500000");
    check("BinaryDeserialize(ByteArray({56,58,105,224,94,248,255}))", //
        "-500000");

    check("BinaryDeserialize(ByteArray({56,58,106,169,251}))", //
        "-1111");
    check("BinaryDeserialize(ByteArray({56,58,106,87,4}))", //
        "1111");
    check("BinaryDeserialize(ByteArray({56,58,67,42}))", //
        "42");
    check("BinaryDeserialize(ByteArray({56,58,67,214}))", //
        "-42");

    check(
        "BinaryDeserialize(ByteArray({56,58,102,1,115,8,79,112,116,105,111,110,97,108,102,2,115,7,80,97,116,116,101,114,110,115,1,120,102,0,115,5,66,108,97,110,107}))", //
        "x_.");
    check(
        "BinaryDeserialize(ByteArray({56,58,102,2,115,7,80,97,116,116,101,114,110,115,8,71,108,111,98,97,108,96,120,102,1,115,5,66,108,97,110,107,115,7,73,110,116,101,103,101,114}))", //
        "x_Integer");

    check(
        "BinaryDeserialize(ByteArray({56,58,102,2,115,7,80,97,116,116,101,114,110,115,8,71,108,111,98,97,108,96,120,102,0,115,5,66,108,97,110,107}))", //
        "x_");

    check("BinaryDeserialize(ByteArray({56,58,102,0,115,5,66,108,97,110,107}))", //
        "_");

    check(
        "BinaryDeserialize(ByteArray({56, 58, 102, 1, 115, 5, 66, 108, 97, 110, 107, 115, 7, 73, 110, 116, 101, 103, 101, 114}))", //
        "_Integer");

    checkNumeric(
        "BinaryDeserialize(ByteArray({56,58,102,2,115,7,67,111,109,112,108,101,120,114,194,192,115,239,33,65,143,192,114,205,204,204,204,204,12,69,64}))", //
        "-1000.14157+I*42.1");

    check("BinaryDeserialize(ByteArray({56,58,114,253,193,192,115,239,33,9,192}))", //
        "-3.14157");

    check("BinaryDeserialize(ByteArray({56,58,114,0,0,0,0,0,0,232,63}))", //
        "0.75");

    check(
        "BinaryDeserialize(ByteArray({56,58,102,2,115,7,67,111,109,112,108,101,120,102,2,115,8,82,97,116,105,111,110,\n"
            + "97,108,67,2,67,3,102,2,115,8,82,97,116,105,111,110,97,108,67,7,67,4}))", //
        "2/3+I*7/4");

    check(
        "BinaryDeserialize(ByteArray({56,58,102,2,115,8,82,97,116,105,111,110,97,108,67,2,67,3}))", //
        "2/3");

    check("BinaryDeserialize(ByteArray({56,58,115,4,80,108,111,116}))", //
        "Plot");

    check("BinaryDeserialize(ByteArray({56,58,83,6,104,101,108,108,111,33}))", //
        "hello!");

    check("BinaryDeserialize(ByteArray({56,58,102,0,115,4,76,105,115,116}))", //
        "{}");

    check("BinaryDeserialize(ByteArray({56,58,102,0,115,8,71,108,111,98,97,108,96,102}))", //
        "f()");

    check(
        "BinaryDeserialize(ByteArray({56,58,102,1,115,8,71,108,111,98,97,108,96,102,115,8,71,108,111,98,97,108,96,103}))", //
        "f(g)");

    check(
        "BinaryDeserialize(ByteArray({56,58,102,1,115,8,71,108,111,98,97,108,96,102,102,2,115,8,71,108,111,98,97,108,96,103,115,8,71,108,111,98,97,108,96,120,115,8,71,108,111,98,97,108,96,121}))", //
        "f(g(x,y))");

    check(
        "BinaryDeserialize(ByteArray({56,58,102,2,115,8,71,108,111,98,97,108,96,102,115,8,71,108,111,98,97,108,96,103,67,2}))", //
        "f(g,2)");
  }

   @Test
   public void testBinaryDeserializeGraph() {
    check("BinaryDeserialize(ByteArray(" //
        + "{56,58,102,3,115,5,71,114,97,112,104,102,3,115,4,76,105,115,116,67,1,67,2,67,3,\n" //
        + "102,3,115,4,76,105,115,116,102,2,115,12,68,105,114,101,99,116,101,100,69,100,103,\n" //
        + "101,67,1,67,2,102,2,115,12,68,105,114,101,99,116,101,100,69,100,103,101,67,2,67,\n" //
        + "3,102,2,115,12,68,105,114,101,99,116,101,100,69,100,103,101,67,3,67,1,102,1,115,\n" //
        + "4,76,105,115,116,102,2,115,4,82,117,108,101,115,10,69,100,103,101,87,101,105,103,\n" //
        + "104,116,102,3,115,4,76,105,115,116,114,0,0,0,0,0,0,0,0,114,0,0,0,0,0,0,240,63,\n" //
        + "114,0,0,0,0,0,0,240,63}" //
        + "))", //
        "Graph({1,2,3},{1->2,2->3,3->1},{EdgeWeight->{0.0,1.0,1.0}})");

    check(
        "BinaryDeserialize( ByteArray( {56, 58, 102, 3, 115, 5, 71, 114, 97, 112, 104, 102, 3, 115, 4, 76, 105, 115, \n"
            + "    	  116, 67, 1, 67, 2, 67, 3, 102, 2, 115, 4, 76, 105, 115, 116, 115, 4, 78, \n"
            + "    	  117, 108, 108, 193, 0, 2, 3, 2, 1, 2, 2, 3, 3, 1, 102, 1, 115, 4, 76, 105, \n"
            + "    	  115, 116, 102, 2, 115, 4, 82, 117, 108, 101, 115, 10, 69, 100, 103, 101, \n"
            + "    	  87, 101, 105, 103, 104, 116, 193, 0, 1, 3, 0, 3, 3}  ) )", //
        "Graph({1,2,3},{Null,{{1,2},{2,3},{3,1}}},{EdgeWeight->{0,3,3}})");

    check(
        "BinaryDeserialize( ByteArray({56, 58, 102, 3, 115, 5, 71, 114, 97, 112, 104, 102, 3, 115, 4, 76, 105, 115, "
            + " 116, 67, 1, 67, 2, 67, 3, 102, 2, 115, 4, 76, 105, 115, 116, 115, 4, 78, "
            + "  117, 108, 108, 193, 0, 2, 3, 2, 1, 2, 2, 3, 3, 1, 102, 2, 115, 4, 76, 105, "
            + "  115, 116, 102, 2, 115, 4, 82, 117, 108, 101, 115, 10, 69, 100, 103, 101, "
            + "  76, 97, 98, 101, 108, 115, 102, 1, 115, 4, 76, 105, 115, 116, 102, 2, 115, "
            + "  4, 82, 117, 108, 101, 102, 2, 115, 14, 85, 110, 100, 105, 114, 101, 99, "
            + "  116, 101, 100, 69, 100, 103, 101, 67, 3, 67, 1, 83, 5, 104, 101, 108, 108, "
            + "  111, 102, 2, 115, 4, 82, 117, 108, 101, 115, 12, 86, 101, 114, 116, 101, "
            + "  120, 76, 97, 98, 101, 108, 115, 102, 1, 115, 4, 76, 105, 115, 116, 83, 4, "
            + "  78, 97, 109, 101} ) ) // InputForm", //
        "Graph({1,2,3},{Null,{{1,2},{2,3},{3,1}}},{EdgeLabels->{3<->1->\"hello\"},VertexLabels->{\"Name\"}})");
    check(
        "BinaryDeserialize( ByteArray( {56, 58, 102, 2, 115, 5, 71, 114, 97, 112, 104, 102, 3, 115, 4, 76, 105, 115, "
            + "116, 67, 1, 67, 2, 67, 3, 102, 2, 115, 4, 76, 105, 115, 116, 115, 4, 78, "
            + "117, 108, 108, 193, 0, 2, 3, 2, 1, 2, 2, 3, 3, 1}) )", //
        "Graph({1,2,3},{Null,{{1,2},{2,3},{3,1}}})");
  }

   @Test
   public void testBinaryDeserializeSparseArray() {
    // SparseArray({{1, 1} -> a, {2, 2} -> b, {3, 3} -> 3, {1, 3} -> c})
    check(
        "BinaryDeserialize( ByteArray({56,58,102,4,115,11,83,112,97,114,115,101,65,114,114,97,121,115,9,65,117,116,111,\n"
            + "109,97,116,105,99,193,0,1,2,3,3,67,0,102,3,115,4,76,105,115,116,67,1,102,2,115,4,\n"
            + "76,105,115,116,193,0,1,4,0,2,3,4,193,0,2,4,1,1,3,2,3,102,4,115,4,76,105,115,116,\n"
            + "115,8,71,108,111,98,97,108,96,97,115,8,71,108,111,98,97,108,96,99,115,8,71,108,\n"
            + "111,98,97,108,96,98,67,3})) // Normal", //
        "{{a,0,c},\n" //
            + " {0,b,0},\n" + " {0,0,3}}");

    // SparseArray({{1, 1} -> 1, {2, 2} -> 2, {3, 3} -> 3, {1, 3} -> 4})
    check(
        "BinaryDeserialize( ByteArray({56, 58, 102, 4, 115, 11, 83, 112, 97, 114, 115, 101, 65, 114, 114, 97, 121,\n"
            + "	  115, 9, 65, 117, 116, 111, 109, 97, 116, 105, 99, 193, 0, 1, 2, 3, 3, 67,\n"
            + "	  0, 102, 3, 115, 4, 76, 105, 115, 116, 67, 1, 102, 2, 115, 4, 76, 105, 115,\n"
            + "	  116, 193, 0, 1, 4, 0, 2, 3, 4, 193, 0, 2, 4, 1, 1, 3, 2, 3, 193, 0, 1, 4,\n"
            + "	  1, 4, 2, 3})) // Normal", //
        "{{1,0,4},\n" //
            + " {0,2,0},\n" + " {0,0,3}}");

    check(
        "BinaryDeserialize( ByteArray( {56,58,102,4,115,11,83,112,97,114,115,101,65,114,114,97,121,115,9,65,117,116,111,"
            + "109,97,116,105,99,193,0,1,2,5,5,67,0,102,3,115,4,76,105,115,116,67,1,102,2,115,4,76,105,115,116,193,0,1,6,0,"
            + "2,5,8,11,13,193,0,2,13,1,2,1,1,2,3,4,3,2,3,5,4,4,5,193,0,1,13,1,254,1,254,1,1,254,1,1,1,254,1,254} )) // Normal", //
        "{{-2,1,0,0,0},\n" + " {1,-2,1,0,0},\n" + " {0,1,-2,1,0},\n" + " {0,0,1,-2,1},\n"
            + " {0,0,0,1,-2}}");
  }

   @Test
   public void testBinaryDeserializeNumericArray() {
    // NumericArray({-2,-1,0,1,2}, "Integer16")
    check("a=BinaryDeserialize( ByteArray({56,58,194,1,1,5,254,255,255,255,0,0,1,0,2,0}))", //
        "NumericArray(Type: Integer16 Dimensions: {5})");
    check("a // Normal", //
        "{-2,-1,0,1,2}");
    // NumericArray({-2,-1,0,1,2}, "Integer32")
    check(
        "a=BinaryDeserialize( ByteArray({56,58,194,2,1,5,254,255,255,255,255,255,255,255,0,0,0,0,1,0,0,0,2,0,0,0}))", //
        "NumericArray(Type: Integer32 Dimensions: {5})");
    check("a // Normal", //
        "{-2,-1,0,1,2}");
    // NumericArray({-2,-1,0,1,2}, "Integer64")
    check(
        "a=BinaryDeserialize( ByteArray({56,58,194,3,1,5,254,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0}))", //
        "NumericArray(Type: Integer64 Dimensions: {5})");
    check("a // Normal", //
        "{-2,-1,0,1,2}");

    // NumericArray({3.145+I*2.9,-3.145 - I*2.9}, "ComplexReal32")
    check(
        "a=BinaryDeserialize( ByteArray({56,58,194,51,1,2,174,71,73,64,154,153,57,64,174,71,73,192,154,153,57,192}))", //
        "NumericArray(Type: ComplexReal32 Dimensions: {2})");
    check("a // Normal", //
        "{3.145+I*2.9,-3.145+I*(-2.9)}");

    // NumericArray({3.145+I*2.9},"ComplexReal64")
    check(
        "a=BinaryDeserialize( ByteArray({56,58,194,52,1,2,41,92,143,194,245,40,9,64,51,51,51,51,51,51,7,64,41,92,143,194,245,40,9,64,51,51,51,51,51,51,7,64}  ))", //
        "NumericArray(Type: ComplexReal64 Dimensions: {2})");
    check("a // Normal", //
        "{3.145+I*2.9,3.145+I*2.9}");

    check("a=BinaryDeserialize( ByteArray({56,58,194,0,1,5,1,2,3,4,5}))", //
        "NumericArray(Type: Integer8 Dimensions: {5})");
    check("a // Normal", //
        "{1,2,3,4,5}");

    // NumericArray{{{0.46433598803226617, 0.10535663717153976}, {0.857006637722326,
    // 0.09359911160247836}, {0.3144570067848558, 0.8210961899128764}},
    // {{0.9901396175777173, 0.48264909154735447}, {0.48649701387553845,
    // 0.419473619410462}, {0.5815917141166007, 0.31909030523660276}}}, "Real64")
    check(
        "a=BinaryDeserialize( ByteArray( {56,58,194,35,3,2,3,2,100,21,189,74,174,183,221,63,48,124,17,15,167,248,186,63,42,29,"
            + "47,47,153,108,235,63,176,109,68,131,28,246,183,63,120,229,8,72,16,32,212,63,232,120,81,132,107,70,234,63,58,12,127,"
            + "71,57,175,239,63,184,245,232,3,185,227,222,63,56,157,12,95,196,34,223,63,152,197,57,225,167,216,218,63,22,44,248,57,"
            + "102,156,226,63,56,143,93,190,249,107,212,63}  ))", //
        "NumericArray(Type: Real64 Dimensions: {2,3,2})");
    check("a // Normal", //
        "{{{0.464336,0.105357},{0.857007,0.0935991},{0.314457,0.821096}},{{0.99014,0.482649},{0.486497,0.419474},{0.581592,0.31909}}}");

    // NumericArray({{{0.46433, 0.10535}, {0.85700,
    // 0.093599}, {0.31445, 0.8210961}},
    // {{0.9901396, 0.48264}, {0.486497,
    // 0.419473}, {0.58159, 0.3190903}}}, "Real32")
    check(
        "a=BinaryDeserialize( ByteArray( {56,58,194,34,3,2,3,2,169,188,237,62,190,193,215,61,90,100,91,63,213,176,191,61,151,"
            + "255,160,62,91,51,82,63,202,121,125,63,151,28,247,62,35,22,249,62,42,197,214,62,21,227,20,63,206,95,163,62}   ))", //
        "NumericArray(Type: Real32 Dimensions: {2,3,2})");
    check("a // Normal", //
        "{{{0.46433,0.10535},{0.857,0.093599},{0.31445,0.821096}},{{0.99014,0.48264},{0.486497,0.419473},{0.58159,0.31909}}}");

    check("bds=BinaryDeserialize( ByteArray({56,58,194,16,1,4,0,1,128,255}))", //
        "NumericArray(Type: UnsignedInteger8 Dimensions: {4})");
    check("bds// Normal", //
        "{0,1,128,255}");
    check("bds=BinaryDeserialize( ByteArray({56,58,194,17,1,4,0,0,1,0,128,0,255,255}))", //
        "NumericArray(Type: UnsignedInteger16 Dimensions: {4})");
    check("bds// Normal", //
        "{0,1,128,65535}");
    check(
        "bds=BinaryDeserialize( ByteArray({56,58,194,18,1,4,0,0,0,0,1,0,0,0,128,0,0,0,255,255,255,255}))", //
        "NumericArray(Type: UnsignedInteger32 Dimensions: {4})");
    check("bds// Normal", //
        "{0,1,128,4294967295}");
    check(
        "bds=BinaryDeserialize( ByteArray({56,58,194,19,1,4,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,128,0,0,0,0,0,0,0,255,255,255,255,255,255,255,255}))", //
        "NumericArray(Type: UnsignedInteger64 Dimensions: {4})");
    check("bds// Normal", //
        "{0,1,128,18446744073709551615}");
  }

   @Test
   public void testByteArrayData() {
    // serialize {0,42,192}
    check("bytes = Normal(BinarySerialize(ByteArray({0, 42, 192})))", //
        "{56,58,66,3,0,42,192}");
    // deserialize {0,42,192} back
    check("BinaryDeserialize(ByteArray({56,58,66,3,0,42,192})) // Normal", //
        "{0,42,192}");
  }

   @Test
   public void testByteArray() {
    check(" ByteArray({1,2,3})", //
        "ByteArray[3 Bytes]");
    check(" ByteArray(Range(16))", //
        "ByteArray[16 Bytes]");
  }

   @Test
   public void testByteArrayQ() {
    check("ByteArrayQ(ByteArray({1,2,3}))", //
        "True");
    check("ByteArrayQ(ByteArray(Range(16)))", //
        "True");
  }
}
