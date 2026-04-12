package org.matheclipse.core.expression;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.Map.Entry;
import java.util.Set;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.linear.RealVector;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.data.NumericArrayExpr;
import org.matheclipse.core.integrate.rubi.UtilityFunctionCtors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IDataExpr;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.RulesData;
import org.matheclipse.core.reflection.system.Integrate;
import org.matheclipse.core.reflection.system.Share;
import org.matheclipse.core.visit.AbstractVisitor;
import org.matheclipse.parser.trie.Trie;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.FieldSerializer;

public class KryoUtil {
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
  }

  /** Serialize <code>length</code> into varint bytes and return them as a byte array. */
  @Deprecated
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

  private static class BuiltInSymbolSerializer extends Serializer<IExpr> {

    public BuiltInSymbolSerializer() {
      setImmutable(true);
    }

    @Override
    public void write(Kryo kryo, Output output, IExpr object) {
      if (object instanceof IBuiltInSymbol) {
        output.writeVarInt(((IBuiltInSymbol) object).ordinal(), true);
      }
    }

    @Override
    public IExpr read(Kryo kryo, Input input, Class<? extends IExpr> type) {
      if (type.equals(BuiltInSymbol.class)) {
        int id = input.readVarInt(true);
        return S.symbol(id);
      }
      return null;
    }
  }
  /**
   * Serializer for standard Symbols. Manages the transient Context field by serializing the context
   * name and resolving it against the EvalEngine on deserialization.
   */
  public static class SymbolSerializer extends Serializer<Symbol> {

    public SymbolSerializer() {
      setImmutable(false);
    }

    @Override
    public void write(Kryo kryo, Output output, Symbol object) {
      output.writeString(object.getSymbolName());

      Context context = object.getContext();
      output.writeString(context != null ? context.getContextName() : null);

      output.writeInt(object.getAttributes());

      kryo.writeClassAndObject(output, object.getRulesData());
    }

    @Override
    public Symbol read(Kryo kryo, Input input, Class<? extends Symbol> type) {
      String symbolName = input.readString();
      String contextName = input.readString();
      int attributes = input.readInt();

      Context context = null;
      if (contextName != null) {
        // Map directly to predefined static contexts
        if (contextName.equals(Context.SYSTEM_CONTEXT_NAME)) {
          context = Context.SYSTEM;
        } else if (contextName.equals(Context.DUMMY_CONTEXT_NAME)) {
          context = Context.DUMMY;
        } else if (contextName.equals(Context.FORMAL_CONTEXT_NAME)) {
          context = Context.FORMAL;
          // } else if (contextName.equals(Context.GLOBAL_CONTEXT_NAME)) {
          // context = Context.GLOBAL;
        } else if (contextName.equals(Context.RUBI_STR)) {
          context = Context.RUBI;
        } else {
          // Fallback to current engine's ContextPath
          context = EvalEngine.get().getContextPath().getContext(contextName);
          if (context == null) {
            context = new Context(contextName);
          }
        }
      }

      Symbol symbol = null;
      boolean isNew = false;

      // EXACT IDENTITY LOOKUP
      if (context != null) {
        ISymbol existing = context.get(symbolName);
        if (existing instanceof Symbol) {
          // Symbol already exists in the system memory pool. Use it!
          symbol = (Symbol) existing;
        } else {
          if (contextName.equals(Context.FORMAL_CONTEXT_NAME)) {
            ISymbol formalSymbol = F.HIDDEN_SYMBOLS_MAP.get(symbolName);
            if (formalSymbol instanceof Symbol) {
              symbol = (Symbol) formalSymbol;
            }
          }
          if (symbol == null) {
            // Symbol does not exist. Create it...
            symbol = new Symbol(symbolName, context);
            // ...and IMMEDIATELY register it in the context so future
            // references in this stream use this exact instance.
            context.put(symbolName, symbol);
            isNew = true;
          }
        }
      } else {
        symbol = new Symbol(symbolName, null);
        isNew = true;
      }

      // CRITICAL: Register reference in Kryo to avoid cyclic reference overflow
      kryo.reference(symbol);

      // We MUST read the RulesData to advance the Kryo stream pointer,
      // even if we are discarding it for an already-existing symbol.
      RulesData rulesData = (RulesData) kryo.readClassAndObject(input);

      // State Application
      // Only overwrite attributes and rules if this was a brand new symbol.
      // If it already existed, we respect its live state in the system.
      if (isNew) {
        symbol.setAttributes(attributes);
        if (rulesData != null) {
          symbol.setRulesData(rulesData);
        }
      }
      return symbol;
    }
  }
  private static class BuiltInRubiSerializer extends Serializer<BuiltInRubi> {

    public BuiltInRubiSerializer() {
      setImmutable(true);
    }

    @Override
    public void write(Kryo kryo, Output stream, BuiltInRubi rubISymbol) {
      stream.writeString(rubISymbol.getSymbolName());
    }

    @Override
    public BuiltInRubi read(Kryo kryo, Input input, Class<? extends BuiltInRubi> type) {
      if (type.equals(BuiltInRubi.class)) {
        String rubiName = input.readString();
        BuiltInRubi rubiSymbol = (BuiltInRubi) F.$rubi(rubiName);
        return rubiSymbol;
      }
      return null;
    }
  }

  private static class ContextSerializer extends Serializer<Context> {

    @Override
    public void write(Kryo kryo, Output stream, Context context) {
      stream.writeString(context.getContextName());
      Set<Entry<String, ISymbol>> entrySet = context.entrySet();
      stream.writeInt(entrySet.size());
      for (Entry<String, ISymbol> entry : entrySet) {
        stream.writeString(entry.getKey());
        kryo.writeClassAndObject(stream, entry.getValue());
      }
      for (Entry<String, ISymbol> entry : entrySet) {
        kryo.writeClassAndObject(stream, entry.getValue().getRulesData());
      }
    }

    @Override
    public Context read(Kryo kryo, Input input, Class<? extends Context> type) {
      if (type.equals(Context.class)) {
        ContextPath contextPath = EvalEngine.get().getContextPath();
        String contextName = input.readString();
        Context context = new Context(contextName);
        if (contextName.equals(Context.RUBI_STR)) {
          Context.RUBI = context;
          contextPath.setContext(Context.RUBI_STR, context);
        } else if (contextName.equals(Context.GLOBAL_CONTEXT_NAME)) {
          contextPath.setContext(Context.GLOBAL_CONTEXT_NAME, context);
        } else if (contextName.equals(Context.DUMMY_CONTEXT_NAME)) {
          contextPath.setContext(Context.DUMMY_CONTEXT_NAME, context);
        }
        int size = input.readInt();
        ISymbol[] symbolArray = new ISymbol[size];
        for (int i = 0; i < size; i++) {
          String symbolName = input.readString();
          ISymbol symbol = (ISymbol) kryo.readClassAndObject(input);
          symbolArray[i] = symbol;
          context.put(symbolName, symbol);
        }
        for (int i = 0; i < size; i++) {
          RulesData rulesData = (RulesData) kryo.readClassAndObject(input);
          symbolArray[i].setRulesData(rulesData);
        }
        return context;
      }
      return null;
    }
  }

  /**
   * Serializer for expressions implementing IDataExpr. Uses the standard normal form if available,
   * otherwise falls back to field serialization.
   */
  private static class DataExprSerializer<T extends IDataExpr<?>> extends Serializer<T> {
    private final Serializer<T> fallbackSerializer;

    public DataExprSerializer(Kryo kryo, Class<T> type) {
      // Initialize default field serializer as fallback for when normal(false) is null
      this.fallbackSerializer = new FieldSerializer<>(kryo, type);
    }

    @Override
    public void write(Kryo kryo, Output output, T object) {
      IAST ast = object.normal(false);
      if (ast != null) {
        output.writeBoolean(true);
        kryo.writeClassAndObject(output, ast);
      } else {
        output.writeBoolean(false);
        fallbackSerializer.write(kryo, output, object);
      }
    }

    @Override
    public T read(Kryo kryo, Input input, Class<? extends T> type) {
      boolean hasNormalForm = input.readBoolean();
      if (hasNormalForm) {
        IAST ast = (IAST) kryo.readClassAndObject(input);
        IExpr eval = EvalEngine.get().evaluate(ast);
        if (type.isInstance(eval)) {
          return type.cast(eval);
        }
        return null;
      } else {
        return fallbackSerializer.read(kryo, input, type);
      }
    }
  }

  /**
   * Optimized serializer for RulesData. Serializes the raw IAST definitions instead of complex
   * internal pattern matchers.
   */
  /**
   * Exact-state serializer for RulesData. Uses reflection to directly serialize the internal
   * collections, perfectly preserving the IPatternMatcher objects, their explicit priorities,
   * pattern hashes, and list ordering.
   */
  public static class RulesDataSerializer extends Serializer<RulesData> {

    private Field equalDownRulesField;
    private Field patternDownRulesField;
    private Field priorityDownRulesField;
    private Field equalUpRulesField;
    private Field simplePatternUpRulesField;

    public RulesDataSerializer() {
      setImmutable(false);
      try {
        // Gain access to the internal private collections
        equalDownRulesField = RulesData.class.getDeclaredField("fEqualDownRules");
        equalDownRulesField.setAccessible(true);

        patternDownRulesField = RulesData.class.getDeclaredField("fPatternDownRules");
        patternDownRulesField.setAccessible(true);

        priorityDownRulesField = RulesData.class.getDeclaredField("fPriorityDownRules");
        priorityDownRulesField.setAccessible(true);

        equalUpRulesField = RulesData.class.getDeclaredField("fEqualUpRules");
        equalUpRulesField.setAccessible(true);

        simplePatternUpRulesField = RulesData.class.getDeclaredField("fSimplePatternUpRules");
        simplePatternUpRulesField.setAccessible(true);
      } catch (Exception e) {
        System.err.println("Failed to initialize RulesData reflection fields: " + e.getMessage());
      }
    }

    @Override
    public void write(Kryo kryo, Output output, RulesData rulesData) {
      try {
        // Write the raw underlying collections directly.
        // Kryo's default FieldSerializer will automatically traverse the PatternMatcher
        // objects inside these lists and preserve all of their internal state.
        kryo.writeClassAndObject(output, equalDownRulesField.get(rulesData));
        kryo.writeClassAndObject(output, patternDownRulesField.get(rulesData));
        kryo.writeClassAndObject(output, priorityDownRulesField.get(rulesData));
        kryo.writeClassAndObject(output, equalUpRulesField.get(rulesData));
        kryo.writeClassAndObject(output, simplePatternUpRulesField.get(rulesData));
      } catch (Exception e) {
        throw new RuntimeException("Failed to serialize RulesData collections", e);
      }
    }

    @Override
    public RulesData read(Kryo kryo, Input input, Class<? extends RulesData> type) {
      RulesData rulesData = new RulesData();
      try {
        // Restore the exact collections directly into the new RulesData instance
        equalDownRulesField.set(rulesData, kryo.readClassAndObject(input));
        patternDownRulesField.set(rulesData, kryo.readClassAndObject(input));
        priorityDownRulesField.set(rulesData, kryo.readClassAndObject(input));
        equalUpRulesField.set(rulesData, kryo.readClassAndObject(input));
        simplePatternUpRulesField.set(rulesData, kryo.readClassAndObject(input));
      } catch (Exception e) {
        throw new RuntimeException("Failed to deserialize RulesData collections", e);
      }
      return rulesData;
    }
  }

  private static class BuiltInDummySerializer extends Serializer<BuiltInDummy> {
    public BuiltInDummySerializer() {
      setImmutable(true);
    }

    @Override
    public void write(Kryo kryo, Output output, BuiltInDummy object) {
      output.writeString(object.getSymbolName());
    }

    @Override
    public BuiltInDummy read(Kryo kryo, Input input, Class<? extends BuiltInDummy> type) {
      if (type.equals(BuiltInDummy.class)) {
        String rubiName = input.readString();
        return (BuiltInDummy) F.$s(rubiName);
      }
      return null;
    }
  }

  @Deprecated
  public static class IASTSerializer extends Serializer<IAST> {

    @Deprecated
    @Override
    public void write(Kryo kryo, Output stream, IAST ast) {
      if (ast instanceof ASTRealVector) {
        RealVector vector = ((ASTRealVector) ast).getRealVector();
        stream.writeInt(WXF_CONSTANTS.PackedArray);
        stream.write(NumericArrayExpr.Real64);
        stream.write(0x01);
        stream.write(varintBytes(vector.getDimension()));
        for (int i = 0; i < vector.getDimension(); i++) {
          writeDouble(stream, vector.getEntry(i));
        }
        return;
      } else if (ast instanceof ASTRealMatrix) {
        RealMatrix matrix = ((ASTRealMatrix) ast).getRealMatrix();
        stream.writeInt(WXF_CONSTANTS.PackedArray);
        stream.write(NumericArrayExpr.Real64);
        stream.write(0x02);
        stream.write(varintBytes(matrix.getRowDimension()));
        stream.write(varintBytes(matrix.getColumnDimension()));
        for (int i = 0; i < matrix.getRowDimension(); i++) {
          for (int j = 0; j < matrix.getColumnDimension(); j++) {
            writeDouble(stream, matrix.getEntry(i, j));
          }
        }
        return;
      } else if (ast instanceof ASTAssociation) {
        // <|a->b, c:>d,...|>
        stream.writeInt(WXF_CONSTANTS.Association);
        stream.write(varintBytes(ast.argSize()));
        for (int i = 1; i < ast.size(); i++) {
          IAST rule = (IAST) ast.getRule(i);
          if (rule.isRuleDelayed()) {
            stream.write(WXF_CONSTANTS.RuleDelayed);
          } else {
            stream.write(WXF_CONSTANTS.Rule);
          }
          kryo.writeObject(stream, rule.arg1());
          kryo.writeObject(stream, rule.arg2());
        }
        return;
      }
      stream.writeInt(WXF_CONSTANTS.Function);
      stream.writeInt(ast.argSize());
      for (int i = 0; i < ast.size(); i++) {
        kryo.writeClassAndObject(stream, ast.get(i));
      }

    }

    private void writeDouble(Output stream, double d) {
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

    @Deprecated
    @Override
    public IAST read(Kryo kryo, Input input, Class<? extends IAST> type) {
      if (IAST.class.isAssignableFrom(type)) {
        int exprType = input.readInt();
        switch (exprType) {
          case WXF_CONSTANTS.Function:
            int length = input.readInt();
            IASTAppendable ast = F.ast(F.NIL, length);
            ast.set(0, (IExpr) kryo.readClassAndObject(input));
            for (int i = 0; i < length; i++) {
              ast.append((IExpr) kryo.readClassAndObject(input));
            }
            return ast;
        }
      }
      return null;
    }
  }

  @Deprecated
  static public void main(String[] args) throws Exception {
    // List<Class<?>> asList = Arrays.asList(B3.class.getDeclaredClasses());
    // for (int i = 0; i < asList.size(); i++) {
    // System.out.println(
    // "kryo.register(B3." + asList.get(i).getSimpleName() + ".class);");
    // }

    F.initSymja();
    Kryo kryo = initKryo();
    // IBuiltInSymbol object = S.Im;
    // IAST object = F.Sin(F.x);
    // IAST object =F.ternaryAST3(F.f, F.x, F.Dummy("$dummy"), F.stringx("string"));

    Integrate.CONST.await();

    Context rubiContext = Context.RUBI;
    Output rubiOutput = new Output(new FileOutputStream("rubi_context.bin"));
    kryo.writeClassAndObject(rubiOutput, rubiContext);
    rubiOutput.close();

    Input rubiInput = new Input(new FileInputStream("rubi_context.bin"));
    rubiContext = (Context) kryo.readClassAndObject(rubiInput);
    System.out.println(rubiContext.toString());
    rubiInput.close();

    IBuiltInSymbol symbol = S.Integrate;
    RulesData rulesData = symbol.getRulesData();
    AbstractVisitor visitor = Share.createVisitor();
    rulesData.accept(visitor);
    Output output = new Output(new FileOutputStream("integrate.bin"));
    kryo.writeClassAndObject(output, rulesData);
    output.close();

    Input input = new Input(new FileInputStream("integrate.bin"));
    RulesData object2 = (RulesData) kryo.readClassAndObject(input);
    System.out.println(object2.definition());
    input.close();
  }

  public static Kryo initKryo() throws ClassNotFoundException {
    Kryo kryo = new Kryo();
    kryo.setReferences(true);

    kryo.register(Context.class, new ContextSerializer());
    kryo.register(Trie.class);
    kryo.register(java.util.ArrayList.class);
    kryo.register(java.util.HashMap.class);
    kryo.register(java.util.TreeMap.class);
    kryo.register(org.matheclipse.core.eval.util.OpenIntToIExprHashMap.class);
    kryo.register(it.unimi.dsi.fastutil.ints.IntArrayList.class);
    // kryo.register(org.matheclipse.core.patternmatching.RulesData.class);
    kryo.register(org.matheclipse.core.patternmatching.RulesData.class, new RulesDataSerializer());
    kryo.register(org.matheclipse.core.patternmatching.PatternMatcherEquals.class);
    kryo.register(org.matheclipse.core.patternmatching.PatternMatcherAndEvaluator.class);

    // numbers
    kryo.register(ApcomplexNum.class);
    kryo.register(ApfloatNum.class);
    kryo.register(BigFractionSym.class);
    kryo.register(BigIntegerSym.class);
    kryo.register(ComplexSym.class);
    kryo.register(ComplexNum.class);
    kryo.register(FractionSym.class);
    kryo.register(IntegerSym.class);
    kryo.register(Num.class);
    kryo.register(NumStr.class);

    // atoms
    kryo.register(IExpr.class);
    kryo.register(Symbol.class, new SymbolSerializer());
    kryo.register(StringX.class);

    // patterns
    kryo.register(Blank.class);
    kryo.register(OptionsPattern.class);
    kryo.register(Pattern.class);
    kryo.register(PatternNested.class);
    kryo.register(PatternSequence.class);
    kryo.register(RepeatedPattern.class);

    // IAST
    kryo.register(AST.class, new IASTSerializer());
    kryo.register(AST0.class);
    kryo.register(AST1.class);
    kryo.register(AST2.class);
    kryo.register(AST3.class);
    kryo.register(ASTAssociation.class, new IASTSerializer());
    kryo.register(ASTRealMatrix.class, new IASTSerializer());
    kryo.register(ASTRealVector.class, new IASTSerializer());
    kryo.register(ASTSeriesData.class, new IASTSerializer());

    kryo.register(B1.Cos.class);
    kryo.register(B1.Csc.class);
    kryo.register(B1.Im.class);
    kryo.register(B1.IntegerQ.class);
    kryo.register(B1.Line.class);
    kryo.register(B1.List.class);
    kryo.register(B1.Log.class);
    kryo.register(B1.Missing.class);
    kryo.register(B1.Not.class);
    kryo.register(B1.Point.class);
    kryo.register(B1.Re.class);
    kryo.register(B1.Return.class);
    kryo.register(B1.Sin.class);
    kryo.register(B1.Slot.class);
    kryo.register(B1.Tan.class);
    kryo.register(B1.Throw.class);

    kryo.register(B2.And.class);
    kryo.register(B2.B2Set.class);
    kryo.register(B2.Condition.class);
    kryo.register(B2.DirectedEdge.class);
    kryo.register(B2.Equal.class);
    kryo.register(B2.FreeQ.class);
    kryo.register(B2.Greater.class);
    kryo.register(B2.GreaterEqual.class);
    kryo.register(B2.If.class);
    kryo.register(B2.Integrate.class);
    kryo.register(B2.Less.class);
    kryo.register(B2.LessEqual.class);
    kryo.register(B2.List.class);
    kryo.register(B2.MemberQ.class);
    kryo.register(B2.Or.class);
    kryo.register(B2.Part.class);
    kryo.register(B2.Plus.class);
    kryo.register(B2.PolynomialQ.class);
    kryo.register(B2.Power.class);
    kryo.register(B2.Rule.class);
    kryo.register(B2.RuleDelayed.class);
    kryo.register(B2.SameQ.class);
    kryo.register(B2.Times.class);
    kryo.register(B2.UndirectedEdge.class);
    kryo.register(B2.With.class);

    kryo.register(B3.And.class);
    kryo.register(B3.Equal.class);
    kryo.register(B3.Greater.class);
    kryo.register(B3.GreaterEqual.class);
    kryo.register(B3.If.class);
    kryo.register(B3.Less.class);
    kryo.register(B3.LessEqual.class);
    kryo.register(B3.List.class);
    kryo.register(B3.Or.class);
    kryo.register(B3.Part.class);
    kryo.register(B3.Plus.class);
    kryo.register(B3.Times.class);

    // data expressions
    kryo.addDefaultSerializer(IDataExpr.class, DataExprSerializer.class);
    // kryo.register(ByteArrayExpr.class);
    // kryo.register(CompiledFunctionExpr.class);
    // kryo.register(DateObjectExpr.class);
    // kryo.register(DispatchExpr.class);
    // kryo.register(ExprEdge.class);
    // kryo.register(ExprWeightedEdge.class);
    // kryo.register(FileExpr.class);
    // kryo.register(FittedModelExpr.class);
    // kryo.register(GeoPositionExpr.class);
    // kryo.register(Graph.class);
    // kryo.register(GraphExpr.class);
    // kryo.register(BufferedImage.class);
    // // kryo.register(ImageExpr.class);
    // kryo.register(InputStreamExpr.class);
    // kryo.register(InterpolatingFunctionExpr.class);
    // kryo.register(JavaClassExpr.class);
    // kryo.register(JavaObjectExpr.class);
    // kryo.register(LinearSolveFunctionExpr.class);
    // kryo.register(NumericArrayExpr.class);
    // kryo.register(OutputStreamExpr.class);
    // kryo.register(SparseArrayExpr.class);
    // kryo.register(TestReportObjectExpr.class);
    // kryo.register(TestResultObjectExpr.class);
    // kryo.register(TimeObjectExpr.class);
    // kryo.register(BDDExpr.class);

    kryo.register(BuiltInSymbol.class, new BuiltInSymbolSerializer());
    kryo.register(BuiltInRubi.class, new BuiltInRubiSerializer());
    kryo.register(BuiltInDummy.class, new BuiltInDummySerializer());

    UtilityFunctionCtors.registerKryo(kryo);
    return kryo;
  }

}
