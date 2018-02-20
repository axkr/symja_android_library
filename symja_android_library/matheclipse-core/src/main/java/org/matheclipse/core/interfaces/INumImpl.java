package org.matheclipse.core.interfaces;

import org.matheclipse.core.expression.F;

/**
 * Created by Duy on 2/20/2018.
 */

public abstract class INumImpl extends ISignedNumberImpl implements INum {


    public INum multiply(IRational val) {
        return multiply(F.num(val.getReal()));
    }
}
