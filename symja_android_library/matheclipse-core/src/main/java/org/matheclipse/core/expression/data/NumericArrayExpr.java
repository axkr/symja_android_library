package org.matheclipse.core.expression.data;

import static com.google.common.base.Preconditions.checkArgument;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.LinearAlgebra;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumericArray;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.parser.trie.Trie;
import com.google.common.primitives.UnsignedBytes;
import com.google.common.primitives.UnsignedInts;
import com.google.common.primitives.UnsignedLong;
import it.unimi.dsi.fastutil.ints.IntList;

public class NumericArrayExpr extends DataExpr<Object> implements INumericArray, Externalizable {

  public static class RangeException extends Exception {
    private static final long serialVersionUID = 5301913995459242598L;

    RangeException(String message) {
      super(message);
    }
  }

  public static class TypeException extends Exception {
    private static final long serialVersionUID = -8868546084855177025L;

    TypeException(String message) {
      super(message);
    }
  }

  /** The UNDEFINED value type token. */
  public static final byte UNDEFINED = (byte) (0xFF);

  /**
   * See <a href=
   * "https://reference.wolfram.com/language/tutorial/WXFFormatDescription.html">WXFFormatDescription
   * - Section NUmeric Arrays</a>
   */
  public static final byte Integer8 = (byte) (0x00);

  /**
   * See <a href=
   * "https://reference.wolfram.com/language/tutorial/WXFFormatDescription.html">WXFFormatDescription
   * - Section NUmeric Arrays</a>
   */
  public static final byte Integer16 = (byte) (0x01);

  /**
   * See <a href=
   * "https://reference.wolfram.com/language/tutorial/WXFFormatDescription.html">WXFFormatDescription
   * - Section NUmeric Arrays</a>
   */
  public static final byte Integer32 = (byte) (0x02);
  /**
   * See <a href=
   * "https://reference.wolfram.com/language/tutorial/WXFFormatDescription.html">WXFFormatDescription
   * - Section NUmeric Arrays</a>
   */
  public static final byte Integer64 = (byte) (0x03);

  /**
   * See <a href=
   * "https://reference.wolfram.com/language/tutorial/WXFFormatDescription.html">WXFFormatDescription
   * - Section NUmeric Arrays</a>
   */
  public static final byte UnsignedInteger8 = (byte) (0x10);

  /**
   * See <a href=
   * "https://reference.wolfram.com/language/tutorial/WXFFormatDescription.html">WXFFormatDescription
   * - Section NUmeric Arrays</a>
   */
  public static final byte UnsignedInteger16 = (byte) (0x11);

  /**
   * See <a href=
   * "https://reference.wolfram.com/language/tutorial/WXFFormatDescription.html">WXFFormatDescription
   * - Section NUmeric Arrays</a>
   */
  public static final byte UnsignedInteger32 = (byte) (0x12);

  /**
   * See <a href=
   * "https://reference.wolfram.com/language/tutorial/WXFFormatDescription.html">WXFFormatDescription
   * - Section NUmeric Arrays</a>
   */
  public static final byte UnsignedInteger64 = (byte) (0x13);

  /**
   * See <a href=
   * "https://reference.wolfram.com/language/tutorial/WXFFormatDescription.html">WXFFormatDescription
   * - Section NUmeric Arrays</a>
   */
  public static final byte Real32 = (byte) (0x22);

  /**
   * See <a href=
   * "https://reference.wolfram.com/language/tutorial/WXFFormatDescription.html">WXFFormatDescription
   * - Section NUmeric Arrays</a>
   */
  public static final byte Real64 = (byte) (0x23);

  /**
   * See <a href=
   * "https://reference.wolfram.com/language/tutorial/WXFFormatDescription.html">WXFFormatDescription
   * - Section NUmeric Arrays</a>
   */
  public static final byte ComplexReal32 = (byte) (0x33);

  /**
   * See <a href=
   * "https://reference.wolfram.com/language/tutorial/WXFFormatDescription.html">WXFFormatDescription
   * - Section NUmeric Arrays</a>
   */
  public static final byte ComplexReal64 = (byte) (0x34);

  private static final Map<String, Byte> TYPE_MAP = new HashMap<String, Byte>();

  private static final Map<Byte, String> TYPE_STRING_MAP = new HashMap<Byte, String>();

