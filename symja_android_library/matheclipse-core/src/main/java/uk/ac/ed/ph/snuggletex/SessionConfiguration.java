/* $Id:SessionConfiguration.java 179 2008-08-01 13:41:24Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex;

/**
 * Specifies options for how a {@link SnuggleSession} should parse and process
 * {@link SnuggleInput}s.
 *
 * @author  David McKain
 * @version $Revision:179 $
 */
public final class SessionConfiguration implements Cloneable {
    
    public static final int DEFAULT_EXPANSION_LIMIT = 100;
   
    /** Set to true to fail immediately on error. Default is to record error but keep going */
    private boolean failingFast;
    
    /** 
     * Maximum depth when expanding out user-defined commands and environments.
     * This prevents possible infinite recursion issues, which are hard to detect due to the 
     * highly dynamic nature of LaTeX input.
     * <p>
     * The default value is {@link #DEFAULT_EXPANSION_LIMIT}. 
     * Set this to 0 or less to disable this safeguard (and risk possible consequences,
     * such as the stack or heap being eaten up).
     * <p>
     * Evaluating a user-defined command body, or the begin/end clause of an environment increases
     * the current depth by 1.
     */
    private int expansionLimit;
    
    public SessionConfiguration() {
        this.failingFast = false;
        this.expansionLimit = DEFAULT_EXPANSION_LIMIT;
    }
    
    /**
     * Returns whether or not the {@link SnuggleSession} will fail immediately on error, or
     * keep going.
     * 
     * @return true to fail immediately, false to keep going
     */
    public boolean isFailingFast() {
        return failingFast;
    }
    
    /**
     * Sets whether or not the {@link SnuggleSession} will fail immediately on error, or
     * keep going.
     * 
     * @param failingFast true to fail immediately, false to keep going
     */
    public void setFailingFast(boolean failingFast) {
        this.failingFast = failingFast;
    }
    

    /** 
     * Gets the maximum depth when expanding out user-defined commands and environments.
     * This prevents possible infinite recursion issues, which are hard to detect due to the 
     * highly dynamic nature of LaTeX input.
     * <p>
     * The default value is {@link #DEFAULT_EXPANSION_LIMIT}. 
     * Set this to 0 or less to disable this safeguard (and risk possible consequences,
     * such as the stack or heap being eaten up).
     * <p>
     * Evaluating a user-defined command body, or the begin/end clause of an environment increases
     * the current depth by 1.
     */
    public int getExpansionLimit() {
        return expansionLimit;
    }
    
    /** 
     * Sets the maximum depth when expanding out user-defined commands and environments.
     * This prevents possible infinite recursion issues, which are hard to detect due to the 
     * highly dynamic nature of LaTeX input.
     * <p>
     * The default value is {@link #DEFAULT_EXPANSION_LIMIT}. 
     * Set this to 0 or less to disable this safeguard (and risk possible consequences,
     * such as the stack or heap being eaten up).
     * <p>
     * Evaluating a user-defined command body, or the begin/end clause of an environment increases
     * the current depth by 1.
     */
    public void setExpansionLimit(int expansionLimit) {
        this.expansionLimit = expansionLimit;
    }


    @Override
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new SnuggleLogicException(e);
        }
    }
}
