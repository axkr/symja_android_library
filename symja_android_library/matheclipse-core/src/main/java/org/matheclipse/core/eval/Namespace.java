package org.matheclipse.core.eval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.expression.Symbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * 
 */
public class Namespace {
	ArrayList<Namespace> fNamespaces;
	ArrayList<String> fNamespacesString;
	Map<String, Namespace> fPackageNamespaceMap;

	public Namespace() {
		fNamespaces = new ArrayList<Namespace>();
		fNamespacesString = new ArrayList<String>();
		fPackageNamespaceMap = new HashMap<String, Namespace>();
	}

	/**
	 * Adds a Java packagename to the namespace
	 * 
	 * @param arg
	 *          the name of a Java package which should be add to this namespace
	 * @return
	 */
	public boolean add(final String arg) {
		fPackageNamespaceMap.put(arg, null);
		return fNamespacesString.add(arg);
	}

	/**
	 * Adds a Java packagename to the namespace
	 * 
	 * @param arg
	 *          the name of a Java package which should be add to this namespace
	 * @return
	 */
	public boolean add(final Namespace arg) {
		fPackageNamespaceMap.put(arg.getClass().getPackage().getName(), arg);
		return fNamespaces.add(arg);
	}

	/**
	 * Search the IEvaluator class for a symbol in the given namespaces in
	 * descending order If an IEvaluator class exists, the symbol runs through the
	 * setUp() method of the IEvaluator and is set into the symbol.
	 * 
	 * @param symbol
	 *          the symbol which should be set-up
	 */
	@SuppressWarnings("unchecked")
	public void setEvaluator(final ISymbol symbol) {
		String functionName;
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			String symbolName = symbol.toString().toLowerCase();
			functionName = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(symbolName);
		} else {
			functionName = symbol.toString();
		}
		if (functionName != null) {
			for (Map.Entry<String, Namespace> namespaceEntry : fPackageNamespaceMap.entrySet()) {
				Class clazz;
				try {
					if (namespaceEntry.getValue() == null) {
						clazz = Class.forName(namespaceEntry.getKey() + "." + functionName);
					} else {
						clazz = Class.forName(namespaceEntry.getKey() + "." + functionName, true, namespaceEntry.getValue().getClass()
								.getClassLoader());
					}
				} catch (final ClassNotFoundException e) {
					// not a predefined function
					clazz = null;
				} catch (final NoClassDefFoundError e) {
					// wrong written functionnames (i.e. PLot, PROduct)
					// not a predefined function
					clazz = null;
				} catch (final Throwable se) {
					if (Config.SHOW_STACKTRACE) {
						se.printStackTrace();
					}
					clazz = null;
				}

				if (clazz == null) {
					continue;
				}

				try {
					IEvaluator module = (IEvaluator) clazz.newInstance();
					// a predefined function
					// use reflection to setUp this symbol
					symbol.setEvaluator(module);
					// module.setUp(symbol);
					return;
				} catch (final Throwable se) {
					if (Config.SHOW_STACKTRACE) {
						se.printStackTrace();
					}
					continue;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void setEquals(final Symbol symbol) {
		String namespace;
		for (int i = fNamespaces.size() - 1; i >= 0; i--) {
			namespace = fNamespaces.get(i) + symbol.toString();
			Class clazz;
			try {
				clazz = Class.forName(namespace);
			} catch (final ClassNotFoundException e) {
				// not a predefined function
				continue;
			}

			IEvaluator module;
			try {
				module = (IEvaluator) clazz.newInstance();
				// a predefined function
				// use reflection to setUp this symbol
				symbol.setEvaluator(module);
				// module.setUpXML(symbol);
				// module.setUp(symbol);
				return;
			} catch (final Throwable se) {
				if (Config.SHOW_STACKTRACE) {
					se.printStackTrace();
				}
				continue;
			}
		}
	}

	public static final char[] NO_CHAR = new char[0];
}