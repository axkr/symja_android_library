package org.matheclipse.core.reflection.system.rules;

import static org.matheclipse.core.expression.F.*;
import org.matheclipse.core.interfaces.IAST;

public interface ArcCoshRules {
	final public static IAST RULES = List(
Set(ArcCosh(C0),
    Times(CC(0L,1L,1L,2L),Pi))
	);
}