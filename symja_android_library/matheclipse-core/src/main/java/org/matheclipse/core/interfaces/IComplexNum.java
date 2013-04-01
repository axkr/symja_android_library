package org.matheclipse.core.interfaces;

/**
 * 
 */
public interface IComplexNum extends INumber {

	public double getRealPart();

	public double getImaginaryPart();

	public IComplexNum conjugate();

	public IComplexNum add(IComplexNum val);

	public IComplexNum multiply(IComplexNum val);
	
	public IComplexNum pow(IComplexNum val);
}
