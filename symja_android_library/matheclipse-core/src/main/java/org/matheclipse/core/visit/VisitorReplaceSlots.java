package org.matheclipse.core.visit;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IntegerSym;
import org.matheclipse.core.expression.data.DatasetExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IStringX;

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

	private IExpr getSlot(IInteger ii) {
		int i = ii.toIntDefault(Integer.MIN_VALUE);
		if (i >= 0 && i < astSlots.size()) {
			return astSlots.get(i);
		}
		return F.NIL;
	}

	private IExpr getSlot(IStringX str) {
		if (astSlots.arg1() instanceof DatasetExpr) {
			return ((DatasetExpr) astSlots.arg1()).getValue(str);
		}
		if (astSlots.arg1().isAssociation()) {
			return ((IAssociation) astSlots.arg1()).getValue(str);
		}
		return F.NIL;
	}

	private IExpr getSlotSequence(IntegerSym ii) {
		int i = ii.toIntDefault(Integer.MIN_VALUE);
		if (i >= 0 && i <= astSlots.size()) {
			IASTAppendable result = F.ast(F.Sequence, astSlots.size(), false);
			for (int j = i; j < astSlots.size(); j++) {
				result.append(astSlots.get(j));
			}
			return result;
		}
		return F.NIL;
	}

	private int getSlotSequence(IASTAppendable ast, int pos, IntegerSym ii) {
		int i = ii.toIntDefault(Integer.MIN_VALUE);
		if (i >= 0 && i < astSlots.size()) {
			ast.remove(pos);
			for (int j = i; j < astSlots.size(); j++) {
				ast.append(pos++, astSlots.get(j));
			}
		}
		return pos;
	}

	@Override
	public IExpr visit(IASTMutable ast) {
		if (ast.isSlot()) {
			if (ast.arg1().isInteger()) {
				return getSlot((IInteger) ast.arg1());
			} else if (ast.arg1().isString()) {
				return getSlot((IStringX) ast.arg1());
			}
		}
		if (ast.isSlotSequence() && ast.arg1().isInteger()) {
			return getSlotSequence((IntegerSym) ast.arg1());
		}
		return visitAST(ast);
	}

	@Override
	protected IExpr visitAST(IAST ast) {
		IExpr temp;
		IASTAppendable result = F.NIL;
		int i = 0;
		int j = 0;
		int size = ast.size();
		while (i < size) {
			if (!ast.get(i).isFunction()) {
				if (ast.get(i).isSlotSequence()) {
					IAST slotSequence = (IAST) ast.get(i);
					if (slotSequence.arg1().isInteger()) {
						// something may be evaluated - return a new IAST:
						result = ast.copyAppendable();
						j = getSlotSequence(result, i, (IntegerSym) slotSequence.arg1());
						i++;
					}
					break;
				}
				temp = ast.get(i).accept(this);
				if (temp.isPresent()) {
					// something was evaluated - return a new IAST:
					result = ast.setAtClone(i++, temp);
					j++;
					break;
				}
			}
			j++;
			i++;
		}
		if (result.isPresent()) {
			while (i < size) {
				if (!ast.get(i).isFunction()) {
					if (ast.get(i).isSlotSequence()) {
						IAST slotSequence = (IAST) ast.get(i);
						if (slotSequence.arg1().isInteger()) {
							j = getSlotSequence(result, j, (IntegerSym) slotSequence.arg1());
							i++;
						}
						continue;
					}
					temp = ast.get(i).accept(this);
					if (temp.isPresent()) {
						result.set(j, temp);
					}
				}
				i++;
				j++;
			}
		}
		return result;
	}
}