  static {
    TYPE_MAP.put("Integer8", Integer8);
    TYPE_MAP.put("Integer16", Integer16);
    TYPE_MAP.put("Integer32", Integer32);
    TYPE_MAP.put("Integer64", Integer64);
    TYPE_MAP.put("UnsignedInteger8", UnsignedInteger8);
    TYPE_MAP.put("UnsignedInteger16", UnsignedInteger16);
    TYPE_MAP.put("UnsignedInteger32", UnsignedInteger32);
    TYPE_MAP.put("UnsignedInteger64", UnsignedInteger64);
    TYPE_MAP.put("Real32", Real32);
    TYPE_MAP.put("Real64", Real64);
    TYPE_MAP.put("ComplexReal32", ComplexReal32);
    TYPE_MAP.put("ComplexReal64", ComplexReal64);

    TYPE_STRING_MAP.put(Integer8, "Integer8");
    TYPE_STRING_MAP.put(Integer16, "Integer16");
    TYPE_STRING_MAP.put(Integer32, "Integer32");
    TYPE_STRING_MAP.put(Integer64, "Integer64");
    TYPE_STRING_MAP.put(UnsignedInteger8, "UnsignedInteger8");
    TYPE_STRING_MAP.put(UnsignedInteger16, "UnsignedInteger16");
    TYPE_STRING_MAP.put(UnsignedInteger32, "UnsignedInteger32");
    TYPE_STRING_MAP.put(UnsignedInteger64, "UnsignedInteger64");
    TYPE_STRING_MAP.put(Real32, "Real32");
    TYPE_STRING_MAP.put(Real64, "Real64");
    TYPE_STRING_MAP.put(ComplexReal32, "ComplexReal32");
    TYPE_STRING_MAP.put(ComplexReal64, "ComplexReal64");
  }

  private static boolean arrayComplexFloatRecursive(IAST nestedListsOfValues, int level,
      float[] floatArr, int[] index) throws RangeException, TypeException {
    level--;
    for (int i = 1; i < nestedListsOfValues.size(); i++) {
      IExpr arg = nestedListsOfValues.get(i);
      if (level == 0) {
        if (arg.isList()) {
          return false;
        }
        Complex value = arg.evalfc();
        floatArr[index[0]++] = (float) value.getReal();
        floatArr[index[0]++] = (float) value.getImaginary();
      } else {
        if (!arg.isList() || !arrayComplexFloatRecursive((IAST) arg, level, floatArr, index)) {
          return false;
        }
      }
    }
    return true;
  }

  private static boolean arrayComplexDoubleRecursive(IAST nestedListsOfValues, int level,
      double[] doubleArr, int[] index) throws RangeException, TypeException {
    level--;
    for (int i = 1; i < nestedListsOfValues.size(); i++) {
      IExpr arg = nestedListsOfValues.get(i);
      if (level == 0) {
        if (arg.isList()) {
          return false;
        }
        Complex value = arg.evalfc();
        doubleArr[index[0]++] = value.getReal();
        doubleArr[index[0]++] = value.getImaginary();
      } else {
        if (!arg.isList() || !arrayDoubleRecursive((IAST) arg, level, doubleArr, index)) {
          return false;
        }
      }
    }
    return true;
  }

  private static boolean arrayDoubleRecursive(IAST nestedListsOfValues, int level,
      double[] doubleArr, int[] index) throws RangeException, TypeException {
    level--;
    for (int i = 1; i < nestedListsOfValues.size(); i++) {
      IExpr arg = nestedListsOfValues.get(i);
      if (level == 0) {
        if (arg.isList()) {
          return false;
        }
        doubleArr[index[0]++] = arg.evalf();
      } else {
        if (!arg.isList() || !arrayDoubleRecursive((IAST) arg, level, doubleArr, index)) {
          return false;
        }
      }
    }
    return true;
  }

  private static boolean arrayFloatRecursive(IAST nestedListsOfValues, int level, float[] floatArr,
      int[] index) throws RangeException, TypeException {
    level--;
    for (int i = 1; i < nestedListsOfValues.size(); i++) {
      IExpr arg = nestedListsOfValues.get(i);
      if (level == 0) {
        if (arg.isList()) {
          return false;
        }
        floatArr[index[0]++] = (float) arg.evalf();
      } else {
        if (!arg.isList() || !arrayFloatRecursive((IAST) arg, level, floatArr, index)) {
          return false;
        }
      }
    }
    return true;
  }

