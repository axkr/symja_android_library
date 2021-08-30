/*
 * $Id$
 */

package edu.jas.kern;


/**
 * PrettyPrint, defines global pretty print status.
 * @author Heinz Kredel
 */

public class PrettyPrint {


    private static volatile boolean doPretty = true;


    protected PrettyPrint() {
    }


    /**
     * isTrue.
     * @return true, if to use pretty printing, else false.
     */
    public static boolean isTrue() {
        return doPretty;
    }


    /**
     * setPretty. Set use pretty printing to true.
     */
    public static void setPretty() {
        doPretty = true;
    }


    /**
     * setInternal. Set use pretty printing to false.
     */
    public static void setInternal() {
        doPretty = false;
    }

}
