package org.matheclipse.core.reflection.system.rules;

import static org.matheclipse.core.expression.F.*;
import org.matheclipse.core.interfaces.IAST;

public interface ArcTanhRules {
	final public static IAST RULES = List(
Set(ArcTanh(C0),
    C0)
	);
}