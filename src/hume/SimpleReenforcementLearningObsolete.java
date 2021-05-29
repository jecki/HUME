/**
 * 
 */
package hume;

/**
 * Implements a <em>very</em> simple reenforcement learning algorithm.
 * 
 * <p>STATUS: not tested
 * 
 * @author eckhart
 *
 */
public strictfp class SimpleReenforcementLearningObsolete implements ILearningObsolete {
	
	/**
	 * Increases a probability value through some continuous mapping.
	 * There is no guarantee of any particular algorithm.
	 * @param probability the probability value that shall be amplified
	 * @return the amplified probability value
	 */
	protected double amplify(double probability) {
		return StrictMath.sqrt(probability);
	}
	
	/**
	 * Decreases a probability value through some continuous mapping.
	 * There is no guarantee of any particular algorithm.
	 * @param probability the probability value that shall be dampened
	 * @return the dampened probability value
	 */
	protected double dampen(double probability) {
		return probability * probability;
	}

	/* (non-Javadoc)
	 * @see hume.ILearning#learnExploitAsCustomer(hume.Agent, hume.Agent, hume.TrustGame)
	 */
	public void learnExploitAsCustomer(Agent agent, Agent other, TrustGame game) {
		if (Agent.network.isNeighbor(agent, other, Agent.neighborhoodRadius))
			agent.localTrust = dampen(agent.localTrust);
		else agent.marketTrust = dampen(agent.marketTrust);		
	}

	/* (non-Javadoc)
	 * @see hume.ILearning#learnExploitAsSupplier(hume.Agent, hume.Agent, hume.TrustGame)
	 */
	public void learnExploitAsSupplier(Agent agent, Agent other, TrustGame game) {
		if (Agent.network.isNeighbor(agent, other, Agent.neighborhoodRadius))
			agent.localTrustworthiness = dampen(agent.localTrustworthiness);
		else agent.marketTrustworthiness = dampen(agent.marketTrustworthiness);
	}

	/* (non-Javadoc)
	 * @see hume.ILearning#learnRewardAsCustomer(hume.Agent, hume.Agent, hume.TrustGame)
	 */
	public void learnRewardAsCustomer(Agent agent, Agent other, TrustGame game) {
		if (Agent.network.isNeighbor(agent, other, Agent.neighborhoodRadius))
			agent.localTrust = amplify(agent.localTrust);
		else agent.marketTrust = amplify(agent.marketTrust);	
	}

	/* (non-Javadoc)
	 * @see hume.ILearning#learnRewardAsSupplier(hume.Agent, hume.Agent, hume.TrustGame)
	 */
	public void learnRewardAsSupplier(Agent agent, Agent other, TrustGame game) {
		if (Agent.network.isNeighbor(agent, other, Agent.neighborhoodRadius))
			agent.localTrustworthiness = dampen(agent.localTrustworthiness);
		else agent.marketTrustworthiness = dampen(agent.marketTrustworthiness);
	}

	/* (non-Javadoc)
	 * @see hume.ILearning#learnStayedAtHome(hume.Agent, hume.TrustGame)
	 */
	public void learnStayedAtHome(Agent agent, TrustGame trustGame) {
		// cannot learn anything from staying at home...
	}

}
