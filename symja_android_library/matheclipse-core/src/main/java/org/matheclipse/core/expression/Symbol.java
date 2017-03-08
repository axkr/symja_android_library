package org.matheclipse.core.expression;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import org.hipparchus.complex.Complex;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.RuleCreationError;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.generic.UnaryVariable2Slot;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IEvaluationEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMap;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcherAndInvoker;
import org.matheclipse.core.patternmatching.PatternMatcherEquals;
import org.matheclipse.core.patternmatching.RulesData;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;

public class Symbol implements ISymbol, Serializable {

	/**
	 * The attribute values of the symbol represented by single bits.
	 */
	protected int fAttributes = NOATTRIBUTE;
	/**
	 * The pattern matching &quot;down value&quot; rules associated with this
	 * symbol.
	 */
	private transient RulesData fRulesData;

	/**
	 * The name of this symbol. The characters may be all lower-cases if the
	 * system doesn't distinguish between lower- and upper-case function names.
	 */
	protected String fSymbolName;
	/**
	 * The hash value of this object computed in the constructor.
	 * 
	 */
	protected final int fHashValue;

	public Symbol(final String symbolName) {
		super();
		fHashValue = symbolName.hashCode();
		fSymbolName = symbolName;
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

	/** {@inheritDoc} */
	@Override
	public long accept(IVisitorLong visitor) {
		return visitor.visit(this);
	}

	/** {@inheritDoc} */
	@Override
	public final IExpr apply(IExpr... expressions) {
		return F.ast(expressions, this);
	}

	/** {@inheritDoc} */
	@Override
	public final void clear(EvalEngine engine) {
		if (!engine.isPackageMode()) {
			if (Config.SERVER_MODE && (fSymbolName.charAt(0) != '$')) {
				throw new RuleCreationError(null);
			}
		}
		if (fRulesData != null) {
			fRulesData.clear();
		}
	}

	/** {@inheritDoc} */
	@Override
	public final void clearAll(EvalEngine engine) {
		clear(engine);
		fAttributes = NOATTRIBUTE;
	}

	/**
	 * Compares this expression with the specified expression for order. Returns
	 * a negative integer, zero, or a positive integer as this expression is
	 * canonical less than, equal to, or greater than the specified expression.
	 */
	@Override
	public int compareTo(final IExpr expr) {
		if (expr instanceof Symbol) {
			if (this == expr) {
				// Symbols are unique objects
				// Makes no sense to compare the symbol names, if they are equal
				return 0;
			}
			return fSymbolName.compareTo(((Symbol) expr).fSymbolName);
		}
		return ISymbol.super.compareTo(expr);
	}

	/** {@inheritDoc} */
	@Override
	public boolean containsRules() {
		return fRulesData != null;
	}

	@Override
	public IExpr copy() {
		try {
			return (IExpr) clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	/** {@inheritDoc} */
	@Override
	public final void createRulesData(int[] sizes) {
		if (fRulesData == null) {
			fRulesData = new RulesData(EvalEngine.get().getContext(), sizes);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IAST> definition() {
		ArrayList<IAST> result = new ArrayList<IAST>();
		if (fRulesData != null) {
			result.addAll(fRulesData.definition());
		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public String definitionToString() throws IOException {
		StringWriter buf = new StringWriter();

		OutputFormFactory off = OutputFormFactory.get(EvalEngine.get().isRelaxedSyntax());
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
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Symbol) {
			if (fHashValue != ((Symbol) obj).fHashValue) {
				return false;
			}
			return fSymbolName.equals(((Symbol) obj).fSymbolName);
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final Complex evalComplex() {
		INumber number = evalNumber();
		if (number != null) {
			return number.complexNumValue().complexValue();
		}
		throw new WrongArgumentType(this, "Conversion into a complex numeric value is not possible!");
	}

	/** {@inheritDoc} */
	@Override
	public final double evalDouble() {
		ISignedNumber signedNumber = evalSignedNumber();
		if (signedNumber != null) {
			return signedNumber.doubleValue();
		}
		throw new WrongArgumentType(this, "Conversion into a double numeric value is not possible!");
	}

	/** {@inheritDoc} */
	@Override
	public final IExpr evalDownRule(final IEvaluationEngine ee, final IExpr expression) {
		if (fRulesData == null) {
			return F.NIL;
		}
		return fRulesData.evalDownRule(expression);
	}

	/** {@inheritDoc} */
	@Override
	public final INumber evalNumber() {
		if (isNumericFunction()) {
			IExpr result = F.evaln(this);
			if (result.isNumber()) {
				return (INumber) result;
			}
		} else if (hasLocalVariableStack()) {
			IExpr temp = get();
			if (temp != null && temp.isNumericFunction()) {
				IExpr result = F.evaln(this);
				if (result.isNumber()) {
					return (INumber) result;
				}
			}
		} else {
			IExpr temp = evalDownRule(EvalEngine.get(), this);
			if (temp.isPresent() && temp.isNumericFunction()) {
				IExpr result = F.evaln(this);
				if (result.isNumber()) {
					return (INumber) result;
				}
			}
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public final ISignedNumber evalSignedNumber() {
		if (isNumericFunction()) {
			IExpr result = F.evaln(this);
			if (result.isSignedNumber()) {
				return (ISignedNumber) result;
			}
		} else if (hasLocalVariableStack()) {
			IExpr temp = get();
			if (temp != null && temp.isNumericFunction()) {
				IExpr result = F.evaln(this);
				if (result.isSignedNumber()) {
					return (ISignedNumber) result;
				}
			}
		} else {
			IExpr temp = evalDownRule(EvalEngine.get(), this);
			if (temp.isPresent() && temp.isNumericFunction()) {
				IExpr result = F.evaln(this);
				if (result.isSignedNumber()) {
					return (ISignedNumber) result;
				}
			}
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(EvalEngine engine) {
		if (hasLocalVariableStack()) {
			return IExpr.ofNullable(get());
		}
		IExpr result;
		if ((result = evalDownRule(engine, this)).isPresent()) {
			return result;
		}
		return F.NIL;
	}

	@Override
	public IExpr evaluateHead(IAST ast, EvalEngine engine) {
		IExpr result = evaluate(engine);
		if (result.isPresent()) {
			// set the new evaluated header !
			return ast.apply(result);
		}
		return F.NIL;
	}

	/** {@inheritDoc} */
	@Override
	public final IExpr evalUpRule(final IEvaluationEngine ee, final IExpr expression) {
		if (fRulesData == null) {
			return F.NIL;
		}
		return fRulesData.evalUpRule(expression);
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

	/** {@inheritDoc} */
	@Override
	public final IExpr get() {
		final List<IExpr> localVariableStack = EvalEngine.localStack(this);
		if (localVariableStack == null) {
			return null;
		}
		return localVariableStack.get(localVariableStack.size() - 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IExpr getAssignedValue() {
		if (hasLocalVariableStack()) {
			return get();
		} else {
			if (fRulesData != null) {
				PatternMatcherEquals pme = fRulesData.getEqualDownRules().get(this);
				if (pme != null) {
					return pme.getRHS();
				}
			}
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public final int getAttributes() {
		return fAttributes;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr getDefaultValue() {
		// special case for a general default value
		return fRulesData != null ? fRulesData.getDefaultValue(RulesData.DEFAULT_VALUE_INDEX) : null;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr getDefaultValue(int pos) {
		// default value at this position
		return fRulesData != null ? fRulesData.getDefaultValue(pos) : null;
	}

	/**
	 * Get the rules for initializing the pattern matching rules of this symbol.
	 * 
	 * @return <code>null</code> if no rule is defined
	 */
	@Override
	public RulesData getRulesData() {
		return fRulesData;
	}

	/** {@inheritDoc} */
	@Override
	public final String getSymbolName() {
		return fSymbolName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasAssignedSymbolValue() {
		if (hasLocalVariableStack()) {
			return get() != null;
		} else {
			if (fRulesData != null) {
				PatternMatcherEquals pme = fRulesData.getEqualDownRules().get(this);
				if (pme != null) {
					return pme.getRHS() != null;
				}
			}
		}
		return false;
	}

	@Override
	public boolean hasFlatAttribute() {
		return (fAttributes & FLAT) == FLAT;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return fHashValue;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean hasLocalVariableStack() {
		final List<IExpr> localVariableStack = EvalEngine.localStack(this);
		return (localVariableStack != null) && !(localVariableStack.isEmpty());
	}

	@Override
	public boolean hasOneIdentityAttribute() {
		return (fAttributes & ONEIDENTITY) == ONEIDENTITY;
	}

	@Override
	public boolean hasOrderlessAttribute() {
		return (fAttributes & ORDERLESS) == ORDERLESS;
	}

	@Override
	public boolean hasOrderlessFlatAttribute() {
		return (fAttributes & FLATORDERLESS) == FLATORDERLESS;
	}

	/** {@inheritDoc} */
	@Override
	public ISymbol head() {
		return F.SymbolHead;
	}

	/** {@inheritDoc} */
	@Override
	public int hierarchy() {
		return SYMBOLID;
	}

	/** {@inheritDoc} */
	@Override
	public String internalFormString(boolean symbolsAsFactoryMethod, int depth) {
		return internalJavaString(symbolsAsFactoryMethod, depth, false);
	}

	/** {@inheritDoc} */
	@Override
	public String internalJavaString(boolean symbolsAsFactoryMethod, int depth, boolean useOperators) {
		if (symbolsAsFactoryMethod) {
			return internalJavaStringAsFacoryMethod();
		}
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			String name;
			if (fSymbolName.length() == 1) {
				name = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(fSymbolName);
			} else {
				name = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(fSymbolName.toLowerCase(Locale.ENGLISH));
			}
			if (name != null) {
				return name;
			}
		}
		return fSymbolName;
	}

	/**
	 * Used to generate special Symja Java code
	 * 
	 * @return
	 */
	private String internalJavaStringAsFacoryMethod() {
		if (fSymbolName.length() == 1) {
			char ch = fSymbolName.charAt(0);
			if ('a' <= ch && ch <= 'z') {
				return fSymbolName;
			}
			if ('A' <= ch && ch <= 'G' && ch != 'D' && ch != 'E') {
				return fSymbolName + "Symbol";
			}
		}
		if (Config.RUBI_CONVERT_SYMBOLS) {
			if (fSymbolName.length() == 2 && '§' == fSymbolName.charAt(0)
					&& Character.isLowerCase(fSymbolName.charAt(1))) {
				char ch = fSymbolName.charAt(1);
				if ('a' <= ch && ch <= 'z') {
					return "p" + ch;
				}
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

	/** {@inheritDoc} */
	@Override
	public String internalScalaString(boolean symbolsAsFactoryMethod, int depth) {
		return internalJavaString(symbolsAsFactoryMethod, depth, true);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isAtom() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isConstant() {
		return (fAttributes & CONSTANT) == CONSTANT;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNegative() {
		if (isNumericFunction()) {
			IExpr temp = F.evaln(this);
			if (temp.isSignedNumber() && temp.isNegative()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isNumericFunction() {
		if (isConstant()) {
			return true;
		} else if (hasLocalVariableStack()) {
			IExpr temp = get();
			if (temp != null && temp.isNumericFunction()) {
				return true;
			}
		} else {
			IExpr temp = evalDownRule(EvalEngine.get(), this);
			if (temp.isPresent() && temp.isNumericFunction()) {
				return true;
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPolynomial(IAST variables) {
		if (variables.isAST0()) {
			return true;
		}
		for (int i = 1; i < variables.size(); i++) {
			if (this.equals(variables.get(i))) {
				return true;
			}
		}
		if (isConstant()) {
			return true;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPolynomial(ISymbol variable) {
		if (variable == null) {
			return true;
		}
		return this.equals(variable);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPolynomialOfMaxDegree(ISymbol variable, long maxDegree) {
		if (maxDegree == 0L) {
			if (this.equals(variable)) {
				return false;
			}
		}
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPositive() {
		if (isNumericFunction()) {
			IExpr temp = F.evaln(this);
			if (temp.isSignedNumber() && temp.isPositive()) {
				return true;
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isString(final String str) {
		return fSymbolName.equals(str);
	}

	@Override
	public final boolean isSymbolName(String name) {
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			if (fSymbolName.length() == 1) {
				return fSymbolName.equals(name);
			}
			return fSymbolName.equalsIgnoreCase(name);
		}
		return fSymbolName.equals(name);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isValue() {
		return evaluate(EvalEngine.get()).isPresent();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isVariable() {
		return (fAttributes & CONSTANT) != CONSTANT;
	}

	/** {@inheritDoc} */
	@Override
	public final void popLocalVariable() {
		final List<IExpr> localVariableStack = EvalEngine.localStack(this);
		localVariableStack.remove(localVariableStack.size() - 1);
	}

	/** {@inheritDoc} */
	@Override
	public final void pushLocalVariable() {
		pushLocalVariable(null);
	}

	/** {@inheritDoc} */
	@Override
	public final void pushLocalVariable(final IExpr expression) {
		EvalEngine.localStackCreate(this).add(expression);
	}

	/** {@inheritDoc} */
	@Override
	public final IPatternMatcher putDownRule(final ISymbol.RuleType symbol, final boolean equalRule,
			final IExpr leftHandSide, final IExpr rightHandSide, boolean packageMode) {
		return putDownRule(symbol, equalRule, leftHandSide, rightHandSide, PatternMap.DEFAULT_RULE_PRIORITY,
				packageMode);
	}

	/** {@inheritDoc} */
	@Override
	public final IPatternMatcher putDownRule(final ISymbol.RuleType setSymbol, final boolean equalRule,
			final IExpr leftHandSide, final IExpr rightHandSide, final int priority, boolean packageMode) {
		EvalEngine evalEngine = EvalEngine.get();
		if (!packageMode) {
			if (Config.SERVER_MODE && (fSymbolName.charAt(0) != '$')) {
				throw new RuleCreationError(leftHandSide);
			}

			EvalEngine.get().addModifiedVariable(this);
		}
		if (fRulesData == null) {
			fRulesData = new RulesData(evalEngine.getContext());
		}
		return fRulesData.putDownRule(setSymbol, equalRule, leftHandSide, rightHandSide);
	}

	/** {@inheritDoc} */
	@Override
	public final PatternMatcher putDownRule(final PatternMatcherAndInvoker pmEvaluator) {
		if (fRulesData == null) {
			fRulesData = new RulesData(EvalEngine.get().getContext());
		}
		return fRulesData.putDownRule(pmEvaluator);
	}

	/** {@inheritDoc} */
	@Override
	public final IPatternMatcher putUpRule(final ISymbol.RuleType symbol, boolean equalRule, IAST leftHandSide,
			IExpr rightHandSide) {
		return putUpRule(symbol, equalRule, leftHandSide, rightHandSide, PatternMap.DEFAULT_RULE_PRIORITY);
	}

	/** {@inheritDoc} */
	@Override
	public final IPatternMatcher putUpRule(final ISymbol.RuleType setSymbol, final boolean equalRule,
			final IAST leftHandSide, final IExpr rightHandSide, final int priority) {
		EvalEngine engine = EvalEngine.get();
		if (!engine.isPackageMode()) {
			if (Config.SERVER_MODE && (fSymbolName.charAt(0) != '$')) {
				throw new RuleCreationError(leftHandSide);
			}

			engine.addModifiedVariable(this);
		}
		if (fRulesData == null) {
			fRulesData = new RulesData(engine.getContext());
		}
		return fRulesData.putUpRule(setSymbol, equalRule, leftHandSide, rightHandSide);
	}

	private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
		fSymbolName = stream.readUTF();
		fAttributes = stream.read();
	}

	public Object readResolve() throws ObjectStreamException {
		Symbol sym = (Symbol) F.$s(fSymbolName);
		sym.fAttributes = fAttributes;
		return sym;
	}

	/** {@inheritDoc} */
	@Override
	public void readRules(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
		fSymbolName = stream.readUTF();
		fAttributes = stream.read();
		boolean hasDownRulesData = stream.readBoolean();
		if (hasDownRulesData) {
			fRulesData = new RulesData(EvalEngine.get().getContext());
			fRulesData = (RulesData) stream.readObject();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IExpr[] reassignSymbolValue(Function<IExpr, IExpr> function, ISymbol functionSymbol) {
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
			if (fRulesData != null) {
				PatternMatcherEquals pme = fRulesData.getEqualDownRules().get(this);
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
		throw new WrongArgumentType(this, functionSymbol.toString() + " - Symbol: " + toString()
				+ " has no value! Reassignment with a new value is not possible");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IExpr[] reassignSymbolValue( IAST ast, ISymbol functionSymbol, EvalEngine engine) {
		IExpr[] result = new IExpr[2];
		IExpr symbolValue;
		if (hasLocalVariableStack()) {
			symbolValue = get();
			result[0] = symbolValue;
			// IExpr calculatedResult = function.apply(symbolValue);
			ast.set(1, symbolValue);
			IExpr calculatedResult = engine.evaluate(ast);//F.binaryAST2(this, symbolValue, value));
			if (calculatedResult != null) {
				set(calculatedResult);
				result[1] = calculatedResult;
				return result;
			}

		} else {
			if (fRulesData != null) {
				PatternMatcherEquals pme = fRulesData.getEqualDownRules().get(this);
				if (pme != null) {
					symbolValue = pme.getRHS();
					if (symbolValue != null) {
						result[0] = symbolValue;
						// IExpr calculatedResult = function.apply(symbolValue);
						ast.set(1, symbolValue);
						IExpr calculatedResult = engine.evaluate(ast);
						if (calculatedResult != null) {
							pme.setRHS(calculatedResult);
							result[1] = calculatedResult;
							return result;
						}
					}
				}
			}
		}
		throw new WrongArgumentType(this, functionSymbol.toString() + " - Symbol: " + toString()
				+ " has no value! Reassignment with a new value is not possible");
	}

	@Override
	public final void removeRule(final ISymbol.RuleType setSymbol, final boolean equalRule, final IExpr leftHandSide,
			boolean packageMode) {
		if (!packageMode) {
			if (Config.SERVER_MODE && (fSymbolName.charAt(0) != '$')) {
				throw new RuleCreationError(leftHandSide);
			}

			EvalEngine.get().addModifiedVariable(this);
		}
		if (fRulesData != null) {
			fRulesData.removeRule(setSymbol, equalRule, leftHandSide);
		}
	}

	/** {@inheritDoc} */
	@Override
	public final void set(final IExpr value) {
		final List<IExpr> localVariableStack = EvalEngine.localStack(this);
		localVariableStack.set(localVariableStack.size() - 1, value);
	}

	/** {@inheritDoc} */
	@Override
	public final void setAttributes(final int attributes) {
		fAttributes = attributes;
		if (fSymbolName.charAt(0) == '$' && Config.SERVER_MODE) {
			EvalEngine engine = EvalEngine.get();
			engine.addModifiedVariable(this);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void setDefaultValue(IExpr expr) {
		// special case for a general default value
		setDefaultValue(RulesData.DEFAULT_VALUE_INDEX, expr);
	}

	/** {@inheritDoc} */
	@Override
	public void setDefaultValue(int pos, IExpr expr) {
		// default value at this position
		if (fRulesData == null) {
			fRulesData = new RulesData(EvalEngine.get().getContext());
		}
		fRulesData.putfDefaultValues(pos, expr);
	}

	@Override
	public String toString() {
		try {
			StringBuilder sb = new StringBuilder();
			OutputFormFactory.get(EvalEngine.get().isRelaxedSyntax()).convertSymbol(sb, this);
			return sb.toString();
		} catch (Exception e1) {
			return fSymbolName;
		}
	}

	/** {@inheritDoc} */
	@Override
	public IExpr variables2Slots(final Map<IExpr, IExpr> map, final Collection<IExpr> variableCollector) {
		final UnaryVariable2Slot uv2s = new UnaryVariable2Slot(map, variableCollector);
		return uv2s.apply(this);
	}

	private void writeObject(java.io.ObjectOutputStream stream) throws java.io.IOException {
		stream.writeUTF(fSymbolName);
		stream.write(fAttributes);
	}

	private Object writeReplace() throws ObjectStreamException {
		return optional(F.GLOBAL_IDS_MAP.get(this));
	}

	/** {@inheritDoc} */
	@Override
	public boolean writeRules(java.io.ObjectOutputStream stream) throws java.io.IOException {
		stream.writeUTF(fSymbolName);
		stream.write(fAttributes);
		// if (!containsRules()) {
		// return false;
		// }
		if (fRulesData == null) {
			stream.writeBoolean(false);
		} else {
			stream.writeBoolean(true);
			stream.writeObject(fRulesData);
		}
		return true;
	}

}
