package de.tilman_neumann.jml.base;

import java.util.Random;

/**
 * Simple helper class to generate random numbers.
 * @author Tilman Neumann
 */
public class Rng extends Random {

	private static final long serialVersionUID = 1821148670513516540L;

	/**
	 * Creates a random integer from the uniform distribution U[min, max-1].
	 * The only requirements are min >= 0 and max > min.
	 * @param min
	 * @param max
	 * @return random int in the desired range
	 */
	public int nextInt(int min, int max) {
		if (min < 0) {
			throw new IllegalArgumentException("min = " + min + " is negative");
		}
		if (max <= min) {
			throw new IllegalArgumentException("max = " + max + " is no bigger than min = " + min);
		}
		
		int diff = max - min;
		int rand = nextInt(diff); // 0...(diff-1)
		return min + rand;
	}

	/**
	 * Creates a random long from the uniform distribution U[0, max-1].
	 * The only requirement is max > 0.
	 * @param max
	 * @return random long in the desired range
	 */
	public long nextLong(long max) {
		if (max < 1) {
			throw new IllegalArgumentException("max = " + max + " is not positive");
		}
		
		long rand = nextLong(); // Long.MIN_VALUE...Long.MAX_VALUE
		long normalized; // shall become 0...(max-1)
		if (rand >= 0) {
			normalized = rand % max;
		} else if (rand > Long.MIN_VALUE) {
			normalized = (-rand) % max;
		} else { // special treatment because -Long.MIN_VALUE == Long.MIN_VALUE
			normalized = Long.MAX_VALUE % max;
		}
		return normalized;
	}

	/**
	 * Creates a random long from the uniform distribution U[min, max-1].
	 * The only requirements are min >= 0 and max > min.
	 * @param min
	 * @param max
	 * @return random long in the desired range
	 */
	public long nextLong(long min, long max) {
		if (min < 0) {
			throw new IllegalArgumentException("min = " + min + " is negative");
		}
		if (max <= min) {
			throw new IllegalArgumentException("max = " + max + " is no bigger than min = " + min);
		}
		
		long diff = max - min;
		long rand = nextLong(); // Long.MIN_VALUE...Long.MAX_VALUE
		long normalized; // shall become 0...(diff-1)
		if (rand >= 0) {
			normalized = rand % diff;
		} else if (rand > Long.MIN_VALUE) {
			normalized = (-rand) % diff;
		} else { // special treatment because -Long.MIN_VALUE == Long.MIN_VALUE
			normalized = Long.MAX_VALUE % diff;
		}
		return min + normalized;
	}
}
