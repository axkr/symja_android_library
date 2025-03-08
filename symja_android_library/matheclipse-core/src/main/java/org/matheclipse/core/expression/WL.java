package org.matheclipse.core.expression;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;
import org.apfloat.Apfloat;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.linear.RealVector;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.data.ByteArrayExpr;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.expression.data.NumericArrayExpr;
import org.matheclipse.core.expression.data.SparseArrayExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.tensor.qty.IQuantity;

/**
 * Methods for handling the WXF serialization format.
 *
 * <p>
 * See: <a href="https://reference.wolfram.com/language/tutorial/WXFFormatDescription.html">WXF
 * Format Description</a>
 */
public class WL {
  /** The list of all array value type tokens. */
  static class ARRAY_TYPES_ELEM_SIZE {
    static final byte Integer8 = 1;
    static final byte Integer16 = 2;
    static final byte Integer32 = 4;
    static final byte Integer64 = 8;
    static final byte UnsignedInteger8 = 1;
    static final byte UnsignedInteger16 = 2;
    static final byte UnsignedInteger32 = 4;
    static final byte UnsignedInteger64 = 8;
    static final byte Real32 = 4;
    static final byte Real64 = 8;
    static final byte ComplexReal32 = 8;
    static final byte ComplexReal64 = 16;
  }

  private static class ReadInternalObject extends ReadObject {
    public ReadInternalObject(byte[] array) {
      super(array);
    }

    public ReadInternalObject(byte[] array, int position) {
      super(array, position);
    }

    @Override
    protected IExpr internalRead(byte exprType) {
      if (exprType == WXF_CONSTANTS.InternalExprID) {
        return S.exprID((short) parseLength());
      }
      return super.internalRead(exprType);
    }
  }

  private static class ReadObject {
    byte[] array;
    int position;

    public ReadObject(byte[] array) {
      this(array, 2);
    }

    public ReadObject(byte[] array, int position) {
      this.array = array;
      this.position = position;
    }

    /**
     * @param exprType
     * @return
     */
    protected IExpr internalRead(byte exprType) throws AbortException {
      byte value;
      int length;
      switch (exprType) {
        case WXF_CONSTANTS.Integer8:
          value = array[position++];
          return F.ZZ(value);
        case WXF_CONSTANTS.Integer16:
          ByteBuffer b16 = ByteBuffer.wrap(array, position, 2);
          b16.order(ByteOrder.LITTLE_ENDIAN);
          short v = b16.getShort();
          position += 2;
          return F.ZZ(v);
        case WXF_CONSTANTS.Integer32:
          ByteBuffer b32 = ByteBuffer.wrap(array, position, 4);
          b32.order(ByteOrder.LITTLE_ENDIAN);
          int iValue = b32.getInt();
          position += 4;
          return F.ZZ(iValue);
        case WXF_CONSTANTS.Integer64:
          ByteBuffer b64 = ByteBuffer.wrap(array, position, 8);
          b64.order(ByteOrder.LITTLE_ENDIAN);
          long lValue = b64.getLong();
          position += 8;
          return F.ZZ(lValue);
        case WXF_CONSTANTS.BigInteger:
          length = parseLength();
          StringBuilder bigIntegerString = new StringBuilder();
          for (int i = 0; i < length; i++) {
            char ch = (char) array[position++];
            bigIntegerString.append(ch);
          }
          return F.ZZ(new BigInteger(bigIntegerString.toString()));
        case WXF_CONSTANTS.BigReal:
          length = parseLength(); // (int) array[position++];
          StringBuilder brStr = new StringBuilder();
          for (int i = 0; i < length; i++) {
            brStr.append((char) array[position++]);
          }
          String s = brStr.toString();
          // decimal 96 / hex 60 / character `
          int index = s.indexOf(0x60);
          if (index > 0) {
            String numStr = s.substring(0, index);
            String precString = s.substring(index + 1);
            // decimal 46 / hex 2E / character .
            index = precString.indexOf(0x2E);
            if (index > 0) {
              int prec = Integer.parseInt(precString.substring(0, index));
              Apfloat af = new Apfloat(numStr, prec, 10);
              return F.num(af);
            }
          }
          break;
        case WXF_CONSTANTS.Real64:
          double real64 = parseDouble();
          return F.num(real64);
        case WXF_CONSTANTS.PackedArray:
          return readPackedArray();
        case WXF_CONSTANTS.RawArray:
          return readNumericArray();
        case WXF_CONSTANTS.Symbol:
          return readSymbol();
        case WXF_CONSTANTS.Function:
          length = parseLength(); // (int) array[position++];
          IASTAppendable ast = F.ast(F.NIL, length);
          ast.set(0, read());
          for (int i = 0; i < length; i++) {
            ast.append(read());
          }
          IExpr head = ast.head();
          if (head == S.Complex || head == S.Rational || head == S.Pattern || head == S.Optional) {
            // head == F.Blank ||
            // head == F.BlankSequence ||
            // head == F.BlankNullSequence) {
            IFunctionEvaluator evaluator =
                (IFunctionEvaluator) ((IBuiltInSymbol) head).getEvaluator();
            IExpr temp = evaluator.evaluate(ast, EvalEngine.get());
            if (temp.isPresent()) {
              return temp;
            }
          }
          return ast;
        case WXF_CONSTANTS.Association:
          length = parseLength();
          ASTAssociation assoc = new ASTAssociation();
          for (int i = 0; i < length; i++) {
            value = array[position++];
            IExpr ruleHead = S.Rule;
            if (value == WXF_CONSTANTS.RuleDelayed) {
              ruleHead = S.RuleDelayed;
            }
            IExpr arg1 = read();
            IExpr arg2 = read();
            assoc.appendRule(F.binaryAST2(ruleHead, arg1, arg2));
          }
          return assoc;
        case WXF_CONSTANTS.String:
          length = parseLength();
          StringBuilder str = new StringBuilder();
          for (int i = 0; i < length; i++) {
            str.append((char) array[position++]);
          }
          return F.stringx(str);
        case WXF_CONSTANTS.BinaryString:
          length = parseLength();
          byte[] bArray = new byte[length];
          System.arraycopy(array, position, bArray, 0, length);
          position += length;
          return ByteArrayExpr.newInstance(bArray);
      }
      throw AbortException.ABORTED;
    }

