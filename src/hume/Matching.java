/**
 * 
 */
package hume;

/**
 * Class <code>Matching</code> is an abstract class that encapsulates 
 * the matching algorithm. To implement a matching algorithm a
 * descendent of this class should be defined that implements the
 * abstract method <code>matchAgents</code>.
 * 
 * <p>Method <code>match Agents</code> is the only method that needs
 * to be overridden, in order to implement a matching algorithm. All
 * other methods in this class are protected methods that implement
 * certain useful utility functions, such as picking a sample from
 * a set of agents.
 * 
 * STATUS: fairly mature and well tested
 * 
 * @author eckhart
 *
 */
public abstract strictfp class Matching {

	
	/**
	 * Randomly picks <code>n</code> agents from an array of
	 * Agents
	 * @param n			the number of agents to sample
	 * @param agentList the array of agents from which the sample
	 *                  shall be taken
	 * @return an array of <code>n</code> samples
	 */
	protected final static Agent[] sampleAgents(int n, Agent[] agentList) {
		assert n <= agentList.length;
		Agent[] agentSample = new Agent[n];
		int[] ind = RND.sample(n, agentList.length);

//		// just a little testing...
//		for (int k = 0; k < ind.length; k++)
//			for (int l = k+1; l < ind.length; l++)
//				assert ind[k] != ind[l] : n+" "+agentList.length+" "+k+" "+l+" "+ind[k]+" "+ind[l];
		
		for (int i = 0; i < n; i++)
			agentSample[i] = agentList[ind[i]];
		return agentSample;
	}
	
	/**
	 * Returns a sample of agents the size of which is a certain
	 * fraction of the reference group. The returned sample does
	 * never include the given agent (for which the sample is taken). 
	 * @param sampleSize     the relative size of the sample 
	 *                       (ranging from 0.0 to 1.0)
	 * @param referenceGroup the group of agents from which the sample
	 *                       shall be taken
	 * @return an array of agents that represents the sample
	 */
	protected final static Agent[] pickSample(Agent agent, double sampleSize, 
			                  Agent[] referenceGroup) {
		assert sampleSize >= 0.0 && sampleSize <= 1.0;
		int n = (int) (sampleSize * referenceGroup.length + 1);
		assert n <= referenceGroup.length;
		Agent[] sample = sampleAgents(n, referenceGroup); 
		Agent[] ret = new Agent[n-1];
		
		// if this agent was in the sample, remove it
		// otherwise remove just the last element form the sample
		int k = 0;
		for (int i = 0; i < n-1; i++) {
			if (sample[i] == agent) k = 1;
			ret[i] = sample[i+k];
		}
		return ret;
	}		
	
	/**
	 * Estimates the revenue an agent receives if it lets another
	 * agent solve its problem (instead of solving it by itself).
	 * 
	 * @param agent     the agent for which the revenue shall be estimated
	 * @param sample    the sample of agents which shall be used for estimation
	 * @param game      the game to be played between the agent an a potential solution
	 *                  provider.
	 * @param discount  a discount value rangeing from 0.0 to 1.0 that specifies 
	 *                  where the actual estiamted value shall be chosen in 
	 *                  the interval from the mean eastimated value 
	 *                  (discount = 0.0) and the maximum estimated value
	 *                  (discount = 1.0)
	 * @return the revenue from getting an external solution
	 */
	protected double estimateService(Agent agent, Agent[] sample, TrustGame game,
									 double discount) {
		int n = 0;
		double r, mean = 0.0, max = 0.0;
		
		for (Agent a: sample) {
			if (agent.trust(a)) {
				n++;
				r = game.customersReward(a.competence(agent.currentProblem));
				if (r > max) max = r;
				mean += r;
			}
		}
		
		if (n == 0) return 0.0; // does this occur often ??
		else {
			mean /= n;
			return mean + (max - mean) * discount;
		}
	}	
	
	/**
	 * Assigns problems to the agents and then finds for every 
	 * agent that has a problem an agent that solves the problem. 
	 * Some agents may be assigned to themselves as problem solvers. 
	 * 
	 * <p>The method returns an array of pairs ("matches") of
	 * agents. Every agent that has a problem will occur
	 * in the first place of exactly one of these pairs. Agents 
	 * that are matched to solve their own problem will occur
	 * in both places of one pair. No agent will occur more than 
	 * once in the second place of a pair, i.e. no agent solves 
	 * two problems in one round. Agents that do neither have
	 * a problem nor solve a problem do not appear in the
	 * return value
	 * 
	 * @param network the interaction network that contains the 
	 *                agents that shall be matched.
	 *                Not all agents need to have a problem, but 
	 *                all agents are considered potential problem 
	 *                solvers.
	 * @param game that the problem-agent and its solver play.
	 * @return an array of pairs of agents that have been matched.
	 *         Depending on how many problems occured (which may
	 *         be smaller than the number of agents), not all 
	 *         agents may occur in the return value.
	 */
	public abstract Agent[][] matchAgents(Network network, TrustGame game);
	
}
