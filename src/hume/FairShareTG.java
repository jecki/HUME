/**
 * 
 */
package hume;


/**
 * Class <code>Fair Share Trust Game</code> defines a trust game, where the price
 * to pe paid lies always between the objective costs of the solution and the
 * costs plus the added value. This has the <em>undesired consequence</em> that
 * the supplier agent, if it intends to exploit, should theoretically prefer
 * a customer with a problem for which its competence is rather low, because in
 * this case the customer must pay a high price (which covers the high costs of
 * incompetent craftmanship)!
 * 
 * STATUS: mature and tested.
 * 
 * @author eckhart
 */
public strictfp class FairShareTG extends TrustGame {
	public double sigma = 1.4;
	public double phi   = 1.2;
	public double beta  = 0.5;
	
	/**
	 * Creates a TrustGame object.
	 */
	public FairShareTG() { }
	
	/**
	 * Creates a "fair share" trust game object with the given parameters.
	 * @param sigma 
	 * @param phi
	 * @param beta
	 */
	public FairShareTG(double sigma, double phi, double beta) {
		this.sigma = sigma;
		this.phi = phi;
		this.beta = beta;
	}
	
	/* (non-Javadoc)
	 * @see hume.TrustGame#costs(double competence)
	 */
	@Override
	protected double costs(double competence) {
		assert competence > 0.0 && competence < 1.0;
		
		return 1.0 - StrictMath.pow(competence, sigma);
	}

	/* (non-Javadoc)
	 * @see hume.TrustGame#value(double competence)
	 */	
	@Override
	protected double value(double competence) {
		assert competence > 0.0 && competence < 1.0;
		
		return 1.0 + StrictMath.pow(competence, phi);
	}


	/* (non-Javadoc)
	 * @see hume.TrustGame#price(double competence)
	 */	
	@Override
	protected double price(double suppliersCompetence) {
		return costs(suppliersCompetence) + beta * valueAdd(suppliersCompetence);
	}	

}