    private double parseDouble() {
      long bits = 0;
      position += 8;
      int pos2 = position - 1;
      bits = (bits | (array[pos2--] & 0xFF)) << 8;
      bits = (bits | (array[pos2--] & 0xFF)) << 8;
      bits = (bits | (array[pos2--] & 0xFF)) << 8;
      bits = (bits | (array[pos2--] & 0xFF)) << 8;
      bits = (bits | (array[pos2--] & 0xFF)) << 8;
      bits = (bits | (array[pos2--] & 0xFF)) << 8;
      bits = (bits | (array[pos2--] & 0xFF)) << 8;
      bits = (bits | (array[pos2--] & 0xFF));
      return Double.longBitsToDouble(bits);
    }

    private float parseFloat() {
      int bits = 0;
      position += 4;
      int pos2 = position - 1;
      bits = (bits | (array[pos2--] & 0xFF)) << 8;
      bits = (bits | (array[pos2--] & 0xFF)) << 8;
      bits = (bits | (array[pos2--] & 0xFF)) << 8;
      bits = (bits | (array[pos2--] & 0xFF));
      return Float.intBitsToFloat(bits);
    }

    private short parseInteger16() {
      int bits = 0;
      position += 2;
      int pos2 = position - 1;
      bits = ((array[pos2--] & 0xFF) << 8) | (array[pos2--] & 0xFF);
      return (short) (bits);
    }

    private int parseInteger32() {
      int bits = 0;
      position += 4;
      int pos2 = position - 1;
      bits = (bits | (array[pos2--] & 0xFF)) << 8;
      bits = (bits | (array[pos2--] & 0xFF)) << 8;
      bits = (bits | (array[pos2--] & 0xFF)) << 8;
      bits = (bits | (array[pos2--] & 0xFF));
      return bits;
    }

    private long parseInteger64() {
      long bits = 0;
      position += 8;
      int pos2 = position - 1;
      bits = (bits | (array[pos2--] & 0xFF)) << 8;
      bits = (bits | (array[pos2--] & 0xFF)) << 8;
      bits = (bits | (array[pos2--] & 0xFF)) << 8;
      bits = (bits | (array[pos2--] & 0xFF)) << 8;
      bits = (bits | (array[pos2--] & 0xFF)) << 8;
      bits = (bits | (array[pos2--] & 0xFF)) << 8;
      bits = (bits | (array[pos2--] & 0xFF)) << 8;
      bits = (bits | (array[pos2--] & 0xFF));
      return bits;
    }

    private int parseInteger8() {
      byte b = array[position++];
      return b;
    }

    protected int parseLength() {
      int[] result = parseVarint(array, position);
      position = result[1];
      return result[0];
    }

    /**
     * Read an object from the input stream.
     *
     * @return {@link F#NIL} if the byte array is <code>null</code> or not valid.
     */
    public IExpr read() {
      byte exprType = array[position++];
      try {
        return internalRead(exprType);
      } catch (AbortException ae) {
        //
      }
      return F.NIL;
    }

