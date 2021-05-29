/**
 * 
 */
package hume;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import java.util.Random;

/**
 * Test case for class <code>Network</code> and descendant classes.
 * 
 * STATUS: working; documentation desirable
 * 
 * @author eckhart
 *
 */
public strictfp class NetworkTest {
	final static int N = 3;
	Network[] nw = new Network[N];
	Random random;
	
	Agent[] randomPick(Agent[] agents, int n) {
		// does this algorrithm really work as intended?
		int range = agents.length;
		if (range < n) n = range;
		int[] ind = new int[n];
		for (int i = 0; i < n; i++) {
			ind[i] = random.nextInt(range-i);
			for (int k = i-1; k >= 0; k--) {
				if (ind[k] == ind[i]) ind[i] = range-1-i; 
			}
		}
		Agent[] ret = new Agent[n];
		for (int i = 0; i < n; i++) ret[i] = agents[ind[i]];
		return ret;
	}
	
	Agent[] randomPickOthers(Agent a, Agent[] agents, int n) {
		Agent[] pick = randomPick(agents, n+1);
		Agent[] ret = new Agent[n];
		for (int i = 0; i < n; i++) {
			if (pick[i] == a)
				ret[i] = pick[n];
		}
		return ret;
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		random = new Random(1000);
		nw[0] = new PartitionNetwork(Agent.factory, 287, 11);
		nw[1] = new PartitionNetwork(Agent.factory, 45, 30);
		nw[2] = new PartitionNetwork(Agent.factory, 60, 1);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link hume.Network#neighbors(hume.Agent, double)}.
	 */
	@Test
	public void testNeighbors() {
		Set<Agent> nb;
		for (Network n: nw) {
			String s = n.getClass().toString();
			for (Agent a: n.arrayView) {		// use random-pick instead to save time?
				nb = n.neighbors(a, 0.0);
				assertTrue(s, nb.size() == 1);
				assertTrue(s, nb.contains(a));
				nb = n.neighbors(a, 0.5);
				for (Agent b: nb) {
					assertTrue(s, n.isNeighbor(a, b, 0.5));
					assertTrue(s, n.distance(a, b) <= 0.5);
				}
				nb = n.neighbors(a, 1.0);
				assertTrue(s, nb.size() == n.arrayView.length);
			}
		}
	}

	/**
	 * Test method for {@link hume.Network#strangers(hume.Agent, double)}.
	 */
	@Test
	public void testStrangers() {
		Set<Agent> st;
		for (Network n: nw) {
			String s = n.getClass().toString();
			for (Agent a: n.arrayView) {		// use random-pick instead to save time?
				st = n.strangers(a, 0.0);
				assertTrue(s, st.size() == n.arrayView.length-1);
				assertFalse(s, st.contains(a));
				st = n.strangers(a, 0.5);
				for (Agent b: st) {
					assertFalse(s, n.isNeighbor(a, b, 0.5));
					assertTrue(s, n.distance(a, b) > 0.5);
				}
				st = n.strangers(a, 1.0);
				assertTrue(s, st.size() == 0);
			}
		}
	}

	/**
	 * Test method for {@link hume.Network#isNeighbor(hume.Agent, hume.Agent, double)}.
	 */
	@Test
	public void testIsNeighbor() {
		for (Network n: nw) {
			String s = n.getClass().toString();
			for (Agent a: n.arrayView) {
				assertTrue(s, n.isNeighbor(a, a, 0.0));
				assertTrue(s, n.isNeighbor(a, a, 0.4));
				assertTrue(s, n.isNeighbor(a, a, random.nextDouble()));				
				assertTrue(s, n.isNeighbor(a, a, 1.0));				
				for (Agent b: randomPickOthers(a, n.arrayView, 10)) {
					assertFalse(n.isNeighbor(a, b, 0.0));
					assertTrue(n.isNeighbor(a, b, 1.0));
					assertTrue(n.isNeighbor(a, b, 0.5) == n.isNeighbor(b, a, 0.5));
				}
			}
		}
	}

	/**
	 * Test method for {@link hume.Network#distance(hume.Agent, hume.Agent)}.
	 */
	@Test
	public void testDistance() {
		Set<Agent> nb, st;
		for (Network n: nw) {
			String s = n.getClass().toString();
			for (Agent a: n.arrayView) {
				double radius = random.nextDouble();
				nb = n.neighbors(a, radius);
				st = n.strangers(a,	radius);
				for (Agent b: nb) {
					double dist = n.distance(a, b);
					assertTrue(s, dist == n.distance(b, a));
					assertTrue(s, dist <= radius);
					if (a != b) {
						assertTrue(s, dist > 0.0);
					}
				}
				for (Agent b: st) {
					double dist = n.distance(a, b);
					assertTrue(s, dist == n.distance(b, a));
					assertTrue(s, dist > radius);
					assertTrue(s, a != b);
				}				
			}
		}		
	}

}
