package hume;

import java.util.Random; 

/**
 * This purpose of class <code>RND</code> is to define one particular random
 * number generator that is to be used throughout the simulation.
 * 
 * Additionally, it provides a few useful methods for picking a certain
 * number of samples from a set of numbers or picking an entry from
 * a probability distribution.
 * 
 * <p>This allows to reproduce simulation results exactly by
 * reusing the same seed for the random number generator.
 * 
 * <p>By default the java.util.Random generator is used. In order to
 * make the simulation reproducible, the seed should either be set
 * before the simulation run via method <code>setSeed</code> or
 * it should be read via method<code>getSeed</code> and be included
 * in the simulation report, so that it can be set to exactly the same
 * value when trying to reproduce the simulation results 1:1.
 *
 * STATUS: mature.
 * 
 * @author eckhart
 *
 */
public strictfp class RND {
	/* The seed for the random number generator. The seed can be
	 * set via the <code>setSeed</code> method. The default
	 * seed is the current system time in milliseconds. */
	private static long seed = System.currentTimeMillis();
	
	/** 
	 * The random number generator to be used by any class
	 * within the simulation. Defaults to the standard 
	 * java random number generator.
	 */
	public static Random random = new Random(seed);
	
	/** Returns the seed the random number generator was initialized with. */
	public static long getSeed() { return seed; };

	/**
	 * Sets the seed of the random number generator. This should
	 * be done only between simulation runs.
	 * @param seed long value that contains the seed of the 
	 *             random number generator.
	 */
	public static void setSeed(long seed) { 
		RND.seed = seed;
		random.setSeed(seed);
	}
	
	/**
	 * Initialize the random number generator with a new seed.
	 * The seed can be read via the method <code>getSeed</code>.
	 * This method should always be called before staring a new
	 * simulation, so that the simulation begings with a
	 * "defined" state of the random number generator.
	 */
	public static void pickNewSeed() {
		setSeed(System.currentTimeMillis());
	}
	
	//
	// additional methods
	//
	
	/**
	 * Picks <code>n</code> random numbers from the range
	 * [0..<code>range</code>[, no number occuring twice
	 * @param n    the number of samples to pick
	 * @param range the upper limit of the range (exclusive)
	 * @return the array of random numbers
	 */
	public static int[] sample(int n, int range) {
		assert n <= range && n >= 0;
		
		// this algroithms works well, when n is small
		// but does it really work???
		
		int[] ret = new int[n];
		int l;
		for (int i = 0; i < n; i++) {
			ret[i] = RND.random.nextInt(range-i);
			l = i;
			for (int k = i-1; k >= 0; k--) {
				if (ret[l] == ret[k]) {
					ret[k] = range-k-1;
					l = k;
				}
			}
		}
		return ret;
	}	
	
	/**
	 * Returns the normalized vector, the entries of which add up to 1
	 * 
	 * @param vector  vector that is to be normalized
	 * @return the normalized vector
	 */
	public static double[] normalized(double [] vector) {
		double[] normalized = new double[vector.length];
		double sum = 0.0;
		for (double f: vector) sum += f;
		for (int i = 0; i < vector.length; i++) 
			normalized[i] = vector[i] / sum;
		return normalized;
	}
	
	/**
	 * Selects a random value from a probability distribution
	 * @param distribution an array of positive real values
	 * @return the index of the value picked
	 */
	public static int select(double [] distribution) {
		double[] cumulation = new double[distribution.length];
		double r;
		int i, k, m;
		
		cumulation[0] = distribution[0];
		for (i = 1; i < distribution.length; i++)
			cumulation[i] = cumulation[i-1] + distribution[i];
		
		i = 0; m=0; k = cumulation.length-1;
		r = random.nextDouble() * cumulation[k];		
		while (i < k) {
			m = (i+k)/2;
			if (r > cumulation[m]) i = m+1;
			else k = m;
		}
		return i;
	}
}
