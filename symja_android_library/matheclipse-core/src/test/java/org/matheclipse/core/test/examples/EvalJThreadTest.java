package org.matheclipse.core.test.examples;

class EvalJThreadTest extends Thread {
	String name;
	String rules;

	EvalJThreadTest(String s, String r) {
		name = s;
		rules = r;
	}

	public void run() {
		EvalJThread p = new EvalJThread(name, rules);

		for (int i = 0; i < 10000; i++) {
			p.load("$x=" + i); // load the input variables
			p.run(); // execute this Evaluator
		}
	}

}
