package org.matheclipse.core.expression;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.matheclipse.core.interfaces.ISymbol;

public class Context {
	public final static Context GLOBAL = new Context("Global", F.PREDEFINED_SYMBOLS_MAP);

	final String contextName;

	final Map<String, ISymbol> symbolTable;

	public Context(String contextName) {
		this(contextName, new HashMap<String, ISymbol>());
	}

	public Context(String contextName, Map<String, ISymbol> symbolTable) {
		this.symbolTable = symbolTable;
		this.contextName = contextName;
	}

	public Set<Entry<String, ISymbol>> entrySet() {
		return symbolTable.entrySet();
	}

	@Override
	public boolean equals(Object obj) {
		return contextName.equals(obj);
	}

	public ISymbol get(Object key) {
		return symbolTable.get(key);
	}

	@Override
	public int hashCode() {
		return contextName.hashCode();
	}

	public ISymbol put(String key, ISymbol value) {
		return symbolTable.put(key, value);
	}

	public ISymbol remove(Object key) {
		return symbolTable.remove(key);
	}

	public int size() {
		return symbolTable.size();
	}

	@Override
	public String toString() {
		return contextName;
	}

}
