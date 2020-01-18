package org.matheclipse.core.expression;

import java.util.HashMap;
import java.util.Map;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

public class AssociationAST extends AST {

	private Map<IExpr, Integer> map;

	/**
	 * Public no-arg constructor only needed for serialization
	 * 
	 */
	public AssociationAST() {
		super();
		map = new HashMap<IExpr, Integer>();
	}

	public AssociationAST(IAST listOfRules) {
		super(listOfRules.size(), false);
		map = new HashMap<IExpr, Integer>();
		append(F.Association);

		for (int i = 1; i < listOfRules.size(); i++) {
			IExpr rule = listOfRules.get(i);
			if (rule.isRule()) {
				append(rule.second());
				map.put(rule.first(), i);
			} else {
				throw new WrongArgumentType(F.Association, "Rule expected");
			}
		}
	}

	public IAST normal() {
		return normal(F.List);
	}
	protected IAST normal(IBuiltInSymbol symbol) {
		IASTMutable list = F.ast(symbol, argSize(), true);

		for (Map.Entry<IExpr, Integer> element : map.entrySet()) {
			Integer value = element.getValue();
			list.set(value, F.Rule(element.getKey(), get(value)));
		}
		return list;
	}

	public IExpr getValue(IExpr key) {
		Integer index = map.get(key);
		if (index == null) {
			return F.Missing(F.stringx("KeyAbsent"), key);
		}
		return get(index);
	}
	public String fullFormString() {
		return normal(F.Association).fullFormString();
	} 
	
}
