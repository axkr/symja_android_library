package org.matheclipse.core.expression;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import org.hipparchus.complex.Complex;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.AttributeFunctions;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.RuleCreationError;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.generic.UnaryVariable2Slot;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
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
	protected transient Context fContext;

	private final static Collator US_COLLATOR = Collator.getInstance(Locale.US);

	/**
	 * The attribute values of the symbol represented by single bits.
	 */
	protected int fAttributes = NOATTRIBUTE;
	/**
	 * The pattern matching &quot;down value&quot; rules associated with this symbol.
	 */
	protected transient RulesData fRulesData;

	/**
	 * The name of this symbol. The characters may be all lower-cases if the system doesn't distinguish between lower-
	 * and upper-case function names.
	 */
	protected String fSymbolName;

	/**
	 * The hash value of this object computed in the constructor.
	 * 
	 */
	protected int fHashValue;

	// public static ISymbol valueOf(final String symbolName, final Context context) {
	// return new Symbol(symbolName, context);
	// }

	public Symbol(final String symbolName, final Context context) {
		super();
		fContext = context;
		fHashValue = symbolName.hashCode();
		fSymbolName = symbolName;
	}

	/** {@inheritDoc} */
	@Override
	public <T> T accept(IVisitor<T> visitor) {
		return visitor.visit(this);
	}

	/** {@inheritDoc} */
	@Override
	public boolean accept(IVisitorBoolean visitor) {
		return visitor.visit(this);
	}

	/** {@inheritDoc} */
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
	public final void addAttributes(final int attributes) {
		fAttributes |= attributes;
		if (isLocked()) {
			throw new RuleCreationError(this);
		}
		EvalEngine engine = EvalEngine.get();
		engine.addModifiedVariable(this);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isLocked(boolean packageMode) {
		return !packageMode && fContext == Context.SYSTEM; // fSymbolName.charAt(0) != '$';
	}

	/** {@inheritDoc} */
	@Override
	public boolean isLocked() {
		return !EvalEngine.get().isPackageMode() && fContext == Context.SYSTEM; // fSymbolName.charAt(0) != '$';
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
			if (isLocked()) {
				throw new RuleCreationError(this);
			}
		}
		if (fRulesData != null) {
			fRulesData = null;
		}
	}

	/** {@inheritDoc} */
	@Override
	public final void clearAttributes(final int attributes) {
		fAttributes &= (0xffff ^ attributes);
		if (isLocked()) {
			throw new RuleCreationError(this);
		}
		EvalEngine engine = EvalEngine.get();
		engine.addModifiedVariable(this);
	}

	/** {@inheritDoc} */
	@Override
	public final void clearAll(EvalEngine engine) {
		clear(engine);
		fAttributes = NOATTRIBUTE;
	}

	/**
	 * Compares this expression with the specified expression for order. Returns a negative integer, zero, or a positive
	 * integer as this expression is canonical less than, equal to, or greater than the specified expression.
	 */
	@Override
	public int compareTo(final IExpr expr) {
		if (expr instanceof ISymbol) {
			// O-2
			if (this == expr) {
				// Symbols are unique objects
				// Makes no sense to compare the symbol names, if they are equal
				return 0;
			}
			// sort lexicographically
			return US_COLLATOR.compare(fSymbolName, ((ISymbol) expr).getSymbolName());// fSymbolName);
		}
		if (expr.isNot() && expr.first().isSymbol()) {
			int cp = compareTo(expr.first());
			return cp != 0 ? cp : -1;
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
		IAST attributesList = AttributeFunctions.attributesList(this);
		OutputFormFactory off = OutputFormFactory.get(EvalEngine.get().isRelaxedSyntax());
		off.setIgnoreNewLine(true);
		List<IAST> list = definition();
		buf.append("Attributes(");
		buf.append(this.toString());
		buf.append(")=");
		buf.append(attributesList.toString());
		buf.append("\n");
		for (int i = 0; i < list.size(); i++) {
			off.convert(buf, list.get(i));
			if (i < list.size() - 1) {
				buf.append("\n");
				off.setColumnCounter(0);
			}
		}
		return buf.toString();
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof IBuiltInSymbol) {
			return false;
		}
		if (obj instanceof Symbol) {
			Symbol symbol = (Symbol) obj;
			if (fHashValue != symbol.fHashValue) {
				return false;
			}
			if (fSymbolName.equals(symbol.fSymbolName)) {
				// #172
				return fContext.equals(symbol.fContext);
			}
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
	public final IExpr evalDownRule(final EvalEngine engine, final IExpr expression) {
		if (fRulesData == null) {
			return F.NIL;
		}
		return fRulesData.evalDownRule(expression, engine);
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

	/** {@inheritDoc} */
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
	public final IExpr evalUpRule(final EvalEngine engine, final IExpr expression) {
		if (fRulesData == null) {
			return F.NIL;
		}
		return fRulesData.evalUpRule(expression, engine);
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
		final Deque<IExpr> localVariableStack = EvalEngine.get().localStack(this);
		if (localVariableStack == null) {
			return null;
		}
		return localVariableStack.peek();
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
	public final Context getContext() {
		return fContext;
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
		final Deque<IExpr> localVariableStack = EvalEngine.get().localStack(this);
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
		return F.Symbol;
	}

	/** {@inheritDoc} */
	@Override
	public int hierarchy() {
		return SYMBOLID;
	}

	/** {@inheritDoc} */
	@Override
	public String internalFormString(boolean symbolsAsFactoryMethod, int depth) {
		return internalJavaString(symbolsAsFactoryMethod, depth, false, false);
	}

	/** {@inheritDoc} */
	@Override
	public String internalJavaString(boolean symbolsAsFactoryMethod, int depth, boolean useOperators,
			boolean usePrefix) {
		String prefix = usePrefix ? "F." : "";
		if (symbolsAsFactoryMethod) {
			return prefix + internalJavaStringAsFactoryMethod();
		}
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			String name;
			if (fSymbolName.length() == 1) {
				name = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(fSymbolName);
			} else {
				name = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(fSymbolName.toLowerCase(Locale.ENGLISH));
			}
			if (name != null) {
				return prefix + name;
			}
		} else {
			String name = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(fSymbolName.toLowerCase(Locale.ENGLISH));
			if (name != null && name.equals(fSymbolName)) {
				return prefix + name;
			}
		}
		char ch = fSymbolName.charAt(0);
		if (fSymbolName.length() == 1 && ('a' <= ch && ch <= 'z')) {
			return prefix + fSymbolName;
		} else {
			return fSymbolName;
		}
	}

	/**
	 * Used to generate special Symja Java code
	 * 
	 * @return
	 */
	private String internalJavaStringAsFactoryMethod() {
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
			} else if (fSymbolName.equals("Int")) {
				return "Integrate";
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
		return internalJavaString(symbolsAsFactoryMethod, depth, true, false);
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
		if (isConstant()) {
			return true;
		}
		return variables.exists(x -> this.equals(x));
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPolynomial(IExpr variable) {
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
	public IExpr of(EvalEngine engine, IExpr... args) {
		IAST ast = F.ast(args, this);
		return engine.evaluate(ast);
	}

	/** {@inheritDoc} */
	@Override
	public IExpr of(IExpr... args) {
		return of(EvalEngine.get(), args);
	}

	/** {@inheritDoc} */
	@Override
	public boolean ofQ(EvalEngine engine, IExpr... args) {
		IAST ast = F.ast(args, this);
		return engine.evalTrue(ast);
	}

	/** {@inheritDoc} */
	@Override
	public final void popLocalVariable() {
		final Deque<IExpr> localVariableStack = EvalEngine.get().localStack(this);
		localVariableStack.pop();
	}

	/** {@inheritDoc} */
	@Override
	public final void pushLocalVariable() {
		pushLocalVariable(F.NIL);
	}

	/** {@inheritDoc} */
	@Override
	public final void pushLocalVariable(final IExpr expression) {
		EvalEngine.get().localStackCreate(this).push(expression);
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
			if (isLocked(packageMode)) {
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
			if (isLocked(false)) {
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
		fHashValue = fSymbolName.hashCode();
		fAttributes = stream.read();
		fContext = (Context) stream.readObject();
		if (fContext == null) {
			fContext = Context.SYSTEM;
		} else {
			boolean hasDownRulesData = stream.readBoolean();
			if (hasDownRulesData) {
				fRulesData = new RulesData(EvalEngine.get().getContext());
				fRulesData = (RulesData) stream.readObject();
			}
		}
	}

	// public Object readResolve() throws ObjectStreamException {
	// ISymbol sym = fContext.get(fSymbolName);
	// if (sym != null) {
	// return sym;
	// }
	// // probably user defined
	// Symbol symbol = new Symbol(fSymbolName, fContext);
	// fContext.put(fSymbolName, symbol);
	// symbol.fAttributes = fAttributes;
	// return symbol;
	// }

	/** {@inheritDoc} */
	@Override
	public void readRules(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
		fSymbolName = stream.readUTF();
		fHashValue = fSymbolName.hashCode();
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
	public IExpr[] reassignSymbolValue(Function<IExpr, IExpr> function, ISymbol functionSymbol, EvalEngine engine) {
		IExpr[] result = new IExpr[2];
		IExpr symbolValue;
		if (hasLocalVariableStack()) {
			symbolValue = get();
			result[0] = symbolValue;
			IExpr calculatedResult = function.apply(symbolValue);
			if (calculatedResult.isPresent()) {
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
						if (calculatedResult.isPresent()) {
							pme.setRHS(calculatedResult);
							result[1] = calculatedResult;
							return result;
						}
					}
				}
			}
		}
		engine.printMessage(toString() + " is not a variable with a value, so its value cannot be changed.");
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IExpr[] reassignSymbolValue(IASTMutable ast, ISymbol functionSymbol, EvalEngine engine) {
		IExpr[] result = new IExpr[2];
		IExpr symbolValue;
		if (hasLocalVariableStack()) {
			symbolValue = get();
			result[0] = symbolValue;
			// IExpr calculatedResult = function.apply(symbolValue);
			ast.set(1, symbolValue);
			IExpr calculatedResult = engine.evaluate(ast);// F.binaryAST2(this, symbolValue, value));
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

	/** {@inheritDoc} */
	@Override
	public final boolean removeRule(final ISymbol.RuleType setSymbol, final boolean equalRule, final IExpr leftHandSide,
			boolean packageMode) {
		if (!packageMode) {
			if (isLocked(packageMode)) {
				throw new RuleCreationError(leftHandSide);
			}

			EvalEngine.get().addModifiedVariable(this);
		}
		if (fRulesData != null) {
			return fRulesData.removeRule(setSymbol, equalRule, leftHandSide);

		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final void set(final IExpr value) {
		final Deque<IExpr> localVariableStack = EvalEngine.get().localStack(this);
		localVariableStack.remove();
		localVariableStack.push(value);
	}

	/** {@inheritDoc} */
	@Override
	public final void setAttributes(final int attributes) {
		fAttributes = attributes;
		if (isLocked()) {
			throw new RuleCreationError(this);
		}
		EvalEngine engine = EvalEngine.get();
		engine.addModifiedVariable(this);
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
		if (fContext.equals(Context.SYSTEM)) {
			stream.writeObject(null);
		} else {
			stream.writeObject(fContext);
			if (fRulesData == null) {
				stream.writeBoolean(false);
			} else {
				stream.writeBoolean(true);
				stream.writeObject(fRulesData);
			}
		}
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
