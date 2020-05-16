package org.matheclipse.core.expression.data;

import java.time.LocalDateTime;

import org.gavaghan.geodesy.GlobalPosition;
import org.jgrapht.graph.AbstractBaseGraph;
import org.matheclipse.core.builtin.GraphFunctions;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

public class DateObjectExpr extends DataExpr<LocalDateTime> { 
	
	private static final long serialVersionUID = 33260626252103830L;

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static DateObjectExpr newInstance(final LocalDateTime value) {
		return new DateObjectExpr(value);
	}

	protected DateObjectExpr(final LocalDateTime position) {
		super(F.DateObject, position);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof DateObjectExpr) {
			return fData.equals(((DateObjectExpr) obj).fData);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (fData == null) ? 353 : 353 + fData.hashCode();
	}

	@Override
	public int hierarchy() {
		return DATEOBJECTEXPRID;
	}

	@Override
	public IExpr copy() {
		return new DateObjectExpr(fData);
	}
	
	@Override
	public String toString() { 
		return  fData.toString() ;
	}
}
