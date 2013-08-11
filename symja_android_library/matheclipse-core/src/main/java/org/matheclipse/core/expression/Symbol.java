package org.matheclipse.core.expression;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.SystemNamespace;
import org.matheclipse.core.eval.exception.RuleCreationError;
import org.matheclipse.core.eval.interfaces.INumericConstant;
import org.matheclipse.core.eval.interfaces.ISymbolEvaluator;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.generic.UnaryVariable2Slot;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IEvaluationEngine;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.DownRulesData;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcherAndInvoker;
import org.matheclipse.core.patternmatching.PatternMatcherEquals;
import org.matheclipse.core.patternmatching.UpRulesData;
import org.matheclipse.core.util.OpenIntToIExprHashMap;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.generic.interfaces.INumericFunction;

import com.google.common.base.Function;

/**
 * Implements Symbols for function, constant and variable names
 * 
 */
public class Symbol extends ExprImpl implements ISymbol {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4991038487281911261L;

	/**
	 * 
	 */

	private static final int DEFAULT_VALUE_INDEX = Integer.MIN_VALUE;

	/**
	 * The attribute values of the symbol represented by single bits.
	 */
	private int fAttributes = NOATTRIBUTE;

	private transient IEvaluator fEvaluator;

	/**
	 * The pattern matching &quot;down value&quot; rules associated with this symbol.
	 */
	private transient DownRulesData fDownRulesData = new DownRulesData();

	/**
	 * The pattern matching &quot;up value&quot; rules associated with this symbol.
	 */
	private transient UpRulesData fUpRulesData = new UpRulesData();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IExpr[] reassignSymbolValue(Function<IExpr, IExpr> function) {
		IExpr[] result = new IExpr[2];
		IExpr symbolValue;
		if (hasLocalVariableStack()) {
			symbolValue = get();
			result[0] = symbolValue;
			IExpr calculatedResult = function.apply(symbolValue);
			if (calculatedResult != null) {
				set(calculatedResult);
				result[1] = calculatedResult;
				return result;
			}

		} else {
			// Pair<ISymbol, IExpr> pair = fRulesData.getEqualRules().get(this);
			// if (pair != null) {
			// symbolValue = pair.getSecond();
			// if (symbolValue != null) {
			// result[0] = symbolValue;
			// IExpr calculatedResult = function.apply(symbolValue);
			// if (calculatedResult != null) {
			// pair.setSecond(calculatedResult);
			// result[1] = calculatedResult;
			// return result;
			// }
			// }
			// }
			if (fDownRulesData != null) {
				PatternMatcherEquals pme = fDownRulesData.getEqualDownRules().get(this);
				if (pme != null) {
					symbolValue = pme.getRHS();
					if (symbolValue != null) {
						result[0] = symbolValue;
						IExpr calculatedResult = function.apply(symbolValue);
						if (calculatedResult != null) {
							pme.setRHS(calculatedResult);
							result[1] = calculatedResult;
							return result;
						}
					}
				}
			}
		}
		return null;
	}

	private OpenIntToIExprHashMap fDefaultValues = null;

	static class DummyEvaluator implements IEvaluator {
		@Override
		public void setUp(ISymbol symbol) {

		}
	}

	protected static final DummyEvaluator DUMMY_EVALUATOR = new DummyEvaluator();

	/* package private */String fSymbolName;

	/**
	 * The hash value of this object computed in the constructor.
	 * 
	 */
	final int fHashValue;

	public Symbol(final String symbolName) {
		this(symbolName, null);
	}

