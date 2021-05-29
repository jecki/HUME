/**
 * 
 */
package hume;

import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Class <code>PartitionNetwork</code> defines a partition network topology, where
 * the world is a partition of neighborhods of agents. Agents within the same
 * neighborhood have network distance of epsilon (actually 
 * <code>Double.MIN_VALUE</code>). Agents from different neighborhoods are considered
 * "total" strangers, i.e. the network distance between them is 1. The agents partition-
 * nieghbors are thus neighbors within any neighborhood radius > 0 and < 1. 
 *
 * <p>Note: Class <code>PartitionNetwork</code> only defines a certain network topology,
 * it is not to be confused with the "Partition Market Scenario" which is not yet 
 * implemented and of which it only forms the basis.
 * 
 * STATUS: mature and tested.

 * @author eckhart
 *
 */
public strictfp class PartitionNetwork extends Network {
	/** A map that maps each agent to its respective neighborhood. */
	HashMap<Agent, Set<Agent>> partition;
	/** A list of neighborhoods */
	ArrayList<Set<Agent>> neighborhoods = new ArrayList<Set<Agent>>();

	/**
	 * Creates a new partition network from a given network structure.
	 * @param partition a set of sets of agents which is a
	 *                  partition, i.e. one and the same agent
	 *                  within any of these sets must not be a member
	 *                  of another set as well
	 */
	public PartitionNetwork(Set<Set<Agent>> partition) {
		assert false : "not yet implemented!";
	}
	
	/**
	 * Creates a new partition network from a previously given set of
	 * agents.
	 * @param agents  An array of agents which shall be spread over the 
	 *                partition network 
	 * @param numNhds the number of neighborhoods (must be smaller
	 *                or equal to the number of agents)
	 */
	public PartitionNetwork(Agent[] agents, int numNhds) {
		assert agents.length >= numNhds : "There must be at least as many "+
        								  "agents as neigbhorhoods.";	
		assert agents.length >= 1 && numNhds >= 1 : "There must be at least "+
                                             "one agent and one neighborhood";
		
		Set<Agent> nh;
		partition = new HashMap<Agent, Set<Agent>>();
		
		// make sure each neighborhood contains at least one agent
		for (int i = 0; i < numNhds; i++) {
			nh = new HashSet<Agent>();
			nh.add(agents[i]);
			partition.put(agents[i], nh);
			neighborhoods.add(nh);
		}
		
		// fill the neighborhoods randomly with agents
		for (int i = numNhds; i < agents.length; i++) {
			int n = RND.random.nextInt(numNhds);
			nh = partition.get(agents[n]);
			nh.add(agents[i]);
			partition.put(agents[i], nh);
		}

		setView = partition.keySet();
		arrayView = setView.toArray(new Agent[0]);		
	}
	
	/**
	 * (Obsolete) Creates a new partition network with a given number of agents and
	 * neighborhoods.
	 * 
	 * @param numAgents  the number of agents
	 * @param numNhds    the number of neighborhoods (must be equal to 
	 *                   or smaller than the number of agents)
	 */
	public PartitionNetwork(Agent.Factory agentFactory, int numAgents, int numNhds) {
		assert numAgents >= numNhds : "There must be at least as many "+
		                              "agents as neigbhorhoods.";
		assert numAgents >= 1 && numNhds >= 1 : "There must be at least "+
		                              "one agent and one neighborhood";
		
		Agent[] ag = new Agent[numAgents];
		Set<Agent> nh;
		partition = new HashMap<Agent, Set<Agent>>();
		
		// create agents
		Agent.network = this;
		for (int i = 0; i < numAgents; i++) ag[i] = agentFactory.create();
		
		// make sure each neighborhood contains at least one agent
		for (int i = 0; i < numNhds; i++) {
			nh = new HashSet<Agent>();
			nh.add(ag[i]);
			partition.put(ag[i], nh);
			neighborhoods.add(nh);
		}
		
		// fill the neighborhoods randomly with agents
		for (int i = numNhds; i < numAgents; i++) {
			int n = RND.random.nextInt(numNhds);
			nh = partition.get(ag[n]);
			nh.add(ag[i]);
			partition.put(ag[i], nh);
		}

		setView = partition.keySet();
		arrayView = setView.toArray(new Agent[0]);
	}
	
	
//	public Collection<HashSet<Agent>>getPartition() {
//		return partition.values();
//	}
		
	
	/* (non-Javadoc)
	 * @see hume.Network#distance(hume.Agent, hume.Agent)
	 */
	@Override
	public double distance(Agent agentA, Agent agentB) {
		if (agentA == agentB) return 0.0;
		else if (partition.get(agentA) == partition.get(agentB)) {
			return Double.MIN_VALUE;
		} else return 1.0;
	}

	/* (non-Javadoc)
	 * @see hume.Network#isNeighbor(hume.Agent, hume.Agent, double)
	 */
	@Override
	public boolean isNeighbor(Agent agentA, Agent agentB, double radius) {
		return distance(agentA, agentB) <= radius;
	}

	/* (non-Javadoc)
	 * @see hume.Network#neighbors(hume.Agent, double)
	 */
	@Override
	public Set<Agent> neighbors(Agent agent, double radius) {
		if (radius == 0.0) {
			HashSet<Agent> nb = new HashSet<Agent>(1);
			nb.add(agent);
			return nb;
			
		} else if (radius == 1.0) return new HashSet<Agent>(setView);
		else return partition.get(agent);
	}
}
