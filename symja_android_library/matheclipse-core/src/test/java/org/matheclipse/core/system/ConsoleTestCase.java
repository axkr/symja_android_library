package org.matheclipse.core.system;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.matheclipse.core.eval.Console;

/**
 * 
 */
public class ConsoleTestCase extends AbstractTestCase {
	public ConsoleTestCase(String name) {
		super(name);
	}

	public void test001() {
		String[] args = new String[] { "-c", "D(sin(x)^3,x)" };
		check(args, "3*Cos(x)*Sin(x)^2");
	}

	public void test002() {
		String[] args = new String[] { "-f", "Factorial", "-a", "20" };
		check(args, "2432902008176640000");
	}

	public void test003() {
		String[] args = new String[] { "-c", "f(x_,y_):={x,y}; f(a,b)" };
		check(args, "{a,b}");
	}

	private void check(String[] args, String result) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		PrintStream old = System.out;
		System.setOut(ps);

		Console.main(args);

		System.out.flush();
		System.setOut(old);
		assertEquals(baos.toString(), //
				result);
	}
}