	public Symbol(final String symbolName, final IEvaluator evaluator) {
		super();
		fHashValue = (symbolName == null) ? 197 : 7 * symbolName.hashCode();
		fSymbolName = symbolName;
		fEvaluator = evaluator;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr apply(IExpr... expressions) {
		return F.ast(expressions, this);
	}

	/** {@inheritDoc} */
	@Override
	public void pushLocalVariable() {
		pushLocalVariable(null);
	}

	/** {@inheritDoc} */
	@Override
	public void pushLocalVariable(final IExpr expression) {
		final Stack<IExpr> localVariableStack = EvalEngine.localStackCreate(fSymbolName);
		localVariableStack.push(expression);
	}

	/** {@inheritDoc} */
	@Override
	public void popLocalVariable() {
		final Stack<IExpr> fLocalVariableStack = EvalEngine.localStack(fSymbolName);
		fLocalVariableStack.pop();
	}

	/** {@inheritDoc} */
	@Override
	public void clear(EvalEngine engine) {
		if (!engine.isPackageMode()) {
			if (Config.SERVER_MODE && (fSymbolName.charAt(0) != '$')) {
				throw new RuleCreationError(null);
			}
		}
		if (fDownRulesData != null) {
			fDownRulesData.clear();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void clearAll(EvalEngine engine) {
		clear(engine);
		fAttributes = NOATTRIBUTE;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object obj) {
		// Symbols are unique objects
		// Makes no sense to compare the symbol names.
		return this == obj;
	}

	@Override
	public boolean isSymbolName(String name) {
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			return fSymbolName.equalsIgnoreCase(name);
		}
		return fSymbolName.equals(name);
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(EvalEngine engine) {
		if (hasLocalVariableStack()) {
			return get();
		}
		IExpr result;
		if ((result = evalDownRule(engine, this)) != null) {
			return result;
		}
		final IEvaluator module = getEvaluator();
		if (module instanceof ISymbolEvaluator) {
			if (engine.isNumericMode()) {
				return ((ISymbolEvaluator) module).numericEval(this);
			}
			return ((ISymbolEvaluator) module).evaluate(this);
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evalDownRule(final IEvaluationEngine ee, final IExpr expression) {
		if (fDownRulesData == null) {
			return null;
		}
		return fDownRulesData.evalDownRule(ee, expression);
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evalUpRule(final IEvaluationEngine ee, final IExpr expression) {
		if (fUpRulesData == null) {
			return null;
		}
		return fUpRulesData.evalUpRule(ee, expression);
	}

	/** {@inheritDoc} */
	@Override
	public final int getAttributes() {
		return fAttributes;
	}

	/** {@inheritDoc} */
	@Override
	public IEvaluator getEvaluator() {
		if (fEvaluator == null) {
			fEvaluator = DUMMY_EVALUATOR;
			if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
				SystemNamespace.DEFAULT.setEvaluator(this);
			} else {
				if (Character.isUpperCase(fSymbolName.charAt(0))) {
					SystemNamespace.DEFAULT.setEvaluator(this);
				}
			}
		}
		return fEvaluator;
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasLocalVariableStack() {
		final Stack<IExpr> localVariableStack = EvalEngine.localStack(fSymbolName);
		return (localVariableStack != null) && !(localVariableStack.isEmpty());
	}

	/** {@inheritDoc} */
	@Override
	public IExpr get() {
		final Stack<IExpr> localVariableStack = EvalEngine.localStack(fSymbolName);
		if (localVariableStack == null) {
			return null;
		}
		return localVariableStack.peek();
	}

	/** {@inheritDoc} */
	@Override
	public void set(final IExpr value) {
		final Stack<IExpr> localVariableStack = EvalEngine.localStack(fSymbolName);

		localVariableStack.set(localVariableStack.size() - 1, value);
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return fHashValue;
	}

	/** {@inheritDoc} */
	@Override
	public int hierarchy() {
		return SYMBOLID;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isString(final String str) {
		return fSymbolName.equals(str);
	}

	/** {@inheritDoc} */
	@Override
	public IPatternMatcher putDownRule(ISymbol symbol, final boolean equalRule, final IExpr leftHandSide,
			final IExpr rightHandSide, boolean packageMode) {
		return putDownRule(symbol, equalRule, leftHandSide, rightHandSide, DEFAULT_RULE_PRIORITY, packageMode);
	}

	/** {@inheritDoc} */
	@Override
	public IPatternMatcher putDownRule(ISymbol setSymbol, final boolean equalRule, final IExpr leftHandSide,
			final IExpr rightHandSide, final int priority, boolean packageMode) {
		if (!packageMode) {
			if (Config.SERVER_MODE && (fSymbolName.charAt(0) != '$')) {
				throw new RuleCreationError(leftHandSide);
			}

			EvalEngine.get().addModifiedVariable(this);
		}
//		EvalEngine engine = EvalEngine.get();
//		if (!engine.isPackageMode()) {
//			if (Config.SERVER_MODE && (fSymbolName.charAt(0) != '$')) {
//				throw new RuleCreationError(leftHandSide);
//			}
//
//			engine.addModifiedVariable(this);
//		}
		if (fDownRulesData == null) {
			fDownRulesData = new DownRulesData();
		}
		return fDownRulesData.putDownRule(setSymbol, equalRule, leftHandSide, rightHandSide, priority);
	}

	/** {@inheritDoc} */
	@Override
	public PatternMatcher putDownRule(final PatternMatcherAndInvoker pmEvaluator) {
		if (fDownRulesData == null) {
			fDownRulesData = new DownRulesData();
		}
		return fDownRulesData.putDownRule(pmEvaluator);
	}

	/** {@inheritDoc} */
	@Override
	public IPatternMatcher putUpRule(ISymbol symbol, boolean equalRule, IAST leftHandSide, IExpr rightHandSide) {
		return putUpRule(symbol, equalRule, leftHandSide, rightHandSide, DEFAULT_RULE_PRIORITY);
	}

	/** {@inheritDoc} */
	@Override
	public IPatternMatcher putUpRule(ISymbol setSymbol, final boolean equalRule, final IAST leftHandSide,
			final IExpr rightHandSide, final int priority) {
		EvalEngine engine = EvalEngine.get();
		if (!engine.isPackageMode()) {
			if (Config.SERVER_MODE && (fSymbolName.charAt(0) != '$')) {
				throw new RuleCreationError(leftHandSide);
			}

			engine.addModifiedVariable(this);
		}
		if (fUpRulesData == null) {
			fUpRulesData = new UpRulesData();
		}
		return fUpRulesData.putUpRule(setSymbol, equalRule, leftHandSide, rightHandSide, priority);
	}

	/** {@inheritDoc} */
	@Override
	public void setAttributes(final int attributes) {
		fAttributes = attributes;
		if (fSymbolName.charAt(0) == '$' && Config.SERVER_MODE) {
			EvalEngine engine = EvalEngine.get();
			engine.addModifiedVariable(this);
		}
	}

	/** {@inheritDoc} */
	@Override
	public final void setEvaluator(final IEvaluator evaluator) {
		fEvaluator = evaluator;
		evaluator.setUp(this);
	}

	/**
	 * Compares this expression with the specified expression for order. Returns a negative integer, zero, or a positive integer as
	 * this expression is canonical less than, equal to, or greater than the specified expression.
	 */
	@Override
	public int compareTo(final IExpr obj) {
		if (obj instanceof Symbol) {
			if (this == obj) {
				// Symbols are unique objects
				// Makes no sense to compare the symbol names, if they are equal
				return 0;
			}
			return fSymbolName.compareTo(((Symbol) obj).fSymbolName);
		}
		if (obj instanceof AST) {
			final AST ast = (AST) obj;
			final IExpr header = ast.head();
			if (ast.size() > 1) {
				if (header == F.Power && ast.size() == 3) {
					if (ast.get(1) instanceof ISymbol) {
						final int cp = fSymbolName.compareTo(((Symbol) ast.get(1)).fSymbolName);
						if (cp != 0) {
							return cp;
						}
						if (EvalEngine.get().isNumericMode()) {
							return F.CD1.compareTo(ast.get(2));
						}
						return F.C1.compareTo(ast.get(2));
					}
				} else if (header == F.Times) {
					// compare with the last ast element:
					final IExpr lastTimes = ast.get(ast.size() - 1);
					if (lastTimes instanceof AST) {
						final IExpr lastTimesHeader = ((IAST) lastTimes).head();
						if ((lastTimesHeader == F.Power) && (((IAST) lastTimes).size() == 3)) {
							final int cp = compareTo(((IAST) lastTimes).get(1));
							if (cp != 0) {
								return cp;
							}
							return F.C1.compareTo(((IAST) lastTimes).get(2));
						}
					}
					final int cp = compareTo(lastTimes);
					if (cp != 0) {
						return cp;
					}
				}
			}
			return -1;
		}
		return (hierarchy() - (obj).hierarchy());
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isAtom() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isConstant() {
		return (fAttributes & CONSTANT) == CONSTANT;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isTrue() {
		return this.equals(F.True);
		// return fSymbolName.equals("True");
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isValue() {
		return evaluate(EvalEngine.get()) != null;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isValue(IAST ast) {
		if (ast.head() instanceof ISymbol) {
			IExpr result = ((ISymbol) ast.head()).evalDownRule(EvalEngine.get(), ast);
			return result != null;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isFalse() {
		// return fSymbolName.equals("False");
		return this.equals(F.False);
	}

	/** {@inheritDoc} */
	@Override
	public ISymbol head() {
		return F.SymbolHead;
	}

	/** {@inheritDoc} */
	public final String getSymbol() {
		return fSymbolName;
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public IExpr variables2Slots(final Map<IExpr, IExpr> map, final List<IExpr> variableList) {
		final UnaryVariable2Slot uv2s = new UnaryVariable2Slot(map, variableList);
		return uv2s.apply(this);
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public String internalFormString(boolean symbolsAsFactoryMethod, int depth) {
		if (symbolsAsFactoryMethod) {
			if (fSymbolName.length() == 1 && Character.isLowerCase(fSymbolName.charAt(0))) {
				char ch = fSymbolName.charAt(0);
				if ('a' <= ch && ch <= 'z') {
					return fSymbolName;
				}
			}
			if (Character.isUpperCase(fSymbolName.charAt(0))) {
				String alias = F.PREDEFINED_INTERNAL_FORM_STRINGS.get(fSymbolName);
				if (alias != null) {
					if (alias.contains("::")) {
						return "$s(\"" + alias + "\")";
					}
					return alias;
				}
			}
			return "$s(\"" + fSymbolName + "\")";
		}
		return fSymbolName;
	}

	@Override
	public String toString() {
		return fSymbolName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String fullFormString() {
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			String str = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(fSymbolName);
			if (str != null) {
				return str;
			}
		}
		return fSymbolName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IAST> definition() {
		ArrayList<IAST> result = new ArrayList<IAST>();
		if (fDownRulesData != null) {
			result.addAll(fDownRulesData.definition());
		}
		if (fUpRulesData == null) {
			return result;
		}
		result.addAll(fUpRulesData.definition());
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr getDefaultValue() {
		// special case for a general default value
		if (fDefaultValues == null) {
			return null;
		}
		return fDefaultValues.get(DEFAULT_VALUE_INDEX);
	}

	/** {@inheritDoc} */
	@Override
	public IExpr getDefaultValue(int pos) {
		// default value at this position
		if (fDefaultValues == null) {
			return null;
		}
		return fDefaultValues.get(Integer.valueOf(pos));
	}

	/** {@inheritDoc} */
	@Override
	public void setDefaultValue(IExpr expr) {
		// special case for a general default value
		if (fDefaultValues == null) {
			fDefaultValues = new OpenIntToIExprHashMap();
		}
		fDefaultValues.put(DEFAULT_VALUE_INDEX, expr);
	}

	/** {@inheritDoc} */
	@Override
	public void setDefaultValue(int pos, IExpr expr) {
		// default value at this position
		if (fDefaultValues == null) {
			fDefaultValues = new OpenIntToIExprHashMap();
		}
		fDefaultValues.put(Integer.valueOf(pos), expr);
	}

	/** {@inheritDoc} */
	@Override
	public String definitionToString() throws IOException {
		// dummy call to ensure, that the associated rules are loaded:
		getEvaluator();

		StringWriter buf = new StringWriter();

		OutputFormFactory off = OutputFormFactory.get();
		off.setIgnoreNewLine(true);
		List<IAST> list = definition();
		buf.append("{");
		for (int i = 0; i < list.size(); i++) {
			off.convert(buf, list.get(i));
			if (i < list.size() - 1) {
				buf.append(",\n ");
				off.setColumnCounter(0);
			}
		}
		buf.append("}\n");
		return buf.toString();
	}

	/** {@inheritDoc} */
	@Override
	public void readSymbol(java.io.ObjectInputStream stream) throws IOException {
		fSymbolName = stream.readUTF();
		fAttributes = stream.read();
		boolean hasDownRulesData = stream.readBoolean();
		if (hasDownRulesData) {
			fDownRulesData.readSymbol(stream);
		}
		boolean hasUpRulesData = stream.readBoolean();
		if (hasUpRulesData) {
			fUpRulesData.readSymbol(stream);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void writeSymbol(java.io.ObjectOutputStream stream) throws java.io.IOException {
		stream.writeUTF(fSymbolName);
		stream.write(fAttributes);
		if (fDownRulesData == null) {
			stream.writeBoolean(false);
		} else {
			stream.writeBoolean(true);
			fDownRulesData.writeSymbol(stream);
		}
		if (fUpRulesData == null) {
			stream.writeBoolean(false);
		} else {
			stream.writeBoolean(true);
			fUpRulesData.writeSymbol(stream);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T accept(IVisitor<T> visitor) {
		return visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean accept(IVisitorBoolean visitor) {
		return visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int accept(IVisitorInt visitor) {
		return visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IExpr mapConstantDouble(INumericFunction<IExpr> function) {
		if (isConstant()) {
			IEvaluator evaluator = getEvaluator();
			if (evaluator instanceof INumericConstant) {
				INumericConstant numericConstant = (INumericConstant) evaluator;
				double value = numericConstant.evalReal();
				if (value < Integer.MAX_VALUE && value > Integer.MIN_VALUE) {
					return function.apply(value);
				}
			}
		}
		return null;
	}

	@Override
	public IExpr negate() {
		return F.Times(F.CN1, this);
	}
}