package org.matheclipse.core.interfaces;

/**
 * Created by Duy on 2/20/2018.
 */

public abstract class IComplexNumImpl extends INumberImpl implements IComplexNum {
    public abstract IComplexNum add(IComplexNum val);

    @Override
    public abstract IComplexNum conjugate();

    public abstract IComplexNum multiply(IComplexNum val);

    public abstract IComplexNum pow(IComplexNum val);

    @Override
    public long leafCount() {
        return 3;
    }
}
