/**
 * 
 */
package hume;

/**
 * Interface for classes that implement a learning algorithm.
 * 
 * @author eckhart
 */
public interface ILearning {
	
	/**
	 * Adjusts the agents propensities to enter the market, to trust and
	 * to reward according to a learning mechanism. The learning
	 * mechanism may take the network structure and all of the agent's
	 * properties into account.
	 * 
	 * @param network  the network
	 */
	public void learning(Network network);
}
