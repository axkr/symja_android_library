/*
 * @(#)Variable.java
 */
package jp.ac.kobe_u.cs.cream;

/**
 * Variables. A variable is a component of a
 * {@linkplain Network constraint network}. A variable is constructed with an
 * initial {@linkplain Domain domain} which specifies the set of elements over
 * which the variable ranges. See {@link Network} for example programs to
 * construct variables and add them to a constraint network.
 * 
 * @see Network
 * @see Domain
 * @since 1.0
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class Variable implements Cloneable {
	private static int count = 1;

	private Network network;

	private int index = -1;

	private Domain domain;

	private String name;

	private boolean modified;

	private boolean watch;

	/**
	 * Constructs a variable of the network with an initial domain <tt>d</tt>
	 * and a default name. This constructor is equivalent to
	 * <tt>Variable(network, d, null)</tt>.
	 * 
	 * @param net
	 *            the network
	 * @param d
	 *            the initial domain
	 */
	public Variable(Network net, Domain d) {
		this(net, d, null);
	}

	/**
	 * Constructs a variable of the network with an initial domain <tt>d</tt>
	 * and a name specified by the parameter <tt>name</tt>. When the
	 * parameter <tt>name</tt> is <tt>null</tt>, default names (<tt>v1</tt>,
	 * <tt>v2</tt>, and so on) are used.
	 * 
	 * @param net
	 *            the network
	 * @param d
	 *            the initial domain
	 * @param name
	 *            the name of the variable, or <tt>null</tt> for a default
	 *            name
	 */
	public Variable(Network net, Domain d, String name) {
		network = net;
		domain = d;
		modified = true;
		watch = false;
		if (name == null) {
			this.name = "v" + (count++);
		} else {
			this.name = name;
		}
		network.add(this);
	}

	/**
	 * Returns the {@linkplain Network constraint network}.
	 * 
	 * @return the constraint network
	 */
	public Network getNetwork() {
		return network;
	}

	/**
	 * Sets the index of the variable in the network. This is an internal
	 * method.
	 * 
	 * @param index
	 *            the index
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * Returns the index of the variable in the network. This is an internal
	 * method.
	 * 
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Returns the domain of the variable.
	 * @return the domain
	 */
	public Domain getDomain() {
		return domain;
	}

	/**
	 * Sets the domain of the variables.
	 * Modified flag is set when the domain is changed.
	 * @param d the domain.
	 */
	public void setDomain(Domain d) {
		if (domain == null || !domain.equals(d)) {
			domain = d;
			modified = true;
		}
	}

	/**
	 * Updates the domain of the variables.
	 * Old domain is saved to the trail stack.
	 * Modified flag is set when the domain is changed.
	 * New domain is printed out when the watch flag is set.
	 * @param d the domain.
	 * @param trail the trail stack
	 */
	public void updateDomain(Domain d, Trail trail) {
		if (domain == null || !domain.equals(d)) {
			trail.push(this);
			domain = d;
			modified = true;
			if (watch) {
				System.out.println(this + " = " + domain);
			}
		}
	}

	/**
	 * Returns the modified flag.
	 * @return the modified flag
	 */
	public boolean isModified() {
		return modified;
	}

	/**
	 * Sets the modified flag.
	 * @param modified the modified to set
	 */
	public void setModified(boolean modified) {
		this.modified = modified;
	}

	/**
	 * Returns the watch flag.
	 * @return the watch flag
	 */
	public boolean isWatch() {
		return watch;
	}

	/**
	 * Sets the watch flag.
	 * @param watch the watch to set
	 */
	public void setWatch(boolean watch) {
		this.watch = watch;
	}

	/**
	 * Returns the name of this variable.
	 * 
	 * @return the name of this variable
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this variable.
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Creates a copy of this variable for a new network.
	 * 
	 * @return the copy of the variable
	 */
	protected Variable copy(Network net) {
		return new Variable(net, domain, name);
	}

	/**
	 * Returns the name of this variable.
	 * 
	 * @return the name of this variable
	 */
	public String toString() {
		return name;
	}
}
