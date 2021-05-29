/**
 * 
 */
package hume;

import java.util.HashMap;
import java.util.ArrayList;

/**
 * Matching algorithm for "matrix matching". In its current implementation the
 * algorithm relies on the PM-scenario (see comments in the program code)! It
 * roughly works as follows:
 * 
 * <ol>
 * <li>To every agent the role of either a p-agent or an s-agent is assigned
 *     with a 50% chance.</li>
 * <li>The "search range" (i.e. whehter the agent enters the market is determined
 *     for every agent)</li>
 * <li>Candidate lists are prepared for all agents. An agent is a candidate for another 
 *     agent, if it has a different role and if both agents are possible matches 
 *     for each other. A pair of agents is a possible match, if a) they both search 
 *     within the same area (i.e. either locally in the same neighborhood or on the 
 *     market), b) the s-agents competence for solving the p-agents problem
 *     is higher than the p-agents competence for solving it by itself. and 
 *     c) the p-agent trusts the s-agent.</li>
 * <li>P-agents and S-agents are picked alternatingly and a suitable match
 *     is searched for the selected agent. This is done by randomly picking
 *     a partner from the candidate list that is not already matched. The
 *     probability of picking a specific partner is weighted by the expected
 *     profit. The profit is, in the case of the p-agent, the expected reward.
 *     In the case of an s-agent, it is either the reward of the exploitation
 *     payoff, depending on whether the s-agent decides to exploit.
 *     </li>    
 * </ol>
 * 
 * <p>STATUS: working, but accordance to the HUME model description needs to be
 * checked and unit tests are needed!
 * 
 * @author eckhart
 *
 */
public strictfp class MatrixMatching extends Matching {
	/**
	 * Objects of the local class <code>AgentState</code> store additional
	 * information about agents that is determined before the agents are matches.
	 * This information is their search range (i.e. do they stay at home or not?)
	 * and whether they are going to act as exploiters.
	 */
	enum MatchingStates { AVAILABLE, MATCHED };
	protected class AgentState {
		double   range;		// search range of the agent (local of market in PM)
		MatchingStates match = MatchingStates.AVAILABLE; // current matching state
		ArrayList<Agent> candidates = new ArrayList<Agent>();
	}

	/**
	 * Determines the search range of an agent. Currently, the algorithm
	 * employed is only meaningful for the Partition Network. It works
	 * by casting a random number r with 0 <= r <= <code>agent.localTrust +
	 * agent.marketTrust</code>. If <code>r > agent.lcoalTrust</code> then
	 * the agents search radius is 1.0, othiserwise it is neighborhood radius, 
	 * indicating that the agent is going to search for a partner only in the
	 * neighbourhood.
	 * 
	 * @param agent   the agent, whose search range is to be determined
	 * @return a double value from 0.0 to 1.0 that is the network distance
	 *         within which the agent searches for a partner
	 */
	protected double searchRange(Agent agent) {
		double threshold = agent.isCustomer ? agent.enterMarketCustomer : agent.enterMarketSupplier;
		if (RND.random.nextDouble() > threshold) 
			return Agent.neighborhoodRadius;
		else return 1.0;
	}

	public HashMap<Agent, Double> ranges = null; 
	/* (non-Javadoc)
	 * @see hume.Matching#matchAgents(hume.Network, hume.TrustGame)
	 */
	@Override
	public Agent[][] matchAgents(Network network, TrustGame game) {
		ranges = new HashMap<Agent, Double>();
		HashMap<Agent, ArrayList<Agent>> candidates = new HashMap<Agent, ArrayList<Agent>>();
		ArrayList<Agent> pAgents = new ArrayList<Agent>(); // ggf. besser als Mengentyp?
		ArrayList<Agent> sAgents = new ArrayList<Agent>(); // ggf. besser als Mengentyp?
		ArrayList<Agent[]> matches = new ArrayList<Agent[]>();
		
		// preparation for matching
		for (Agent a: network.arrayView) {	
			a.assignProblem(RND.random.nextInt(a.competences.length));			
			// assign role: pAgent or sAgent
			if (RND.random.nextDouble() >= 0.5) { 
				a.isCustomer = true;
				pAgents.add(a);
			} else { 
				a.isCustomer = false;
				sAgents.add(a);
			}

			// does the agent enter the market
			ranges.put(a, searchRange(a));
			
			// prepare candidate list
			candidates.put(a, new ArrayList<Agent>());
		}
		
		// populate candidate lists
		for (Agent p: pAgents) {
			for (Agent s: sAgents) {
				if ((ranges.get(s).equals(ranges.get(p))) &&  // This is reasonable only for the PM-scenario
					network.isNeighbor(p, s, ranges.get(p)) && p.trust(s) &&
					s.competence(p.currentProblem) > p.competence(p.currentProblem)) {
						candidates.get(p).add(s);
						candidates.get(s).add(p);
				}
			}
		}
		
		while (pAgents.size() > 0 || sAgents.size() > 0) {
			Agent[] match = new Agent[2];			
			if ((matches.size() % 2 == 0 && pAgents.size() > 0) || sAgents.size() == 0) {
				
				// randomly pick a p-agent
				int index = RND.random.nextInt(pAgents.size());
				Agent p = pAgents.remove(index);
				ArrayList<Agent> clist = candidates.get(p);
				
				// remove all s-agents from candidate list which are already matched
				for (Agent c: (ArrayList<Agent>) clist.clone()) {
					if (!sAgents.contains(c)) clist.remove(c);
				}
				if (clist.size() > 0) {
					// randomly pick a candidate. The probability of picking an agent
					// is weighted by the expected reward
					double[] rating = new double[clist.size()];
					for (int k = 0; k < clist.size(); k++) {
						double c = clist.get(k).competence(p.currentProblem);
						rating[k] = game.customersReward(c);
					}
					match[0] = p;
					match[1] = clist.get(RND.select(RND.normalized(rating)));
					pAgents.remove(match[0]);					
					sAgents.remove(match[1]);					
				} else {
					match[0] = p;
					match[1] = p;
					pAgents.remove(p);
				}
				matches.add(match);				
			} else {				
				int index = RND.random.nextInt(sAgents.size());
				Agent s = sAgents.remove(index);
				ArrayList<Agent> clist = candidates.get(s);				
				for (Agent c: (ArrayList<Agent>) clist.clone()) {
					if (!pAgents.contains(c)) clist.remove(c);
				}
				if (clist.size() > 0) {
					double[] rating = new double[clist.size()];
					for (int k = 0; k < clist.size(); k++) {
						Agent other = clist.get(k);
						if (s.exploit(other)) 
							rating[k] = game.suppliersExploit(s.competence(other.currentProblem));
						else 
							rating[k] = game.suppliersReward(s.competence(other.currentProblem));
					}
					match[0] = clist.get(RND.select(RND.normalized(rating)));
					match[1] = s;
					pAgents.remove(match[0]);					
					sAgents.remove(match[1]);
				} else {
					match[0] = s;
					match[1] = s;
					sAgents.remove(s);
				}
				matches.add(match);				
			}
		}
		Agent ret[][] = new Agent[matches.size()][2];
		// System.out.println("Debug");
		for (int i = 0; i < ret.length; i++) {
			ret[i] = matches.get(i);
			//for (int k = 0; k < i; k++) {
			//	if (ret[i][0] == ret[k][0] || ret[i][1] == ret[k][1]) System.out.println(i+" "+k);
			//}
		}
		return ret;
	}

}
