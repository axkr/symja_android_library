package org.matheclipse.core.reflection.system.rules;

import static org.matheclipse.core.expression.F.*;
import org.matheclipse.core.interfaces.IAST;

public interface ArcCothRules {
	final public static IAST RULES = List(
Set(ArcCoth(C0),
    Times(CC(0L,1L,1L,2L),Pi))
	);
}