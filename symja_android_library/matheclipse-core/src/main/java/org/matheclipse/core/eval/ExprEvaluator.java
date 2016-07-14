package org.matheclipse.core.eval;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

/**
 * Evaluate math expressions to <code>IExpr</code> results.
 * 
 */
public class ExprEvaluator {
	static {
		F.initSymbols(null, null, true);
	}
	private Map<ISymbol, IExpr> fVariableMap;
	private final List<ISymbol> fVariables;

	private final EvalEngine engine;

	private IExpr fExpr;

	/**
	 * Constructor for an <code>IExpr</code> object evaluator. By default no
	 * output history for the <code>Out()</code> function is stored in the
	 * evaluation engine. <code>$ans</code> won't get evaluate to the last
	 * result.
	 * 
	 */
	public ExprEvaluator() {
		this(true, 0);
	}

	/**
	 * Constructor for an <code>IExpr</code> object evaluator. By default no
	 * output history for the <code>Out()</code> function is stored in the
	 * evaluation engine. <code>$ans</code> won't get evaluate to the last
	 * result.
	 * 
	 * @param outListDisabled
	 *            if <code>false</code> create a
	 *            <code>LastCalculationsHistory(historyCapacity)</code>,
	 *            otherwise no history of the last calculations will be saved
	 *            and the <code>Out()</code> function (or <code>$ans</code>
	 *            variable or the <code>%</code> operator) will be unevaluated.
	 * @param historyCapacity
	 *            the number of last entries of the calculations which should be
	 *            stored.
	 */
	public ExprEvaluator(boolean outListDisabled, int historyCapacity) {
		this(new EvalEngine(true), outListDisabled, historyCapacity);
	}

	/**
	 * Constructor for an <code>IExpr</code> object evaluator. By default no
	 * output history for the <code>Out()</code> function is stored in the
	 * evaluation engine. <code>$ans</code> won't get evaluate to the last
	 * result.
	 * 
	 * @parm engine
	 * @param outListDisabled
	 *            if <code>false</code> create a
	 *            <code>LastCalculationsHistory(historyCapacity)</code>,
	 *            otherwise no history of the last calculations will be saved
	 *            and the <code>Out()</code> function (or <code>$ans</code>
	 *            variable or the <code>%</code> operator) will be unevaluated.
	 * @param historyCapacity
	 *            the number of last entries of the calculations which should be
	 *            stored.
	 */
	public ExprEvaluator(EvalEngine engine, boolean outListDisabled, int historyCapacity) {
		this.fVariableMap = new IdentityHashMap<>();
		this.fVariables = new ArrayList<>();
		this.engine = engine;
		if (!outListDisabled) {
			engine.setOutListDisabled(outListDisabled, historyCapacity);
		}
	}

	/**
	 * Clear all <b>local variables</b> defined with the
	 * <code>defineVariable()</code> method for this evaluator. <b>Note:</b>
	 * global variables assigned in scripting mode can be cleared with the
	 * <code>Clear(variable)</code> function.
	 * 
	 * @see #defineVariable(ISymbol, IExpr)
	 */
	public void clearVariables() {
		fVariableMap.clear();
		// pop all local variables from local variable stack
		for (int i = 0; i < fVariables.size(); i++) {
			fVariables.get(i).popLocalVariable();
		}
	}

	/**
	 * Define a given variable on the <b>local variable stack</b> without
	 * assigning a value.
	 * 
	 * @param variable
	 * @param value
	 */
	public ISymbol defineVariable(ISymbol variable) {
		return defineVariable(variable, null);
	}

	/**
	 * Define a double value for a given variable name on the <b>local variable
	 * stack</b> .
	 * 
	 * @param variable
	 * @param value
	 */
	public ISymbol defineVariable(ISymbol variable, double value) {
		return defineVariable(variable, F.num(value));
	}

	/**
	 * Define a value for a given variable name on the <b>local variable
	 * stack</b>. The value is evaluated before it's assigned to the local
	 * variable.
	 * 
	 * @param variable
	 * @param value
	 */
	public ISymbol defineVariable(ISymbol variable, IExpr value) {
		variable.pushLocalVariable();
		if (value != null) {
			// this evaluation step may throw an exception
			IExpr temp = engine.evaluate(value);
			variable.set(temp);
		}
		fVariables.add(variable);
		fVariableMap.put(variable, value);
		return variable;
	}

