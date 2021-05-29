/**
 * 
 */
package hume;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.HashSet;
import java.util.ArrayList;

/**
 * Test case for class <code>Matching</code> and its descending classes.
 * 
 * STATUS: working, but some tests could be made faster by using stochastic testing
 * instead of nested for loops; documentation desirable.
 * 
 * @author eckhart
 *
 */
public strictfp class MatchingTest {
	final static int NN = 1;
	final static int MN = 3;
	Network[] nw = new Network[NN];
	Matching[] matching = new Matching[MN];
	Random random;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		random = new Random(1000);
		RND.setSeed(5000);
		nw[0] = new PartitionNetwork(Agent.factory, random.nextInt(513)+22, random.nextInt(20)+1);
		matching[0] = new DoubleSidedMatching();
		matching[1] = new SingleSidedMatching();
		matching[2] = new MatrixMatching();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link hume.Matching#sample(int, int)}.
	 */
	@Test
	public void testSample() {
		int n = 20, range = 20; // boundary condition
		for (int i = 0; i < 100; i++) {
			if (n > range) { int x = n; n = range; range = x; }
			int[] sample = RND.sample(n, range);
			assertTrue(sample.length == n);
			for (int k = 0; k < sample.length; k++) {
				assertTrue(k >= 0 && k < range);
				for (int l = k-1; l >= 0; l--) assertTrue(k != l);
			}
			n = random.nextInt(1000)+1;
			range = random.nextInt(1000)+1;			
		}
	}

	/**
	 * Test method for {@link hume.Matching#sampleAgents(int, hume.Agent[])}.
	 */
	@Test
	public void testSampleAgents() {
		for (int i = 0; i < 20; i++) {
			for (Network n: nw) {
				int p = random.nextInt(50);
				Agent[] sample = Matching.sampleAgents(p, n.arrayView);
				assertTrue(sample.length == p);
				for (int k = 0; k < sample.length; k++) {
					for (int l = k+1; l < sample.length; l++) {
						assertTrue(sample[k] != sample[l]);
					}
				}
			}
		}
	}

	/**
	 * Test method for {@link hume.Matching#pickSample(hume.Agent, double, hume.Agent[])}.
	 */
	@Test
	public void testPickSample() {
		for (Network n: nw) {
			double p = ((double) n.arrayView.length-1) / ((double) n.arrayView.length);
			Agent a = n.arrayView[random.nextInt(n.arrayView.length)];
			Agent[] sample = Matching.pickSample(a, p, n.arrayView);
			assertTrue(sample.length == n.arrayView.length-1);
			for (int k = 0; k < sample.length; k++) {
				assertTrue(sample[k] != a);
				for (int l = k+1; l < sample.length; l++) {
					assertTrue(sample[k] != sample[l]);
				}
			}
		}
		
	}

	/**
	 * Test method for {@link hume.Matching#matchAgents(hume.Network, hume.FairShareTG)}.
	 * 
	 * Check that no agent is matched more than once!
	 */
	@Test
	public void testMatchAgents() {
		Agent[][] matches;
		TrustGame game = new FairShareTG();
		
		for (Network n: nw) {
			for (Matching m: matching) {
				HashSet<Agent> customers = new HashSet<Agent>();
				HashSet<Agent> suppliers = new HashSet<Agent>();				
				matches = m.matchAgents(n, game);
				for (Agent[] match: matches) {
					assertFalse(customers.contains(match[0]));
					assertFalse(suppliers.contains(match[1]));
					customers.add(match[0]);
					suppliers.add(match[1]);
				}
			}
		}
	}

}

