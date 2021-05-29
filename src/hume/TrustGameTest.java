/**
 * 
 */
package hume;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * @author eckhart
 *
 */
public strictfp class TrustGameTest {
	private TrustGame tgList[] = null;
	private final double STEP = 0.01;	
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		tgList = new TrustGame[2];
		tgList[0] = new ReasonablePriceTG();
		tgList[1] = new FairShareTG();
	}	
	
	/**
	 * Test method for {@link hume.TrustGame#costs(double)}.
	 * 
	 * <code>cost</code> is a monotonically decrasing function
	 * greater zero.
	 */
	@Test
	public void testCost() {
		for (TrustGame tg : tgList) {
			double cmp = Double.MAX_VALUE;
			double cost;
			for (double c = STEP; c < 1.0; c += STEP) {
				cost = tg.costs(c);
				assertTrue("costs should decrease as competence increases", cost < cmp);
				assertTrue("costs must be greater zero", cost > 0);
				cmp = cost;
			}
		}
	}

	/**
	 * Test method for {@link hume.TrustGame#value(double)}.
	 * 
	 * <code>value</code> is a monotonically increasing function
	 * greater zero
	 */
	@Test
	public void testValue() {
		for (TrustGame tg: tgList) {
			double cmp = 0.0;
			double value;
			for (double c = STEP; c < 1.0; c += STEP) {
				value = tg.value(c);
				assertTrue("value should increase with competence", value > cmp);
				cmp = value;
			}		
		}
	}

	/**
	 * Test method for {@link hume.TrustGame#valueAdd(double)}.
	 * 
	 * surplus should be monotonicall increasing and  at least 
	 * for high competencies the surplus value should be positive
	 */
	@Test
	public void testSurplus() {
		for (TrustGame tg : tgList) {
			double cmp = -Double.MAX_VALUE;
			double surplus;
			for (double c = 0.5; c < 1.0; c += STEP) {
				surplus = tg.valueAdd(c);
				assertTrue("added value should be increasing with competence", 
						surplus > cmp);
				cmp = surplus;
				assertTrue("added Value should be greater zero for high competency values",
				           surplus > 0.0);			
			}		
		}
	}

	/**
	 * Test method for {@link hume.TrustGame#price(double)}.
	 * 
	 * payment should always be somewhere in between the value
	 * and the cost of the solution
	 */
	@Test
	public void testPrice() {
		for (TrustGame tg: tgList) {
			double value, costs, price;
			for (double c = 0.5; c < 1.0; c += STEP) {
				value = tg.value(c);
				costs = tg.costs(c);
				price = tg.price(c);
				assertTrue("value should be greater than payment for high competencies", value > price);
				assertTrue("price should be more than costs for high competencies", price > costs);
			}
		}
	}

	/**
	 * Test method for {@link hume.TrustGame#customersExploit(double)}.
	 * 
	 * The customer's payoff when eploited should always be less then when
	 * rewarded and monotonically decreasing with the supplier's competence.
	 */
	@Test
	public void testCustomersExploit() {
		for (TrustGame tg : tgList) {
			for (double c = STEP; c < 1.0; c += STEP) {
				assertTrue("being exploited should be worse than being rewarded", 
						tg.customersExploit(c) < tg.customersReward(c));
			}
			
			double cmp = tg.customersExploit(0.0), val;
			for (double c = STEP; c < 1.0; c += STEP) {
				val = tg.customersExploit(c);
				assertTrue("customers's exploit should be monotonically decreasing with its competence", 
						   val <= cmp);
				cmp = val;
			}			
		}
	}

	/**
	 * Test method for {@link hume.TrustGame#suppliersExploit(double)}.
	 * 
	 * The supplier's payoff from exploiting should always be higher
	 * than when rewarding and monotonically increasing with the comptetence
	 */
	@Test
	public void testSuppliersExploit() {
		for (TrustGame tg: tgList) {
			for (double c = STEP; c < 1.0; c += STEP) {
				assertTrue("exploiting should yield a higher payoff than rewarding", 
						tg.suppliersExploit(c) > tg.suppliersReward(c));
			}
			
			double cmp = tg.suppliersExploit(0.0), val;
			for (double c = STEP; c < 1.0; c += STEP) {
				val = tg.suppliersExploit(c);
				assertTrue("supplier's exploit should be monotonically increasing with its competence", 
						   val >= cmp);
				cmp = val;
			}			
		}
	}

	/**
	 * Test method for {@link hume.TrustGame#customersReward(double)}.
	 * 
	 * The customer's reward should always be positive (bigger than the 
	 * payoff of no interaction) and monotonically increasing with the supplier's comptence.
	 */
	@Test
	public void testCustomersReward() {
		for (TrustGame tg: tgList) {
			for (double c = 0.5; c < 1.0; c += STEP) {
				assertTrue("customer's reward should always be greater than zero for high competences", 
						tg.customersReward(c) > 0.0);
			}
			
			double cmp = tg.customersReward(0.0), val;
			for (double c = STEP; c < 1.0; c += STEP) {
				val = tg.customersReward(c);
				assertTrue("customers's reward should be monotonically increasing with its competence", 
						   val >= cmp);
				cmp = val;
			}
		}
	}

	/**
	 * Test method for {@link hume.TrustGame#suppliersReward(double)}.
	 * 
	 * The suppliers reward should always be positive (bigger than the
	 * payoff of no interaction) and monotonically increasing with competence.
	 */
	@Test
	public void testSuppliersReward() {
		for (TrustGame tg: tgList) {
			for (double c = 0.5; c < 1.0; c += STEP) {
				assertTrue("suppliers's reward should always be greater than zero for high competences",
						tg.suppliersReward(c) > 0.0);
			}	
			
			double cmp = tg.suppliersReward(0.0), val;
			for (double c = STEP; c < 1.0; c += STEP) {
				val = tg.suppliersReward(c);
				assertTrue("supplier's reward should be monotonically increasing with its competence", 
						   val >= cmp);
				cmp = val;
			}
		}
	}
}
