/**
 * 
 */
package hume;

/**
 * The class <code>ReasonablePriceTG</code>defines a trust game where the price
 * depends on the competence (and thus the expected value) of the supplier.
 * 
 * @author eckhart
 *
 */
public strictfp class ReasonablePriceTG extends TrustGame {

	/* (non-Javadoc)
	 * @see hume.TrustGame#costs(double)
	 */
	@Override
	protected double costs(double competence) {
		return 1.0 - (StrictMath.pow(competence, 1.0/4.0)*0.8);
	}

	/* (non-Javadoc)
	 * @see hume.TrustGame#price(double)
	 */
	@Override
	protected double price(double suppliersCompetence) {
		// TODO Auto-generated method stub
		return value(suppliersCompetence) * 0.5 + 0.2;
	}

	/* (non-Javadoc)
	 * @see hume.TrustGame#value(double)
	 */
	@Override
	protected double value(double competence) {
		return StrictMath.pow(competence, 1.0/3.0);
	}

}
