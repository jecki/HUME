/**
 * 
 */
package hume;

/**
 * The SimulationDefinition class contains all the information that is required to
 * set up a specific simulation. The information required encompasses: 
 * The number of agents, the initial values for the
 * agents variables, the number of agent competences, the type of interaction network, 
 * the defining parameters for the network, the type of situation (partition market
 * or grid distance scenario), the parameters of the trust game, the type of matching
 * algorithm and the type of learning algorithm.
 * 
 * <p>Apart from storing the information about the simulation setup, the
 * class SimulationData provides the services of verifying the correctness of
 * the setup data, and loading and saving the simulation data.
 * 
 * <p>The main reason for the introducing the class simulation data is to transfer
 * the setup data from the input user interface to the simulation class. 
 *  
 * <p>STATUS: data fields as far implemented as the programming of the
 *            simulation has proceeded so far; services not yet implemented,
 *            service methods may still change (i.e. use of streams instead
 *            of filenames as parameters etc.)
 *  
 * @author eckhart
 *
 */
public strictfp class SimulationDefinition {
	public String	name;
	
	public int 		numberOfAgents = 1000;
	
		// initial values for the agents' variables
		public int  	agentNumberOfCompetences = 20;	
		public double	agentLocalTrustworthiness = 0.9;
		public double   agentMarketTrustworthiness = 0.2;
		public double   agentLocalTrust = 0.8;
		public double	agentMarketTrust = 0.3;
		public double   neighborhoodRadius = 0.1;
	
	
	public String   network = "PartitionNetwork";
	
		// defining parameters for a partition network 
		public int		pnNumberOfNeighborhoods = 20;
	
	
	// type of situation (partition market, grid distance) not yet implemented
	// public String	situation = "GridDistance";
	
	public String   game = "FairShareTG"; // the type of game to be played
	
		// defining parameters for the trust game
		public double	tgSigma = 1.0;
		public double   tgPhi = 1.0;
		public double   tgBeta = 0.5;
	
	
	public String   matching = "MatrixMatching";
		// possible values: DoubleSidedMatching, SingleSidedMatching, MatrixMatching
	
		// defining parameters for double sided matching
		public double	dsmSampleSize = 0.1;
		public double   dsmServiceDiscount = 0.5;
		public double   dsmEarningsDiscount = 0.1;
		
		
	public String	learning = "RoleModelLearning";
		// possible values: RoleModelLearning
	
		//defining parameters for role model learning
		public double	rmlLearningProbability = 0.1;
		public double	rmlMutationProbability = 0.05;
		public double   rmlMutationAmplitude = 0.2;
	
	
	// service methods not yet implemented!
	
	/**
	 * Verifies the correctness of the data contained in this 
	 * SimulationData object. Returns true, if the data does
	 * define a valid simulation.
	 */
	public boolean verify() {
		return true; // not yet implemented
	}
	
	/**
	 * Saves the simulation setup data contained in this object to
	 * a file.
	 * @param fileName the name of the file to which the simulation 
	 *                 setup shall be saved
	 * @return true, if saving the data to disk was successful.
	 */
	public boolean save(String fileName) {
		return false; // not yet implemented
	}
	
	/**
	 * Loads the simulation setup data contained in this object from 
	 * a file.
	 * @param fileName the file name.
	 * @return true, if loading was successful.
	 */
	public boolean load(String fileName) {
		return false; // not yet implemented
	}
	
	/**
	 * Returns a description of the contents of SimulationData, i.e. the 
	 * parameters that control the simulation, in a nicely formatted,
	 * human readable form :)
	 */
	public String toString() {
		return "Sorry, but SimulationDefinition.report is not yet implemented!";
	}
}
