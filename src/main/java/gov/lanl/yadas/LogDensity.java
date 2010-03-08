package gov.lanl.yadas;

/**
 * Interface that helps define terms in the unnormalized posterior distribution.  
 * More restrictive than a Likelihood, a LogDensity assumes that its arguments 
 * are shaped like a rectangular array, and computations proceed one row at at time.  
 */ 
public interface LogDensity extends Likelihood {

	public double compute (double[][] args, int i);

}
