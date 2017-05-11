package org.matheclipse.core.visit;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IntegerSym;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Replace all occurrences of Slot[] expressions.
 * 
 * The visitors <code>visit()</code> methods return <code>F.NIL</code> if no substitution occurred.
 */
public class VisitorReplaceSlots extends VisitorExpr {
	final IAST astSlots;

	public VisitorReplaceSlots(IAST ast) {
		super();
		this.astSlots = ast;
	}

	private IExpr getSlot(IntegerSym ii) {
		int i = ii.toInt();
		if (i > 0 && i < astSlots.size()) {
			return astSlots.get(i);
		}
		return F.NIL;
	}

	private int getSlotSequence(IAST ast, int pos, IntegerSym ii) {
		int i = ii.toInt();
		if (i > 0 && i < astSlots.size()) {
			ast.remove(pos);
			for (int j = i; j < astSlots.size(); j++) {
				ast.append(pos++, astSlots.get(j));
			}
		}
		return pos;
	}

	@Override
	public IExpr visit(IAST ast) {
		if (ast.isSlot() && ast.arg1() instanceof IntegerSym) {
			return getSlot((IntegerSym) ast.arg1());
		}
		return visitAST(ast);
	}

	protected IExpr visitAST(IAST ast) {
		IExpr temp;
		IAST result = F.NIL;
		int i = 0;
		int j = 0;
		int size = ast.size();
		while (i < size) {
			if (ast.get(i).isSlotSequence()) {
				IAST slotSequence = (IAST) ast.get(i);
				if (slotSequence.arg1() instanceof IntegerSym) {
					// something may be evaluated - return a new IAST:
					result = ast.clone();
					j = getSlotSequence(result, i, (IntegerSym) slotSequence.arg1());
					i++;
				}
				break;
			}
			temp = ast.get(i).accept(this);
			if (temp.isPresent()) {
				// something was evaluated - return a new IAST:
				result = ast.clone();
				result.set(i++, temp);
				j++;
				break;
			}
			j++;
			i++;
		}
		if (result.isPresent()) {
			while (i < size) {
				if (ast.get(i).isSlotSequence()) {
					IAST slotSequence = (IAST) ast.get(i);
					if (slotSequence.arg1() instanceof IntegerSym) {
						j = getSlotSequence(result, j, (IntegerSym) slotSequence.arg1());
						i++;
					}
					continue;
				}
				temp = ast.get(i).accept(this);
				if (temp.isPresent()) {
					result.set(j, temp);
				}
				i++;
				j++;
			}
		}
		return result;
	}
}
