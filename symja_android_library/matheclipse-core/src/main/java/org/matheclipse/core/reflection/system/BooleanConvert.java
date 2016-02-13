package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.Equivalent;
import static org.matheclipse.core.expression.F.Implies;
import static org.matheclipse.core.expression.F.Nand;
import static org.matheclipse.core.expression.F.Nor;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Xor;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.visit.VisitorExpr;

/**
 *  
 */
public class BooleanConvert extends AbstractFunctionEvaluator {

	public BooleanConvert() {
		super();
	}

	static class BooleanConvertVisitor extends VisitorExpr {
		public BooleanConvertVisitor() {
			super();
		}

		@Override
		protected IExpr visitAST(IAST ast) {
			if (ast.isNot()) {
				if (ast.arg1().isAST()) {
					IAST notArg1 = (IAST) ast.arg1();
					if (notArg1.isASTSizeGE(Nand, 1)) {
						return notArg1.apply(And);
					} else if (notArg1.isASTSizeGE(Nor, 1)) {
						return notArg1.apply(Or);
					} else if (notArg1.isASTSizeGE(And, 1)) {
						return convertNand(notArg1);
					} else if (notArg1.isASTSizeGE(Or, 1)) {
						return convertNor(notArg1);
					}

				}
			} else if (ast.isASTSizeGE(Equivalent, 1)) {
				return convertEquivalent(ast);
			} else if (ast.isAST(Implies, 3)) {
				return convertImplies(ast);
			} else if (ast.isASTSizeGE(Nand, 1)) {
				return convertNand(ast);
			} else if (ast.isASTSizeGE(Nor, 1)) {
				return convertNor(ast);
			} else if (ast.isASTSizeGE(Xor, 3)) {
				return convertXor(ast);
			}
			return super.visitAST(ast);
		}

		public IAST convertEquivalent(IAST ast) {
			IAST term1 = ast.apply(F.And);
			IAST term2 = term1.mapAt(F.Not(null), 1);
			return F.Or(term1, term2);
		}

		public IAST convertImplies(IAST ast) {
			return F.Or(F.Not(ast.arg1()), ast.arg2());
		}

		public IAST convertNand(IAST ast) {
			IAST result = F.Or();
			for (int i = 1; i < ast.size(); i++) {
				result.add(Not(ast.get(i)));
			}
			return result;
		}

		public IAST convertNor(IAST ast) {
			IAST result = F.And();
			for (int i = 1; i < ast.size(); i++) {
				result.add(Not(ast.get(i)));
			}
			return result;
		}

		public IAST convertXor(IAST ast) {
			IExpr temp = ast.arg2();
			if (ast.size() > 3) {
				IAST clone = ast.clone();
				clone.remove(1);
				temp = convertXor(clone);
			}
			return F.Or(F.And(ast.arg1(), F.Not(temp)), F.And(F.Not(ast.arg1()), temp));
		}
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		BooleanConvertVisitor bcVisitor = new BooleanConvertVisitor();
		IExpr result = ast.arg1().accept(bcVisitor);
		return result.isPresent() ? result : ast.arg1();
	}
}