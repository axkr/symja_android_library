package org.matheclipse.core.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.interfaces.ISymbol;

public class ContextPath {
	public final static String GLOBAL_CONTEXT_NAME = "Global`";

	public final Map<String, Context> fContextMap;
	List<Context> path = new ArrayList<Context>();

	private ContextPath(List<Context> path) {
		fContextMap = new HashMap<String, Context>(17);
		this.path.add(Context.SYSTEM);
		this.fContextMap.put(Context.SYSTEM.getContextName(), Context.SYSTEM);
		for (int i = 1; i < path.size(); i++) {
			// Start at index 1 because SYSTEM is already set!
			this.path.add(path.get(i).copy());
		}
	}

	public ContextPath() {
		this(GLOBAL_CONTEXT_NAME);
	}

	public ContextPath(String contextName) {
		fContextMap = new HashMap<String, Context>(17);
		path.add(Context.SYSTEM);
		fContextMap.put(Context.SYSTEM.getContextName(), Context.SYSTEM);
		path.add(getContext(contextName));
	}

	public ContextPath(Context context) {
		fContextMap = new HashMap<String, Context>(17);
		path.add(Context.SYSTEM);
		fContextMap.put(Context.SYSTEM.getContextName(), Context.SYSTEM);
		path.add(context);
	}

	public ContextPath copy() {
		return new ContextPath(path);
	}

	public Context getGlobalContext() {
		int size = path.size();
		int start = size - 1;
		for (int i = start; i >= 0; i--) {
			Context temp = path.get(i);
			if (temp.getContextName().equals(GLOBAL_CONTEXT_NAME)) {
				return temp;
			}
		}
		return null;
	}

	public boolean setGlobalContext(Context context) {
		int size = path.size();
		int start = size - 1;
		for (int i = start; i >= 0; i--) {
			Context temp = path.get(i);
			if (temp.getContextName().equals(GLOBAL_CONTEXT_NAME)) {
				path.set(i, context);
				return true;
			}
		}
		return false;
	}

	public Context getContext(String contextName) {
		Context context = fContextMap.get(contextName);
		if (context != null) {
			return context;
		}
		context = new Context(contextName);
		fContextMap.put(contextName, context);
		return context;
	}

	public boolean contains(Context context) {
		for (int i = path.size()-1; i >=0; i--) {
			if (path.get(i).equals(context)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean add(Context context) {
		return path.add(context);
	}

	public Context get(int index) {
		return path.get(index);
	}

	public ISymbol getSymbol(String symbolName, boolean relaxedSyntax) {
		String name = symbolName;
		if (relaxedSyntax) {
			if (symbolName.length() == 1) {
				name = symbolName;
			} else {
				name = symbolName.toLowerCase(Locale.ENGLISH);
			}
		}
		Context context;
		ISymbol symbol;
		for (int i = path.size() - 1; i >= 0; i--) {
			context = path.get(i);
			symbol = context.get(name);
			if (symbol != null) {
				return symbol;
			}
		}
		context = path.get(path.size() - 1);

		// EvalEngine engine = EvalEngine.get();
		// if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
		// if (SYMBOL_OBSERVER.createPredefinedSymbol(name)) {
		// // second try, because the symbol may now be added to
		// // fSymbolMap
		// ISymbol secondTry = PREDEFINED_SYMBOLS_MAP.get(name);
		// if (secondTry != null) {
		// return secondTry;
		// }
		// }
		// } else {
		// if (Character.isUpperCase(name.charAt(0))) {
		// if (SYMBOL_OBSERVER.createPredefinedSymbol(name)) {
		// // second try, because the symbol may now be added to
		// // fSymbolMap
		// ISymbol secondTry = PREDEFINED_SYMBOLS_MAP.get(name);
		// if (secondTry != null) {
		// return secondTry;
		// }
		// }
		// }
		// }
		symbol = new Symbol(name, context);
		context.put(name, symbol);
		// engine.putUserVariable(name, symbol);
		if (Config.SERVER_MODE) {
			if (name.charAt(0) == '$') {
				F.SYMBOL_OBSERVER.createUserSymbol(symbol);
			}
		}

		return symbol;
	}

	public ISymbol getSymbol(String symbolName, final Context context, boolean relaxedSyntax) {
		String name = symbolName;
		if (relaxedSyntax) {
			if (symbolName.length() == 1) {
				name = symbolName;
			} else {
				name = symbolName.toLowerCase(Locale.ENGLISH);
			}
		}
		ISymbol symbol = context.get(name);
		if (symbol != null) {
			return symbol;
		}

		symbol = new Symbol(name, context);
		context.put(name, symbol);
		// engine.putUserVariable(name, symbol);
		if (Config.SERVER_MODE) {
			if (name.charAt(0) == '$') {
				F.SYMBOL_OBSERVER.createUserSymbol(symbol);
			}
		}

		return symbol;
	}

	public ISymbol removeSymbol(String symbolName) {
		String name = symbolName;
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			if (symbolName.length() == 1) {
				name = symbolName;
			} else {
				name = symbolName.toLowerCase(Locale.ENGLISH);
			}
		}
		Context context;
		ISymbol symbol;
		for (int i = path.size() - 1; i >= 0; i--) {
			context = path.get(i);
			if (context.equals(Context.SYSTEM)) {
				// don't remove predefined symbols
				continue;
			}
			symbol = context.remove(name);
			if (symbol != null) {
				return symbol;
			}
		}
		return null;
	}

	public Context remove(int index) {
		return path.remove(index);
	}

	public Context set(int index, Context element) {
		return path.set(index, element);
	}

	public int size() {
		return path.size();
	}

	@Override
	public String toString() {
		return path.toString();
	}

}
