package org.matheclipse.core.expression;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Methods for handling the WXF serialization format.
 *
 */
public class WL {

	/**
	 * The list of all array value type tokens.
	 */
	static class ARRAY_TYPES {
		static final byte Integer8 = (byte) (0x00);
		static final byte Integer16 = (byte) (0x01);
		static final byte Integer32 = (byte) (0x02);
		static final byte Integer64 = (byte) (0x03);
		static final byte UnsignedInteger8 = (byte) (0x10);
		static final byte UnsignedInteger16 = (byte) (0x11);
		static final byte UnsignedInteger32 = (byte) (0x12);
		static final byte UnsignedInteger64 = (byte) (0x13);
		static final byte Real32 = (byte) (0x22);
		static final byte Real64 = (byte) (0x23);
		static final byte ComplexReal32 = (byte) (0x33);
		static final byte ComplexReal64 = (byte) (0x34);
	}

	/**
	 * The list of all array value type tokens.
	 */
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

	public static class ReadObject {
		byte[] array;
		int position;

		public ReadObject(byte[] array) {
			this(array, 2);
		}

		public ReadObject(byte[] array, int position) {
			this.array = array;
			this.position = position;
		}

		private int parseLength() {
			int[] result = parseVarint(array, position);
			position = result[1];
			return result[0];
		}

		public IExpr read() {
			byte exprType = array[position++];
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
			case WXF_CONSTANTS.BigInteger:
				length = parseLength();
				StringBuilder bigIntegerString = new StringBuilder();
				for (int i = 0; i < length; i++) {
					char ch = (char) array[position++];
					bigIntegerString.append(ch);
				}
				return F.ZZ(new BigInteger(bigIntegerString.toString()));
			case WXF_CONSTANTS.Real64:
				long l = 0;
				position += 8;
				int pos2 = position - 1;
				l = (l | (array[pos2--] & 0xFF)) << 8;
				l = (l | (array[pos2--] & 0xFF)) << 8;
				l = (l | (array[pos2--] & 0xFF)) << 8;
				l = (l | (array[pos2--] & 0xFF)) << 8;
				l = (l | (array[pos2--] & 0xFF)) << 8;
				l = (l | (array[pos2--] & 0xFF)) << 8;
				l = (l | (array[pos2--] & 0xFF)) << 8;
				l = (l | (array[pos2--] & 0xFF));
				return F.num(Double.longBitsToDouble(l));
			case WXF_CONSTANTS.Symbol:
				return readSymbol();
			case WXF_CONSTANTS.Function:
				length = parseLength();// (int) array[position++];
				IASTAppendable ast = F.ast(F.NIL, length, false);
				ast.set(0, read());
				for (int i = 0; i < length; i++) {
					ast.append(read());
				}
				// System.out.println(ast.toString());
				return ast;
			case WXF_CONSTANTS.String:
				length = parseLength();// (int) array[position++];
				StringBuilder str = new StringBuilder();
				for (int i = 0; i < length; i++) {
					str.append((char) array[position++]);
				}
				return F.stringx(str);
			}
			return F.NIL;
		}

