/**
 * 
 */
package hume;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import java.util.Random;
import java.util.Set;
import java.util.HashSet;

/**
 * Test case for the class <code>DoubleSidedMatching</code>. This unit test is to be
 * considered as an extension to <code>MatchingTest</code> in order to test specific
 * features of the implementation of double sided matching.
 * 
 * STATUS: still provisional, test for method <code>staysAtHome<code> needed,
 * better tests and documentation desirable.
 * 
 * @author eckhart
 *
 */
public strictfp class DoubleSidedMatchingTest {
	Network nw = new PartitionNetwork(Agent.factory, 433, 27);
	TrustGame game = new FairShareTG();
	DoubleSidedMatching matching = new DoubleSidedMatching();
	Random random;

	void initAgent(Agent a) {
		a.localTrust = 0.9;
		a.marketTrust = 0.3;
		a.localTrustworthiness = 0.85;
		a.marketTrustworthiness = 0.35;
		for (int i = 0; i < Agent.numCompetences; i++) {
			a.competences[i] = 1.0-random.nextDouble();
		}
		a.normalizeCompetences();
		a.assignProblem(random.nextInt(Agent.numCompetences));		
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		random = new Random(1000);
		RND.setSeed(5000);
		for (Agent a : nw.arrayView) initAgent(a);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link hume.DoubleSidedMatching#matchAgents(hume.Network, hume.FairShareTG)}.
	 */
	@Test
	public void testMatchAgents() {
		Matching matching = new DoubleSidedMatching();
		Agent[][] pairs = matching.matchAgents(nw, game);
		assertTrue(pairs.length == nw.arrayView.length);
		
		Set<Agent> customers = new HashSet<Agent>(nw.setView);
		Set<Agent> suppliers = new HashSet<Agent>(nw.setView);
		for (Agent[] match: pairs) {
			assertTrue(customers.remove(match[0]));
			assertTrue(suppliers.remove(match[1]));
		}
	}

	/**
	 * Test method for {@link hume.DoubleSidedMatching#estimateService(hume.Agent, hume.Agent[], hume.FairShareTG, double)}.
	 */
	@Test
	public void testEstimateService() {
		double value1 = 0.0, value2 = 0.0, comp, c;
		for (Agent a: nw.arrayView) {
			comp = 0.0;
			for (Agent b: nw.arrayView) {
				if (a != b) {
					c = game.customersReward(b.competence(a.currentProblem));
					if (c > comp) comp = c;
				}
			}
			assertTrue(comp >= matching.estimateService(a, nw.arrayView, game, 0.5));
		}
		
		for (int i = 0; i < 10; i++) {
			for (Agent a: nw.arrayView) {
				value1 += matching.estimateService(a, 
						nw.neighbors(a, 0.5).toArray(new Agent[0]), game, 0.5);
				value2 += matching.estimateService(a, nw.arrayView, game, 0.5);
			}
		}
		value1 /= 10*nw.arrayView.length;
		value2 /= 10*nw.arrayView.length;
		assertTrue(value2 >= value1);		
	}

	/**
	 * Test method for {@link hume.DoubleSidedMatching#estimateEarnings(hume.Agent, hume.Agent[], hume.FairShareTG, double)}.
	 */
	@Test
	public void testEstimateEarnings() {
		double value1 = 0.0, value2 = 0.0;
		for (int i = 0; i < 10; i++) {
			for (Agent a: nw.arrayView) {
				value1 += matching.estimateEarnings(a, 
						nw.neighbors(a, 0.5).toArray(new Agent[0]), game, 0.1);
				value2 += matching.estimateEarnings(a, nw.arrayView, game, 0.1);
			}
		}
		value1 /= 10*nw.arrayView.length;
		value2 /= 10*nw.arrayView.length;
		assertTrue(value2 >= value1);	
	}

	/**
	 * Test method for {@link hume.DoubleSidedMatching#staysAtHome(hume.Agent, hume.Agent[], hume.FairShareTG)}.
	 */
	@Ignore
	public void testStaysAtHome() {
		fail("Test not implemented!");
	}

	/**
	 * Test method for {@link hume.DoubleSidedMatching#findSupplier(hume.Agent, java.util.Set, hume.FairShareTG)}.
	 */
	@Test
	public void testFindSupplier() {
		for (Agent a: nw.arrayView) {
			initAgent(a);
			a.localTrust = 1.0;
			a.marketTrust = 1.0;
		}
		for (Agent a: nw.arrayView) 
			matching.staysAtHome(a, nw.arrayView, game);
		HashSet<Agent> suppliers = new HashSet<Agent>(nw.setView);
		for (Agent a: nw.arrayView) {
			assertTrue(suppliers.size()+" ", matching.findSupplier(a, suppliers, game) != null);
		}
	}
}

