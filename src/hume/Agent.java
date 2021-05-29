/**
 *
 */
package hume;


/**
 * The <code>Agent</code> class defines the agent's properties and their
 * behaviour.
 *
 * @author eckhart
 */
public strictfp class Agent {	
	/** Ther upper limit for the number of problems (Draft 28.11, p.6). Other than
	 * in the draft the problems are numbered from zero to numProblems-1. Starting to
	 * count from zero simplifies indexing. */	
	public static int numCompetences = 20;
	public static Network network = null;
	public static double neighborhoodRadius = 0.1;
	public static double payoffDiscount = 0.9;
	
	public double competences[];
	public int currentProblem;
	
	double localTrustworthiness = 0.9;
	double marketTrustworthiness = 0.2;
	
	double localTrust = 0.8;
	double marketTrust = 0.3;
	
	double enterMarketCustomer = 0.6;
	double enterMarketSupplier = 0.5;

	double aspirationLevel = -1.0;  // a negative value means: not assigned
	int	   localExploiter  = -1;    // -1 = not assigned, 0 = no, 1 = yes
	int    marketExploiter = -1;
	
	boolean isCustomer; // must be set by the matching algorithm
	double accumulatedPayoff = 0.0;
 
	
	/**
	 * The agent factory interface contains only the method <code>create</code> to
	 * create new agents. It is meant to be implemented in anonymous local
	 * classes that can be passed to the methods of objects that need to create
	 * agents (i.e. network constructors) without knowing with what parameters to
	 * create these agents.	 	 
	 */
	public interface Factory {
		Agent create();
	}
	/**
	 * Standard Agent factory, which uses the parameterless default constructor
	 * to create Agent objects.	 
	 */
	public static Factory factory = new Factory() {
		public Agent create() { return new Agent();	} }; 
	
	/**
	 * Constructs an agent object. Before constructing an agent object the
	 * static fields <code>numCompetences, network</code> must be assigned 
	 * with suitable values.
	 */
	public Agent() {
		assert numCompetences > 0;
		competences = new double[numCompetences];
		for (int i = 0; i < numCompetences; i++) competences[i] = 10.0 + RND.random.nextDouble(); // competences[i] = 1.0 / numCompetences;
		normalizeCompetences();
	}	

	/**
	 * Constructs an agent object with the given trustworthiness and trust levels. 
	 * Before constructing an agent object the
	 * static fields <code>numCompetences, network</code> must be assigned 
	 * with suitable values.
	 */	
	public Agent(double localTrustworthiness, double marketTrustworthiness, 
				 double localTrust, double marketTrust) {
		this();
		this.localTrustworthiness = localTrustworthiness;
		this.marketTrustworthiness = marketTrustworthiness;
		this.localTrust = localTrust;
		this.marketTrust = marketTrust;
	}
	
	/**
	 * Returns the agent's competence for solving a certain problem.
	 */
	public double competence(int problem) {
		return competences[problem];
	}
	
	/**
	 * Normalizes the competences vector so that its components add up to
	 * one.
	 */
	public void normalizeCompetences() {
		double sum = 0.0;
		for (double f : competences) {
			sum += f;
		}
		for (int i = 0; i < numCompetences; i++) competences[i] /= sum;
	}

    /**
     * Increases the competence for solving a certain problem at the expense
     * of the competences for solving other problems. (Draft 28.11., p. 7)
     *
     * @param problem the problem for the solution of which the competence
     * 		          shall be increased.
     */
	protected void acquireCompetence(int problem) {
		competences[problem] += 0.1; 	// so far, delta is arbitrarily set to 0.1 !
		normalizeCompetences();
	}

	/**
	 * A step functions that determines whether to use the local
	 * trustworthiness or the market trustworthiness depending on
	 * the distance (towards another agent) and the neighborhood radius.
	 *
	 * @param distance
	 * @param radius
	 * @return the probability of being trustworthy
	 */
	protected double stepFunction(double distance, double radius) {
		assert distance >= 0.0 && distance <= 1.0;
		if (distance > radius) return marketTrustworthiness;
		else return localTrustworthiness;
	}
	
	/**
	 * Assings a new problem to the agent. This method also implicitely notifies
	 * the agent that a new round has started. The agent resets all round-specific
	 * variables within this method.
	 * 
	 * @param problem the problem that occured to the agent
	 */
	public void assignProblem(int problem) {
		assert problem < numCompetences;
		currentProblem = problem;
		// clear round specific variables:
		aspirationLevel = -1.0;	
		localExploiter = -1;
		marketExploiter = -1;
	}
	
	/**
	 * Sets the aspirationLevel of the agent. As determining the
	 * aspiration level is part of the matching procdure, the 
	 * algorithm that determines the aspiration level is implemented
	 * in a descendant of the <code>Matching</code> class and is 
	 * suppoed to invoke the agent's method <code>setAspirationLevel</code>
	 * as appropriate.
	 * @param aspirationLevel the aspiration level of the agent regarding
	 * 		  an external solution of its problem.
	 */
	public void setAspirationLevel(double aspirationLevel) {
		assert this.aspirationLevel < 0.0: "aspiration level should be "+
        "determined only once per round after the assignment of a problem!";
		assert currentProblem >= 0: "An agent that does not have a problem " +
				                    "cannot have an aspiration level either!";
		this.aspirationLevel = aspirationLevel;
	}
	
	/**
	 * Returns true, if the agent is willing to trust the other agent.
	 * Presently, only a very simple algorithm is implemented which 
	 * assumes that the agent is able to "smell" the trustworthiness
	 * of the other agent with some degree of reliability. The actual
	 * propensity to trust the other agent is to one half determined
	 * by the trust level of the agent and to one half by the actual
	 * trsutworthiness of the other agent. (Both, the agent's trust
	 * level and the the other agent's trustworthiness level do
	 * of course depend on the network distance of the agents.)
	 * Thus: propensity = 0.5 * trust + 0.5 * trustworthiness
	 * 
	 * @param other the other agent
	 */
	public boolean trust(Agent other) {
//		if (network.distance(this, other) > neighborhoodRadius) {
//			return (RND.random.nextDouble() < marketTrust); 
//		} else {
//			return (RND.random.nextDouble() < localTrust);
//		}
		
		double trust, trustworthiness, propensity;
		if (network.distance(this, other) > neighborhoodRadius) {
			trust = marketTrust;
			trustworthiness = other.marketTrustworthiness;
		} else {
			trust =  localTrust;
			trustworthiness = other.localTrustworthiness;
		}
		propensity = 0.5 * trust + 0.5 * trustworthiness;
		return (RND.random.nextDouble() <= propensity);	
	}
	
	/**
	 * Returns true, if the agent will exploit the other agent.
	 * The result returned is always the same during one round,
	 * i.e. the agent decides only whether to be an exploiter or
	 * not only the first time the method <code>exploit</code> is
	 * called. After that it always sticks with this decision until
	 * a new round is started (i.e. a new problem is assigned).
	 * @param other the other agent
	 */	
	public boolean exploit(Agent other) {
		return exploit(network.distance(this, other));
	}

	/**
	 * Returns true, if the agent will exploit an agent to which
	 * it has the given network-<code>distance</code>.
	 * The result returned is always the same during one round.
	 * i.e. the agent decides only whether to be an exploiter or
	 * not only the first time the method <code>exploit</code> is
	 * called. After that it always sticks with this decision until
	 * a new round is started (i.e. a new problem is assigned). 
	 * @param distance  the network distance to determine exploitation.
	 */		
	public boolean exploit(double distance) {
		if (distance > neighborhoodRadius) {
			if (marketExploiter == -1) {
				if (RND.random.nextDouble() < marketTrustworthiness)
					marketExploiter = 1;
				else marketExploiter = 0;
			}
			if (marketExploiter == 1) return true;
			else return false;
		} else {
			if (localExploiter == -1) {
				if (RND.random.nextDouble() < localTrustworthiness)
					localExploiter = 1;
				else localExploiter = 0;
			}
			if (localExploiter == 1) return true;
			else return false;			
		}
	}
	
	/**
	 * Updates the variable <code>accumulatedPayoff</code> with
	 * the payoff received in the current round according
	 * to the forumula:
	 * <code>
	 * accumulatedPayoff = accumulatedPayoff * payoffDiscount + payoff
	 * </code>
	 * @param payoff  the payoff received during this round
	 */
	public void receivePayoff(double payoff) {
		accumulatedPayoff = accumulatedPayoff * payoffDiscount + payoff; 
	}

}

