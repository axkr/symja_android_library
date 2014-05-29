package org.matheclipse.core.reflection.system.rules;

import static org.matheclipse.core.expression.F.*;
import org.matheclipse.core.interfaces.IAST;

public interface ArcSinhRules {
	final public static IAST RULES = List(
Set(ArcSinh(C0),
    C0)
	);
}