package org.matheclipse.core.expression;

import java.io.ByteArrayOutputStream;
import java.util.function.IntConsumer;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.linear.RealVector;

/**
 * Low-level byte encodings of the WXF format: varint lengths and little-endian doubles. Kept apart
 * from {@link WL}, so that the WXF reader and writer classes do not call back into {@link WL}.
 */
final class WXFVarint {

  private WXFVarint() {}

  /**
   * Parse a readable binary buffer for a positive varint encoded integer.
   *
   * @return the decoded value and the position after the last byte read
   */
  static int[] parseVarint(byte[] array, int position) {
    int length = 0;
    // a non-negative int needs at most 5 varint bytes, the 5th with 3 bits
    for (int shift = 0; shift < 35; shift += 7) {
      byte next_byte = array[position++];
      if (shift == 28 && (next_byte & 0xF8) != 0) {
        throw new UnsupportedOperationException("Varint exceeds the range of an int.");
      }
      length |= (next_byte & 0x7F) << shift;
      if ((next_byte & 0x80) == 0) {
        return new int[] {length, position};
      }
    }
    throw new UnsupportedOperationException("Varint exceeds the range of an int.");
  }

  /** Serialize <code>length</code> into varint bytes and return them as a byte array. */
  static byte[] varintBytes(int length) {
    byte[] buf = new byte[9];
    if (length < 0) {
      throw new UnsupportedOperationException("Negative values cannot be encoded as varint.");
    }
    int count = 0;
    while (true) {
      int next = (length & 0x7f);
      length >>= 7;
      if (length != 0) {
        buf[count] = (byte) (next | 0x80);
        count += 1;
      } else {
        buf[count] = (byte) next;
        count += 1;
        break;
      }
    }
    byte[] result = new byte[count];
    System.arraycopy(buf, 0, result, 0, count);
    return result;
  }

  /**
   * Write the 8 bytes of a <code>double</code> in little-endian order, as the WXF format stores a
   * Real64.
   *
   * @param out receives one byte per call
   */
  static void writeDoubleLE(double d, IntConsumer out) {
    long bits = Double.doubleToRawLongBits(d);
    for (int shift = 0; shift < 64; shift += 8) {
      out.accept((byte) ((bits >> shift) & 0xff));
    }
  }

  /**
   * The rank, the dimension and the little-endian Real64 entries of a packed vector: the bytes
   * which follow the <code>PackedArray</code> and <code>Real64</code> tokens.
   *
   * @param vector the vector to encode
   * @return the encoded bytes
   */
  static byte[] packedReal64(RealVector vector) {
    final int n = vector.getDimension();
    ByteArrayOutputStream out = new ByteArrayOutputStream(10 + 8 * n);
    out.write(0x01);
    writeVarint(out, n);
    for (int i = 0; i < n; i++) {
      writeDoubleLE(vector.getEntry(i), out::write);
    }
    return out.toByteArray();
  }

  /**
   * The rank, the dimensions and the little-endian Real64 entries in row order of a packed matrix:
   * the bytes which follow the <code>PackedArray</code> and <code>Real64</code> tokens.
   *
   * @param matrix the matrix to encode
   * @return the encoded bytes
   */
  static byte[] packedReal64(RealMatrix matrix) {
    final int rows = matrix.getRowDimension();
    final int columns = matrix.getColumnDimension();
    ByteArrayOutputStream out = new ByteArrayOutputStream(19 + 8 * rows * columns);
    out.write(0x02);
    writeVarint(out, rows);
    writeVarint(out, columns);
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        writeDoubleLE(matrix.getEntry(i, j), out::write);
      }
    }
    return out.toByteArray();
  }

  private static void writeVarint(ByteArrayOutputStream out, int value) {
    byte[] bytes = varintBytes(value);
    out.write(bytes, 0, bytes.length);
  }
}