  private static boolean arrayByteRecursive(IAST nestedListsOfValues, int level, byte[] byteArr,
      int[] index) throws RangeException, TypeException {
    level--;
    for (int i = 1; i < nestedListsOfValues.size(); i++) {
      IExpr arg = nestedListsOfValues.get(i);
      if (level == 0) {
        byte b = getByte(arg);
        byteArr[index[0]++] = b;
      } else {
        if (!arg.isList() || !arrayByteRecursive((IAST) arg, level, byteArr, index)) {
          return false;
        }
      }
    }
    return true;
  }

  private static boolean arrayShortRecursive(IAST nestedListsOfValues, int level, short[] shortArr,
      int[] index) throws RangeException, TypeException {
    level--;
    for (int i = 1; i < nestedListsOfValues.size(); i++) {
      IExpr arg = nestedListsOfValues.get(i);
      if (level == 0) {
        if (!arg.isInteger()) {
          throw new TypeException("Not a valid Integers type");
        }
        int value = ((IReal) arg).toInt();
        if (value < (Short.MIN_VALUE) || value > Short.MAX_VALUE) {
          throw new RangeException("Value " + value + " out of Integer16 range");
        }
        shortArr[index[0]++] = (short) value;
      } else {
        if (!arg.isList() || !arrayShortRecursive((IAST) arg, level, shortArr, index)) {
          return false;
        }
      }
    }
    return true;
  }

  private static boolean arrayIntRecursive(IAST nestedListsOfValues, int level, int[] intArr,
      int[] index) throws RangeException, TypeException {
    level--;
    for (int i = 1; i < nestedListsOfValues.size(); i++) {
      IExpr arg = nestedListsOfValues.get(i);
      if (level == 0) {
        if (!arg.isInteger()) {
          throw new TypeException("Not a valid Integers type");
        }
        intArr[index[0]++] = ((IReal) arg).toInt();
      } else {
        if (!arg.isList() || !arrayIntRecursive((IAST) arg, level, intArr, index)) {
          return false;
        }
      }
    }
    return true;
  }

  private static boolean arrayLongRecursive(IAST nestedListsOfValues, int level, long[] longArr,
      int[] index) throws RangeException, TypeException {
    level--;
    for (int i = 1; i < nestedListsOfValues.size(); i++) {
      IExpr arg = nestedListsOfValues.get(i);
      if (level == 0) {
        if (!arg.isInteger()) {
          throw new TypeException("Not a valid Integers type");
        }
        longArr[index[0]++] = ((IReal) arg).toLong();
      } else {
        if (!arg.isList() || !arrayLongRecursive((IAST) arg, level, longArr, index)) {
          return false;
        }
      }
    }
    return true;
  }

  private static boolean arrayUnsignedByteRecursive(IAST nestedListsOfValues, int level,
      byte[] byteArr, int[] index) throws RangeException, TypeException {
    level--;
    for (int i = 1; i < nestedListsOfValues.size(); i++) {
      IExpr arg = nestedListsOfValues.get(i);
      if (level == 0) {
        byte b = getUnsignedByte(arg);
        byteArr[index[0]++] = b;
      } else {
        if (!arg.isList() || !arrayUnsignedByteRecursive((IAST) arg, level, byteArr, index)) {
          return false;
        }
      }
    }
    return true;
  }

  public static byte getByte(IExpr arg) throws TypeException, RangeException {
    if (!arg.isInteger()) {
      throw new TypeException("Not a valid Integers type");
    }
    int value = arg.toIntDefault();
    if (value < (Byte.MIN_VALUE) || value > Byte.MAX_VALUE) {
      throw new RangeException("Value " + value + " out of Integer8 range");
    }
    byte b = (byte) value;
    return b;
  }

  public static byte getUnsignedByte(IExpr arg) throws IllegalArgumentException, TypeException {
    if (!arg.isInteger()) {
      throw new TypeException("Not a valid Integers type");
    }
    long value = ((IReal) arg).toLong();
    byte b = UnsignedBytes.checkedCast(value);
    return b;
  }

  /**
   * Returns the {@code short} value that, when treated as unsigned, is equal to {@code value}, if
   * possible.
   *
   * @param value a value between 0 and 2<sup>16</sup>-1 inclusive
   * @return the {@code short} value that, when treated as unsigned, equals {@code value}
   * @throws IllegalArgumentException if {@code value} is negative or greater than or equal to
   *         2<sup>16</sup>
   * @since 21.0
   */
  private static short checkedCastUnsignedShort(int value) {
    checkArgument((value >> Short.SIZE) == 0, "out of range: %s", value);
    return (short) value;
  }

