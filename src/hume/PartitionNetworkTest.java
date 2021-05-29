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
 * Unit test case for class <code>PartitionNetwork</code>.
 * 
 * STATUS: provisional; documentation desired
 * 
 * @author eckhart
 *
 */
public strictfp class PartitionNetworkTest {
	Random random;
	PartitionNetwork pnw;
	
	@Before
	public void setUp() throws Exception {
		random = new Random(1000);
		pnw = new PartitionNetwork(Agent.factory, 227, 23);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIsNeighbor() {
		Set<Set<Agent>> nbhds = new HashSet<Set<Agent>>();
		for (Agent a: pnw.arrayView) {
			Set<Agent> nb = pnw.neighbors(a, 0.5);
			if (!nbhds.contains(nb)) {
				nbhds.add(nb);
				Set<Agent> st = pnw.strangers(a, 0.5);
					
				assertTrue(nb.equals(pnw.neighbors(a, Double.MIN_NORMAL)));
				assertTrue(st.equals(pnw.strangers(a, Double.MIN_NORMAL)));
			
				assertTrue(nb.equals(pnw.neighbors(a, 0.99)));
				assertTrue(st.equals(pnw.strangers(a, 0.99)));
				for (Agent b: nb) {
					assertTrue(pnw.isNeighbor(a, b, Double.MIN_NORMAL));
					assertTrue(pnw.isNeighbor(a, b, 0.99));
				}
				for (Agent b: st) {
					assertFalse(pnw.isNeighbor(a, b, Double.MIN_NORMAL));
					assertFalse(pnw.isNeighbor(a, b, 0.99));
				}				
			} 
 		}
	}

	@Test
	public void testDistance() {
		Agent[] agents = pnw.arrayView;
 		for (int i = 0; i < 1000; i++) {
			Agent a = agents[random.nextInt(agents.length)];
			Agent b = agents[random.nextInt(agents.length)];
			if (a == b) {
				assertTrue(pnw.distance(a, b) == 0.0);
			} else if (pnw.isNeighbor(a, b, 0.5)) {
				assertTrue(pnw.distance(a, b) <= Double.MIN_NORMAL);
			} else {
				assertTrue(pnw.distance(a, b) == 1.0);
			}
		}
	}

	@Ignore
	public void testPartitionNetworkSetOfSetOfAgent() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore
	public void testPartitionNetworkSetOfAgentInt() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testPartitionNetworkIntInt() {
		int x, y, agNum, nbNum;
		for (int i = 0; i < 10; i++) {
			x = random.nextInt(1000)+1; 
			y = random.nextInt(1000)+1;
			if (x > y) { agNum = x; nbNum = y; }
			else { agNum = y; nbNum = x; }
			
			PartitionNetwork nw = new PartitionNetwork(Agent.factory, agNum, nbNum);
			Set<Agent> allNeighbors = new HashSet<Agent>(), nb;
			int nbCount = 0;
			for (Agent a: nw.arrayView) {
				nb = nw.neighbors(a, 0.5);
				assertTrue(nb.size() > 0);
				if (!allNeighbors.contains(a)) {
					allNeighbors.addAll(nb);
					nbCount++;
				}	
			}
			assertTrue(nbCount == nbNum);
		}
	}

}
