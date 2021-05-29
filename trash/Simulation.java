/**
 * The simulation main class
 */
package hume;

import hume.Network;
import java.util.Set;

/**
 * The <code>Simulation<code/> runs and controls the whole simulation.
 * It also contains all global simulation parameters.
 *
 * SATUS: unfinished, not working!
 *
 * @author eckhart
 *
 */
public strictfp class Simulation {

	/** The interaction network. */
	Network network;
	
	/** An array of all agents. <em>read only!</em> */
	Agent[] allAgents;	
	
	/** The "trust-game" */
	TrustGame game;
	
	/** Ther upper limit for the number of problems (Draft 28.11, p.6). Other than
	 * in the draft the problems are numbered from zero to numProblems-1. Starting to
	 * count from zero simplifies indexing. */
	int numCompetences = -1;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public Simulation() {
		numCompetences = 20;
		game = new TrustGame();
		network = new PartitionNetwork(1000, 20);
		allAgents = network.arrayView;
	}
	
	
	/**
	 * Picks <code>n</code> random numbers from the range
	 * [0..<code>from</code>[, no number occuring twice
	 * @param n    the number of samples to pick
	 * @param from the upper limit of the range (exclusive)
	 * @return the array of random numbers
	 */
	private int[] sample(int n, int range) {
		int[] ret = new int[range];
		for (int i = 0; i < n; i++) {
			ret[i] = RND.random.nextInt(range-i);
			for (int k = i-1; k >= 0; k--) {
				if (ret[k] == ret[i]) ret[i] = range-1-i; 
			}
		}
		return ret;
	}
	
	/**
	 * Randomly picks <code>n</code> agents from an array of
	 * Agents
	 * @param n			the number of agents to sample
	 * @param agentList the array of agents from which the sample
	 *                  shall be taken
	 * @return an array of <code>n</code> samples
	 */
	public Agent[] sampleAgents(int n, Agent[] agentList) {
		Agent[] agentSample = new Agent[n];
		int[] ind = sample(n, agentList.length);
		for (int i = 0; i < n; i++)
			agentSample[i] = agentList[ind[i]];
		return agentSample;
	}
	
	/**
	 * (obsolete ?) Picks a sample of a size of <code>fraction</code> per cent from
	 * an array of agents.
	 * @param fraction	the size of the sample as a fraction of the whole
	 *                  array
	 * @param agentList the array from which the agents shall be sampled
	 * @return an array of randomly picked agents.
	 */
	public Agent[] sampleAgents(double fraction, Agent[] agentList) {
		return sampleAgents((int) fraction * agentList.length, agentList);
	}

	/**
	 * (obsolete? ) Picks a sample of a size of <code>fraction</code> per cent from
	 * a <code>Set</code> of agents.
	 * @param fraction	the size of the sample as a fraction of the whole
	 *                  array
	 * @param agentList the base set
	 * @return an array of randomly picked agents.
	 */
	public Agent[] sampleAgents(double fraction, Set<Agent> agentSet) {
		return sampleAgents(fraction, (Agent[]) agentSet.toArray());
	}
	
	
//	protected void nextRound() {
//		for (Agent a: allAgents) {
//			a.assignProblem(RND.random.nextInt(numCompetences));
//		}
//		Set<Agent> agentSet = network.setView();
//		for (Agent a : agentSet) {
//			if (a.staysAtHome()) {
//				a.learnStayedAtHome();
//				agentSet.remove(a); // geht das mit iteratoren!?
//			}
//		}
//		
//	}

}
