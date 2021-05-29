/**
 * 
 */
package hume;

/**
 * @author eckhart
 *
 */
public interface ISimulation {
	void reset();
	void step();
	boolean finished();
	void terminate();
}
