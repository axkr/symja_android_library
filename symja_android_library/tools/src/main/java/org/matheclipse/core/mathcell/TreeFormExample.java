package org.matheclipse.core.mathcell;

public class TreeFormExample extends BasePlotExample {

	@Override
	public String exampleFunction() {
		// return "TreeForm(test)";
		// return "TreeForm(a+b)";
		 return "TreeForm(a+(b*q*s)^(2*y)+Sin(c)^(3-z))";
		// return "TreeForm(a+b^3+c^2+d*Pi*e)";
	}

	public static void main(String[] args) {
		TreeFormExample p = new TreeFormExample();
		p.generateHTML();
	}
}