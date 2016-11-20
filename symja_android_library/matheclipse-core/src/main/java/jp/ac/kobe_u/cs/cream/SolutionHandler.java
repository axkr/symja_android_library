/*
 * @(#)SolutionHandler.java
 */
package jp.ac.kobe_u.cs.cream;

/**
 * An interface for solution handlers.
 * A solution handler is invoked by {@linkplain Solver a solver}.
 * See {@link Solver} for example programs.
 * @see Solver
 * @see Solution
 * @since 1.0
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public interface SolutionHandler {

    /**
     * This method is called for each solution and
     * at the end of search (<tt>solution</tt> is set to <tt>null</tt>).
     * @param solver the solver
     * @param solution the solution or <tt>null</tt> at the end of search
     */
    public void solved(Solver solver, Solution solution);

}
