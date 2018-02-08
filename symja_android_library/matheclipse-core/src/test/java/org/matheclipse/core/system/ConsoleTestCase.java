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
		 
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos); 
		PrintStream old = System.out; 
		System.setOut(ps); 
		
		Console.main(args); 
		
		System.out.flush();
		System.setOut(old); 
		assertEquals(baos.toString(), //
				"3*Cos(x)*Sin(x)^2");
	}

	public void test002() {
		String[] args = new String[] { "-f", "Factorial", "-a", "20" };
 
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos); 
		PrintStream old = System.out; 
		System.setOut(ps); 
		Console.main(args); 
		
		System.out.flush();
		System.setOut(old); 
		assertEquals(baos.toString(), //
				"2432902008176640000");
	}
}
