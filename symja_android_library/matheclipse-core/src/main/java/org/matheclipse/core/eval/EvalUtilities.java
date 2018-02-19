package org.matheclipse.core.eval;

import java.io.Writer;
import com.duy.lambda.Predicate;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

/**
 * Utility to evaluate math expressions.
 * 
 */
public class EvalUtilities extends MathMLUtilities {

	/**
	 * Constructor for an object which evaluates an expression. By default the
	 * internal <code>EvalEngine</code> didn't create a history list for the
	 * <code>Out[]</code> function.
	 * 
	 * @param evalEngine
	 * @param mathMTagPrefix
	 *            if set to <code>true</code> use &quot;m:&quot; as tag prefix
	 *            for the MathML output.
	 * @param relaxedSyntax
	 */
	public EvalUtilities() {
		this(new EvalEngine(), false, false);
	}

	/**
	 * Constructor for an object which evaluates an expression.
	 * 
	 * @param mathMTagPrefix
	 *            if set to <code>true</code> use &quot;m:&quot; as tag prefix
	 *            for the MathML output.
	 * @param relaxedSyntax
	 *            if set to <code>true</code> use &quot;(...)&quot; instead of
	 *            &quot;[...]&quot; for function arguments (i.e. sin(...)
	 *            instead of sin[...]).
	 */
	public EvalUtilities(final boolean mathMTagPrefix, final boolean relaxedSyntax) {
		this(new EvalEngine(relaxedSyntax), mathMTagPrefix, relaxedSyntax);
	}

	/**
	 * Constructor for an object which evaluates an expression.
	 * 
	 * @param evalEngine
	 * @param mathMTagPrefix
	 *            if set to <code>true</code> use &quot;m:&quot; as tag prefix
	 *            for the MathML output.
	 * @param relaxedSyntax
	 *            if set to <code>true</code> use &quot;(...)&quot; instead of
	 *            &quot;[...]&quot; for function arguments (i.e. sin(...)
	 *            instead of sin[...]).
	 */
	public EvalUtilities(final EvalEngine evalEngine, final boolean mathMTagPrefix, final boolean relaxedSyntax) {
		super(evalEngine, mathMTagPrefix, relaxedSyntax);
	}

	/**
	 * Evaluate the <code>inputExpression</code> and return the resulting
	 * expression.
	 * 
	 * @param inputExpression
	 *            the expression which should be evaluated.
	 * @return <code>F.NIL</code>, if the inputExpression is <code>null</code>
	 * 
	 */
	public IExpr evaluate(final String inputExpression) throws MathException {
		if (inputExpression != null) {
			startRequest();
			EvalEngine.set(fEvalEngine);;
			fEvalEngine.reset();
			IExpr parsedExpression = fEvalEngine.parse(inputExpression);
			if (parsedExpression != null) {
//				F.join();
				fEvalEngine.reset();
				IExpr temp = fEvalEngine.evaluate(parsedExpression);
				fEvalEngine.addOut(temp);
				return temp;
			}
		}
		return F.NIL;
	}

	/**
	 * Evaluate the <code>inputExpression</code> and return the resulting
	 * expression. <br/>
	 * The parser first tries (independently from the settings in the
	 * <code>evalEngine</code>) to parse the expression in the &quot;relaxed
	 * mode&quot; (i.e. &quot;common math expression syntax&quot; with
	 * parentheses for function arguments) and if that results in a
	 * <code>SyntaxError</code> exception it tries to parse in the
	 * &quot;stronger mode&quot; (i.e. with square brackets for function
	 * arguments). <br />
	 * <b>Note</B> that after the second parser step this method may also throw
	 * a <code>SyntaxError</code> exception.
	 * 
	 * @param inputExpression
	 *            the expression which should be evaluated.
	 * @param evalEngine
	 *            the evaluation engine which should be used
	 * @return <code>F.NIL</code>, if the inputExpression is <code>null</code>
	 * @throw org.matheclipse.parser.client.SyntaxError
	 * @throw org.matheclipse.parser.client.math.MathException
	 */
	public static IExpr eval(final String inputExpression, final EvalEngine evalEngine) throws MathException {
		if (inputExpression != null) {
			EvalEngine.set(evalEngine);
			boolean SIMPLE_SYNTAX = true;
			IExpr parsedExpression;
			try {
				ExprParser parser = new ExprParser(evalEngine, SIMPLE_SYNTAX);
				parsedExpression = parser.parse(inputExpression);
			} catch (SyntaxError se1) {
				try {
					SIMPLE_SYNTAX = false;
					ExprParser parser = new ExprParser(evalEngine, SIMPLE_SYNTAX);
					parsedExpression = parser.parse(inputExpression);
				} catch (SyntaxError se2) {
					throw se1;
				}
			}
			if (parsedExpression != null) {
//				F.join();
				evalEngine.reset();
				IExpr temp = evalEngine.evaluate(parsedExpression);
				evalEngine.addOut(temp);
				return temp;
			}
		}
		return F.NIL;
	};

