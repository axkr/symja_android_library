package org.matheclipse.core.generic;

import static org.matheclipse.core.expression.F.Slot;

import java.util.Collection;
import java.util.Map;
import com.duy.lambda.Function;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class UnaryVariable2Slot implements Function<IExpr, IExpr> {
	final private Map<IExpr, IExpr> fMap;

	final private Collection<IExpr> fVariableList;

	private int fSlotCounter;

	public UnaryVariable2Slot(final Map<IExpr, IExpr> map, final Collection<IExpr> variableList) {
		fMap = map;
		fVariableList = variableList;
		fSlotCounter = 0;
	}

	/**
	 * For every given argument return the associated unique slot from the
	 * internal Map
	 * 
	 * @return <code>F.NIL</code>
	 */
	@Override
	public IExpr apply(final IExpr firstArg) {
		if (firstArg.isVariable()) {
			if ((firstArg.toString().charAt(0) >= 'A') && (firstArg.toString().charAt(0) <= 'Z')) {
				if ((((ISymbol) firstArg).getAttributes() & ISymbol.ORDERLESS) == ISymbol.ORDERLESS) {
					return F.NIL;
				}
				if (firstArg.equals(F.Print)) {
					// Print function has "side-effects"
					return F.NIL;
				}
				// probably a built-in function
				return firstArg;
			}
			if (Config.SERVER_MODE && (firstArg.toString().charAt(0) == '$')) {
				// a user-modifiable variable in server mode is not allowed
				return F.NIL;
			}
		} else {
			// not a symbol
			return F.NIL;
		}

		// a variable which could be replaced by a slot:
		IExpr result = fMap.get(firstArg);
		if (result == null) {
			result = Slot(F.integer(++fSlotCounter));
			fMap.put(firstArg, result);
			fVariableList.add(firstArg);
		}
		return result != null ? result : F.NIL;
	}

}
