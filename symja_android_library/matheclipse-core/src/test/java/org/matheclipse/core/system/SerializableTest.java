package org.matheclipse.core.system;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.ISymbol;

public class SerializableTest extends SpecialTestCase {
	public SerializableTest(String name) {
		super(name);
	}

	public void testRoundTripSerializationSymbol() throws Exception, IOException, ClassNotFoundException {
		EvalEngine engine = EvalEngine.get();
		engine.setSessionID("SerializableTest");
		engine.setRecursionLimit(256);
		ISymbol sym = F.Sin;
		FileOutputStream out = new FileOutputStream("c:\\temp\\testsym.ser");
		ObjectOutputStream oos = new ObjectOutputStream(out);
		sym.writeSymbol(oos);
		oos.close();
		out.close();
		check(engine, false, "Sin[3/10*Pi]", "1/4*5^(1/2)+1/4");
		check(engine, false, "Sin[Pi/5]", "1/4*2^(1/2)*(-5^(1/2)+5)^(1/2)");
		check(engine, false, "Sin[Pi/2]", "1");

		// deserialize
		// Config.SERVER_MODE = true;

		InputStream in = new FileInputStream("c:\\temp\\testsym.ser");
		ObjectInputStream ois = new ObjectInputStream(in);
		sym.clearAll(engine);
		sym.readSymbol(ois);
		ois.close();
		in.close();
		// test the result

		assertEquals(sym.definitionToString(), "{Sin[Pi]=0,\n" + 
				" Sin[2/5*Pi]=1/4*2^(1/2)*(5^(1/2)+5)^(1/2),\n" + 
				" Sin[5/12*Pi]=1/4*(1/3*3^(1/2)+1)*6^(1/2),\n" + 
				" Sin[1/6*Pi]=1/2,\n" + 
				" Sin[I]=I*Sinh[1],\n" + 
				" Sin[1/5*Pi]=1/4*2^(1/2)*(-5^(1/2)+5)^(1/2),\n" + 
				" Sin[1/4*Pi]=1/2*2^(1/2),\n" + 
				" Sin[1/3*Pi]=1/2*3^(1/2),\n" + 
				" Sin[1/10*Pi]=1/4*5^(1/2)-1/4,\n" + 
				" Sin[3/8*Pi]=1/2*(2^(1/2)+2)^(1/2),\n" + 
				" Sin[1/12*Pi]=1/4*(-1/3*3^(1/2)+1)*6^(1/2),\n" + 
				" Sin[1/8*Pi]=1/2*(-2^(1/2)+2)^(1/2),\n" + 
				" Sin[3/10*Pi]=1/4*5^(1/2)+1/4,\n" + 
				" Sin[1/2*Pi]=1,\n" + 
				" Sin[0]=0,\n" + 
				" Sin[ArcSin[x_]]:=x,\n" + 
				" Sin[ArcCos[x_]]:=(-x^2+1)^(1/2),\n" + 
				" Sin[ArcTan[x_]]:=x*(x^2+1)^(1/2)^(-1),\n" +  
				" Sin[Pi*x_NumberQ]:=If[x<1,Sin[(-x+1)*Pi],If[x<2,-Sin[(-x+2)*Pi],Sin[(-2*Quotient[Trunc[x],2]+x)*Pi]]]/;x>=1/2}\n" + 
				"");

		check(engine, false, "Sin[3/10*Pi]", "1/4*5^(1/2)+1/4");
		check(engine, false, "Sin[Pi/5]", "1/4*2^(1/2)*(-5^(1/2)+5)^(1/2)");
		check(engine, false, "Sin[Pi/2]", "1");
	}
}