		private IExpr readSymbol() {
			int length = parseLength();// (int) array[position++];
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
			if (contextEnd == contextStart || contextName.equals("System`")) {
				// use System Context
				ISymbol sym = Context.SYSTEM.get(lcSymbolName);
				if (sym != null) {
					return sym;
				}
			}
			ContextPath contextPath = engine.getContextPath();
			Context context = contextPath.getContext(contextName);
			return contextPath.getSymbol(lcSymbolName, context, engine.isRelaxedSyntax());
		}

	}

	public static class WriteObject implements Closeable {
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
			case IExpr.SYMBOLID:
				writeSymbol(arg1);
				return;
			case IExpr.COMPLEXID:
				writeAST2(F.Complex, ((IComplex) arg1).re(), ((IComplex) arg1).im());
				return;
			case IExpr.INTEGERID:
				writeInteger(arg1);
				return;
			case IExpr.FRACTIONID:
				writeAST2(F.Rational, ((IRational) arg1).numerator(), ((IRational) arg1).denominator());
				return;
			case IExpr.DOUBLEID:
				writeDouble(((INum) arg1).doubleValue());
				return;
			case IExpr.DOUBLECOMPLEXID:
				writeAST2(F.Complex, ((IComplexNum) arg1).re(), ((IComplexNum) arg1).im());
				return;
			case IExpr.STRINGID:
				writeString(arg1);
				return;
			case IExpr.BLANKID:
				IPattern blank = (IPattern) arg1;
				IExpr blankCondition = blank.getCondition();
				IExpr blankDef = blank.getDefaultValue();
				if (blankDef != null) {
					blank = F.$b();
					writeAST2(F.Optional, blank, blankDef);
				} else if (blank.isPatternDefault()) {
					blank = F.$b();
					writeAST1(F.Optional, blank);
				} else if (blankCondition != null) {
					writeAST1(F.Blank, blankCondition);
				} else {
					writeAST0(F.Blank);
				}
				return;
			case IExpr.PATTERNID:
				IPattern pat = (IPattern) arg1;
				IExpr condition = pat.getCondition();
				IExpr def = pat.getDefaultValue();
				if (def != null) {
					pat = F.$p(pat.getSymbol());
					writeAST2(F.Optional, pat, def);
				} else if (pat.isPatternDefault()) {
					pat = F.$p(pat.getSymbol());
					writeAST1(F.Optional, pat);
				} else if (condition != null) {
					writeAST2(F.Pattern, pat.getSymbol(), F.unaryAST1(F.Blank, condition));
				} else {
					writeAST2(F.Pattern, pat.getSymbol(), F.headAST0(F.Blank));
				}
				return;
			}
		}

		private void writeAST(IExpr arg1) throws IOException {
			IAST ast = (IAST) arg1;
			stream.write(WL.WXF_CONSTANTS.Function);
			stream.write(varintBytes(ast.argSize()));
			for (int i = 0; i < ast.size(); i++) {
				write(ast.get(i));
			}
		}

		private void writeAST0(IExpr head) throws IOException {
			stream.write(WL.WXF_CONSTANTS.Function);
			stream.write(0);
			write(head);
		}

		private void writeAST1(IExpr head, IExpr arg1) throws IOException {
			stream.write(WL.WXF_CONSTANTS.Function);
			stream.write(1);
			write(head);
			write(arg1);
		}

		private void writeAST2(IExpr head, IExpr arg1, IExpr arg2) throws IOException {
			stream.write(WL.WXF_CONSTANTS.Function);
			stream.write(2);
			write(head);
			write(arg1);
			write(arg2);
		}

		private void writeDouble(double d) {
			long l = Double.doubleToRawLongBits(d);
			stream.write(WL.WXF_CONSTANTS.Real64);
			stream.write((byte) (l & 0x00000000000000ff));
			stream.write((byte) (l >> 8 & 0x00000000000000ff));
			stream.write((byte) (l >> 16 & 0x00000000000000ff));
			stream.write((byte) (l >> 24 & 0x00000000000000ff));
			stream.write((byte) (l >> 32 & 0x00000000000000ff));
			stream.write((byte) (l >> 40 & 0x00000000000000ff));
			stream.write((byte) (l >> 48 & 0x00000000000000ff));
			stream.write((byte) (l >> 56 & 0x00000000000000ff));
		}

		private void writeInteger(IExpr arg1) throws IOException {
			IInteger s = (IInteger) arg1;
			if (s instanceof IntegerSym) {
				int i = ((IntegerSym) s).intValue();
				if (Byte.MIN_VALUE <= i && i <= Byte.MAX_VALUE) {
					stream.write(WL.WXF_CONSTANTS.Integer8);
					stream.write((byte) i);
				} else if (Short.MIN_VALUE <= i && i <= Short.MAX_VALUE) {
					stream.write(WL.WXF_CONSTANTS.Integer16);
					stream.write((byte) (i & 0xFF));
					stream.write((byte) ((i >> 8) & 0xFF));
				} else if (Integer.MIN_VALUE <= i && i <= Integer.MAX_VALUE) {
					stream.write(WL.WXF_CONSTANTS.Integer32);
					stream.write((byte) (i & 0xFF));
					stream.write((byte) ((i >> 8) & 0xFF));
					stream.write((byte) ((i >> 16) & 0xFF));
					stream.write((byte) ((i >> 24) & 0xFF));
				}
			} else if (s instanceof BigIntegerSym) {
				String big = ((BigIntegerSym) s).toBigNumerator().toString();
				stream.write(WL.WXF_CONSTANTS.BigInteger);
				stream.write(varintBytes(big.length()));
				for (int i = 0; i < big.length(); i++) {
					stream.write(big.charAt(i));
				}
			}
		}

		private void writeString(IExpr arg1) throws IOException {
			IStringX s = (IStringX) arg1;
			char[] str = s.toString().toCharArray();
			int size = str.length;
			stream.write(WL.WXF_CONSTANTS.String);
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
				str = (context.getContextName() + s.toString()).toCharArray();
			}
			int size = str.length;
			stream.write(WL.WXF_CONSTANTS.Symbol);
			stream.write(varintBytes(size));
			for (int i = 0; i < size; i++) {
				stream.write(str[i]);
			}
		}
	}

	/**
	 * The list of all the WXF tokens.
	 */
	public static class WXF_CONSTANTS {
		public static final byte Function = 'f';
		public static final byte Symbol = 's';
		public static final byte String = 'S';
		static final byte BinaryString = 'B';
		public static final byte Integer8 = 'C';
		public static final byte Integer16 = 'j';
		public static final byte Integer32 = 'i';
		static final byte Integer64 = 'L';
		public static final byte Real64 = 'r';
		public static final byte BigInteger = 'I';
		static final byte BigReal = 'R';
		static final byte PackedArray = (byte) 0xC1;
		static final byte RawArray = (byte) 0xC2;
		static final byte Association = 'A';
		static final byte Rule = '-';
		static final byte RuleDelayed = ':';
	}

	public static IExpr deserialize(byte[] bArray) {
		// byte[] bArray = WL.toByteArray((IAST) arg1);
		WL.ReadObject ro = new WL.ReadObject(bArray);
		IExpr result = ro.read();
		return result;
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
		return new int[] { length, position };
	}

	/**
	 * 
	 * @param expr
	 * @return <code>null</code> if the expression couldn't be serialized
	 */
	public static byte[] serialize(IExpr expr) {
		WL.WriteObject wo = new WL.WriteObject();
		try {
			wo.write(expr);
			return wo.toByteArray();
		} catch (IOException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		} finally {
			try {
				wo.close();
			} catch (IOException e) {
			}
		}
		return null;
	}

	/**
	 * Convert <code>List(<byte values>)</code> to Java byte array.
	 * 
	 * @param list
	 * @return
	 */
	public static byte[] toByteArray(IAST list) {
		byte[] result = new byte[list.size() - 1];
		for (int i = 1; i < list.size(); i++) {
			result[i - 1] = ((IInteger) list.get(i)).byteValue();
		}
		return result;
	}

	/**
	 * Convert Java byte array to <code>List(<byte values>)</code>.
	 * 
	 * @param bArray
	 * @return
	 */
	public static IAST toList(byte[] bArray) {
		IASTAppendable list = F.ListAlloc(bArray.length);
		for (int i = 0; i < bArray.length; i++) {
			int value = 0x000000FF & bArray[i];
			list.append(F.ZZ(value));
		}
		return list;
	}

	/**
	 * Serialize <code>length</code> into varint bytes and return them as a byte array.
	 */
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
