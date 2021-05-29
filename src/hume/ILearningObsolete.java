/**
 * 
 */
package hume;

/**
 * Interface for the learning algorithm that agents use to update the 
 * variables that control their behaviour, i.e. the variables
 * <code>localTrustworthiness</code>, <code>marketTrustworthiness</code>,
 * <code>localTrust</code>, <code>maketTrust</code>.
 * 
 * <p>Any concrete learning algorithm must implement this interface. 
 * Implementors of this interface may implement some methods of this
 * interface with merely an empty method body. This would be suitable
 * if the learning algorithm does not allow to draw conclusions in
 * all possible cases. (For example, in a particular learning algorithm
 * it may not be intended to adjust any of the agents' variables if the
 * agent "stayed at home".)  
 * 
 * <p>STATUS: complete, but no implementing classes so far
 * a remaining problem is that there is no information regarding why an
 * agent stayed at home. Should this information matter for learning?
 * 
 * @author eckhart
 *
 */
public strictfp interface ILearningObsolete {
	/**
	 * Draws conclusions (i.e. adjusts its variables) for the agent 
	 * <code>agent</code>, if it decided to stay at home.
	 * @param agent     the agent that (potentially) learns something
	 * @param trustGame the trust game the agent would have played if it
	 *                  did not stay at home
	 */
	public void learnStayedAtHome(Agent agent, TrustGame trustGame);
	
	/**
	 * Draws conclusions (i.e. adjusts its variables) for the agent 
	 * <code>agent</code>, if it was rewarded in the role of the customer
	 * by the agent <code>other</code>. 
	 * @param agent the agent for which learning takes place
	 * @param other the supplier that rewarded the agent
	 * @param game  the trust game that the agents played
	 */
	public void learnRewardAsCustomer(Agent agent, Agent other, TrustGame game);
	
	/**
	 * Draws conclusions (i.e. adjusts its variables) for the agent 
	 * <code>agent</code>, if it rewarded in the role of the supplier
	 * by the agent <code>other</code>.  
	 * @param agent the agent for which learning takes place
	 * @param other the customer that was rewarded by the agent
	 * @param game  the game that the agents played
	 */
	public void learnRewardAsSupplier(Agent agent, Agent other, TrustGame game);
	
	/**
	 * Draws conclusions (i.e. adjusts its variables) for the agent 
	 * <code>agent</code>, if it was exploited in the role of the customer
	 * by the agent <code>other</code>. 
	 * @param agent the agent for which learning takes place
	 * @param other the supplier that exploited the agent
	 * @param game  the trust game that the agents played
	 */
	public void learnExploitAsCustomer(Agent agent, Agent other, TrustGame game);

	/**
	 * Draws conclusions (i.e. adjusts its variables) for the agent 
	 * <code>agent</code>, if it exploited in the role of the supplier
	 * by the agent <code>other</code>.  
	 * @param agent the agent for which learning takes place
	 * @param other the customer that was exploited by the agent
	 * @param game  the game that the agents played
	 */	
	public void learnExploitAsSupplier(Agent agent, Agent other, TrustGame game);
}