    private IExpr readNumericArray() throws AbortException {
      byte arrayType = array[position++];
      int rank = parseLength();
      int[] dimensions = new int[rank];
      int size = 1;
      for (int i = 0; i < rank; i++) {
        dimensions[i] = parseLength();
        size *= dimensions[i];
      }

      switch (arrayType) {
        case NumericArrayExpr.Integer8:
        case NumericArrayExpr.UnsignedInteger8: {
          byte[] byteArr = new byte[size];
          System.arraycopy(array, position, byteArr, 0, size);
          position += size;
          return NumericArrayExpr.newInstance(byteArr, dimensions, arrayType);
        }
        case NumericArrayExpr.Integer16:
        case NumericArrayExpr.UnsignedInteger16: {
          short[] shortArr = new short[size];
          for (int i = 0; i < size; i++) {
            shortArr[i] = parseInteger16();
          }
          return NumericArrayExpr.newInstance(shortArr, dimensions, arrayType);
        }
        case NumericArrayExpr.Integer32:
        case NumericArrayExpr.UnsignedInteger32: {
          int[] intArr = new int[size];
          for (int i = 0; i < size; i++) {
            intArr[i] = parseInteger32();
          }
          return NumericArrayExpr.newInstance(intArr, dimensions, arrayType);
        }
        case NumericArrayExpr.Integer64:
        case NumericArrayExpr.UnsignedInteger64: {
          long[] longArr = new long[size];
          for (int i = 0; i < size; i++) {
            longArr[i] = parseInteger64();
          }
          return NumericArrayExpr.newInstance(longArr, dimensions, arrayType);
        }
        case NumericArrayExpr.Real32: {
          float[] floatArr = new float[size];
          for (int i = 0; i < size; i++) {
            floatArr[i] = parseFloat();
          }
          return NumericArrayExpr.newInstance(floatArr, dimensions, arrayType);
        }
        case NumericArrayExpr.Real64: {
          double[] doubleArr = new double[size];
          for (int i = 0; i < size; i++) {
            doubleArr[i] = parseDouble();
          }
          return NumericArrayExpr.newInstance(doubleArr, dimensions, arrayType);
        }
        case NumericArrayExpr.ComplexReal32: {
          int doubledSize = size * 2;
          float[] floatArr = new float[doubledSize];
          int i = 0;
          while (i < doubledSize) {
            floatArr[i++] = parseFloat();
            floatArr[i++] = parseFloat();
          }
          return NumericArrayExpr.newInstance(floatArr, dimensions, arrayType);
        }
        case NumericArrayExpr.ComplexReal64: {
          int doubledSize = size * 2;
          double[] doubleArr = new double[doubledSize];
          int i = 0;
          while (i < doubledSize) {
            doubleArr[i++] = parseDouble();
            doubleArr[i++] = parseDouble();
          }
          return NumericArrayExpr.newInstance(doubleArr, dimensions, arrayType);
        }
      }
      throw AbortException.ABORTED;
    }

    private IExpr readPackedArray() throws AbortException {
      byte arrayType = array[position++];
      switch (arrayType) {
        case NumericArrayExpr.Integer8: {
          int rank = parseLength();
          int[] dimensions = new int[rank];
          for (int i = 0; i < rank; i++) {
            dimensions[i] = parseLength();
          }
          if (rank == 1) {
            IASTAppendable list = F.ListAlloc(dimensions[0]);
            for (int i = 0; i < dimensions[0]; i++) {
              int value = parseInteger8();
              list.append(value);
            }
            return list;
          } else if (rank == 2) {
            IASTAppendable m = F.ListAlloc(dimensions[0]);
            for (int i = 0; i < dimensions[0]; i++) {
              IASTAppendable row = F.ListAlloc(dimensions[1]);
              for (int j = 0; j < dimensions[1]; j++) {
                row.append(parseInteger8());
              }
              m.append(row);
            }

            return m;
          }
        }
          break;
        case NumericArrayExpr.Real64: {
          int rank = parseLength();
          int[] dimensions = new int[rank];
          for (int i = 0; i < rank; i++) {
            dimensions[i] = parseLength();
          }
          if (rank == 1) {
            double[] vector = new double[dimensions[0]];
            for (int i = 0; i < vector.length; i++) {
              double d = parseDouble();
              vector[i] = d;
            }
            return new ASTRealVector(vector, false);
          } else if (rank == 2) {
            double[][] matrix = new double[dimensions[0]][dimensions[1]];
            for (int i = 0; i < dimensions[0]; i++) {
              for (int j = 0; j < dimensions[1]; j++) {
                double d = parseDouble();
                matrix[i][j] = d;
              }
            }
            return new ASTRealMatrix(matrix, false);
          }
        }
          break;
      }
      throw AbortException.ABORTED;
    }

    private IExpr readSymbol() {
      int length = parseLength();
      StringBuilder symbol = new StringBuilder();
      int contextStart = position;
      int contextEnd = contextStart;
      for (int i = 0; i < length; i++) {
        char ch = (char) array[position++];
        if (ch == '`') {
          contextEnd = position;
        }
        symbol.append(ch);
      }
      String lcSymbolName = symbol.toString();
      String contextName = "";
      if (contextEnd > contextStart) {
        contextName = lcSymbolName.substring(0, contextEnd - contextStart);
        lcSymbolName = lcSymbolName.substring(contextEnd - contextStart);
      }
      EvalEngine engine = EvalEngine.get();
      if (engine.isRelaxedSyntax()) {
        if (lcSymbolName.length() > 1) {
          // use the lower case string here to use it as associated class
          // name in package org.matheclipse.core.reflection.system
          lcSymbolName = lcSymbolName.toLowerCase(Locale.ENGLISH);
        }
      }
      if (contextEnd == contextStart || contextName.equals(Context.SYSTEM_CONTEXT_NAME)) {
        // use System Context
        ISymbol sym = Context.SYSTEM.get(lcSymbolName);
        if (sym != null) {
          return sym;
        }
      } else if (contextName.equals(Context.RUBI_STR)) {
        // use Rubi Context
        return F.$rubi(lcSymbolName);
        // ISymbol sym = Context.SYSTEM.get(lcSymbolName);
        // if (sym != null) {
        // return sym;
        // }
      }
      ContextPath contextPath = engine.getContextPath();
      Context context = contextPath.getContext(contextName);
      return ContextPath.getSymbol(lcSymbolName, context, engine.isRelaxedSyntax());
    }
  }

