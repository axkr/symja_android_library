package org.matheclipse.core.eval;

import java.util.ArrayDeque;
import java.util.IdentityHashMap;

import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.ISymbol;

public class OptionsStack extends ArrayDeque<IdentityHashMap<ISymbol, IASTAppendable>> {
	 
	private static final long serialVersionUID = -3766795742140489462L;

	public OptionsStack() {
		super();
	}
}
