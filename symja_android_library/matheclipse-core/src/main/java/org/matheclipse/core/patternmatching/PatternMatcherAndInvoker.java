package org.matheclipse.core.patternmatching;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;

public class PatternMatcherAndInvoker extends PatternMatcher {
	/**
	* 
	*/
	private static final long serialVersionUID = -2448717771259975643L;

	private transient Method fMethod;
	private transient Type[] fTypes;
	private transient IFunctionEvaluator fInstance;

	@Override
	public Object clone() throws CloneNotSupportedException {
		PatternMatcherAndInvoker v = (PatternMatcherAndInvoker) super.clone();
		v.fMethod = fMethod;
		v.fTypes = fTypes;
		v.fInstance = fInstance;
		return v;
	}

	/**
	 * Void constructor for Externalizable.
	 */
	@SuppressWarnings("unused")
	private PatternMatcherAndInvoker() {
		super(null);
	}

	/**
	 * Create a pattern-matching rule which invokes the method name in the given
	 * instance, if leftHandSide is matching.
	 * 
	 * @param leftHandSide
	 *            could contain pattern expressions for "pattern-matching"
	 * @param instance
	 *            instance of an IFunctionEvaluator interface
	 * @param method
	 *            method to call
	 */
	public PatternMatcherAndInvoker(final IExpr leftHandSide, IFunctionEvaluator instance, final String methodName) {
		super(leftHandSide);
		this.fInstance = instance;
		initInvoker(instance, methodName);
	}

	/**
	 * Create a pattern-matching rule which invokes the method name in the given
	 * instance, if leftHandSide is matching.
	 * 
	 * @param leftHandSide
	 * @param instance
	 * @param methodName
	 */
	public PatternMatcherAndInvoker(final String leftHandSide, IFunctionEvaluator instance, final String methodName) {
		this.fInstance = instance;

		final ExprParser parser = new ExprParser(EvalEngine.get());
		IExpr lhs = parser.parse(leftHandSide);

		// Parser parser = new Parser();
		// ASTNode node = parser.parse(leftHandSide);
		// IExpr lhs = AST2Expr.CONST.convert(node);
		fLhsPatternExpr = lhs;
		init(fLhsPatternExpr);
		initInvoker(instance, methodName);
	}

	private void initInvoker(IFunctionEvaluator instance, final String methodName) {
		Class<? extends IFunctionEvaluator> c = instance.getClass();
		Method[] declaredMethods = c.getDeclaredMethods();
		List<Method> namedMethods = new ArrayList<Method>();
		for (Method method : declaredMethods) {
			if (method.getName().equals(methodName))
				namedMethods.add(method);
		}
		if (namedMethods.size() == 1) {
			this.fMethod = namedMethods.get(0);
			this.fTypes = fMethod.getGenericParameterTypes();
		} else {
			// throw an exception ?
		}
	}

	/** {@inheritDoc} */
	@Override
	public IExpr eval(final IExpr leftHandSide, EvalEngine engine) {

		if (isRuleWithoutPatterns() && fLhsPatternExpr.equals(leftHandSide)) {
			if (fTypes.length != 0) {
				return F.NIL;
			}
			IExpr result = F.NIL;
			try {
				result = (IExpr) fMethod.invoke(fInstance);
			} catch (IllegalArgumentException e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			} catch (IllegalAccessException e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			} catch (InvocationTargetException e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			}
			return result != null ? result : F.NIL;
		}
		PatternMap patternMap=getPatternMap();
		if (fTypes.length != patternMap.size()) {
			return F.NIL;
		}
		patternMap.initPattern();
		if (matchExpr(fLhsPatternExpr, leftHandSide, engine)) {

			List<IExpr> args = patternMap.getValuesAsList();
			try {
				if (args != null) {
					IExpr result = (IExpr) fMethod.invoke(fInstance, args.toArray());
					return result != null ? result : F.NIL;
				}
			} catch (IllegalArgumentException e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			} catch (IllegalAccessException e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			} catch (InvocationTargetException e) {
				if (Config.SHOW_STACKTRACE) {
					e.printStackTrace();
				}
			}
		}
		return F.NIL;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof PatternMatcherAndInvoker) {
			// don't compare fInstance, fMethod, fTypes here
			return super.equals(obj);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode() * 47;
	}
}