  private static class WriteInternalObject extends WriteObject {

    public WriteInternalObject() {
      super();
    }

    public WriteInternalObject(ByteArrayOutputStream stream) {
      super(stream);
    }

    @Override
    public void write(IExpr arg1) throws IOException {
      Short exprID = S.GLOBAL_IDS_MAP.get(arg1);
      if (exprID != null) {
        stream.write(WXF_CONSTANTS.InternalExprID);
        stream.write(varintBytes(exprID));
        return;
      }
      super.write(arg1);
    }
  }

  private static class WriteObject implements Closeable {
    ByteArrayOutputStream stream;

    public WriteObject() {
      this(new ByteArrayOutputStream());
    }

    public WriteObject(ByteArrayOutputStream stream) {
      this.stream = stream;
      stream.write('8');
      stream.write(':');
    }

    @Override
    public void close() throws IOException {
      stream.close();
    }

    public byte[] toByteArray() {
      return stream.toByteArray();
    }

    public void write(IExpr arg1) throws IOException {
      // argument dispatching
      final int hier = arg1.hierarchy();
      switch (hier) {
        case IExpr.ASTID:
          writeAST(arg1);
          return;
        case IExpr.BLANKID:
          writeBlank(arg1);
          return;
        case IExpr.SYMBOLID:
          writeSymbol(arg1);
          return;
        case IExpr.COMPLEXID:
          writeAST2(S.Complex, ((IComplex) arg1).re(), ((IComplex) arg1).im());
          return;
        case IExpr.INTEGERID:
          writeInteger(arg1);
          return;
        case IExpr.FRACTIONID:
          writeAST2(S.Rational, ((IRational) arg1).numerator(), ((IRational) arg1).denominator());
          return;
        case IExpr.DOUBLEID:
          // if (arg1 instanceof ApfloatNum) {
          //
          // }
          stream.write(WXF_CONSTANTS.Real64);
          writeDouble(((INum) arg1).doubleValue());
          return;
        case IExpr.DOUBLECOMPLEXID:
          writeAST2(S.Complex, ((IComplexNum) arg1).re(), ((IComplexNum) arg1).im());
          return;
        case IExpr.GRAPHEXPRID:
          writeGraphExpr(arg1);
          return;
        case IExpr.BYTEARRAYID:
          writeBinaryString(arg1);
          return;

        case IExpr.NUMERICARRAYID:
          writeNumericArray((NumericArrayExpr) arg1);
          return;
        case IExpr.PATTERNID:
          writePattern(arg1);
          return;
        case IExpr.QUANTITYID:
          writeQuantity(arg1);
          return;
        case IExpr.SERIESID:
          writeSeriesData(arg1);
          return;
        case IExpr.SPARSEARRAYID:
          writeSparseArray((SparseArrayExpr) arg1);
          return;

        case IExpr.STRINGID:
          writeString(arg1);
          return;

        default:
          throw new IllegalArgumentException("Unknown hierarchy ID: " + hier);
      }
    }

    private void writeAST(IExpr arg1) throws IOException {
      IAST ast = (IAST) arg1;
      if (ast instanceof ASTRealVector) {
        RealVector vector = ((ASTRealVector) ast).getRealVector();
        stream.write(WXF_CONSTANTS.PackedArray);
        stream.write(NumericArrayExpr.Real64);
        stream.write(0x01);
        stream.write(varintBytes(vector.getDimension()));
        for (int i = 0; i < vector.getDimension(); i++) {
          writeDouble(vector.getEntry(i));
        }
        return;
      } else if (ast instanceof ASTRealMatrix) {
        RealMatrix matrix = ((ASTRealMatrix) ast).getRealMatrix();
        stream.write(WXF_CONSTANTS.PackedArray);
        stream.write(NumericArrayExpr.Real64);
        stream.write(0x02);
        stream.write(varintBytes(matrix.getRowDimension()));
        stream.write(varintBytes(matrix.getColumnDimension()));
        for (int i = 0; i < matrix.getRowDimension(); i++) {
          for (int j = 0; j < matrix.getColumnDimension(); j++) {
            writeDouble(matrix.getEntry(i, j));
          }
        }
        return;
      } else if (ast instanceof ASTAssociation) {
        // <|a->b, c:>d,...|>
        stream.write(WXF_CONSTANTS.Association);
        stream.write(varintBytes(ast.argSize()));
        for (int i = 1; i < ast.size(); i++) {
          IAST rule = (IAST) ast.getRule(i);
          if (rule.isRuleDelayed()) {
            stream.write(WXF_CONSTANTS.RuleDelayed);
          } else {
            stream.write(WXF_CONSTANTS.Rule);
          }
          write(rule.arg1());
          write(rule.arg2());
        }
        return;
      }
      stream.write(WXF_CONSTANTS.Function);
      stream.write(varintBytes(ast.argSize()));
      for (int i = 0; i < ast.size(); i++) {
        write(ast.get(i));
      }
    }

