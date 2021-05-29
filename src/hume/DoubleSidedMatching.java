/**
 * ETG Exchanges (HUME Draft 2.1.08, p. 14-15)
 */
package hume;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Matching algorithm for "double sided" matching. "Double sided matching"
 * means that agents (when deciding to find an external solution for their
 * problem rather than solving the problem by themselves) consider both 
 * the expected quality of an external solution and the expected earnings
 * when providing a solution for a problem of another agent (as compared to
 * solving their own problem by themselves and potentially achieving a better
 * quality but also foregoing the chance to earn something by providing a
 * solution for someone else). 
 * 
 * <p>The algorithm works as follows:
 * <ol>
 * <li><em>Every</em> agent is assigned a new problem.
 *     (method <code>matchAgents</code>)</li>
 * <li>Every agent decides whether to stay at home and solve the problem itself or not.
 *     (method <code>staysAtHome</code>)</li>
 *     <ol>
 *     <li>The agent determines the profit it receives when staying at home.</li>
 *     <li>The agent esitmates the quality of service it may recieve when
 *         someone else solves its problem by examining a sample of the potential
 *         suppliers (method <code>estimateService</code>). 
 *         (This estimate is later also considered to be the agent's aspiration level)</li>
 *         <ol>
 *         <li>The agent determines the mean payoff and the maximum payoff it
 *         would receive form those agents of the sample that it trusts.</li>
 *         <li>The estimated service quality is picked from the interval
 *         between the mean an the maximum possible service.</li>
 *         </ol>
 *     <li>The agent estimates the earnings it can receive when solving a problem
 *         for someone else (method <code>estimateEarnings</code>).</li>
 *         <ol>
 *         <li>From a sample of agents the agent picks those agents for which
 *         it might be a suitable problem solver (when its competence for solving
 *         the problem of that agent is more than average) and which might trust (where
 *         the probability with which the other agent trusts is assumed to be
 *         exactly the probability with which the agent would exploit the other
 *         agent).</li>
 *         <li>It determines the average and maximum earnings of these agents
 *         taking into account that it might still cheat them.</li>
 *         <li>The estimated earnings are picked from the interval between mean
 *         and maximum earnings.</li>
 *         </ol>
 *     <li>If the profit when staying at home is greater than the
 *         estimeted earnings plus the estimated service, the agent stays at home.</li>
 *     </ol>
 * <li>Every agent that does not stay at home searches a supplier that solves its problem.
 *     (method <code>findSupplier</code></li>)
 *     <ol>
 *     <li>From all agents of the reference group (for example the group of agents
 *     within a specific search radius) the first agent that is trusted and will
 *     deliver an equal or better result than the aspiration level is taken as
 *     supplier</li>
 *     <li>If no agent that meets this requirement exists the last agent that was
 *     trusted is taken as a "last resort".</li>
 *     <li>If no agent is trusted than no supplier is chosen.</li>
 *     </ol>
 * <li>The "match" for every agent is either its supplier or the agent itself,
 *     if it stays at home.</li>
 * </ol>
 * 
 * <p>STATUS: working, more testing and benchmarking needed, local class
 * <code>Accounting</code> is still provisional, should eventually be 
 * replaced by a better (simulation-global) solution.
 * 
 * @author eckhart
 *
 */
public strictfp class DoubleSidedMatching extends Matching {
	
	/**
	 * Provides services for the self-monitoring of the DoubleSidedMatching-
	 * Algroithm. As of this is very sketchy. More a hack than a solution.
	 * Probably some general "accounting" solution should be thought, since
	 * we want to monitor all components of the simulation during the simulation
	 * run...?
	 * @author eckhart
	 *
	 */
	public class Accounting {
		public int turnBacks = 0;
		public int lastResorts = 0;
		public HashMap<Agent, double[]> estimates = new HashMap<Agent, double[]>();
		
		void reset() {
			turnBacks = 0;
			lastResorts = 0;
			estimates = new HashMap<Agent, double[]>();
		}
		void stayingBefore(int n) { turnBacks = n; }
		void stayingAfter(int n) { 
			turnBacks = n - turnBacks; 
			}
		void lastResortSupplier() { lastResorts++; }
		void registerEstimates(Agent a, double service, double earnings) {
			double[] est = new double[2];
			est[0] = service;  est[1] = earnings;
			estimates.put(a, est);
		}
	}
	public static Accounting ac;
	
	public double sampleSize = 0.1;
	public double serviceDiscount = 0.5;
	public double earningsDiscount = 0.1;
	
	/**
	 * Creates a new DoubleSidedMatching object with standard parameters.
	 */
	public DoubleSidedMatching() { }
	
	public DoubleSidedMatching(double sampleSize, double serviceDiscount,
			                   double earningsDiscount) {
		this();
		this.sampleSize = sampleSize;
		this.serviceDiscount = serviceDiscount;
		this.earningsDiscount = earningsDiscount;
	}
	
	/**
	 * Estimates the earnings an agent may receive when offering
	 * its services to others (instead of solving its own problem) 
	 * 
	 * @param agent    the agent for which the revenue shall be estimated
	 * @param sample   the sample of agents which shall be used for estimation
	 * @param game     the game to be pleyed between the agent an a potential 
	 *                 customer.
	 * @param discount a discount value rangeing from 0.0 to 1.0 that specifies 
	 *                 where the actual estiamted value shall be chosen in 
	 *                 the interval from the mean eastimated value 
	 *                 (discount = 0.0) and the maximum estimated value
	 *                 (discount = 1.0)
	 * @return the estimated earnings for offering service
	 * 
	 */
	protected double estimateEarnings(Agent agent, Agent[] sample,
			                          TrustGame game, double discount) {
		int n = 0;
		double c, r, mean = 0.0, max = 0.0;
		
		for (Agent a: sample) {
			// am I a candidate for solving a certain problem
			c = agent.competence(a.currentProblem);
			if (c > 1.0/agent.competences.length) {
				// would the other agent trust me
				if (!agent.exploit(a)) {
					n += 1;
					if (agent.exploit(a)) r = game.suppliersExploit(c);
					else r = game.suppliersReward(c);
					if (r > max) max = r;
					mean += r;
				}
			}
		}
		if (n == 0) return 0.0; // does this occur often ??
		else {
			mean /= n;
			return mean + (max - mean) * discount;
		}
	}
	

	/**
	 * Returns true, if the agent would consider it more profitable to
	 * stay at home and solve its own problem, instead of offering
	 * its service to another agents and let its own problem be solved by
	 * someone else.
	 * 
	 * <p>Implicitely sets the aspiration level of the agents.
	 * 
	 * <p>Please note that the semantics of this method differs from
	 * the method with the same name from class SingleSidedMatching
	 * 
	 * @see SingleSidedMatching.staysAtHome
	 */
	protected boolean staysAtHome(Agent agent, Agent[] referenceGroup, 
			                      TrustGame game) {
		double stayProfit = game.valueAdd(agent.competence(agent.currentProblem));
	
		Agent[] sample;
		sample = pickSample(agent, sampleSize, referenceGroup);
		double aspirationLevel = estimateService(agent, sample, game, 
												 this.serviceDiscount);
		agent.setAspirationLevel(aspirationLevel);

		sample = pickSample(agent, sampleSize, referenceGroup);
		double expectedEarnings = estimateEarnings(agent, sample, game, 
												   this.earningsDiscount);
		
		ac.registerEstimates(agent, aspirationLevel, expectedEarnings);
		
		double expectedLeaveProfit = expectedEarnings + aspirationLevel;
		
		return expectedLeaveProfit < stayProfit;
	}	
	
	
	
	/** 
	 * Finds a suitable problem solver. A "suitable problem solver" is the
	 * first agent that meets the aspiration level or if none could be
	 * found the last agent that was at least trusted ("last resort problem solver"). 
	 * 
	 * @param agent     the agent for which a problem solver shall be found
	 * @param suppliers the set of available suppliers. If the set of suppliers
	 *                  contains the agent itself, it is ignored.
	 * @param game      the trust game the agents play
	 * @return the agent that was found or <code>null</code>, if none of the 
	 *         agents could be trusted.
	 */
	protected Agent findSupplier(Agent agent, Collection<Agent> suppliers, TrustGame game) {		
		// step one: find someone that I trust and that meets my aspiration level
		Agent lastAgent = null;
		for (Agent a : suppliers) {
			if (a != agent && agent.trust(a)) {
				if (game.customersReward(a.competence(agent.currentProblem)) > 
					agent.aspirationLevel) {
					return a;
				} else lastAgent = a;
			}
		}
		ac.lastResortSupplier();
		// isn't chosing a last resort supplier a bit like cheating?
		return lastAgent;
	}
	
	
	/* (non-Javadoc)(
	 * @see hume.Matching#matchAgents(hume.Network)
	 */
	@Override
	public Agent[][] matchAgents(Network network, TrustGame game) {
		ac = this.new Accounting();
		
		// assign a problem to every agent
		
		for (Agent a: network.arrayView) {
			a.assignProblem(RND.random.nextInt(a.competences.length));
     	}
		
		// determine which agents "stay at home"
				
		List<Agent> roamingAgents = new ArrayList<Agent>();
		List<Agent> stayingAgents = new ArrayList<Agent>();
		
		// at this spot changes are needed to take into account the differences
		// between the PM and GD scenario...
		Agent[] referenceGroup = network.arrayView;
		
		for (Agent a : network.arrayView) {
			if (staysAtHome(a, referenceGroup, game)) 
				stayingAgents.add(a); 
			else roamingAgents.add(a);
		}
		
		ac.stayingBefore(stayingAgents.size());
		
		// find a suitable supplier for every agent that
		// does not stay at home
		// if no supplier is found let the agent "stay at home"
		
		Map<Agent, Agent> backMap = new HashMap<Agent, Agent>();
		Set<Agent> availableSuppliers = new HashSet<Agent>(roamingAgents);
		
		while (roamingAgents.size() > 0) {
			Agent a = roamingAgents.remove(RND.random.nextInt(roamingAgents.size()));
			Agent supplier = findSupplier(a, availableSuppliers, game);
			if (supplier != null) {
				backMap.put(supplier, a);
				availableSuppliers.remove(supplier);
			} else {
				stayingAgents.add(a);
				Agent b = backMap.remove(a);
				if (b != null) roamingAgents.add(b);
			}
		}
		
		ac.stayingAfter(stayingAgents.size());
		
		// assemble the customer-supplier pairs of agents
		
		int size = network.arrayView.length;
		assert size == stayingAgents.size() + backMap.size();
		Agent ret[][] = new Agent[size][2];
		
		int i = 0;
		for (Agent a: stayingAgents) {
			ret[i][0] = a;
			ret[i][1] = a;
			i++;
		}
		for (Agent key: backMap.keySet()) {
			ret[i][1] = key;
			ret[i][0] = backMap.get(key);
			i++;
		}
		return ret;
	}
}

