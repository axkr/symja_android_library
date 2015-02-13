package org.matheclipse.core.expression;

import java.lang.reflect.Method;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Implements Symbols for function, constant and variable names
 * 
 */
public class MethodSymbol extends Symbol {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4637484703674565623L;

	/**
	 * 
	 */
	private static final int DEFAULT_VALUE_INDEX = Integer.MIN_VALUE;

	private Method fMethod;

	public MethodSymbol(final String symbolName, final String packagename, final String classname, final String methodname) {
		super(symbolName);
		try {
			Class<?> c = Class.forName(packagename + '.' + classname);
			fMethod = c.getDeclaredMethod(methodname, new Class[] { IAST.class });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public MethodSymbol(final String symbolName, final String classname, final String methodname) {
		this(symbolName, "org.matheclipse.core.reflection.system", classname, methodname);

	}

	/**
	 * Invoke the static method represented by this object
	 * 
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public IExpr invoke(IAST args) {
		try {
			return (IExpr) fMethod.invoke(null, new Object[] { args });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(this.getClass().equals(obj.getClass()))) {
			return false;
		}
		if (fMethod.equals(((MethodSymbol) obj).fMethod)) {
			if (Config.DEBUG) {
				System.err.println(fMethod.toString() + " EQUALS " + ((MethodSymbol) obj).fMethod.toString());
			}
			return true;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return fMethod.hashCode();
	}

	/** {@inheritDoc} */
	@Override
	public int hierarchy() {
		return METHODSYMBOLID;
	}

	/**
	 * Compares this expression with the specified expression for order. Returns a
	 * negative integer, zero, or a positive integer as this expression is
	 * canonical less than, equal to, or greater than the specified expression.
	 */
	@Override
	public int compareTo(final IExpr obj) {
		if (obj instanceof MethodSymbol) {
			return fMethod.toString().compareTo(((MethodSymbol) obj).fMethod.toString());
		}
		return super.compareTo(obj);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isTrue() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isFalse() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public ISymbol head() {
		return F.MethodHead;
	}

	@Override
	public String toString() {
		return fMethod.toString();
	}

}