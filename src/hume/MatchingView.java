/**
 * 
 */
package hume;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

/**
 * @author eckhart
 *
 */
public class MatchingView implements IView {
	Simulation simulation;
	private PartitionNetworkView PNView; 
	HashMap<Agent, Color> colorTable;
	HashMap<Agent, Point2D.Float> agentLocation;
	boolean dirtyFlag = true;
	private Agent[][] matches;
	
	public static final Color NO_PARTNER  = new Color(1, 1, 1);
	public static final Color P_SERVED    = new Color(0, 255, 255);
	public static final Color P_EXPLOITED = new Color(255, 255, 0);
	public static final Color S_HONEST    = new Color(0, 0, 255);
	public static final Color S_EXPLOITER = new Color(255, 0, 0);
	
	public MatchingView(Simulation simulation) {
		this.simulation = simulation;
		if (simulation.network instanceof PartitionNetwork) {
			PNView = new PartitionNetworkView(simulation);
		}
		matches = new Agent[simulation.network.arrayView.length][2];
		for (int i = 0; i < matches.length; i++) {
			matches[i][0] = simulation.network.arrayView[i];
			matches[i][1] = matches[i][0];
		}
	}
	
	public synchronized void dataUpdatedNotification() { 
		this.matches = simulation.matches.clone();
		dirtyFlag = true;
	}
	
	public String getName() {
		return "Matching";
	}
	
	class SpiralCounter {
		Point2D.Float location;
		float gap;
		int counter, steps;
		int orientation;
		final int LEFT = 0, UP = 1, RIGHT = 2, DOWN = 3;
		SpiralCounter(float x, float y, float gap) {
			location = new Point2D.Float(x, y);
			this.gap = gap;
			this.steps = 1;
			this.counter = 1;
			this.orientation = LEFT;
		}
		Point2D.Float nextLocation() {
			Point2D.Float save = (Point2D.Float) location.clone();
			if (orientation == LEFT) {
				counter--;
				location.x += gap;
				if (counter <= 0) {
					orientation = UP;
					counter = steps;
				}
			} else if (orientation == UP) {
				counter--;
				location.y += gap;
				if (counter <= 0) {
					orientation = RIGHT;
					steps++;
					counter = steps;
				}
			} else if (orientation == RIGHT) {
				counter--;
				location.x -= gap;
				if (counter <= 0) {
					orientation = DOWN;
					counter = steps;
				}
			} else if (orientation == DOWN) {
				counter--;
				location.y -= gap;
				if (counter <= 0) {
					orientation = LEFT;
					steps++;
					counter = steps;
				}
			}
			return save;
		}
	}
	
	/**
	 * Evaluates agent's status in a partition network. This should be moved to a dedicated class!!!
	 */
	void evaluatePNData() {
		if (dirtyFlag) {
			colorTable = new HashMap<Agent, Color>();
			agentLocation = new HashMap<Agent, Point2D.Float>();
			SpiralCounter sc = new SpiralCounter(0.5f, 0.5f, PNView.fullDiameter);
			for (int i = 0; i < matches.length; i++) {
				if ( (((MatrixMatching) simulation.matching).ranges == null) ||
					 // (((MatrixMatching) simulation.matching).ranges.size() == 0) ||
				     (((MatrixMatching) simulation.matching).ranges.get(matches[i][0]) < 0.5) ) {
				   // if (simulation.network.isNeighbor(matches[i][0], matches[i][1], 0.5)) {
					if (matches[i][0] == matches[i][1]) {
						agentLocation.put(matches[i][0], 
										  PNView.agentLocation.get(matches[i][0]));
						colorTable.put(matches[i][0], NO_PARTNER);
					} else {
						agentLocation.put(matches[i][0], 
								  PNView.agentLocation.get(matches[i][0]));
						agentLocation.put(matches[i][1], 
								  PNView.agentLocation.get(matches[i][1]));						
						if (matches[i][1].exploit(matches[i][0])) {
							colorTable.put(matches[i][1], S_EXPLOITER);
							colorTable.put(matches[i][0], P_EXPLOITED);							
						} else {
							colorTable.put(matches[i][1], S_HONEST);
							colorTable.put(matches[i][0], P_SERVED);							
						}
					}
				} else {
					if (matches[i][0] == matches[i][1]) {
						agentLocation.put(matches[i][0], 
										  sc.nextLocation());
						colorTable.put(matches[i][0], NO_PARTNER);
					} else {					
						agentLocation.put(matches[i][0], sc.nextLocation());				
						agentLocation.put(matches[i][1], sc.nextLocation());
						if (matches[i][1].exploit(matches[i][0])) { 
							colorTable.put(matches[i][1], S_EXPLOITER);	
							colorTable.put(matches[i][0], P_EXPLOITED);	
						} else {
							colorTable.put(matches[i][1], S_HONEST);
							colorTable.put(matches[i][0], P_SERVED);							
						}
					}
				}
			}
			dirtyFlag = false;
		}
	}
	
	public void paint(Graphics2D g) {
		if (simulation.network instanceof PartitionNetwork) {
			evaluatePNData();
			Rectangle bounds = g.getClipBounds();
			PNView.paintVillages(g, bounds, PNView.villageLocation);
			PNView.paintAgents(g, bounds, agentLocation, colorTable);
		} else g.drawString("Not yet able to display this network type", 100, 100);
	}
}

