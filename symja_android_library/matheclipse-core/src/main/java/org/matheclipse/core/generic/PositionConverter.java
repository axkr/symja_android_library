package org.matheclipse.core.generic;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.generic.interfaces.IPositionConverter;

public class PositionConverter implements IPositionConverter<IExpr>{
	public IExpr toObject(final int i) {
		if (i < 3) {
			switch (i) {
			case 0:
				return F.C0;
			case 1:
				return F.C1;
			case 2:
				return F.C2;
			}
		}
		return F.integer(i);
	}

	public int toInt(final IExpr position) {
		if (position instanceof IInteger) {
			return ((IInteger)position).toInt();
		}
		return -1;
	}
}
