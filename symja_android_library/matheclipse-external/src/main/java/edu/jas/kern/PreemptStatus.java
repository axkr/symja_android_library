/*
 * $Id$
 */

package edu.jas.kern;


/**
 * PreemptStatus, defines global status for preemptive interruption handling.
 * @author Heinz Kredel
 */

public class PreemptStatus {


    /**
     * Global status flag.
     */
    private static volatile boolean allowPreempt = true;


    /**
     * No public constructor.
     */
    protected PreemptStatus() {
    }


    /**
     * isAllowed.
     * @return true, preemtive interruption is allowed, else false.
     */
    public static boolean isAllowed() {
        //System.out.println("allowPreempt: " + allowPreempt);
        return allowPreempt;
    }


    /**
     * setAllow, set preemtive interruption to allowed status.
     */
    public static void setAllow() {
        allowPreempt = true;
    }


    /**
     * setNotAllow, set preemtive interruption to not allowed status.
     */
    public static void setNotAllow() {
        allowPreempt = false;
    }

}
