/**
 * 
 */
package hume;

import java.util.Set;

/**
 * Role model learning as described in ("Lernen in HUME 1.0", April, 11).
 * The algorithm works as follows:
 * <ol>
 * <li>For every agent the agent with the highest accumulated payoff in the
 *     agent's neighborhood, is consiedered its "role model".</li>
 * <li>With a certain, globally fixed probability the agent copies the set
 *     of behavior determining variables from its role model.</li>
 * <li>Independently, the agent, changes the set of its behavior determining
 *     variables with a certain mutation probability. If it does so the values
 *     of the respective variables are changed randomly with a certain amplitude</li>
 * </ol>
 * 
 * @author eckhart
 *
 */
public class RoleModelLearning implements ILearning {
	/**
	 * works only for PartitionNetworks so far!
	 */
	public double learningProbability = 0.1; // beta
	public double mutationProbability = 0.02; // mü
	public double mutationAmplitude = 0.3;   // delta
	
	private static Agent zombie;	
	
	public RoleModelLearning() {
		zombie = new Agent();
		zombie.accumulatedPayoff = -1000000.0;
	}
	
	public RoleModelLearning(double learningProbability, 
			                 double mutationProbability,
			                 double mutationAmplitude) {
		zombie = new Agent();
		zombie.accumulatedPayoff = -1000000.0;
		this.learningProbability = learningProbability;
		this.mutationProbability = mutationProbability;
		this.mutationAmplitude = mutationAmplitude;
	}
	
	protected double mutated(double value) {
		if (RND.random.nextDouble() <= 0.5) {	
			value += mutationAmplitude;
			if (value > 1.0) value = 1.0;			
		} else { 
			value -= mutationAmplitude;
			if (value < 0.0) value = 0.0;
		}
		return value;
	}
	
	public void learning(Network network) {
		PartitionNetwork pn = (PartitionNetwork) network;
		for (Set<Agent> nbhd: pn.neighborhoods) {
			Agent best = zombie;
			for (Agent a : nbhd)
				if (a.accumulatedPayoff > best.accumulatedPayoff) best = a;
			for (Agent a : nbhd) {
				if (a != best) {
					if (RND.random.nextDouble() <= learningProbability) {
						a.localTrust 		    = best.localTrust;
						a.localTrustworthiness  = best.localTrustworthiness;
						a.marketTrust			= best.marketTrust;
						a.marketTrustworthiness = best.marketTrustworthiness;
						a.enterMarketCustomer   = best.enterMarketCustomer;
						a.enterMarketSupplier   = best.enterMarketSupplier;
					}
				}
				if (RND.random.nextDouble() <= mutationProbability) {
					a.localTrust = mutated(a.localTrust);
					a.localTrustworthiness = mutated(a.localTrustworthiness);
					a.marketTrust = mutated(a.marketTrust);
					a.marketTrustworthiness = mutated(a.marketTrustworthiness);
					a.enterMarketCustomer = mutated(a.enterMarketCustomer);
					a.enterMarketSupplier = mutated(a.enterMarketSupplier);
				}
			}
		}
	}
}
