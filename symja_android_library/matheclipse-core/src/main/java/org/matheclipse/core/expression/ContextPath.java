package org.matheclipse.core.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.interfaces.ISymbol;

public class ContextPath {
	List<Context> path = new ArrayList<Context>();

	public ContextPath() {
		path.add(Context.SYSTEM);
		path.add(new Context("Global"));
	}

	public ContextPath(String contextName) {
		path.add(Context.SYSTEM);
		path.add(new Context(contextName));
	}

	public ContextPath(Context context) {
		path.add(Context.SYSTEM);
		path.add(context);
	}

	public boolean add(Context e) {
		return path.add(e);
	}

	public Context get(int index) {
		return path.get(index);
	}

	public ISymbol getSymbol(String symbolName) {
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
		symbol = new Symbol(name);
		context.put(name, symbol);
		// engine.putUserVariable(name, symbol);
		// if (name.charAt(0) == '$') {
		// SYMBOL_OBSERVER.createUserSymbol(symbol);
		// }

		return symbol;
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
