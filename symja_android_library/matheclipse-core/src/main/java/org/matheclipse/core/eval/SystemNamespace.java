package org.matheclipse.core.eval;

/**
 * 
 */
public class SystemNamespace extends Namespace {
	public final static Namespace DEFAULT = new SystemNamespace();

	public final static String DEFAULT_PACKAGE = "org.matheclipse.core.reflection.system";

	static {
		DEFAULT.add(DEFAULT_PACKAGE);
	}

	public SystemNamespace() {
		super();
	}

}
