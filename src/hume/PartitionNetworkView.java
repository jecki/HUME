/**
 * 
 */
package hume;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.Color;
import java.util.*;
import java.lang.StrictMath;

/**
 * PartitionNetworkView displays a partition network on the screen
 *
 */
public strictfp class PartitionNetworkView implements IView {

	Simulation simulation;
	float fullDiameter, agentOffset, agentDiameter;
	HashMap<Agent, Point2D.Float> agentLocation;
	HashMap<Set<Agent>, Rectangle2D.Float> villageLocation;

	private Rectangle2D.Float createLocation(int side, float place, float extension) {
		assert side < 4;
		Rectangle2D.Float ret = new Rectangle2D.Float();
		if (side == 0) {
			ret.x = place;  
			ret.y = 0.0f + fullDiameter / 2.0f;
		} else if (side == 1) {
			ret.x = 1.0f - extension - fullDiameter / 2.0f;
			ret.y = place;
		} else if (side == 2) {
			ret.x = 1.0f - place - extension - fullDiameter / 2.0f;
			ret.y = 1.0f - extension - fullDiameter / 2.0f;
		} else if (side == 3) {
			ret.x = 0.0f + fullDiameter / 2.0f;
			ret.y = 1.0f - place - extension + fullDiameter / 2.0f;
		}
		ret.width = extension;
		ret.height = extension;
		return ret;
	}	
	
	public PartitionNetworkView(Simulation simulation) {
		this.simulation = simulation;
		agentLocation =  new HashMap<Agent, Point2D.Float>();
		villageLocation =  new HashMap<Set<Agent>, Rectangle2D.Float>();

		assert simulation.network instanceof PartitionNetwork;
		PartitionNetwork pn = (PartitionNetwork) simulation.network;
		ArrayList<Set<Agent>> ordered = new ArrayList<Set<Agent>>();
		Set<Agent> village;
		Rectangle2D.Float location;
		Point2D.Float position;
		int side;
		float place, extension ;
		int maxSize;
		
		maxSize = 0;
		for (Set<Agent> nh: pn.neighborhoods) {
			if (nh.size() > maxSize) maxSize = nh.size();
			int k = 0;
			while (k < ordered.size() && nh.size() > ordered.get(k).size()) 
				k++;
			ordered.add(k, nh);
		}
		int k = (pn.neighborhoods.size()+3)/4;
		int c = (int) (StrictMath.sqrt(maxSize)+0.9999);
		fullDiameter = (1.0f / ((k+2))) / ((float) c);
		agentOffset = fullDiameter / 6.0f;  
		agentDiameter = fullDiameter * 2.0f / 3.0f;
		for (int i = 0; i < pn.neighborhoods.size(); i++) {
			if (i % 2 == 0) village = ordered.remove(ordered.size()-1);
			else village = ordered.remove(0);
			side = i / k;
			place = ((float) (i % k)) / ((float) (k+1));
			c = (int) (StrictMath.sqrt(village.size())+0.9999);
			/* extension = (((float) village.size()) / ((float) maxSize)) /
					     ((float) (k+1)); */
			extension = fullDiameter * c;
			place += (1.0 / ((float) (k+1)) - extension) / 2;
			location = createLocation(side, place, extension);
			villageLocation.put(village, location);
			int n = 0, m = (int) (StrictMath.sqrt(village.size())+0.9999);
			for (Agent a: village) {
				position = new Point2D.Float();
				position.x = location.x + (n % m) * location.width / m;
				position.y = location.y + (n / m) * location.height / m;
				agentLocation.put(a, position);
				n++;
			}
		}		
	}
	
	/* (non-Javadoc)
	 * @see hume.IView#dataUpdatedNotification()
	 */
	public void dataUpdatedNotification() {
		assert false: "The network structure should never change during a simulation!";
	}

	/* (non-Javadoc)
	 * @see hume.IView#getName()
	 */
	public String getName() {
		return "Network";
	}

	
	void paintVillages(Graphics2D g, Rectangle bounds,
			           HashMap<Set<Agent>, Rectangle2D.Float> villageLocation) {
		RoundRectangle2D.Float rect = new RoundRectangle2D.Float();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
		                   RenderingHints.VALUE_ANTIALIAS_ON);			
		for(Rectangle2D.Float location: villageLocation.values()) {
			rect.x = location.x * bounds.width;
			rect.y = location.y * bounds.height;
			rect.width = location.width * bounds.width;
			rect.height = location.height * bounds.height;
			rect.arcwidth = 5;  rect.archeight = 5;
			g.draw(rect);
		}
	}
	
	void paintAgents(Graphics2D g, Rectangle bounds,
					 HashMap<Agent, Point2D.Float> agentLocation,
					 HashMap<Agent, Color> colorTable) {
		Ellipse2D.Float ellipse = new Ellipse2D.Float();
		Point2D.Float location;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
				           RenderingHints.VALUE_ANTIALIAS_ON);		
		// for (Point2D.Float location: agentLocation.values()) {
		for (Map.Entry<Agent, Point2D.Float> entry: agentLocation.entrySet()) {
			location = entry.getValue();
			ellipse.x = (location.x + agentOffset) * bounds.width;
			ellipse.y = (location.y + agentOffset) * bounds.height;
			ellipse.width = agentDiameter * bounds.width;
			ellipse.height = agentDiameter * bounds.height;
			if (colorTable != null) {
				g.setColor(colorTable.get(entry.getKey()));
			}
			g.fill(ellipse);
		}
	}
	/* (non-Javadoc)
	 * @see hume.IView#paint(java.awt.Graphics2D)
	 */
	public void paint(Graphics2D g) {
		Rectangle bounds = g.getClipBounds();
		g.setColor(Color.WHITE);
		g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		paintVillages(g, bounds, villageLocation);
		paintAgents(g, bounds, agentLocation, null);
	}

}
