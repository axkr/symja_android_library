package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.LinearAlgebra;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumericArray;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.parser.trie.Trie;
import org.matheclipse.parser.trie.TrieNode;
import org.matheclipse.parser.trie.Tries;

public class NumericArrayExpr extends DataExpr<Object> implements INumericArray, Externalizable {

  private static final Map<String, Integer> TYPE_MAP = new HashMap<String, Integer>();

  private static final Map<Integer, String> TYPE_STRING_MAP = new HashMap<Integer, String>();

  private static final int UNDEFINED_TYPE = -1;

  /** Byte type */
  private static final int INTEGER8_TYPE = 1;

  /** Short type */
  private static final int INTEGER16_TYPE = 2;

  /** Integer type */
  private static final int INTEGER32_TYPE = 3;

  /** Long type */
  private static final int INTEGER64_TYPE = 4;

  /** Float type */
  private static final int FLOAT_TYPE = 5;

  /** Double type */
  private static final int DOUBLE_TYPE = 6;

  /** Float double type for real and imaginary part of a complex number */
  private static final int COMPLEX_FLOAT_TYPE = 7;

  /** Complex double type for real and imaginary part of a complex number */
  private static final int COMPLEX_DOUBLE_TYPE = 8;

  static {
    TYPE_MAP.put("Integer8", INTEGER8_TYPE);
    TYPE_MAP.put("Integer16", INTEGER16_TYPE);
    TYPE_MAP.put("Integer32", INTEGER32_TYPE);
    TYPE_MAP.put("Integer64", INTEGER64_TYPE);
    TYPE_MAP.put("Real32", FLOAT_TYPE);
    TYPE_MAP.put("Real64", DOUBLE_TYPE);
    TYPE_MAP.put("ComplexReal32", COMPLEX_FLOAT_TYPE);
    TYPE_MAP.put("ComplexReal64", COMPLEX_DOUBLE_TYPE);

    TYPE_STRING_MAP.put(INTEGER8_TYPE, "Integer8");
    TYPE_STRING_MAP.put(INTEGER16_TYPE, "Integer16");
    TYPE_STRING_MAP.put(INTEGER32_TYPE, "Integer32");
    TYPE_STRING_MAP.put(INTEGER64_TYPE, "Integer64");
    TYPE_STRING_MAP.put(FLOAT_TYPE, "Real32");
    TYPE_STRING_MAP.put(DOUBLE_TYPE, "Real64");
    TYPE_STRING_MAP.put(COMPLEX_FLOAT_TYPE, "ComplexReal32");
    TYPE_STRING_MAP.put(COMPLEX_DOUBLE_TYPE, "ComplexReal64");
  }

  private static boolean arrayDoubleRecursive(
      IAST nestedListsOfValues, int level, double[] doubleArr, int[] index) {
    level--;
    for (int i = 1; i < nestedListsOfValues.size(); i++) {
      IExpr arg = nestedListsOfValues.get(i);
      if (level == 0) {
        if (arg.isList()) {
          return false;
        }
        doubleArr[index[0]++] = arg.evalDouble();
      } else {
        if (!arg.isList() || !arrayDoubleRecursive((IAST) arg, level, doubleArr, index)) {
          return false;
        }
      }
    }
    return true;
  }

  private static boolean arrayFloatRecursive(
      IAST nestedListsOfValues, int level, float[] floatArr, int[] index) {
    level--;
    for (int i = 1; i < nestedListsOfValues.size(); i++) {
      IExpr arg = nestedListsOfValues.get(i);
      if (level == 0) {
        if (arg.isList()) {
          return false;
        }
        floatArr[index[0]++] = (float) arg.evalDouble();
      } else {
        if (!arg.isList() || !arrayFloatRecursive((IAST) arg, level, floatArr, index)) {
          return false;
        }
      }
    }
    return true;
  }

  private static boolean arrayIntRecursive(
      IAST nestedListsOfValues, int level, int[] intArr, int[] index) {
    level--;
    for (int i = 1; i < nestedListsOfValues.size(); i++) {
      IExpr arg = nestedListsOfValues.get(i);
      if (level == 0) {
        if (!arg.isReal()) {
          return false;
        }
        intArr[index[0]++] = ((ISignedNumber) arg).toInt();
      } else {
        if (!arg.isList() || !arrayIntRecursive((IAST) arg, level, intArr, index)) {
          return false;
        }
      }
    }
    return true;
  }

