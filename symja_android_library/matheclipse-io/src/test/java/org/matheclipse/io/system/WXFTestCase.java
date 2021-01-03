package org.matheclipse.io.system;

import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.WL;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Unit tests for handling the WXF serialization format.
 *
 * <p>See: <a href="https://reference.wolfram.com/language/tutorial/WXFFormatDescription.html">WXF
 * Format Description</a>
 */
public class WXFTestCase extends AbstractTestCase {
  public WXFTestCase(String name) {
    super(name);
  }

  private String toString(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < bytes.length; i++) {
      sb.append((int) bytes[i] & 0x00ff);
      sb.append(' ');
    }
    return sb.toString();
  }

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

  public void testBinarySerialize() {
    check(
        "BinarySerialize(<|a->b,1:>2|>) // Normal", //
        "{56,58,65,2,45,115,8,71,108,111,98,97,108,96,97,115,8,71,108,111,98,97,108,96,98,\n"
            + "58,67,1,67,2}");

    check(
        "BinarySerialize(Quantity(12, \"Hours\")) // Normal", //
        "{56,58,102,2,115,8,81,117,97,110,116,105,116,121,67,12,83,5,72,111,117,114,115}");

    // check("BinarySerialize(N(Pi,30))", //
    // "");

    check(
        "BinarySerialize(SeriesData(x, 0, {1, 1, 1/2, 1/6, 1/24, 1/120}, 0, 6, 1)) // Normal", //
        "{56,58,102,6,115,10,83,101,114,105,101,115,68,97,116,97,115,8,71,108,111,98,97,\n"
            + "108,96,120,67,0,102,6,115,4,76,105,115,116,67,1,67,1,102,2,115,8,82,97,116,105,\n"
            + "111,110,97,108,67,1,67,2,102,2,115,8,82,97,116,105,111,110,97,108,67,1,67,6,102,\n"
            + "2,115,8,82,97,116,105,111,110,97,108,67,1,67,24,102,2,115,8,82,97,116,105,111,\n"
            + "110,97,108,67,1,67,120,67,0,67,6,67,1}");

    check(
        "BinarySerialize(Quantity(12, \"Hours\")) // Normal", //
        "{56,58,102,2,115,8,81,117,97,110,116,105,116,121,67,12,83,5,72,111,117,114,115}");

    double[] d0 = new double[] {1.1, 2.34, 4.11};
    double[] d1 = new double[] {3.1415, 100000.1234567, -100000.4711};
    ASTRealMatrix m = new ASTRealMatrix(new double[][] {d0, d1}, false);
    IExpr expr = F.Normal.of(F.BinarySerialize.of(m));
    check(
        expr.toString(), //
        "{56,58,193,35,2,2,3,154,153,153,153,153,153,241,63,184,30,133,235,81,184,2,64,\n"
            + "113,61,10,215,163,112,16,64,111,18,131,192,202,33,9,64,144,187,173,249,1,106,248,\n"
            + "64,82,39,160,137,7,106,248,192}");

    ASTRealVector v = new ASTRealVector(new double[] {1.1, 2.34, 4.11}, false);
    expr = F.Normal.of(F.BinarySerialize.of(v));
    check(
        expr.toString(), //
        "{56,58,193,35,1,3,154,153,153,153,153,153,241,63,184,30,133,235,81,184,2,64,113,\n"
            + "61,10,215,163,112,16,64}");

    check(
        "BinarySerialize(x->y) // Normal", //
        "{56,58,102,2,115,4,82,117,108,101,115,8,71,108,111,98,97,108,96,120,115,8,71,108,\n"
            + "111,98,97,108,96,121}");

    check(
        "BinarySerialize(x___) // Normal", //
        "{56,58,102,2,115,7,80,97,116,116,101,114,110,115,8,71,108,111,98,97,108,96,120,\n"
            + "102,0,115,17,66,108,97,110,107,78,117,108,108,83,101,113,117,101,110,99,101}");
    check(
        "BinarySerialize(x__) // Normal", //
        "{56,58,102,2,115,7,80,97,116,116,101,114,110,115,8,71,108,111,98,97,108,96,120,\n"
            + "102,0,115,13,66,108,97,110,107,83,101,113,117,101,110,99,101}");
    check(
        "BinarySerialize(___) // Normal", //
        "{56,58,102,0,115,17,66,108,97,110,107,78,117,108,108,83,101,113,117,101,110,99,\n"
            + "101}");
    check(
        "BinarySerialize(__) // Normal", //
        "{56,58,102,0,115,13,66,108,97,110,107,83,101,113,117,101,110,99,101}");
    check(
        "BinarySerialize(-25!) // Normal", //
        "{56,58,73,27,45,49,53,53,49,49,50,49,48,48,52,51,51,51,48,57,56,53,57,56,52,48,\n"
            + "48,48,48,48,48}");
    check(
        "BinarySerialize(25!) // Normal", //
        "{56,58,73,26,49,53,53,49,49,50,49,48,48,52,51,51,51,48,57,56,53,57,56,52,48,48,\n"
            + "48,48,48,48}");
    check(
        "BinarySerialize(500000) // Normal", //
        "{56,58,105,32,161,7,0}");
    check(
        "BinarySerialize(-500000) // Normal", //
        "{56,58,105,224,94,248,255}");
    check(
        "BinarySerialize(-1111) // Normal", //
        "{56,58,106,169,251}");
    check(
        "BinarySerialize(1111) // Normal", //
        "{56,58,106,87,4}");
    check(
        "BinarySerialize(42) // Normal", //
        "{56,58,67,42}");
    check(
        "BinarySerialize(-42) // Normal", //
        "{56,58,67,214}");

    check(
        "BinarySerialize(x_.) // Normal", //
        "{56,58,102,1,115,8,79,112,116,105,111,110,97,108,102,2,115,7,80,97,116,116,101,\n"
            + "114,110,115,8,71,108,111,98,97,108,96,120,102,0,115,5,66,108,97,110,107}");
    check(
        "BinarySerialize(x_Integer) // Normal", //
        "{56,58,102,2,115,7,80,97,116,116,101,114,110,115,8,71,108,111,98,97,108,96,120,\n"
            + "102,1,115,5,66,108,97,110,107,115,7,73,110,116,101,103,101,114}");
    check(
        "BinarySerialize(x_) // Normal", //
        "{56,58,102,2,115,7,80,97,116,116,101,114,110,115,8,71,108,111,98,97,108,96,120,\n"
            + "102,0,115,5,66,108,97,110,107}");
    check(
        "BinarySerialize(_) // Normal", //
        "{56,58,102,0,115,5,66,108,97,110,107}");
    check(
        "BinarySerialize(_Integer) // Normal", //
        "{56,58,102,1,115,5,66,108,97,110,107,115,7,73,110,116,101,103,101,114}");
    check(
        "BinarySerialize(-1000.14157+I*42.1) // Normal", //
        "{56,58,102,2,115,7,67,111,109,112,108,101,120,114,194,192,115,239,33,65,143,192,\n"
            + "114,205,204,204,204,204,12,69,64}");
    check(
        "BinarySerialize(-3.14157) // Normal", //
        "{56,58,114,253,193,192,115,239,33,9,192}");
    check(
        "BinarySerialize(0.75) // Normal", //
        "{56,58,114,0,0,0,0,0,0,232,63}");
    check(
        "BinarySerialize(2/3+7/4*I) // Normal", //
        "{56,58,102,2,115,7,67,111,109,112,108,101,120,102,2,115,8,82,97,116,105,111,110,\n"
            + "97,108,67,2,67,3,102,2,115,8,82,97,116,105,111,110,97,108,67,7,67,4}");
    check(
        "BinarySerialize(2/3) // Normal", //
        "{56,58,102,2,115,8,82,97,116,105,111,110,97,108,67,2,67,3}");

    check(
        "BinarySerialize(Plot) // Normal", //
        "{56,58,115,4,80,108,111,116}");
    check(
        "BinarySerialize(\"hello!\") // Normal", //
        "{56,58,83,6,104,101,108,108,111,33}");
    check(
        "BinarySerialize({}) // Normal", //
        "{56,58,102,0,115,4,76,105,115,116}");
    check(
        "BinarySerialize(f( )) // Normal", //
        "{56,58,102,0,115,8,71,108,111,98,97,108,96,102}");
    check(
        "BinarySerialize(f(g)) // Normal", //
        "{56,58,102,1,115,8,71,108,111,98,97,108,96,102,115,8,71,108,111,98,97,108,96,103}");
    check(
        "BinarySerialize(f(g(x,y))) // Normal", //
        "{56,58,102,1,115,8,71,108,111,98,97,108,96,102,102,2,115,8,71,108,111,98,97,108,\n"
            + "96,103,115,8,71,108,111,98,97,108,96,120,115,8,71,108,111,98,97,108,96,121}");
    check(
        "BinarySerialize(f(g,2)) // Normal", //
        "{56,58,102,2,115,8,71,108,111,98,97,108,96,102,115,8,71,108,111,98,97,108,96,103,\n"
            + "67,2}");
  }

  public void testBinaryDeserialize() {
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
        "{{1.1,2.34,4.11},\n"
            + //
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

    check(
        "BinaryDeserialize(ByteArray({56,58,105,32,161,7,0}))", //
        "500000");
    check(
        "BinaryDeserialize(ByteArray({56,58,105,224,94,248,255}))", //
        "-500000");

    check(
        "BinaryDeserialize(ByteArray({56,58,106,169,251}))", //
        "-1111");
    check(
        "BinaryDeserialize(ByteArray({56,58,106,87,4}))", //
        "1111");
    check(
        "BinaryDeserialize(ByteArray({56,58,67,42}))", //
        "42");
    check(
        "BinaryDeserialize(ByteArray({56,58,67,214}))", //
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

    check(
        "BinaryDeserialize(ByteArray({56,58,102,0,115,5,66,108,97,110,107}))", //
        "_");

    check(
        "BinaryDeserialize(ByteArray({56, 58, 102, 1, 115, 5, 66, 108, 97, 110, 107, 115, 7, 73, 110, 116, 101, 103, 101, 114}))", //
        "_Integer");

    checkNumeric(
        "BinaryDeserialize(ByteArray({56,58,102,2,115,7,67,111,109,112,108,101,120,114,194,192,115,239,33,65,143,192,114,205,204,204,204,204,12,69,64}))", //
        "-1000.14157+I*42.1");

    check(
        "BinaryDeserialize(ByteArray({56,58,114,253,193,192,115,239,33,9,192}))", //
        "-3.14157");

    check(
        "BinaryDeserialize(ByteArray({56,58,114,0,0,0,0,0,0,232,63}))", //
        "0.75");

    check(
        "BinaryDeserialize(ByteArray({56,58,102,2,115,7,67,111,109,112,108,101,120,102,2,115,8,82,97,116,105,111,110,\n"
            + "97,108,67,2,67,3,102,2,115,8,82,97,116,105,111,110,97,108,67,7,67,4}))", //
        "2/3+I*7/4");

    check(
        "BinaryDeserialize(ByteArray({56,58,102,2,115,8,82,97,116,105,111,110,97,108,67,2,67,3}))", //
        "2/3");

    check(
        "BinaryDeserialize(ByteArray({56,58,115,4,80,108,111,116}))", //
        "Plot");

    check(
        "BinaryDeserialize(ByteArray({56,58,83,6,104,101,108,108,111,33}))", //
        "hello!");

    check(
        "BinaryDeserialize(ByteArray({56,58,102,0,115,4,76,105,115,116}))", //
        "{}");

    check(
        "BinaryDeserialize(ByteArray({56,58,102,0,115,8,71,108,111,98,97,108,96,102}))", //
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

  public void testByteArrayData() {
    // serialize {0,42,192}
    check(
        "bytes = Normal(BinarySerialize(ByteArray({0, 42, 192})))", //
        "{56,58,66,3,0,42,192}");
    // deserialize {0,42,192} back
    check(
        "BinaryDeserialize(ByteArray({56,58,66,3,0,42,192})) // Normal", //
        "{0,42,192}");
  }

  public void testByteArray() {
    check(
        " ByteArray({1,2,3})", //
        "ByteArray[3 Bytes]");
    check(
        " ByteArray(Range(16))", //
        "ByteArray[16 Bytes]");
  }

  public void testByteArrayQ() {
    check(
        "ByteArrayQ(ByteArray({1,2,3}))", //
        "True");
    check(
        "ByteArrayQ(ByteArray(Range(16)))", //
        "True");
  }
}
