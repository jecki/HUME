/**
 *
 */
package hume;

import hume.Agent;

import java.util.Set;
import java.util.HashSet;

/**
 * The <code>Network</class> class is an abstract class for networks. Every network
 * can be understood as a fuzzy system of neighborhoods each of which contains
 * one or more agents. For the neighborhood relation the following conditions
 * hold:
 *
 * 1. fuzziness: for each pair of agents there is a <em>degree</em> of
 * 				 neighborship ranging from 0.0 to 1.0. A <em>neighborhood
 * 				 radius</em> defines the a maximally allowed degree of
 * 				 neighborship between two agents so that these agents count
 * 				 as "neighbors within the radius r" (or something of that sort)
 *
 *               Properties of the neighborship degree:
 *               a) the smaller the degree the stronger the neighborship, i.e.
 *                  if d1 < d2 then the set of agents which have at least a
 *                  neighborship degree of d1 is a subset of the set of agents
 *                  which share at least a neighborship degree of d2
 *               b) No two different agents have a neighborship degree of 0.0
 *                  to each other, i.e. a neighborhsip degree of 0.0 between two
 *                  agents means that it is in fact one and the same agent.
 *               c) Any two agents have a neighborhood degree of 1.0 or
 *                  smaller, i.e. any two agents are neighbors within a
 *                  neighborhood radius of 1.0.
 *
 * 2. reflexivity: Every agent is a neighbor of itself to the fullest degree
 * 3. symmetry:  If agent one is a neighbor of agent two, so is agent two of
 *               agent one to the same degree
 *
 * STATUS: mature, documented and tested; more testing always desirable...
 *
 * @author eckhart
 */
public abstract strictfp class Network {

	/** 
	 * Array <em>view</em> of all agents. Being considered a "view" 
	 * <code>arrayView</code> should never be changed or manipulated from
	 * outside the <code>Network</code> object. 
	 */ 
	public Agent[] arrayView;

	
	/**
	 * Set <em>view</em> of all agents. Being considered a "view" 
	 * <code>setView</code> should never be changed or manipulated from
	 * outside the <code>Network</code> object. 
	 */
	public Set<Agent> setView;

	
	/**
	 * Returns all agents which count as neighbors of <code>agent</code>
	 * within a neighborhood <code>radius</code>.
	 * @param agent	 the agent for which the neighbors are to be determined
	 * @param radius the neighborhood radius ranging from 0.0 to 1.0.
	 * 				 (0.0 = nobody else is a neighbor, 1.0 = everybody is a
	 *               neighbor)
	 * @return an <code>ArrayList</code> of agents which are neighbors of
	 * 		   the agent
	 */
	abstract public Set<Agent> neighbors(Agent agent, double radius);


	/**
	 * Returns all agents which are <em>not</em> neighbors of
	 * <code>agent</code> within a neighborhood <code>radius</code>.
	 * @param agent	 the agent for which the strangers are to be determined
	 * @param radius the neighborhood radius ranging from 0.0 to 1.0.
	 * 				 (0.0 = everybody else is a stranger,
	 *                1.0 = nobody else is a stranger)
	 * @return an <code>ArrayList</code> of agents which are strangers to
	 * 		   the agent
	 */
	public Set<Agent> strangers(Agent agent, double radius) {
		Set<Agent> agSet = new HashSet<Agent>(setView);
		agSet.removeAll(neighbors(agent, radius));
		return agSet;
	}


	/**
	 * Tests if two agents are neighbors within the given neighborhood
	 * radius.
	 * @param agentA first agent
	 * @param agentB second agent
	 * @param radius the neighborhood radius (0.0 = nobody else is a neighbor,
	 *               1.0 = everybody is a neighbor)
	 * @return true, if the agents are neighbors
	 */
	abstract public boolean isNeighbor(Agent agentA, Agent agentB, double radius);

	/**
	 * Returns the degree of neighborship of two agents. The degree of neighorship
	 * is the minimum radius under which they still count as neighbours.
	 *
	 * Note: In analogy to a distance relation the neighborship is the stronger, the
	 * smaller the degree number.
	 *
	 * @param agentA first agent
	 * @param agentB second agent
	 * @return the degree of neighborship ranging from 0.0 (identity)
	 * 		   to 1.0 (total strangers)
	 */
	abstract public double distance(Agent agentA, Agent agentB);
}

