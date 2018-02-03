package org.matheclipse.core.expression;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.ISymbol;

public class Context implements Serializable {

	private static final long serialVersionUID = 8656114325955206899L;

	/**
	 * The map for predefined (context &quot;System&quot;) symbols
	 */
	public final static Map<String, ISymbol> PREDEFINED_SYMBOLS_MAP = new HashMap<String, ISymbol>(997);

	public final static Context SYSTEM = new Context("System", PREDEFINED_SYMBOLS_MAP);

	private String contextName;

	private Map<String, ISymbol> symbolTable;

	public Context(String contextName) {
		this(contextName, new HashMap<String, ISymbol>());
	}

	private Context(String contextName, Map<String, ISymbol> symbolTable) {
		this.symbolTable = symbolTable;
		this.contextName = contextName;
	}

	public Set<Entry<String, ISymbol>> entrySet() {
		return symbolTable.entrySet();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Context) {
			return contextName.equals(((Context) obj).contextName);
		}
		return false;
	}

	public ISymbol get(Object key) {
		return symbolTable.get(key);
	}

	public String getContextName() {
		return contextName;
	}

	@Override
	public int hashCode() {
		return contextName.hashCode();
	}

	public ISymbol put(String key, ISymbol value) {
		return symbolTable.put(key, value);
	}

	private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
		contextName = stream.readUTF();
		symbolTable = (Map<String, ISymbol>) stream.readObject();
	}

	public Object readResolve() throws ObjectStreamException {
		Context context = EvalEngine.get().getContextPath().getContext(contextName);
		return context;
	}

	public ISymbol remove(String key) {
		return symbolTable.remove(key);
	}

	public int size() {
		return symbolTable.size();
	}

	@Override
	public String toString() {
		return contextName;
	}

	private void writeObject(java.io.ObjectOutputStream stream) throws java.io.IOException {
		stream.writeUTF(contextName);
		stream.writeObject(symbolTable);
	}
}
