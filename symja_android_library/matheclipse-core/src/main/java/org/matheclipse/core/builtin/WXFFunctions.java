package org.matheclipse.core.builtin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.ContextPath;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IntegerSym;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

public class WXFFunctions {
	static {
		F.BinarySerialize.setEvaluator(new BinarySerialize());
		F.BinaryDeserialize.setEvaluator(new BinaryDeserialize());
	}

	/**
	 * The list of all the WXF tokens.
	 */
	static class WXF_CONSTANTS {
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
	};

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

	private static class BinarySerialize extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					baos.write('8');
					baos.write(':');
					WriteObject wo = new WriteObject(baos);
					wo.write(ast.arg1());

					byte[] bArray = baos.toByteArray();
					baos.close();
					IASTAppendable list = F.ListAlloc(bArray.length);

					for (int i = 0; i < bArray.length; i++) {
						int value = 0x000000FF & bArray[i];
						list.append(F.ZZ(value));
					}
					return list;
				} catch (IOException ioe) {
					if (Config.SHOW_STACKTRACE) {
						ioe.printStackTrace();
					}
				}
			}
			return F.NIL;
		}

		static class WriteObject {
			ByteArrayOutputStream stream;

			public WriteObject(ByteArrayOutputStream stream) {
				this.stream = stream;
			}

			private void write(IExpr arg1) {
				// argument dispatching
				final int hier = arg1.hierarchy();
				switch (hier) {
				case IExpr.ASTID:
					writeAST(arg1);
					return;
				case IExpr.SYMBOLID:
					writeSymbol(arg1);
					return;
				case IExpr.INTEGERID:
					writeInteger(arg1);
					return;
				case IExpr.STRINGID:
					writeString(arg1);
					return;
				}
			}

			private void writeString(IExpr arg1) {
				IStringX s = (IStringX) arg1;
				char[] str = s.toString().toCharArray();
				int size = str.length;
				stream.write(WXF_CONSTANTS.String);
				stream.write(str.length);
				for (int i = 0; i < size; i++) {
					stream.write(str[i]);
				}
			}

			private void writeSymbol(IExpr arg1) {
				ISymbol s = (ISymbol) arg1;
				Context context = s.getContext();
				final char[] str;
				if (context == Context.SYSTEM) {
					str = s.toString().toCharArray();
				} else {
					str = (context.getContextName() + s.toString()).toCharArray();
				}
				int size = str.length;
				stream.write(WXF_CONSTANTS.Symbol);
				stream.write(str.length);
				for (int i = 0; i < size; i++) {
					stream.write(str[i]);
				}
			}

			private void writeInteger(IExpr arg1) {
				IInteger s = (IInteger) arg1;
				if (s instanceof IntegerSym) {
					int i = ((IntegerSym) s).intValue();
					if (0 <= i && i <= Byte.MAX_VALUE) {
						stream.write(WXF_CONSTANTS.Integer8);
						stream.write((byte) i);
					} else if (0 > i && i >= Byte.MIN_VALUE) {
						stream.write(WXF_CONSTANTS.Integer8);
						stream.write((byte) i);
					}
				}
			}

			private void writeAST(IExpr arg1) {
				IAST ast = (IAST) arg1;
				stream.write(WXF_CONSTANTS.Function);
				stream.write(ast.argSize());
				for (int i = 0; i < ast.size(); i++) {
					write(ast.get(i));
				}
			}
		}
	}

	private static class BinaryDeserialize extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1() && ast.arg1().isList()) {

				try {
					byte[] bArray = toByteArray((IAST) ast.arg1());
					ByteArrayInputStream bais = new ByteArrayInputStream(bArray);

					ReadObject rd = new ReadObject(bArray, 2);
					IExpr result = rd.read();
					bais.close();
					return result;
				} catch (ClassCastException cce) {
					if (Config.SHOW_STACKTRACE) {
						cce.printStackTrace();
					}
				} catch (IOException ioe) {
					if (Config.SHOW_STACKTRACE) {
						ioe.printStackTrace();
					}
				}
			}
			return F.NIL;
		}

		private static byte[] toByteArray(IAST list) {
			byte[] result = new byte[list.size() - 1];
			for (int i = 1; i < list.size(); i++) {
				result[i - 1] = ((IInteger) list.get(i)).byteValue();
			}
			return result;
		}

		static class ReadObject {
			byte[] array;
			int position;

			public ReadObject(byte[] array, int position) {
				this.array = array;
				this.position = position;
			}

			private IExpr read() {
				byte exprType = array[position++];
				byte value;
				int length;
				switch (exprType) {
				case WXF_CONSTANTS.Integer8:
					value = array[position++];
					return F.ZZ(value);
				case WXF_CONSTANTS.Symbol:
					return readSymbol();
				case WXF_CONSTANTS.Function:
					length = (int) array[position++];
					IASTAppendable ast = F.ast(F.NIL, length, false);
					ast.set(0, read());
					for (int i = 0; i < length; i++) {
						ast.append(read());
					}
					return ast;
				case WXF_CONSTANTS.String:
					length = (int) array[position++];
					StringBuilder str = new StringBuilder();
					for (int i = 0; i < length; i++) {
						char ch = (char) array[position++];
						str.append(ch);
					}
					return F.stringx(str);
				}
				return F.NIL;
			}

			private IExpr readSymbol() {
				int length = (int) array[position++];
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
	}

	private final static WXFFunctions CONST = new WXFFunctions();

	public static WXFFunctions initialize() {
		return CONST;
	}

	private WXFFunctions() {

	}

}