  private static boolean arrayUnsignedShortRecursive(IAST nestedListsOfValues, int level,
      short[] shortArr, int[] index) throws RangeException, TypeException {
    level--;
    for (int i = 1; i < nestedListsOfValues.size(); i++) {
      IExpr arg = nestedListsOfValues.get(i);
      if (level == 0) {
        if (!arg.isInteger()) {
          throw new TypeException("Not a valid Integers type");
        }
        int value = ((IReal) arg).toInt();
        shortArr[index[0]++] = checkedCastUnsignedShort(value);
      } else {
        if (!arg.isList() || !arrayUnsignedShortRecursive((IAST) arg, level, shortArr, index)) {
          return false;
        }
      }
    }
    return true;
  }

  private static boolean arrayUnsignedIntRecursive(IAST nestedListsOfValues, int level,
      int[] intArr, int[] index) throws RangeException, TypeException {
    level--;
    for (int i = 1; i < nestedListsOfValues.size(); i++) {
      IExpr arg = nestedListsOfValues.get(i);
      if (level == 0) {
        if (!arg.isInteger()) {
          throw new TypeException("Not a valid Integers type");
        }
        long value = ((IReal) arg).toLong();
        intArr[index[0]++] = UnsignedInts.checkedCast(value);
      } else {
        if (!arg.isList() || !arrayUnsignedIntRecursive((IAST) arg, level, intArr, index)) {
          return false;
        }
      }
    }
    return true;
  }

