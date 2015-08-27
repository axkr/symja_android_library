package org.matheclipse.core.eval;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.math.MathException;

/**
 * Evaluate math expressions to <code>IExpr</code> results.
 * 
 */
public class ExprEvaluator {
	private Map<ISymbol, IExpr> fVariableMap;
	private final List<ISymbol> fVariables;
	private final EvalEngine engine;

	private IExpr fExpr;

	/**
	 * Constructor for an <code>IExpr</code> object evaluator. By default no output history for the <code>Out()</code> function is
	 * stored in the evaluation engine. <code>$ans</code> won't get evaluate to the last result.
	 * 
	 */
	public ExprEvaluator() {
		this(true, 0);
	}

	/**
	 * Constructor for an <code>IExpr</code> object evaluator. By default no output history for the <code>Out()</code> function is
	 * stored in the evaluation engine. <code>$ans</code> won't get evaluate to the last result.
	 * 
	 * @param outListDisabled
	 *            if <code>false</code> create a <code>LastCalculationsHistory(historyCapacity)</code>, otherwise no history of the
	 *            last calculations will be saved and the <code>Out()</code> function (or <code>$ans</code> variable or the
	 *            <code>%</code> operator) will be unevaluated.
	 * @param historyCapacity
	 *            the number of last entries of the calculations which should be stored.
	 */
	public ExprEvaluator(boolean outListDisabled, int historyCapacity) {
		this(new EvalEngine(true, outListDisabled), outListDisabled, historyCapacity);
	}

	/**
	 * Constructor for an <code>IExpr</code> object evaluator. By default no output history for the <code>Out()</code> function is
	 * stored in the evaluation engine. <code>$ans</code> won't get evaluate to the last result.
	 * 
	 * @parm engine
	 * @param outListDisabled
	 *            if <code>false</code> create a <code>LastCalculationsHistory(historyCapacity)</code>, otherwise no history of the
	 *            last calculations will be saved and the <code>Out()</code> function (or <code>$ans</code> variable or the
	 *            <code>%</code> operator) will be unevaluated.
	 * @param historyCapacity
	 *            the number of last entries of the calculations which should be stored.
	 */
	public ExprEvaluator(EvalEngine engine, boolean outListDisabled, int historyCapacity) {
		this.fVariableMap = new IdentityHashMap<ISymbol, IExpr>();
		this.fVariables = new ArrayList<ISymbol>();
		this.engine = engine;
		if (!outListDisabled) {
			engine.setOutListDisabled(outListDisabled, 100);
		}
	}

	/**
	 * Clear all defined variables for this evaluator.
	 */
	public void clearVariables() {
		fVariableMap.clear();
		// pop all local variables from local variable stack
		for (int i = 0; i < fVariables.size(); i++) {
			fVariables.get(i).popLocalVariable();
		}
	}

	/**
	 * Define a given variable on the local variable stack without assigning a value.
	 * 
	 * @param variable
	 * @param value
	 */
	public ISymbol defineVariable(ISymbol variable) {
		return defineVariable(variable, null);
	}

	/**
	 * Define a double value for a given variable name.
	 * 
	 * @param variable
	 * @param value
	 */
	public ISymbol defineVariable(ISymbol variable, double value) {
		return defineVariable(variable, F.num(value));
	}

	/**
	 * Define a value for a given variable name. The value is evauate before it's assigned to the local variable
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
	 * Define a given variable name on the local variable stack without assigning a value.
	 * 
	 * @param variableName
	 * @param value
	 */
	public ISymbol defineVariable(String variableName) {
		return defineVariable(F.$s(variableName), null);
	}

	/**
	 * Define a boolean value for a given variable name.
	 * 
	 * @param variableName
	 * @param value
	 */
	public void defineVariable(String variableName, boolean value) {
		defineVariable(F.$s(variableName), value ? F.True : F.False);
	}

	/**
	 * Define a double value for a given variable name.
	 * 
	 * @param variableName
	 * @param value
	 */
	public ISymbol defineVariable(String variableName, double value) {
		return defineVariable(F.$s(variableName), F.num(value));
	}

	/**
	 * Define a value for a given variable name.
	 * 
	 * @param variableName
	 * @param value
	 */
	public ISymbol defineVariable(String variableName, IExpr value) {
		return defineVariable(F.$s(variableName), value);
	}

	/**
	 * Reevaluate the last <code>expression</code> (possibly after a new variable assignment).
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
	 * Evaluate an expression for the given &quot;local variables list&quot;. If evaluation is not possible return the input object.
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
	 * Parse the given <code>expression String</code> and evaluate it to a double value
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
				IExpr temp = evaluate(fExpr);
				return temp;
			}
		}
		return null;
	}

	public EvalEngine getEvalEngine() {
		return engine;
	}

	/**
	 * Returns the expression value to which the specified variableName is mapped, or {@code null} if this map contains no mapping
	 * for the variableName.
	 * 
	 * @param variableName
	 * @return
	 */
	public IExpr getVariable(String variableName) {
		return fVariableMap.get(F.$s(variableName));
	}

	/**
	 * Converts the inputExpression string into a MathML expression and writes the result to the given <code>Writer</code>
	 * 
	 * @param inputExpression
	 * @param out
	 */
	public String toJavaForm(final String inputExpression) throws MathException {
		IExpr parsedExpression = null;
		ASTNode node;
		if (inputExpression != null) {
			node = engine.parseNode(inputExpression);
			parsedExpression = AST2Expr.CONST.convert(node, engine);
			return parsedExpression.internalFormString(false, 0);
		}
		return "";
	}
}
