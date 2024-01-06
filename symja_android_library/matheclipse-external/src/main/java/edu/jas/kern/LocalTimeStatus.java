/*
 * $Id$
 */

package edu.jas.kern;


import java.util.concurrent.Callable;


/**
 * Run-time status, defines local status and handling for local run time limits.
 * @see edu.jas.kern.TimeStatus
 * @author Heinz Kredel
 */

public class LocalTimeStatus {


    /**
     * Local status flag.
     */
    private boolean allowTime = false;


    /**
     * Local run-time limit in milliseconds.
     */
    private long limitTime = Long.MAX_VALUE;


    /**
     * Local run-time limit in milliseconds.
     */
    private long startTime = System.currentTimeMillis();


    /**
     * Local call back method. true means continue, false means throw exception.
     */
    private Callable<Boolean> callBack = null;


    /**
     * Public constructor.
     */
    public LocalTimeStatus() {
        this(false, Long.MAX_VALUE, false);
    }


    /**
     * Public constructor.
     * @param a true for active, false for inactive
     * @param d time limit before exception
     * @param r true for continue, false for exception
     */
    public LocalTimeStatus(boolean a, long d, boolean r) {
        allowTime = a;
        limitTime = d;
        callBack = new LocalTimeStatus.TSCall(r);
        startTime = System.currentTimeMillis();
    }


    /**
     * To String.
     * @return String representation of this.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("LocalTimeStatus(");
        sb.append("" + allowTime);
        sb.append(", " + limitTime);
        try {
            sb.append(", " + (callBack == null ? "null" : callBack.call()));
        } catch (Exception e) {
        }
        sb.append(", " + startTime);
        sb.append(")");
        return sb.toString();
    }


    /**
     * isActive.
     * @return true, if run-time interruption is active, else false.
     */
    public synchronized boolean isActive() {
        return allowTime;
    }


    /**
     * setAllow, set run-time interruption to allowed status.
     */
    public synchronized void setActive() {
        allowTime = true;
    }


    /**
     * setNotActive, set run-time interruption to not active status.
     */
    public synchronized void setNotActive() {
        allowTime = false;
    }


    /**
     * setLimit, set run-time limit in milliseconds.
     */
    public synchronized void setLimit(long t) {
        limitTime = t;
    }


    /**
     * Restart timer, set run-time to current time.
     */
    public synchronized void restart() {
        startTime = System.currentTimeMillis();
    }


    /**
     * set call back, set the Callabe object.
     */
    public synchronized void setCallBack(Callable<Boolean> cb) {
        callBack = cb;
    }


    /**
     * Check for exceeded time, test if time has exceeded and throw an exception
     * if so.
     * @param msg the message to be send with the exception.
     */
    public synchronized void checkTime(String msg) {
        if (!allowTime) {
            return;
        }
        if (limitTime == Long.MAX_VALUE) {
            return;
        }
        long tt = (System.currentTimeMillis() - startTime - limitTime);
        //System.out.println("tt = " + (limitTime+tt));
        if (tt <= 0L) {
            return;
        }
        if (callBack != null) {
            try {
                boolean t = callBack.call();
                if (t) {
                    return;
                }
            } catch (Exception e) {
            }
        }
        if (msg != null) {
            msg = msg + ", ";
        }
        throw new TimeExceededException(msg + "elapsed time >= " + (limitTime + tt) + " ms");
    }


    /**
     * A default call back class.
     */
    public static class TSCall implements Callable<Boolean> {


        boolean flag = true;


        public TSCall(boolean b) {
            flag = b;
        }


        public Boolean call() {
            return flag;
        }

    }
}