    private void writeAST0(IExpr head) throws IOException {
      stream.write(WXF_CONSTANTS.Function);
      stream.write(0);
      write(head);
    }

    private void writeAST1(IExpr head, IExpr arg1) throws IOException {
      stream.write(WXF_CONSTANTS.Function);
      stream.write(1);
      write(head);
      write(arg1);
    }

    private void writeAST2(IExpr head, IExpr arg1, IExpr arg2) throws IOException {
      stream.write(WXF_CONSTANTS.Function);
      stream.write(2);
      write(head);
      write(arg1);
      write(arg2);
    }

    private void writeBinaryString(IExpr arg1) throws IOException {
      byte[] bArray = ((ByteArrayExpr) arg1).toData();
      int size = bArray.length;
      stream.write(WXF_CONSTANTS.BinaryString);
      stream.write(varintBytes(size));
      stream.write(bArray, 0, size);
    }

    private void writeBlank(IExpr arg1) throws IOException {
      IPattern blank = (IPattern) arg1;
      IExpr blankCondition = blank.getHeadTest();
      // IExpr blankDef = blank.getOptionalValue();
      // if (blankDef != null) {
      // blank = F.$b();
      // writeAST2(F.Optional, blank, blankDef);
      // } else
      if (blank.isPatternDefault()) {
        blank = F.$b();
        writeAST1(S.Optional, blank);
      } else if (blankCondition != null) {
        writeAST1(S.Blank, blankCondition);
      } else {
        writeAST0(S.Blank);
      }
    }

    private void writeDouble(double d) {
      long bits = Double.doubleToRawLongBits(d);
      stream.write((byte) (bits & 0x00000000000000ff));
      stream.write((byte) ((bits >> 8) & 0x00000000000000ff));
      stream.write((byte) ((bits >> 16) & 0x00000000000000ff));
      stream.write((byte) ((bits >> 24) & 0x00000000000000ff));
      stream.write((byte) ((bits >> 32) & 0x00000000000000ff));
      stream.write((byte) ((bits >> 40) & 0x00000000000000ff));
      stream.write((byte) ((bits >> 48) & 0x00000000000000ff));
      stream.write((byte) ((bits >> 56) & 0x00000000000000ff));
    }

    private void writeFloat(float f) {
      int bits = Float.floatToIntBits(f);
      stream.write((byte) (bits & 0x000000ff));
      stream.write((byte) ((bits >> 8) & 0x000000ff));
      stream.write((byte) ((bits >> 16) & 0x000000ff));
      stream.write((byte) ((bits >> 24) & 0x000000ff));
    }

    private void writeGraphExpr(IExpr arg1) throws IOException {
      GraphExpr graph = (GraphExpr) arg1;
      IAST fullForm = graph.fullForm();
      writeAST(fullForm);
      // stream.write(WXF_CONSTANTS.Function);
      // stream.write(3);
      // write(fullForm.head());
      // write(fullForm.arg1());
      // write(fullForm.arg2());
      // if (fullForm.isAST3()) {
      // try {
      // NumericArrayExpr numericArray =
      // NumericArrayExpr.newList((IAST) fullForm.arg3(), INumericArray.Real64);
      // writeNumericArray(numericArray);
      // } catch (RangeException e) {
      // e.printStackTrace();
      // } catch (TypeException e) {
      // e.printStackTrace();
      // }
      // }
    }

    private void writeInteger(IExpr arg1) throws IOException {
      IInteger s = (IInteger) arg1;
      if (s instanceof IntegerSym) {
        int bits = ((IntegerSym) s).intValue();
        if (Byte.MIN_VALUE <= bits && bits <= Byte.MAX_VALUE) {
          stream.write(WXF_CONSTANTS.Integer8);
          stream.write((byte) bits);
        } else if (Short.MIN_VALUE <= bits && bits <= Short.MAX_VALUE) {
          stream.write(WXF_CONSTANTS.Integer16);
          writeInteger16((short) bits);
        } else {
          stream.write(WXF_CONSTANTS.Integer32);
          writeInteger32(bits);
        }
      } else if (s instanceof BigIntegerSym) {
        try {
          long bits = ((BigIntegerSym) s).toLong();
          stream.write(WXF_CONSTANTS.Integer64);
          writeInteger64(bits);
        } catch (ArithmeticException ae) {
          String big = ((BigIntegerSym) s).toBigNumerator().toString();
          stream.write(WXF_CONSTANTS.BigInteger);
          stream.write(varintBytes(big.length()));
          for (int i = 0; i < big.length(); i++) {
            stream.write(big.charAt(i));
          }
        }
      }
    }

    private void writeInteger16(short bits) {
      stream.write((byte) (bits & 0xFF));
      stream.write((byte) ((bits >> 8) & 0xFF));
    }

