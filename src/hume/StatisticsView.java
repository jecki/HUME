/**
 * 
 */
package hume;

import java.awt.Graphics2D;
import java.awt.Color;
import org.math.plot.Plot2DPanel;
import java.util.ArrayList;

/**
 * This class gathers and plots the statistics for the HUME 1.0 simulation. At
 * present stage it is only a stub. Most importantly, the gathering and aggregation
 * of the relevant data should be moved to another class.
 *  
 * @author eckhart
 */
public class StatisticsView implements IPlotter {
	Simulation simulation;
	Plot2DPanel plotter = null;
	ArrayList<Double> marketTraders = new ArrayList<Double>();
	ArrayList<Double> localNonInteractors = new ArrayList<Double>();
	ArrayList<Double> marketNonInteractors = new ArrayList<Double>();
	ArrayList<Double> localExploits = new ArrayList<Double>();
	ArrayList<Double> marketExploits = new ArrayList<Double>();
	
	public StatisticsView(Simulation simulation) {
		this.simulation = simulation;
	}
	
	/**
	 * Plots an array of double values between 0.0 and 1.0.
	 * @param data The array of double values.
	 * @param description The title to be entered in the legend.
	 */
	protected void plot(ArrayList<Double> data, String description) {
		double yval[] = new double[data.size()];
		double xval[] = new double[yval.length];
		for (int i = 0; i < yval.length; i++) {
			xval[i] = i;
			yval[i] = data.get(i);
		}
		plotter.addLinePlot(description, xval, yval);
		plotter.setFixedBounds(1, 0.0, 1.0);
	}
	
	/* (non-Javadoc)
	 * @see hume.IView#dataUpdatedNotification()
	 */
	public void dataUpdatedNotification() {
		int mt = 0, lNI = 0, mNI = 0, lE = 0, mE = 0;
		// gather the number of market traders
		// RECOMMENDATION: move the gathering of statistics to a dedicated class!
		simulation.matchingView.evaluatePNData();
		if (simulation.matchingView.colorTable == null) return;
		for (Agent[] m : simulation.matches) {
			Color status = simulation.matchingView.colorTable.get(m[0]);
			if ( (((MatrixMatching) simulation.matching).ranges != null) &&
				 (((MatrixMatching) simulation.matching).ranges.get(m[0]) >= 0.5) ) {
			 //if (!simulation.network.isNeighbor(m[0], m[1], 0.5)) {
				if (m[0] != m[1]) mt += 2;
				else mt += 1;
				if (status == MatchingView.P_EXPLOITED) 
					mE += 1;
				else if (status == MatchingView.NO_PARTNER) 
					mNI += 1;
			} else {
				if (status == MatchingView.P_EXPLOITED) 
					lE += 1;
				else if (status == MatchingView.NO_PARTNER) 
					lNI += 1;
			}
		}
		int numAgents = simulation.network.arrayView.length;
		marketTraders.add((double) mt / (double) numAgents);
		localNonInteractors.add((double) lNI / (double) (numAgents - mt));
		marketNonInteractors.add((double) mNI / (double) (mt));
		localExploits.add((double) lE / (double) ((numAgents - mt - lNI)/2));
		marketExploits.add((double) mE / (double) ((mt - mNI)/2));
		plotter.removeAllPlots();
		plot(marketTraders, "market traders");
		plot(localNonInteractors, "local agents w/o partner");
		plot(marketNonInteractors, "market agents w/o partner");
		plot(localExploits, "local exploitation");
		plot(marketExploits, "market exploitation");
	}

	/* (non-Javadoc)
	 * @see hume.IView#getName()
	 */
	public String getName() {
		// TODO Auto-generated method stub
		return "Statistics";
	}

	/* (non-Javadoc)
	 * @see hume.IView#paint(java.awt.Graphics2D)
	 */
	public void paint(Graphics2D g) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see hume.IPlotter#assignPlotter(org.math.plot.Plot2DPanel)
	 */
	public void assignPlotter(Plot2DPanel plotter) {
		this.plotter = plotter;
		plotter.setFixedBounds(1, 0.0, 1.0);
		plotter.addLegend("SOUTH");
		plotter.addPlotToolBar("NORTH");
		plotter.setAxisLabels("step", "%-ratio");
	}

}
