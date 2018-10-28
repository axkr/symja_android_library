package org.matheclipse.core.expression;

public class BuiltInRubi extends BuiltInDummy {
	public BuiltInRubi(final String symbolName) {
		super(symbolName);
	}
	
	@Override
	public final Context getContext() {
		return Context.RUBI;
	}
}