    private void writeInteger32(int bits) {

      stream.write((byte) (bits & 0xFF));
      stream.write((byte) ((bits >> 8) & 0xFF));
      stream.write((byte) ((bits >> 16) & 0xFF));
      stream.write((byte) ((bits >> 24) & 0xFF));
    }

    private void writeInteger64(long bits) {

      stream.write((byte) (bits & 0xFF));
      stream.write((byte) ((bits >> 8) & 0x00000000000000ff));
      stream.write((byte) ((bits >> 16) & 0x00000000000000ff));
      stream.write((byte) ((bits >> 24) & 0x00000000000000ff));
      stream.write((byte) ((bits >> 32) & 0x00000000000000ff));
      stream.write((byte) ((bits >> 40) & 0x00000000000000ff));
      stream.write((byte) ((bits >> 48) & 0x00000000000000ff));
      stream.write((byte) ((bits >> 56) & 0x00000000000000ff));
    }

    private void writeInteger8(byte bits) {
      stream.write(bits);
    }

    private void writeNumericArray(NumericArrayExpr numericArray) throws IOException {
      byte arrayType = numericArray.getType();
      byte[] buf = new byte[] {WXF_CONSTANTS.RawArray, arrayType};
      stream.write(buf);
      writeNumericArrayData(arrayType, numericArray);
    }

    private void writeNumericArrayData(byte arrayType, NumericArrayExpr numericArray)
        throws IOException {
      int[] dimensions = numericArray.getDimension();
      int rank = dimensions.length;
      stream.write(varintBytes(rank));
      int size = 1;
      for (int i = 0; i < rank; i++) {
        stream.write(dimensions[i]);
        size *= dimensions[i];
      }

      switch (arrayType) {
        case NumericArrayExpr.Integer8:
        case NumericArrayExpr.UnsignedInteger8: {
          byte[] byteArr = (byte[]) numericArray.toData();
          stream.write(byteArr, 0, size);
          return;
        }
        case NumericArrayExpr.Integer16:
        case NumericArrayExpr.UnsignedInteger16: {
          short[] shortArr = (short[]) numericArray.toData();
          for (int i = 0; i < shortArr.length; i++) {
            writeInteger16(shortArr[i]);
          }
          return;
        }
        case NumericArrayExpr.Integer32:
        case NumericArrayExpr.UnsignedInteger32: {
          int[] intArr = (int[]) numericArray.toData();
          for (int i = 0; i < intArr.length; i++) {
            writeInteger32(intArr[i]);
          }
          return;
        }
        case NumericArrayExpr.Integer64:
        case NumericArrayExpr.UnsignedInteger64: {
          long[] longArr = (long[]) numericArray.toData();
          for (int i = 0; i < longArr.length; i++) {
            writeInteger64(longArr[i]);
          }
          return;
        }
        case NumericArrayExpr.Real32: {
          float[] floatArr = (float[]) numericArray.toData();
          for (int i = 0; i < floatArr.length; i++) {
            writeFloat(floatArr[i]);
          }
          return;
        }
        case NumericArrayExpr.Real64: {
          double[] doubleArr = (double[]) numericArray.toData();
          for (int i = 0; i < doubleArr.length; i++) {
            writeDouble(doubleArr[i]);
          }
          return;
        }
        case NumericArrayExpr.ComplexReal32: {
          float[] floatArr = (float[]) numericArray.toData();
          int doubledSize = floatArr.length;
          int i = 0;
          while (i < doubledSize) {
            writeFloat(floatArr[i++]);
          }
          return;
        }
        case NumericArrayExpr.ComplexReal64: {
          double[] doubleArr = (double[]) numericArray.toData();
          int doubledSize = doubleArr.length;
          int i = 0;
          while (i < doubledSize) {
            writeDouble(doubleArr[i++]);
          }
        }
        default:
          throw new UnsupportedEncodingException();
      }
    }

    private void writePackedArray(IAST list) throws IOException {
      NumericArrayExpr numericArray =
          NumericArrayExpr.newListByType(list, NumericArrayExpr.UNDEFINED, S.Integers);
      if (numericArray != null) {
        stream.write(WXF_CONSTANTS.PackedArray);
        byte arrayType = numericArray.getType();
        stream.write(arrayType);
        writeNumericArrayData(arrayType, numericArray);
      } else {
        write(list);
      }
    }

