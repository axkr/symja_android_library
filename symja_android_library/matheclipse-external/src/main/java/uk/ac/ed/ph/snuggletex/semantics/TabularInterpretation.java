/* $Id: TabularInterpretation.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.semantics;

import uk.ac.ed.ph.snuggletex.internal.util.ObjectUtilities;

/**
 * Represents something which is interpreted as having a tabular format. This is used during
 * token fixing to tidy up rows and coluns.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public class TabularInterpretation implements Interpretation {

    public InterpretationType getType() {
        return InterpretationType.TABULAR;
    }
    
    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }

}