	/**
	 * Evaluate the <code>parsedExpression</code> and return the resulting
	 * expression.
	 * 
	 * @param parsedExpression
	 *            the expression which should be evaluated.
	 * @return
	 */
	public IExpr evaluate(final IExpr parsedExpression) throws MathException {
		if (parsedExpression != null) {
//			F.join();
			startRequest();
			EvalEngine.set(fEvalEngine);
			fEvalEngine.reset();
			IExpr temp = fEvalEngine.evaluate(parsedExpression);
			fEvalEngine.addOut(temp);
			return temp;
		}
		return F.NIL;
	}

	/**
	 * Converts the inputExpression string into a MathML expression and writes
	 * the result to the given <code>Writer</code>
	 * 
	 * @param inputExpression
	 * @param out
	 */
	public String toJavaForm(final String inputExpression) throws MathException {
		if (inputExpression != null) {
			EvalEngine.set(fEvalEngine);
			fEvalEngine.reset();
			ExprParser parser = new ExprParser(fEvalEngine);
			IExpr parsedExpression = parser.parse(inputExpression);
			// node = fEvalEngine.parseNode(inputExpression);
			// parsedExpression = AST2Expr.CONST.convert(node, fEvalEngine);
			return parsedExpression.internalFormString(false, 0);
		}
		return "";
	}

	/**
	 * Evaluate the <code>inputExpression</code> and return the
	 * <code>Trace[inputExpression]</code> (i.e. all (sub-)expressions needed to
	 * calculate the result).
	 * 
	 * @param inputExpression
	 *            the expression which should be evaluated.
	 * @param matcher
	 *            a filter which determines the expressions which should be
	 *            traced, If the matcher is set to <code>null</code>, all
	 *            expressions are traced.
	 * @param list
	 *            an IAST object which will be cloned for containing the traced
	 *            expressions. Typically a <code>F.List()</code> will be used.
	 * @return <code>F.NIL</code> if <code>inputExpression == null</code>
	 */
	public IAST evalTrace(final String inputExpression, Predicate<IExpr> matcher, IAST list) throws MathException {

		if (inputExpression != null) {
			// try {
			startRequest();
			EvalEngine.set(fEvalEngine);
			IExpr parsedExpression = fEvalEngine.parse(inputExpression);
			if (parsedExpression != null) {
//				F.join();
				fEvalEngine.reset();
				IAST temp = fEvalEngine.evalTrace(parsedExpression, matcher, list);
				fEvalEngine.addOut(temp);
				return temp;
			}
		}
		return F.NIL;
	}

	/**
	 * Evaluate the <code>parsedExpression</code> and return the
	 * <code>Trace[parsedExpression]</code> (i.e. all (sub-)expressions needed
	 * to calculate the result).
	 * 
	 * @param parsedExpression
	 *            the expression which should be evaluated.
	 * @param matcher
	 *            a filter which determines the expressions which should be
	 *            traced, If the matcher is set to <code>null</code>, all
	 *            expressions are traced.
	 * @param list
	 *            an IAST object which will be cloned for containing the traced
	 *            expressions. Typically a <code>F.List()</code> will be used.
	 * @return
	 */
	public IAST evalTrace(final IExpr parsedExpression, Predicate<IExpr> matcher, IAST list) throws RuntimeException {
		if (parsedExpression != null) {
//			F.join();
			startRequest();
			EvalEngine.set(fEvalEngine);
			fEvalEngine.reset();
			IAST temp = fEvalEngine.evalTrace(parsedExpression, matcher, list);
			return temp;
		}
		return F.NIL;
	}

	/** {@inheritDoc} */
	@Override
	synchronized public void toMathML(final String inputExpression, final Writer out) {
		try {
			EvalEngine.set(fEvalEngine);
			fEvalEngine.reset();
			final IExpr result = evaluate(inputExpression);
			if (result.isPresent()) {
				toMathML(result, out);
			}
		} catch (final Throwable e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}
	}

}