    private void writePattern(IExpr arg1) throws IOException {
      if (arg1 instanceof IPatternSequence) {
        IPatternSequence pat = (IPatternSequence) arg1;
        IExpr condition = pat.getHeadTest();
        ISymbol symbol = pat.getSymbol();
        if (symbol == null) {
          if (pat.isNullSequence()) {
            if (condition != null) {
              writeAST1(S.BlankNullSequence, condition);
            } else {
              writeAST0(S.BlankNullSequence);
            }
          } else {
            if (condition != null) {
              writeAST1(S.BlankSequence, condition);
            } else {
              writeAST0(S.BlankSequence);
            }
          }
        } else {
          if (pat.isNullSequence()) {
            if (condition != null) {
              writeAST2(S.Pattern, pat.getSymbol(), F.unaryAST1(S.BlankNullSequence, condition));
            } else {
              writeAST2(S.Pattern, pat.getSymbol(), F.headAST0(S.BlankNullSequence));
            }
          } else {
            if (condition != null) {
              writeAST2(S.Pattern, pat.getSymbol(), F.unaryAST1(S.BlankSequence, condition));
            } else {
              writeAST2(S.Pattern, pat.getSymbol(), F.headAST0(S.BlankSequence));
            }
          }
        }
      } else {
        IPattern pat = (IPattern) arg1;
        IExpr condition = pat.getHeadTest();
        // IExpr def = pat.getOptionalValue();
        // if (def != null) {
        // pat = F.$p(pat.getSymbol());
        // writeAST2(F.Optional, pat, def);
        // } else
        if (pat.isPatternDefault()) {
          pat = F.$p(pat.getSymbol());
          writeAST1(S.Optional, pat);
        } else if (condition != null) {
          writeAST2(S.Pattern, pat.getSymbol(), F.unaryAST1(S.Blank, condition));
        } else {
          writeAST2(S.Pattern, pat.getSymbol(), F.headAST0(S.Blank));
        }
      }
    }

    private void writeQuantity(IExpr arg1) throws IOException {
      IQuantity quantity = (IQuantity) arg1;
      // simulate AST Quantity(..., ...)
      stream.write(WXF_CONSTANTS.Function);
      stream.write(varintBytes(2));
      write(quantity.head());
      write(quantity.value());
      write(F.stringx(quantity.unitString()));
    }

    private void writeSeriesData(IExpr arg1) throws IOException {
      ASTSeriesData ast = (ASTSeriesData) arg1;
      stream.write(WXF_CONSTANTS.Function);
      stream.write(varintBytes(ast.argSize()));
      for (int i = 0; i < ast.size(); i++) {
        write(ast.get(i));
      }
    }

    private void writeSparseArray(SparseArrayExpr sparseArray) throws IOException {
      IAST fullForm = sparseArray.fullForm();

      if (fullForm.size() == 5) {
        stream.write(WXF_CONSTANTS.Function);
        stream.write(varintBytes(fullForm.argSize()));
        write(fullForm.head());
        write(fullForm.arg1());
        writePackedArray((IAST) fullForm.arg2());
        // sparse array default value:
        write(fullForm.arg3());
        // sparse array structure:
        IAST list = (IAST) fullForm.arg4();
        stream.write(WXF_CONSTANTS.Function);
        stream.write(varintBytes(list.argSize()));
        write(S.List);
        write(list.arg1());

        IAST list2 = (IAST) list.arg2();
        stream.write(WXF_CONSTANTS.Function);
        stream.write(varintBytes(list2.argSize()));
        write(S.List);
        writePackedArray((IAST) list2.arg1());
        writePackedArray((IAST) list2.arg2());

        writePackedArray((IAST) list.arg3());
      } else {
        write(fullForm);
      }
    }

    private void writeString(IExpr arg1) throws IOException {
      IStringX s = (IStringX) arg1;
      char[] str = s.toString().toCharArray();
      int size = str.length;
      stream.write(WXF_CONSTANTS.String);
      stream.write(varintBytes(size));
      for (int i = 0; i < size; i++) {
        stream.write(str[i]);
      }
    }

    private void writeSymbol(IExpr arg1) throws IOException {
      ISymbol s = (ISymbol) arg1;
      Context context = s.getContext();
      final char[] str;
      if (context == Context.SYSTEM) {
        str = s.toString().toCharArray();
      } else {
        str = (context.getContextName() + s.getSymbolName()).toCharArray();
      }
      int size = str.length;
      stream.write(WXF_CONSTANTS.Symbol);
      stream.write(varintBytes(size));
      for (int i = 0; i < size; i++) {
        stream.write(str[i]);
      }
    }
  }


  /** The list of all the WXF tokens. */
  private static class WXF_CONSTANTS {
    static final byte Function = 'f';
    static final byte Symbol = 's';
    static final byte String = 'S';
    static final byte BinaryString = 'B';
    static final byte Integer8 = 'C';
    static final byte Integer16 = 'j';
    static final byte Integer32 = 'i';
    static final byte Integer64 = 'L';
    static final byte Real64 = 'r';
    static final byte BigInteger = 'I';
    static final byte BigReal = 'R';
    static final byte PackedArray = (byte) 0xC1;
    static final byte RawArray = (byte) 0xC2;
    static final byte Association = 'A';
    static final byte Rule = '-';
    static final byte RuleDelayed = ':';

    /**
     * Internal proprietary format token for serializing predefined &quot;constant&quot; Symja
     * expressions like built-in symbols, pattern objects, constant numbers, ...
     *
     * <p>
     * Use only with methods {@link WL#serializeInternal(IExpr)} or
     * {@link WL#deserializeInternal(byte[])}
     */
    static final byte InternalExprID = (byte) 0xD1;
  }

