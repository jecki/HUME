/**
 * The simulation main class
 */
package hume;

import hume.Network;
import hume.FrontEnd;
import java.util.HashMap;

/**
 * The <code>Simulation<code/> runs and controls the whole simulation.
 * It also contains all global simulation parameters.
 *
 * SATUS: unfinished, but working!
 *
 * @author eckhart
 *
 */
public strictfp class Simulation implements ISimulation {
	/** The setup data for the simulation */
	SimulationDefinition setup;
	
	/** The front end for data and process output etc. */
	IOutputServices frontEnd;
	
	/** The trust game that is played between the agents */
	TrustGame game;	
	/** The interaction network. */
	Network network;	
	// situation still missing !!!
	/** The matching algorithm */
	Matching matching;
	/** the matches (always pairs of agents, 
	 *  an agent can be paired with itself) */
	Agent[][] matches;	
	/** The matching view */
	MatchingView matchingView;
	/** Output panel for certain simulation statistics */
	StatisticsView statisticsView;	
	/** The learning algorithm */
	ILearning learning;

	/** number of the current round */
	int roundCounter;

	/**
	 * Creates a new simulation with the given setup data.
	 * @param setup the setup data for the simulation
	 */
	public Simulation(SimulationDefinition setup, IOutputServices frontEnd) {
		this.frontEnd = frontEnd;
		newSimulation(setup);	
	}
	
	/**
	 * Stops a currently running simulation and creates a new simulation with the
	 * given setup data.
	 */
	public void newSimulation(SimulationDefinition setup) {
		this.setup = setup;
		
		Agent.numCompetences = setup.agentNumberOfCompetences;
		Agent.neighborhoodRadius = setup.neighborhoodRadius;
			
		final SimulationDefinition data = setup; // needed because of the anonymous class!?
		Agent.Factory factory = new Agent.Factory() {
			public Agent create() {
				return new Agent(data.agentLocalTrustworthiness,
			 			         data.agentMarketTrustworthiness,
						         data.agentLocalTrust,
						         data.agentMarketTrust); 
			}
		};
		if (setup.network == "PartitionNetwork") 
			network = new PartitionNetwork(factory, setup.numberOfAgents,
					                       setup.pnNumberOfNeighborhoods);
		
		if (setup.game == "FairShareTG")
			game = new FairShareTG(setup.tgSigma, setup.tgPhi, setup.tgBeta);
		else if (setup.game == "ReasonablePriceTG")
			game = new ReasonablePriceTG();
		else throw new IllegalArgumentException(setup.game + 
				                                " is not a known game!");
		
		if (setup.matching == "DoubleSidedMatching") 
			matching = new DoubleSidedMatching(setup.dsmSampleSize, 
					                           setup.dsmServiceDiscount, 
					                           setup.dsmEarningsDiscount);
		else if (setup.matching == "MatrixMatching")
			matching = new MatrixMatching();
			
		if (setup.learning == "RoleModelLearning") 
			learning = new RoleModelLearning(setup.rmlLearningProbability,
											 setup.rmlMutationProbability,
											 setup.rmlMutationAmplitude);
		
		// create views
		
		matchingView = new MatchingView(this);
		frontEnd.addView(matchingView);			
		
		statisticsView = new StatisticsView(this);
		frontEnd.addView(statisticsView);
		frontEnd.clearLogText();
		
		roundCounter = 0;
		RND.pickNewSeed();
	}
	
	
	/**
	 * Calculates the next round of the simulation and updates the 
	 * simulation output after the round is finishied.
	 * 
	 * <p>Each round constits of four statges:
	 * <ol>
	 *   <li>Assignment of problems to (at least) some agents.
	 *       (Technically this stage is merged with the matching
	 *       stage, because it depends on the matching algorithm
	 *       whether all or only some agents get a problem.)
	 *   <li>Matching of agents. Each agent that has a problem looks
	 *       for a suitable problem solve. The agent may also solve the
	 *       problem on its own.</li>
	 *   <li>Interaction and Learning: The agents interact, i.e. play
	 *       a trust game that simulates their letting someone else
	 *       solve their problems. After the interaction the agents "learn"
	 *       by adjusting their trustworthiness and trust levels depending
	 *       on the outcome of the interaction</li>
	 *   <li>The simulation output is updated. For example, the mean trust
	 *       and trustworthiness values of the agents are plotted on
	 *       some graph etc.</li>
	 * </ol>
	 */
	protected void nextRound() {
		roundCounter++;
		matching(); // includes problem assignment!
		matchingView.dataUpdatedNotification();
		statisticsView.dataUpdatedNotification();
		frontEnd.repaint(matchingView);
		interactionAndLearning();
		output();
	}
	
	/**
	 * Matching phase of the simulation.
	 */
	protected void matching() {
		// matching includes problem assignment
		matches = matching.matchAgents(network, game);
	}
	
	/**
	 * <p>Interaction and learning phase of the simulation.
	 * <p>As the p-agents have already decided during the
	 * matching stage, whether to trust their partner, it
	 * is now only up to the parter to decide whether to 
	 * exploit the agent it was trustedby.
	 * <p>Agents that solve problems (either their own,
	 * if they stayed at home or that of another if they
	 * were entrusted by another agent and didn't decide
	 * to exploit) will increase their competence in 
	 * solving the paricular problem they had to solve.
	 */
	protected void interactionAndLearning() {
		for (Agent[] pair : matches) {
			if (pair[0] == pair[1]) {
				pair[0].acquireCompetence(pair[0].currentProblem);				
				pair[0].receivePayoff(0);
			} else if (pair[1].exploit(pair[0])) {
				pair[0].receivePayoff(game.customersExploit(pair[1].competence(pair[0].currentProblem)));
				pair[1].receivePayoff(game.suppliersExploit(pair[1].competence(pair[0].currentProblem)));
			} else {
				pair[1].acquireCompetence(pair[0].currentProblem);
				pair[0].receivePayoff(game.customersReward(pair[1].competence(pair[0].currentProblem)));
				pair[1].receivePayoff(game.suppliersReward(pair[1].competence(pair[0].currentProblem)));
			}
		}
		learning.learning(network);
	}

	/**
	 * Ouput phase of the simulation (not yet implemented).
	 */
	protected void output() { }
	
	// Interface ISimulation
	public String getName() {
		return setup.name;
	}
	public String getDescription() {
		return setup.toString();
	}
	
	// simulation control
	public void reset() {
		frontEnd.removeView(matchingView);
		frontEnd.removeView(statisticsView);
		newSimulation(setup);
		frontEnd.repaint(matchingView);
	}
	
	public void step() {
		nextRound();
		String log = "Round "+roundCounter+" :\n";
		log += "Ratio of agent's that enter the market: " + 
		       statisticsView.marketTraders.get(statisticsView.marketTraders.size()-1) + "\n";
		log += "Ratio of local agent's that do not interact: " + 
		       statisticsView.localNonInteractors.get(statisticsView.localNonInteractors.size()-1) + "\n";
		log += "Ratio of agent's on the market that do not interact: " +
		       statisticsView.marketNonInteractors.get(statisticsView.marketNonInteractors.size()-1) + "\n";
		log += "Ratio of local customers that were exploited: " +
		       statisticsView.localExploits.get(statisticsView.localExploits.size()-1) + "\n";
		log += "Ratio of market customers that were exploited: " +
	           statisticsView.marketExploits.get(statisticsView.marketExploits.size()-1) + "\n";
		frontEnd.addLogText(log);		
	}
	
	public boolean finished() {
		return false;
	}
	
	public void terminate() {
		;	
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HashMap<String, SimulationDefinition> setups = new HashMap<String, SimulationDefinition>();
		setups.put("Matrix Matching", new SimulationDefinition());
		SimulationDefinition sd = new SimulationDefinition();
		// sd.matching = "DoubleSidedMatching";
		// setups.put("DoubleSidedMatching", sd);
		// sd = new SimulationDefinition();
		// sd.matching = "SinlgeSidedMatching";
		// setups.put("SingleSidedMatching", sd);
		sd = new SimulationDefinition();
		sd.pnNumberOfNeighborhoods = 45;
		sd.numberOfAgents = 5000;
		setups.put("Many Agents", sd);
		new FrontEnd(setups);
		// Simulation sim = new Simulation(new SimulationDefinition(), frontEnd);
	}
}
