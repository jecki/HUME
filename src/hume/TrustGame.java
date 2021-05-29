package hume;

abstract public class TrustGame {

	public TrustGame() {
		super();
	}

	/**
	 * Returns the cost for solving a problem depending on the competence
	 * of the player. The higher the competence, the lower the cost. 
	 * @param competence  the competence of the player
	 * @return the cost incurred
	 */
	abstract protected double costs(double competence);

	/**
	 * Returns the overall value an agent produces when solving a problem with
	 * a certain competence.
	 * @param competence
	 * @return the value produced
	 */
	abstract protected double value(double competence);
	
	/**
	 * Returns the surplus value (value - cost) an agent produces when solving
	 * a problem with a certain competence.
	 * @param competence
	 * @return the value added
	 */
	public double valueAdd(double competence) {
		return value(competence) - costs(competence);
	}

	/**
	 * Returns the payment a customer has to give a supplier for solving
	 * the customer's problem. 
	 * <p>The payment lies always between the costs
	 * for the solution and the value of the solution.
	 * 
	 * HUME 2.0: payment should depend on the <em>announced competence</em>
	 * rather than the real competence of the supplier.
	 * 
	 * @param suppliersCompetence the competence of the supplier (upon which
	 * 							 the costs and the value depend)
	 * @return the payment
	 */
	abstract protected double price(double suppliersCompetence);

	/**
	 * Returns the payoff the customer receives if exploited, which
	 * is the negative of the customer's payment.
	 * 
	 * HUME 2.0: customor's exploit payoff should depend on the 
	 * <em>announced competence</em> rather than the real competence 
	 * of the supplier. 
	 * 
	 * @param suppliersCompetence the competence of the supplier
	 * @return the customer's payoff
	 */
	public double customersExploit(double suppliersCompetence) {
		return -price(suppliersCompetence);
	}

	/**
	 * Returns the payoff the supplier receives if the supplier exploits
	 * the customer. The payoff is then simply the payment of the 
	 * customer.
	 * 
	 * HUME 2.0: supplier's exploit payoff should depend on the 
	 * <em>announced competence</em> rather than the real competence 
	 * of the supplier.	   
	 * 
	 * @param suppliersCompetence  the competence of the supplier (on which
	 *                             the payoff depends)
	 * @return the cheating supplier's payoff
	 */
	public double suppliersExploit(double suppliersCompetence) {
		return price(suppliersCompetence);
	}

	/**
	 * Returns the reward the customer receives when the supplier proved
	 * to be trustworthy. The net reward is the value of the ssolution
	 * minus the payment
	 * 
	 * HUME 2.0: the customer's "reward" shoould be a function of the
	 * <em>invested competence</em> and the <em>announced competence</em>
	 * of the supplier. 
	 * 
	 * @param suppliersCompetence  the competence the supplier invests in
	 *                             solving the problem 
	 * @return the customer's "reward"
	 */
	public double customersReward(double suppliersCompetence) {
		return value(suppliersCompetence) - price(suppliersCompetence);		
	}

	/**
	 * Returns the reward a trustworthy supplier keeps from the business.
	 * This is the payment minus the suppliers cost.
	 *
	 * HUME 2.0: the customer's "reward" shoould be a function of the
	 * <em>invested competence</em> and the <em>announced competence</em>
	 * of the supplier. 
	 * 
	 * @param suppliersCompetence the suppliers competence (upon which
	 * 							  the cost and payment depend)
	 * @return the trustworthy supplier's "reward"
	 */
	public double suppliersReward(double suppliersCompetence) {
		return price(suppliersCompetence) - costs(suppliersCompetence);
	}

}