  /**
   * Deserialize the byte array to an {@link IExpr} expression.
   *
   * @param bArray
   * @return {@link F#NIL} if the byte array is <code>null</code> or not valid.
   */
  public static IExpr deserialize(byte[] bArray) {
    if (bArray == null || bArray.length < 3) {
      return F.NIL;
    }
    ReadObject ro = new ReadObject(bArray);
    return ro.read();
  }

  /**
   * Deserialize the byte array to an {@link IExpr} expression from internal version dependent
   * format. The format will change on every change in the built-in symbols table.
   *
   * <p>
   * <b>Warning:</b>: Don't use for &quot;outside communication&quot;.
   *
   * @param bArray
   * @return {@link F#NIL} if the byte array is <code>null</code> or not valid.
   */
  public static IExpr deserializeInternal(byte[] bArray) {
    if (bArray == null || bArray.length < 3) {
      return F.NIL;
    }
    ReadInternalObject ro = new ReadInternalObject(bArray);
    return ro.read();
  }

  /**
   * Finds a resource with a given name and if present, deserializes the binary encoded WXF resource
   * to an {@link IExpr} expression.
   *
   * @param resourceName the name of an {@link InputStream}
   * @param internal if <code>true</code> deserialize from internal format
   * @return {@link F#NIL} if the resource cannot be found or the derialization failed.
   */
  public static IExpr deserializeResource(String resourceName, boolean internal) {
    try (InputStream resourceAsStream = Config.class.getResourceAsStream(resourceName)) {
      if (resourceAsStream == null) {
        return F.NIL;
      } else {
        byte[] byteArray = resourceAsStream.readAllBytes();
        return deserializeInternal(byteArray);
      }
    } catch (IOException ex) {
      // `1`.
      throw new ArgumentTypeException("error", F.List("IOException in WL#deserializeResource()."));
    }
  }

  /**
   * Parse a readable binary buffer for a positive varint encoded integer.
   *
   * @return
   */
  public static int[] parseVarint(byte[] array, int position) {
    int count = 0;
    boolean continuation = true;
    int shift = 0;
    int length = 0;
    // when we read from stream we get a sequence of bytes. Its length is 1
    // except if we reached EOF in which case taking index 0 raises IndexError.
    // try:
    while (continuation && count < 8) {
      count++;
      byte next_byte = array[position++];
      // next_byte = ord(next_byte);
      length |= (next_byte & 0x7F) << shift;
      shift = shift + 7;
      continuation = (next_byte & 0x80) != 0;
    }

    if (continuation) {
      byte next_byte = array[position++];
      // next_byte = ord(next_byte);
      next_byte &= 0x7F;
      if (next_byte == 0) {
        throw new UnsupportedOperationException("Invalid last varint byte.");
      }
      length |= next_byte << shift;
    }
    return new int[] {length, position};
  }

  /**
   * Serialize the {@link IExpr} into a byte array in WXF format
   *
   * @param expr
   * @return <code>null</code> if the expression couldn't be serialized
   */
  public static byte[] serialize(IExpr expr) {
    if (expr.isPresent()) {
      try (WriteObject wo = new WriteObject()) {
        wo.write(expr);
        return wo.toByteArray();
      } catch (IOException e) {
        // `1`.
        throw new ArgumentTypeException("error", F.List("IOException in WL#serialize()."));
      }
    }
    return null;
  }

  /**
   * Serialize the {@link IExpr} into a byte array in internal version dependent format. The format
   * will change on every change in the built-in symbols table.
   *
   * <p>
   * <b>Warning:</b>: Don't use for &quot;outside communication&quot;.
   *
   * @param expr
   * @return <code>null</code> if the expression couldn't be serialized
   */
  public static byte[] serializeInternal(IExpr expr) {
    if (expr.isPresent()) {
      try (WriteInternalObject wo = new WriteInternalObject()) {
        wo.write(expr);
        return wo.toByteArray();
      } catch (IOException e) {
        // `1`.
        throw new ArgumentTypeException("error", F.List("IOException in WL#serializeInternal()."));
      }
    }
    return null;
  }

  /**
   * Convert <code>List(<byte values>)</code> to Java byte array.
   *
   * @param list
   * @return <code>null</code> if the list is nota a list of bytes
   */
  public static byte[] toByteArray(IAST list) {
    byte[] result = new byte[list.argSize()];
    for (int i = 1; i < list.size(); i++) {
      if (list.get(i).isInteger()) {
        final int val = list.get(i).toIntDefault();
        if (val >= 0 && val < 256) {
          result[i - 1] = (byte) val;
          continue;
        }
      }
      return null;
    }
    return result;
  }

  /**
   * Convert Java byte array to <code>List(<byte values>)</code>.
   *
   * @param bArray
   * @return
   */
  public static IASTMutable toList(byte[] bArray) {
    return F.mapRange(0, bArray.length, i -> F.ZZ(0x000000FF & bArray[i]));
  }

  /** Serialize <code>length</code> into varint bytes and return them as a byte array. */
  public static byte[] varintBytes(int length) {
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
    for (int i = 0; i < count; i++) {
      result[i] = buf[i];
    }
    return result;
  }
}
