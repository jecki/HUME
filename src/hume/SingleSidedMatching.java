/**
 * ETG Solutions (HUME Draft 2.1.08, p. 10-11)
 */
package hume;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Matching algorithm for "single sided" matching. In the case of single sided
 * matching (at most) half of the agents get a problem every round. These
 * agents then look for a solution provider among the agents that do not have
 * a problem.
 * 
 * STATUS: finished, MatchingTest successful, 
 * but a SingleSidedMacthingTest has not been programmed yet!
 * 
 * @author eckhart
 *
 */
public strictfp class SingleSidedMatching extends Matching {
	/** percentage of agents that will be assigned a problem. Usually, this
	 * should not be greater than 0.5 */
	public double problemRatio = 0.4;
	
	public double sampleSize = 0.1;
	public double serviceDiscount = 0.5;	
	
	/**
	 * Returns true, if the agent considers it more profitable to
	 * stay at home and solve its own problem instead of letting
	 * someone else solve its problem.
	 * 
	 * <p>Implicitely sets the aspiration level of the agents.
	 * 
	 * <p>Please note that the semantics of this method differs from
	 * the method with the same name from class DoubleSidedMatching
	 * 
	 * @see DoubleSidedMatching.staysAtHome
	 */
	protected boolean staysAtHome(Agent agent, Agent[] referenceGroup, 
			                      TrustGame game) {
		double stayProfit = game.valueAdd(agent.competence(agent.currentProblem));
	
		Agent[] sample;
		sample = pickSample(agent, sampleSize, referenceGroup);
		double aspirationLevel = estimateService(agent, sample, game, 
												 this.serviceDiscount);
		agent.setAspirationLevel(aspirationLevel);
		return aspirationLevel < stayProfit;
	}	

	/** 
	 * Finds a suitable problem solver. A "suitable problem solver" is the
	 * first agent that meets the aspiration level.
	 * 
	 * @param agent     the agent for which a problem solver shall be found
	 * @param suppliers the set of available suppliers. If the set of suppliers
	 *                  contains the agent itself, it is ignored.
	 * @param game      the trust game the agents play
	 * @return the agent that was found or none, if none of the 
	 *         agents could be trusted.
	 */
	protected Agent findSupplier(Agent agent, Collection<Agent> suppliers, TrustGame game) {		
		// step one: find someone that I trust and that meets my aspiration level
		for (Agent a : suppliers) {
			if (a != agent && agent.trust(a)) {
				if (game.customersReward(a.competence(agent.currentProblem)) > 
					agent.aspirationLevel) {
					return a;
				} 
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see hume.Matching#matchAgents(hume.Network, hume.TrustGame)
	 */
	@Override
	public Agent[][] matchAgents(Network network, TrustGame game) {
		LinkedList<Agent> remaining = new LinkedList<Agent>(network.setView); // Use a linked set instead?
		HashSet<Agent> problemAgents = new HashSet<Agent>();
		Agent pa, supp;
		
		// assign problems and construct two sets of agents, one with those agents
		// that do have a problem and one with those that don't
		
		int numPAgents = (int)(problemRatio * remaining.size());
		for (int i = 0; i < numPAgents; i++) {
			pa = remaining.remove(RND.random.nextInt(remaining.size()));
			pa.assignProblem(RND.random.nextInt(pa.competences.length));
			problemAgents.add(pa);
		}
		
		// Determine which agents stay at home
		
		Agent[]	referenceGroup = remaining.toArray(new Agent[remaining.size()]);
		Agent [][] ret = new Agent[numPAgents][2];
		Iterator<Agent> it = problemAgents.iterator();
		
		for (int i = 0; i < numPAgents; i++) {
			pa = it.next();
			ret[i][0] = pa;
			ret[i][1] = pa;
			if (!staysAtHome(pa, referenceGroup, game)) {
				supp = findSupplier(pa, remaining, game); 
				if (supp != null) {
					ret[i][1] = supp;
					remaining.remove(supp);
				}
			}
		}
		
		return ret;
	}
}