  private static boolean arrayLongRecursive(
      IAST nestedListsOfValues, int level, long[] longArr, int[] index) {
    level--;
    for (int i = 1; i < nestedListsOfValues.size(); i++) {
      IExpr arg = nestedListsOfValues.get(i);
      if (level == 0) {
        if (!arg.isReal()) {
          return false;
        }
        longArr[index[0]++] = ((ISignedNumber) arg).toLong();
      } else {
        if (!arg.isList() || !arrayLongRecursive((IAST) arg, level, longArr, index)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * @param key
   * @return
   */
  public static int getType(String key) {
    Integer result = TYPE_MAP.get(key);
    if (result != null) {
      return result;
    }
    return UNDEFINED_TYPE;
  }

  public static NumericArrayExpr newInstance(final Object value, int[] dimension, int type) {
    return new NumericArrayExpr(value, dimension, type);
  }

  public static NumericArrayExpr newList(final IAST list, int type) {
    int[] dimension = null;
    ArrayList<Integer> dims = LinearAlgebra.dimensions(list);
    if (dims != null && dims.size() > 0) {
      int size = 1;
      dimension = new int[dims.size()];
      for (int i = 0; i < dims.size(); i++) {
        dimension[i] = dims.get(i);
        size *= dimension[i];
      }

      try {
        int[] index = new int[1];
        switch (type) {
          case INTEGER32_TYPE:
            int[] intArr = new int[size];
            if (arrayIntRecursive(list, dimension.length, intArr, index)) {
              return new NumericArrayExpr(intArr, dimension, type);
            }
            break;
          case INTEGER64_TYPE:
            long[] longArr = new long[size];
            if (arrayLongRecursive(list, dimension.length, longArr, index)) {
              return new NumericArrayExpr(longArr, dimension, type);
            }
            break;
          case FLOAT_TYPE:
            float[] floatArr = new float[size];
            if (arrayFloatRecursive(list, dimension.length, floatArr, index)) {
              return new NumericArrayExpr(floatArr, dimension, type);
            }
            break;
          case DOUBLE_TYPE:
            double[] doubleArr = new double[size];
            if (arrayDoubleRecursive(list, dimension.length, doubleArr, index)) {
              return new NumericArrayExpr(doubleArr, dimension, type);
            }
            break;
        }
      } catch (RuntimeException rex) {

      }
    }
    return null;
  }

  private static void normalRecursive(
      double[] doubleArray, IASTMutable list, int[] dimension, int position, int[] index) {
    int size = dimension[position];
    if (dimension.length - 1 == position) {
      for (int i = 1; i <= size; i++) {
        list.set(i, F.num(doubleArray[index[0]++]));
      }
      return;
    }
    int size2 = dimension[position + 1];
    for (int i = 1; i <= size; i++) {
      IASTAppendable currentList = F.ast(S.List, size2, true);
      list.set(i, currentList);
      normalRecursive(doubleArray, currentList, dimension, position + 1, index);
    }
  }

  private static void normalRecursive(
      float[] floatArray, IASTMutable list, int[] dimension, int position, int[] index) {
    int size = dimension[position];
    if (dimension.length - 1 == position) {
      for (int i = 1; i <= size; i++) {
        list.set(i, F.num(floatArray[index[0]++]));
      }
      return;
    }
    int size2 = dimension[position + 1];
    for (int i = 1; i <= size; i++) {
      IASTAppendable currentList = F.ast(S.List, size2, true);
      list.set(i, currentList);
      normalRecursive(floatArray, currentList, dimension, position + 1, index);
    }
  }

  private static void normalRecursive(
      int[] intArray, IASTMutable list, int[] dimension, int position, int[] index) {
    int size = dimension[position];
    if (dimension.length - 1 == position) {
      for (int i = 1; i <= size; i++) {
        list.set(i, F.ZZ(intArray[index[0]++]));
      }
      return;
    }
    int size2 = dimension[position + 1];
    for (int i = 1; i <= size; i++) {
      IASTAppendable currentList = F.ast(S.List, size2, true);
      list.set(i, currentList);
      normalRecursive(intArray, currentList, dimension, position + 1, index);
    }
  }

  private static void normalRecursive(
      long[] longArray, IASTMutable list, int[] dimension, int position, int[] index) {
    int size = dimension[position];
    if (dimension.length - 1 == position) {
      for (int i = 1; i <= size; i++) {
        list.set(i, F.ZZ(longArray[index[0]++]));
      }
      return;
    }
    int size2 = dimension[position + 1];
    for (int i = 1; i <= size; i++) {
      IASTAppendable currentList = F.ast(S.List, size2, true);
      list.set(i, currentList);
      normalRecursive(longArray, currentList, dimension, position + 1, index);
    }
  }

  /** The dimension of the numeric array. */
  int[] fDimension;

  int fType;

  public NumericArrayExpr() {
    super(S.NumericArray, null);
    fType = UNDEFINED_TYPE;
  }

  protected NumericArrayExpr(final Object array, int[] dimension, int type) {
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
          case INTEGER32_TYPE:
            return Arrays.equals((int[]) fData, (int[]) numericArray.fData);
          case INTEGER64_TYPE:
            return Arrays.equals((long[]) fData, (long[]) numericArray.fData);
          case FLOAT_TYPE:
            return Arrays.equals((float[]) fData, (float[]) numericArray.fData);
          case DOUBLE_TYPE:
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
      //      return fData.get(partIndex);
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

  public String getType() {
    return TYPE_STRING_MAP.get(fType);
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

  public IASTMutable normal(int[] dims) {
    return normalAppendable(S.List, dims);
  }

  private IASTAppendable normalAppendable(IExpr head, int[] dims) {
    IASTAppendable list = F.ast(head, dims[0], true);
    int[] index = new int[1];
    switch (fType) {
      case INTEGER32_TYPE:
        normalRecursive((int[]) fData, list, dims, 0, index);
        break;
      case INTEGER64_TYPE:
        normalRecursive((long[]) fData, list, dims, 0, index);
        break;
      case FLOAT_TYPE:
        normalRecursive((float[]) fData, list, dims, 0, index);
        break;
      case DOUBLE_TYPE:
        normalRecursive((double[]) fData, list, dims, 0, index);
        break;
    }

    return list;
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    int type = in.readInt();
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
    return fHead + "[" + fData.toString() + "]";
  }

  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeInt(fType);
    output.writeObject(fDimension);
    output.writeObject(fData);
  }
}
