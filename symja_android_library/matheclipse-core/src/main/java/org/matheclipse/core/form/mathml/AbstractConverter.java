package org.matheclipse.core.form.mathml;

/**
 *
 */
public abstract class AbstractConverter implements IConverter {
	protected AbstractMathMLFormFactory fFactory;

	public AbstractConverter() {
	}

	/**
	 * @return
	 */
	public AbstractMathMLFormFactory getFactory() {
		return fFactory;
	}

	/**
	 * @param factory
	 */
	@Override
	public void setFactory(final AbstractMathMLFormFactory factory) {
		fFactory = factory;
	}

}
