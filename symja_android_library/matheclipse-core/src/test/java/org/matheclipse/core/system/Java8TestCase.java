package org.matheclipse.core.system;

import com.duy.lambda.Consumer;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

import static org.matheclipse.core.expression.F.C10;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.a;
import static org.matheclipse.core.expression.F.b;
import static org.matheclipse.core.expression.F.c;
import static org.matheclipse.core.expression.F.d;
import static org.matheclipse.core.expression.F.e;

/**
 * Tests for the Java port of the
 * <a href="http://www.apmaths.uwo.ca/~arich/">Rubi - rule-based integrator</a>.
 */
public class Java8TestCase extends AbstractTestCase {
    public Java8TestCase(String name) {
        super(name);
    }

    public void testForeach() {
        IAST ast = List(C10, a, b, c, d, e);
        IASTAppendable result = F.List();
        Consumer<IExpr> action = new Consumer<IExpr>() {
            @Override
            public void accept(IExpr x) {
                result.append(x);
            }
        };
        ast.forEach(action);
        assertEquals("{10,a,b,c,d,e}", result.toString());
    }

    public void testStream001() {
        // TODO: 2/20/2018 convert to java 7
//		IAST ast = List(C10, a, b, c, d, e);
//		IASTAppendable result = F.ListAlloc(2);
//		// Consumer<IExpr> action = (IExpr x) -> System.out.println(x);
//		ast.stream().forEach(new java.util.function.Consumer<IExpr>() {
//			@Override
//			public void accept(IExpr x) {
//				result.append(x);
//			}
//		});
//		ast.stream(0, 7).forEach(new java.util.function.Consumer<IExpr>() {
//			@Override
//			public void accept(IExpr x) {
//				result.append(x);
//			}
//		});
//		assertEquals("{10,a,b,c,d,e,List,10,a,b,c,d,e}", result.toString());
    }

}
