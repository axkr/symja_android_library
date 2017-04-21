package org.matheclipse.core.expression;

public class NumStr extends Num {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6378124858265275437L;

	private String fFloatStr;
	private int fExponent;

	public NumStr(String floatStr) {
		this(floatStr, 1);
	}

	public NumStr(String floatStr, int exponent) {
		int index = floatStr.indexOf("*^");
		fExponent = 1;
		fFloatStr = floatStr;
		if (index > 0) {
			fFloatStr = floatStr.substring(0, index);
			fExponent = Integer.parseInt(floatStr.substring(index + 2));
		}

		fDouble = Double.parseDouble(fFloatStr);
		if (fExponent != 1) {
			// value * 10 ^ exponent
			fDouble = fDouble * Math.pow(10, fExponent);
		}
	}

	public int getExponent() {
		return fExponent;
	}

	public String getFloatStr() {
		return fFloatStr;
	}

}