	/**
	 * Define a given variable name on the <b>local variable stack</b> without
	 * assigning a value.
	 * 
	 * @param variableName
	 * @param value
	 */
	public ISymbol defineVariable(String variableName) {
		return defineVariable(F.$s(variableName), null);
	}

	/**
	 * Define a boolean value for a given variable name on the< b>local variable
	 * stack</b>
	 * 
	 * @param variableName
	 * @param value
	 */
	public void defineVariable(String variableName, boolean value) {
		defineVariable(F.$s(variableName), value ? F.True : F.False);
	}

	/**
	 * Define a double value for a given variable name on the< b>local variable
	 * stack</b>
	 * 
	 * @param variableName
	 * @param value
	 */
	public ISymbol defineVariable(String variableName, double value) {
		return defineVariable(F.$s(variableName), F.num(value));
	}

	/**
	 * Define a value for a given variable name on the< b>local variable
	 * stack</b>
	 * 
	 * @param variableName
	 * @param value
	 */
	public ISymbol defineVariable(String variableName, IExpr value) {
		return defineVariable(F.$s(variableName), value);
	}

	/**
	 * Reevaluate the last <code>expression</code> (possibly after a new
	 * variable assignment).
	 * 
	 * @return
	 * @throws SyntaxError
	 */
	public IExpr evaluate() {
		if (fExpr == null) {
			throw new SyntaxError(0, 0, 0, " ", "No parser input defined", 1);
		}
		return evaluate(fExpr);
	}

	/**
	 * Evaluate an expression for the given &quot;local variables list&quot;. If
	 * evaluation is not possible return the input object.
	 * 
	 * @param expr
	 *            the expression which should be evaluated
	 * @return the evaluated object
	 */
	public IExpr evaluate(final IExpr expr) {
		fExpr = expr;
		engine.reset();
		IExpr temp = engine.evaluate(expr);
		if (!engine.isOutListDisabled()) {
			engine.addOut(temp);
		}
		return temp;
	}

	/**
	 * Parse the given <code>expression String</code> and evaluate it to an
	 * IExpr value
	 * 
	 * @param expression
	 * @return
	 * @throws SyntaxError
	 */
	public IExpr evaluate(final String inputExpression) {
		if (inputExpression != null) {
			engine.reset();
			fExpr = engine.parse(inputExpression);
			if (fExpr != null) {
				return evaluate(fExpr);
			}
		}
		return null;
	}

	/**
	 * Parse the given <code>expression String</code> and evaluate it to a
	 * double value
	 * 
	 * @param expression
	 * @return
	 * @throws SyntaxError
	 */
	public double evaluateDoube(final String inputExpression) {
		if (inputExpression != null) {
			engine.reset();
			fExpr = engine.parse(inputExpression);
			if (fExpr != null) {
				IExpr temp = evaluate(F.N(fExpr));
				if (temp.isSignedNumber()) {
					return ((ISignedNumber) temp).doubleValue();
				}
			}
		}
		return Double.NaN;
	}

	public EvalEngine getEvalEngine() {
		return engine;
	}

	/**
	 * Returns the expression value to which the specified variableName is
	 * mapped, or {@code null} if this map contains no mapping for the
	 * variableName.
	 * 
	 * @param variableName
	 * @return
	 */
	public IExpr getVariable(String variableName) {
		return fVariableMap.get(F.$s(variableName));
	}

	/**
	 * Converts the inputExpression string into a Java Symja expression string.
	 * 
	 * @param inputExpression
	 * @param out
	 */
	public String toJavaForm(final String inputExpression) throws MathException {
		IExpr parsedExpression;
		if (inputExpression != null) {
			ExprParser parser = new ExprParser(EvalEngine.get());
			parsedExpression = parser.parse(inputExpression);
			return parsedExpression.internalFormString(false, 0);
		}
		return "";
	}

	/**
	 * Converts the inputExpression string into a Scala expression and writes
	 * the result to the given <code>Writer</code>string.
	 * 
	 * @param inputExpression
	 * @param out
	 */
	public String toScalaForm(final String inputExpression) throws MathException {
		IExpr parsedExpression;
		if (inputExpression != null) {
			ExprParser parser = new ExprParser(EvalEngine.get());
			parsedExpression = parser.parse(inputExpression);
			return parsedExpression.internalScalaString(false, 0);
		}
		return "";
	}
}
