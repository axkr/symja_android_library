/*
 * @(#)Solver.java
 */
package jp.ac.kobe_u.cs.cream;

/**
 * An abstract class for constraint solvers. A solver is constructed with a
 * {@linkplain Network constraint network} which is used by the solver to find
 * solutions. Please note that any network can not be simultaneously shared by
 * two different solvers.
 * <p>
 * Solvers can be used in three typical ways.
 * <ul>
 * <li> As a subroutine: {@link #findFirst()}, {@link #findBest()}, etc.
 * 
 * <pre>
 * Solution solution = solver.findFirst();
 * </pre>
 * 
 * <li> As a handler caller: {@link #findAll(SolutionHandler handler)}, etc.
 * 
 * <pre>
 *     solver.findAll(new SolutionHandler() {
 *         public synchronized void solved(Solver solver, Solution solution) {
 *             .....
 *         }
 *     });
 * </pre>
 * 
 * <li> As a coroutine: {@link #start()}, {@link #waitNext()},
 * {@link #resume()}, {@link #stop()}, etc.
 * 
 * <pre>
 *     for (solver.start(); solver.waitNext(); solver.resume()) {
 *         Solution solution = solver.getSolution();
 *         .....
 *     }
 *     solver.stop();
 * </pre>
 * 
 * </ul>
 * 
 * @see Network
 * @see Solution
 * @see SolutionHandler
 * @since 1.0
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public abstract class Solver implements Runnable {
	/**
	 * A constant value for the default option
	 */
	public static final int DEFAULT = -1;

	/**
	 * A constant value for no options
	 */
	public static final int NONE = 0;

	/**
	 * An option value specifying to minimize the objective variable
	 */
	public static final int MINIMIZE = 1 << 0;

	/**
	 * An option value specifying to maximize the objective variable
	 */
	public static final int MAXIMIZE = 1 << 1;

	/**
	 * An option value specifying to return only better solutions
	 */
	public static final int BETTER = 1 << 2;

	private static int idCounter = 0;

	private int id;

	protected Network network;

	protected int option;

	protected String name;

	protected boolean debug = false;

	protected Solution solution = null;

	protected Solution bestSolution = null;

	protected int bestValue;

	private Thread thread = null;

	private boolean abort = false;

	private boolean running = false;

	private boolean ready = false;

	protected long totalTimeout = 0;

	protected long startTime = 0;

	protected long count = 0;
	
	private Monitor monitor = null;

	/**
	 * Constructs a solver for the given network (for invocation by subclass
	 * constructors). This constructor is equivalent to
	 * <tt>Solver(network, DEFAULT, null)</tt>.
	 * 
	 * @param network
	 *            the constraint network
	 */
	public Solver(Network network) {
		this(network, DEFAULT, null);
	}

	/**
	 * Constructs a solver for the given network and option (for invocation by
	 * subclass constructors). This constructor is equivalent to
	 * <tt>Solver(network, option, null)</tt>.
	 * 
	 * @param network
	 *            the constraint network
	 * @param option
	 *            the option for search strategy
	 */
	public Solver(Network network, int option) {
		this(network, option, null);
	}

	/**
	 * Constructs a solver for the given network and name (for invocation by
	 * subclass constructors). This constructor is equivalent to
	 * <tt>Solver(network, DEFAULT, name)</tt>.
	 * 
	 * @param network
	 *            the constraint network
	 * @param name
	 *            the name of the solver
	 */
	public Solver(Network network, String name) {
		this(network, DEFAULT, name);
	}

	/**
	 * Constructs a solver for the given network, option, and name (for
	 * invocation by subclass constructors). When <tt>option</tt> is
	 * <tt>DEFAULT</tt>, <tt>NONE</tt> is used if the network has no
	 * objective variable, or else <tt>MINIMIZE</tt> is used. Solvers and
	 * subclasses have their ID number starting from 0.
	 * 
	 * @param network
	 *            the constraint network
	 * @param option
	 *            the option for search strategy, or DEFAULT for default search
	 *            strategy
	 * @param name
	 *            the name of the solver, or <tt>null</tt> for a default name
	 */
	public Solver(Network network, int option, String name) {
		this.network = network;
		if (option == DEFAULT) {
			if (network.getObjective() == null)
				option = NONE;
			else
				option = MINIMIZE;
		}
		this.option = option;
		id = idCounter++;
		if (name == null) {
			this.name = getClass().getName() + id;
		} else {
			this.name = name;
		}
		clearBest();
	}

	/**
	 * Sets the monitor.
	 * 
	 * @param monitor
	 *            monitor
	 */
	public void setMonitor(Monitor monitor) {
		this.monitor = monitor;
		monitor.add(this);
	}

	/**
	 * Gets the monitor.
	 */
	public Monitor getMonitor() {
		return monitor;
	}

	/**
	 * Resets the ID counter to be 0.
	 */
	public static void resetIDCounter() {
		idCounter = 0;
	}

	/**
	 * Returns the ID number of this solver.
	 * 
	 * @return the ID number of this solver
	 */
	public int getID() {
		return id;
	}

	/**
	 * Clears the best solution this solver has been found.
	 */
	public void clearBest() {
		bestSolution = null;
		if (isOption(MINIMIZE)) {
			bestValue = IntDomain.MAX_VALUE;
		} else {
			bestValue = IntDomain.MIN_VALUE;
		}
	}

	/**
	 * Returns the last solution this solver is found.
	 * 
	 * @return the last solution, or <tt>null</tt> if no solutions have been
	 *         found
	 */
	public Solution getSolution() {
		return solution;
	}

	/**
	 * Returns the best solution this solver has been found.
	 * 
	 * @return the best solution, or <tt>null</tt> if no solutions have been
	 *         found
	 */
	public Solution getBestSolution() {
		return bestSolution;
	}

	/**
	 * Returns the best objective value this solver has been found. When no
	 * solutions have been found, this method returns
	 * <tt>IntDomain.MAX_VALUE</tt> if the search strategy is MINIMIZE, or
	 * <tt>IntDomain.MIN_VALUE</tt> if the search strategy is MAXIMIZE.
	 * 
	 * @return the best objective value
	 */
	public int getBestValue() {
		return bestValue;
	}

	/**
	 * Returns the option value.
	 * 
	 * @return the option value
	 */
	public int getOption() {
		return option;
	}

	protected boolean isOption(int opt) {
		return (option & opt) != 0;
	}

	protected boolean isBetter(int value1, int value2) {
		return isOption(MINIMIZE) ? value1 < value2 : value1 > value2;
	}

	protected boolean updateBest() {
		if (solution == null)
			return false;
		if (network.getObjective() == null) {
			bestSolution = solution;
			return true;
		}
		int value = solution.getObjectiveIntValue();
		if (isBetter(value, bestValue)) {
			bestSolution = solution;
			bestValue = value;
			return true;
		}
		return false;
	}

	/**
	 * Returns true if the solver is stopped
	 * 
	 * @return true if the solver is stopped
	 */
	public boolean isAborted() {
		return abort;
	}

	/**
	 * Starts the solver in a new thread, and immediately returns to the caller.
	 * The {@link #waitNext()} and {@link #waitNext(long timeout)} methods can
	 * be used to wait the next solution. When a solution is found, the solver
	 * suspends the execution until the {@link #resume()} method is called. You
	 * can stop the solver anytime by calling the {@link #stop()} method.
	 */
	public synchronized void start() {
		start(0);
	}

	/**
	 * Starts the solver in a new thread with the timeout, and immediately
	 * returns to the caller. When the <tt>timeout</tt> milliseconds have been
	 * elapsed since the start of the solver, it stops the execution. The
	 * {@link #waitNext()} and {@link #waitNext(long timeout)} methods can be
	 * used to wait the next solution, or to detect the timeout. When a solution
	 * is found, the solver suspends the execution until the {@link #resume()}
	 * method is called. You can stop the solver anytime by calling the
	 * {@link #stop()} method.
	 * 
	 * @param timeout
	 *            timeout in milliseconds (non-positive value means no timeout)
	 */
	public synchronized void start(long timeout) {
		// For bug fix. (ParallelSolver would not stop)
		// Modified by Muneyuki Kawatani 05/12/09
		thread = null;
		if (debug) {
			System.out.println(name + " start");
		}
		startTime = System.currentTimeMillis();
		count = 0;
		totalTimeout = timeout;
		abort = false;
		running = true;
		ready = false;
		solution = null;
		thread = new Thread(this);
		thread.start();
		notifyAll();
	}

	private class HandlerInvoker implements Runnable {
		private SolutionHandler handler;

		private long timeout;

		public HandlerInvoker(SolutionHandler handler, long timeout) {
			this.handler = handler;
			this.timeout = timeout;
		}

		public void run() {
			clearBest();
			for (start(timeout); waitNext(); resume()) {
				if (isAborted())
					break;
				handler.solved(Solver.this, solution);
			}
			// solution = null;
			handler.solved(Solver.this, null);
			stop();
		}
	}

	/**
	 * Starts the solver in a new thread, and immediately returns to the caller.
	 * The handler is called for each solution and at the end of the solver
	 * execution. You can stop the solver anytime by calling the {@link #stop()}
	 * method.
	 * 
	 * @param handler
	 *            solution handler
	 */
	public synchronized void start(SolutionHandler handler) {
		start(handler, 0);
	}

	/**
	 * Starts the solver in a new thread with the timeout, and immediately
	 * returns to the caller. When the <tt>timeout</tt> milliseconds have been
	 * elapsed since the start of the solver, it stops the execution. The
	 * handler is called for each solution and at the end of the solver
	 * execution. You can stop the solver anytime by calling the {@link #stop()}
	 * method.
	 * 
	 * @param handler
	 *            solution handler
	 * @param timeout
	 *            timeout in milliseconds (non-positive value means no timeout)
	 */
	public synchronized void start(SolutionHandler handler, long timeout) {
		(new Thread(new HandlerInvoker(handler, timeout))).start();
	}

	/**
	 * Waits for the next solution, or the end of the solver execution. It
	 * returns <tt>true</tt> if the next solution is available, <tt>false</tt>
	 * if the solver ends the execution.
	 * 
	 * @return <tt>true</tt> if the next solution is available
	 */
	public synchronized boolean waitNext() {
		return waitNext(0);
	}

	/**
	 * Waits for the next solution, or the end of the solver execution with the
	 * timeout. It returns <tt>true</tt> if the next solution is available
	 * within the timeout milliseconds, <tt>false</tt> if the solver ends the
	 * execution or the <tt>timeout</tt> milliseconds have been elapsed since
	 * the start of this method.
	 * 
	 * @param timeout
	 *            timeout in milliseconds (non-positive value means no timeout)
	 * @return <tt>true</tt> if the next solution is available
	 */
	public synchronized boolean waitNext(long timeout) {
		if (debug) {
			System.out.println(name + " waitNext");
		}
		long deadline = Long.MAX_VALUE;
		if (timeout > 0) {
			deadline = System.currentTimeMillis() + timeout;
		}
		if (totalTimeout > 0) {
			deadline = Math.min(deadline, startTime + totalTimeout);
		}
		while (running && !ready) {
			if (deadline == Long.MAX_VALUE) {
				try {
					wait();
				} catch (InterruptedException e) {
				}
			} else {
				long t = deadline - System.currentTimeMillis();
				if (t <= 0)
					break;
				try {
					wait(Math.max(10, t));
				} catch (InterruptedException e) {
				}
			}
		}
		if (!running) {
			// Failure
			return false;
		} else if (!ready) {
			// Timeout
			return false;
		}
		return true;
	}

	/**
	 * Resumes the execution of the solver.
	 */
	public synchronized void resume() {
		if (debug) {
			System.out.println(name + " resume");
		}
		ready = false;
		notifyAll();
	}

	/**
	 * Waits until the solver ends the execution.
	 */
	public synchronized void join() {
		if (debug) {
			System.out.println(name + " join");
		}
		while (thread == null) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		while (running) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		thread = null;
	}

	/**
	 * Stops the execution of the solver.
	 */
	public synchronized void stop() {
		if (debug) {
			System.out.println(name + " stop");
		}
		abort = true;
		while (running) {
			notifyAll();
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		// For bug fix. (ParallelSolver would not stop)
		// Modified by Muneyuki Kawatani 05/12/09
		// thread = null;
	}

	protected synchronized void success() {
		if (debug) {
			System.out.println(name + " success");
		}
		if (abort)
			return;
		count++;
		Thread.yield();
		boolean better = updateBest();
		if (isOption(BETTER)) {
			if (monitor != null) {
				// monitor.addData(this, bestValue);
				int value = solution.getObjectiveIntValue();
				monitor.addData(this, value);
			}
			if (!better)
				return;
		} else {
			if (monitor != null) {
				int value = solution.getObjectiveIntValue();
				monitor.addData(this, value);
			}
		}
		ready = true;
		notifyAll();
		while (!abort && ready) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
	}

	protected synchronized void fail() {
		if (debug) {
			System.out.println(name + " fail");
		}
		solution = null;
		running = false;
		notifyAll();
	}

	/**
	 * Returns the number of solutions. 
	 * @return the number of solutions
	 */
	public long getCount() {
		return count;
	}

	/**
	 * Returns the elapsed time in milli seconds.
	 * @return the elapsed time
	 */
	public long getElapsedTime() {
		long time = System.currentTimeMillis();
		return time - startTime;
	}

	/**
	 * The body of the solver. This method is called from {@link Solver#start}
	 * methods.
	 */
	public abstract void run();

	/**
	 * Finds the first solution. This method is equivalent to
	 * {@link #findFirst(long) findFirst(0)}.
	 * 
	 * @return the first solution, or <tt>null</tt> if no solutions found
	 */
	public synchronized Solution findFirst() {
		return findFirst(0);
	}

	/**
	 * Finds the first solution with the timeout. This method is implemented as
	 * follows:
	 * 
	 * <pre>
	 * clearBest();
	 * start(timeout);
	 * waitNext();
	 * stop();
	 * return getBestSolution();
	 * </pre>
	 * 
	 * @param timeout
	 *            timeout in milliseconds (non-positive value means no timeout)
	 * @return the first solution, or <tt>null</tt> if no solutions found
	 */
	public synchronized Solution findFirst(long timeout) {
		clearBest();
		start(timeout);
		waitNext();
		stop();
		return getBestSolution();
	}

	/**
	 * Finds the best solution. This method is equivalent to
	 * {@link #findBest(long) findBest(0)}.
	 * 
	 * @return the best solution, or <tt>null</tt> if no solutions found
	 */
	public synchronized Solution findBest() {
		return findBest(0);
	}

	/**
	 * Finds the best solution with the timeout. This method is implemented as
	 * follows:
	 * 
	 * <pre>
	 * clearBest();
	 * for (start(timeout); waitNext(); resume()) {
	 * 	;
	 * }
	 * stop();
	 * return getBestSolution();
	 * </pre>
	 * 
	 * @param timeout
	 *            timeout in milliseconds (non-positive value means no timeout)
	 * @return the best solution, or <tt>null</tt> if no solutions found
	 */
	public synchronized Solution findBest(long timeout) {
		clearBest();
		for (start(timeout); waitNext(); resume()) {
			;
		}
		stop();
		return getBestSolution();
	}

	/**
	 * Invokes the handler for each solution. This method is equivalent to
	 * {@link #findAll(SolutionHandler, long) findFirst(handler, 0)}.
	 * 
	 * @param handler
	 *            solution handler
	 */
	public synchronized void findAll(SolutionHandler handler) {
		findAll(handler, 0);
	}

	/**
	 * Invokes the handler for each solution with the timeout. This method is
	 * implemented as follows:
	 * 
	 * <pre>
	 * clearBest();
	 * start(handler, timeout);
	 * join();
	 * </pre>
	 * 
	 * @param handler
	 *            solution handler
	 * @param timeout
	 *            timeout in milliseconds (non-positive value means no timeout)
	 */
	public synchronized void findAll(SolutionHandler handler, long timeout) {
		clearBest();
		start(handler, timeout);
		join();
	}

	/**
	 * Returns the name of this solver.
	 * 
	 * @return the name of this solver
	 */
	public String toString() {
		return name;
	}
}