  private static boolean arrayUnsignedLongRecursive(IAST nestedListsOfValues, int level,
      long[] longArr, int[] index) throws RangeException, TypeException {
    level--;
    for (int i = 1; i < nestedListsOfValues.size(); i++) {
      IExpr arg = nestedListsOfValues.get(i);
      if (level == 0) {
        if (!arg.isInteger()) {
          throw new TypeException("Not a valid Integers type");
        }
        BigInteger value = ((IInteger) arg).toBigNumerator();
        UnsignedLong uint64 = UnsignedLong.valueOf(value);
        longArr[index[0]++] = uint64.longValue();
      } else {
        if (!arg.isList() || !arrayUnsignedLongRecursive((IAST) arg, level, longArr, index)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Return the type of this expression as byte value defined as:
   *
   * <ul>
   * <li>{@link NumericArrayExpr#UnsignedInteger8},
   * <li>{@link NumericArrayExpr#UnsignedInteger16},
   * <li>{@link NumericArrayExpr#UnsignedInteger32},
   * <li>{@link NumericArrayExpr#UnsignedInteger64},
   * <li>{@link NumericArrayExpr#Integer8},
   * <li>{@link NumericArrayExpr#Integer16},
   * <li>{@link NumericArrayExpr#Integer32},
   * <li>{@link NumericArrayExpr#Integer64},
   * <li>{@link NumericArrayExpr#Real32},
   * <li>{@link NumericArrayExpr#Real64},
   * <li>{@link NumericArrayExpr#ComplexReal32},
   * <li>{@link NumericArrayExpr#ComplexReal64}
   * </ul>
   *
   * @param typeAsString
   * @return {@link NumericArrayExpr#UNDEFINED} if the type name isn't defined
   */
  public static byte toType(String typeAsString) {
    Byte result = TYPE_MAP.get(typeAsString);
    if (result != null) {
      return result;
    }
    return UNDEFINED;
  }

  public static NumericArrayExpr newInstance(final Object value, int[] dimension, byte type) {
    return new NumericArrayExpr(value, dimension, type);
  }

  /**
   * Create an appropriate <code>NumericArrayExpr</code> from the list. if <code>type==UNDEFINED
   * </code> search for the suitable type. The search range can de restricted by the <code>symbol
   * </code> parameter.
   *
   * @param list
   * @param type if {@link #UNDEFINED} search for suitable type
   * @param symbol one of {@link S#All}, {@link S#Integers}, {@link S#Reals}, {@link S#Complexes}
   * @return <code>null</code> if list cannot be converted into a numeric array
   */
  public static NumericArrayExpr newListByType(final IAST list, byte type, IBuiltInSymbol symbol) {
    if (type == UNDEFINED) {
      if (symbol == S.Integers || symbol == S.All) {
        try {
          NumericArrayExpr result = newList(list, Integer8);
          if (result != null) {
            return result;
          }
        } catch (ArithmeticException | IllegalArgumentException | RangeException rex) {
          try {
            NumericArrayExpr result = newList(list, Integer16);
            if (result != null) {
              return result;
            }
          } catch (ArithmeticException | IllegalArgumentException | RangeException rex2) {
            try {
              NumericArrayExpr result = newList(list, Integer32);
              if (result != null) {
                return result;
              }
            } catch (ArithmeticException | IllegalArgumentException | RangeException rex3) {
              try {
                NumericArrayExpr result = newList(list, Integer64);
                if (result != null) {
                  return result;
                }
              } catch (ArithmeticException | IllegalArgumentException | RangeException
                  | ArgumentTypeException | TypeException e) {
              }
            } catch (ArgumentTypeException | TypeException tex) {
            }
          } catch (ArgumentTypeException | TypeException tex) {
          }
        } catch (ArgumentTypeException | TypeException tex) {
        }
      }

      if (symbol == S.Reals || symbol == S.All) {
        try {
          NumericArrayExpr result = newList(list, Real32);
          if (result != null) {
            return result;
          }
        } catch (ArithmeticException | IllegalArgumentException | RangeException rex) {
          try {
            NumericArrayExpr result = newList(list, Real64);
            if (result != null) {
              return result;
            }
          } catch (ArithmeticException | IllegalArgumentException | RangeException
              | ArgumentTypeException | TypeException e) {
          }
        } catch (ArgumentTypeException | TypeException tex) {
        }
      }
      if (symbol == S.Complexes || symbol == S.All) {
        try {
          NumericArrayExpr result = newList(list, ComplexReal32);
          if (result != null) {
            return result;
          }
        } catch (ArithmeticException | IllegalArgumentException | RangeException rex) {
          try {
            NumericArrayExpr result = newList(list, ComplexReal64);
            if (result != null) {
              return result;
            }
          } catch (ArithmeticException | RangeException | ArgumentTypeException | TypeException e) {
          }
        } catch (ArgumentTypeException | TypeException tex) {
        }
      }
      return null;
    }

    try {
      return newList(list, type);
    } catch (RangeException | TypeException e) {
    }
    return null;
  }

  private static NumericArrayExpr newList(final IAST list, byte type)
      throws RangeException, TypeException {
    int[] dimension = null;
    final IntList dims = LinearAlgebra.dimensions(list);
    final int dimsSize = dims.size();
    if (dimsSize > 0) {
      int size = 1;
      dimension = new int[dimsSize];
      for (int i = 0; i < dimsSize; i++) {
        dimension[i] = dims.getInt(i);
        size *= dimension[i];
      }

      try {
        int[] index = new int[1];
        switch (type) {
          case Integer8:
            byte[] byteArr = new byte[size];
            if (arrayByteRecursive(list, dimension.length, byteArr, index)) {
              return new NumericArrayExpr(byteArr, dimension, type);
            }
            break;
          case Integer16:
            short[] shortArr = new short[size];
            if (arrayShortRecursive(list, dimension.length, shortArr, index)) {
              return new NumericArrayExpr(shortArr, dimension, type);
            }
            break;
          case Integer32:
            int[] intArr = new int[size];
            if (arrayIntRecursive(list, dimension.length, intArr, index)) {
              return new NumericArrayExpr(intArr, dimension, type);
            }
            break;
          case Integer64:
            long[] longArr = new long[size];
            if (arrayLongRecursive(list, dimension.length, longArr, index)) {
              return new NumericArrayExpr(longArr, dimension, type);
            }
            break;
          case UnsignedInteger8:
            byte[] unsignedByteArr = new byte[size];
            if (arrayUnsignedByteRecursive(list, dimension.length, unsignedByteArr, index)) {
              return new NumericArrayExpr(unsignedByteArr, dimension, type);
            }
            break;
          case UnsignedInteger16:
            short[] unsignedShortArr = new short[size];
            if (arrayUnsignedShortRecursive(list, dimension.length, unsignedShortArr, index)) {
              return new NumericArrayExpr(unsignedShortArr, dimension, type);
            }
            break;
          case UnsignedInteger32:
            int[] unsignedIntArr = new int[size];
            if (arrayUnsignedIntRecursive(list, dimension.length, unsignedIntArr, index)) {
              return new NumericArrayExpr(unsignedIntArr, dimension, type);
            }
            break;
          case UnsignedInteger64:
            long[] unsignedLongArr = new long[size];
            if (arrayUnsignedLongRecursive(list, dimension.length, unsignedLongArr, index)) {
              return new NumericArrayExpr(unsignedLongArr, dimension, type);
            }
            break;
          case Real32:
            float[] floatArr = new float[size];
            if (arrayFloatRecursive(list, dimension.length, floatArr, index)) {
              return new NumericArrayExpr(floatArr, dimension, type);
            }
            break;
          case Real64: {
            double[] doubleArr = new double[size];
            if (arrayDoubleRecursive(list, dimension.length, doubleArr, index)) {
              return new NumericArrayExpr(doubleArr, dimension, type);
            }
          }
            break;
          case ComplexReal32: {
            float[] complexFloatArr = new float[size * 2];
            if (arrayComplexFloatRecursive(list, dimension.length, complexFloatArr, index)) {
              return new NumericArrayExpr(complexFloatArr, dimension, type);
            }
          }
            break;
          case ComplexReal64: {
            double[] complexDoubleArr = new double[size * 2];
            if (arrayComplexDoubleRecursive(list, dimension.length, complexDoubleArr, index)) {
              return new NumericArrayExpr(complexDoubleArr, dimension, type);
            }
          }
            break;
        }
      } catch (RuntimeException rex) {

      }
    }
    return null;
  }

  private static void normalRecursive(float[] floatArray, IASTMutable list, int[] dimension,
      int position, int[] index) {
    int size = dimension[position];
    if (dimension.length - 1 == position) {
      for (int i = 1; i <= size; i++) {
        list.set(i, F.num(floatArray[index[0]++]));
      }
      return;
    }
    int size2 = dimension[position + 1];
    for (int i = 1; i <= size; i++) {
      IASTMutable currentList = F.astMutable(S.List, size2);
      list.set(i, currentList);
      normalRecursive(floatArray, currentList, dimension, position + 1, index);
    }
  }

  private static void normalRecursive(double[] doubleArray, IASTMutable list, int[] dimension,
      int position, int[] index) {
    int size = dimension[position];
    if (dimension.length - 1 == position) {
      for (int i = 1; i <= size; i++) {
        list.set(i, F.num(doubleArray[index[0]++]));
      }
      return;
    }
    int size2 = dimension[position + 1];
    for (int i = 1; i <= size; i++) {
      IASTMutable currentList = F.astMutable(S.List, size2);
      list.set(i, currentList);
      normalRecursive(doubleArray, currentList, dimension, position + 1, index);
    }
  }

  private static void normalRecursiveComplex(float[] floatArray, IASTMutable list, int[] dimension,
      int position, int[] index) {
    int size = dimension[position];
    if (dimension.length - 1 == position) {
      for (int i = 1; i <= size; i++) {
        list.set(i, F.complexNum(floatArray[index[0]++], floatArray[index[0]++]));
      }
      return;
    }
    int size2 = dimension[position + 1];
    for (int i = 1; i <= size; i++) {
      IASTMutable currentList = F.astMutable(S.List, size2);
      list.set(i, currentList);
      normalRecursiveComplex(floatArray, currentList, dimension, position + 1, index);
    }
  }

  private static void normalRecursiveComplex(double[] doubleArray, IASTMutable list,
      int[] dimension, int position, int[] index) {
    int size = dimension[position];
    if (dimension.length - 1 == position) {
      for (int i = 1; i <= size; i++) {
        list.set(i, F.complexNum(doubleArray[index[0]++], doubleArray[index[0]++]));
      }
      return;
    }
    int size2 = dimension[position + 1];
    for (int i = 1; i <= size; i++) {
      IASTMutable currentList = F.astMutable(S.List, size2);
      list.set(i, currentList);
      normalRecursiveComplex(doubleArray, currentList, dimension, position + 1, index);
    }
  }

  private static void normalRecursive(byte[] byteArray, boolean unsigned, IASTMutable list,
      int[] dimension, int position, int[] index) {
    int size = dimension[position];
    if (dimension.length - 1 == position) {
      for (int i = 1; i <= size; i++) {
        IInteger intValue = unsigned ? F.ZZ(Byte.toUnsignedInt(byteArray[index[0]++]))
            : F.ZZ(byteArray[index[0]++]);
        list.set(i, intValue);
      }
      return;
    }
    int size2 = dimension[position + 1];
    for (int i = 1; i <= size; i++) {
      IASTMutable currentList = F.astMutable(S.List, size2);
      list.set(i, currentList);
      normalRecursive(byteArray, unsigned, currentList, dimension, position + 1, index);
    }
  }

  private static void normalRecursive(short[] shortArray, boolean unsigned, IASTMutable list,
      int[] dimension, int position, int[] index) {
    int size = dimension[position];
    if (dimension.length - 1 == position) {
      for (int i = 1; i <= size; i++) {
        IInteger intValue = unsigned ? F.ZZ(Short.toUnsignedInt(shortArray[index[0]++]))
            : F.ZZ(shortArray[index[0]++]);
        list.set(i, intValue);
      }
      return;
    }
    int size2 = dimension[position + 1];
    for (int i = 1; i <= size; i++) {
      IASTMutable currentList = F.astMutable(S.List, size2);
      list.set(i, currentList);
      normalRecursive(shortArray, unsigned, currentList, dimension, position + 1, index);
    }
  }

  private static void normalRecursive(int[] intArray, boolean unsigned, IASTMutable list,
      int[] dimension, int position, int[] index) {
    int size = dimension[position];
    if (dimension.length - 1 == position) {
      for (int i = 1; i <= size; i++) {
        long value = unsigned ? Integer.toUnsignedLong(intArray[index[0]++]) //
            : intArray[index[0]++];
        list.set(i, F.ZZ(value));
      }
      return;
    }
    int size2 = dimension[position + 1];
    for (int i = 1; i <= size; i++) {
      IASTMutable currentList = F.astMutable(S.List, size2);
      list.set(i, currentList);
      normalRecursive(intArray, unsigned, currentList, dimension, position + 1, index);
    }
  }

  private static void normalRecursive(long[] longArray, boolean unsigned, IASTMutable list,
      int[] dimension, int position, int[] index) {
    int size = dimension[position];
    if (dimension.length - 1 == position) {
      for (int i = 1; i <= size; i++) {
        IInteger intValue =
            unsigned ? F.ZZ(UnsignedLong.fromLongBits(longArray[index[0]++]).bigIntegerValue())
                : F.ZZ(longArray[index[0]++]);
        list.set(i, intValue);
      }
      return;
    }
    int size2 = dimension[position + 1];
    for (int i = 1; i <= size; i++) {
      IASTMutable currentList = F.astMutable(S.List, size2);
      list.set(i, currentList);
      normalRecursive(longArray, unsigned, currentList, dimension, position + 1, index);
    }
  }

  /** The dimension of the numeric array. */
  int[] fDimension;

  byte fType;

  public NumericArrayExpr() {
    super(S.NumericArray, null);
    fType = UNDEFINED;
  }

  public NumericArrayExpr(final Object array, int[] dimension, byte type) {
    super(S.NumericArray, array);
    fDimension = dimension;
    fType = type;
  }

  @Override
  public IExpr copy() {
    return new NumericArrayExpr(fData, fDimension, fType);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof NumericArrayExpr) {
      NumericArrayExpr numericArray = (NumericArrayExpr) obj;
      if (fType == numericArray.fType) {
        switch (fType) {
          case Integer8:
            return Arrays.equals((byte[]) fData, (byte[]) numericArray.fData);
          case Integer16:
            return Arrays.equals((short[]) fData, (short[]) numericArray.fData);
          case Integer32:
            return Arrays.equals((int[]) fData, (int[]) numericArray.fData);
          case Integer64:
            return Arrays.equals((long[]) fData, (long[]) numericArray.fData);
          case Real32:
            return Arrays.equals((float[]) fData, (float[]) numericArray.fData);
          case Real64:
            return Arrays.equals((double[]) fData, (double[]) numericArray.fData);
          case ComplexReal32:
            return Arrays.equals((float[]) fData, (float[]) numericArray.fData);
          case ComplexReal64:
            return Arrays.equals((double[]) fData, (double[]) numericArray.fData);
        }
      }
    }
    return false;
  }

  @Override
  public IExpr get(int position) {
    int[] dims = getDimension();

    final int partSize = 1;

    int len = 0;
    int[] partIndex = new int[dims.length];
    int count = 0;
    partIndex[0] = position;

    for (int i = partSize; i < dims.length; i++) {
      partIndex[i] = -1;
      count++;
    }
    if (count == 0 && partSize == dims.length) {
      // return fData.get(partIndex);
      return F.NIL;
    }
    int[] newDimension = new int[count];
    count = 0;
    for (int i = 0; i < partIndex.length; i++) {
      if (partIndex[i] == (-1)) {
        len++;
        newDimension[count++] = dims[i];
      }
    }
    final Trie<int[], IExpr> value = Config.TRIE_INT2EXPR_BUILDER.build();

    return new NumericArrayExpr(value, newDimension, fType);
  }

  @Override
  public int[] getDimension() {
    return fDimension;
  }

  private IExpr getPart(int[] position) {
    int index = 1;
    for (int i = 0; i < position.length; i++) {
      index *= position[i];
    }

    return F.NIL;
  }

  @Override
  public String getStringType() {
    return TYPE_STRING_MAP.get(fType);
  }

  /**
   * Return the type of this expression as byte value defined as:
   *
   * <ul>
   * <li>{@link NumericArrayExpr#UnsignedInteger8},
   * <li>{@link NumericArrayExpr#UnsignedInteger16},
   * <li>{@link NumericArrayExpr#UnsignedInteger32},
   * <li>{@link NumericArrayExpr#UnsignedInteger64},
   * <li>{@link NumericArrayExpr#Integer8},
   * <li>{@link NumericArrayExpr#Integer16},
   * <li>{@link NumericArrayExpr#Integer32},
   * <li>{@link NumericArrayExpr#Integer64},
   * <li>{@link NumericArrayExpr#Real32},
   * <li>{@link NumericArrayExpr#Real64},
   * <li>{@link NumericArrayExpr#ComplexReal32},
   * <li>{@link NumericArrayExpr#ComplexReal64}
   * </ul>
   */
  @Override
  public byte getType() {
    return fType;
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 541 : 541 + fType;
  }

  @Override
  public int hierarchy() {
    return NUMERICARRAYID;
  }

  @Override
  public boolean isNumericArray() {
    return true;
  }

  @Override
  public IASTMutable normal(boolean nilIfUnevaluated) {
    if (fDimension.length > 0) {
      return normalAppendable(S.List, fDimension);
    }
    return F.headAST0(S.List);
  }

  @Override
  public IASTMutable normal(int[] dims) {
    return normalAppendable(S.List, dims);
  }

  private IASTMutable normalAppendable(IExpr head, int[] dims) {
    IASTMutable list = F.astMutable(head, dims[0]);
    int[] index = new int[1];
    switch (fType) {
      case Integer8:
        normalRecursive((byte[]) fData, false, list, dims, 0, index);
        break;
      case Integer16:
        normalRecursive((short[]) fData, false, list, dims, 0, index);
        break;
      case Integer32:
        normalRecursive((int[]) fData, false, list, dims, 0, index);
        break;
      case Integer64:
        normalRecursive((long[]) fData, false, list, dims, 0, index);
        break;
      case UnsignedInteger8:
        normalRecursive((byte[]) fData, true, list, dims, 0, index);
        break;
      case UnsignedInteger16:
        normalRecursive((short[]) fData, true, list, dims, 0, index);
        break;
      case UnsignedInteger32:
        normalRecursive((int[]) fData, true, list, dims, 0, index);
        break;
      case UnsignedInteger64:
        normalRecursive((long[]) fData, true, list, dims, 0, index);
        break;
      case Real32:
        normalRecursive((float[]) fData, list, dims, 0, index);
        break;
      case Real64:
        normalRecursive((double[]) fData, list, dims, 0, index);
        break;
      case ComplexReal32:
        normalRecursiveComplex((float[]) fData, list, dims, 0, index);
        break;
      case ComplexReal64:
        normalRecursiveComplex((double[]) fData, list, dims, 0, index);
        break;
    }

    return list;
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    byte type = in.readByte();
    fType = type;
    fDimension = (int[]) in.readObject();
    fData = in.readObject();
  }

  @Override
  public int size() {
    return fDimension[0] + 1;
  }

  @Override
  public Object toData() {
    return fData;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("NumericArray(Type: ");
    buf.append(TYPE_STRING_MAP.get(fType));
    buf.append(" Dimensions: {");
    for (int i = 0; i < fDimension.length; i++) {
      buf.append(fDimension[i]);
      if (i < fDimension.length - 1) {
        buf.append(",");
      }
    }
    buf.append("})");
    return buf.toString();
  }

  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeByte(fType);
    output.writeObject(fDimension);
    output.writeObject(fData);
  }
}
