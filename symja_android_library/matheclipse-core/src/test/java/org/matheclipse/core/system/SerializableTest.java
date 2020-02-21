package org.matheclipse.core.system;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.ASTDataset;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.RulesData;
import org.matheclipse.core.reflection.system.Share;
import org.matheclipse.core.visit.AbstractVisitor;

import junit.framework.TestCase;
import tech.tablesaw.api.Table;

public class SerializableTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// wait for initializing of Integrate() rules:
		F.await();
	}

	public void testAssociation() {
		IAssociation assoc = F.assoc(F.List(F.Rule(F.a, F.b)));
		equalsCopy(assoc);
	}

	public void testDataset() {
		Table table = Table.read().csv("Products,Sales,Market_Share\n" + //
				"a,5500,3\n" + //
				"b,12200,4\n" + //
				"c,60000,33\n", "");

		ASTDataset ds = ASTDataset.newInstance(table);
		equalsStringCopy(ds);

	}

	public void testNum() {
		equalsCopy(F.num(0.5));
		equalsCopy(F.num(Integer.MAX_VALUE));
	}

	public void testComplexNum() {
		equalsCopy(F.complexNum(0.5));
		equalsCopy(F.complexNum(Integer.MAX_VALUE));
		equalsCopy(F.complexNum(0.5, 0.5));
		equalsCopy(F.complexNum(Integer.MAX_VALUE, Integer.MAX_VALUE));
	}

	public void testInteger() {
		equalsCopy(F.ZZ(Integer.MAX_VALUE));
		equalsCopy(F.ZZ(Integer.MIN_VALUE));
		equalsCopy(F.ZZ((Integer.MAX_VALUE) * 2L));
		equalsCopy(F.ZZ((Integer.MIN_VALUE) * 2L));
	}

	public void testFraction() {
		equalsCopy(F.fraction(1L, Integer.MAX_VALUE));
		equalsCopy(F.fraction(1L, Integer.MIN_VALUE));
		equalsCopy(F.fraction(1L, Integer.MAX_VALUE * 2L));
		equalsCopy(F.fraction(1L, Integer.MIN_VALUE * 2L));
		equalsCopy(F.fraction(Integer.MAX_VALUE, 7L));
		equalsCopy(F.fraction(Integer.MIN_VALUE, 3L));
		equalsCopy(F.fraction(Integer.MAX_VALUE * 2L, 5L));
		equalsCopy(F.fraction(Integer.MIN_VALUE * 2L, 11L));
	}

	public void testComplex() {
		equalsCopy(F.complex(F.fraction(1L, Integer.MAX_VALUE)));
		equalsCopy(F.complex(F.fraction(1L, Integer.MIN_VALUE)));
		equalsCopy(F.complex(F.fraction(1L, Integer.MAX_VALUE * 2L)));
		equalsCopy(F.complex(F.fraction(1L, Integer.MIN_VALUE * 2L)));
		equalsCopy(F.complex(F.fraction(Integer.MAX_VALUE, 7L)));
		equalsCopy(F.complex(F.fraction(Integer.MIN_VALUE, 3L)));
		equalsCopy(F.complex(F.fraction(Integer.MAX_VALUE * 2L, 5L)));
		equalsCopy(F.complex(F.fraction(Integer.MIN_VALUE * 2L, 11L)));

		equalsCopy(F.complex(F.fraction(1L, Integer.MAX_VALUE), F.fraction(1L, Integer.MAX_VALUE)));
		equalsCopy(F.complex(F.fraction(1L, Integer.MIN_VALUE), F.fraction(1L, Integer.MIN_VALUE)));
		equalsCopy(F.complex(F.fraction(1L, Integer.MAX_VALUE * 2L), F.fraction(1L, Integer.MAX_VALUE * 2L)));
		equalsCopy(F.complex(F.fraction(1L, Integer.MIN_VALUE * 2L), F.fraction(1L, Integer.MAX_VALUE * 2L)));
		equalsCopy(F.complex(F.fraction(Integer.MAX_VALUE, 7L), F.fraction(Integer.MAX_VALUE, 7L)));
		equalsCopy(F.complex(F.fraction(Integer.MIN_VALUE, 3L), F.fraction(Integer.MIN_VALUE, 3L)));
		equalsCopy(F.complex(F.fraction(Integer.MAX_VALUE * 2L, 5L), F.fraction(Integer.MAX_VALUE * 2L, 5L)));
		equalsCopy(F.complex(F.fraction(Integer.MIN_VALUE * 2L, 11L), F.fraction(Integer.MIN_VALUE * 2L, 11L)));
	}

	public void testSymbol() {
		// equalsCopy(F.Pi);
		equalsCopy(F.symbol("testme"));
	}

	public void testStringX() {
		equalsCopy(F.$str("test"));
	}

	public void testFunction() {
		equalsCopy(F.ast(F.symbol("fun1")));

		equalsCopy(F.Sin(F.Times(F.C1D2, F.Pi)));
		equalsCopy(F.Continue());
		equalsCopy(F.If(F.True, F.Plus(F.Infinity, F.Pi), F.False));
		equalsCopy(F.Log(F.x, F.y));
		equalsCopy(F.ast(F.symbol("fun1")));
		equalsCopy(F.ast(new IExpr[] { F.x, F.y }, F.symbol("fun2")));
	}

	public void testBlank() {
		equalsCopy(F.$b(null));
		equalsCopy(F.$b(F.IntegerQ));
	}

	public void testPattern() {
		equalsCopy(F.$p(F.symbol("test"), true));
		equalsCopy(F.$p(F.symbol("test"), false));
		equalsCopy(F.$p(F.x));
	}

	public void testPatternSequence() {
		equalsCopy(F.$ps(F.symbol("test")));
		equalsCopy(F.$ps(F.x, F.IntegerQ));
		equalsCopy(F.$ps(F.x, F.IntegerQ, true, true));
		equalsCopy(F.$ps(F.x, F.IntegerQ, true, false));
	}

	public void testIntegrateDefinition() {
		// do a dummy evaluation to load integration rules
		F.Integrate.of(F.x, F.x);
		RulesData rulesData = F.Integrate.getRulesData();
		AbstractVisitor visitor = Share.createVisitor();
		rulesData.accept(visitor);
		equalsStringCopy(rulesData);
	}

	public void testSinDefinition() {
		// try to share common sub-IASTs first:
		RulesData rulesData = F.Sin.getRulesData();
		AbstractVisitor visitor = Share.createVisitor();
		rulesData.accept(visitor);
		equalsCopy(rulesData);
	}

	public void testASTRealMatrix() {
		equalsCopy(new ASTRealMatrix(new double[][] { { 1.0, 2.0, 3.0 }, { 3.3, 4.4, 5.5 } }, false));

		// PseudoInverse({{1,2,3},{3,4,5}})
		EvalEngine engine = EvalEngine.get();
		IExpr result = engine.evaluate(F.PseudoInverse(F.List(F.List(F.C1, F.C2, F.C3), F.List(F.C4, F.C5, F.C6))));
		equalsCopy(result);
	}

	public void testASTRealVector() {
		equalsCopy(new ASTRealVector(new double[] { 1.0, 1.2, 3.4 }, false));
	}

	public void testPowerSeries() {
		equalsCopy(new ASTSeriesData(F.x, F.a, F.List(F.C0, F.C1, F.C3), 0, 10, 1));

		// Series(Log(x),{x,a,4})
		EvalEngine engine = EvalEngine.get();
		IExpr result = engine.evaluate(F.Series(F.Log(F.x), F.List(F.x, F.a, F.C4)));
		equalsCopy(result);
	}

	public void testNIL() {
		equalsCopy(F.NIL);
	}

	public void testEvalEngine() {
		try {
			EvalEngine engine = EvalEngine.get();
			engine.evaluate("x=10");
			Context context = engine.getContextPath().getGlobalContext();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(context);
			byte[] bArray = baos.toByteArray();
			baos.close();
			oos.close();
			ByteArrayInputStream bais = new ByteArrayInputStream(bArray);
			ObjectInputStream ois = new ObjectInputStream(bais);
			Context copy = (Context) ois.readObject();
			bais.close();
			ois.close();
			engine.getContextPath().setGlobalContext(copy);
			IExpr result = engine.evaluate("x");
			assertEquals("10", result.toString());

		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
			assertEquals("", cnfe.toString());
		} catch (IOException ioe) {
			ioe.printStackTrace();
			assertEquals("", ioe.toString());
		}
	}

	private void equalsCopy(Object original) {
		try {

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(original);
			byte[] bArray = baos.toByteArray();
			baos.close();
			oos.close();
			ByteArrayInputStream bais = new ByteArrayInputStream(bArray);
			ObjectInputStream ois = new ObjectInputStream(bais);
			Object copy = ois.readObject();
			bais.close();
			ois.close();
			assertEquals(original, copy);

			// if (!original.toString().equals(copy.toString())) {
			// System.out.println(copy.toString());
			// }
			// assertEquals(original.toString(), copy.toString());
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
			assertEquals("", cnfe.toString());
		} catch (IOException ioe) {
			ioe.printStackTrace();
			assertEquals("", ioe.toString());
		}
	}

	private void equalsStringCopy(Object original) {
		try {

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);

			long start0 = System.currentTimeMillis();
			oos.writeObject(original);
			byte[] bArray = baos.toByteArray();
			baos.close();
			oos.close();

			long start1 = System.currentTimeMillis();
			ByteArrayInputStream bais = new ByteArrayInputStream(bArray);
			ObjectInputStream ois = new ObjectInputStream(bais);
			Object copy = ois.readObject();
			bais.close();
			ois.close();
			long end = System.currentTimeMillis();
			long temp = start1 - start0;
			System.out.println(Long.valueOf(temp).toString());
			temp = end - start1;
			System.out.println(Long.valueOf(temp).toString());
			assertEquals(original.toString(), copy.toString());

		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
			assertEquals("", cnfe.toString());
		} catch (IOException ioe) {
			ioe.printStackTrace();
			assertEquals("", ioe.toString());
		}
	}
}
