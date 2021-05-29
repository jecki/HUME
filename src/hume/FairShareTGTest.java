/**
 * 
 */
package hume;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * <code>TrustGameTest</code> contains the <em>unit test</em> for trust
 * game. It checks whether the actual implementation of trust
 * game always fullfills certain conditions one should expect from
 * our trust game, i.e. that rewards are better than no interaction
 * or that the costs decrease as competence increases etc.
 * 
 * STATUS: mature.
 * 
 * @author eckhart
 *
 */
public strictfp class FairShareTGTest {
	private TrustGame tg = new FairShareTG();
	private final double STEP = 0.01;
	
	/**
	 * Test method for {@link hume.FairShareTG#price(double)}.
	 * 
	 * payment should always be somewhere in between the value
	 * and the cost of the solution
	 */
	@Test
	public void testPrice() {
		double value, costs, payment;
		for (double c = STEP; c < 1.0; c += STEP) {
			value = tg.value(c);
			costs = tg.costs(c);
			payment = tg.price(c);
			assertTrue("value should be greater than payment", value > payment);
			assertTrue("payment should be more than costs", payment > costs);
		}
	}
}
